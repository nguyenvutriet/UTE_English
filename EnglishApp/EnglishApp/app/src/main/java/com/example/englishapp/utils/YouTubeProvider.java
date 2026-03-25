package com.example.englishapp.utils;


import android.content.Context;
import android.util.Log;

import com.example.englishapp.model.Video;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YouTubeProvider {

    private static final String TAG = "YouTubeProvider";

    public interface Callback {
        void onSuccess(List<Video> videos);
        void onError(Exception e);
    }

    public interface ChannelCallback {
        void onSuccess(String iconUrl);
        void onError(Exception e);
    }

    public static void fetchVideos(Context context, String channelId, Callback callback) {
        new Thread(() -> {
            try {
                YouTube youtube = YouTubeService.getService(context);
                String apiKey = YouTubeService.getApiKey();

                // 1. Lấy Uploads Playlist ID để tiết kiệm quota
                YouTube.Channels.List channelRequest = youtube.channels().list(Collections.singletonList("contentDetails"));
                channelRequest.setId(Collections.singletonList(channelId));
                channelRequest.setKey(apiKey);

                ChannelListResponse channelResponse = channelRequest.execute();
                List<Channel> channels = channelResponse.getItems();

                if (channels != null && !channels.isEmpty() && channels.get(0).getContentDetails() != null) {
                    String uploadsPlaylistId = channels.get(0).getContentDetails().getRelatedPlaylists().getUploads();

                    // 2. Lấy video từ playlist
                    YouTube.PlaylistItems.List playlistItemsRequest = youtube.playlistItems().list(Collections.singletonList("snippet"));
                    playlistItemsRequest.setPlaylistId(uploadsPlaylistId);
                    playlistItemsRequest.setMaxResults(20L);
                    playlistItemsRequest.setKey(apiKey);

                    PlaylistItemListResponse playlistResponse = playlistItemsRequest.execute();
                    List<PlaylistItem> items = playlistResponse.getItems();

                    List<Video> videos = new ArrayList<>();
                    if (items != null) {
                        for (PlaylistItem item : items) {
                            String videoId = item.getSnippet().getResourceId().getVideoId();
                            String title = item.getSnippet().getTitle();

                            // Lấy thumbnail tốt nhất
                            String thumbnailUrl = null;
                            if (item.getSnippet().getThumbnails() != null) {
                                if (item.getSnippet().getThumbnails().getMaxres() != null) {
                                    thumbnailUrl = item.getSnippet().getThumbnails().getMaxres().getUrl();
                                } else if (item.getSnippet().getThumbnails().getHigh() != null) {
                                    thumbnailUrl = item.getSnippet().getThumbnails().getHigh().getUrl();
                                } else if (item.getSnippet().getThumbnails().getMedium() != null) {
                                    thumbnailUrl = item.getSnippet().getThumbnails().getMedium().getUrl();
                                } else if (item.getSnippet().getThumbnails().getDefault() != null) {
                                    thumbnailUrl = item.getSnippet().getThumbnails().getDefault().getUrl();
                                }
                            }
                            if (thumbnailUrl == null) thumbnailUrl = "https://i.ytimg.com/vi/" + videoId + "/hqdefault.jpg";

                            videos.add(new Video(videoId, title, thumbnailUrl));
                        }
                    }
                    callback.onSuccess(videos);
                } else {
                    // Fallback to Search API
                    Log.w(TAG, "No uploads playlist found, falling back to Search API");
                    YouTube.Search.List searchRequest = youtube.search().list(Collections.singletonList("snippet"));
                    searchRequest.setChannelId(channelId);
                    searchRequest.setType(Collections.singletonList("video"));
                    searchRequest.setVideoEmbeddable("true");
                    searchRequest.setMaxResults(20L);
                    searchRequest.setKey(apiKey);

                    SearchListResponse searchResponse = searchRequest.execute();
                    List<SearchResult> searchResults = searchResponse.getItems();

                    List<Video> videos = new ArrayList<>();
                    if (searchResults != null) {
                        for (SearchResult item : searchResults) {
                            String videoId = item.getId().getVideoId();
                            String title = item.getSnippet().getTitle();

                            String thumbnailUrl = null;
                            // Kiểm tra theo cấu trúc SearchResult Thumbnail
                            if (item.getSnippet().getThumbnails() != null) {
                                if (item.getSnippet().getThumbnails().getHigh() != null) {
                                    thumbnailUrl = item.getSnippet().getThumbnails().getHigh().getUrl();
                                } else if (item.getSnippet().getThumbnails().getMedium() != null) {
                                    thumbnailUrl = item.getSnippet().getThumbnails().getMedium().getUrl();
                                } else if (item.getSnippet().getThumbnails().getDefault() != null) {
                                    thumbnailUrl = item.getSnippet().getThumbnails().getDefault().getUrl();
                                }
                            }
                            if (thumbnailUrl == null) thumbnailUrl = "https://i.ytimg.com/vi/" + videoId + "/hqdefault.jpg";

                            videos.add(new Video(videoId, title, thumbnailUrl));
                        }
                    }
                    callback.onSuccess(videos);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching videos for channelId: " + channelId, e);
                callback.onError(e);
            }
        }).start();
    }

    public static void fetchChannelIcon(Context context, String channelId, ChannelCallback callback) {
        new Thread(() -> {
            try {
                YouTube youtube = YouTubeService.getService(context);
                String apiKey = YouTubeService.getApiKey();

                YouTube.Channels.List request = youtube.channels().list(Collections.singletonList("snippet"));
                request.setId(Collections.singletonList(channelId));
                request.setKey(apiKey);

                ChannelListResponse response = request.execute();
                List<Channel> channels = response.getItems();

                if (channels != null && !channels.isEmpty()) {
                    Channel channel = channels.get(0);
                    if (channel.getSnippet() != null && channel.getSnippet().getThumbnails() != null) {
                        String iconUrl = null;
                        if (channel.getSnippet().getThumbnails().getDefault() != null) {
                            iconUrl = channel.getSnippet().getThumbnails().getDefault().getUrl();
                        } else if (channel.getSnippet().getThumbnails().getMedium() != null) {
                            iconUrl = channel.getSnippet().getThumbnails().getMedium().getUrl();
                        } else if (channel.getSnippet().getThumbnails().getHigh() != null) {
                            iconUrl = channel.getSnippet().getThumbnails().getHigh().getUrl();
                        }

                        if (iconUrl != null) {
                            callback.onSuccess(iconUrl);
                        } else {
                            callback.onError(new Exception("No thumbnail found"));
                        }
                    } else {
                        callback.onError(new Exception("No snippet or thumbnail info"));
                    }
                } else {
                    callback.onError(new Exception("Channel not found"));
                }
            } catch (Exception e) {
                Log.e(TAG, "fetchChannelIcon error", e);
                callback.onError(e);
            }
        }).start();
    }
}