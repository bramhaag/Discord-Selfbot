package me.bramhaag.discordselfbot.commands.base;

import lombok.Data;
import lombok.NonNull;

import java.lang.reflect.Method;

@Data
public class CommandData {

    @NonNull
    private Method method;

    @NonNull
    private Object executor;
}
