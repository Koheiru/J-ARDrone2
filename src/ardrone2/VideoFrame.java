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

import java.awt.image.BufferedImage;

/**
 * Class VideoFrame
 * @author Prostov Yury
 */
public class VideoFrame extends BufferedImage {

    private int m_frameNumber = -1;
    private int m_timestamp = -1;
    
    public VideoFrame(int width, int height) {
        super(width, height, BufferedImage.TYPE_INT_RGB);
    }
    
    public VideoFrame(int width, int height, int[] data) {
        super(width, height, BufferedImage.TYPE_INT_RGB);
        getRaster().setDataElements(0, 0, width, height, data);
    }
    
    public void setFrameNumber(int frameNumber) {
        m_frameNumber = frameNumber;
    }
    
    public int frameNumber() {
        return m_frameNumber;
    }
    
    public void setTimestamp(int timestamp) {
        m_timestamp = timestamp;
    }
    
    public int timestamp() {
        return m_timestamp;
    }
    
}
