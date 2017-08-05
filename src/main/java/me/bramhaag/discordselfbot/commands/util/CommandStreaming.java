/*
 * Copyright 2017 Bram Hagens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.bramhaag.discordselfbot.commands.util;

import me.bramhaag.bcf.CommandContext;
import me.bramhaag.bcf.annotations.Command;
import me.bramhaag.bcf.annotations.CommandBase;
import me.bramhaag.bcf.annotations.Optional;
import net.dv8tion.jda.core.entities.Game;

@Command("streaming")
public class CommandStreaming {

    @CommandBase
    public void execute(CommandContext context, String twitchUrl, @Optional String[] text) {
        if(twitchUrl.equals("reset") && (text == null || text.length == 0)) {
            context.getJda().getPresence().setGame(Game.of(" "));
            return;
        }

        context.getJda().getPresence().setGame(Game.of(String.join(" ", text), twitchUrl));
    }
}