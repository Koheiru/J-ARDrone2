/* 
 * Copyright 2015 Prostov Yury.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ardrone2.impl;

import ardrone2.api.DroneMessage;
import ardrone2.api.DroneState;
import ardrone2.messages.DemoMessage;
import ardrone2.messages.StateMessage;

/**
 * Class DroneStateImpl
 * @author Prostov Yury
 */
public class DroneStateImpl 
    extends DroneModuleExt<DroneState.StateListener>
    implements DroneState, Engine.Receiver {
    
    private int m_battteryLevel = 0;
    private boolean m_batteryTooLow = false;
    private boolean m_batteryTooHigh = false;
    
    public DroneStateImpl() {
        super(DroneState.StateListener.class);
    }
    
    @Override
    public int batteryLevel() {
        return m_battteryLevel;
    }

    @Override
    public boolean isBatteryTooLow() {
        return m_batteryTooLow;
    }

    @Override
    public boolean isBatteryTooHigh() {
        return m_batteryTooHigh;
    }
    
    @Override
    public void onMessageReceived(DroneMessage message) {
        if (message instanceof StateMessage) {
            StateMessage stateMessage = (StateMessage)message;
            updateStateFlags(stateMessage);
        }
        if (message instanceof DemoMessage) {
            DemoMessage demoMessage = (DemoMessage)message;
            updateBatteryLevel(demoMessage);
        }
    }
    
    private void updateStateFlags(StateMessage stateMessage) {
        m_batteryTooLow  = stateMessage.isFlagsEnabled(StateMessage.BATTERY_TOO_LOW_FLAG);
        m_batteryTooHigh = stateMessage.isFlagsEnabled(StateMessage.BATTERY_TOO_HIGH_FLAG);
    }
    
    private void updateBatteryLevel(DemoMessage demoMessage) {
        int newValue = demoMessage.batteryLevel();
        if (newValue == m_battteryLevel) {
            return;
        }
        
        m_battteryLevel = newValue;
        DroneState.StateListener[] listeners = listeners();
        for (StateListener listener: listeners) {
            listener.onBatteryLevelChanged(m_battteryLevel);
        }
    }
    
}
