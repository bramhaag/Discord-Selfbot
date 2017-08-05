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

package me.bramhaag.discordselfbot.commands.admin;

import com.google.common.base.Stopwatch;
import me.bramhaag.bcf.CommandContext;
import me.bramhaag.bcf.annotations.Command;
import me.bramhaag.bcf.annotations.CommandBase;
import me.bramhaag.discordselfbot.util.Constants;
import me.bramhaag.discordselfbot.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.util.concurrent.TimeUnit;

@Command("evaluate")
public class CommandEvaluate {

    @NotNull
    private ScriptEngine engine;

    public CommandEvaluate() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");

        try {
            engine.eval(String.format("var imports = new JavaImporter(%s);", StringUtils.join(Constants.EVAL_IMPORTS, ',')));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }


    @CommandBase
    public void execute(@NotNull CommandContext context, String[] args) {
        Message message = context.getMessage();
        engine.put("jda", message.getJDA());

        engine.put("channel", message.getChannel());
        engine.put("guild", message.getGuild());

        engine.put("msg", message);

        engine.put("user", message.getAuthor());
        engine.put("member", message.getGuild().getMember(message.getAuthor()));
        engine.put("bot", message.getJDA().getSelfUser());

        String input = String.join(" ", args);
        input = input.startsWith("```") && input.endsWith("```") ? input.substring(3, input.length() - 3) : input;

        String output;

        Color color;

        Stopwatch stopwatch;

        try {
            stopwatch = Stopwatch.createStarted();
            Object rawOutput = engine.eval(String.format("with (imports) { %s }", input));
            stopwatch.stop();

            output = rawOutput == null ? "null" : rawOutput.toString();

            color = Color.GREEN;
        } catch (ScriptException e) {
            output = e.getMessage();
            stopwatch = Stopwatch.createUnstarted();

            color = Color.RED;
        }

        message.editMessage(EmbedUtil.addDefaults(new EmbedBuilder()
                .setTitle("Evaluate", null)
                .addField("Input",  new MessageBuilder().appendCodeBlock(input, "javascript").build().getRawContent(), true)
                .addField("Output", new MessageBuilder().appendCodeBlock(output, "javascript").build().getRawContent(), true)
                .setColor(color), String.format("Took %d ms (%d ns) to complete", stopwatch.elapsed(TimeUnit.MILLISECONDS), stopwatch.elapsed(TimeUnit.NANOSECONDS)), stopwatch.elapsed(TimeUnit.NANOSECONDS) != 0).build()).queue();
    }
}
