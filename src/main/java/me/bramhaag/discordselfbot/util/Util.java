package me.bramhaag.discordselfbot.util;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

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
}
