package me.bramhaag.discordselfbot.commands.fun;

import me.bramhaag.discordselfbot.commands.Command;
import me.bramhaag.discordselfbot.util.Util;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.io.IOException;

public class CommandTriggered {

    //TODO JMagik
    @Command(name = "triggered", aliases = { "trigger", "triggering" }, minArgs = 1)
    public void execute(Message message, TextChannel channel, String[] args) {
        if(message.getMentionedUsers().size() == 0) {
            Util.editMessageError(message, "Invalid user!");
            return;
        }

        User user = message.getMentionedUsers().get(0);
        try {
            Util.getImage(user.getAvatarUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
