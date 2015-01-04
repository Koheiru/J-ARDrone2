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
package ardrone2.messages;

import ardrone2.api.DroneMessage;

/**
 * Class DemoMessage
 * @author Prostov Yury
 */
public class DemoMessage implements DroneMessage {
    
    private int   m_state        = 0;
    private int   m_batteryLevel = 0;
    private float m_altitude     = 0.0f;
    private float m_pitch        = 0.0f;
    private float m_roll         = 0.0f;
    private float m_yaw          = 0.0f;
    private float m_xVelocity    = 0.0f;
    private float m_yVelocity    = 0.0f;
    private float m_zVelocity    = 0.0f;
    
    public static final short TAG = (short)0x0000;
    
    /**** Major state ****/
    public static final short MAJOR_STATE_DEFAULT       = 0;
    public static final short MAJOR_STATE_INIT          = 1;
    public static final short MAJOR_STATE_LANDED        = 2;
    public static final short MAJOR_STATE_FLYING        = 3;
    public static final short MAJOR_STATE_HOVERING      = 4;
    public static final short MAJOR_STATE_TEST          = 5;
    public static final short MAJOR_STATE_TRANS_TAKEOFF = 6;
    public static final short MAJOR_STATE_TRANS_GOTOFIX = 7;
    public static final short MAJOR_STATE_TRANS_LANDING = 8;
    public static final short MAJOR_STATE_TRANS_LOOPING = 9;
    
    /**** Flying state ****/
    public static final short MINOR_STATE_FLYING_OK               = 0;
    public static final short MINOR_STATE_FLYING_LOST_ALT         = 1;
    public static final short MINOR_STATE_FLYING_LOST_ALT_GO_DOWN = 2;
    public static final short MINOR_STATE_FLYING_ALT_OUT_ZONE     = 3;
    public static final short MINOR_STATE_FLYING_COMBINED_YAW     = 4;
    public static final short MINOR_STATE_FLYING_BRAKE            = 5;
    public static final short MINOR_STATE_FLYING_NO_VISION        = 6;
    
    /**** Hovering state ****/
    public static final short MINOR_STATE_HOVERING_OK                    = 0;
    public static final short MINOR_STATE_HOVERING_YAW                   = 1;
    public static final short MINOR_STATE_HOVERING_YAW_LOST_ALT          = 2;
    public static final short MINOR_STATE_HOVERING_YAW_LOST_ALT_GO_DOWN  = 3;
    public static final short MINOR_STATE_HOVERING_ALT_OUT_ZONE          = 4;
    public static final short MINOR_STATE_HOVERING_YAW_ALT_OUT_ZONE      = 5;
    public static final short MINOR_STATE_HOVERING_LOST_ALT              = 6;
    public static final short MINOR_STATE_HOVERING_LOST_ALT_GO_DOWN      = 7;
    public static final short MINOR_STATE_HOVERING_LOST_COM              = 8;
    public static final short MINOR_STATE_HOVERING_COM_LOST_ALT          = 9;
    public static final short MINOR_STATE_HOVERING_COM_LOST_ALT_TOO_LONG = 10;
    public static final short MINOR_STATE_HOVERING_COM_ALT_OK            = 11;
    public static final short MINOR_STATE_HOVERING_MAGNETO_CALIB         = 12;
    public static final short MINOR_STATE_HOVERING_DEMO                  = 13;
    
    /**** Take off state ****/
    public static final short MINOR_STATE_TAKEOFF_GROUND = 0;
    public static final short MINOR_STATE_TAKEOFF_AUTO   = 1;
    
    /**** Gotofix state ****/
    public static final short MINOR_STATE_GOTOFIX_OK       = 0;
    public static final short MINOR_STATE_GOTOFIX_LOST_ALT = 1;
    public static final short MINOR_STATE_GOTOFIX_YAW      = 2;
    
    /**** Landing state ****/
    public static final short MINOR_STATE_LANDING_CLOSED_LOOP    = 0;
    public static final short MINOR_STATE_LANDING_OPEN_LOOP      = 1;
    public static final short MINOR_STATE_LANDING_OPEN_LOOP_FAST = 2;
    
    public DemoMessage(int majorState, int minorState, int batteryLevel, 
                       float altitude, float pitch, float roll, float yaw, 
                       float xVelocity, float yVelocity, float zVelocity) {
        m_state        = (majorState << 16) | (minorState & 0xFFFF);
        m_batteryLevel = batteryLevel;
        m_altitude     = altitude;
        m_pitch        = pitch;
        m_roll         = roll;
        m_yaw          = yaw;
        m_xVelocity    = xVelocity;
        m_yVelocity    = yVelocity;
        m_zVelocity    = zVelocity;
    }
    
    private void setMajorState(int majorState) {
        m_state = (majorState << 16) | (m_state & 0xFFFF);
    }
    
    public final int majorState() {
        return ((m_state >> 16) & 0xFFFF);
    }
    
    private final void setMinorState(int minorState) {
        m_state = (m_state & 0xFFFF0000) | (minorState & 0xFFFF);
    }
    
    public final int minorState() {
        return (m_state & 0xFFFF);
    }
    
    public final int batteryLevel() {
        return m_batteryLevel;
    }
    
    public final float altitude() {
        return m_altitude;
    }
    
    public final float pitch() {
        return m_pitch;
    }
    
    public final float roll() {
        return m_roll;
    }
    
    public final float yaw() {
        return m_yaw;
    }
    
    public final float xVelocity() {
        return m_xVelocity;
    }
    
    public final float yVelocity() {
        return m_yVelocity;
    }
    
    public final float zVelocity() {
        return m_zVelocity;
    }
    
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[DemoMessage]")
              .append(" state: ").append(Integer.toHexString(m_state)).append(";")
              .append(" battery: ").append(m_batteryLevel).append(";")
              .append(" altitude: ").append(m_altitude).append(";")
              .append(" pitch: ").append(m_pitch).append(";")
              .append(" roll: ").append(m_roll).append(";")
              .append(" yaw: ").append(m_yaw).append(";")
              .append(" velocity_x: ").append(m_xVelocity).append(";")
              .append(" velocity_y: ").append(m_yVelocity).append(";")
              .append(" velocity_z: ").append(m_zVelocity).append(";");
        return buffer.toString();
    }
    
}
