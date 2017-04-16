package me.bramhaag.discordselfbot.commands;

import me.bramhaag.discordselfbot.Util;
import me.bramhaag.discordselfbot.commands.base.Command;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;

import java.util.stream.Collectors;

public class CommandPrune {

    @Command(name = "prune", aliases = { "purge" }, minArgs = 1)
    public Message execute(Message message, String[] args) {

        int amount;

        try {
            amount = Integer.valueOf(args[0]);
        }
        catch (NumberFormatException e) {
            Util.editMessageError(message, e.toString());
            return null;
        }

        MessageHistory history = message.getChannel().getHistory();
        history.retrievePast(amount)
                .queue(messages -> messages
                                   .parallelStream()
                                   .filter(m -> m.getAuthor().getId().equalsIgnoreCase(message.getAuthor().getId()))
                                   .collect(Collectors.toList())
                                   .forEach(m -> m.delete().queue()));

        return null;
    }
}
