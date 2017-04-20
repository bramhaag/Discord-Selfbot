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

package me.bramhaag.discordselfbot.util;

import lombok.Data;
import lombok.NonNull;
import me.bramhaag.discordselfbot.Constants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class JavadocParser {

    public static JavadocResult getJavadoc(String input) throws IOException {
        String c = input.split("/")[0];
        String method = input.split("/")[1];

        System.out.println(c);
        System.out.println(method);

        for(String javadocURL : Constants.JAVADOC_URLS) {
            Map.Entry<String, String> entry = getClasses(javadocURL).entrySet().parallelStream().filter(target -> target.getKey().equals(c)).findFirst().orElse(null);
            if(entry == null) {
                continue;
            }

            System.out.println("URL: " + entry.getValue() + "/" + entry.getKey());
            return getJavadoc(javadocURL, entry.getValue() + "/" + entry.getKey(), method);
        }

        return null;
    }

    public static JavadocResult getJavadoc(String path, String method) throws IOException {
        String javadocURL = Arrays.stream(Constants.JAVADOC_URLS)
                                  .filter(url -> checkJavadoc(String.format("%s%s.html", url, path)))
                                  .findAny().orElse(null);

        if(javadocURL == null) {
            return null;
        }

        javadocURL += path + ".html";
        return parseJavadoc(javadocURL, method);
    }

    public static JavadocResult getJavadoc(String url, String path, String method) throws IOException {
        //System.out.println(url + path + ".html");
        return parseJavadoc(url + path + ".html", method);
    }

    public static List<String> getPackages(String url) {
        return null;
    }

    private static Map<String, String> getClasses(String url) throws IOException {
        Document doc = Jsoup.connect(url + "allclasses-frame.html").get();
        Elements classes = doc.select("div.indexContainer ul li");

        Map<String, String> map = new LinkedHashMap<>();
        classes.forEach(c -> {
            Element child = c.child(0);

            String link = child.attr("href");
            map.put(child.text(), link.substring(0, link.lastIndexOf("/")));
        });

        return map;
    }

    private static JavadocResult parseJavadoc(String url, String method) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements details = doc.select("div.contentContainer div.details ul.blockList li.blockList ul.blockList li.blockList ul.blockList li.blockList");

        JavadocResult result = null;

        for(Element element : details) {
            String name = element.select("h4").text();
            //System.out.println(name);

            if(!name.equals(method)) {
                continue;
            }

            result = new JavadocResult(name, element.select("pre").text(), element.select("div.block").text());
            System.out.println(result);
            Element fields = element.select("dl").first();
            if(fields == null) {
                break;
            }

            Elements children = fields.children();

            String title = "";
            List<String> descriptions = new ArrayList<>();

            for (int i = 0; i < children.size(); i++) {
                Element child = children.get(i);

                if(child.tagName().equalsIgnoreCase("dt")) {
                    if(descriptions.size() != 0) {
                        result.fields.put(title, new ArrayList<>(descriptions));
                        descriptions.clear();
                    }

                    title = child.text().substring(0, child.text().length() - 1);
                }
                else if(child.tagName().equalsIgnoreCase("dd")) {
                    descriptions.add(child.text());

                    if(i == children.size() - 1) {
                        result.fields.put(title, new ArrayList<>(descriptions));
                    }
                }
            }
        }

        return result;
    }

    private static String parseName(String input) {
        return input.replace("\\.", "/");
    }

    private static boolean checkJavadoc(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("HEAD");
            connection.setRequestProperty("User-Agent", Constants.USER_AGENT);

            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }

    @Data
    public static class JavadocResult {
        @NonNull
        private String name;

        @NonNull
        private String format;

        @NonNull
        private String description;

        public Map<String, List<String>> fields = new LinkedHashMap<>();
    }
}
