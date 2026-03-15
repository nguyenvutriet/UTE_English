package com.example.englishapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.adapter.SubtitleAdapter;
import com.example.englishapp.model.Subtitle;
import com.example.englishapp.model.Video;
import com.example.englishapp.utils.HistoryDatabaseHelper;
import com.example.englishapp.utils.SubtitleFetcher;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoPlayerActivity extends AppCompatActivity {

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer activePlayer;

    private ImageView ivThumbnail, ivPlayOverlay;
    private TextView tvVideoTitle;

    private RecyclerView rvSubtitles;
    private SubtitleAdapter subtitleAdapter;

    private String videoId;
    private String videoTitle;
    private String thumbnail;
    private boolean playRequested = false;
    private HistoryDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoId = getIntent().getStringExtra("videoId");
        videoTitle = getIntent().getStringExtra("videoTitle");
        thumbnail = getIntent().getStringExtra("thumbnail");
        dbHelper = new HistoryDatabaseHelper(this);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        ivThumbnail = findViewById(R.id.ivThumbnail);
        ivPlayOverlay = findViewById(R.id.ivPlayOverlay);
        tvVideoTitle = findViewById(R.id.tvVideoTitle);
        rvSubtitles = findViewById(R.id.rvSubtitles);

        tvVideoTitle.setText(videoTitle);

        Picasso.get().load(thumbnail).into(ivThumbnail);

        rvSubtitles.setLayoutManager(new LinearLayoutManager(this));

        CardView btnSubtitle = findViewById(R.id.btnSubtitle);
        CardView btnChooseWord = findViewById(R.id.btnChooseWord);

        View layoutSelection = findViewById(R.id.layoutSelection);
        View layoutSubtitleList = findViewById(R.id.layoutSubtitleList);
        View btnCloseSubtitle = findViewById(R.id.btnCloseSubtitle);

        View btnBackTop = findViewById(R.id.btnBackTop);
        View btnBackBottom = findViewById(R.id.btnBackBottom);

        btnBackTop.setOnClickListener(v -> finish());
        btnBackBottom.setOnClickListener(v -> finish());

        View.OnClickListener playVideoListener = v -> {
            ivThumbnail.setVisibility(View.GONE);
            ivPlayOverlay.setVisibility(View.GONE);
            playRequested = true;
            if (activePlayer != null) {
                activePlayer.play();
                saveToHistory();
            }
        };

        ivThumbnail.setOnClickListener(v -> Toast.makeText(this, "Vui lòng chọn kiểu xem bên dưới", Toast.LENGTH_SHORT).show());
        ivPlayOverlay.setOnClickListener(v -> Toast.makeText(this, "Vui lòng chọn kiểu xem bên dưới", Toast.LENGTH_SHORT).show());

        btnSubtitle.setOnClickListener(v -> {

            layoutSelection.setVisibility(View.GONE);
            layoutSubtitleList.setVisibility(View.VISIBLE);

            playVideoListener.onClick(v);

            loadSubtitles();
        });

        btnCloseSubtitle.setOnClickListener(v -> {

            layoutSubtitleList.setVisibility(View.GONE);
            layoutSelection.setVisibility(View.VISIBLE);
        });

        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(
                new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        activePlayer = youTubePlayer;
                        activePlayer.cueVideo(videoId, 0);
                        if (playRequested) {
                            activePlayer.play();
                            saveToHistory();
                        }
                    }

                    @Override
                    public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float second) {
                        if (subtitleAdapter != null) {
                            subtitleAdapter.updateActiveSubtitle(second);
                            int index = subtitleAdapter.getCurrentActiveIndex();
                            if (index != -1) {
                                rvSubtitles.post(() -> rvSubtitles.smoothScrollToPosition(index));
                            }
                        }
                        // Cập nhật tiến trình xem
                        dbHelper.updateWatchedDuration(videoId, second);
                    }
                });
    }

    private void saveToHistory() {
        Video video = new Video(videoId, videoTitle, thumbnail);
        dbHelper.addOrUpdateVideo(video, 0f);
    }

    private void loadSubtitles() {

        new Thread(() -> {

            List<Subtitle> subs = SubtitleFetcher.fetch(videoId);

            if (subs == null || subs.isEmpty()) {

                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Video này không có subtitle",
                                Toast.LENGTH_LONG).show());

                return;
            }

            runOnUiThread(() -> {

                subtitleAdapter = new SubtitleAdapter(subs,
                        new SubtitleAdapter.OnSubtitleClickListener() {

                            @Override
                            public void onPauseClick() {

                                if (activePlayer != null)
                                    activePlayer.pause();
                            }

                            @Override
                            public void onSubtitleClick(Subtitle subtitle) {

                                if (activePlayer != null) {

                                    activePlayer.seekTo(subtitle.getStartTime());
                                    activePlayer.play();
                                }
                            }
                        });

                rvSubtitles.setAdapter(subtitleAdapter);
            });

        }).start();
    }
}