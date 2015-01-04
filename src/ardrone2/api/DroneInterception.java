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
package ardrone2.api;

import java.util.List;

/**
 * Interface DroneInterception
 * @author Prostov Yury
 */
public interface DroneInterception {
    
    public static interface Interceptor {
        public void handleCommand(DroneCommand input, List<DroneCommand> output);
        public void handleMessage(DroneMessage input, List<DroneMessage> output);
    }
    
    public void addInterceptor(Interceptor interceptor);
    
    public void removeInterceptor(Interceptor interceptor);
    
}
