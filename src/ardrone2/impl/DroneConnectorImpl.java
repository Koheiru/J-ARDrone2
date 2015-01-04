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

import ardrone2.api.DroneConnector;
import ardrone2.api.DroneCommand;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Class DroneConnectorImpl
 * @author Prostov Yury
 */
public class DroneConnectorImpl
    extends DroneModuleExt<DroneConnector.ConnectionListener>
    implements DroneConnector {

    private ConnectionState m_state = ConnectionState.DISCONNECTED;
    private final Object m_sync = new Object();
    
    public DroneConnectorImpl() {
        super(DroneConnector.ConnectionListener.class);
    }
        
    @Override
    public ConnectionState connectionState() {
        synchronized (m_sync) {
            return m_state;
        }
    }

    @Override
    public boolean connect(String address) throws UnknownHostException
    {
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
        return waitForState(ConnectionState.CONNECTED, msec);
    }

    @Override
    public boolean waitForDisconnected(long msec) throws InterruptedException {
        return waitForState(ConnectionState.DISCONNECTED, msec);
    }

    @Override
    public void execute(DroneCommand command) {
        engine().send(command);
    }
    
    @Override
    public void onStateChanged(Engine.State engineState) {
        ConnectionState newState = convert(engineState);
        
        synchronized (m_sync) {
            if (m_state == newState) {
                return;
            }
            m_state = newState;
            m_sync.notifyAll();
        }
        
        Object[] lll = listeners();
        DroneConnector.ConnectionListener[] listeners = (DroneConnector.ConnectionListener[]) lll;
        for (ConnectionListener listener: listeners) {
            listener.onConnectionStateChanged(newState);
        }
    }
    
    private ConnectionState convert(Engine.State engineState) {
        if (engineState == Engine.State.Disconnected) {
            return ConnectionState.DISCONNECTED;
        }
        else if (engineState == Engine.State.Connecting) {
            return ConnectionState.CONNECTING;
        }
        else if (engineState == Engine.State.Connected) {
            return ConnectionState.CONNECTED;
        }
        else {
            return ConnectionState.DISCONNECTING;
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
    
}