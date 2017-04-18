package me.bramhaag.discordselfbot.util;

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
    public static BufferedImage getImage(@NonNull String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", Constants.USER_AGENT);

        return ImageIO.read(connection.getInputStream());
    }
}
