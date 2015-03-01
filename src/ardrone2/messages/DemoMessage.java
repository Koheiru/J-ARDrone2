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

import ardrone2.Message;

/**
 * Class DemoMessage
 * @author Prostov Yury
 */
public class DemoMessage implements Message {
    
    public int   state        = 0;
    public int   batteryLevel = 0;
    public float altitude     = 0.0f;
    public float pitch        = 0.0f;
    public float roll         = 0.0f;
    public float yaw          = 0.0f;
    public float xVelocity    = 0.0f;
    public float yVelocity    = 0.0f;
    public float zVelocity    = 0.0f;
    
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
    
    public DemoMessage() {
    }
    
    public DemoMessage(int majorState, int minorState, int batteryLevel, 
                       float altitude, float pitch, float roll, float yaw, 
                       float xVelocity, float yVelocity, float zVelocity) {
        this.state        = (majorState << 16) | (minorState & 0xFFFF);
        this.batteryLevel = batteryLevel;
        this.altitude     = altitude;
        this.pitch        = pitch;
        this.roll         = roll;
        this.yaw          = yaw;
        this.xVelocity    = xVelocity;
        this.yVelocity    = yVelocity;
        this.zVelocity    = zVelocity;
    }
    
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[DemoMessage]")
              .append(" state: ").append(Integer.toHexString(state)).append(";")
              .append(" battery: ").append(batteryLevel).append(";")
              .append(" altitude: ").append(altitude).append(";")
              .append(" pitch: ").append(pitch).append(";")
              .append(" roll: ").append(roll).append(";")
              .append(" yaw: ").append(yaw).append(";")
              .append(" velocity_x: ").append(xVelocity).append(";")
              .append(" velocity_y: ").append(yVelocity).append(";")
              .append(" velocity_z: ").append(zVelocity).append(";");
        return buffer.toString();
    }
    
}
