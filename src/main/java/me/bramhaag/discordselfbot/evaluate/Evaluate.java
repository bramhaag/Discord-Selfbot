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

import com.google.common.base.Stopwatch;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import me.bramhaag.discordselfbot.Constants;
import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Evaluate {

    private static ScriptEngine engine;
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
                if(engine == null) {
                    engine = new ScriptEngineManager().getEngineByName("nashorn");
                }

                bindings.forEach(engine::put);

                String output;
                Stopwatch stopwatch = null;

                try {
                    engine.eval(String.format("var imports = new JavaImporter(%s);", StringUtils.join(Constants.EVAL_IMPORTS, ',')));

                    stopwatch = Stopwatch.createStarted();
                    Object rawOutput = engine.eval(String.format("with (imports) { %s }", input));
                    stopwatch.stop();

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
