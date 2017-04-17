package me.bramhaag.discordselfbot.commands.util;

import me.bramhaag.discordselfbot.commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


public class CommandSpeedtest {

    File speedtestFile;

    public CommandSpeedtest() {
        speedtestFile = new File("speedtest.py");
        if(speedtestFile.exists())  {
            return;
        }

        try {
            URL website = new URL("https://raw.githubusercontent.com/sivel/speedtest-cli/master/speedtest.py");
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream("speedtest.py");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Command(name = "speedtest")
    public void execute(Message message, TextChannel channel, String[] args) {
        String s = "";
        String pS = "";

        EmbedBuilder builder = new EmbedBuilder()
                                   .setTitle("Speedtest", "http://speedtest.net/")
                                   .addField("Ping", "Waiting...", true)
                                   .addField("Download", "Waiting...", true)
                                   .addField("Upload", "Waiting...", true);

        MessageEmbed embed = builder.build();

        message.editMessage(embed).queue();


        try {
            Process process = Runtime.getRuntime().exec("py speedtest.py --share");

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
            e.printStackTrace();
        }
    }

    private MessageEmbed editField(String name, String value, MessageEmbed embed) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(embed.getTitle(), embed.getUrl());
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
