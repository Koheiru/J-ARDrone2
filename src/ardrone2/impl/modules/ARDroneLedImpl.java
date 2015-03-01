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

package ardrone2.impl.modules;

import ardrone2.ARDroneLed;
import ardrone2.LedAnimation;
import ardrone2.commands.LedCommand;
import ardrone2.impl.ARDroneModule;

/**
 * Class ARDroneLedImpl
 * @author Prostov Yury
 */
public class ARDroneLedImpl extends ARDroneModule implements ARDroneLed {
    
    public ARDroneLedImpl() {
        super();
    }

    @Override
    public void animateLed(int animationId, float frequency, float duration) {
        engine().send(new LedCommand(animationId, frequency, duration));
    }

    @Override
    public void animateLed(LedAnimation animationId, float frequency, float duration) {
        animateLed(animationId.getValue(), frequency, duration);
    }
    
}
