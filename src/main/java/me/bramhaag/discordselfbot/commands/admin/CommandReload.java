package me.bramhaag.discordselfbot.commands.admin;

import lombok.NonNull;
import me.bramhaag.discordselfbot.Constants;
import me.bramhaag.discordselfbot.Main;
import me.bramhaag.discordselfbot.commands.Command;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class CommandReload {

    @Command(name = "reload")
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        Main.bot.getCommandHandler().unregister();
        Main.bot.registerCommands();

        channel.sendMessage(new MessageBuilder().appendCodeBlock("Reloaded!", "").build()).queue(m -> m.delete().queueAfter(Constants.REMOVE_TIME_SHORT, TimeUnit.SECONDS));
    }
}
