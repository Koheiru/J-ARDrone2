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

import ardrone2.DroneVision;
import ardrone2.video.VideoFrame;

/**
 * Class DroneVisionImpl
 * @author Prostov Yury
 */
public class DroneVisionImpl 
    extends DroneModuleExt<DroneVision.VisionListener>
    implements DroneVision, Engine.VideoReceiver {

    private VideoFrame m_videoFrame = null;
    
    public DroneVisionImpl() {
        super(DroneVision.VisionListener.class);
    }

    @Override
    public void onVideoReceived(VideoFrame videoFrame) {
        m_videoFrame = videoFrame;
        
        DroneVision.VisionListener[] listeners = listeners();
        for (DroneVision.VisionListener listener : listeners) {
            listener.onVideoFrameReceived(videoFrame);
        }
    }
    
    public VideoFrame videoFrame() {
        return m_videoFrame;
    }
    
}
