package me.bramhaag.discordselfbot.commands;

import me.bramhaag.discordselfbot.Constants;
import me.bramhaag.discordselfbot.commands.base.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class CommandPing {

    @Command(name = "ping")
    public void execute(Message message, TextChannel channel, String[] args) {
        long startMillis = System.currentTimeMillis();
        message.editMessage("`Waiting...`").queue(m -> m.editMessage(
                new EmbedBuilder().setTitle(Constants.PONG_EMOTE + " Pong!", null)
                                  .addField("Response time", (System.currentTimeMillis() - startMillis) + " ms", false)
                                  .addField("Response time (JDA)", message.getJDA().getPing() + " ms", false)
                                  .build()).queue());
    }
}
