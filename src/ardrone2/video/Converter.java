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

package ardrone2.video;

import ardrone2.VideoFrame;
import java.awt.image.BufferedImage;

/**
 * Class Converter
 * @author Prostov Yury
 */
public class Converter {
    
    public static BufferedImage VideoFrameToImage(VideoFrame videoFrame) throws Exception {
        BufferedImage image = new BufferedImage(videoFrame.width(), videoFrame.height(), 
                                                BufferedImage.TYPE_INT_RGB);
        VideoFrameToImage(videoFrame, image);
        return image;
    }
    
    public static void VideoFrameToImage(VideoFrame videoFrame, BufferedImage image) throws Exception {
        //! TODO: search more efficient way to do this.
        
        if (image.getType() != BufferedImage.TYPE_INT_RGB && image.getType() != BufferedImage.TYPE_INT_ARGB) {
            throw new IllegalArgumentException("Invalid image type");
        }
        
        if (image.getWidth() != videoFrame.width() || image.getHeight() != videoFrame.height()) {
            throw new IllegalArgumentException("Invalid image size");
        }
        
        int[] rgb = VideoFrameToRGB(videoFrame);
        image.getRaster().setDataElements(0, 0, videoFrame.width(), videoFrame.height(), rgb);
    }
    
    public static int[] VideoFrameToRGB(VideoFrame videoFrame) throws Exception {
        int[] rgb = new int[videoFrame.width() * videoFrame.height()];
        VideoFrameToRGB(videoFrame, rgb);
        return rgb;
    }
    
    public static void VideoFrameToRGB(VideoFrame videoFrame, int[] rgb) throws Exception {
        if (rgb.length < videoFrame.width() * videoFrame.height()) {
            throw new IllegalArgumentException("Invalid array size (it is too small)");
        }
        
        int[] data_y  = videoFrame.data()[0];
        int[] data_cb = videoFrame.data()[1];
        int[] data_cr = videoFrame.data()[2];
        
        int width = videoFrame.width();
        int height = videoFrame.height();
        
        for (int i = 0; i < height; i++) {
            int offset = i * width;
            
            //! Expected 4:2:0 sampling.
            int offset_y = i * videoFrame.dataStride()[0];
            int offset_cb = (i >> 1) * videoFrame.dataStride()[1];
            int offset_cr = (i >> 1) * videoFrame.dataStride()[2];
            
            for (int j = 0; j < width; j++) {
                int y = data_y[offset_y + j] - 16;
                int cr = data_cr[offset_cr + (j >> 1)] - 128;
                int cb = data_cb[offset_cb + (j >> 1)] - 128;

                int a = 255;
                int r = (298 * y + 409 * cr + 128) >> 8;
                int g = (298 * y - 100 * cb - 208 * cr + 128) >> 8;
                int b = (298 * y + 516 * cb + 128) >> 8;

                rgb[offset + j] = (a << 24) | (round(r) << 16) | (round(g) << 8) | round(b);
            }
        }
    }
    
    private static int round(int value) {
        return value < 0 ? 0 : (value > 255 ? 255 : value);
    }

}
