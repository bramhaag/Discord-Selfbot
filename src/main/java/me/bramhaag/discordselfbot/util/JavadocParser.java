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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class JavadocParser {

    public static URL getUrlFromInput(String input) throws IOException {
        for (String s : Constants.JAVADOC_URLS) {
            URL url = new URL(String.format("%s%s.html", s, input));

            if(testConnection(url)) {
                return url;
            }
        }

        return null;
    }

    public static URL getUrlFromInput(URL searchUrl, String input) throws MalformedURLException {
        searchUrl = searchUrl.toString().endsWith("/") ? searchUrl : new URL(searchUrl + "/");
        return new URL(String.format("%s %s.html", searchUrl, input.replace(".", "/")));
    }

    private static boolean testConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", Constants.USER_AGENT);

        return connection.getResponseCode() == 200;

    }

    public static JavadocResult parseMethod(URL url, String method) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url.toString()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(doc == null) {
            return null;
        }

        Elements blockLinks = doc.select("div.contentContainer div.details ul.blockList li.blockList ul.blockList li.blockList ul.blockList li.blockList");

        JavadocResult result = null;

        for(Element element : blockLinks) {
            String name = element.select("h4").text();
            if(!name.equals(method)) {
                continue;
            }

            result = new JavadocResult(name, element.select("pre").text(), element.select("div.block").text());

            Element fields = element.select("dl").first();
            if(fields != null) {
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
        }

        return result;

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
