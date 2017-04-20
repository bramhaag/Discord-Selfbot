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
import me.bramhaag.discordselfbot.util.Util;
import me.bramhaag.discordselfbot.commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommandPrune {

    @Command(name = "prune", aliases = { "purge" }, minArgs = 1)
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        int amount;

        try {
            amount = Integer.valueOf(args[0]);
        }
        catch (NumberFormatException e) {
            Util.sendError(message, e.toString());
            return;
        }

        MessageHistory history = message.getChannel().getHistory();
        history.retrievePast(amount)
                .queue(messages -> {
                    messages.parallelStream()
                            .filter(m -> m.getAuthor().getId().equalsIgnoreCase(message.getAuthor().getId()))
                            .collect(Collectors.toList()).forEach(m -> m.delete().queue());

                    channel.sendMessage(new EmbedBuilder()
                           .setTitle("Prune", null)
                           .setDescription("Prune completed! Looked through " + amount + " message(s)")
                           .setFooter("Prune | " + Util.generateTimestamp(), null)
                           .build())
                           .queue(m -> m.delete().queueAfter(Constants.REMOVE_TIME_LONG, TimeUnit.SECONDS));
                });
    }
}
