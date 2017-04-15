package me.bramhaag.discordselfbot.commands.base;

import lombok.Data;
import lombok.NonNull;

import java.lang.reflect.Method;

@Data
class CommandData {

    @NonNull
    private Method method;

    @NonNull
    private Command annotation;

    @NonNull
    private Object executor;
}
