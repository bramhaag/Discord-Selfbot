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

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.io.CharStreams;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.bramhaag.discordselfbot.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Speedtest {

    private final static String SPEEDTEST_CONFIG = "https://www.speedtest.net/speedtest-config.php";
    private final static String SPEEDTEST_API = "https://www.speedtest.net/api/api.php";
    private final static String SPEEDTEST_SERVERS = "http://c.speedtest.net/speedtest-servers.php";
    private final static String SPEEDTEST_SERVERS_STATIC = "http://c.speedtest.net/speedtest-servers-static.php";

    private Config config;
    private List<Server> servers = new ArrayList<>();

    private long ping;
    private long download;
    private long upload;

    private long bytesReceived;
    private long bytesSent;

    private String serverId;

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

    public List<Server> getServers() throws IOException, ParserConfigurationException, SAXException {
        if(!this.servers.isEmpty())
            return this.servers;

        getServersFromUrl(SPEEDTEST_SERVERS);
        getServersFromUrl(SPEEDTEST_SERVERS_STATIC);

        return this.servers;
    }

    private void getServersFromUrl(String url) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new URL(url).openStream());

        NodeList xmlServers = doc.getElementsByTagName("server");
        for(int i = 0; i < xmlServers.getLength(); i++) {
            Node server = xmlServers.item(i);
            NamedNodeMap attributes = server.getAttributes();

            Node id = attributes.getNamedItem("id");
            if(id == null || Arrays.asList(getConfig().getIgnoreServers()).contains(id.getNodeValue())) {
                continue;
            }

            float lat = Float.valueOf(attributes.getNamedItem("lat").getNodeValue());
            float lon = Float.valueOf(attributes.getNamedItem("lon").getNodeValue());

            this.servers.add(new Server(id.getNodeValue(), attributes.getNamedItem("url").getNodeValue(), lat, lon));
        }
    }

    public Server getClosestServer() throws ParserConfigurationException, SAXException, IOException {
        float lat = Float.valueOf(getConfig().getClient().getNamedItem("lat").getNodeValue());
        float lon = Float.valueOf(getConfig().getClient().getNamedItem("lon").getNodeValue());

        float lowestDistance = -1;
        Server server = null;
        for(Server s : getServers()) {
            float distance = distance(s.getLatitude(), s.getLongitude(), lat, lon);

            if(lowestDistance == -1) {
                server = s;
                lowestDistance = distance;
                continue;
            }

            if(lowestDistance > distance) {
                server = s;
                lowestDistance = distance;
            }
        }

        return server;
    }

    public void testDownload(Consumer<Long> callback) throws IOException, SAXException, ParserConfigurationException {
        List<String> urls = new ArrayList<>();

        for(long size : getConfig().getDownloadSizes()) {
            for(int i = 0;  i < getConfig().downloadCount; i++) {
                urls.add(String.format("%s/random%sx%s.jpg", getClosestServer().getUrl(), size, size));
            }
        }

        new Thread(() -> {
            long received = 0;
            Stopwatch stopwatch = Stopwatch.createUnstarted();
            for (int i = 0; i < urls.size(); i++) {
                if(i == 0) stopwatch.start();
                String url = urls.get(i);

                try {
                    HttpURLConnection connection = createConnection(url, null, String.valueOf(i));
                    received = connection.getContentLengthLong();

                    connection.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            stopwatch.stop();

            callback.accept(received / stopwatch.elapsed(TimeUnit.SECONDS));
        }).start();

    }
    public String getShareUrl() throws IOException, NoSuchAlgorithmException {
        String[] data = new String[] {
                "recommendedserverid=" + serverId,
                "ping=" + ping,
                "screenresolution=",
                "promo=",
                "download=" + download,
                "screendpi=",
                "upload=" + upload,
                "testmethod=http",
                "hash=" + StringUtil.toMD5Hex(String.format("%s-%s-%s-%s", ping, upload, download, "297aae72")),
                "touchscreenmode=none",
                "startmode=pingselect",
                "accuracy=1",
                "bytesreceived=" + bytesReceived,
                "bytessent=" + bytesSent,
                "serverid=" + serverId
        };

        HttpURLConnection connection = createConnection(SPEEDTEST_API, data);

        //TODO checks

        return CharStreams.toString(new InputStreamReader(
                connection.getInputStream(), Charsets.UTF_8));
    }

    private HttpURLConnection createConnection(String url, String... data) throws IOException {
        return createConnection(url, data, null);
    }

    private HttpURLConnection createConnection(String url, String[] data, String bump) throws IOException {
        URL u = new URL(String.format("%s?x=%s.%s",url, System.currentTimeMillis(), bump == null ? "" : bump));
        System.out.println(u);
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();

        connection.setRequestMethod("POST");

        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Referer", "http://c.speedtest.net/flash/speedtest.swf");

        connection.setDoOutput(true);
        connection.setUseCaches(false);

        if(data != null) {
            String stringData = String.join("&", data);
            connection.setRequestProperty("Content-Length", Integer.toString(stringData.length()));

            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(stringData.getBytes());
            }
        }

        return connection;
    }


    public static float distance(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return (float) (earthRadius * c);
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

        private Config() throws ParserConfigurationException, IOException, SAXException {
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

    @Data
    @AllArgsConstructor
    public class Server {
        private String id;
        private String url;

        private float latitude;
        private float longitude;
    }
}
