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

import me.bramhaag.bcf.CommandContext;
import me.bramhaag.bcf.annotations.Command;
import me.bramhaag.bcf.annotations.CommandBase;
import me.bramhaag.discordselfbot.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.jetbrains.annotations.NotNull;

@Command("prune")
public class CommandPrune {

    @CommandBase
    public void execute(@NotNull CommandContext context, int amount) {
        context.getChannel().getHistory().retrievePast(amount + 1).queue(history -> {
            Message[] messages = history.parallelStream().filter(m -> m.getAuthor().getId().equals(context.getAuthor().getId())).toArray(Message[]::new);
            for (int i = 0; i < messages.length; i++) {
                if (i + 1 == messages.length) {
                    messages[i].delete().queue(ignored -> context.getChannel().sendMessage(
                            EmbedUtil.addDefaults(new EmbedBuilder()
                                    .setTitle("Prune")
                                    .setDescription("Prune completed! Deleted " + (messages.length - 1) + " messages"), "Prune", true).build()).queue());

                    return;
                }

                messages[i].delete().queue();
            }
        });
    }
}
