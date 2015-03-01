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
import ardrone2.math.Matrix3x3;
import ardrone2.math.Vector3D;
import ardrone2.messages.VisionDetectMessage;
import java.util.List;

/**
 * Class VisionDetectMessageDecoder
 * @author Prostov Yury
 */
public class VisionDetectMessageDecoder implements ARDroneEngine.MessageDecoder {
    
    private static final int VISION_DETECT_MESSAGE_TAG = 0x0010;
    private static final int VALUES_LENGTH = 4;
    private static final int VALUES_SIZE   = (Integer.SIZE / 8) * VALUES_LENGTH;
    
    @Override
    public int tag() {
        return VISION_DETECT_MESSAGE_TAG;
    }

    @Override
    public boolean decode(byte[] data, List<Message> messages) {
        if (data.length < 4 + 5 * VALUES_SIZE) {
            return false;
        }
        
        VisionDetectMessage message = new VisionDetectMessage();
        
        int data_offset = 0;
        message.detectedCount = DataDecoder.readInt(data, data_offset);
        
        data_offset += 4;
        message.objectTypes = DataDecoder.readIntArray(data, data_offset, VALUES_LENGTH);
        
        data_offset += VALUES_SIZE;
        message.objectPositions  = DataDecoder.readPointArray(data, data_offset, VALUES_LENGTH);
        
        data_offset += 2 * VALUES_SIZE;
        message.objectDimensions = DataDecoder.readDimensionArray(data, data_offset, VALUES_SIZE);
        
        //! TODO: read the rest...
        message.cameraSources      = new int[VALUES_LENGTH];
        message.objectDistances    = new int[VALUES_LENGTH];
        message.objectOrientations = new float[VALUES_LENGTH];
        message.objectRotations    = new Matrix3x3[VALUES_LENGTH];
        message.objectTranslations = new Vector3D[VALUES_LENGTH];
        
        messages.add(message);
        return true;
    }

}
