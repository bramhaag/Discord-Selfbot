package me.bramhaag.discordselfbot.commands.util;

import lombok.NonNull;
import me.bramhaag.discordselfbot.util.Util;
import me.bramhaag.discordselfbot.commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class CommandTimer {

    @Command(name = "timer", minArgs = 1)
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        long delay;

        try {
            delay = Long.valueOf(args[0]);
        }
        catch (NumberFormatException e) {
            Util.sendError(message, e.toString());
            return;
        }

        message.editMessage(new EmbedBuilder()
                .setTitle("Timer", null)
                .setDescription("Timer ending in " + DurationFormatUtils.formatDuration(delay * 1000, "H:mm:ss", true))
                .setFooter("Timer | " + Util.generateTimestamp(), null)
                .setColor(Color.GREEN)
                .build())
        .queue();

        channel.sendMessage(new EmbedBuilder()
                .setTitle("Timer", null)
                .setDescription("Timer expired!")
                .setFooter("Timer | " + Util.generateTimestamp(), null)
                .setColor(Color.GREEN).build())
        .queueAfter(delay, TimeUnit.SECONDS);
    }
}
