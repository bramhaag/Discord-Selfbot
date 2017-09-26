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

import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtil {

    /**
     * Get {@code user}'s avatar
     * @param user user whom's avatar to get
     * @return {@code user}'s avatar
     * @throws IOException if an error occurs during reading or when not
     * able to create required ImageInputStream.
     */
    @NotNull
    public static BufferedImage getAvatar(@NotNull User user) throws IOException {
        String url = user.getAvatarUrl() == null ? user.getDefaultAvatarUrl() : user.getAvatarUrl();
        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", Constants.USER_AGENT);

        return ImageIO.read(connection.getInputStream());
    }

    /**
     * Resize picture
     * @param source picture to be resized
     * @param type type of image
     * @param width width of image
     * @param height height of image
     * @return resized image with {@code width}x{@code height} as dimensions
     */
    @NotNull
    public static BufferedImage resize(@NotNull BufferedImage source, int type, int width, int height) {
        Image scaled = source.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, type);

        Graphics2D g = resized.createGraphics();
        g.drawImage(scaled, 0, 0,null);
        g.dispose();

        return resized;
    }

    /**
     * Create circle from an image
     * @param image image to create circle of
     * @param type type of image
     * @param size radius of circle
     * @return circular image of {@code image}
     */
    @NotNull
    public static BufferedImage makeCircular(@NotNull BufferedImage image, int type, int size) {
        BufferedImage circle = new BufferedImage(size, size, type);

        Graphics2D g = circle.createGraphics();
        g.clip(new Ellipse2D.Float(0, 0, size, size));
        g.drawImage(image, 0, 0, null);

        return circle;
    }
}
