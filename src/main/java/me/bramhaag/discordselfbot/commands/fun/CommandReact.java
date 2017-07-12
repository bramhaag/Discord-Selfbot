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

package me.bramhaag.discordselfbot.commands.fun;

import me.bramhaag.bcf.CommandContext;
import me.bramhaag.bcf.annotations.Command;
import me.bramhaag.bcf.annotations.CommandBase;
import me.bramhaag.bcf.annotations.Optional;
import net.dv8tion.jda.core.entities.Message;

import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Command("react")
public class CommandReact {

    private Character[] regionalIndicators;

    public CommandReact() {
        regionalIndicators = IntStream.rangeClosed((int) '\uDDE6', (int) '\uDDFF')
                .mapToObj(i -> (char) i)
                .toArray(Character[]::new);
    }

    @CommandBase
    public void execute(CommandContext context, String text, @Optional String messageId, @Optional String channelId, @Optional String guildId) {
        guildId = guildId == null ? context.getGuild().getId()   : guildId;
        channelId = channelId == null ? context.getChannel().getId() : channelId;
        Message target = messageId == null ?
                context.getChannel().getHistoryAround(context.getMessage(), 2).complete().getRetrievedHistory().get(1) :
                context.getJDA().getGuildById(guildId).getTextChannelById(channelId).getHistoryAround(messageId, 1).complete().getRetrievedHistory().get(0);

        context.getMessage().delete().queue();

        for(Character c : text.toLowerCase().toCharArray()) {
            if(!Pattern.matches("[a-zA-Z]", c.toString())) {
                continue;
            }

            target.addReaction('\uD83C' + regionalIndicators[(int) c - 'a'].toString()).queue();
        }
    }
}
