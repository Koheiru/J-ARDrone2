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
 * Class StateMessage
 * @author Prostov Yury
 */
public class StateMessage implements Message {
    
    public int flags = 0;
    
    /**** State flags ****/
    public static final int FLYING_FLAG              = 1;
    public static final int VIDEO_ENABLED_FLAG       = 1 << 1;
    public static final int VISION_ENABLED_FLAG      = 1 << 2;
    public static final int ANGULAR_SPEED_CONTROL_FLAG = 1 << 3;
    public static final int ALTITUDE_CONTROL_FLAG    = 1 << 4;
    public static final int USER_FEEDBACK_FLAG       = 1 << 5;
    public static final int CTRL_RECEIVED_FLAG       = 1 << 6;
    public static final int CAMERA_READY_FLAG        = 1 << 7;
    public static final int TRAVELLING_ENABLED_FLAG  = 1 << 8;
    public static final int USB_READY_FLAG           = 1 << 9;
    public static final int ONLY_DEMO_DATA_FLAG      = 1 << 10;
    public static final int BOOTSTRAP_STATE_FLAG     = 1 << 11;
    public static final int MOTORS_DOWN_FLAG         = 1 << 12;
    public static final int COM_LOST_FLAG            = 1 << 13;
    public static final int SOFTWARE_FAULT_FLAG      = 1 << 14;
    public static final int BATTERY_TOO_LOW_FLAG     = 1 << 15;
    public static final int USER_EMERGENCY_FLAG      = 1 << 16;
    public static final int TIMER_ELAPSED_FLAG       = 1 << 17;
    public static final int MAGNETO_STRAYED_FLAG     = 1 << 18;
    public static final int ANGELS_OUT_OF_RANGE_FLAG = 1 << 19;
    public static final int TOO_MUCH_WIND_FLAG       = 1 << 20;
    public static final int ULTRASONIC_DEAF_FLAG     = 1 << 21;
    public static final int CUTOUT_DETECTED_FLAG     = 1 << 22;
    public static final int PIC_VERSION_OK_FLAG      = 1 << 23;
    public static final int COMMAND_THREAD_ON_FLAG   = 1 << 24;
    public static final int NAVDATA_THREAD_ON_FLAG   = 1 << 25;
    public static final int VIDEO_THREAD_ON_FLAG     = 1 << 26;
    public static final int ACQUISION_THREAD_ON_FLAG = 1 << 27;
    public static final int CTRL_WDG_DELAYED_FLAG    = 1 << 28;
    public static final int ADC_WDG_DELAYED_FLAG     = 1 << 29;
    public static final int COM_PROBLEM_FLAG         = 1 << 30;
    public static final int EMERGENCY_FLAG           = 1 << 31;
    
    public StateMessage() {
    }
    
    public StateMessage(int flags) {
        this.flags = flags;
    }
    
    public final void enableFlags(int enabledFlags) {
        flags = flags | enabledFlags;
    }
    
    public final void disableFlags(int disabledFlags) {
        flags = flags & ~disabledFlags;
    }
    
    public final boolean isFlagsEnabled(int testFlags) {
        return (Integer.bitCount(testFlags) == Integer.bitCount(flags & testFlags));
    }
    
    public final boolean isFlagsDisabled(int testFlags) {
        return (Integer.bitCount(flags & testFlags) == 0);
    }
    
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[StateMessage]")
              .append(" flying: ").append(isFlagsEnabled(FLYING_FLAG)).append(";")
              .append(" video_enabled: ").append(isFlagsEnabled(VIDEO_ENABLED_FLAG)).append(";")
              .append(" vision_enabled: ").append(isFlagsEnabled(VISION_ENABLED_FLAG)).append(";")
              .append(" angular_speed_control_enabled: ").append(isFlagsEnabled(ANGULAR_SPEED_CONTROL_FLAG)).append(";")
              .append(" altitude_control_enabled: ").append(isFlagsEnabled(ALTITUDE_CONTROL_FLAG)).append(";")
              .append(" user_feedback_on: ").append(isFlagsEnabled(USER_FEEDBACK_FLAG)).append(";")
              .append(" control_received: ").append(isFlagsEnabled(CTRL_RECEIVED_FLAG)).append(";")
              .append(" camera_ready: ").append(isFlagsEnabled(CAMERA_READY_FLAG)).append(";")
              .append(" travelling_enabled:: ").append(isFlagsEnabled(TRAVELLING_ENABLED_FLAG)).append(";")
              .append(" usb_ready: ").append(isFlagsEnabled(USB_READY_FLAG)).append(";")
              .append(" only_demo_data_send: ").append(isFlagsEnabled(ONLY_DEMO_DATA_FLAG)).append(";")
              .append(" bootstrap_state: ").append(isFlagsEnabled(BOOTSTRAP_STATE_FLAG)).append(";")
              .append(" motors_down: ").append(isFlagsEnabled(MOTORS_DOWN_FLAG)).append(";")
              .append(" communication_lost: ").append(isFlagsEnabled(COM_LOST_FLAG)).append(";")
              .append(" battery_too_low: ").append(isFlagsEnabled(BATTERY_TOO_LOW_FLAG)).append(";")
              .append(" user_emergency: ").append(isFlagsEnabled(USER_EMERGENCY_FLAG)).append(";")
              .append(" timer_elapsed: ").append(isFlagsEnabled(TIMER_ELAPSED_FLAG)).append(";")
              .append(" magneto_strayed: ").append(isFlagsEnabled(MAGNETO_STRAYED_FLAG)).append(";")
              .append(" angels_out_of_range: ").append(isFlagsEnabled(ANGELS_OUT_OF_RANGE_FLAG)).append(";")
              .append(" too_much_wind: ").append(isFlagsEnabled(TOO_MUCH_WIND_FLAG)).append(";")
              .append(" ultrasonic_deaf: ").append(isFlagsEnabled(ULTRASONIC_DEAF_FLAG)).append(";")
              .append(" cutout_detected: ").append(isFlagsEnabled(CUTOUT_DETECTED_FLAG)).append(";")
              .append(" pic_version_ok: ").append(isFlagsEnabled(PIC_VERSION_OK_FLAG)).append(";")
              .append(" common_thread_on: ").append(isFlagsEnabled(COMMAND_THREAD_ON_FLAG)).append(";")
              .append(" nav_data_thread_on: ").append(isFlagsEnabled(NAVDATA_THREAD_ON_FLAG)).append(";")
              .append(" video_thread_on: ").append(isFlagsEnabled(VIDEO_THREAD_ON_FLAG)).append(";")
              .append(" acquision_thread_on: ").append(isFlagsEnabled(ACQUISION_THREAD_ON_FLAG)).append(";")
              .append(" control_watchdog_delayed: ").append(isFlagsEnabled(CTRL_WDG_DELAYED_FLAG)).append(";")
              .append(" adc_watchdog_delayed: ").append(isFlagsEnabled(ADC_WDG_DELAYED_FLAG)).append(";")
              .append(" communication_problems: ").append(isFlagsEnabled(COM_PROBLEM_FLAG)).append(";")
              .append(" emergency_mode: ").append(isFlagsEnabled(EMERGENCY_FLAG)).append(";");
        return buffer.toString();
    }
    
    
}
