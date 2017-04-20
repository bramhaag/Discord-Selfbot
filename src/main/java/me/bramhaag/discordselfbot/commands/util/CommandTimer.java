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

import lombok.NonNull;
import me.bramhaag.discordselfbot.util.Util;
import me.bramhaag.discordselfbot.commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class CommandTimer {

    @Command(name = "timer", minArgs = 1)
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        long delay;

        try {
            delay = Long.valueOf(args[0]);
        }
        catch (NumberFormatException e) {
            Util.sendError(message, e.toString());
            return;
        }

        message.editMessage(new EmbedBuilder()
                .setTitle("Timer", null)
                .setDescription("Timer ending in " + DurationFormatUtils.formatDuration(delay * 1000, "H:mm:ss", true))
                .setFooter("Timer | " + Util.generateTimestamp(), null)
                .setColor(Color.GREEN)
                .build())
        .queue();

        channel.sendMessage(new EmbedBuilder()
                .setTitle("Timer", null)
                .setDescription("Timer expired!")
                .setFooter("Timer | " + Util.generateTimestamp(), null)
                .setColor(Color.GREEN).build())
        .queueAfter(delay, TimeUnit.SECONDS);
    }
}
