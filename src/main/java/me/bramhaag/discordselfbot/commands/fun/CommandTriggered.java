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
import me.bramhaag.discordselfbot.Bot;
import me.bramhaag.discordselfbot.Constants;
import me.bramhaag.discordselfbot.commands.Command;
import me.bramhaag.discordselfbot.util.Util;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class CommandTriggered {

    @Command(name = "triggered", aliases = { "trigger", "triggering" }, minArgs = 1)
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        User user;
        String image = "triggered";
        final String text;

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

        if(args.length >= 3 && (args[1].equalsIgnoreCase("--image") || args[1].equals("-i"))) {
            image = args[2];
        }

        if(args.length >= 2 && image == null) {
            text = StringUtils.join(args, 1, args.length);
        } else if(args.length >= 4 && image != null) {
            text = StringUtils.join(args, 4, args.length);
        }
        else {
            text = null;
        }

        message.editMessage("```Generating GIF...```").queue();

        File avatar = new File("avatar_" + user.getId() + "_" + System.currentTimeMillis() + ".png");
        try {
            ImageIO.write(Util.getImage(user.getAvatarUrl()), "png", avatar);
        } catch (IOException e) {
            e.printStackTrace();

            Util.sendError(message, e.getMessage());
            return;
        }

        File output = new File("triggered_" + user.getId() + "_" + System.currentTimeMillis() + ".gif");
        File triggered = new File("assets/" + image + ".png");

        String avatarPath    = avatar.getAbsolutePath();
        String triggeredPath = triggered.getAbsolutePath();

        new Thread(() -> {
            try {
                Process generateGif = Runtime.getRuntime().exec((Bot.getConfig().getImagemagickPath() + " convert canvas:none -size 512x680 -resize 512x680! -draw \"image over -60,-60 640,640 \"\"{avatar}\"\"\" -draw \"image over 0,512 0,0 \"\"{triggered}\"\"\" " +
                        "( canvas:none -size 512x680! -draw \"image over -45,-50 640,640 \"\"{avatar}\"\"\" -draw \"image over -5,512 0,0 \"\"{triggered}\"\"\" ) " +
                        "( canvas:none -size 512x680! -draw \"image over -50,-45 640,640 \"\"{avatar}\"\"\" -draw \"image over -1,505 0,0 \"\"{triggered}\"\"\" )  " +
                        "( canvas:none -size 512x680! -draw \"image over -45,-65 640,640 \"\"{avatar}\"\"\" -draw \"image over -5,530 0,0 \"\"{triggered}\"\"\" ) " +
                        "-layers Optimize -set delay 2 " + output.getPath()).replace("{avatar}", avatarPath).replace("{triggered}", triggeredPath));

                generateGif.waitFor();

                if(text != null) {
                    Process addText = Runtime.getRuntime().exec(String.format("%s convert %s -font Calibri -pointsize 60 caption:\"%s\" %s", Constants.MAGICK_PATH, output, text, output));
                    addText.waitFor();
                }

                message.getChannel().sendFile(output, new MessageBuilder().append(" ").build()).queue(m -> {
                    message.delete().queue();

                    Preconditions.checkState(avatar.delete(), String.format("File %s not deleted!", avatar.getName()));
                    Preconditions.checkState(output.delete(), String.format("File %s not deleted!", output.getName()));
                });
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
