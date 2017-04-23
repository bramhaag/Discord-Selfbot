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
import me.bramhaag.discordselfbot.Constants;
import me.bramhaag.discordselfbot.commands.Command;
import me.bramhaag.discordselfbot.util.Util;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.File;
import java.io.IOException;

public class CommandText {

    @Command(name = "text", minArgs = 1)
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        File output = new File("text_" + System.currentTimeMillis() + ".png");

        message.editMessage("```Generating Image...```").queue();
        new Thread(() -> {
            try {
                Process generateImage = Runtime.getRuntime().exec((Constants.MAGICK_PATH + " convert -background cyan -fill black -font Ravie -size 320x140 caption:\"" + Util.combineArgs(args) + "\" " + output.getPath()));
                generateImage.waitFor();


                message.getChannel().sendFile(output, new MessageBuilder().append(" ").build()).queue(m -> {
                    message.delete().queue();
                    Preconditions.checkState(output.delete(), String.format("File %s not deleted!", output.getName()));
                });
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
