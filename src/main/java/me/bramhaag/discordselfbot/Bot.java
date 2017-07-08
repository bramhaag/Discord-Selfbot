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

import lombok.Getter;
import lombok.NonNull;
import me.bramhaag.bcf.BCF;
import me.bramhaag.discordselfbot.commands.admin.CommandPing;
import me.bramhaag.discordselfbot.commands.admin.CommandVersion;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;

public class Bot {

    @Getter
    private JDA jda;

    /**
     * Constructor which starts bot
     *
     * @param token Bot's token
     */
    Bot(@NonNull String token) throws FileNotFoundException, LoginException, InterruptedException, RateLimitedException {
        this.jda = new JDABuilder(AccountType.CLIENT).setToken(token).setAutoReconnect(true).buildBlocking();
        new BCF(jda)
                .setPrefix("::")
                .register(new CommandPing())
                .register(new CommandVersion());
    }
}
