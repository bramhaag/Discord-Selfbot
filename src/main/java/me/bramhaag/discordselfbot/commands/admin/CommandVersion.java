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

package me.bramhaag.discordselfbot.commands.admin;

import com.jcabi.manifests.Manifests;
import lombok.NonNull;
import me.bramhaag.discordselfbot.commands.Command;
import me.bramhaag.discordselfbot.util.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class CommandVersion {

    @Command(name = "version")
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        message.editMessage(new EmbedBuilder()
            .addField("Version",     Manifests.read("Version"), true)
            .addField("Build Date",  Manifests.read("Date"),    true)
            .addField("Build Id",    Manifests.read("Build"),   true)
            .addField("Commit Hash", Manifests.read("Commit"),  true)
            .setFooter(Util.generateTimestamp(), null)
        .build()).queue();
    }
}
