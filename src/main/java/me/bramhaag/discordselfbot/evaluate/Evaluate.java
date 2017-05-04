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

package me.bramhaag.discordselfbot.evaluate;

import bsh.EvalError;
import bsh.Interpreter;
import com.google.common.base.Stopwatch;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import me.bramhaag.discordselfbot.Constants;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import org.apache.commons.lang3.StringUtils;
import org.python.util.PythonInterpreter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Evaluate {

    private static ScriptEngine javascriptEngine;

    private static Interpreter javaInterpreter;
    private static PythonInterpreter pythonInterpreter;

    private static GroovyShell groovyShell;

    @Data
    public static class Result {

        @NonNull
        private final String output;

        private final Stopwatch stopwatch;
    }

    public enum Language {
        JAVASCRIPT("js") {
            @Override
            public Result evaluate(Map<String, Object> bindings, String input) {
                if(javascriptEngine == null) {
                    javascriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
                }

                bindings.forEach(javascriptEngine::put);

                String output;
                Stopwatch stopwatch = null;

                try {
                    javascriptEngine.eval(String.format("var imports = new JavaImporter(%s);", StringUtils.join(Constants.EVAL_IMPORTS, ',')));

                    stopwatch = Stopwatch.createStarted();
                    Object rawOutput = javascriptEngine.eval(String.format("with (imports) { %s }", input));
                    stopwatch.stop();

                    System.out.println(rawOutput);
                    output = rawOutput == null ? "null" : rawOutput.toString();
                } catch (ScriptException e) {
                    output = e.getMessage();
                }

                return new Result(output, stopwatch);

            }
        },
        GROOVY("g") {
            @Override
            public Result evaluate(Map<String, Object> bindings, String input) {
                if(groovyShell == null) {
                    Binding binding = new Binding();
                    bindings.forEach(binding::setProperty);
                    groovyShell = new GroovyShell(binding);
                } else {
                    bindings.forEach(groovyShell::setProperty);
                }


                Object rawOutput;
                Stopwatch stopwatch = null;

                try {
                    stopwatch = Stopwatch.createStarted();
                    rawOutput = groovyShell.evaluate(input);
                    stopwatch.stop();
                } catch(Exception e) {
                    rawOutput = e.getMessage();
                }

                return new Result(rawOutput == null ? "null" : rawOutput.toString(), stopwatch);
            }
        },
        JAVA {
            public Result evaluate(Map<String, Object> bindings, String input) {
                if (javaInterpreter == null) {
                    javaInterpreter = new Interpreter();
                }

                Object rawOutput;
                Stopwatch stopwatch = null;

                try {
                    for (Map.Entry<String, Object> entry : bindings.entrySet()) {
                        javaInterpreter.set(entry.getKey(), entry.getValue());
                    }

                    stopwatch = Stopwatch.createStarted();
                    rawOutput = javaInterpreter.eval(input);
                    stopwatch.stop();
                } catch (EvalError e) {
                    rawOutput = e.getMessage();
                }

                return new Result(rawOutput == null ? "null" : rawOutput.toString(), stopwatch);
            }
        },
        PYTHON("py") {
            @Override
            public Result evaluate(Map<String, Object> bindings, String input) {
                if(pythonInterpreter == null) {
                    pythonInterpreter = new PythonInterpreter();
                }

                bindings.forEach(pythonInterpreter::set);

                String output;
                Stopwatch stopwatch = null;

                try {
                    stopwatch = Stopwatch.createStarted();
                    Object rawOutput = pythonInterpreter.eval(input).__tojava__(Object.class);
                    stopwatch.stop();

                    output = rawOutput == null ? "null" : rawOutput.toString();
                } catch (Exception e) {
                    output = e.getClass() + ": " + e.getMessage();
                }

                return new Result(output, stopwatch);
            }
        };

        @Getter
        private List<String> aliases;

        Language(String... aliases) {
            this.aliases = Arrays.asList(aliases);
        }

        public Result evaluate(Map<String, Object> bindings, String input) {
            throw new AbstractMethodError("Method not overridden!");
        }

        public static Language getLanguage(String input) {
            return Arrays.stream(Language.values()).filter(language -> language.name().equalsIgnoreCase(input) || language.getAliases().contains(input.toLowerCase())).findFirst().orElse(null);
        }
    }
}
