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

package me.bramhaag.discordselfbot.commands;

import lombok.NonNull;
import me.bramhaag.discordselfbot.Bot;
import me.bramhaag.discordselfbot.util.BreakException;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CommandHandler {

    private Map<String, CommandData> commands = new HashMap<>();

    /**
     * Register commands
     * @param objects Command classes to register
     */
    public void register(@NonNull Object... objects) {
        Arrays.stream(objects).forEach(this::register);
    }

    /**
     * Register a single command
     * @param object Command to register
     */
    private void register(@NonNull Object object) {
        Arrays.stream(object.getClass().getMethods()).forEach(method -> {
            if(!method.isAnnotationPresent(Command.class)) return;

            Command info = method.getAnnotation(Command.class);
            CommandData commandData = new CommandData(method, info, object);

            commands.put(info.name(), commandData);
            Arrays.stream(info.aliases()).forEach(alias -> commands.put(alias, commandData));
        });
    }

    public void unregister() {
        commands.clear();
    }

    /**
     * Execute command from a {@link MessageReceivedEvent}
     * @param message Message containing command
     *
     * @throws BreakException thrown when command is found to break the forEach loop, should be ignored
     */
    public void executeCommand(@NonNull Message message) {

        String content = message.getRawContent();
        String[] parts = content.split(" ");

        String name = parts[0].substring(Bot.getConfig().getPrefix().length()).trim();
        String[] args = new String[parts.length - 1];

        for(int i = 1; i < parts.length; i++) {
            args[i - 1] = parts[i].trim();
        }

        commands.forEach((command, data) -> {
            if (!name.equalsIgnoreCase(command)) return;

            Command info = data.getAnnotation();
            if (info.minArgs() != -1 && info.minArgs() > args.length) {
                message.editMessage(new MessageBuilder().appendCodeBlock("An error occurred while executing command! Not enough arguments!", "javascript").build()).queue();
                return;
            }

            try {
                data.getMethod().invoke(data.getExecutor(), message, message.getTextChannel(), args);
                throw new BreakException();

            } catch (IllegalAccessException | InvocationTargetException e) {
                //TODO handle exception
                e.printStackTrace();
            }
        });
    }
}
