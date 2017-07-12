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

package me.bramhaag.discordselfbot.commands.fun;

import me.bramhaag.bcf.CommandContext;
import me.bramhaag.bcf.annotations.Command;
import me.bramhaag.bcf.annotations.CommandBase;
import me.bramhaag.discordselfbot.graphics.ImageBuilder;

import java.io.IOException;

@Command("image")
public class CommandImageTest {

    @CommandBase
    public void execute(CommandContext context) {
        ImageBuilder builder;
        try {
            builder = new ImageBuilder(512, 712).addImage(null, 0, 0).addImage(null, 0, 512);
            context.getChannel().sendFile(builder.create(), "reeeeee", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
