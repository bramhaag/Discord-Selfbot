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
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

public class CommandLMGTFY {

    @Command(name = "lmgtfy", minArgs = 1)
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        String tinyURL = "http://tinyurl.com/api-create.php?url=";
        String lmgtfyURL = "http://lmgtfy.com?q=";

        String url;

        try {
            if (args[0].equalsIgnoreCase("--expanded") || args[0].equalsIgnoreCase("-e") && args.length >= 2) {
                url = lmgtfyURL + URLEncoder.encode(StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " "), "UTF-8");
            } else {
                Document doc;
                try {
                    doc = Jsoup.connect(tinyURL + lmgtfyURL + URLEncoder.encode(StringUtils.join(args, " "), "UTF-8")).get();
                } catch (IOException e) {
                    e.printStackTrace();

                    Util.sendError(message, e.getMessage());
                    return;
                }

                url = doc.body().text();
            }
        } catch (UnsupportedEncodingException e) {
            Util.sendError(message, e.getMessage());
            return;
        }

        message.editMessage("<" + url + ">").queue();
    }
}
