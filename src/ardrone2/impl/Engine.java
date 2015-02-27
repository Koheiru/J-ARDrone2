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

import java.net.InetAddress;
import java.util.List;
import ardrone2.DroneCommand;
import ardrone2.DroneMessage;
import ardrone2.video.VideoFrame;

/**
 * Class Engine
 * @author Prostov Yury
 */
public interface Engine {
    
    public static enum State {
        Disconnected,
        Connecting,
        Connected,
        Disconnecting,
    }
    
    public static interface Handler {
        public void initialize(Engine engine);
        public void uninitialize();
        public void onStateChanged(State state);
    }
    
    public static interface Interceptor extends Handler {
        public DroneCommand handleCommand(DroneCommand command);
        public DroneMessage handleMessage(DroneMessage message);
    }
    
    public static interface MessageReceiver extends Handler {
        public void onMessageReceived(DroneMessage message);
    }
    
    public static interface VideoReceiver extends Handler {
        public void onVideoReceived(VideoFrame videoFrame);
    }
    
    public void initialize(List<Handler> handlers);
    
    public void uninitialize();
    
    public State state();
    
    public InetAddress address();
    
    public boolean connect(InetAddress address);
    
    public void disconnect();
    
    public void send(DroneCommand command);
    
}
