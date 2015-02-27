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

import ardrone2.video.VideoFrame;
import com.twilight.h264.decoder.AVFrame;
import com.twilight.h264.decoder.AVPacket;
import com.twilight.h264.decoder.H264Decoder;
import com.twilight.h264.decoder.MpegEncContext;
import static com.twilight.h264.player.H264Player.INBUF_SIZE;
import com.twilight.h264.util.FrameUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.awt.image.MemoryImageSource;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class VideoDecoder
 * @author Prostov Yury
 */
public class VideoDecoder extends MessageToMessageDecoder<VideoPacket> {
    
    private class VideoDecoderWorker extends Thread {
        @Override
        public void run() {
            int[] got_frame = new int[1];
            H264Decoder codec = new H264Decoder();
            MpegEncContext context = MpegEncContext.avcodec_alloc_context();
            AVFrame avFrame = AVFrame.avcodec_alloc_frame();
            AVPacket avPacket = new AVPacket();
            avPacket.av_init_packet();
            
            if (codec == null) {
                System.out.println("codec not found\n");
                System.exit(1);
            }
            if (context.avcodec_open(codec) < 0) {
                System.out.println("could not open codec\n");
                System.exit(1);
            }
            
            if ((codec.capabilities & H264Decoder.CODEC_CAP_TRUNCATED) != 0) {
                context.flags |= MpegEncContext.CODEC_FLAG_TRUNCATED; /* we do not send complete frames */
            }
            
            int counter = 0;
            while (true) {
                try {
                    VideoPacket videoPacket = m_packetsQueue.take();
                    if (videoPacket.data == null) {
                        // Means to stop decoding.
                        break;
                    }
                    
                    /*
                    if (counter == 0) {
                        counter = 1;
                        FileOutputStream stream = new FileOutputStream("d:\\Work\\Develop\\h264j\\H264Player\\sample_clips\\test-4.264", true);
                        stream.write(videoPacket.data);
                    }
                    if (true) {
                        continue;
                    }
                    */
                    
                    int INBUF_SIZE = 65535;
                    //int intBufferSize = videoPacket.dataSize + MpegEncContext.FF_INPUT_BUFFER_PADDING_SIZE;
                    int[] intBuffer = new int[INBUF_SIZE];
                    toIntArray(videoPacket.data, intBuffer);
                    
                    avPacket.data_base    = intBuffer;
                    avPacket.data_offset  = 0;
                    avPacket.size         = videoPacket.dataSize;
                    //avPacket.stream_index = videoPacket.streamId;
                    
                    while (avPacket.size > 0) {
                        int len = context.avcodec_decode_video2(avFrame, got_frame, avPacket);
                        if (len < 0) {
                            System.out.println("Error while decoding frame...");
                            // Discard current packet and proceed to next packet
                            break;
                        }
                        
                        if (got_frame[0] != 0) {
                            avFrame = context.priv_data.displayPicture;
                            int frameWidth  = avFrame.imageWidthWOEdge;
                            int frameHeight = avFrame.imageHeightWOEdge;
                            
                            int bufferSize = frameWidth * frameHeight;
                            int[] buffer = new int[bufferSize];
                            FrameUtils.YUV2RGB_WOEdge(avFrame, buffer);
                            
                            VideoPacket lastPacket = m_packetsQueue.peek();
                            if (lastPacket != null && lastPacket.data == null)
                            {
                                // Means to stop decoding.
                                break;
                            }
                            
                            VideoFrame videoFrame = new VideoFrame(frameWidth, frameHeight, buffer);
                            videoFrame.setNumber(videoPacket.frameNumber);
                            videoFrame.setTimestamp(videoPacket.frameTimestamp);
                            m_context.fireChannelRead(videoFrame);
                        }
                        
                        avPacket.size -= len;
                        avPacket.data_offset += len;
                    }
                }
                catch (Exception exception) {
                    System.out.println("Exception while decoding!");
                    //System.out.println(exception.toString());
                    exception.printStackTrace();
                }
            }
        }
        
        private void toIntArray(byte[] src, int[] dst) {
            for (int i = 0; i < src.length; ++i) {
                dst[i] = src[i] & 0xFF;
            }
            //IntBuffer ib = ByteBuffer.wrap(src).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
            //ib.get(dst, 0, ib.remaining());
        }
    }
    
    private VideoDecoderWorker m_worker = new VideoDecoderWorker();
    private BlockingQueue<VideoPacket> m_packetsQueue = new LinkedBlockingQueue();
    private ChannelHandlerContext m_context = null;
    
    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        m_context = context;
        m_packetsQueue.clear();
        m_worker.start();
        super.channelActive(context);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        m_packetsQueue.clear();
        m_packetsQueue.put(new VideoPacket());
        m_worker.join();
        super.channelInactive(context);
    }
    
    @Override
    protected void decode(ChannelHandlerContext context, VideoPacket videoPacket, List<Object> out) throws Exception {
        m_packetsQueue.put(videoPacket);
    }

}
