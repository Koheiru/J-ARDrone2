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

package ardrone2.impl.engine;

import ardrone2.impl.DataDecoder;
import ardrone2.messages.VideoPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

/**
 * Class VideoChannelDecoder
 * @author Prostov Yury
 */
public class VideoChannelDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final int PaVE_STRUCT_SIZE = 64;
    private VideoPacket m_message = null;

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
    protected void decode(ChannelHandlerContext context, ByteBuf stream, List<Object> out) throws Exception {
        while (stream.readableBytes() > 0) {
            if (!isValidHeader(stream)) {
                if (isHeaderExpected()) {
                    context.fireChannelRead(stream);
                    return;
                }
            }
            else {
                if (!isHeaderExpected()) {
                    // Log warning...
                    reset();
                }
                readHeader(stream);
            }

            readData(stream);
            if (isCompleted()) {
                out.add(m_message);
                reset();
            }
        }
    }
    
    private boolean isHeaderExpected() {
        return (m_message == null);
    }
    
    private boolean isValidHeader(ByteBuf stream) {
        if (stream.readableBytes() < PaVE_STRUCT_SIZE) {
            return false;
        }
        
        byte value_1 = stream.readByte();
        byte value_2 = stream.readByte();
        byte value_3 = stream.readByte();
        byte value_4 = stream.readByte();
        stream.readerIndex(stream.readerIndex() - 4);
        
        if (value_1 != 'P' || value_2 != 'a' || value_3 != 'V' || value_4 != 'E') {
            return false;
        }
        
        return true;
    }
    
    private boolean isCompleted() {
        return (m_message.dataSize == m_message.payloadSize);
    }
    
    private void reset() {
        m_message = null;
    }
    
    private void readHeader(ByteBuf stream) {
        byte[] data = new byte[PaVE_STRUCT_SIZE];
        stream.readBytes(data);
        
        m_message = new VideoPacket();
        m_message.version              = DataDecoder.readByte (data, 4);
        m_message.videoCodec           = DataDecoder.readByte (data, 5);
        m_message.headerSize           = DataDecoder.readShort(data, 6);
        m_message.payloadSize          = DataDecoder.readInt  (data, 8);
        m_message.encodedWidth         = DataDecoder.readShort(data, 12);
        m_message.encodedHeight        = DataDecoder.readShort(data, 14);
        m_message.displayWidth         = DataDecoder.readShort(data, 16);
        m_message.displayHeight        = DataDecoder.readShort(data, 18);
        m_message.frameNumber          = DataDecoder.readInt  (data, 20);
        m_message.frameTimestamp       = DataDecoder.readInt  (data, 24);
        m_message.chunksCount          = DataDecoder.readByte (data, 28);
        m_message.chunkIndex           = DataDecoder.readByte (data, 29);
        m_message.frameType            = DataDecoder.readByte (data, 30);
        m_message.controlMark          = DataDecoder.readByte (data, 31);
        m_message.streamBytePositionLW = DataDecoder.readInt  (data, 32);
        m_message.streamBytePositionUW = DataDecoder.readInt  (data, 36);
        m_message.streamId             = DataDecoder.readShort(data, 40);
        m_message.slicesCount          = DataDecoder.readByte (data, 42);
        m_message.sliceIndex           = DataDecoder.readByte (data, 43);
        m_message.spsHeaderSize        = DataDecoder.readByte (data, 44);
        m_message.ppsHeaderSize        = DataDecoder.readByte (data, 45);
        
        // Plus some additional fields - skip them.
        int skippedFieldsSize = m_message.headerSize - PaVE_STRUCT_SIZE;
        stream.readerIndex(stream.readerIndex() + skippedFieldsSize);
        
        m_message.data = new byte[m_message.payloadSize];
        m_message.dataSize = 0;
    }
    
    private void readData(ByteBuf stream) {
        int expectedSize = m_message.payloadSize - m_message.dataSize;
        int availableSize = stream.readableBytes();
        int readSize = Math.min(expectedSize, availableSize);
        stream.readBytes(m_message.data, m_message.dataSize, readSize);
        m_message.dataSize += readSize;
    }
    
}
