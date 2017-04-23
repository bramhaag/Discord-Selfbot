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

import lombok.NonNull;
import me.bramhaag.discordselfbot.commands.Command;
import me.bramhaag.discordselfbot.util.Util;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.regex.Pattern;

public class CommandEmoji {

    private String[] numbers = { "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine" };

    @Command(name = "emoji")
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        String text = Util.combineArgs(args);

        StringBuilder builder = new StringBuilder();
        for(Character c : text.toCharArray()) {
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

        message.editMessage(builder.toString()).queue();
    }
}
