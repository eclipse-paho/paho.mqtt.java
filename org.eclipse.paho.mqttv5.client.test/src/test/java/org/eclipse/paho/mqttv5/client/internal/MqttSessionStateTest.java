package org.eclipse.paho.mqttv5.client.internal;

import org.junit.Test;

import static org.junit.Assert.*;

public class MqttSessionStateTest {

    @Test
    public void testClearSessionState() {
        MqttSessionState state = new MqttSessionState();
        state.clearSessionState();
        assertTrue("Clear session state resets subscription identifier", 1 == state.getNextSubscriptionIdentifier());
    }

    /**
     * Test that the subscription identifier is bounded between 1 and 268,435,455
     */
    @Test
    public void testSubscriptionIdIsBounded() {
        MqttSessionState state = new MqttSessionState();
        for (int i = 1; i <= 268_435_456; i++) {
            assertTrue("Subscription identifier minimum bound", state.getNextSubscriptionIdentifier()>=1);
            assertTrue("Subscription identifier maximum bound", state.getNextSubscriptionIdentifier()<=268_435_455);
        }
    }

}
