package me.bramhaag.discordselfbot.commands;

import me.bramhaag.discordselfbot.Constants;
import me.bramhaag.discordselfbot.util.Util;
import me.bramhaag.discordselfbot.commands.base.Command;
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

        try {
            Object rawOutput = engine.eval(String.format("with (imports) { %s }", input));
            output = rawOutput == null ? "null" : rawOutput.toString();

            color = Color.GREEN;
        } catch (ScriptException e) {
            output = e.getMessage();

            color = Color.RED;
        }

        channel.sendMessage(new EmbedBuilder()
                .setTitle("Evaluate", null)
                .setDescription(new MessageBuilder().append("Input:\n")
                                                    .appendCodeBlock(input, "javascript")
                                                    .append("Output:\n")
                                                    .appendCodeBlock(output, "javascript")
                                                    .build().getRawContent())
                .setColor(color)
                .build()).queue();
    }
}
