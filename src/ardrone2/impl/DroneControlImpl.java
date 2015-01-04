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

import ardrone2.api.DroneControl;
import ardrone2.api.DroneMessage;
import ardrone2.commands.ConfigCommand;
import ardrone2.commands.EmergencyCommand;
import ardrone2.commands.FlatTrimCommand;
import ardrone2.messages.DemoMessage;
import ardrone2.commands.HoverCommand;
import ardrone2.commands.LandCommand;
import ardrone2.commands.MoveCommand;
import ardrone2.commands.TakeOffCommand;

/**
 * Class DroneControlImpl
 * @author Prostov Yury
 */
public class DroneControlImpl
    extends DroneModuleExt<DroneControl.ControlListener>
    implements DroneControl, Engine.Receiver {
    
    private ControlState m_state = ControlState.Unknown;
    private float m_altitude = 0.0f;
    private float m_pitch = 0.0f;
    private float m_roll = 0.0f;
    private float m_yaw = 0.0f;
    private float m_xVelocity = 0.0f;
    private float m_yVelocity = 0.0f;
    private float m_zVelocity = 0.0f;
    
    public DroneControlImpl() {
        super(DroneControl.ControlListener.class);
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
    public float pitchAngle() {
        return m_pitch;
    }

    @Override
    public float rollAngle() {
        return m_roll;
    }

    @Override
    public float yawAngle() {
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
    public void move(float roll, float pitch, float yaw, float gaz) {
        engine().send(new MoveCommand(pitch, roll, yaw, gaz));
    }

    @Override
    public void move(float roll, float pitch, float yaw, float gaz, boolean isCombinedYaw) {
        engine().send(new MoveCommand(pitch, roll, yaw, gaz, isCombinedYaw));
    }
    
    @Override
    public void onStateChanged(Engine.State state) {
        if (state == Engine.State.Connected) {
            //! This module is want to get navdata.
            engine().send(new ConfigCommand("general:navdata_demo", "TRUE"));
        }
    }
    
    @Override
    public void onMessageReceived(DroneMessage message) {
        if (message instanceof DemoMessage) {
            DemoMessage demoMessage = (DemoMessage)message;
            boolean stateChanged = updateControlState(demoMessage.majorState(), demoMessage.minorState());
            boolean directionChanged = updateDirection(demoMessage.altitude(), demoMessage.pitch(), demoMessage.roll(), demoMessage.yaw());
            boolean velocityChanged = updateVelocity(demoMessage.xVelocity(), demoMessage.yVelocity(), demoMessage.zVelocity());
            notifyListeners(stateChanged, directionChanged, velocityChanged);
        }
    }
    
    private boolean updateControlState(int majorState, int minorState) {
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
        DroneControl.ControlListener[] listeners = listeners();
        for (ControlListener listener: listeners) {
            //! TODO: think about splitting of this code...
            if (stateChanged)     { listener.onControlStateChanged(m_state); }
            if (directionChanged) { listener.onDirectionChanged(m_altitude, m_pitch, m_roll, m_yaw); }
            if (velocityChanged)  { listener.onVelocityChanged(m_xVelocity, m_yVelocity, m_zVelocity); }
        }
        
    }
    
}
