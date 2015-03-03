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

/**
 * Class UnknownMessage
 * @author Prostov Yury
 */
public class UnknownMessage implements Message {
    
    public int    tag = 0;
    public byte[] data = null;
    
    public UnknownMessage() {
    }
    
    public UnknownMessage(int tag, byte[] data) {
        this.tag = tag;
        this.data = data;
    }
    
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[UnknownMessage]")
              .append(" tag: 0x").append(Integer.toHexString(tag)).append(";")
              .append(" data: [blob] with size ").append(data.length).append(";");
        return buffer.toString();
    }
    
}
