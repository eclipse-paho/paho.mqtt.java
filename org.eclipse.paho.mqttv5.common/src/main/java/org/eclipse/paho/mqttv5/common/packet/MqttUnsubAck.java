/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 	  Dave Locke   - Original MQTTv3 implementation
 *    James Sutton - Initial MQTTv5 implementation
 */
package org.eclipse.paho.mqttv5.common.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.packet.util.CountingInputStream;

public class MqttUnsubAck extends MqttAck{
	

	
	private static final int[] validReturnCodes = {
			MqttReturnCode.RETURN_CODE_SUCCESS,
			MqttReturnCode.RETURN_CODE_NO_SUBSCRIPTION_EXISTED,
			MqttReturnCode.RETURN_CODE_UNSPECIFIED_ERROR,
			MqttReturnCode.RETURN_CODE_IMPLEMENTATION_SPECIFIC_ERROR,
			MqttReturnCode.RETURN_CODE_NOT_AUTHORIZED,
			MqttReturnCode.RETURN_CODE_TOPIC_FILTER_NOT_VALID,
			MqttReturnCode.RETURN_CODE_PACKET_ID_IN_USE
	};
	
	// Fields
	private int[] returnCodes;
	

	public MqttUnsubAck(byte info, byte[] data) throws IOException, MqttException {
		super(MqttWireMessage.MESSAGE_TYPE_UNSUBACK);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		CountingInputStream counter = new CountingInputStream(bais);
		DataInputStream inputStream = new DataInputStream(counter);
		
		msgId = inputStream.readUnsignedShort();
		
		int remainingLengh = data.length - counter.getCounter();
		returnCodes = new int[remainingLengh];
		
		for(int i = 0; i < remainingLengh; i++){
			returnCodes[i] = inputStream.readUnsignedByte();
			validateReturnCode(returnCodes[i], validReturnCodes);
		}
		
		inputStream.close();
	}
	
	public MqttUnsubAck(int[] returnCodes) throws MqttException{
		super(MqttWireMessage.MESSAGE_TYPE_UNSUBACK);
		for(int returnCode : returnCodes){
			validateReturnCode(returnCode, validReturnCodes);
		}
		this.returnCodes = returnCodes;
	}
	
	@Override
	protected byte[] getVariableHeader() throws MqttException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(baos);
			
			// Encode the msgId
			outputStream.writeShort(msgId);
			outputStream.flush();
			return baos.toByteArray();
		} catch (IOException ioe) {
			throw new MqttException(ioe);
		}
	}
	
	@Override
	public byte[] getPayload() throws MqttException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(baos);
			
			for(int returnCode : returnCodes){
				outputStream.writeByte(returnCode);
			}
			
			outputStream.flush();
			return baos.toByteArray();
		} catch (IOException ioe){
			throw new MqttException(ioe);
		}
		
	}
	

	public int[] getReturnCodes() {
		return returnCodes;
	}

	@Override
	public String toString() {
		return "MqttUnsubAck [returnCodes=" + Arrays.toString(returnCodes) + "]";
	}

	

}
