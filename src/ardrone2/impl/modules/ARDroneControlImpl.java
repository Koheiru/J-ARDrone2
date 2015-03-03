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

import ardrone2.ARDroneControl;
import ardrone2.ControlState;
import ardrone2.Message;
import ardrone2.commands.ConfigCommand;
import ardrone2.commands.EmergencyCommand;
import ardrone2.commands.FlatTrimCommand;
import ardrone2.commands.HoverCommand;
import ardrone2.commands.LandCommand;
import ardrone2.commands.MoveCommand;
import ardrone2.commands.TakeOffCommand;
import ardrone2.impl.ARDroneEngine;
import ardrone2.impl.ARDroneEngine.Channel;
import ardrone2.impl.ARDroneEngine.ChannelState;
import ardrone2.impl.ARDroneModule;
import ardrone2.impl.ListenersList;
import ardrone2.impl.codecs.DemoMessageDecoder;
import ardrone2.impl.codecs.StateMessageDecoder;
import ardrone2.messages.DemoMessage;

/**
 * Class ARDroneControlImpl
 * @author Prostov Yury
 */
public class ARDroneControlImpl extends ARDroneModule implements ARDroneControl {
    
    private ListenersList m_listeners = null;
    
    private ControlState m_state = ControlState.Unknown;
    private float m_altitude  = 0.0f;
    private float m_pitch     = 0.0f;
    private float m_roll      = 0.0f;
    private float m_yaw       = 0.0f;
    private float m_xVelocity = 0.0f;
    private float m_yVelocity = 0.0f;
    private float m_zVelocity = 0.0f;

    public ARDroneControlImpl() {
        super(Channel.MessagesStream, DemoMessageDecoder.class);
        m_listeners = new ListenersList(ARDroneControl.ControlListener.class);
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
    public void addControlListener(ControlListener listener) {
        m_listeners.addListener(listener);
    }

    @Override
    public void removeControlListener(ControlListener listener) {
        m_listeners.removeListener(listener);
    }

    @Override
    public ControlState controlState() {
        return m_state;
    }

    @Override
    public float altitude() {
        return m_altitude;
    }

    @Override
    public float pitch() {
        return m_pitch;
    }

    @Override
    public float roll() {
        return m_roll;
    }

    @Override
    public float yaw() {
        return m_yaw;
    }

    @Override
    public float xVelocity() {
        return m_xVelocity;
    }

    @Override
    public float yVelocity() {
        return m_yVelocity;
    }

    @Override
    public float zVelocity() {
        return m_zVelocity;
    }

    @Override
    public void takeOff() {
        engine().send(new TakeOffCommand());
    }

    @Override
    public void land() {
        engine().send(new LandCommand());
    }

    @Override
    public void hover() {
        engine().send(new HoverCommand());
    }

    @Override
    public void flatTrim() {
        engine().send(new FlatTrimCommand());
    }

    @Override
    public void emergency() {
        engine().send(new EmergencyCommand());
    }

    @Override
    public void move(float pitch, float roll, float yaw, float gaz) {
        engine().send(new MoveCommand(pitch, roll, yaw, gaz));
    }

    @Override
    public void move(float pitch, float roll, float yaw, float gaz, boolean isCombinedYaw) {
        engine().send(new MoveCommand(pitch, roll, yaw, gaz, isCombinedYaw));
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
        boolean isValidMessage = (message instanceof DemoMessage);
        if (!isValidMessage) {
            return;
        }
        
        DemoMessage demoMessage = (DemoMessage)message;
        boolean stateChanged = updateControlState(demoMessage.state);
        boolean directionChanged = updateDirection(demoMessage.altitude, demoMessage.pitch, demoMessage.roll, demoMessage.yaw);
        boolean velocityChanged = updateVelocity(demoMessage.xVelocity, demoMessage.yVelocity, demoMessage.zVelocity);
        notifyListeners(stateChanged, directionChanged, velocityChanged);
    }
    
    private boolean updateControlState(int state) {
        int majorState = state >> 16;
        int minorState = state & 0x0000FFFF;
        ControlState newState = convertToControlState(majorState, minorState);
        if (m_state == newState) {
            return false;
        }
        m_state = newState;
        return true;
    }
    
    private ControlState convertToControlState(int majorState, int minorState)
    {
        switch (majorState) {
            case DemoMessage.MAJOR_STATE_LANDED:        return ControlState.Landed;
            case DemoMessage.MAJOR_STATE_TRANS_TAKEOFF: return ControlState.TakingOff;
            case DemoMessage.MAJOR_STATE_FLYING:        return ControlState.Flying;
            case DemoMessage.MAJOR_STATE_HOVERING:      return ControlState.Hovering;
            case DemoMessage.MAJOR_STATE_TRANS_LANDING: return ControlState.Landing;
            default: 
                return ControlState.Unknown;
        }
    }
    
    private boolean updateDirection(float newAltitude, float newPitch, float newRoll, float newYaw) {
        if (m_altitude == newAltitude && m_pitch == newPitch && m_roll == newRoll && m_yaw == newYaw) {
            return false;
        }
        
        m_altitude = newAltitude;
        m_pitch = newPitch;
        m_roll = newRoll;
        m_yaw = newYaw;
        return true;
    }
    
    private boolean updateVelocity(float newXVelocity, float newYVelocity, float newZVelocity) {
        if (m_xVelocity == newXVelocity && m_yVelocity == newYVelocity && m_zVelocity == newZVelocity) {
            return false;
        }
        
        m_xVelocity = newXVelocity;
        m_yVelocity = newYVelocity;
        m_zVelocity = newZVelocity;
        return true;
    }
    
    private void notifyListeners(boolean stateChanged, boolean directionChanged, boolean velocityChanged) {
        if (!stateChanged && !directionChanged && !velocityChanged) {
            return;
        }
        
        ControlListener[] listeners = (ControlListener[])m_listeners.listeners();
        for (ControlListener listener: listeners) {
            //! TODO: think about splitting of this code...
            if (stateChanged)     { listener.onControlStateChanged(m_state); }
            if (directionChanged) { listener.onDirectionChanged(m_altitude, m_pitch, m_roll, m_yaw); }
            if (velocityChanged)  { listener.onVelocityChanged(m_xVelocity, m_yVelocity, m_zVelocity); }
        }
        
    }
    
}
