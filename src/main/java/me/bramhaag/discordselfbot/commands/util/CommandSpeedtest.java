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

package me.bramhaag.discordselfbot.commands.util;

import me.bramhaag.bcf.CommandContext;
import me.bramhaag.bcf.annotations.Command;
import me.bramhaag.bcf.annotations.CommandBase;
import me.bramhaag.discordselfbot.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Command("speedtest")
public class CommandSpeedtest {

    @CommandBase
    public void execute(@NotNull CommandContext context) throws IOException {
        MessageEmbed embed = EmbedUtil.addDefaults(new EmbedBuilder()
                .setTitle("Speedtest")
                .addField("Ping", "Waiting...", true)
                .addField("Download", "Waiting...", true)
                .addField("Upload", "Waiting...", true), "Speedtest", null
        ).build();

        context.getMessage().editMessage(embed).queue();

//        Speedtest speedtest = new Speedtest();
//        try {
//            Speedtest.Server s = speedtest.getClosestServer();
//            System.out.printf("%s, (%s, %s)", s.getId(), s.getLatitude(), s.getLongitude());
//            speedtest.testDownload(aLong -> System.out.println(aLong + " b/s"));
//        } catch (ParserConfigurationException | SAXException | IOException e) {
//            e.printStackTrace();
//        }
    }
}
