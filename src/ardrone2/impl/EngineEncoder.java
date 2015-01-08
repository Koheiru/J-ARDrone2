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
import io.netty.handler.codec.MessageToByteEncoder;
import ardrone2.DroneCommand;
import ardrone2.commands.WatchDogCommand;

/**
 * Class EngineEncoder
 * @author Prostov Yury
 */
public class EngineEncoder extends MessageToByteEncoder<DroneCommand> {

    private int m_sequenceId = 0;
    
    public void reset() {
        m_sequenceId = 0;
    }
    
    @Override
    protected void encode(ChannelHandlerContext context, DroneCommand command, ByteBuf out) throws Exception {
        if (command instanceof WatchDogCommand) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(command.name()).append('\r');
            out.writeBytes(buffer.toString().getBytes("ASCII"));
            return;
        }
        
        StringBuilder buffer = new StringBuilder();
        buffer.append(command.name()).append("=").append(++m_sequenceId);
        
        for (Object param: command.parameters()) {
            if (param instanceof String) {
                buffer.append(",\"").append(param).append('"');
            }
            else if (param instanceof Integer) {
                buffer.append(',').append(param.toString());
            } 
            else if (param instanceof Float) {
                buffer.append(',').append(Integer.toString(Float.floatToIntBits((Float)param)));
            }
            else {
                throw new IllegalArgumentException("Unsupported parameter type: " + 
                                                   param.getClass().getName());
            }
        }
        
        buffer.append('\r');
        out.writeBytes(buffer.toString().getBytes("ASCII"));
    }
    
}
