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
package ardrone2.messages;

import ardrone2.Message;
import ardrone2.math.Point;
import ardrone2.math.Dimension;
import ardrone2.math.Vector3D;
import ardrone2.math.Matrix3x3;

/**
 * Class VisionDetectMessage
 * @author Prostov Yury
 */
public class VisionDetectMessage implements Message {
    
    public int         detectedCount = 0;
    public int[]       cameraSources = null;
    public int[]       objectTypes = null;
    public int[]       objectDistances = null;
    public Point[]     objectPositions = null;
    public Dimension[] objectDimensions = null;
    public float[]     objectOrientations = null;
    public Vector3D[]  objectTranslations = null;
    public Matrix3x3[] objectRotations = null;
    
    public VisionDetectMessage() {
    }
    
    public VisionDetectMessage(int detectedCount, int[] objectTypes, int[] cameraSources,
                               int[] objectDistances, Point[] objectPositions, 
                               Dimension[] objectDimensions, float[] objectOrientations, 
                               Vector3D[] objectTranslations, Matrix3x3[] objectRotations) {
        this.detectedCount      = detectedCount;
        this.objectTypes        = objectTypes;
        this.cameraSources      = cameraSources;
        this.objectDistances    = objectDistances;
        this.objectPositions    = objectPositions;
        this.objectDimensions   = objectDimensions;
        this.objectOrientations = objectOrientations;
        this.objectTranslations = objectTranslations;
        this.objectRotations    = objectRotations;
    }
        
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[VisionDetectMessage]")
              .append(" detectedCount: ").append(detectedCount).append(";");
        
        for (int i = 0; i < detectedCount; ++i) {
            buffer.append(" [index: ").append(i).append(";")
                  .append(" camera: ").append(cameraSources[i]).append(";")
                  .append(" type: ").append(objectTypes[i]).append(";")
                  .append(" distance: ").append(objectDistances[i]).append(";")
                  .append(" position: ").append(objectPositions[i]).append(";")
                  .append(" dimension: ").append(objectDimensions[i]).append(";")
                  .append(" orientation: ").append(objectOrientations[i]).append(";")
                  .append(" translation: ").append(objectTranslations[i]).append(";")
                  .append(" rotation: ").append(objectRotations[i]).append("];");
        }
        
        return buffer.toString();
    }
    
}
