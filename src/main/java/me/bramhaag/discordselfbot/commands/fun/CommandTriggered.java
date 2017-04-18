package me.bramhaag.discordselfbot.commands.fun;

import com.google.common.base.Preconditions;
import magick.DrawInfo;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import me.bramhaag.discordselfbot.commands.Command;
import me.bramhaag.discordselfbot.util.Util;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandTriggered {

    @Command(name = "triggered", aliases = { "trigger", "triggering" }, minArgs = 1)
    public void execute(Message message, TextChannel channel, String[] args) {
        if(message.getMentionedUsers().size() == 0) {
            Util.sendError(message, "Invalid user!");
            return;
        }

        User user = message.getMentionedUsers().get(0);

        File avatar = new File("avatar_" + user.getId() + "_" + System.currentTimeMillis() + ".png");
        try {
            ImageIO.write(Util.getImage(user.getAvatarUrl()), "png", avatar);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File output = new File("triggered_" + user.getId() + "_" + System.currentTimeMillis() + ".gif");
        File triggered = new File("assets/triggered.png");

        String avatarPath =    avatar.getAbsolutePath();
        String triggeredPath = triggered.getAbsolutePath();


        try {
            //TODO path work pls
            Process process = Runtime.getRuntime().exec(("C:/Program Files/ImageMagick-7.0.5-Q16/magick.exe convert canvas:none -size 512x680 -resize 512x680! -draw \"image over -60,-60 640,640 \"\"{avatar}\"\"\" -draw \"image over 0,512 0,0 \"\"{triggered}\"\"\" " +
                    "( canvas:none -size 512x680! -draw \"image over -45,-50 640,640 \"\"{avatar}\"\"\" -draw \"image over -5,512 0,0 \"\"{triggered}\"\"\" ) " +
                    "( canvas:none -size 512x680! -draw \"image over -50,-45 640,640 \"\"{avatar}\"\"\" -draw \"image over -1,505 0,0 \"\"{triggered}\"\"\" )  " +
                    "( canvas:none -size 512x680! -draw \"image over -45,-65 640,640 \"\"{avatar}\"\"\" -draw \"image over -5,530 0,0 \"\"{triggered}\"\"\" ) " +
                    "-layers Optimize -set delay 2 " + output.getPath()).replace("{avatar}", avatarPath).replace("{triggered}", triggeredPath));

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            process.destroy();

            message.getChannel().sendFile(output, new MessageBuilder().append(" ").build()).queue(m -> {
                message.delete().queue();

                Preconditions.checkState(avatar.delete(), String.format("File %s not deleted!", avatar.getName()));
                Preconditions.checkState(output.delete(), String.format("File %s not deleted!", output.getName()));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
