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
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main {

    public static Bot bot;

    public static void main(String[] args) {
        Preconditions.checkArgument(args.length == 1, "Please specify a token");

        try {
            bot = new Bot(args[0]);
        } catch (LoginException | RateLimitedException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
