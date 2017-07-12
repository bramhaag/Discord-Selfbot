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

import java.awt.*;

public class Constants {
    /**
     * Time it takes to delete a debug message
     */
    public static final int REMOVE_TIME_SHORT = 5;

    /**
     * Time it takes to delete a normal message
     */
    public static final int REMOVE_TIME_LONG = 10;

    /**
     * Placeholder value for {@link net.dv8tion.jda.core.entities.Message}
     */
    public static final String LENNY_FACE = "( ͡° ͜ʖ ͡°)";

    /**
     * Placeholder value for {@link net.dv8tion.jda.core.entities.Message}
     */
    public static final String SHRUG = "¯\\_(ツ)_/¯";

    /**
     * {@code Ping pong} emoji (U+1F3D3) name
     */
    public static final String PONG_EMOTE  = "\uD83C\uDFD3";

    /**
     * {@code White Heavy Check Mark} emoji (U+2705) UTF-8 character
     */
    public static final String CHECK_EMOTE = "✅";

    /**
     * {@code Cross Mark} emoji (U+274C) UTF-8 character
     */
    public static final String CROSS_EMOTE = "❌";

    public static final Color PRIMARY_COLOR = new Color(85, 140, 255);
    public static final Color ERROR_COLOR = new Color(255, 80, 75);

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
}
