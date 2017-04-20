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

package me.bramhaag.discordselfbot.commands.admin;

import lombok.NonNull;
import me.bramhaag.discordselfbot.Constants;
import me.bramhaag.discordselfbot.Main;
import me.bramhaag.discordselfbot.commands.Command;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class CommandReload {

    @Command(name = "reload")
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        Main.bot.getCommandHandler().unregister();
        Main.bot.registerCommands();

        channel.sendMessage(new MessageBuilder().appendCodeBlock("Reloaded!", "").build()).queue(m -> m.delete().queueAfter(Constants.REMOVE_TIME_SHORT, TimeUnit.SECONDS));
    }
}
