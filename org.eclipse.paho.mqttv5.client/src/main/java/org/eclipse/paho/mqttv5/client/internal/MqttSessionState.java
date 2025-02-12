package org.eclipse.paho.mqttv5.client.internal;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is used as a store for client information that should be preserved
 * for a single MQTT Session. If the client is disconnected and reconnects with
 * clean start = true, then this object will be reset to it's initial state.
 *
 * Connection variables that this class holds:
 *
 * <ul>
 * <li>Client ID</li>
 * <li>Next Subscription Identifier - The next subscription Identifier available
 * to use.</li>
 * </ul>
 *
 * Subscription identifier can take values from 1 to 268,435,455 according to MQTTv5 specification
 * @see <a href="https://docs.oasis-open.org/mqtt/mqtt/v5.0/os/mqtt-v5.0-os.html#_Toc3901117">MQTTv5 Specification 3.8.2.1.2 Subscription Identifier</a>
 */
public class MqttSessionState {

	// ******* Session Specific Properties and counters ******//
	private AtomicInteger nextSubscriptionIdentifier = new AtomicInteger(1);
	private String clientId;
	private Integer SUBSCRIPTION_IDENTIFIER_MAX_LIMIT = 268_435_455;

	public void clearSessionState() {
		nextSubscriptionIdentifier.set(1);
	}

	public Integer getNextSubscriptionIdentifier() {
		Integer nextValue = nextSubscriptionIdentifier.getAndIncrement();
		if (nextValue <= SUBSCRIPTION_IDENTIFIER_MAX_LIMIT) {
			return nextValue;
		}

		// nextValue > SUBSCRIPTION_IDENTIFIER_MAX_LIMIT, so we need to restart the identifier from 1
		synchronized(nextSubscriptionIdentifier) {
			// read again to make sure no other thread has updated the value
			if (nextSubscriptionIdentifier.get() > SUBSCRIPTION_IDENTIFIER_MAX_LIMIT) {
				clearSessionState();
			}
		}
		return nextSubscriptionIdentifier.getAndIncrement();
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
