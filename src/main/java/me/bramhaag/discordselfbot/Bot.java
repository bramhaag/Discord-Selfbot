package me.bramhaag.discordselfbot;

import lombok.Getter;
import lombok.NonNull;
import me.bramhaag.discordselfbot.commands.admin.CommandEvaluate;
import me.bramhaag.discordselfbot.commands.admin.CommandPing;
import me.bramhaag.discordselfbot.commands.admin.CommandPrune;
import me.bramhaag.discordselfbot.commands.admin.CommandReload;
import me.bramhaag.discordselfbot.commands.CommandHandler;
import me.bramhaag.discordselfbot.commands.fun.CommandEmbed;
import me.bramhaag.discordselfbot.commands.util.*;
import me.bramhaag.discordselfbot.listeners.MessageListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

public class Bot {

    public static final String PREFIX = "::";

    @Getter
    private JDA jda;

    @Getter
    private CommandHandler commandHandler;

    Bot(@NonNull String token) {
        try {
            this.jda = new JDABuilder(AccountType.CLIENT).setToken(token).setAutoReconnect(true).buildBlocking();
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            e.printStackTrace();
            System.exit(0);
        }

        this.jda.addEventListener(new MessageListener(this));
        this.commandHandler = new CommandHandler();

        registerCommands();
    }

    public void registerCommands() {
        this.commandHandler.register(
                new CommandEmbed(),
                new CommandEvaluate(),
                new CommandPing(),
                new CommandPrune(),
                new CommandReload(),
                new CommandTimer()
        );
    }
}
