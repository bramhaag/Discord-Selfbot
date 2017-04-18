package me.bramhaag.discordselfbot.commands.fun;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import me.bramhaag.discordselfbot.commands.Command;
import me.bramhaag.discordselfbot.util.Util;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CommandHTML {

    @Command(name = "html", minArgs = 1)
    public void execute(@NonNull Message message, @NonNull TextChannel channel, @NonNull String[] args) {
        String html;

        int width;
        int height;

        try {
            width = Integer.parseInt(args[0]);
            height = Integer.parseInt(args[1]);

            html = Util.combineArgs(Arrays.copyOfRange(args, 2, args.length));
        } catch (NumberFormatException e) {
            width = 1920;
            height = 1080;

            html = Util.combineArgs(args);
        }

        if(html.startsWith("```") && html.endsWith("```")) {
            html = html.substring(3, html.length() - 3);
        }

        html = html.startsWith("```") && html.endsWith("```") ? html.substring(3, html.length() - 3) : html;

        BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);
        Graphics graphics = image.createGraphics();

        JEditorPane jep = new JEditorPane("text/html", html);
        jep.setSize(width, height);
        jep.print(graphics);

        File file = new File("html_" + System.currentTimeMillis() + ".png");
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            channel.sendFile(file, new MessageBuilder().append(" ").build()).queue(m -> {
                message.delete().queue();

                Preconditions.checkState(file.delete(), String.format("File %s not deleted!", file.getName()));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}