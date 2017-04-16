package me.bramhaag.discordselfbot.commands;

import me.bramhaag.discordselfbot.Main;
import me.bramhaag.discordselfbot.commands.base.Command;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class CommandReload {

    @Command(name = "reload")
    public Message execute(Message message, String[] args) {
        Main.bot.getCommandHandler().unregister();
        Main.bot.registerCommands();

        return new MessageBuilder().appendCodeBlock("Reloaded!", "").build();
    }
}
