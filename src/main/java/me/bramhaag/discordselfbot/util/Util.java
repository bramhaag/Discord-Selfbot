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

import com.google.common.base.Preconditions;
import lombok.NonNull;
import me.bramhaag.discordselfbot.Constants;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

//TODO for meme -> HTML file -> Image -> Send
public class Util {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd-MM-Y H:mm a");

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
    private static BufferedImage getImage(@NonNull String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", Constants.USER_AGENT);

        return ImageIO.read(connection.getInputStream());
    }

    public static void generateGif(String[] args, Message message, String image) {
        User user = message.getMentionedUsers().get(0);

        File avatar = new File("avatar_" + user.getId() + "_" + System.currentTimeMillis() + ".png");
        try {
            ImageIO.write(Util.getImage(user.getAvatarUrl()), "png", avatar);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File output = new File("triggered_" + user.getId() + "_" + System.currentTimeMillis() + ".gif");
        File triggered = new File("assets/" + image);

        String avatarPath    = avatar.getAbsolutePath();
        String triggeredPath = triggered.getAbsolutePath();

        String text = args.length == 1 ? null : Util.combineArgs(Arrays.copyOfRange(args, 1, args.length));
        String magickPath = "C:/Program Files/ImageMagick-7.0.5-Q16/magick.exe";

        new Thread(() -> {
            try {
                //TODO path work pls
                //EDIT fuck that I'll make a config file
                Process generateGif = Runtime.getRuntime().exec((magickPath + " convert canvas:none -size 512x680 -resize 512x680! -draw \"image over -60,-60 640,640 \"\"{avatar}\"\"\" -draw \"image over 0,512 0,0 \"\"{triggered}\"\"\" " +
                        "( canvas:none -size 512x680! -draw \"image over -45,-50 640,640 \"\"{avatar}\"\"\" -draw \"image over -5,512 0,0 \"\"{triggered}\"\"\" ) " +
                        "( canvas:none -size 512x680! -draw \"image over -50,-45 640,640 \"\"{avatar}\"\"\" -draw \"image over -1,505 0,0 \"\"{triggered}\"\"\" )  " +
                        "( canvas:none -size 512x680! -draw \"image over -45,-65 640,640 \"\"{avatar}\"\"\" -draw \"image over -5,530 0,0 \"\"{triggered}\"\"\" ) " +
                        "-layers Optimize -set delay 2 " + output.getPath()).replace("{avatar}", avatarPath).replace("{triggered}", triggeredPath));

                //Debug
                /*String s;

                BufferedReader stdInput = new BufferedReader(new InputStreamReader(generateGif.getErrorStream()));
                while ((s = stdInput.readLine()) != null) {
                    System.out.println(s);
                }*/

                generateGif.waitFor();

                if(text != null) {
                    Process addText = Runtime.getRuntime().exec(String.format("%s convert %s -font Calibri -pointsize 60 caption:\"%s\" %s", magickPath, output, text, output));
                    addText.waitFor();
                }

                message.getChannel().sendFile(output, new MessageBuilder().append(" ").build()).queue(m -> {
                    message.delete().queue();

                    Preconditions.checkState(avatar.delete(), String.format("File %s not deleted!", avatar.getName()));
                    Preconditions.checkState(output.delete(), String.format("File %s not deleted!", output.getName()));
                });
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        message.editMessage("```Generating GIF...```").queue();
    }
}
