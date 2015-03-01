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

import ardrone2.ARDroneVideo;
import ardrone2.Message;
import ardrone2.VideoFrame;
import ardrone2.impl.ARDroneEngine;
import ardrone2.impl.ARDroneEngine.Channel;
import ardrone2.impl.ARDroneEngine.ChannelState;
import ardrone2.impl.ARDroneModule;
import ardrone2.impl.ListenersList;

/**
 * Class ARDroneVideoImpl
 * @author Prostov Yury
 */
public class ARDroneVideoImpl extends ARDroneModule implements ARDroneVideo {

    private ListenersList m_listeners = null;
    
    private VideoFrame m_videoFrame = null;
    
    public ARDroneVideoImpl() {
        super(Channel.VideoStream);
        m_listeners = new ListenersList(ARDroneVideo.VideoListener.class);
    }
    
    @Override
    public void initialize(ARDroneEngine engine) {
        super.initialize(engine);
        subscribeToStates();
        subscribeToMessages();
    }
    
    @Override
    public void deinitialize() {
        super.deinitialize();
        subscribeToStates();
        subscribeToMessages();
    }

    @Override
    public void addVideoListener(VideoListener listener) {
        m_listeners.addListener(listener);
    }

    @Override
    public void removeVideoListener(VideoListener listener) {
        m_listeners.removeListener(listener);
    }

    @Override
    public VideoFrame videoFrame() {
        return m_videoFrame;
    }
    
    @Override
    protected void onStateChanged(ChannelState state) {
    }
    
    @Override
    protected void onMessageReceived(Message message) {
    }
    
}
