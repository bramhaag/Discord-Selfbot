package me.bramhaag.discordselfbot.listeners;

import me.bramhaag.discordselfbot.Bot;
import me.bramhaag.discordselfbot.Constants;
import me.bramhaag.discordselfbot.util.BreakException;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class MessageListener extends ListenerAdapter {

    private Bot bot;

    private Map<String, String> placeholders;

    public MessageListener(Bot bot) {
        this.bot = bot;

        this.placeholders = new HashMap<>();
        this.placeholders.put("lenny", Constants.LENNY_FACE);
        this.placeholders.put("shrug", Constants.SHRUG);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (!e.getAuthor().getId().equalsIgnoreCase(e.getJDA().getSelfUser().getId())) {
            return;
        }

        Message message = e.getMessage();
        String rawContent = e.getMessage().getRawContent();

        for (Map.Entry placeholder : placeholders.entrySet()) {
            rawContent = rawContent.replace("{" + placeholder.getKey() + "}", placeholder.getValue().toString());
        }

        if(message.getRawContent().equals(rawContent))
            executeCommand(message);
        else
            message.editMessage(rawContent).queue(this::executeCommand);
    }

    private void executeCommand(Message message) {
        if(!message.getRawContent().startsWith(Bot.PREFIX)) {
            return;
        }

        try {
            bot.getCommandHandler().executeCommand(message);
        } catch (BreakException ex) {
            //Ignore
        }
    }

    private void edit(Message message, String search, String value) {
        String content = message.getRawContent();

        if(!content.contains(search)) {
            return;
        }

        message.editMessage(content.replace(search, value)).queue();
    }
}
