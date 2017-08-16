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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.packet.MqttPubRel;
import org.eclipse.paho.mqttv5.common.packet.MqttWireMessage;
import org.junit.Assert;
import org.junit.Test;

public class MqttPubRelTest {
	private static final int returnCode = MqttReturnCode.RETURN_CODE_PACKET_ID_NOT_FOUND;
	private static final String reasonString = "Reason String 123.";
	private static final String userKey1 = "userKey1";
	private static final String userKey2 = "userKey2";
	private static final String userKey3 = "userKey3";
	private static final String userValue1 = "userValue1";
	private static final String userValue2 = "userValue2";
	private static final String userValue3 = "userValue3";
	
	@Test
	public void testEncodingMqttPubRel() throws MqttException {
		MqttPubRel mqttPubRelPacket = generateMqttPubRelPacket();
		mqttPubRelPacket.getHeader();
		mqttPubRelPacket.getPayload();
	}
	
	@Test
	public void testDecodingMqttPubRel() throws MqttException, IOException {
		MqttPubRel mqttPubRelPacket = generateMqttPubRelPacket();
		byte[] header = mqttPubRelPacket.getHeader();
		byte[] payload = mqttPubRelPacket.getPayload();
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(header);
		outputStream.write(payload);
		
		MqttPubRel decodedPubRelPacket = (MqttPubRel) MqttWireMessage.createWireMessage(outputStream.toByteArray());
		
		Assert.assertEquals(returnCode, decodedPubRelPacket.getReturnCode());
		Assert.assertEquals(reasonString, decodedPubRelPacket.getReasonString());
		Assert.assertEquals(3, decodedPubRelPacket.getUserDefinedPairs().size());
		Assert.assertEquals(userValue1, decodedPubRelPacket.getUserDefinedPairs().get(userKey1));
		Assert.assertEquals(userValue2, decodedPubRelPacket.getUserDefinedPairs().get(userKey2));
		Assert.assertEquals(userValue3, decodedPubRelPacket.getUserDefinedPairs().get(userKey3));
		
		
	}
	
	public MqttPubRel generateMqttPubRelPacket() throws MqttException{
		MqttPubRel mqttPubRelPacket = new MqttPubRel(returnCode, 1);
		mqttPubRelPacket.setReasonString(reasonString);
		Map<String, String> userDefinedPairs = new HashMap<String,String>();
		userDefinedPairs.put(userKey1, userValue1);
		userDefinedPairs.put(userKey2, userValue2);
		userDefinedPairs.put(userKey3, userValue3);
		mqttPubRelPacket.setUserDefinedPairs(userDefinedPairs);
		
		return mqttPubRelPacket;
	}
	

}
