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
import me.bramhaag.discordselfbot.commands.Command;
import me.bramhaag.discordselfbot.util.JavadocParser;
import me.bramhaag.discordselfbot.util.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class CommandJavadoc {

    @Command(name = "javadoc", aliases = { "doc", "jd" }, minArgs = 1)
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        String[] parts = args[0].split("\\.");

        String pack = parts.length >= 3 ? StringUtils.join(Arrays.copyOf(parts, parts.length - 2), "/") + "/" : null;
        String clazz = parts[parts.length - 2];
        String method = args[0].contains(".") ? args[0].substring(args[0].lastIndexOf(".") + 1, args[0].length()) : args[0];

        JavadocParser.JavadocResult result = null;

        try {
            if(pack == null) {
                result = JavadocParser.getJavadoc(clazz + "/" + method);
            }
            else {
                result = args.length > 1 ? JavadocParser.getJavadoc(args[1], pack + clazz, method) : JavadocParser.getJavadoc(pack + clazz, method);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(result == null) {
            Util.sendError(message, "Result is null!");
        }

        EmbedBuilder builder = new EmbedBuilder().setTitle("Javadoc", null)
                                                 .addField("Name", code(result.getName()), false)
                                                 .addField("Format", code(result.getFormat()), false)
                                                 .addField("Description", code(result.getDescription()), false);

        result.getFields().forEach((key, value) -> builder.addField(key, code(StringUtils.join(value, "\n")), false));
        channel.sendMessage(builder.build()).queue();
    }

    private String code(String input) {
        return "```" + input + "```";
    }
}
