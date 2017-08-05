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

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Command("emoji")
public class CommandEmoji {

    private String[] numbers = { "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine" };

    @CommandBase
    public void execute(CommandContext context, String[] text) {
        String input = String.join(" ", text);

        StringBuilder builder = new StringBuilder();
        for(Character c : input.toCharArray()) {
            if(Pattern.matches("[0-9]", c.toString())) {
                builder.append(":").append(numbers[(int) c - '0']).append(":");
            }
            else if(Pattern.matches("[a-zA-Z]", c.toString())) {
                builder.append(":regional_indicator_").append(Character.toLowerCase(c)).append(":");
            }
            else {
                builder.append(c);
            }
        }

        context.getMessage().editMessage(builder.toString()).queue();
    }
}
