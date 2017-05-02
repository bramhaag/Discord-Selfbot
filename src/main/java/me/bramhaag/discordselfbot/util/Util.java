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

import lombok.NonNull;
import me.bramhaag.discordselfbot.Constants;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Util {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd-MM-Y 'at' H:mm a");

    /**
     * Combine {@code args}.
     * @param args {@code String[]} to combine.
     * @return combined {@code args}.
     */
    @NonNull
    public static String combineArgs(@NonNull String[] args) {
        return StringUtils.join(args, ' ');
    }

    /**
     * Edit message with an error message.
     * @param message message to edit.
     * @param reason reason error was thrown.
     */
    public static void sendError(@NonNull Message message, @NonNull String reason) {
        message.editMessage(new MessageBuilder().appendCodeBlock("An error occurred! " + reason, "javascript").build().getRawContent(), 5).queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
    }

    /**
     * Generate timestamp from current date with pattern "EE dd-MM-Y H:mm a" {@link #dateFormat}.
     * @return Formatted timestamp with pattern "EE dd-MM-Y H:mm a" from {@link #dateFormat}.
     *
     * @see #dateFormat
     */
    @NonNull
    public static String generateTimestamp() {
        return dateFormat.format(new Date());
    }

    /**
     * Get image from URL.
     * @param url url to get image from.
     * @return image.
     *
     * @throws IOException if an I/O error occurs while creating the input stream.
     */
    @NonNull
    public static BufferedImage getImage(@NonNull String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", Constants.USER_AGENT);

        return ImageIO.read(connection.getInputStream());
    }

    public static void generateGif(String[] args, Message message, String image) {

    }
}
