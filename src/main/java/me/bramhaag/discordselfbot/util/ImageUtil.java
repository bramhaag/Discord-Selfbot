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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtil {

    public static BufferedImage getAvatar(User user) throws IOException {
        String url = user.getAvatarUrl() == null ? user.getDefaultAvatarUrl() : user.getAvatarUrl();
        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", Constants.USER_AGENT);

        return ImageIO.read(connection.getInputStream());
    }

    public static BufferedImage resize(BufferedImage source, int width, int height) {
        Image scaled = source.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = resized.createGraphics();
        g.drawImage(scaled, 0, 0,null);
        g.dispose();

        return resized;
    }
}
