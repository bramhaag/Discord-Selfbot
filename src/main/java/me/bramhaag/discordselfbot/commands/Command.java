package me.bramhaag.discordselfbot.commands;

import lombok.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * Get/set name of command
     * @return command's name
     */
    @NonNull
    String name();

    /**
     * Get/set command's aliases
     * @return command's aliases
     */
    @NonNull
    String[] aliases() default { };

    /**
     * Get/set command's minimum arguments
     * @return command's minimum arguments
     */
    @NonNull
    int minArgs() default -1;
}
