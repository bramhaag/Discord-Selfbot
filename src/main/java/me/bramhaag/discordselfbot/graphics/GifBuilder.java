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
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class GifBuilder {
    private final int FRAME_DURATION;

    private Map<BufferedImage, Integer> frameQueue = new LinkedHashMap<>();

    public GifBuilder() {
        this.FRAME_DURATION = 100;
    }

    public GifBuilder(int frameDuration) {
        this.FRAME_DURATION = frameDuration;
    }

    @NotNull
    public GifBuilder addFrame(@NotNull BufferedImage image) {
        return addFrame(image, FRAME_DURATION);
    }

    @NotNull
    public GifBuilder addFrame(@NotNull BufferedImage image, Integer duration) {
        frameQueue.put(image, duration);

        return this;
    }

    @NotNull
    public byte[] create() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try(ImageOutputStream out = ImageIO.createImageOutputStream(bos);
            GifSequenceWriter writer = new GifSequenceWriter(out, BufferedImage.TYPE_INT_RGB, FRAME_DURATION, true)
        ) {
            for (Map.Entry<BufferedImage, Integer> entry : frameQueue.entrySet()) {
                for (int i = 0; i < entry.getValue() / FRAME_DURATION; i++) {
                    writer.writeToSequence(entry.getKey());
                }
            }
        }

        return bos.toByteArray();
    }
}
