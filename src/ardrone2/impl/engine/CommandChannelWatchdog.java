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

package ardrone2.impl.engine;

import ardrone2.commands.WatchdogCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

/**
 * Class CommandChannelWatchdog
 * @author Prostov Yury
 */
public class CommandChannelWatchdog extends IdleStateHandler {
    public static final int WRITE_IDLE_TIMEOUT = 100;

    public CommandChannelWatchdog() {
        super(0, WRITE_IDLE_TIMEOUT, 0, TimeUnit.MILLISECONDS);
    }
    
    @Override
    protected void channelIdle(ChannelHandlerContext context, IdleStateEvent event) throws Exception {
        context.writeAndFlush(new WatchdogCommand());
        //! Drop out parent method.
        //super.channelIdle(context, event);
    }
}
