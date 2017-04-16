package me.bramhaag.discordselfbot.commands;

import lombok.Data;
import lombok.NonNull;

import java.lang.reflect.Method;

@Data
class CommandData {

    /**
     * Method with {@link Command} annotation
     */
    @NonNull
    private Method method;

    /**
     * Annotation which stores information about the command
     */
    @NonNull
    private Command annotation;

    /**
     * Class to invoke
     */
    @NonNull
    private Object executor;
}
