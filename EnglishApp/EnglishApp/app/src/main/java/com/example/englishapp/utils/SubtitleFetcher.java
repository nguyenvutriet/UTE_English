package com.example.englishapp.utils;


import com.example.englishapp.model.Subtitle;

import java.net.URL;
import java.net.HttpURLConnection;

import java.util.ArrayList;
import java.util.List;

public class SubtitleFetcher {

    public static List<Subtitle> fetch(String videoId) {

        try {

            // thử subtitle chuẩn
            String url =
                    "https://video.google.com/timedtext?lang=en&v=" + videoId;

            HttpURLConnection conn =
                    (HttpURLConnection) new URL(url).openConnection();

            List<Subtitle> list =
                    SubtitleParser.parse(conn.getInputStream());

            if (!list.isEmpty())
                return list;

            // thử en-US
            url =
                    "https://video.google.com/timedtext?lang=en-US&v=" + videoId;

            conn =
                    (HttpURLConnection) new URL(url).openConnection();

            list =
                    SubtitleParser.parse(conn.getInputStream());

            if (!list.isEmpty())
                return list;

            // thử auto caption
            url =
                    "https://video.google.com/timedtext?lang=a.en&v=" + videoId;

            conn =
                    (HttpURLConnection) new URL(url).openConnection();

            list =
                    SubtitleParser.parse(conn.getInputStream());

            return list;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return new ArrayList<>();
    }
}