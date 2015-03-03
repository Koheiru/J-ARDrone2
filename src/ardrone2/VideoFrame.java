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

/**
 * Class VideoFrame
 * @author Prostov Yury
 */
public class VideoFrame {
    
    private int m_width    = -1;
    private int m_height   = -1;
    private int[][] m_data       = null;
    private int[]   m_dataStride = null;
    
    private int m_streamId  = -1;
    private int m_number    = -1;
    private int m_timestamp = -1;
    
    
    public VideoFrame() {
    }
    
    public VideoFrame(int width, int height, int[][] data, int[] dataStride) {
        m_width      = width;
        m_height     = height;
        m_data       = data;
        m_dataStride = dataStride;
    }
    
    public VideoFrame(int width, int height, int[][] data, int[] dataStride, 
                      int streamId, int number, int timestamp) {
        this(width, height, data, dataStride);
        m_streamId  = streamId;
        m_number    = number;
        m_timestamp = timestamp;
    }
    
    public int streamId() {
        return m_streamId;
    }
    
    public int number() {
        return m_number;
    }
    
    public int timestamp() {
        return m_timestamp;
    }
    
    public int width() {
        return m_width;
    }
    
    public int height() {
        return m_height;
    }
    
    public int[][] data() {
        return m_data;
    }
    
    public int[] dataStride() {
        return m_dataStride;
    }
    
}
