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
import ardrone2.messages.StateMessage;
import java.util.List;

/**
 * Class StateMessageDecoder
 * @author Prostov Yury
 */
public class StateMessageDecoder implements ARDroneEngine.MessageDecoder {
    
    private static final int STATE_MESSAGE_TAG = 0x55667788;
    
    @Override
    public int tag() {
        return STATE_MESSAGE_TAG;
    }

    @Override
    public boolean decode(byte[] data, List<Message> messages) {
        if (data.length < 12) {
            return false;
        }
        
        int stateFlags = DataDecoder.readInt(data, 0);
        int sequenceId = DataDecoder.readInt(data, 4);
        int visionFlag = DataDecoder.readInt(data, 8);
        
        messages.add(new StateMessage(stateFlags));
        return true;
    }

}
