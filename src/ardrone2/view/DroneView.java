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

package ardrone2.view;

import ardrone2.ARDrone2;
import ardrone2.DroneVision;
import ardrone2.video.VideoFrame;
import ardrone2.view.hud.ControlDrawer;
import ardrone2.view.hud.StateDrawer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JPanel;

/**
 * Class DroneView
 * @author Prostov Yury
 */
public class DroneView extends JPanel {
    
    private class DroneListener implements DroneVision.VisionListener {
        @Override
        public void onVideoFrameReceived(VideoFrame videoFrame) {
            DroneView.this.m_videoFrame = videoFrame;
            DroneView.this.revalidate();
            DroneView.this.repaint();
        }
    }
    
    private ARDrone2 m_drone = null;
    private DroneListener m_listener = new DroneListener();
    private Image m_canvas = null;
    private VideoFrame m_videoFrame = null;
    
    public DroneView() {
    }
    
    public DroneView(ARDrone2 drone) {
        attach(drone);
    }
    
    final public void attach(ARDrone2 drone) {
        detach();
        if (drone != null) {
            m_drone = drone;
            m_drone.addListener(m_listener);
        }
    }
    
    final public void detach() {
        if (m_drone == null) {
            return;
        }
        m_drone.removeListener(m_listener);
        m_drone = null;
    }
    
    final public ARDrone2 drone() {
        return m_drone;
    }
    
    @Override
    public void paint(Graphics graphics) {
        int canvasWidth = getWidth();
        int canvasHeight = getHeight();
        
        if (m_canvas == null || 
            m_canvas.getWidth(this) != canvasWidth ||
            m_canvas.getHeight(this) != canvasHeight)
        {
            m_canvas = createImage(canvasWidth, canvasHeight);
        }
        
        Graphics2D painter = (Graphics2D) m_canvas.getGraphics();
        Color backgroundColor = new Color(26, 53, 71);
        painter.setColor(backgroundColor);
        painter.fillRect(0, 0, canvasWidth, canvasHeight);
        
        drawContent(painter, new Dimension(canvasWidth, canvasHeight));
        graphics.drawImage(m_canvas, 0, 0, canvasWidth, canvasHeight, this);
    }
    
    private void drawContent(Graphics2D painter, Dimension bounds) {
        ARDrone2 drone = m_drone;
        if (drone == null || m_videoFrame == null) {
            return;
        }

        Dimension frameSize = new Dimension(m_videoFrame.getWidth(), m_videoFrame.getHeight());
        Rectangle frameBounds = getScaledPosition(frameSize, bounds);
        Rectangle canvasBounds = new Rectangle(0, 0, bounds.width, bounds.height);
        
        //! Draw video.
        drawFrame(painter, frameBounds, m_videoFrame);
        drawTimestamp(painter, frameBounds, m_videoFrame);
        
        //! Draw hud.
        ControlDrawer.draw(painter, canvasBounds, drone);
        StateDrawer.draw(painter, canvasBounds, drone);
    }

    private static void drawFrame(Graphics2D painter, Rectangle bounds, VideoFrame videoFrame) {
        Image scaledFrame = videoFrame.getScaledInstance(bounds.width, bounds.height, Image.SCALE_SMOOTH);
        painter.drawImage(scaledFrame, bounds.x, bounds.y, bounds.width, bounds.height, null);
    }

    private static void drawTimestamp(Graphics2D painter, Rectangle bounds, VideoFrame videoFrame) {
        Date date = new Timestamp(videoFrame.timestamp() - 10800000);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        String text = formatter.format(date);
        
        Font font = new Font("default", Font.PLAIN, 20);
        painter.setFont(font);
        
        FontMetrics fontMetrics = painter.getFontMetrics(font);
        int width = (int)fontMetrics.getStringBounds(text, painter).getWidth();
        int x = bounds.x + bounds.width - width - 8;
        int y = bounds.y + bounds.height - 8;
        drawText(painter, x, y, text);
    }
    
    private static void drawText(Graphics2D painter, int x, int y, String text) {
        painter.setColor(Color.white);
        painter.drawString(text, x - 1, y - 1);
        painter.drawString(text, x - 1, y + 1);
        painter.drawString(text, x + 1, y - 1);
        painter.drawString(text, x + 1, y + 1);
        painter.setColor(Color.black);
        painter.drawString(text, x, y);
    }
    
    private static Rectangle getScaledPosition(Dimension size, Dimension boundary) {
        double k_width = (double)size.width / (double)boundary.width;
        double k_height = (double)size.height / (double)boundary.height;
        double k = Math.max(k_width, k_height);
        
        Rectangle position = new Rectangle(0, 0, (int)(size.width / k), (int)(size.height / k));
        position.x = (int)((boundary.width - position.width) / 2);
        position.y = (int)((boundary.height - position.height) / 2);
        return position;
    }
}
