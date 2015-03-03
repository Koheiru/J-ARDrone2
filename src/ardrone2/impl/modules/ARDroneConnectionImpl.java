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

package ardrone2.impl.modules;

import ardrone2.ARDroneConnection;
import ardrone2.Command;
import ardrone2.ConnectionState;
import ardrone2.impl.ARDroneEngine;
import ardrone2.impl.ARDroneEngine.Channel;
import ardrone2.impl.ARDroneEngine.ChannelState;
import ardrone2.impl.ARDroneModule;
import ardrone2.impl.ListenersList;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Class ARDroneConnectionImpl
 * @author Prostov Yury
 */
public class ARDroneConnectionImpl extends ARDroneModule implements ARDroneConnection {

    private ListenersList m_listeners = null;
    
    private ConnectionState m_state = ConnectionState.Disconnected;
    private final Object m_sync = new Object();
        
    public ARDroneConnectionImpl() {
        super(Channel.MessagesStream);
        m_listeners = new ListenersList(ARDroneConnection.ConnectionListener.class);
    }
    
    @Override
    public void initialize(ARDroneEngine engine) {
        super.initialize(engine);
        subscribeToStates();
    }
    
    @Override
    public void deinitialize() {
        unsubscribeFromStates();
        super.deinitialize();
    }

    @Override
    public void addConnectionListener(ConnectionListener listener) {
        m_listeners.addListener(listener);
    }

    @Override
    public void removeConnectionListener(ConnectionListener listener) {
        m_listeners.removeListener(listener);
    }

    @Override
    public ConnectionState connectionState() {
        synchronized (m_sync) {
            return m_state;
        }
    }

    @Override
    public boolean connect(String address) throws UnknownHostException {
        return connect(InetAddress.getByName(address));
    }

    @Override
    public boolean connect(InetAddress address) throws UnknownHostException {
        return engine().connect(address);
    }

    @Override
    public void disconnect() {
        engine().disconnect();
    }

    @Override
    public boolean waitForConnected(long msec) throws InterruptedException {
        return waitForState(ConnectionState.Connected, msec);
    }

    @Override
    public boolean waitForDisconnected(long msec) throws InterruptedException {
        return waitForState(ConnectionState.Disconnected, msec);
    }

    @Override
    public void send(Command command) {
        engine().send(command);
    }
    
    @Override
    protected void onStateChanged(ChannelState state) {
        ConnectionState newState = convert(state);
        
        synchronized (m_sync) {
            if (m_state == newState) {
                return;
            }
            m_state = newState;
            m_sync.notifyAll();
        }
        
        ConnectionListener[] listeners = (ConnectionListener[])m_listeners.listeners();
        for (ConnectionListener listener: listeners) {
            listener.onConnectionStateChanged(newState);
        }
    }
    
    private boolean waitForState(ConnectionState state, long timeout) throws InterruptedException {
        long elapsedTime = 0;
        synchronized (m_sync) {
            while (true) {
                if (m_state == state) {
                    return true;
                }
                if (timeout == 0) {
                    m_sync.wait();
                }
                else {
                    if (elapsedTime >= timeout) {
                        return false;
                    }
                    long beginTimestamp = System.currentTimeMillis();
                    m_sync.wait(timeout - elapsedTime);
                    elapsedTime = System.currentTimeMillis() - beginTimestamp;
                }
            }
        }
    }
    
    private static ConnectionState convert(ARDroneEngine.ChannelState channelState) {
        if (channelState == ARDroneEngine.ChannelState.Disconnected) {
            return ConnectionState.Disconnected;
        }
        else if (channelState == ARDroneEngine.ChannelState.Connecting) {
            return ConnectionState.Connecting;
        }
        else if (channelState == ARDroneEngine.ChannelState.Connected) {
            return ConnectionState.Connected;
        }
        else {
            return ConnectionState.Disconnecting;
        }
    }
    
}
