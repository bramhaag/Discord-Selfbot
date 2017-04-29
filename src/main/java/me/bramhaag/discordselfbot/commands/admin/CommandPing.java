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

import com.google.common.base.Stopwatch;
import lombok.NonNull;
import me.bramhaag.discordselfbot.Constants;
import me.bramhaag.discordselfbot.commands.Command;
import me.bramhaag.discordselfbot.util.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CommandPing {

    @Command(name = "ping")
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        message.editMessage("`Waiting...`").queue(m -> {
            stopwatch.stop();
            m.editMessage(
                    new EmbedBuilder().setTitle(Constants.PONG_EMOTE + " Pong!", null)
                            .addField("Response time (Bot)", stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms", true)
                            .addField("Response time (API)", message.getJDA().getPing() + " ms", true)
                            .setFooter("Ping | " + Util.generateTimestamp(), null)
                            .build())
                    .queue(embed -> embed.delete().queueAfter(Constants.REMOVE_TIME_LONG, TimeUnit.SECONDS));
        });
    }
}
