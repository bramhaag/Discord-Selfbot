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
import me.bramhaag.discordselfbot.graphics.ImageBuilder;
import me.bramhaag.discordselfbot.util.EmbedUtil;
import me.bramhaag.discordselfbot.util.ImageUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//TODO GIF support
@Command("trigger|triggered")
public class CommandTriggered {

    @CommandBase
    public void execute(CommandContext context, User user) {
        context.getMessage().editMessage(
                EmbedUtil.addDefaults(new EmbedBuilder().setTitle("Triggered")
                        .setDescription("Generating image..."), "Triggered", null).build()
        ).queue();

        try {
            BufferedImage avatar = ImageUtil.resize(ImageUtil.getAvatar(user), 512, 512);
            ImageBuilder builder = new ImageBuilder(512, 712).addImage(avatar, 0, 0).addImage(new File("assets/triggered.png"), 0, 512);
            context.getChannel().sendFile(builder.create(), "triggered.png", null).queue(ignored -> context.getMessage().delete().queue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
