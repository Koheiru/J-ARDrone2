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


/**
 * Class VideoPacket
 * @author Prostov Yury
 */
public class VideoPacket {
    public int version;
    public int videoCodec;
    public int headerSize;
    public int payloadSize;
    public int encodedStreamWidth;
    public int encodedStreamHeight;
    public int displayWidth;
    public int displayHeight;
    public int frameNumber;
    public int frameTimestamp;
    public int chunksCount;
    public int chunkIndex;
    public int frameType;
    public int control;
    public int streamId;
    public int streamBytePositionLW;
    public int streamBytePositionUW;
    public int slicesCount;
    public int sliceIndex;
    public int spsHeaderSize;
    public int ppsHeaderSize;
    public byte[] data;
    public int    dataSize;
}
