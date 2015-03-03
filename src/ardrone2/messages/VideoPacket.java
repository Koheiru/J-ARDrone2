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
 * Class VideoPacket
 * @author Prostov Yury
 */
public class VideoPacket implements Message {
    public int version;
    public int videoCodec;
    public int headerSize;
    public int payloadSize;
    public int encodedWidth;
    public int encodedHeight;
    public int displayWidth;
    public int displayHeight;
    public int frameNumber;
    public int frameTimestamp;
    public int chunksCount;
    public int chunkIndex;
    public int frameType;
    public int controlMark;
    public int streamId;
    public int streamBytePositionLW;
    public int streamBytePositionUW;
    public int slicesCount;
    public int sliceIndex;
    public int spsHeaderSize;
    public int ppsHeaderSize;
    public int    dataSize;
    public byte[] data;
    
    public VideoPacket() {
    }
    
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[VideoMessage]")
              .append(" version: ").append(version).append(";")
              .append(" video_codec: ").append(videoCodec).append(";")
              .append(" header_size: ").append(headerSize).append(";")
              .append(" payload_size: ").append(payloadSize).append(";")
              .append(" encoded_width: ").append(encodedWidth).append(";")
              .append(" encoded_height: ").append(encodedHeight).append(";")
              .append(" display_width: ").append(displayWidth).append(";")
              .append(" display_height: ").append(displayHeight).append(";")
              .append(" frame_number: ").append(frameNumber).append(";")
              .append(" frame_timestamp: ").append(frameTimestamp).append(";")
              .append(" chunks_count: ").append(chunksCount).append(";")
              .append(" chunk_index: ").append(chunkIndex).append(";")
              .append(" frame_type: ").append(frameType).append(";")
              .append(" control_mark: ").append(controlMark).append(";")
              .append(" stream_id: ").append(streamId).append(";")
              .append(" stream_byte_position_lw: ").append(streamBytePositionLW).append(";")
              .append(" stream_byte_position_uw: ").append(streamBytePositionUW).append(";")
              .append(" slices_count: ").append(slicesCount).append(";")
              .append(" slice_index: ").append(sliceIndex).append(";")
              .append(" sps_header_size: ").append(spsHeaderSize).append(";")
              .append(" pps_header_size: ").append(ppsHeaderSize).append(";")
              .append(" data_size: ").append(dataSize).append(";")
              .append(" data: [blob] with size ").append(data.length).append(";");
        return buffer.toString();
    }
}
