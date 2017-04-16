package me.bramhaag.discordselfbot.commands.fun;

import me.bramhaag.discordselfbot.util.Util;
import me.bramhaag.discordselfbot.commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.Arrays;

public class CommandEmbed {

    @Command(name = "embed")
    public void execute(Message message, TextChannel channel, String[] args) {
        message.getChannel().sendMessage(new EmbedBuilder()
                .setTitle(args[0], null)
                .setDescription(Util.combineArgs(Arrays.copyOfRange(args, 1, args.length)))
                .setFooter(message.getAuthor().getName(), message.getAuthor().getAvatarUrl())
                .setColor(Color.GREEN)
                .build()).queue();
    }
}
