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

import ardrone2.Message;
import ardrone2.impl.ARDroneEngine.Channel;
import ardrone2.impl.ARDroneEngine.ChannelState;
import ardrone2.impl.ARDroneEngine.MessageDecoder;
import ardrone2.impl.ARDroneEngine.StateListener;
import ardrone2.impl.ARDroneEngine.MessageListener;


/**
 * Class ARDroneModule
 * @author Prostov Yury
 */
public abstract class ARDroneModule {
    
    private class ARDroneEngineListener implements StateListener, MessageListener {
        @Override
        public void onStateChanged(Channel channel, ChannelState state) {
            ARDroneModule.this.onStateChanged(state);
        }
        
        @Override
        public void onMessageReceived(Channel channel, Message message) {
            ARDroneModule.this.onMessageReceived(message);
        }
    }
    
    private Channel m_channel;
    
    //! TODO: reimplements this ugly code.
    private Class<? extends MessageDecoder> m_decoder1 = null;
    private Class<? extends MessageDecoder> m_decoder2 = null;
    
    private ARDroneEngine m_engine = null;
    private ARDroneEngineListener m_engineListener = null;
    
    public ARDroneModule() {
    }
    
    public ARDroneModule(Channel listenedChannel) {
        this();
        m_channel = listenedChannel;
        m_engineListener = new ARDroneEngineListener();
    }
    
    public ARDroneModule(Channel listenedChannel, Class<? extends MessageDecoder> channelDecoder) {
        this(listenedChannel);
        m_decoder1 = channelDecoder;
    }
    
    public ARDroneModule(Channel listenedChannel, Class<? extends MessageDecoder> channelDecoder1, 
                         Class<? extends MessageDecoder> channelDecoder2) {
        this(listenedChannel, channelDecoder1);
        m_decoder2 = channelDecoder2;
    }
    
    public void initialize(ARDroneEngine engine) {
        m_engine = engine;
        if (m_decoder1 != null) { m_engine.registerMessageDecoder(m_decoder1); }
        if (m_decoder2 != null) { m_engine.registerMessageDecoder(m_decoder2); }
    }
    
    public void deinitialize() {
    }
    
    protected ARDroneEngine engine() {
        return m_engine;
    }
    
    protected void subscribeToStates() {
        m_engine.addStateListener(m_channel, m_engineListener);
    }
    
    protected void unsubscribeFromStates() {
        m_engine.removeStateListener(m_channel, m_engineListener);
    }
    
    protected void subscribeToMessages() {
        m_engine.addMessageListener(m_channel, m_engineListener);
    }
    
    protected void unsubscribeFromMessages() {
        m_engine.removeMessageListener(m_channel, m_engineListener);
    }
    
    protected void onStateChanged(ChannelState state) {
    }
    
    protected void onMessageReceived(Message message) {
    }
    
}
