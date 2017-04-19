package me.bramhaag.discordselfbot.commands.util;

import lombok.NonNull;
import me.bramhaag.discordselfbot.commands.Command;
import me.bramhaag.discordselfbot.util.JavadocParser;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class CommandJavadoc {

    @Command(name = "javadoc", aliases = { "doc", "jd" }, minArgs = 1)
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        String[] parts = args[0].split("\\.");

        String path = StringUtils.join(Arrays.copyOf(parts, parts.length - 1), "/");
        String method = parts[parts.length - 1];

        URL url;
        JavadocParser.JavadocResult result;

        try {
            url = JavadocParser.getUrlFromInput(path);
            result = JavadocParser.parseMethod(url, method);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if(url == null || result == null) {
            return;
        }

        EmbedBuilder builder = new EmbedBuilder().setTitle("Javadoc", url.toString());
        builder.addField("Name", code(result.getName()), false);
        builder.addField("Format", code(result.getFormat()), false);
        builder.addField("Description", code(result.getDescription()), false);

        result.getFields().forEach((key, value) -> builder.addField(key, code(StringUtils.join(value, "\n")), false));

        channel.sendMessage(builder.build()).queue();
    }

    private String code(String input) {
        return "```" + input + "```";
    }
}
