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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

/**
 * Class VideoAggregator
 * @author Prostov Yury
 */
public class VideoAggregator extends MessageToMessageDecoder<ByteBuf> {
    private static final int PaVE_STRUCT_SIZE = 64;
    private VideoPacket m_packet = null;
    
    public VideoAggregator() {
        super();
    }
    
    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        super.channelActive(context);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        reset();
        super.channelInactive(context);
    }
    
    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf message, List<Object> out) throws Exception {
        while (message.readableBytes() > 0) {
            if (!isValidHeader(message)) {
                if (isHeaderExpected()) {
                    context.fireChannelRead(message);
                    return;
                }
            }
            else {
                if (!isHeaderExpected()) {
                    // Log warning...
                    reset();
                }
                readHeader(message);
            }

            readData(message);
            if (isCompleted()) {
                out.add(m_packet);
                reset();
            }
        }
    }
    
    private boolean isHeaderExpected() {
        return (m_packet == null);
    }
    
    private boolean isValidHeader(ByteBuf message) {
        if (message.readableBytes() < PaVE_STRUCT_SIZE) {
            return false;
        }
        
        byte value_1 = message.readByte();
        byte value_2 = message.readByte();
        byte value_3 = message.readByte();
        byte value_4 = message.readByte();
        message.readerIndex(message.readerIndex() - 4);
        
        if (value_1 != 'P' || value_2 != 'a' || value_3 != 'V' || value_4 != 'E') {
            return false;
        }
        
        return true;
    }
    
    private boolean isCompleted() {
        return (m_packet.dataSize == m_packet.payloadSize);
    }
    
    private void reset() {
        m_packet = null;
    }
    
    private void readHeader(ByteBuf message) {
        byte[] data = new byte[PaVE_STRUCT_SIZE];
        message.readBytes(data);
        
        m_packet = new VideoPacket();
        m_packet.version              = readByte (data, 4);
        m_packet.videoCodec           = readByte (data, 5);
        m_packet.headerSize           = readShort(data, 6);
        m_packet.payloadSize          = readInt  (data, 8);
        m_packet.encodedStreamWidth   = readShort(data, 12);
        m_packet.encodedStreamHeight  = readShort(data, 14);
        m_packet.displayWidth         = readShort(data, 16);
        m_packet.displayHeight        = readShort(data, 18);
        m_packet.frameNumber          = readInt  (data, 20);
        m_packet.frameTimestamp       = readInt  (data, 24);
        m_packet.chunksCount          = readByte (data, 28);
        m_packet.chunkIndex           = readByte (data, 29);
        m_packet.frameType            = readByte (data, 30);
        m_packet.control              = readByte (data, 31);
        m_packet.streamBytePositionLW = readInt  (data, 32);
        m_packet.streamBytePositionUW = readInt  (data, 36);
        m_packet.streamId             = readShort(data, 40);
        m_packet.slicesCount          = readByte (data, 42);
        m_packet.sliceIndex           = readByte (data, 43);
        m_packet.spsHeaderSize        = readByte (data, 44);
        m_packet.ppsHeaderSize        = readByte (data, 45);
        
        // Plus some additional fields - skip them.
        int additionalFieldsSize = m_packet.headerSize - PaVE_STRUCT_SIZE;
        message.readerIndex(message.readerIndex() + additionalFieldsSize);
        
        m_packet.data = new byte[m_packet.payloadSize];
        m_packet.dataSize = 0;
    }
    
    private void readData(ByteBuf message) {
        int expectedSize = m_packet.payloadSize - m_packet.dataSize;
        int availableSize = message.readableBytes();
        int readSize = Math.min(expectedSize, availableSize);
        message.readBytes(m_packet.data, m_packet.dataSize, readSize);
        m_packet.dataSize += readSize;
    }
    
    private int readInt(byte[] data, int offset) {
        int v1 = ((int)data[offset + 3] << 24) & 0xFF000000;
        int v2 = ((int)data[offset + 2] << 16) & 0x00FF0000;
        int v3 = ((int)data[offset + 1] <<  8) & 0x0000FF00;
        int v4 = ((int)data[offset + 0] <<  0) & 0x000000FF;
        int value = v1 + v2 + v3 + v4;
        return value;
    }
    
    private short readShort(byte[] data, int offset) {
        int v1 = ((int)data[offset + 1] << 8) & 0xFF00;
        int v2 = ((int)data[offset + 0] << 0) & 0x00FF;
        short value = (short)(v1 + v2);
        return value;
    }
    
    private byte readByte(byte[] data, int offset) {
        return data[offset];
    }
}
