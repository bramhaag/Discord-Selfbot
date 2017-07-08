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
import me.bramhaag.bcf.CommandContext;
import me.bramhaag.bcf.annotations.Command;
import me.bramhaag.bcf.annotations.CommandBase;
import me.bramhaag.discordselfbot.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

@Command("version")
public class CommandVersion {

    @CommandBase
    public void execute(@NotNull CommandContext context) {
        context.getMessage().editMessage(EmbedUtil.addDefaults(new EmbedBuilder()
                .addField("Version",     Manifests.read("Version"), true)
                .addField("Build Date",  Manifests.read("Date"),    true)
                .addField("Build Id",    Manifests.read("Build"),   true)
                .addField("Commit Hash", Manifests.read("Commit"),  true), "Version", true).build()
        ).queue();
    }
}
