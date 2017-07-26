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

import org.eclipse.paho.mqttv5.common.MqttException;

public class MqttPubAck extends MqttAck{
	

	
	private static final int[] validReturnCodes = {
			MqttReturnCode.RETURN_CODE_SUCCESS,
			MqttReturnCode.RETURN_CODE_NO_MATCHING_SUBSCRIBERS,
			MqttReturnCode.RETURN_CODE_UNSPECIFIED_ERROR,
			MqttReturnCode.RETURN_CODE_IMPLEMENTATION_SPECIFIC_ERROR,
			MqttReturnCode.RETURN_CODE_NOT_AUTHORIZED,
			MqttReturnCode.RETURN_CODE_TOPIC_NAME_INVALID,
			MqttReturnCode.RETURN_CODE_QUOTA_EXCEEDED,
			MqttReturnCode.RETURN_CODE_PAYLOAD_FORMAT_INVALID
	};
	
	private static final int[] validReturnCodes = {
			RETURN_CODE_SUCCESS,
			RETURN_CODE_NO_MATCHING_SUBSCRIBERS,
			RETURN_CODE_UNSPECIFIED_ERROR,
			RETURN_CODE_IMPLEMENTATION_SPECIFIC_ERROR,
			RETURN_CODE_NOT_AUTHORIZED,
			RETURN_CODE_TOPIC_INVALID,
			RETURN_CODE_PACKET_TOO_LARGE,
			RETURN_CODE_QOS_LEVEL_NOT_SUPPORTED
	};
	
	// Identifier / Value Identifiers
	private static final byte REASON_STRING_IDENTIFIER 					= 0x1F;
	
	// Fields
	private int returnCode;
	private String reasonString;

	public MqttPubAck(byte info, byte[] data) throws IOException, MqttException {
		super(MqttWireMessage.MESSAGE_TYPE_PUBACK);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		msgId = dis.readUnsignedShort();
		returnCode = dis.readUnsignedByte();
		validateReturnCode(returnCode, validReturnCodes);
		parseIdentifierValueFields(dis);
		dis.close();
	}
	
	public MqttPubAck(int returnCode) throws MqttException{
		super(MqttWireMessage.MESSAGE_TYPE_PUBACK);
		this.returnCode = returnCode;
		validateReturnCode(returnCode, validReturnCodes);
	}

	
	@Override
	protected byte[] getVariableHeader() throws MqttException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(baos);
			
			// Encode the Message ID
			outputStream.writeShort(msgId);
			
			// Encode the Return Code
			outputStream.write( (byte) returnCode);
			
			// Write Identifier / Value Fields
			byte[] identifierValueFieldsByteArray = getIdentifierValueFields();
			outputStream.write(encodeVariableByteInteger(identifierValueFieldsByteArray.length));
			outputStream.write(identifierValueFieldsByteArray);
			outputStream.flush();
			return baos.toByteArray();	
		} catch (IOException ioe){
			throw new MqttException(ioe);
		}
	}
	
	private byte[] getIdentifierValueFields() throws MqttException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(baos);
			
			// If Present, encode the Reason String (3.4.2.3)
			if(reasonString != null){
				outputStream.write(REASON_STRING_IDENTIFIER);
				encodeUTF8(outputStream, reasonString);
			}
			
			outputStream.flush();
			return baos.toByteArray();
		} catch (IOException ioe){
			throw new MqttException(ioe);
		}
	}
	
	
	private void parseIdentifierValueFields(DataInputStream dis) throws IOException, MqttException {
		// First get the length of the IV fields
		int length = readVariableByteInteger(dis).getValue();
		if(length > 0){
			byte[] identifierValueByteArray = new byte[length];
			dis.read(identifierValueByteArray, 0, length);
			ByteArrayInputStream bais =  new ByteArrayInputStream(identifierValueByteArray);
			DataInputStream inputStream = new DataInputStream(bais);
			while(inputStream.available() > 0){
				// Get the first Byte
				byte identifier = inputStream.readByte();
				if(identifier == REASON_STRING_IDENTIFIER){
					reasonString = decodeUTF8(inputStream);
				} else {
					// Unidentified Identifier
					throw new MqttException(MqttException.REASON_CODE_INVALID_IDENTIFIER);
				}
			}
		}
	}

	public int getReturnCode() {
		return returnCode;
	}

	public String getReasonString() {
		return reasonString;
	}

	public void setReasonString(String reasonString) {
		this.reasonString = reasonString;
	}

}
