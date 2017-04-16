package me.bramhaag.discordselfbot.commands;

import me.bramhaag.discordselfbot.util.Util;
import me.bramhaag.discordselfbot.commands.base.Command;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.stream.Collectors;

public class CommandPrune {

    @Command(name = "prune", aliases = { "purge" }, minArgs = 1)
    public void execute(Message message, TextChannel channel, String[] args) {
        int amount;

        try {
            amount = Integer.valueOf(args[0]);
        }
        catch (NumberFormatException e) {
            Util.editMessageError(message, e.toString());
            return;
        }

        MessageHistory history = message.getChannel().getHistory();
        history.retrievePast(amount)
                .queue(messages -> messages
                                   .parallelStream()
                                   .filter(m -> m.getAuthor().getId().equalsIgnoreCase(message.getAuthor().getId()))
                                   .collect(Collectors.toList())
                                   .forEach(m -> m.delete().queue()));
    }
}
