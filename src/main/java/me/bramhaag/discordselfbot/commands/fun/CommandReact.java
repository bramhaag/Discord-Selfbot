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
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class CommandReact {
    private Character[] regionalIdenticator;

    public CommandReact() {
        regionalIdenticator = IntStream.rangeClosed((int) '\uDDE6', (int) '\uDDFF')
                .mapToObj(i -> (char) i)
                .toArray(Character[]::new);
    }

    @Command(name = "react", minArgs = 1)
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        channel.getHistoryAround(args[0], 1).queue(history -> {
            Message target = history.getRetrievedHistory().get(0);
            if(target == null) {
                return;
            }

            for(Character c : args[1].toCharArray()) {
                c = Character.toLowerCase(c);
                if(!Pattern.matches("[a-zA-Z]", c.toString())) {
                    continue;
                }

                target.addReaction('\uD83C' + regionalIdenticator[(int) c - 'a'].toString()).queue();
            }
        });
    }
}
