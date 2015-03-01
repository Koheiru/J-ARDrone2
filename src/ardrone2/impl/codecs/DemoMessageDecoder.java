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

package ardrone2.impl.codecs;

import ardrone2.Message;
import ardrone2.impl.ARDroneEngine;
import ardrone2.impl.DataDecoder;
import ardrone2.messages.DemoMessage;
import java.util.List;

/**
 * Class DemoMessageDecoder
 * @author Prostov Yury
 */
public class DemoMessageDecoder implements ARDroneEngine.MessageDecoder {
    
    private static final int DEMO_MESSAGE_TAG = 0x0000;
    
    @Override
    public int tag() {
        return DEMO_MESSAGE_TAG;
    }

    @Override
    public boolean decode(byte[] data, List<Message> messages) {
        if (data.length < 36) {
            return false;
        }
        
        DemoMessage message = new DemoMessage();
        message.state        = DataDecoder.readInt  (data, 0);
        message.batteryLevel = DataDecoder.readInt  (data, 4);
        message.pitch        = DataDecoder.readFloat(data, 8)  / 1000.0f;
        message.roll         = DataDecoder.readFloat(data, 12) / 1000.0f;
        message.yaw          = DataDecoder.readFloat(data, 16) / 1000.0f;
        message.altitude     = DataDecoder.readInt  (data, 20) / 1000.0f;
        message.xVelocity    = DataDecoder.readFloat(data, 24);
        message.yVelocity    = DataDecoder.readFloat(data, 28);
        message.zVelocity    = DataDecoder.readFloat(data, 32);
        
        messages.add(message);
        return true;
    }

}
