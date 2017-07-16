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

package me.bramhaag.discordselfbot.speedtest;

import lombok.Data;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class Speedtest {

    private final static String SPEEDTEST_CONFIG = "https://www.speedtest.net/speedtest-config.php";

    private Config config;

    public Config getConfig() {
        if(config == null) {
            try {
                this.config = new Config();
            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
            }
        }

        return this.config;
    }

    public void getServers() {

    }

    public String getShareUrl() {
        //https://github.com/sivel/speedtest-cli/blob/master/speedtest.py#L653-L670
        return null;
    }

    @Data
    private class Config {
        private NamedNodeMap client;
        private String[] ignoreServers;

        private int[] downloadSizes;
        private int[] uploadSizes;

        private int downloadCount;
        private int uploadCount;

        private int downloadThread;
        private int uploadThread;

        private int downloadLength;
        private int uploadLength;

        public Config() throws ParserConfigurationException, IOException, SAXException {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(SPEEDTEST_CONFIG).openStream());

            NamedNodeMap serverConfig = doc.getElementsByTagName("server-config").item(0).getAttributes();
            NamedNodeMap download = doc.getElementsByTagName("download").item(0).getAttributes();
            NamedNodeMap upload = doc.getElementsByTagName("upload").item(0).getAttributes();

            this.client = doc.getElementsByTagName("client").item(0).getAttributes();
            this.ignoreServers = serverConfig.getNamedItem("ignoreids").getNodeValue().split(",");

            int ratio = Integer.valueOf(upload.getNamedItem("ratio").getNodeValue());
            int uploadMax = Integer.valueOf(upload.getNamedItem("maxchunkcount").getNodeValue());
            int[] fullUploadSizes = new int[] {32768, 65536, 131072, 262144, 524288, 1048576, 7340032};

            this.downloadSizes = new int[] {350, 500, 750, 1000, 1500, 2000, 2500, 3000, 3500, 4000};
            this.uploadSizes = Arrays.copyOfRange(fullUploadSizes, ratio - 1, fullUploadSizes.length);

            this.downloadCount = Integer.valueOf(download.getNamedItem("testlength").getNodeValue());
            this.uploadCount = (int)Math.ceil(uploadMax / this.uploadSizes.length);

            this.downloadThread = Integer.valueOf(serverConfig.getNamedItem("threadcount").getNodeValue()) * 2;
            this.uploadThread = Integer.valueOf(upload.getNamedItem("threads").getNodeValue());

            this.downloadLength = Integer.valueOf(upload.getNamedItem("testlength").getNodeValue());
            this.uploadLength = Integer.valueOf(upload.getNamedItem("testlength").getNodeValue());
        }
    }
}
