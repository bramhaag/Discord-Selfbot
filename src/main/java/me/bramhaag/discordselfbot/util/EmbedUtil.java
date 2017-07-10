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

package me.bramhaag.discordselfbot.util;

import net.dv8tion.jda.core.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;

public class EmbedUtil {

    @NotNull
    public static EmbedBuilder addDefaults(@NotNull EmbedBuilder builder, @Nullable String footer, @Nullable Boolean isSuccessful) {
        builder.setTimestamp(ZonedDateTime.now());

        String finalFooter = "";

        if(isSuccessful != null) {
            finalFooter += isSuccessful ? Constants.CHECK_EMOTE : Constants.CROSS_EMOTE;
            builder.setColor(isSuccessful ? Constants.PRIMARY_COLOR : Constants.ERROR_COLOR);
        }

        if(footer != null && !footer.isEmpty()) {
            finalFooter += " " + footer;
        }

        if(!finalFooter.isEmpty()) {
            builder.setFooter(finalFooter, null);
        }

        return builder;
    }
}
