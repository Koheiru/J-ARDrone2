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
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * Class StateDrawer
 * @author Prostov Yury
 */
public class StateDrawer {

    public static void draw(Graphics2D painter, Rectangle bounds, ARDrone2 drone) {
        drawBatteryLevel(painter, bounds, drone);
    }

    public static void drawBatteryLevel(Graphics2D graphics, Rectangle bounds, ARDrone2 drone) {
        Graphics2D painter = (Graphics2D)graphics.create();
        
        String text = new StringBuilder()
                .append("Battery: \n")
                .append(drone.batteryLevel())
                .append("%")
                .toString();
        
        FontMetrics fontMetrics = painter.getFontMetrics();
        Rectangle2D textSize = fontMetrics.getStringBounds(text, painter);
        int textWidth = (int)textSize.getWidth();
        int textHeight = (int)textSize.getHeight();
        int x = bounds.x + bounds.width - textWidth - 8;
        int y = bounds.y + textHeight + 8;
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
    
}
