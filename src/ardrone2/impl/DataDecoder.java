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

import ardrone2.math.Point;
import ardrone2.math.Dimension;

/**
 * Class DataDecoder
 * @author Prostov Yury
 */
public class DataDecoder {

    public static int readInt(byte[] data, int offset) {
        int v1 = ((int)data[offset + 3] << 24) & 0xFF000000;
        int v2 = ((int)data[offset + 2] << 16) & 0x00FF0000;
        int v3 = ((int)data[offset + 1] <<  8) & 0x0000FF00;
        int v4 = ((int)data[offset + 0] <<  0) & 0x000000FF;
        int value = v1 + v2 + v3 + v4;
        return value;
    }
    
    public static short readShort(byte[] data, int offset) {
        int v1 = ((int)data[offset + 1] << 8) & 0xFF00;
        int v2 = ((int)data[offset + 0] << 0) & 0x00FF;
        short value = (short)(v1 + v2);
        return value;
    }
    
    public static byte readByte(byte[] data, int offset) {
        return data[offset];
    }
    
    public static float readFloat(byte[] data, int offset) {
        return Float.intBitsToFloat(readInt(data, offset));
    }
    
    public static int[] readIntArray(byte[] data, int offset, int size) {
        int[] values = new int[size];
        for (int i = 0; i < size; ++i) {
            values[i] = readInt(data, offset + i);
        }
        return values;
    }
    
    public static float[] readFloatArray(byte[] data, int offset, int size) {
        float[] values = new float[size];
        for (int i = 0; i < size; ++i) {
            values[i] = readFloat(data, offset + i);
        }
        return values;
    }
    
    public static Point[] readPointArray(byte[] data, int offset, int size) {
        int[] xValues = readIntArray(data, offset,        size);
        int[] yValues = readIntArray(data, offset + size, size);
        return Conversion.toPointArray(size, xValues, yValues);
    }
    
    public static Dimension[] readDimensionArray(byte[] data, int offset, int size) {
        int[] widthValues = readIntArray(data, offset,        size);
        int[] heightValues = readIntArray(data, offset + size, size);
        return Conversion.toDimensionArray(size, widthValues, heightValues);
    }
    
    public static int calculateChecksum(int data, int checksum) {
        checksum += ((data >> 24) & 0xFF);
        checksum += ((data >> 16) & 0xFF);
        checksum += ((data >> 8)  & 0xFF);
        checksum += (data & 0xFF);
        return checksum;
    }
    
    public static int calculateChecksum(short data, int checksum) {
        checksum += ((data >> 8) & 0xFF);
        checksum += (data & 0xFF);
        return checksum;
    }
    
    public static int calculateChecksum(byte[] data, int checksum) {
        for (int i = 0; i < data.length; ++i) {
            checksum += data[i];
        }
        return checksum;
    }
}
