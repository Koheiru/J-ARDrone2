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

package ardrone2;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Interface ARDroneConnection
 * @author Prostov Yury
 */
public interface ARDroneConnection {
    
    public static interface ConnectionListener {
        public void onConnectionStateChanged(ConnectionState state);
    }
    
    public void addConnectionListener(ConnectionListener listener);
    
    public void removeConnectionListener(ConnectionListener listener);
    
    
    public ConnectionState connectionState();
    
    public boolean connect(String address) throws UnknownHostException;
    
    public boolean connect(InetAddress address) throws UnknownHostException;
    
    public void disconnect();
    
    public boolean waitForConnected(long msec) throws InterruptedException;
    
    public boolean waitForDisconnected(long msec) throws InterruptedException;
    
    public void send(Command command);
    
}
