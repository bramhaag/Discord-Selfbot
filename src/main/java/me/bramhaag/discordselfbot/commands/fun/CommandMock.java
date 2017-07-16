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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

@Command("mock|spongemock|spongebob")
public class CommandMock {

    @CommandBase
    public void execute(CommandContext context, String[] text) {
        context.getMessage().editMessage(
                EmbedUtil.addDefaults(new EmbedBuilder().setTitle("Spongemock!")
                        .setDescription("GEneRATiNg iMaGE..."), "Spongemock", null).build()
        ).queue();

        String input = String.join(" ", text);
        Random r = new Random();
        StringBuilder sb = new StringBuilder(input.length());

        for (char c : input.toCharArray()) sb.append(r.nextBoolean() ? Character.toLowerCase(c) : Character.toUpperCase(c));
        input = sb.toString();

        try {
            ImageBuilder builder = new ImageBuilder(678, 778, BufferedImage.TYPE_INT_ARGB)
                    .fillRect(Color.WHITE, 0, 0, 678, 100)
                    .addImage(new File("assets/spongemock.jpg"), 0, 100)
                    .addText(input, Color.BLACK, 50,25, 60);
            context.getChannel().sendFile(builder.create(), "spongemock.png", null).queue(ignored -> context.getMessage().delete().queue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
