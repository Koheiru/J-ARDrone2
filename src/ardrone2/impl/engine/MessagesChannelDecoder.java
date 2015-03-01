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

import ardrone2.Message;
import ardrone2.impl.ARDroneEngine;
import ardrone2.impl.DataDecoder;
import ardrone2.messages.ChecksumMessage;
import ardrone2.messages.UnknownMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class MessagesChannelDecoder
 * @author Prostov Yury
 */
public class MessagesChannelDecoder extends MessageToMessageDecoder<DatagramPacket> {

    private int m_sequenceId = -1;
    private Map<Integer, ARDroneEngine.MessageDecoder> m_decoders = new HashMap<>();
    
    public MessagesChannelDecoder(List<ARDroneEngine.MessageDecoder> decoders) {
        for (ARDroneEngine.MessageDecoder decoder: decoders) {
            m_decoders.put(decoder.tag(), decoder);
        }
    }
    
    @Override
    protected void decode(ChannelHandlerContext context, DatagramPacket dataPacket, List<Object> out) throws Exception {
        ByteBuf data = dataPacket.content().order(ByteOrder.LITTLE_ENDIAN);

        int header = data.readInt();
        if (header != 0x55667788) {
            //throw new DecoderException();
            return;
        }
        
        //! The first block have fixed known structure: 32-bit integers x 4 (include header).
        int blockTag = header;
        int blockSize = 12;
        byte[] blockData = new byte[blockSize];
        data.readBytes(blockData);
        
        //! Look inside the first block for sequence id.
        int sequenceId = DataDecoder.readInt(blockData, 4);
        if (sequenceId < m_sequenceId) {
            //throw new DecoderException();
            return;
        }
        m_sequenceId = sequenceId;
        
        int checksum = 0;
        checksum = DataDecoder.calculateChecksum(blockTag, checksum);
        //! First block have fixed size and not included in dataPacket.
        //checksum = DataDecoder.calculateChecksum(blockSize, checksum);
        checksum = DataDecoder.calculateChecksum(blockData, checksum);
        
        List<Message> messagesList = new ArrayList<>();
        while (true) {
            if (blockTag == (short)0xFFFF) {
                int messageChecksum = DataDecoder.readInt(blockData, 0);
                messagesList.add(new ChecksumMessage(messageChecksum));
                
                //! Fix problems with signed/unsigned colculations of
                // cheksum from data.
                //if (messageChecksum != checksum) {
                //{
                //    //log...
                //    return;
                //}
                
                for (Message message: messagesList) {
                    out.add(message);
                }
                return;
            }
            
            ARDroneEngine.MessageDecoder decoder = m_decoders.get(blockTag);
            if (decoder == null) {
                Message message = decodeUnknownMessage(blockTag, blockData);
                messagesList.add(message);
            }
            else {
                decoder.decode(blockData, messagesList);
            }
            
            checksum = DataDecoder.calculateChecksum(blockTag, checksum);
            checksum = DataDecoder.calculateChecksum(blockSize, checksum);
            checksum = DataDecoder.calculateChecksum(blockData, checksum);
            
            blockTag = data.readShort();
            blockSize = data.readShort();
            blockData = new byte[blockSize - 4];
            data.readBytes(blockData);
        }
    }
    
    private Message decodeUnknownMessage(int blockTag, byte[] blockData) {
        return new UnknownMessage(blockTag, blockData);
    }
    
}
