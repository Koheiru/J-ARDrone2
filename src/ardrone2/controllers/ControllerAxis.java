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
package ardrone2.controllers;

/**
 * Class ControllerAxis
 * @author Prostov Yury
 */
public class ControllerAxis {
    public float pitch;
    public float roll;
    public float yaw;
    public float gaz;
    
    public ControllerAxis() {
        this.pitch = 0.0f;
        this.roll = 0.0f;
        this.yaw = 0.0f;
        this.gaz = 0.0f;
    }
    
    public ControllerAxis(float pitch, float roll, float yaw, float gaz) {
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
        this.gaz = gaz;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("pitch = ").append(pitch).append(", ")
               .append("roll = ").append(roll).append(", ")
               .append("yaw = ").append(yaw).append(", ")
               .append("gaz = ").append(gaz).append("");
        return builder.toString();
    }
}
