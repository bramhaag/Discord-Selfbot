package me.bramhaag.discordselfbot.commands.base;

import lombok.NonNull;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.*;

public class CommandHandler {

    private Map<String, CommandData> commands = new HashMap<>();

    public void register(@NonNull Object object) {
        Arrays.stream(object.getClass().getMethods()).forEach(method -> {
            if(!method.isAnnotationPresent(Command.class)) return;

            Command info = method.getAnnotation(Command.class);
            CommandData commandData = new CommandData(method, object);

            commands.put(info.name(), commandData);
            Arrays.stream(info.aliases()).forEach(alias -> commands.put(alias, commandData));
        });
    }

    public void executeCommand(@NonNull MessageReceivedEvent event) {

    }
}
