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

import ardrone2.Command;
import ardrone2.Message;
import java.net.InetAddress;
import java.util.List;

/**
 * Interface ARDroneEngine
 * @author Prostov Yury
 */
public interface ARDroneEngine {
    
    public static enum Channel {
        MessagesStream(0),
        VideoStream(1);
        private int m_id;
        private Channel(int id) { m_id = id; }
        public int getValue() { return m_id; }
    }
    
    public static enum ChannelState {
        Disconnected,
        Connecting,
        Connected,
        Disconnecting,
    }
    
    public static interface StateListener {
        public void onStateChanged(Channel channel, ChannelState state);
    }
    
    public static interface MessageListener {
        public void onMessageReceived(Channel channel, Message message);
    }
    
    public static interface MessageDecoder {
        public int tag();
        public boolean decode(byte[] data, List<Message> messages);
    }
    
    
    public void addStateListener(Channel channel, StateListener listener);
    
    public void removeStateListener(Channel channel, StateListener listener);
    
    public void addMessageListener(Channel channel, MessageListener listener);
    
    public void removeMessageListener(Channel channel, MessageListener listener);
    
    
    public <T extends MessageDecoder> void registerMessageDecoder(Class<T> decoder);
    
    public <T extends MessageDecoder> void unregisterMessageDecoder(Class<T> decoder);
    
    
    public boolean connect(InetAddress address);
    
    public void disconnect();
    
    public void send(Command command);
    
}
