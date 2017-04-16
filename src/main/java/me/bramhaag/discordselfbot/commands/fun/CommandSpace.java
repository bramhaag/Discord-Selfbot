package me.bramhaag.discordselfbot.commands.fun;

import me.bramhaag.discordselfbot.commands.Command;
import me.bramhaag.discordselfbot.util.Util;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class CommandSpace {

    @Command(name = "space", minArgs = 1)
    public void execute(Message message, TextChannel channel, String[] args) {
        String input = Util.combineArgs(args);
        StringBuilder builder = new StringBuilder();

        for(Character c : input.toCharArray()) {
            if(c == ' ') {
                builder.append("  ");
                continue;
            }

            builder.append(Character.toUpperCase(c)).append(' ');
        }

        message.editMessage(new MessageBuilder().appendCodeBlock(builder.toString(), "").build()).queue();
    }
}
