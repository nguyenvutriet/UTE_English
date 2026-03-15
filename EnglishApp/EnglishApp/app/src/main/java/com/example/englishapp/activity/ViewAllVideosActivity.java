package com.example.englishapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.adapter.VideoAdapter;
import com.example.englishapp.model.Video;
import com.example.englishapp.utils.YouTubeProvider;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class ViewAllVideosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private final List<Video> videoList = new ArrayList<>();
    private String channelId;
    private String channelName;
    private String headerImageUrl;
    private boolean isTopic = false;
    private android.widget.ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_videos);

        channelId = getIntent().getStringExtra("channelId");
        channelName = getIntent().getStringExtra("channelName");
        headerImageUrl = getIntent().getStringExtra("headerImage");
        isTopic = getIntent().getBooleanExtra("isTopic", false);

        setupHeader();

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.rvAllVideos);
        adapter = new VideoAdapter(this, videoList);
        adapter.setTopicMode(isTopic);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ImageView btnFilter = findViewById(R.id.btnFilter);
        if (btnFilter != null) {
            btnFilter.setOnClickListener(v -> android.widget.Toast.makeText(this, "Tính năng lọc đang được phát triển", android.widget.Toast.LENGTH_SHORT).show());
        }

        loadVideos();
    }

    private void setupHeader() {
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(isTopic ? "Chủ đề" : channelName);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        View layoutTopic = findViewById(R.id.layoutTopicHeader);
        View layoutChannel = findViewById(R.id.layoutChannelHeader);

        if (isTopic) {
            layoutTopic.setVisibility(View.VISIBLE);
            layoutChannel.setVisibility(View.GONE);
            
            ImageView ivHeaderImage = findViewById(R.id.ivHeaderImage);
            // Ưu tiên dùng ảnh được truyền từ bên ngoài vào
            if (headerImageUrl != null && !headerImageUrl.isEmpty()) {
                Picasso.get().load(headerImageUrl).placeholder(android.R.color.darker_gray).into(ivHeaderImage);
            }
        } else {
            layoutTopic.setVisibility(View.GONE);
            layoutChannel.setVisibility(View.VISIBLE);
            
            TextView tvLargeTitle = findViewById(R.id.channelTitleLarge);
            tvLargeTitle.setText(channelName);
            
            ImageView channelLogo = findViewById(R.id.channelLogo);
            YouTubeProvider.fetchChannelIcon(channelId, new YouTubeProvider.ChannelCallback() {
                @Override
                public void onSuccess(String iconUrl) {
                    runOnUiThread(() -> Picasso.get().load(iconUrl).into(channelLogo));
                }
                @Override
                public void onError(Exception e) { e.printStackTrace(); }
            });
        }
    }

    private void loadVideos() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        YouTubeProvider.fetchVideos(channelId, new YouTubeProvider.Callback() {
            @Override
            public void onSuccess(List<Video> videos) {
                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    videoList.clear();
                    if (videos != null) {
                        videoList.addAll(videos);
                        android.util.Log.d("ViewAllVideos", "Fetched " + videos.size() + " videos");
                    } else {
                        android.util.Log.e("ViewAllVideos", "Fetched videos list is null");
                    }
                    adapter.notifyDataSetChanged();
                    
                    if (isTopic && !videoList.isEmpty() && (headerImageUrl == null || headerImageUrl.isEmpty())) {
                        ImageView ivHeaderImage = findViewById(R.id.ivHeaderImage);
                        Picasso.get().load(videoList.get(0).getThumbnail()).into(ivHeaderImage);
                    }

                    if (videoList.isEmpty()) {
                        android.widget.Toast.makeText(ViewAllVideosActivity.this, "Không tìm thấy video nào cho kênh này", android.widget.Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onError(Exception e) { 
                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    android.util.Log.e("ViewAllVideos", "Error fetching videos", e);
                    android.widget.Toast.makeText(ViewAllVideosActivity.this, "Lỗi khi tải dữ liệu: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
