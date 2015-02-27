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

package ardrone2.view.hud;

import ardrone2.ARDrone2;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/**
 * Class ControlDrawer
 * @author Prostov Yury
 */
public class ControlDrawer {

    public static void draw(Graphics2D painter, Rectangle bounds, ARDrone2 drone) {
        drawHorizont(painter, bounds, drone);
    }
    
    public static void drawHorizont(Graphics2D graphics, Rectangle bounds, ARDrone2 drone) {
        Graphics2D painter = (Graphics2D)graphics.create();
        
        painter.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        painter.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        painter.setColor(Color.green);
        
        double centerX = bounds.getCenterX();
        double centerY = bounds.getCenterY();
        painter.translate(centerX, centerY);
        
        double roll = drone.rollAngle() / 180.0 * Math.PI;
        painter.rotate(roll);
        
        int dawSize = 10;
        painter.drawLine(-dawSize, 0, 0, dawSize);
        painter.drawLine(dawSize, 0, 0, dawSize);
                
        int horizontSize = (bounds.width / 3) / 2;
        painter.drawLine(-horizontSize, 0, -dawSize, 0);
        painter.drawLine(horizontSize, 0, dawSize, 0);
    }
    
}
