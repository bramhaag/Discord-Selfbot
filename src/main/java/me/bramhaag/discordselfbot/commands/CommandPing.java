package me.bramhaag.discordselfbot.commands;

import me.bramhaag.discordselfbot.Constants;
import me.bramhaag.discordselfbot.commands.base.Command;
import net.dv8tion.jda.core.entities.Message;

public class CommandPing {

    @Command(name = "ping")
    public Message execute(Message message, String[] args) {
        long startMillis = System.currentTimeMillis();
        message.editMessage("`Waiting...`").queue(m -> m.editMessage(Constants.PONG_EMOTE + " `Pong! (" + (System.currentTimeMillis() - startMillis) + " ms)`").queue());

        return null;
    }
}
