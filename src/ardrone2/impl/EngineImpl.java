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

import ardrone2.api.DroneCommand;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.ArrayList;
import java.util.List;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import ardrone2.api.DroneMessage;
import ardrone2.commands.WatchDogCommand;

/**
 * Class EngineImpl
 * @author Prostov Yury
 */
public class EngineImpl implements Engine {
    
    public class CommandChannelHandler extends IdleStateHandler {
        public static final int IDLE_TIMEOUT      = 2000;
        public static final int WATCHDOG_INTERVAL = 100;
        
        public ScheduledFuture m_watchdogFuture = null;
        
        public CommandChannelHandler() {
            super(0, IDLE_TIMEOUT, 0, TimeUnit.MILLISECONDS);
        }
        
        @Override
        public void channelActive(ChannelHandlerContext context) throws Exception {
            m_watchdogFuture = m_eventLoop.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        m_cmdChannel.writeAndFlush(new WatchDogCommand());
                    }
                }, WATCHDOG_INTERVAL, WATCHDOG_INTERVAL, TimeUnit.MILLISECONDS);
        }
        
        @Override
        public void channelInactive(ChannelHandlerContext context) throws Exception {
            m_watchdogFuture.cancel(false);
        }
    }
    
    public class MessageChannelHandler extends IdleStateHandler {
        public static final int IDLE_TIMEOUT = 2000;
        
        public MessageChannelHandler() {
            super(IDLE_TIMEOUT, 0, 0, TimeUnit.MILLISECONDS);
        }
        
        @Override
        public void channelActive(ChannelHandlerContext context) throws Exception {
            byte[] handshake = {0x01, 0x00, 0x00, 0x00};
            EngineImpl.this.m_msgChannel.writeAndFlush(handshake);
            super.channelActive(context);
        }
        
        @Override
        public final void channelRead(ChannelHandlerContext context, Object object) throws Exception {
            if (EngineImpl.this.m_status == State.Disconnected) {
                EngineImpl.this.m_status = State.Connecting;
            }
            else if (EngineImpl.this.m_status == State.Connecting) {
                EngineImpl.this.m_status = State.Connected;
                EngineImpl.this.updateState(State.Connected);
            }
            super.channelRead(context, object);
        }
    }

    public static class InterceptorsWrapper extends ChannelDuplexHandler {
        private List<Engine.Interceptor> m_interceptors = new ArrayList<>();
        
        public InterceptorsWrapper(List<Engine.Handler> handlers) {
            for (Engine.Handler handler : handlers) {
                if (handler instanceof Engine.Interceptor) {
                    m_interceptors.add((Engine.Interceptor)handler);
                }
            }
        }
        
        @Override
        public final void channelRead(ChannelHandlerContext context, Object object) throws Exception {
            if (object instanceof DroneMessage)
            {
                DroneMessage message = (DroneMessage)object;
                for (Engine.Interceptor interceptor : m_interceptors) {
                    message = interceptor.handleMessage(message);
                }
                object = message;
            }
            context.fireChannelRead(object);
        }

        @Override
        public final void write(ChannelHandlerContext context, Object object, ChannelPromise promise) throws Exception {
            if (object instanceof DroneCommand)
            {
                DroneCommand command = (DroneCommand)object;
                for (Engine.Interceptor interceptor : m_interceptors) {
                    command = interceptor.handleCommand(command);
                }
                object = command;
            }
            context.write(object, promise);
        }
    }
    
    public static class ReceiversWrapper extends ChannelInboundHandlerAdapter {
        private List<Engine.Receiver> m_receivers = new ArrayList<>();
        
        public ReceiversWrapper(List<Engine.Handler> handlers) {
            for (Engine.Handler handler : handlers) {
                if (handler instanceof Engine.Receiver) {
                    m_receivers.add((Engine.Receiver)handler);
                }
            }
        }

        @Override
        public final void channelRead(ChannelHandlerContext context, Object object) throws Exception {
            //System.out.println(object.toString());
            
            if (object instanceof DroneMessage) {
                DroneMessage message = (DroneMessage)object;
                for (Engine.Receiver receiver : m_receivers) {
                    receiver.onMessageReceived(message);
                }
            }
            context.fireChannelRead(object);
        }
    }
    
    private List<Handler> m_handlers = new ArrayList<>();
    
    private EventLoopGroup m_eventLoop = new NioEventLoopGroup();
    private Channel m_cmdChannel = null;
    private Channel m_msgChannel = null;
    
    private InetAddress m_address = null;
    private static final int COMMANDS_PORT = 5556;
    private static final int MESSAGES_PORT = 5554;
    
    private final Object m_sync = new Object();
    private State m_state = State.Disconnected;
    private State m_status = State.Disconnected;
    
    
    @Override
    public void initialize(List<Handler> handlers) {
        m_handlers = handlers;
        for (Handler handler : m_handlers) {
            handler.initialize(this);
        }
    }

    @Override
    public void uninitialize() {
        for (Handler handler : m_handlers) {
            handler.uninitialize();
        }
        m_handlers.clear();
    }

    @Override
    public State state() {
        synchronized (m_sync) {
            return m_state;
        }
    }

    @Override
    public InetAddress address() {
        return m_address;
    }

    @Override
    public boolean connect(InetAddress address) {
        synchronized (m_sync) {
            if (m_state != State.Disconnected) {
                return false;
            }
            
            m_status = State.Disconnected;
            updateState(State.Connecting);
            updateAddress(address);
            
            Bootstrap cmdBootstrap = new Bootstrap();
            cmdBootstrap.group(m_eventLoop)
                        .channel(NioDatagramChannel.class)
                        .handler(new ChannelInitializer<DatagramChannel>() {
                            @Override
                            protected void initChannel(DatagramChannel channel) throws Exception {
                                channel.pipeline().addLast("encoder_base", new ByteArrayEncoder());
                                channel.pipeline().addLast("encoder_cmd", new EngineEncoder());
                                channel.pipeline().addLast("interceptors", new InterceptorsWrapper(m_handlers));
                                channel.pipeline().addLast("engine", new CommandChannelHandler());
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
                                channel.pipeline().addLast("encoder_base", new ByteArrayEncoder());
                                channel.pipeline().addLast("decoder_msg", new EngineDecoder());
                                channel.pipeline().addLast("interceptors", new InterceptorsWrapper(m_handlers));
                                channel.pipeline().addLast("receivers", new ReceiversWrapper(m_handlers));
                                channel.pipeline().addLast("engine", new MessageChannelHandler());
                            }
                        });
            ChannelFuture msgFuture = msgBootstrap.connect(m_address, MESSAGES_PORT);
            m_msgChannel = msgFuture.channel();
        }
        
        return true;
    }

    @Override
    public void disconnect() {
        synchronized (m_sync) {
            if (m_state == State.Disconnected) {
                return;
            }
            
            m_cmdChannel.close();
            m_msgChannel.close();
            updateState(State.Disconnected);
        }
    }

    @Override
    public void send(DroneCommand command) {
        m_cmdChannel.writeAndFlush(command);
    }
    
    private void updateAddress(InetAddress address) {
        m_address = address;
    }
    
    private void updateState(State newState) {
        if (m_state == newState) {
            return;
        }
        
        m_state = newState;
        m_eventLoop.execute(new Runnable() {
            private final State newState = m_state;
            @Override
            public void run() {
                for (Handler handler : m_handlers) {
                    handler.onStateChanged(newState);
                }
            }
        });
    }
}
