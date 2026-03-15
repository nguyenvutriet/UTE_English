package com.example.englishapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.adapter.ChannelAdapter;
import com.example.englishapp.adapter.HistoryVideoAdapter;
import com.example.englishapp.adapter.TopicVideoAdapter;
import com.example.englishapp.model.Channel;
import com.example.englishapp.model.TopicVideo;
import com.example.englishapp.model.Video;
import com.example.englishapp.utils.HistoryDatabaseHelper;
import com.example.englishapp.utils.YouTubeProvider;

import java.util.ArrayList;
import java.util.List;

public class MainVideoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChannelAdapter channelAdapter;
    private TopicVideoAdapter topicAdapter;
    private final List<Channel> channelList = new ArrayList<>();
    private final List<TopicVideo> topicList = new ArrayList<>();
    private HistoryDatabaseHelper dbHelper;

    private final String[] channelIds = {
            "UCAuUUnT6oDeKwE6v1NGQxug", // TED Talk
            "UCsooa4yRKGN_zEE8iknghZA", // TED-Ed
            "UCX6b17PVsYBQ0ip5gyeme-Q", // CrashCourse
            "UCHaHD477h-FeBbVh9Sh7syA", // BBC Learning English
            "UCxJGMJbj7ok19r1t8pR6JMQ"  // Speak English With Vanessa
    };
    
    private final String[] channelNames = {
            "TED Talk",
            "TED-Ed",
            "Crash Course",
            "BBC Learning English",
            "Speak English With Vanessa"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_video);
        dbHelper = new HistoryDatabaseHelper(this);

        setupViews();
        loadChannelData(); 
        prepareTopics();    
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        channelAdapter = new ChannelAdapter(this, channelList);
        
        topicAdapter = new TopicVideoAdapter(this, topicList, topic -> {
            Intent intent = new Intent(MainVideoActivity.this, ViewAllVideosActivity.class);
            intent.putExtra("channelId", topic.getChannelId());
            intent.putExtra("channelName", topic.getName());
            intent.putExtra("headerImage", topic.getImageUrl()); // Gửi ảnh của chủ đề sang
            intent.putExtra("isTopic", true);
            startActivity(intent);
        });

        android.widget.TextView tvTabKenhHay = findViewById(R.id.tvTabKenhHay);
        android.widget.TextView tvTabChuDe = findViewById(R.id.tvTabChuDe);
        android.widget.TextView tvTabDaXem = findViewById(R.id.tvTabDaXem);

        updateTabStyles(tvTabKenhHay, tvTabChuDe, tvTabDaXem, 0);
        recyclerView.setAdapter(channelAdapter);

        tvTabKenhHay.setOnClickListener(v -> {
            updateTabStyles(tvTabKenhHay, tvTabChuDe, tvTabDaXem, 0);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(channelAdapter);
        });

        tvTabChuDe.setOnClickListener(v -> {
            updateTabStyles(tvTabKenhHay, tvTabChuDe, tvTabDaXem, 1);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.setAdapter(topicAdapter);
            refreshTopicStats(); 
        });

        tvTabDaXem.setOnClickListener(v -> {
            updateTabStyles(tvTabKenhHay, tvTabChuDe, tvTabDaXem, 2);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            loadHistoryData();
        });
    }

    private void updateTabStyles(android.widget.TextView t1, android.widget.TextView t2, android.widget.TextView t3, int active) {
        t1.setTextColor(active == 0 ? 0xFF000000 : 0xFF888888);
        t1.setTypeface(null, active == 0 ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        t2.setTextColor(active == 1 ? 0xFF000000 : 0xFF888888);
        t2.setTypeface(null, active == 1 ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        t3.setTextColor(active == 2 ? 0xFF000000 : 0xFF888888);
        t3.setTypeface(null, active == 2 ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        
        t1.setPaintFlags(active == 0 ? (t1.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG) : (t1.getPaintFlags() & ~android.graphics.Paint.UNDERLINE_TEXT_FLAG));
        t2.setPaintFlags(active == 1 ? (t2.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG) : (t2.getPaintFlags() & ~android.graphics.Paint.UNDERLINE_TEXT_FLAG));
        t3.setPaintFlags(active == 2 ? (t3.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG) : (t3.getPaintFlags() & ~android.graphics.Paint.UNDERLINE_TEXT_FLAG));
    }

    private void loadHistoryData() {
        List<Video> historyList = dbHelper.getAllHistoryVideos();
        HistoryVideoAdapter historyAdapter = new HistoryVideoAdapter(this, historyList);
        recyclerView.setAdapter(historyAdapter);
    }

    private void loadChannelData() {
        for (int i = 0; i < channelIds.length; i++) {
            final String cid = channelIds[i];
            final String name = channelNames[i];
            
            YouTubeProvider.fetchChannelIcon(cid, new YouTubeProvider.ChannelCallback() {
                @Override
                public void onSuccess(String iconUrl) {
                    YouTubeProvider.fetchVideos(cid, new YouTubeProvider.Callback() {
                        @Override
                        public void onSuccess(List<Video> videos) {
                            runOnUiThread(() -> {
                                channelList.add(new Channel(cid, name, iconUrl, videos));
                                channelAdapter.notifyDataSetChanged();
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("MainActivity", "fetchVideos error", e);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    Log.e("MainActivity", "fetchChannelIcon error", e);
                }
            });
        }
    }

    private void prepareTopics() {
        topicList.clear();
        // Cấu hình ban đầu cho 6 chủ đề với ảnh liên quan
        topicList.add(new TopicVideo("Cổ xưa", "UC8mWYDxedkJmUReAiA3ze9w", "android.resource://" + getPackageName() + "/" + R.drawable.topic_ancient, 0));
        topicList.add(new TopicVideo("Chiến tranh", "UCfdNM3NAhaBOXCafH7krzrA", "android.resource://" + getPackageName() + "/" + R.drawable.topic_war, 0));
        topicList.add(new TopicVideo("Lời khuyên", "UC1yQ3Y6y-5tJbXH9sP0hZ7A", "android.resource://" + getPackageName() + "/" + R.drawable.topic_advice, 0));
        topicList.add(new TopicVideo("Dịch bệnh", "UCz0M6m2KqJ8bJH0HqgS7y7A", "android.resource://" + getPackageName() + "/" + R.drawable.topic_pandemic, 0));
        topicList.add(new TopicVideo("Lễ đăng quang", "UC0iX6LhY4H0xXzQ1R7C4KkA", "android.resource://" + getPackageName() + "/" + R.drawable.topic_coronation, 0));
        topicList.add(new TopicVideo("Du lịch", "UCb1nG9P0P4sM7J6v9K0XzqQ", "android.resource://" + getPackageName() + "/" + R.drawable.topic_travel, 0));
        
        refreshTopicStats();
    }

    private void refreshTopicStats() {
        for (TopicVideo topic : topicList) {
            YouTubeProvider.fetchVideos(topic.getChannelId(), new YouTubeProvider.Callback() {
                @Override
                public void onSuccess(List<Video> videos) {
                    runOnUiThread(() -> {
                        int actualTotal = videos.size();
                        topic.setTotalVideos(actualTotal);

                        // Nếu chưa có ảnh thì lấy ảnh của video đầu tiên
                        if (!videos.isEmpty() && (topic.getImageUrl() == null || topic.getImageUrl().isEmpty())) {
                            topic.setImageUrl(videos.get(0).getThumbnail());
                        }

                        int watched = 0;
                        for (Video v : videos) {
                            if (dbHelper.isVideoInHistory(v.getVideoId())) {
                                watched++;
                            }
                        }
                        topic.setWatchedCount(watched);
                        
                        topicAdapter.notifyDataSetChanged();
                    });
                }

                @Override
                public void onError(Exception e) {
                    Log.e("MainActivity", "Error refreshing topic: " + topic.getName(), e);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        android.widget.TextView tvTabChuDe = findViewById(R.id.tvTabChuDe);
        if (tvTabChuDe != null && tvTabChuDe.getCurrentTextColor() == 0xFF000000) {
            refreshTopicStats();
        }
    }
}
