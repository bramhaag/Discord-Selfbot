package me.bramhaag.discordselfbot;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NonNull;
import me.bramhaag.discordselfbot.commands.admin.CommandEvaluate;
import me.bramhaag.discordselfbot.commands.admin.CommandPing;
import me.bramhaag.discordselfbot.commands.admin.CommandPrune;
import me.bramhaag.discordselfbot.commands.admin.CommandReload;
import me.bramhaag.discordselfbot.commands.CommandHandler;
import me.bramhaag.discordselfbot.commands.fun.CommandEmbed;
import me.bramhaag.discordselfbot.commands.fun.CommandHTML;
import me.bramhaag.discordselfbot.commands.fun.CommandSpace;
import me.bramhaag.discordselfbot.commands.fun.CommandTriggered;
import me.bramhaag.discordselfbot.commands.util.*;
import me.bramhaag.discordselfbot.listeners.MessageListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.commons.io.FileUtils;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Bot {

    public static final String PREFIX = "::";

    @Getter
    private JDA jda;

    @Getter
    private CommandHandler commandHandler;

    /**
     * Constructor which starts bot
     *
     * @param token Bot's token
     */
    Bot(@NonNull String token) {
        try {
            this.jda = new JDABuilder(AccountType.CLIENT).setToken(token).setAutoReconnect(true).buildBlocking();
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            e.printStackTrace();
            System.exit(0);
        }

        this.jda.addEventListener(new MessageListener(this));
        this.commandHandler = new CommandHandler();

        Preconditions.checkState(extractLibs(), "Cannot extract files!");
        registerCommands();
    }

    /**
     * Register all commands
     */
    //TODO scan package with commands
    public void registerCommands() {
        this.commandHandler.register(
                new CommandEmbed(),
                new CommandEvaluate(),
                new CommandPing(),
                new CommandPrune(),
                new CommandReload(),
                new CommandTimer(),
                new CommandTriggered(),
                new CommandSpace(),
                new CommandHTML(),
                new CommandSpeedtest()
        );
    }


    /**
     * Extract files from resources. It won't copy the file if it already exists.
     *
     * @return {@code true} when all files were copied without issues, {@code false} when something went wrong
     */
    private boolean extractLibs() {
        File libsDir =   new File("libs");
        File assetsDir = new File("assets");

        if(!createDir(libsDir) || !createDir(assetsDir)) {
            return false;
        }

        try {
            extract(getClass().getResource("/libs/speedtest.py"),    new File(libsDir, "speedtest.py"));
            extract(getClass().getResource("/assets/triggered.png"), new File(assetsDir, "triggered.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Create directory
     *
     * @param dir directory to create
     *
     * @return {@code true} when directory was created without issues, {@code false} when something went wrong
     */
    private boolean createDir(File dir) {
        if(!dir.exists() || !dir.isDirectory()) {
            System.out.println(String.format("%s folder not found. Unpacking...", dir.getName()));
            boolean mkdir = dir.mkdir();

            if(!mkdir) {
                System.err.println(String.format("Cannot create folder %s, shutting down...", dir.getName()));
                return false;
            }
        }

        return true;
    }

    /**
     * Copy file from {@code url} to {@code destination}
     *
     * @param url url to file
     * @param destination destination file
     *
     * @throws IOException if {@code url} cannot be opened
     * @throws IOException if {@code destination} is a directory
     * @throws IOException if {@code destination} cannot be written
     * @throws IOException if {@code destination} needs creating but can't be
     * @throws IOException if an IO error occurs during copying
     */
    private void extract(URL url, File destination) throws IOException {
        if(!destination.exists())
            FileUtils.copyURLToFile(url, destination);
    }
}
