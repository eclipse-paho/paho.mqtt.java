package org.eclipse.paho.mqttv5.common;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MqttException extends Exception{

	private static final long serialVersionUID = 1L;

	/** 
	 * Client encountered an exception.  Use the {@link #getCause()}
	 * method to get the underlying reason.
	 */
	public static final short REASON_CODE_CLIENT_EXCEPTION              = 0x00;
	
	// CONNECT packet exceptions
	public static final int REASON_CODE_INVALID_IDENTIFIER 				= 50000; // Invalid Identifier in the IV fields
	public static final int REASON_CODE_INVALID_RETURN_CODE				= 50001; // Invalid Return code
	public static final int REASON_CODE_MALFORMED_PACKET				= 50002; // Packet was somehow malformed and did not comply to the MQTTv5 specification
	public static final int REASON_CODE_UNSUPPORTED_PROTOCOL_VERSION    = 50003; // The CONNECT packet did not contain the correct protocol name or version
	

	
	/** 
	 * Client timed out while waiting for a response from the server.
	 * The server is no longer responding to keep-alive messages.
	 */
	public static final short REASON_CODE_CLIENT_TIMEOUT                = 32000;

	/**
	 * Internal error, caused by no new message IDs being available.
	 */
	public static final short REASON_CODE_NO_MESSAGE_IDS_AVAILABLE      = 32001;

	/** 
	 * Client timed out while waiting to write messages to the server.
	 */
	public static final short REASON_CODE_WRITE_TIMEOUT                 = 32002;
	
	/**
	 * The client is already connected.
	 */
	public static final short REASON_CODE_CLIENT_CONNECTED              = 32100;

	/**
	 * The client is already disconnected.
	 */
	public static final short REASON_CODE_CLIENT_ALREADY_DISCONNECTED   = 32101;
	/** 
	 * The client is currently disconnecting and cannot accept any new work.
	 * This can occur when waiting on a token, and then disconnecting the client.  
	 * If the message delivery does not complete within the quiesce timeout 
	 * period, then the waiting token will be notified with an exception.
	 */
	public static final short REASON_CODE_CLIENT_DISCONNECTING          = 32102;
	
	/** Unable to connect to server */
	public static final short REASON_CODE_SERVER_CONNECT_ERROR          = 32103;

	/** 
	 * The client is not connected to the server.  The {@link MqttClient#connect()}
	 * or {@link MqttClient#connect(MqttConnectOptions)} method must be called
	 * first.  It is also possible that the connection was lost - see 
	 * {@link MqttClient#setCallback(MqttCallback)} for a way to track lost
	 * connections.  
	 */
	public static final short REASON_CODE_CLIENT_NOT_CONNECTED          = 32104;

	/** 
	 * Server URI and supplied <code>SocketFactory</code> do not match.
	 * URIs beginning <code>tcp://</code> must use a <code>javax.net.SocketFactory</code>,
	 * and URIs beginning <code>ssl://</code> must use a <code>javax.net.ssl.SSLSocketFactory</code>.
	 */
	public static final short REASON_CODE_SOCKET_FACTORY_MISMATCH       = 32105;
	
	/**
	 * SSL configuration error.
	 */
	public static final short REASON_CODE_SSL_CONFIG_ERROR              = 32106;

	/** 
	 * Thrown when an attempt to call {@link MqttClient#disconnect()} has been 
	 * made from within a method on {@link MqttCallback}.  These methods are invoked
	 * by the client's thread, and must not be used to control disconnection.
	 * 
	 * @see MqttCallback#messageArrived(String, MqttMessage)
	 */
	public static final short REASON_CODE_CLIENT_DISCONNECT_PROHIBITED  = 32107;

	/** 
	 * Protocol error: the message was not recognized as a valid MQTT packet.
	 * Possible reasons for this include connecting to a non-MQTT server, or
	 * connecting to an SSL server port when the client isn't using SSL.
	 */
	public static final short REASON_CODE_INVALID_MESSAGE				= 32108;

	/**
	 * The client has been unexpectedly disconnected from the server. The {@link #getCause() cause}
	 * will provide more details. 
	 */
	public static final short REASON_CODE_CONNECTION_LOST               = 32109;
	
	/**
	 * A connect operation in already in progress, only one connect can happen
	 * at a time.
	 */
	public static final short REASON_CODE_CONNECT_IN_PROGRESS           = 32110;
	
	/**
	 * The client is closed - no operations are permitted on the client in this
	 * state.  New up a new client to continue.
	 */
	public static final short REASON_CODE_CLIENT_CLOSED		           = 32111;
	
	/**
	 * A request has been made to use a token that is already associated with
	 * another action.  If the action is complete the reset() can ve called on the
	 * token to allow it to be reused.  
	 */
	public static final short REASON_CODE_TOKEN_INUSE		           = 32201;
	
	/**
	 * A request has been made to send a message but the maximum number of inflight 
	 * messages has already been reached. Once one or more messages have been moved
	 * then new messages can be sent.   
	 */
	public static final short REASON_CODE_MAX_INFLIGHT    			= 32202;
	
	/**
	 * The Client has attempted to publish a message whilst in the 'resting' / offline
	 * state with Disconnected Publishing enabled, however the buffer is full and
	 * deleteOldestMessages is disabled, therefore no more messages can be published
	 * until the client reconnects, or the application deletes buffered message
	 * manually. 
	 */
	public static final short REASON_CODE_DISCONNECTED_BUFFER_FULL	= 32203;

	private int reasonCode;
	private Throwable cause;
	
	/**
	 * Constructs a new <code>MqttException</code> with the specified code
	 * as the underlying reason.
	 * @param reasonCode the reason code for the exception.
	 */
	public MqttException(int reasonCode) {
		super();
		this.reasonCode = reasonCode;
	}
	
	/**
	 * Constructs a new <code>MqttException</code> with the specified 
	 * <code>Throwable</code> as the underlying reason.
	 * @param cause the underlying cause of the exception.
	 */
	public MqttException(Throwable cause) {
		super();
		this.reasonCode = REASON_CODE_CLIENT_EXCEPTION;
		this.cause = cause;
	}

	/**
	 * Constructs a new <code>MqttException</code> with the specified 
	 * <code>Throwable</code> as the underlying reason.
	 * @param reason the reason code for the exception.
	 * @param cause the underlying cause of the exception.
	 */
	public MqttException(int reason, Throwable cause) {
		super();
		this.reasonCode = reason;
		this.cause = cause;
	}

	
	/**
	 * Returns the reason code for this exception.
	 * @return the code representing the reason for this exception.
	 */
	public int getReasonCode() {
		return reasonCode;
	}
	
	/**
	 * Returns the underlying cause of this exception, if available.
	 * @return the Throwable that was the root cause of this exception,
	 * which may be <code>null</code>.
	 */
	@Override
	public Throwable getCause() {
		return cause;
	}
	
	/**
	 * Returns the detail message for this exception.
	 * @return the detail message, which may be <code>null</code>.
	 */
	@Override
	public String getMessage() {
		ResourceBundle bundle = ResourceBundle.getBundle("org.eclipse.paho.mqttv5.common.nls.messages");
		try {
			return bundle.getString(Integer.toString(reasonCode));
		} catch (MissingResourceException mre) {
			return "MqttException";
		}
	}
	
	/**
	 * Returns a <code>String</code> representation of this exception.
	 * @return a <code>String</code> representation of this exception.
	 */
	@Override
	public String toString() {
		String result = getMessage() + " (" + reasonCode + ")";
		if (cause != null) {
			result = result + " - " + cause.toString();
		}
		return result;
	}
	
	

}
