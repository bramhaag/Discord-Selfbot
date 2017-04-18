package me.bramhaag.discordselfbot.commands.admin;

import lombok.NonNull;
import me.bramhaag.discordselfbot.Constants;
import me.bramhaag.discordselfbot.commands.Command;
import me.bramhaag.discordselfbot.util.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CommandPing {

    @Command(name = "ping")
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        long startMillis = System.currentTimeMillis();
        message.editMessage("`Waiting...`").queue(m -> m.editMessage(
                new EmbedBuilder().setTitle(Constants.PONG_EMOTE + " Pong!", null)
                                  .addField("Response time (Bot)", (System.currentTimeMillis() - startMillis) + " ms", true)
                                  .addField("Response time (API)", message.getJDA().getPing() + " ms", true)
                                  .setFooter("Ping | " + Util.generateTimestamp(), null)
                                  .build())
                .queue(embed -> embed.delete().queueAfter(Constants.REMOVE_TIME_LONG, TimeUnit.SECONDS)));
    }
}
