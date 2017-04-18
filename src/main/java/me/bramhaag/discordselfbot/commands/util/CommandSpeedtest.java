package me.bramhaag.discordselfbot.commands.util;

import me.bramhaag.discordselfbot.commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CommandSpeedtest {

    @Command(name = "speedtest")
    public void execute(Message message, TextChannel channel, String[] args) {
        String s;

        EmbedBuilder builder = new EmbedBuilder()
                                   .setTitle("Speedtest", "http://speedtest.net/")
                                   .addField("Ping", "Waiting...", true)
                                   .addField("Download", "Waiting...", true)
                                   .addField("Upload", "Waiting...", true)
                                   .setFooter("speedtest.net | " + new SimpleDateFormat("EE dd-MM-Y H:mm a").format(new Date()), null);


        MessageEmbed embed = builder.build();

        message.editMessage(embed).queue();


        try {
            Process process = Runtime.getRuntime().exec("py libs/speedtest.py --share");

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
