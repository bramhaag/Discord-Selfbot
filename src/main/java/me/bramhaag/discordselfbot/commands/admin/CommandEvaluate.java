package me.bramhaag.discordselfbot.commands.admin;

import me.bramhaag.discordselfbot.Constants;
import me.bramhaag.discordselfbot.util.Util;
import me.bramhaag.discordselfbot.commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;

public class CommandEvaluate {

    private ScriptEngineManager factory;
    private ScriptEngine engine;

    public CommandEvaluate() {
        factory = new ScriptEngineManager();
        engine = factory.getEngineByName("nashorn");

        try {
            engine.eval(String.format("var imports = new JavaImporter(%s);", StringUtils.join(Constants.EVAL_IMPORTS, ',')));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    @Command(name = "evaluate", aliases = { "eval", "e" })
    public void execute(Message message, TextChannel channel, String[] args) {
        engine.put("jda", message.getJDA());

        engine.put("channel", message.getChannel());
        engine.put("guild", message.getGuild());

        engine.put("msg", message);

        engine.put("user", message.getAuthor());
        engine.put("member", message.getGuild().getMember(message.getAuthor()));
        engine.put("bot", message.getJDA().getSelfUser());

        String input = Util.combineArgs(args);
        String output;

        Color color;

        long time;

        try {
            long currentNanos = System.nanoTime();
            Object rawOutput = engine.eval(String.format("with (imports) { %s }", input));
            time = System.nanoTime() - currentNanos;

            output = rawOutput == null ? "null" : rawOutput.toString();

            color = Color.GREEN;
        } catch (ScriptException e) {
            output = e.getMessage();
            time = 0;

            color = Color.RED;
        }

        message.editMessage(new EmbedBuilder()
                .setTitle("Evaluate", null)
                .addField("Input",  new MessageBuilder().appendCodeBlock(input, "javascript").build().getRawContent(), true)
                .addField("Output", new MessageBuilder().appendCodeBlock(output, "javascript").build().getRawContent(), true)
                .setFooter(time == 0 ? Constants.CROSS_EMOTE + " An error occurred" : String.format(Constants.CHECK_EMOTE + " Took %d ms (%d ns) to complete", (long)Math.floor(time / 1000000), time), null)
                .setColor(color)
                .build()).queue();
    }
}
