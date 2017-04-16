package me.bramhaag.discordselfbot.commands.util;

import me.bramhaag.discordselfbot.util.Util;
import me.bramhaag.discordselfbot.commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.awt.*;

public class CommandTimer {

    @Command(name = "timer", minArgs = 1)
    public void execute(Message message, TextChannel channel, String[] args) {
        long delay;

        try {
            delay = Long.valueOf(args[0]);
        }
        catch (NumberFormatException e) {
            Util.editMessageError(message, e.toString());
            return;
        }

        Util.editEmbed(message, new EmbedBuilder()
                .setTitle("Timer", null)
                .setDescription("Timer ending in " + DurationFormatUtils.formatDuration(delay * 1000, "H:mm:ss", true))
                .setColor(Color.GREEN)
        .build());

        Util.sendEmbedAfter(new EmbedBuilder().setTitle("Timer", null).setDescription("Timer expired!").setColor(Color.GREEN).build(), message.getTextChannel(), message.getAuthor(), delay);
    }
}
