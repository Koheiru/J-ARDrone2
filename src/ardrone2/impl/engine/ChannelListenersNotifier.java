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
import ardrone2.impl.ARDroneEngine.Channel;
import ardrone2.impl.ARDroneEngine.ChannelState;
import ardrone2.impl.ARDroneEngine.StateListener;
import ardrone2.impl.ARDroneEngine.MessageListener;
import ardrone2.impl.ListenersList;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Class ChannelListenersNotifier
 * @author Prostov Yury
 */
public class ChannelListenersNotifier extends ChannelInboundHandlerAdapter {

    private Channel m_channel;
    private ListenersList<StateListener> m_stateListeners;
    private ListenersList<MessageListener> m_messageListeners;
    
    public ChannelListenersNotifier(Channel channel, 
            ListenersList<StateListener> stateListeners, 
            ListenersList<MessageListener> messageListeners) {
        m_channel = channel;
        m_stateListeners = stateListeners;
        m_messageListeners = messageListeners;
    }
    
    @Override
    public final void channelRead(ChannelHandlerContext context, Object object) throws Exception {
        if (object instanceof Message) {
            Message message = (Message)object;
            MessageListener[] listeners = (MessageListener[])m_messageListeners.listeners();
            for (MessageListener listener : listeners) {
                listener.onMessageReceived(m_channel, message);
            }
        }
        context.fireChannelRead(object);
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext context, Object object) throws Exception {
        if (object instanceof ChannelState) {
            ChannelState state = (ChannelState)object;
            StateListener[] listeners = (StateListener[])m_stateListeners.listeners();
            for (StateListener listener : listeners) {
                listener.onStateChanged(m_channel, state);
            }
        }
        context.fireUserEventTriggered(object);
    }
}
