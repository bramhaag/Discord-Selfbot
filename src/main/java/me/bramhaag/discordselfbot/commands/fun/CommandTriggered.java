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

import com.google.common.base.Preconditions;
import lombok.NonNull;
import me.bramhaag.discordselfbot.commands.Command;
import me.bramhaag.discordselfbot.util.Util;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;

public class CommandTriggered {

    @Command(name = "triggered", aliases = { "trigger", "triggering" }, minArgs = 1)
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        User user;
        String image = "triggered";
        String text;

        try {
            user = message.getJDA().getUserById(args[0].replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            Util.sendError(message, e.getMessage());
            return;
        }

        if(user == null) {
            Util.sendError(message, "Invalid user!");
            return;
        }

        if(args.length >= 3 || args[1].equalsIgnoreCase("--image") || args[1].equals("-i")) {
            image = args[2];
        }

        if(args.length >= 2 && image == null) {
            text = StringUtils.join(args, 1, args.length);
        } else if(args.length >= 4 && image != null) {
            text = StringUtils.join(args, 4, args.length);
        }



        Util.generateGif(args, message, "triggered.png");
    }
}
