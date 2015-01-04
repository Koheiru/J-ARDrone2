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
package ardrone2.math;

/**
 * Class Matrix3x3
 * @author Prostov Yury
 */
public class Matrix3x3 {
    
    public float m11;
    public float m12;
    public float m13;
    public float m21;
    public float m22;
    public float m23;
    public float m31;
    public float m32;
    public float m33;
    
    public Matrix3x3() {
        this(0.0f, 0.0f, 0.0f, 
             0.0f, 0.0f, 0.0f, 
             0.0f, 0.0f, 0.0f);
    }
    
    public Matrix3x3(float m11, float m12, float m13,
                     float m21, float m22, float m23,
                     float m31, float m32, float m33) {
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }
    
}
