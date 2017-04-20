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
import me.bramhaag.discordselfbot.util.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.*;

public class CommandSpeedtest {

    @Command(name = "speedtest")
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        String s;

        EmbedBuilder builder = new EmbedBuilder()
                                   .setTitle("Speedtest", "http://speedtest.net/")
                                   .addField("Ping", "Waiting...", true)
                                   .addField("Download", "Waiting...", true)
                                   .addField("Upload", "Waiting...", true)
                                   .setFooter("Speedtest.net | " + Util.generateTimestamp(), null);


        MessageEmbed embed = builder.build();

        message.editMessage(embed).queue();


        try {
            Process process = new ProcessBuilder("py", "libs/speedtest.py", "--share").start();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((s = stdInput.readLine()) != null) {
                String[] parts = s.split(" ");
                if(s.startsWith("Hosted by")) {
                    embed = editField("Ping", parts[parts.length - 2] + " ms", embed);
                    message.editMessage(embed).queue();
                }
                else if(s.startsWith("Download:")) {
                    embed = editField("Download", parts[1] + " Mbit/s", embed);
                    message.editMessage(embed).queue();
                }
                else if(s.startsWith("Upload:")) {
                    embed = editField("Upload", parts[1] + " Mbit/s", embed);
                    message.editMessage(embed).queue();
                }
                else if(s.startsWith("Share results:")) {
                    message.editMessage(new EmbedBuilder(embed).setImage(parts[2]).build()).queue();
                }
            }

            process.destroy();
        } catch (IOException e) {
            //TODO handle exception
            e.printStackTrace();
        }
    }

    private MessageEmbed editField(String name, String value, MessageEmbed embed) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(embed.getTitle(), embed.getUrl());
        builder.setFooter(embed.getFooter().getText(), null);
        embed.getFields().forEach(field -> {
            if(field.getName().equalsIgnoreCase(name)) {
                builder.addField(name, value, true);
                return;
            }

            builder.addField(field.getName(), field.getValue(), true);
        });

        return builder.build();
    }
}
