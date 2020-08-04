package ua.osadchiy.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public class Course {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
    Document document;
    String url;

    public Course(String url) {
        this.url = url;
        connect();
    }

    private void connect() {
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Date> getInfoAboutVideo() {
        Map<String, Date> data = new HashMap<>();
        Elements elements = document.getElementsByClass("ud-component--course-landing-page-udlite--curriculum");

        if (!elements.isEmpty()) {
            String componentPropsJson = elements
                    .get(0)
                    .dataset()
                    .get("component-props");

            if (componentPropsJson != null) {
                try {
                    JSONObject jsonObject = new JSONObject(componentPropsJson);
                    if (jsonObject.has("displayed_sections")) {
                        JSONArray displayed_sections = (JSONArray) jsonObject.get("displayed_sections");

                        displayed_sections.forEach(ds -> {
                            JSONObject displayed_section = (JSONObject) ds;

                            if (displayed_section.has("items")) {
                                JSONArray items = (JSONArray) displayed_section.get("items");
                                items.forEach(i -> {
                                    JSONObject item = (JSONObject) i;

                                    if (item.get("can_be_previewed") == Boolean.TRUE) {
                                        try {
                                            data.put(item.getString("title"),
                                                    formatter.parse(item.getString("content_summary")));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K, V>> st = map.entrySet().stream();

        st.sorted(Map.Entry.comparingByValue())
                .forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }

    public static void print(Map<String, Date> map) {
        for (Map.Entry<String, Date> pair: map.entrySet()) {
            System.out.println(pair.getKey() + " - " + formatter.format(pair.getValue()));
        }
    }
}
