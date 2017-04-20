/*
 * Copyright 2017 Bram Hagens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
