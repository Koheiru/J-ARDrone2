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
import ardrone2.messages.VideoPacket;
import com.twilight.h264.decoder.AVFrame;
import com.twilight.h264.decoder.AVPacket;
import com.twilight.h264.decoder.H264Decoder;
import com.twilight.h264.decoder.MpegEncContext;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class ARDroneVideoImpl
 * @author Prostov Yury
 */
public class ARDroneVideoImpl extends ARDroneModule implements ARDroneVideo {

    //! NOTE: may be better to pass compressed frames to listeners and implement external 
    // video decoder, but at this moment such flexibility is not required.
    private class DecodingThread extends Thread {
        @Override
        public void run() {
            int[] got_frame = new int[1];
            H264Decoder codec = new H264Decoder();
            MpegEncContext context = MpegEncContext.avcodec_alloc_context();
            AVFrame avFrame = AVFrame.avcodec_alloc_frame();
            AVPacket avPacket = new AVPacket();
            avPacket.av_init_packet();

            if (context.avcodec_open(codec) < 0) {
                System.err.println("Could not open avcodec!");
                return;
            }

            if ((codec.capabilities & H264Decoder.CODEC_CAP_TRUNCATED) != 0) {
                // We do not send complete frames.
                context.flags |= MpegEncContext.CODEC_FLAG_TRUNCATED;
            }

            int INBUF_SIZE = 65535;
            //int intBufferSize = videoPacket.dataSize + MpegEncContext.FF_INPUT_BUFFER_PADDING_SIZE;
            int[] intBuffer = new int[INBUF_SIZE];

            while (true) {
                try {
                    VideoPacket videoPacket = getVideoPacket();
                    if (videoPacket == null) {
                        // Means to stop decoding.
                        break;
                    }

                    toIntArray(videoPacket.data, intBuffer);
                    avPacket.data_base = intBuffer;
                    avPacket.data_offset = 0;
                    avPacket.size = videoPacket.dataSize;
                    //avPacket.stream_index = videoPacket.streamId;

                    while (avPacket.size > 0) {
                        int len = context.avcodec_decode_video2(avFrame, got_frame, avPacket);
                        if (len < 0) {
                            // Discard current packet and proceed to next packet.
                            System.err.println("Error while decoding frame...");
                            break;
                        }

                        if (got_frame[0] != 0) {
                            avFrame = context.priv_data.displayPicture;
                            
                            //! TODO: reduce height to remove artefacts => check is it
                            // decoder bug or something else.
                            int width = avFrame.imageWidthWOEdge;
                            int height = avFrame.imageHeightWOEdge - 36;
                            
                            //! Note: expected 4:2:0 sampling.
                            int[] stride = { avFrame.linesize[0],
                                             avFrame.linesize[1],
                                             avFrame.linesize[2] };
                            int[][] data = { new int[height * stride[0]], 
                                             new int[(height >> 1) * stride[1]],
                                             new int[(height >> 1) * stride[2]] };
                            
                            for (int k = 0; k < 3; ++k) {
                                System.arraycopy(avFrame.data_base[k], avFrame.data_offset[k], 
                                                 data[k], 0, data[k].length);
                            }
                            
                            VideoFrame videoFrame = new VideoFrame(
                                    width, height, data, stride,
                                    videoPacket.streamId, 
                                    videoPacket.frameNumber, 
                                    videoPacket.frameTimestamp);
                            notifyListeners(videoFrame);
                        }

                        avPacket.size -= len;
                        avPacket.data_offset += len;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace(System.out);
                }
            }
        }
        
        private VideoPacket getVideoPacket() {
            synchronized (m_decodingQueueGuard) {
                while (true) {
                    if (m_decodingQueue.isEmpty()) {
                        try {
                            m_decodingQueueGuard.wait();
                        } catch (InterruptedException exception) {
                            exception.printStackTrace(System.err);
                        }
                    }
                    
                    if (!m_decodingQueue.isEmpty()) {
                        return m_decodingQueue.poll();
                    }
                }   
            }
        }
        
        private void notifyListeners(VideoFrame videoFrame) {
            synchronized (m_videoFrameGuard) {
                m_videoFrame = videoFrame;
            }
            
            //! TODO: think about using engine execute method to call listeners
            // from another (not-decoding) thread.
            VideoListener[] listeners = (VideoListener[])m_listeners.listeners();
            for (VideoListener listener: listeners) {
                listener.onVideoFrameDecoded(videoFrame);
            }
        }

        private void toIntArray(byte[] src, int[] dst) {
            for (int i = 0; i < src.length; ++i) {
                dst[i] = src[i] & 0xFF;
            }
        }
    }
    
    private ListenersList m_listeners = null;
    
    private VideoFrame m_videoFrame = null;
    private final Object m_videoFrameGuard = new Object();
    
    private DecodingThread m_decodingThread = new DecodingThread();
    private Queue<VideoPacket> m_decodingQueue = new LinkedList<>();
    private final Object m_decodingQueueGuard = new Object();
    
    
    public ARDroneVideoImpl() {
        super(Channel.VideoStream);
        m_listeners = new ListenersList(ARDroneVideo.VideoListener.class);
    }
    
    @Override
    public void initialize(ARDroneEngine engine) {
        super.initialize(engine);
        subscribeToStates();
        subscribeToMessages();
        startDecodingThread();
    }
    
    @Override
    public void deinitialize() {
        stopDecodingThread();
        unsubscribeFromMessages();
        unsubscribeFromStates();
        super.deinitialize();
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
        boolean isValidMessage = (message instanceof VideoPacket);
        if (!isValidMessage) {
            return;
        }
        
        VideoPacket videoPacket = (VideoPacket)message;
        addVideoPacket(videoPacket);
    }
    
    private void startDecodingThread() {
        m_decodingThread.start();
    }
    
    private void stopDecodingThread() {
        synchronized (m_decodingQueueGuard) {
            m_decodingQueue.clear();
            m_decodingQueue.add(null);
            m_decodingQueueGuard.notify();
        }
        
        try {
            m_decodingThread.join();
        } catch (InterruptedException exception) {
            exception.printStackTrace(System.out);
        }
    }
    
    private void addVideoPacket(VideoPacket videoPacket) {
        //! TODO: implement thinning.
        synchronized (m_decodingQueueGuard) {
            m_decodingQueue.add(videoPacket);
            m_decodingQueueGuard.notify();
        }
    }
    
}
