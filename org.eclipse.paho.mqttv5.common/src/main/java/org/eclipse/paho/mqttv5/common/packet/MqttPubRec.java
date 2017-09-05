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
import java.util.ArrayList;

import org.eclipse.paho.mqttv5.common.MqttException;

public class MqttPubRec extends MqttAck {

	private static final int[] validReturnCodes = { MqttReturnCode.RETURN_CODE_SUCCESS,
			MqttReturnCode.RETURN_CODE_NO_MATCHING_SUBSCRIBERS, MqttReturnCode.RETURN_CODE_UNSPECIFIED_ERROR,
			MqttReturnCode.RETURN_CODE_IMPLEMENTATION_SPECIFIC_ERROR, MqttReturnCode.RETURN_CODE_NOT_AUTHORIZED,
			MqttReturnCode.RETURN_CODE_TOPIC_NAME_INVALID, MqttReturnCode.RETURN_CODE_PACKET_ID_IN_USE,
			MqttReturnCode.RETURN_CODE_QUOTA_EXCEEDED, MqttReturnCode.RETURN_CODE_PAYLOAD_FORMAT_INVALID };

	// Fields
	private int returnCode = MqttReturnCode.RETURN_CODE_SUCCESS;
	private String reasonString;
	private ArrayList<UserProperty> userDefinedProperties = new ArrayList<UserProperty>();

	public MqttPubRec(byte info, byte[] data) throws IOException, MqttException {
		super(MqttWireMessage.MESSAGE_TYPE_PUBREC);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		msgId = dis.readUnsignedShort();
		if (data.length > 2) {
			returnCode = dis.readUnsignedByte();
			validateReturnCode(returnCode, validReturnCodes);
			parseIdentifierValueFields(dis);
		}
		dis.close();
	}

	public MqttPubRec(int returnCode, int msgId) throws MqttException {
		super(MqttWireMessage.MESSAGE_TYPE_PUBREC);
		validateReturnCode(returnCode, validReturnCodes);
		this.returnCode = returnCode;
		this.msgId = msgId;
	}

	@Override
	protected byte[] getVariableHeader() throws MqttException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(baos);

			// Encode the Message ID
			outputStream.writeShort(msgId);
			
			// TODO - implement shortening for PUB ack messages if rc is 0 and no IVs
			byte[] identifierValueFieldsByteArray = getIdentifierValueFields();
			
			if (returnCode != MqttReturnCode.RETURN_CODE_SUCCESS || identifierValueFieldsByteArray.length != 0) {
				// Encode the Return Code
				outputStream.write((byte) returnCode);

				// Write Identifier / Value Fields
				outputStream.write(encodeVariableByteInteger(identifierValueFieldsByteArray.length));
				outputStream.write(identifierValueFieldsByteArray);
			}

			
			outputStream.flush();
			return baos.toByteArray();
		} catch (IOException ioe) {
			throw new MqttException(ioe);
		}
	}

	private byte[] getIdentifierValueFields() throws MqttException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(baos);

			// If Present, encode the Reason String (3.5.2.2.2)
			if (reasonString != null) {
				outputStream.write(MqttPropertyIdentifiers.REASON_STRING_IDENTIFIER);
				encodeUTF8(outputStream, reasonString);
			}

			// If Present, encode the User Properties (3.5.2.2.3)
			if(userDefinedProperties.size() != 0){
				for(UserProperty property : userDefinedProperties) {
					outputStream.write(MqttPropertyIdentifiers.USER_DEFINED_PAIR_IDENTIFIER);
					encodeUTF8(outputStream, property.getKey());
					encodeUTF8(outputStream, property.getValue());
				}
			}

			outputStream.flush();
			return baos.toByteArray();
		} catch (IOException ioe) {
			throw new MqttException(ioe);
		}
	}

	private void parseIdentifierValueFields(DataInputStream dis) throws IOException, MqttException {
		// First get the length of the IV fields
		int length = readVariableByteInteger(dis).getValue();
		if (length > 0) {
			byte[] identifierValueByteArray = new byte[length];
			dis.read(identifierValueByteArray, 0, length);
			ByteArrayInputStream bais = new ByteArrayInputStream(identifierValueByteArray);
			DataInputStream inputStream = new DataInputStream(bais);
			while (inputStream.available() > 0) {
				// Get the first Byte
				byte identifier = inputStream.readByte();
				if (identifier == MqttPropertyIdentifiers.REASON_STRING_IDENTIFIER) {
					reasonString = decodeUTF8(inputStream);
				} else if (identifier == MqttPropertyIdentifiers.USER_DEFINED_PAIR_IDENTIFIER) {
					String key = decodeUTF8(inputStream);
					String value = decodeUTF8(inputStream);
					userDefinedProperties.add(new UserProperty(key,  value));
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

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public String getReasonString() {
		return reasonString;
	}

	public void setReasonString(String reasonString) {
		this.reasonString = reasonString;
	}

	public ArrayList<UserProperty> getUserDefinedProperties() {
		return userDefinedProperties;
	}

	public void setUserDefinedProperties(ArrayList<UserProperty> userDefinedProperties) {
		this.userDefinedProperties = userDefinedProperties;
	}
}
