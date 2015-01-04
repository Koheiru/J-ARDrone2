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

import ardrone2.api.DroneLed;
import ardrone2.commands.LedCommand;

/**
 * Class DroneLedImpl
 * @author Prostov Yury
 */
public class DroneLedImpl 
    extends DroneModule 
    implements DroneLed {
    
    public DroneLedImpl() {
    }
    
    @Override
    public void animateLed(LedAnimation ledAnimation, float animationFrequency, int animationDuration) {
        engine().send(new LedCommand(ledAnimation.getValue(), animationFrequency, animationDuration));
    }

}