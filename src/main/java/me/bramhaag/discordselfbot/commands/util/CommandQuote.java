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
import me.bramhaag.discordselfbot.Bot;
import me.bramhaag.discordselfbot.util.Constants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.time.ZonedDateTime;

@Command("quote")
public class CommandQuote {

    @CommandBase
    public void execute(CommandContext context, @Optional String messageId, @Optional String channelId, @Optional String guildId) {
        guildId = guildId == null ? context.getGuild().getId() : guildId;
        channelId = channelId == null ? context.getChannel().getId() : channelId;
        Message target = messageId == null ?
                context.getChannel().getHistoryAround(context.getMessage(), 2).complete().getRetrievedHistory().get(1) :
                context.getJda().getGuildById(guildId).getTextChannelById(channelId).getHistoryAround(messageId, 1).complete().getRetrievedHistory().get(0);

        context.getMessage().editMessage(new EmbedBuilder()
                .setColor(Bot.getInstance().getConfig().getPrimaryColor())
                .setDescription(target.getRawContent())
                .setFooter(target.getAuthor().getName(), target.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(ZonedDateTime.now())
                .build()
        ).queue();
    }
}
