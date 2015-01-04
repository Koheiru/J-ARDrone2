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

import ardrone2.api.DroneMessage;
import ardrone2.math.Point;
import ardrone2.math.Dimension;
import ardrone2.math.Vector3D;
import ardrone2.math.Matrix3x3;

/**
 * Class VisionDetectMessage
 * @author Prostov Yury
 */
public class VisionDetectMessage implements DroneMessage {
    
    public static final short TAG = (short)0x0010;
    
    private int         m_detectedCount = 0;
    private int[]       m_camerasList = null;
    private int[]       m_typesList = null;
    private int[]       m_distancesList = null;
    private Point[]     m_positionsList = null;
    private Dimension[] m_dimensionsList = null;
    private float[]     m_orientationsList = null;
    private Vector3D[]  m_translationsList = null;
    private Matrix3x3[] m_rotationsList = null;
    
    public VisionDetectMessage(int detectedCount, int[] objectTypes, int[] cameraSources,
            int[] objectDistances, Point[] objectPositions, Dimension[] objectDimensions,
            float[] objectOrientations, Vector3D[] objectTranslations, Matrix3x3[] objectRotations) {
        m_detectedCount = detectedCount;
        m_typesList = objectTypes;
        m_camerasList = cameraSources;
        m_distancesList = objectDistances;
        m_positionsList = objectPositions;
        m_dimensionsList = objectDimensions;
        m_orientationsList = objectOrientations;
        m_translationsList = objectTranslations;
        m_rotationsList = objectRotations;
    }
    
    public int objectsCount() {
        return m_detectedCount;
    }
    
    public int objectType(int index) {
        return m_typesList[index];
    }
    
    public int objectCameraSource(int index) {
        return m_camerasList[index];
    }
    
    public int objectDistance(int index) {
        return m_distancesList[index];
    }
    
    public Point objectPosition(int index) {
        return m_positionsList[index];
    }
    
    public Dimension objectDimension(int index) {
        return m_dimensionsList[index];
    }
    
    public float objectOrientation(int index) {
        return m_orientationsList[index];
    }
    
    public Vector3D objectTranslation(int index) {
        return m_translationsList[index];
    }
    
    public Matrix3x3 objectRotation(int index) {
        return m_rotationsList[index];
    }
    
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[VisionDetectMessage]")
              .append(" objectsCount: ").append(objectsCount()).append(";");
        
        for (int i = 0; i < m_detectedCount; ++i) {
            buffer.append(" [index: ").append(i).append(";")
                  .append(" camera: ").append(objectCameraSource(i)).append(";")
                  .append(" type: ").append(objectType(i)).append(";")
                  .append(" distance: ").append(objectDistance(i)).append(";")
                  .append(" position: ").append(objectPosition(i)).append(";")
                  .append(" dimension: ").append(objectDimension(i)).append(";")
                  .append(" orientation: ").append(objectOrientation(i)).append(";")
                  .append(" translation: ").append(objectTranslation(i)).append(";")
                  .append(" rotation: ").append(objectRotation(i)).append("]");
        }
        
        return buffer.toString();
    }
    
}
