package me.bramhaag.discordselfbot.commands.fun;

import lombok.NonNull;
import me.bramhaag.discordselfbot.commands.Command;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class CommandMeme {

    @Command(name = "meme", minArgs = 3)
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {

    }
}
