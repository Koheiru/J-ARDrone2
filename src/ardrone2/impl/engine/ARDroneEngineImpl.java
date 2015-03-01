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

import ardrone2.Command;
import ardrone2.impl.ARDroneEngine;
import ardrone2.impl.ListenersList;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class ARDroneEngineImpl
 * @author Prostov Yury
 */
public class ARDroneEngineImpl implements ARDroneEngine {
    
    private class ChannelStateWatcher extends ChannelInboundHandlerAdapter {
        
        private boolean m_isFirstMessage = true;
        
        @Override
        public void channelActive(ChannelHandlerContext context) throws Exception {
            byte[] handshake = {0x01, 0x00, 0x00, 0x00};
            context.writeAndFlush(handshake);
            super.channelActive(context);
        }
        
        @Override
        public void channelInactive(ChannelHandlerContext context) throws Exception {
            ChannelState disconnectedState = ChannelState.Disconnected;
            context.fireUserEventTriggered(disconnectedState);
            m_isFirstMessage = true;
            super.channelInactive(context);
        }
        
        @Override
        public final void channelRead(ChannelHandlerContext context, Object object) throws Exception {
            if (m_isFirstMessage) {
                m_isFirstMessage = false;
                ChannelState connectedState = ChannelState.Connected;
                context.fireUserEventTriggered(connectedState);
            }
            super.channelRead(context, object);
        }
        
    }

    private EventLoopGroup m_eventLoop = new NioEventLoopGroup();
    private io.netty.channel.Channel m_cmdChannel = null;
    private io.netty.channel.Channel m_msgChannel = null;
    private io.netty.channel.Channel m_videoChannel = null;
    
    private InetAddress m_address = null;
    private static final int COMMANDS_PORT = 5556;
    private static final int MESSAGES_PORT = 5554;
    private static final int VIDEO_PORT    = 5555;
    
    private List<MessageDecoder> m_decoders = new ArrayList<>();
    private List<ListenersList<StateListener>> m_stateListeners = new ArrayList<>();
    private List<ListenersList<MessageListener>> m_messageListeners = new ArrayList<>();
    
    //! NOTE: connection state is defined by command-message channels as they
    // needs to control drone when other channels is aux.
    private final Object m_sync = new Object();
    private boolean m_isConnected = false;
    
    public ARDroneEngineImpl() {
        for (Channel channel: Channel.values()) {
            m_stateListeners.add(new ListenersList<StateListener>(StateListener.class));
            m_messageListeners.add(new ListenersList<MessageListener>(MessageListener.class));
        }
    }
    
    @Override
    public void addStateListener(Channel channel, StateListener listener) {
        m_stateListeners.get(channel.getValue()).addListener(listener);
    }

    @Override
    public void removeStateListener(Channel channel, StateListener listener) {
        m_stateListeners.get(channel.getValue()).removeListener(listener);
    }
    
    @Override
    public void addMessageListener(Channel channel, MessageListener listener) {
        m_messageListeners.get(channel.getValue()).addListener(listener);
    }

    @Override
    public void removeMessageListener(Channel channel, MessageListener listener) {
        //! TODO: close appropriate channel when listeners list is empty.
        m_messageListeners.get(channel.getValue()).removeListener(listener);
    }

    @Override
    public <T extends MessageDecoder> void registerMessageDecoder(Class<T> decoder) {
        for (MessageDecoder definedDecoder: m_decoders) {
            if (decoder.isInstance(definedDecoder)) {
                return;
            }
        }
        
        try {
            m_decoders.add((MessageDecoder)decoder.newInstance());
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
    }

    @Override
    public <T extends MessageDecoder> void unregisterMessageDecoder(Class<T> decoder) {
        //! NOTE: will be implemented when it be needed.
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public boolean connect(InetAddress address) {
        synchronized (m_sync) {
            if (m_isConnected) {
                return false;
            }
            
            m_address = address;
            
            Bootstrap cmdBootstrap = new Bootstrap();
            cmdBootstrap.group(m_eventLoop)
                        .channel(NioDatagramChannel.class)
                        .handler(new ChannelInitializer<DatagramChannel>() {
                            @Override
                            protected void initChannel(DatagramChannel channel) throws Exception {
                                channel.pipeline().addLast("encoder_base", new ByteArrayEncoder());
                                channel.pipeline().addLast("encoder_cmd", new CommandChannelEncoder());
                                channel.pipeline().addLast("watchdog", new CommandChannelWatchdog());
                            }
                        });
            ChannelFuture cmdFuture = cmdBootstrap.connect(m_address, COMMANDS_PORT);
            m_cmdChannel = cmdFuture.channel();
            
            Bootstrap msgBootstrap = new Bootstrap();
            msgBootstrap.group(m_eventLoop)
                        .channel(NioDatagramChannel.class)
                        .handler(new ChannelInitializer<DatagramChannel>() {
                            @Override
                            protected void initChannel(DatagramChannel channel) throws Exception {
                                Channel channelType = Channel.MessagesStream;
                                int channelId = channelType.getValue();
                                
                                List<MessageDecoder> decoders = m_decoders;
                                ListenersList<StateListener> stateListeners = m_stateListeners.get(channelId);
                                ListenersList<MessageListener> messageListeners = m_messageListeners.get(channelId);
                                
                                channel.pipeline().addLast("encoder", new ByteArrayEncoder());
                                channel.pipeline().addLast("watcher", new ChannelStateWatcher());
                                channel.pipeline().addLast("decoder", new MessagesChannelDecoder(decoders));
                                channel.pipeline().addLast("notifier", new ChannelListenersNotifier(
                                        channelType, stateListeners, messageListeners));
                                
                                ChannelState connectingState = ChannelState.Connecting;
                                channel.pipeline().fireUserEventTriggered(connectingState);
                            }
                        });
            ChannelFuture msgFuture = msgBootstrap.connect(m_address, MESSAGES_PORT);
            m_msgChannel = msgFuture.channel();
            
            Bootstrap videoBootstrap = new Bootstrap();
            videoBootstrap.group(m_eventLoop)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel channel) throws Exception {
                                Channel channelType = Channel.VideoStream;
                                int channelId = channelType.getValue();
                                
                                ListenersList<StateListener> stateListeners = m_stateListeners.get(channelId);
                                ListenersList<MessageListener> messageListeners = m_messageListeners.get(channelId);
                                
                                channel.pipeline().addLast("encoder", new ByteArrayEncoder());
                                channel.pipeline().addLast("watcher", new ChannelStateWatcher());
                                channel.pipeline().addLast("decoder", new VideoChannelDecoder());
                                channel.pipeline().addLast("notifier", new ChannelListenersNotifier(
                                        channelType, stateListeners, messageListeners));
                                
                                ChannelState connectingState = ChannelState.Connecting;
                                channel.pipeline().fireUserEventTriggered(connectingState);
                            }
                        });
            ChannelFuture videoFuture = videoBootstrap.connect(m_address, VIDEO_PORT);
            m_videoChannel = videoFuture.channel();
            
            m_isConnected = true;
        }
        
        return true;
    }

    @Override
    public void disconnect() {
        synchronized (m_sync) {
            if (!m_isConnected) {
                return;
            }
            
            m_cmdChannel.close();
            m_msgChannel.close();
            m_videoChannel.close();
            
            ChannelState closedState = ChannelState.Disconnected;
            m_msgChannel.pipeline().fireUserEventTriggered(closedState);
            m_videoChannel.pipeline().fireUserEventTriggered(closedState);
            
            m_isConnected = false;
        }
    }

    @Override
    public void send(Command command) {
        m_cmdChannel.writeAndFlush(command);
    }

}
