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

import ardrone2.messages.VisionDetectMessage;
import ardrone2.messages.StateMessage;
import ardrone2.messages.ChecksumMessage;
import ardrone2.messages.RawMessage;
import ardrone2.messages.DemoMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import ardrone2.api.DroneMessage;
import ardrone2.math.Conversion;
import ardrone2.math.Dimension;
import ardrone2.math.Matrix3x3;
import ardrone2.math.Point;
import ardrone2.math.Vector3D;

/**
 * Class EngineDecoder
 * @author Prostov Yury
 */
public class EngineDecoder extends MessageToMessageDecoder<DatagramPacket> {
    
    private int m_sequenceId = -1;
    
    public void reset() {
        m_sequenceId = -1;
    }
    
    @Override
    protected void decode(ChannelHandlerContext context, DatagramPacket message, List<Object> out) throws Exception {
        ByteBuf data = message.content().order(ByteOrder.LITTLE_ENDIAN);

        int header = data.readInt();
        if (header != 0x55667788) {
            //throw new DecoderException();
        }
        
        int stateFlags = data.readInt();
        int sequenceId = data.readInt();
        int visionFlag = data.readInt();
        
        if (sequenceId < m_sequenceId) {
            return;
        }
        m_sequenceId = sequenceId;
        
        int checksum = calculateChecksum(header, 0);
        checksum = calculateChecksum(stateFlags, checksum);
        checksum = calculateChecksum(sequenceId, checksum);
        checksum = calculateChecksum(visionFlag, checksum);
        
        List<DroneMessage> messagesList = new ArrayList<>();
        messagesList.add(new StateMessage(stateFlags));
        
        while (true) {
            short blockTag = data.readShort();
            short blockSize = data.readShort();
            
            if (blockTag == ChecksumMessage.TAG) {
                int messageChecksum = data.readInt();
                messagesList.add(new ChecksumMessage(messageChecksum));
                
                //! Fix problems with signed/unsigned colculations of
                // cheksum from data.
                //if (messageChecksum != checksum) {
                //{
                //    //log...
                //    return;
                //}
                
                for (DroneMessage entity : messagesList) {
                    out.add(entity);
                }
                return;
            }
            
            byte[] blockData = new byte[blockSize - 4];
            data.readBytes(blockData);
            
            DroneMessage droneMessage = null;
            switch (blockTag) {
                case DemoMessage.TAG:
                    droneMessage = decodeDemoMessage(blockTag, blockData);
                    break;
                case VisionDetectMessage.TAG:
                    droneMessage = decodeVisionDetectMessage(blockTag, blockData);
                    break;
                default:
                    droneMessage = decodeRawMessage(blockTag, blockData);
                    break;
            }
            
            if (droneMessage != null) {
                messagesList.add(droneMessage);
            }
            
            checksum = calculateChecksum(blockTag, checksum);
            checksum = calculateChecksum(blockSize, checksum);
            checksum = calculateChecksum(blockData, checksum);
        }
    }
    
    private DroneMessage decodeDemoMessage(short blockTag, byte[] blockData) {
        int state        = readInt(blockData, 0);
        int batteryLevel = readInt(blockData, 4);
        float pitchAngle = readFloat(blockData, 8)  / 1000.0f;
        float rollAngle  = readFloat(blockData, 12)  / 1000.0f;
        float yawAngle   = readFloat(blockData, 16) / 1000.0f;
        float altitude   = ((float)readInt(blockData, 20)) / 1000.0f;
        float xVelocity  = readFloat(blockData, 24);
        float yVelocity  = readFloat(blockData, 28);
        float zVelocity  = readFloat(blockData, 32);
        
        int majorState = (state >> 16) & 0xFFFF;
        int minorState = state & 0xFFFF;
        return new DemoMessage(majorState, minorState, batteryLevel, 
                               altitude, pitchAngle, rollAngle, yawAngle, 
                               xVelocity, yVelocity, zVelocity);
    }
    
    private VisionDetectMessage decodeVisionDetectMessage(short blockTag, byte[] blockData) {
        final int valuesCount  = 4;
        final int subBlockSize = 4 * 4; //!< sizeof(uint32_t) * valuesCount
        
        int objectsCount  = readInt(blockData, 0);
        int[] objectsType  = readIntArray(blockData, 4 + subBlockSize * 0, valuesCount);
        
        int[] objectsPosX  = readIntArray(blockData, 4 + subBlockSize * 1, valuesCount);
        int[] objectsPosY  = readIntArray(blockData, 4 + subBlockSize * 2, valuesCount);
        Point[] objectsPosition = Conversion.toPointArray(valuesCount, objectsPosX, objectsPosY);
        
        int[] objectsWidth = readIntArray(blockData, 4 + subBlockSize * 3, valuesCount);
        int[] objectHeight = readIntArray(blockData, 4 + subBlockSize * 4, valuesCount);
        Dimension[] objectsDimension = Conversion.toDimensionArray(valuesCount, objectsWidth, objectHeight);
        
        int[] objectsCameraSource = new int[valuesCount];
        int[] objectsDistance = new int[valuesCount];
        float[] objectsOrientation = new float[valuesCount];
        Vector3D[] objectsTranslation = new Vector3D[valuesCount];
        Matrix3x3[] objectsRotation = new Matrix3x3[valuesCount];
        /*
            public VisionDetectMessage(int detectedCount, int[] cameraSources, int[] objectTypes,
            int[] objectDistances, Point[] objectPositions, Dimension[] objectDimensions,
            float[] objectOrientations, Vector3D[] objectTranslations, Matrix3x3[] objectRotations)
        */
        return new VisionDetectMessage(objectsCount, objectsType, objectsCameraSource,
                objectsDistance, objectsPosition, objectsDimension, 
                objectsOrientation, objectsTranslation, objectsRotation);
    }
    
    private DroneMessage decodeRawMessage(short blockTag, byte[] blockData) {
        return new RawMessage(blockTag, blockData);
    }
    
    private int readInt(byte[] data, int offset) {
        int v1 = ((int)data[offset + 3] << 24) & 0xFF000000;
        int v2 = ((int)data[offset + 2] << 16) & 0x00FF0000;
        int v3 = ((int)data[offset + 1] <<  8) & 0x0000FF00;
        int v4 = ((int)data[offset + 0] <<  0) & 0x000000FF;
        int value = v1 + v2 + v3 + v4;
        return value;
    }
    
    private float readFloat(byte[] data, int offset) {
        return Float.intBitsToFloat(readInt(data, offset));
    }
    
    private int[] readIntArray(byte[] data, int offset, int size) {
        int[] values = new int[size];
        for (int i = 0; i < size; ++i) {
            values[i] = readInt(data, offset + i);
        }
        return values;
    }
    
    private float[] readFloatArray(byte[] data, int offset, int size) {
        float[] values = new float[size];
        for (int i = 0; i < size; ++i) {
            values[i] = readFloat(data, offset + i);
        }
        return values;
    }
    
    private int calculateChecksum(int data, int checksum) {
        checksum += ((data >> 24) & 0xFF);
        checksum += ((data >> 16) & 0xFF);
        checksum += ((data >> 8)  & 0xFF);
        checksum += (data & 0xFF);
        return checksum;
    }
    
    private int calculateChecksum(short data, int checksum) {
        checksum += ((data >> 8) & 0xFF);
        checksum += (data & 0xFF);
        return checksum;
    }
    
    private int calculateChecksum(byte[] data, int checksum) {
        for (int i = 0; i < data.length; ++i) {
            checksum += data[i];
        }
        return checksum;
    }
    
}
