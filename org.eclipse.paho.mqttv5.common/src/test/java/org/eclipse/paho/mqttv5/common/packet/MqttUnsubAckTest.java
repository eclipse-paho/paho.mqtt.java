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
import org.eclipse.paho.mqttv5.common.packet.MqttUnsubAck;
import org.eclipse.paho.mqttv5.common.packet.MqttWireMessage;
import org.junit.Assert;
import org.junit.Test;

public class MqttUnsubAckTest {
	
	private static final int[] returnCodes = {
			MqttReturnCode.RETURN_CODE_SUCCESS,
			MqttReturnCode.RETURN_CODE_NO_SUBSCRIPTION_EXISTED,
			MqttReturnCode.RETURN_CODE_UNSPECIFIED_ERROR,
			MqttReturnCode.RETURN_CODE_IMPLEMENTATION_SPECIFIC_ERROR,
			MqttReturnCode.RETURN_CODE_NOT_AUTHORIZED,
			MqttReturnCode.RETURN_CODE_TOPIC_FILTER_NOT_VALID,
			MqttReturnCode.RETURN_CODE_PACKET_ID_IN_USE
	};

	@Test
	public void testEncodingMqttUnsubAck() throws MqttException {
		MqttUnsubAck mqttUnsubAckPacket = new MqttUnsubAck(returnCodes);
		mqttUnsubAckPacket.getHeader();
		mqttUnsubAckPacket.getPayload();
	}
	
	@Test
	public void testDecodingMqttUnsubAck() throws MqttException, IOException {
		MqttUnsubAck mqttUnsubAckPacket = new MqttUnsubAck(returnCodes);
		byte[] header = mqttUnsubAckPacket.getHeader();
		byte[] payload = mqttUnsubAckPacket.getPayload();
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(header);
		outputStream.write(payload);
		
		MqttUnsubAck decodedUnsubAckPacket = (MqttUnsubAck) MqttWireMessage.createWireMessage(outputStream.toByteArray());
		
		Assert.assertArrayEquals(returnCodes, decodedUnsubAckPacket.getReturnCodes());
		
		
	}
	
	

}
