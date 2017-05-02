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
import me.bramhaag.discordselfbot.evaluate.Evaluate;
import me.bramhaag.discordselfbot.util.Util;
import me.bramhaag.discordselfbot.commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.bramhaag.discordselfbot.Constants.color;

public class CommandEvaluate {

    @Command(name = "evaluate", aliases = { "eval", "e" }, minArgs = 1)
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {

        String input;
        Evaluate.Language language;

        try {
            language = Evaluate.Language.valueOf(args[0].toUpperCase());
            input = Util.combineArgs(Arrays.copyOfRange(args, 1, args.length));
        } catch (IllegalArgumentException e) {
            language = Evaluate.Language.JAVASCRIPT;
            input = Util.combineArgs(args);
        }

        Evaluate.Result result = language.evaluate(Collections.unmodifiableMap(
                Stream.of(
                        ent("jda",     message.getJDA()),
                        ent("channel", message.getChannel()),
                        ent("guild",   message.getGuild()),
                        ent("msg",     message),
                        ent("user",    message.getAuthor()),
                        ent("member",  message.getGuild().getMember(message.getAuthor())),
                        ent("bot",     message.getJDA().getSelfUser())
                ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))), input);

        message.editMessage(new EmbedBuilder()
                .setTitle("Evaluate " + StringUtils.capitalize(language.name().toLowerCase()), null)
                .addField("Input", new MessageBuilder().appendCodeBlock(input, language.name().toLowerCase()).build().getRawContent(), true)
                .addField("Output", new MessageBuilder().appendCodeBlock(result.getOutput(), "javascript").build().getRawContent(), true)
                .setFooter(result.getStopwatch().elapsed(TimeUnit.NANOSECONDS) == 0 ? Constants.CROSS_EMOTE + " An error occurred" : String.format(Constants.CHECK_EMOTE + " Took %d ms (%d ns) to complete | %s", result.getStopwatch().elapsed(TimeUnit.MILLISECONDS), result.getStopwatch().elapsed(TimeUnit.NANOSECONDS), Util.generateTimestamp()), null)
                .setColor(color)
                .build()).queue();
    }

    private static AbstractMap.SimpleEntry<String, Object> ent(String key, Object value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }
}
