package me.bramhaag.discordselfbot.commands;

import me.bramhaag.discordselfbot.Constants;
import me.bramhaag.discordselfbot.Util;
import me.bramhaag.discordselfbot.commands.base.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.util.Arrays;

public class CommandEvaluate {

    private ScriptEngineManager factory;
    private ScriptEngine engine;

    private String imports;

    public CommandEvaluate() {
        factory = new ScriptEngineManager();
        engine = factory.getEngineByName("nashorn");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("load(\"nashorn:mozilla_compat.js\");\n");
        Arrays.stream(Constants.EVAL_IMPORTS).forEach(s -> stringBuilder.append("importPackage(").append(s).append(")\n"));
        stringBuilder.append("\n");
        imports = stringBuilder.toString();
    }

    @Command(name = "evaluate", aliases = { "eval", "e" })
    public MessageEmbed execute(Message message, String[] args) {
        engine.put("jda", message.getJDA());

        engine.put("channel", message.getChannel());
        engine.put("guild", message.getGuild());

        engine.put("msg", message);

        engine.put("user", message.getAuthor());
        engine.put("member", message.getGuild().getMember(message.getAuthor()));
        engine.put("bot", message.getJDA().getSelfUser());

        String input = Util.combineArgs(args);
        String output;

        boolean success;

        try {
            Object rawOutput = engine.eval(imports + input);
            output = rawOutput == null ? "null" : rawOutput.toString();

            success = true;
        } catch (ScriptException e) {
            output = e.getMessage();

            success = false;
        }

        return new EmbedBuilder()
                .setTitle("Evaluate", null)
                .setDescription(new MessageBuilder().append("Input:\n")
                                                    .appendCodeBlock(input, "javascript")
                                                    .append("Output:\n")
                                                    .appendCodeBlock(output, "javascript")
                                .build().getRawContent())
                .setColor(success ? Color.GREEN : Color.RED)
        .build();
    }
}
