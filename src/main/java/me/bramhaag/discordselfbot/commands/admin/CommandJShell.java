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
import jdk.jshell.DeclarationSnippet;
import jdk.jshell.Diag;
import jdk.jshell.EvalException;
import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import jdk.jshell.SnippetEvent;
import jdk.jshell.UnresolvedReferenceException;
import me.bramhaag.bcf.CommandContext;
import me.bramhaag.bcf.annotations.Command;
import me.bramhaag.bcf.annotations.CommandBase;
import me.bramhaag.discordselfbot.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Command("jshell")
public class CommandJShell {

    private JShell jShell;
    private Pattern LINEBREAK = Pattern.compile("\\R");

    public CommandJShell() {
        jShell = JShell.builder().build();
                List.of("java.util.*", "java.io.*", "java.math.*", "java.net.*", "java.util.concurrent.*", "java.util.prefs.*", "java.util.regex.*", "java.util.stream.*")
                    .stream().map(i -> "import " + i + ";").forEach(i -> jShell.eval(i));
    }

    @CommandBase
    public void execute(@NotNull CommandContext context, String[] args) {
        String input = String.join(" ", args);
        input = input.startsWith("```") && input.endsWith("```") ? input.substring(3, input.length() - 3) : input;

        String output;
        Color color = Color.RED;
        Stopwatch stopwatch = Stopwatch.createStarted();

        SnippetEvent snippet = jShell.eval(input).get(0);

        String error = String.join("\n", handleEvent(snippet));
        if(error != null && !error.isEmpty()) {
            output = error;
            stopwatch = Stopwatch.createUnstarted();
        } else {
            output = snippet.value();
            stopwatch.stop();

            color = Color.GREEN;
        }

        context.getMessage().editMessage(EmbedUtil.addDefaults(new EmbedBuilder()
                .setTitle("JShell", null)
                .addField("Input",  new MessageBuilder().appendCodeBlock(input, "java").build().getRawContent(), true)
                .addField("Output", new MessageBuilder().appendCodeBlock(output, "java").build().getRawContent(), true)
                .setColor(color), String.format("Took %d ms (%d ns) to complete", stopwatch.elapsed(TimeUnit.MILLISECONDS), stopwatch.elapsed(TimeUnit.NANOSECONDS)), stopwatch.elapsed(TimeUnit.NANOSECONDS) != 0).build()).queue();

    }

    private List<String> handleEvent(SnippetEvent ste) {
        List<String> output = new ArrayList<>();
        Snippet sn = ste.snippet();

        if (sn == null) {
            output.add("Event with null key: " + ste);
            return output;
        }

        List<Diag> diagnostics = jShell.diagnostics(sn).collect(toList());
        String source = sn.source();

        if(ste.causeSnippet() != null) {
            return output;
        }

        for (Diag d : diagnostics) {
            output.add(d.isError() ? "jshell.msg.error" : "jshell.msg.warning");
            List<String> disp = new ArrayList<>();
            displayDiagnostics(source, d, disp);
            output.addAll(disp);
        }

        if (ste.status() != Snippet.Status.REJECTED) {
            if (ste.exception() != null) {
                if (ste.exception() instanceof EvalException) {
                    EvalException ex = (EvalException) ste.exception();
                    if (ex.getMessage() == null) {
                        output.add(ex.getExceptionClassName() + " thrown");
                    } else {
                        output.add(ex.getExceptionClassName() + " thrown: " + ex.getMessage());
                    }

                    output.add(getStacktrace(ex));
                    return output;
                } else if (ste.exception() instanceof UnresolvedReferenceException) {
                    UnresolvedReferenceException ex = (UnresolvedReferenceException) ste.exception();
                    jShell.diagnostics(ex.getSnippet()).map(i -> i.getMessage(Locale.getDefault())).forEach(output::add);
                    return output;
                } else {
                    output.add("Unexpected execution exception: " + ste.exception());
                    return output;
                }
            }
        } else {
            if (diagnostics.isEmpty()) {
                output.add("jshell.err.failed");
            }

            return output;
        }

        return output;
    }

    private void displayDiagnostics(String source, Diag diag, List<String> toDisplay) {
        Arrays.stream(diag.getMessage(null).split("\\r?\\n"))
                .filter(line -> !line.trim().startsWith("location:"))
                .forEach(toDisplay::add);

        int pstart = (int) diag.getStartPosition();
        int pend = (int) diag.getEndPosition();

        Matcher m = LINEBREAK.matcher(source);

        int pstartl = 0;
        int pendl = -2;

        while (m.find(pstartl)) {
            pendl = m.start();

            if (pendl >= pstart) {
                break;
            } else {
                pstartl = m.end();
            }
        }

        if (pendl < pstart) {
            pendl = source.length();
        }

        toDisplay.add(source.substring(pstartl, pendl));

        StringBuilder sb = new StringBuilder();

        int start = pstart - pstartl;

        for (int i = 0; i < start; ++i) {
            sb.append(' ');
        }

        sb.append('^');

        boolean multiline = pend > pendl;
        int end = (multiline ? pendl : pend) - pstartl - 1;

        if (end > start) {
            for (int i = start + 1; i < end; ++i) {
                sb.append('-');
            }

            sb.append(multiline ? "-..." : '^');
        }

        toDisplay.add(sb.toString());
    }

    private String getStacktrace(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
