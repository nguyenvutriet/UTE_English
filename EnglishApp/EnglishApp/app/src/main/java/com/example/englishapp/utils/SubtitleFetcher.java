package com.example.englishapp.utils;


import android.util.Log;


import com.example.englishapp.model.Subtitle;
import com.example.englishapp.model.SubtitleTrack;
import com.example.englishapp.model.VideoInfo;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Caption;
import com.google.api.services.youtube.model.CaptionListResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubtitleFetcher {

    private static final String TAG = "SubtitleFetcher";

    // ─────────────────────────────────────────────────────────────────────────
    //  Phương thức đồng bộ (gọi trong background thread)
    //  Thực thi chính xác theo tài liệu Java Client (từ prompt của tài khoản)
    // ─────────────────────────────────────────────────────────────────────────

    public static List<Subtitle> fetch(android.content.Context context, String videoId) {
        Log.d(TAG, "Fetching subtitles for video: " + videoId);

        try {
            YouTube youtube = YouTubeService.getService(context);
            String apiKey = YouTubeService.getApiKey();

            // 1. Fetch Caption List
            YouTube.Captions.List captionsListRequest = youtube.captions().list(Collections.singletonList("id,snippet"), videoId);
            captionsListRequest.setKey(apiKey);

            CaptionListResponse listResponse = captionsListRequest.execute();
            List<Caption> captions = listResponse.getItems();

            if (captions == null || captions.isEmpty()) {
                Log.w(TAG, "No captions found for this video.");
                return new ArrayList<>();
            }

            // Tìm caption tối ưu: Tiếng Việt (vietnamese) hoặc lấy cái đầu tiên
            Caption selectedCaption = null;
            for (Caption c : captions) {
                String language = c.getSnippet().getLanguage();
                if (language != null && language.toLowerCase().contains("vi")) {
                    selectedCaption = c;
                    break;
                }
            }
            if (selectedCaption == null) {
                // Ưu tiên tiếng Anh
                for (Caption c : captions) {
                    String language = c.getSnippet().getLanguage();
                    if (language != null && language.toLowerCase().contains("en")) {
                        selectedCaption = c;
                        break;
                    }
                }
            }
            if (selectedCaption == null) {
                selectedCaption = captions.get(0);
            }

            String captionId = selectedCaption.getId();
            Log.d(TAG, "Selected caption ID: " + captionId + ", lang: " + selectedCaption.getSnippet().getLanguage());

            // 2. Download Caption Track (srt)
            YouTube.Captions.Download downloadRequest = youtube.captions().download(captionId);
            downloadRequest.setTfmt("srt");
            downloadRequest.setKey(apiKey);

            InputStream inputStream = downloadRequest.executeAsInputStream();
            String transcript = inputStreamToString(inputStream);

            if (transcript == null || transcript.isEmpty()) {
                Log.w(TAG, "Transcript text is empty.");
                return new ArrayList<>();
            }

            // 3. Parse SRT content
            return SubtitleParser.parse(transcript);

        } catch (Exception e) {
            Log.e(TAG, "Error fetching YouTube Subtitle API: " + e.getMessage(), e);
        }

        return new ArrayList<>();
    }

    private static String inputStreamToString(InputStream is) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "inputStreamToString err: " + e.getMessage());
            return "";
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PUBLIC API – Bất đồng bộ (Mô phỏng lại cho UI cũ chạy được)
    // ─────────────────────────────────────────────────────────────────────────

    public static void fetchAvailableTracks(android.content.Context context, String videoId, YouTubeSubtitleApi.VideoInfoCallback callback) {
        new Thread(() -> {
            try {
                YouTube youtube = YouTubeService.getService(context);
                String apiKey = YouTubeService.getApiKey();

                YouTube.Captions.List captionsListRequest = youtube.captions().list(Collections.singletonList("id,snippet"), videoId);
                captionsListRequest.setKey(apiKey);

                CaptionListResponse listResponse = captionsListRequest.execute();
                List<Caption> captions = listResponse.getItems();

                if (captions == null || captions.isEmpty()) {
                    callback.onError(new Exception("No captions found for this video."));
                    return;
                }

                List<SubtitleTrack> tracks = new ArrayList<>();
                for (Caption c : captions) {
                    String langCode = c.getSnippet().getLanguage();
                    String name = c.getSnippet().getName();
                    if (name == null || name.isEmpty()) name = langCode;

                    boolean isAuto = false;
                    try {
                        // isAuto có thể bị null hoặc false
                        Boolean trackKind = c.getSnippet().getIsAutoSynced();
                        isAuto = (trackKind != null && trackKind);
                    } catch (Exception ignored) {}

                    tracks.add(new SubtitleTrack(langCode, name, isAuto, c.getId())); // Lưu Caption ID vào chuỗi baseUrl
                }

                callback.onSuccess(new VideoInfo(videoId, "Video", "", tracks));
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    public static void fetchTrack(android.content.Context context, String videoId, SubtitleTrack track, YouTubeSubtitleApi.SubtitlesCallback callback) {
        new Thread(() -> {
            try {
                YouTube youtube = YouTubeService.getService(context);
                String apiKey = YouTubeService.getApiKey();

                String captionId = track.getBaseUrl(); // Lấy ID đã lưu trên

                YouTube.Captions.Download downloadRequest = youtube.captions().download(captionId);
                downloadRequest.setTfmt("srt");
                downloadRequest.setKey(apiKey);

                InputStream inputStream = downloadRequest.executeAsInputStream();
                String transcript = inputStreamToString(inputStream);

                if (transcript == null || transcript.isEmpty()) {
                    callback.onError(new Exception("Transcript is empty."));
                } else {
                    List<Subtitle> result = SubtitleParser.parse(transcript);
                    if (result.isEmpty()) {
                        callback.onError(new Exception("Could not parse transcript."));
                    } else {
                        callback.onSuccess(result);
                    }
                }
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }
}