package me.bramhaag.discordselfbot.commands.base;

import lombok.NonNull;
import me.bramhaag.discordselfbot.Bot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CommandHandler {

    private Map<String, CommandData> commands = new HashMap<>();

    public void register(@NonNull Object... objects) {
        Arrays.stream(objects).forEach(this::register);
    }

    private void register(@NonNull Object object) {
        Arrays.stream(object.getClass().getMethods()).forEach(method -> {
            if(!method.isAnnotationPresent(Command.class)) return;

            Command info = method.getAnnotation(Command.class);
            CommandData commandData = new CommandData(method, info, object);

            commands.put(info.name(), commandData);
            Arrays.stream(info.aliases()).forEach(alias -> commands.put(alias, commandData));
        });
    }

    public void executeCommand(@NonNull MessageReceivedEvent event) {
        Message message = event.getMessage();

        String content = message.getRawContent();
        String[] parts = content.split(" ");

        String name = parts[0].substring(Bot.PREFIX.length()).trim();
        String[] args = new String[parts.length - 1];

        for(int i = 1; i < parts.length; i++) {
            args[i - 1] = parts[i].trim();
        }

        commands.forEach((command, data) -> {
            if (!name.equalsIgnoreCase(command)) return;

            Command info = data.getAnnotation();
            if (info.minArgs() != -1 && info.minArgs() < args.length) {
                //TODO do something
                return;
            }

            try {
                Message output = (Message) data.getMethod().invoke(data.getExecutor(), message, args);

                if(output == null) return;
                message.editMessage(output).queue();
            } catch (IllegalAccessException | InvocationTargetException e) {
                //TODO handle exception
                e.printStackTrace();
            }
        });
    }
}
