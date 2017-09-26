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

package me.bramhaag.discordselfbot;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.reflect.ClassPath;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import me.bramhaag.bcf.BCF;
import me.bramhaag.discordselfbot.commands.admin.CommandEvaluate;
import me.bramhaag.discordselfbot.commands.admin.CommandJShell;
import me.bramhaag.discordselfbot.commands.admin.CommandPing;
import me.bramhaag.discordselfbot.commands.admin.CommandPrune;
import me.bramhaag.discordselfbot.commands.util.CommandSpeedtest;
import me.bramhaag.discordselfbot.commands.admin.CommandVersion;
import me.bramhaag.discordselfbot.commands.fun.CommandEmoji;
import me.bramhaag.discordselfbot.commands.fun.CommandMock;
import me.bramhaag.discordselfbot.commands.fun.CommandRetarded;
import me.bramhaag.discordselfbot.commands.fun.CommandScreeching;
import me.bramhaag.discordselfbot.commands.fun.CommandThinking;
import me.bramhaag.discordselfbot.commands.fun.CommandTriggered;
import me.bramhaag.discordselfbot.commands.fun.CommandReact;
import me.bramhaag.discordselfbot.commands.util.CommandQuote;
import me.bramhaag.discordselfbot.commands.util.CommandStreaming;
import me.bramhaag.discordselfbot.config.ColorDeserializer;
import me.bramhaag.discordselfbot.config.Config;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import javax.security.auth.login.LoginException;
import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class Bot {

    private static Bot instance;

    private JDA jda;
    private Config config;

    /**
     * Constructor which starts bot
     * @param token Bot's token, will be read from the config if null
     */
    Bot(@Nullable String token) throws IOException, LoginException, InterruptedException, RateLimitedException {
        instance = this;

        copy("config.json");
        this.config = new GsonBuilder()
                .registerTypeAdapter(Color.class, new ColorDeserializer())
                .create()
                .fromJson(new JsonReader(new FileReader("config.json")), Config.class);

        extractResources();

        jda = new JDABuilder(AccountType.CLIENT)
                .setToken(MoreObjects.firstNonNull(token, config.getToken()))
                .setAutoReconnect(true)
                .setIdle(true)
                .buildBlocking();

        new BCF(jda).setPrefix(getConfig().getPrefix()).register(
                new Reflections("me.bramhaag.discordselfbot.commands", new SubTypesScanner(false))
                        .getSubTypesOf(Object.class).stream().map(this::newInstance).toArray()
        );
    }

    /**
     * Get the instance of the {@link Bot} class
     * @return instance of {@link Bot} class
     */
    public static Bot getInstance() {
        return instance;
    }

    /**
     * Get instance of JDA
     * @return instance of JDA
     */
    public JDA getJda() {
        return jda;
    }

    /**
     * Get config
     * @return config
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Extract all resources from the {@code assets} folder to the current folder
     * @throws IOException thrown by {@link URLDecoder#decode(String, String)} or {@link Class#getProtectionDomain()}
     */
    private void extractResources() throws IOException {
        File assets = new File("assets");
        if(!assets.exists()) Preconditions.checkState(assets.mkdir(), "Cannot create assets folder!");

        try {
            new JarFile(URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8")).stream()
                    .map(ZipEntry::getName)
                    .filter(name -> name.matches("assets/.+"))
                    .forEach(this::copy);
        } catch (InvalidPathException ignored) {
            //do nothing, will be thrown when running with JRebel
        }
    }

    /**
     * Copy file with {@code name} from the jar's resources to the jar's current folder
     * @param name Name of file, includes folder
     */
    private void copy(@NotNull String name) {
        File destination = new File(name);
        if(destination.exists())
            return;

        try {
            Files.copy(getClass().getResourceAsStream("/" + name), destination.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create new instance of {@code clazz} by getting the empty constructor
     * @param clazz Class to create a new instance of
     * @return Instance of type {@code clazz}
     */
    private Object newInstance(@NotNull Class<?> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
