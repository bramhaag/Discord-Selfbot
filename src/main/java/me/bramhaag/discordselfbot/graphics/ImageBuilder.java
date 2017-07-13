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

package me.bramhaag.discordselfbot.graphics;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageBuilder {

    private BufferedImage source;

    public ImageBuilder(@NotNull File source) throws IOException {
        this.source = ImageIO.read(source);
    }

    public ImageBuilder(int width, int height) {
        source = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    @NotNull
    public ImageBuilder addImage(@NotNull BufferedImage image, int x, int y) throws IOException {
        source.getGraphics().drawImage(image, x, y, null);

        return this;
    }

    @NotNull
    public ImageBuilder addImage(@NotNull File file, int x, int y) throws IOException {
        return addImage(ImageIO.read(file), x, y);
    }

    @NotNull
    public byte[] create() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(source, "png", out);
        return out.toByteArray();
    }

    @NotNull
    public BufferedImage createImage() {
        return source;
    }
}
