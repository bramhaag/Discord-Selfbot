package me.bramhaag.discordselfbot.listeners;

import me.bramhaag.discordselfbot.Bot;
import me.bramhaag.discordselfbot.Constants;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(!e.getAuthor().getId().equalsIgnoreCase(e.getJDA().getSelfUser().getId())) {
            return;
        }

        Message message = e.getMessage();

        edit(message, "{lenny}", Constants.LENNY_FACE);
        edit(message, "{shrug}", Constants.SHRUG);

        if(!message.getRawContent().startsWith(Bot.PREFIX)) {
            return;
        }

        //TODO execute command
    }

    private void edit(Message message, String search, String value) {
        String content = message.getRawContent();

        if(!content.contains(search)) {
            return;
        }

        message.editMessage(content.replace(search, value)).queue();
    }
}
