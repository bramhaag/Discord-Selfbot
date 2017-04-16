package me.bramhaag.discordselfbot.commands;

import me.bramhaag.discordselfbot.Util;
import me.bramhaag.discordselfbot.commands.base.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.util.Arrays;

public class CommandEmbed {

    @Command(name = "embed")
    public Message execute(Message message, String[] args) {
        return (Message) new EmbedBuilder()
                .setTitle(args[0], null)
                .setDescription(Util.combineArgs(Arrays.copyOfRange(args, 1, args.length)))
                .setFooter(message.getAuthor().getName(), message.getAuthor().getAvatarUrl())
                .setColor(Color.GREEN)
            .build();
    }
}
