package me.bramhaag.discordselfbot.util;

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
import java.util.concurrent.TimeUnit;

//TODO for meme -> HTML file -> Image -> Send
public class Util {

    /**
     * Combine {@code args}
     * @param args {@code String[]} to combine
     * @return Combined {@code args}
     */
    public static String combineArgs(String[] args) {
        return StringUtils.join(args, ' ');
    }


    public static void sendEmbed(MessageEmbed embed, TextChannel channel, User author) {
        sendEmbedAfter(embed, channel, author, 0);
    }

    public static void sendEmbedAfter(MessageEmbed embed, TextChannel channel, User author, long delay) {
        if (channel.getGuild().getMember(author).hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) {
            channel.sendMessage(embed).queueAfter(delay, TimeUnit.SECONDS);
        } else {
            channel.sendMessage(embed.getDescription()).queueAfter(delay, TimeUnit.SECONDS);
        }
    }

    /**
     * Edit message to {@code embed}.
     * When the sender does not have permission to send embeds, send {@link MessageEmbed#getDescription()}
     * @param message Message to edit
     * @param embed Embed
     */
    public static void editEmbed(Message message, MessageEmbed embed) {
        editEmbedAfter(message, embed, 0);
    }

    /**
     *
     * @param message
     * @param embed
     * @param delay Delay in seconds
     */
    public static void editEmbedAfter(Message message, MessageEmbed embed, long delay) {
        if(message.getGuild().getMember(message.getAuthor()).hasPermission(message.getTextChannel(), Permission.MESSAGE_EMBED_LINKS)) {
            message.editMessage(embed).queueAfter(delay, TimeUnit.SECONDS);
        } else {
            message.editMessage(embed.getDescription()).queueAfter(delay, TimeUnit.SECONDS);
        }
    }

    public static void editMessageAutoDelete(Message message, String edit, long time) {
        message.editMessage(edit).queue(m -> m.delete().queueAfter(time, TimeUnit.SECONDS));
    }

    public static void editMessageError(Message message, String reason) {
        editMessageAutoDelete(message, new MessageBuilder().appendCodeBlock("An error occurred! " + reason, "javascript").build().getRawContent(), 5);
    }

    public static BufferedImage getImage(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        //BufferedImage image = ImageIO.read(connection.getInputStream());
        //ImageIO.write(image, "png", new File("avatar.png"));
        return ImageIO.read(connection.getInputStream());
    }
}
