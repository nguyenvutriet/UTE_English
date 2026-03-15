package com.example.englishapp.utils;


import com.example.englishapp.model.Video;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class YouTubeProvider {

    private static final String API_KEY = "AIzaSyBcSYdCFFOseGM3YeKg8RNVR3te2-uNG_I";

    public interface Callback {
        void onSuccess(List<Video> videos);
        void onError(Exception e);
    }

    public interface ChannelCallback {
        void onSuccess(String iconUrl);
        void onError(Exception e);
    }

    public static void fetchVideos(String channelId, Callback callback) {

        new Thread(() -> {

            try {

                List<Video> videoList = new ArrayList<>();

                String urlString =
                        "https://www.googleapis.com/youtube/v3/search"
                                + "?part=snippet"
                                + "&channelId=" + channelId
                                + "&type=video"
                                + "&videoEmbeddable=true"
                                + "&maxResults=20"
                                + "&key=" + API_KEY;

                URL url = new URL(urlString);

                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));

                StringBuilder json = new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {

                    json.append(line);
                }

                JSONObject root =
                        new JSONObject(json.toString());

                JSONArray items = root.getJSONArray("items");

                for (int i = 0; i < items.length(); i++) {

                    JSONObject item = items.getJSONObject(i);

                    JSONObject snippet =
                            item.getJSONObject("snippet");

                    String videoId =
                            item.getJSONObject("id")
                                    .getString("videoId");

                    String title =
                            snippet.getString("title");

                    String thumbnail =
                            snippet.getJSONObject("thumbnails")
                                    .getJSONObject("high")
                                    .getString("url");

                    videoList.add(
                            new Video(videoId, title, thumbnail));
                }

                callback.onSuccess(videoList);

            } catch (Exception e) {

                callback.onError(e);
            }

        }).start();
    }

    public static void fetchChannelIcon(String channelId, ChannelCallback callback) {

        new Thread(() -> {

            try {

                String urlString =
                        "https://www.googleapis.com/youtube/v3/channels"
                                + "?part=snippet"
                                + "&id=" + channelId
                                + "&key=" + API_KEY;

                URL url = new URL(urlString);

                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));

                StringBuilder json = new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {

                    json.append(line);
                }

                JSONObject root =
                        new JSONObject(json.toString());

                JSONArray items =
                        root.getJSONArray("items");

                if (items.length() > 0) {

                    JSONObject snippet =
                            items.getJSONObject(0)
                                    .getJSONObject("snippet");

                    String iconUrl =
                            snippet.getJSONObject("thumbnails")
                                    .getJSONObject("default")
                                    .getString("url");

                    callback.onSuccess(iconUrl);
                }

            } catch (Exception e) {

                callback.onError(e);
            }

        }).start();
    }
}