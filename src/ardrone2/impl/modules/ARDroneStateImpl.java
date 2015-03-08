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

package ardrone2.impl.modules;

import ardrone2.ARDroneState;
import ardrone2.Message;
import ardrone2.commands.ConfigCommand;
import ardrone2.impl.ARDroneEngine;
import ardrone2.impl.ARDroneEngine.Channel;
import ardrone2.impl.ARDroneEngine.ChannelState;
import ardrone2.impl.ARDroneEngine.MessageDecoder;
import ardrone2.impl.ARDroneModule;
import ardrone2.impl.ListenersList;
import ardrone2.impl.codecs.DemoMessageDecoder;
import ardrone2.impl.codecs.StateMessageDecoder;
import ardrone2.messages.DemoMessage;
import ardrone2.messages.StateMessage;

/**
 * Class ARDroneStateImpl
 * @author Prostov Yury
 */
public class ARDroneStateImpl extends ARDroneModule implements ARDroneState {

    private ListenersList m_listeners = null;
    
    private int m_battteryLevel = 0;
    private boolean m_batteryTooLow = false;
    
    public ARDroneStateImpl() {
        super(Channel.MessagesStream, DemoMessageDecoder.class, StateMessageDecoder.class);
        m_listeners = new ListenersList(ARDroneState.StateListener.class);
    }
    
    @Override
    public void initialize(ARDroneEngine engine) {
        super.initialize(engine);
        subscribeToStates();
        subscribeToMessages();
    }
    
    @Override
    public void deinitialize() {
        unsubscribeFromMessages();
        unsubscribeFromStates();
        super.deinitialize();
    }

    @Override
    public void addStateListener(StateListener listener) {
        m_listeners.addListener(listener);
    }

    @Override
    public void removeStateListener(StateListener listener) {
        m_listeners.removeListener(listener);
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
    protected void onStateChanged(ChannelState state) {
        if (state == ChannelState.Connected) {
            //! This module is want to get navdata.
            engine().send(new ConfigCommand("general:navdata_demo", "TRUE"));
        }
    }
    
    @Override
    protected void onMessageReceived(Message message) {
        boolean batteryFlagsChanged = false;
        if (message instanceof StateMessage) {
            StateMessage stateMessage = (StateMessage)message;
            batteryFlagsChanged = updateStateFlags(stateMessage);
        }
        
        boolean batteryLevelChanged = false;
        if (message instanceof DemoMessage) {
            DemoMessage demoMessage = (DemoMessage)message;
            batteryLevelChanged = updateBatteryLevel(demoMessage);
        }
        
        notifyListeners(batteryFlagsChanged, batteryLevelChanged);
    }
    
    private boolean updateStateFlags(StateMessage stateMessage) {
        boolean batteryTooLow  = stateMessage.isFlagsEnabled(StateMessage.BATTERY_TOO_LOW_FLAG);
        if (m_batteryTooLow == batteryTooLow) {
            return false;
        }
        
        m_batteryTooLow = batteryTooLow;
        return true;
    }
    
    private boolean updateBatteryLevel(DemoMessage demoMessage) {
        int newValue = demoMessage.batteryLevel;
        if (newValue == m_battteryLevel) {
            return false;
        }
        
        m_battteryLevel = newValue;
        return true;
    }
    
    private void notifyListeners(boolean batteryFlagsChanged, boolean batteryLevelChanged) {
        if (!batteryFlagsChanged && !batteryLevelChanged) {
            return;
        }
        
        StateListener[] listeners = (StateListener[])m_listeners.listeners();
        for (StateListener listener: listeners) {
            listener.onBatteryLevelChanged(m_battteryLevel, m_batteryTooLow);
        }
        
    }
    
}
