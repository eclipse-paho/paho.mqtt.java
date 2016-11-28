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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.packet.MqttPubAck;
import org.eclipse.paho.mqttv5.common.packet.MqttWireMessage;
import org.junit.Assert;
import org.junit.Test;

public class MqttPubAckTest {
	private static final int returnCode = MqttPubAck.RETURN_CODE_UNSPECIFIED_ERROR;
	private static final String reasonString = "Reason String 123.";
	
	@Test
	public void testEncodingMqttPuback() throws MqttException {
		MqttPubAck mqttPubackPacket = generateMqttPubackPacket();
		mqttPubackPacket.getHeader();
		mqttPubackPacket.getPayload();
	}
	
	@Test
	public void testDecodingMqttPuback() throws MqttException, IOException {
		MqttPubAck mqttPubackPacket = generateMqttPubackPacket();
		byte[] header = mqttPubackPacket.getHeader();
		byte[] payload = mqttPubackPacket.getPayload();
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(header);
		outputStream.write(payload);
		
		MqttPubAck decodedPubackPacket = (MqttPubAck) MqttWireMessage.createWireMessage(outputStream.toByteArray());
		
		Assert.assertEquals(returnCode, decodedPubackPacket.getReturnCode());
		Assert.assertEquals(reasonString, decodedPubackPacket.getReasonString());
		
		
	}
	
	public MqttPubAck generateMqttPubackPacket() throws MqttException{
		MqttPubAck mqttPubackPacket = new MqttPubAck(returnCode);
		mqttPubackPacket.setReasonString(reasonString);
		
		return mqttPubackPacket;
	}

}
