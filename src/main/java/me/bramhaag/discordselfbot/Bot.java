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

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NonNull;
import me.bramhaag.bcf.BCF;
import me.bramhaag.discordselfbot.commands.admin.CommandPing;
import me.bramhaag.discordselfbot.commands.admin.CommandPrune;
import me.bramhaag.discordselfbot.commands.admin.CommandVersion;
import me.bramhaag.discordselfbot.commands.fun.CommandEmoji;
import me.bramhaag.discordselfbot.commands.fun.CommandMock;
import me.bramhaag.discordselfbot.commands.fun.CommandRetarded;
import me.bramhaag.discordselfbot.commands.fun.CommandScreeching;
import me.bramhaag.discordselfbot.commands.fun.CommandThinking;
import me.bramhaag.discordselfbot.commands.fun.CommandTriggered;
import me.bramhaag.discordselfbot.commands.fun.CommandReact;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.jar.JarFile;

public class Bot {

    @Getter
    private JDA jda;

    /**
     * Constructor which starts bot
     *
     * @param token Bot's token
     */
    Bot(@NonNull String token) throws IOException, LoginException, InterruptedException, RateLimitedException {
        //extractResources();

        this.jda = new JDABuilder(AccountType.CLIENT).setToken(token).setAutoReconnect(true).setIdle(true).buildBlocking();
        new BCF(jda)
                .setPrefix("::")
                .register(new CommandPing())
                .register(new CommandPrune())
                .register(new CommandVersion())
                .register(new CommandReact())
                .register(new CommandTriggered())
                .register(new CommandScreeching())
                .register(new CommandRetarded())
                .register(new CommandMock())
                .register(new CommandEmoji())
                .register(new CommandThinking());
    }

    private void extractResources() throws IOException {
        File assets = new File("assets");
        if(!assets.exists()) Preconditions.checkState(assets.mkdir(), "Cannot create assets folder!");

        new JarFile(URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8")).stream()
                .filter(entry -> entry.getName().matches("assets/.+"))
                .forEach(entry -> copy(entry.getName()));
    }

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
}
