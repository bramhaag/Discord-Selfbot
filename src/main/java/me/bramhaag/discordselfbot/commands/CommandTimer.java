package me.bramhaag.discordselfbot.commands;

import me.bramhaag.discordselfbot.Util;
import me.bramhaag.discordselfbot.commands.base.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.awt.*;
import java.util.Timer;

public class CommandTimer {

    @Command(name = "timer", minArgs = 2)
    public MessageEmbed execute(Message message, String[] args) {

        long delay;

        try {
            delay = Long.valueOf(args[0]);
        }
        catch (NumberFormatException e) {
            Util.editMessageError(message, e.getMessage());
            return null;
        }

        Util.editEmbed(message, new EmbedBuilder()
                .setTitle("Timer", null)
                .setDescription("Timer ending in " + DurationFormatUtils.formatDuration(delay * 1000, "H:mm:ss", true))
                .setColor(Color.GREEN)
        .build());

        Util.sendEmbedAfter(new EmbedBuilder().setTitle("Timer", null).setDescription("Timer expired!").setColor(Color.GREEN).build(), message.getTextChannel(), message.getAuthor(), delay);
        return null;
    }
}
