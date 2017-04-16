package me.bramhaag.discordselfbot.commands;

import lombok.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    @NonNull
    String name();

    @NonNull
    String[] aliases() default { };

    @NonNull
    int minArgs() default -1;
}
