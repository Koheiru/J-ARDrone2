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

import ardrone2.math.Dimension;
import ardrone2.math.Point;
import ardrone2.math.Vector3D;

/**
 * Class Conversion
 * @author Prostov Yury
 */
public class Conversion {
    
    public static Point[] toPointArray(int size, int[] xList, int[] yList) {
        Point[] values = new Point[size];
        for (int i = 0; i < size; ++i) {
            values[i] = new Point(xList[i], yList[i]);
        }
        return values;
    }
    
    public static Dimension[] toDimensionArray(int size, int[] widthList, int[] heightList) {
        Dimension[] values = new Dimension[size];
        for (int i = 0; i < size; ++i) {
            values[i] = new Dimension(widthList[i], heightList[i]);
        }
        return values;
    }
    
    public static Vector3D[] toVEctor3DArray(int size, float[] xList, float[] yList, float[] zList) {
        Vector3D[] values = new Vector3D[size];
        for (int i = 0; i < size; ++i) {
            values[i] = new Vector3D(xList[i], yList[i], zList[i]);
        }
        return values;
    }
    
}
