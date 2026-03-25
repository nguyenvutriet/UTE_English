package com.example.englishapp.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.adapter.ChooseWordAdapter;
import com.example.englishapp.adapter.SubtitleAdapter;
import com.example.englishapp.db.AppDatabase;
import com.example.englishapp.db.SubtitleDao;
import com.example.englishapp.db.SubtitleEntity;
import com.example.englishapp.model.Subtitle;
import com.example.englishapp.model.SubtitleTrack;
import com.example.englishapp.model.Video;
import com.example.englishapp.model.VideoInfo;
import com.example.englishapp.utils.HistoryDatabaseHelper;
import com.example.englishapp.utils.SubtitleFetcher;
import com.example.englishapp.utils.YouTubeSubtitleApi;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import android.widget.ArrayAdapter;


import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.HttpUrl;

import org.json.JSONObject;

public class VideoPlayerActivity extends AppCompatActivity {

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer activePlayer;

    private ImageView ivThumbnail, ivPlayOverlay;
    private TextView tvVideoTitle;

    private RecyclerView rvSubtitles;
    private View layoutSubtitleLoading;
    private SubtitleAdapter subtitleAdapter;
    private List<Subtitle> allSubtitles;

    private View layoutChooseWord;

    // Choose Word game fields
    private RecyclerView rvGameSubtitles;
    private ChooseWordAdapter chooseWordAdapter;
    private List<ChooseWordAdapter.GameSubtitle> gameSubtitleList;
    private Button btnGameOpt1, btnGameOpt2, btnGameOpt3, btnGameOpt4;
    private Button btnListenAgain, btnSkipWord;
    private int currentGameIndex = -1;
    private boolean isGameMode = false;
    private boolean isGamePaused = false;
    private int lastGamePausedIndex = -1;
    private static final float GAME_PAUSE_TOLERANCE_SECONDS = 0.02f;
    private static final float GAME_RESUME_EPSILON_SECONDS = 0.08f;
    private float latestPlaybackSecond = 0f;
    private List<String> correctWords = new ArrayList<>();
    private List<String> skippedWords = new ArrayList<>();

    private List<SubtitleTrack> availableTracks = new ArrayList<>();
    private boolean isTracksLoaded = false;

    private String videoId;
    private String videoTitle;
    private String thumbnail;
    private boolean playRequested = false;
    private HistoryDatabaseHelper dbHelper; // keep for history
    private SubtitleDao subtitleDao;

    private boolean isPlayerPlaying = false;

    private static final int GAME_MAX_WORDS_PER_LINE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(com.example.englishapp.R.layout.activity_video_player);

        videoId = getIntent().getStringExtra("videoId");
        videoTitle = getIntent().getStringExtra("videoTitle");
        thumbnail = getIntent().getStringExtra("thumbnail");
        dbHelper = new HistoryDatabaseHelper(this);
        subtitleDao = AppDatabase.getInstance(this).subtitleDao();

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        ivThumbnail = findViewById(R.id.ivThumbnail);
        ivPlayOverlay = findViewById(R.id.ivPlayOverlay);
        tvVideoTitle = findViewById(R.id.tvVideoTitle);
        rvSubtitles = findViewById(R.id.rvSubtitles);
        tvVideoTitle.setText(videoTitle);

        Picasso.get().load(thumbnail).into(ivThumbnail);

        layoutSubtitleLoading = findViewById(R.id.layoutSubtitleLoading);
        rvSubtitles.setLayoutManager(new LinearLayoutManager(this));

        CardView btnSubtitle = findViewById(R.id.btnSubtitle);
        CardView btnChooseWord = findViewById(R.id.btnChooseWord);

        View layoutSelection = findViewById(R.id.layoutSelection);
        View layoutSubtitleList = findViewById(R.id.layoutSubtitleList);
        layoutChooseWord = findViewById(R.id.layoutChooseWord);

        // Game UI elements
        rvGameSubtitles = findViewById(R.id.rvGameSubtitles);
        rvGameSubtitles.setLayoutManager(new LinearLayoutManager(this));
        btnGameOpt1 = findViewById(R.id.btnGameOpt1);
        btnGameOpt2 = findViewById(R.id.btnGameOpt2);
        btnGameOpt3 = findViewById(R.id.btnGameOpt3);
        btnGameOpt4 = findViewById(R.id.btnGameOpt4);
        btnListenAgain = findViewById(R.id.btnListenAgain);
        btnSkipWord = findViewById(R.id.btnSkipWord);

        View btnCloseSubtitle = findViewById(R.id.btnCloseSubtitle);

        View btnBackTop = findViewById(R.id.btnBackTop);
        View btnBackBottom = findViewById(R.id.btnBackBottom);


        btnBackTop.setOnClickListener(v -> {
            resetToThumbnailState();
        });
        btnBackBottom.setOnClickListener(v -> {
            layoutSubtitleList.setVisibility(View.GONE);
            layoutSelection.setVisibility(View.VISIBLE);
            resetToThumbnailState();
        });

        View btnRewind = findViewById(R.id.btnRewind);
        if (btnRewind != null) {
            btnRewind.setOnClickListener(v -> {
                if (activePlayer != null && subtitleAdapter != null && allSubtitles != null) {
                    int currentIndex = subtitleAdapter.getCurrentActiveIndex();
                    if (currentIndex > 0) {
                        float prevStart = allSubtitles.get(currentIndex - 1).getStartTime();
                        activePlayer.seekTo(prevStart);
                        activePlayer.play();
                    } else if (currentIndex == 0) {
                        activePlayer.seekTo(0f);
                        activePlayer.play();
                    }
                }
            });
        }

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
            findViewById(R.id.subtitleHeader).setVisibility(View.VISIBLE);
            playVideoListener.onClick(v);
            if (!isTracksLoaded) {
                loadAvailableTracks(false);
            } else {
                loadSubtitles(false);
            }
        });

        btnChooseWord.setOnClickListener(v -> {
            layoutSelection.setVisibility(View.GONE);
            layoutChooseWord.setVisibility(View.VISIBLE);
            playVideoListener.onClick(v);
            if (!isTracksLoaded) {
                loadAvailableTracks(true);
            } else {
                loadSubtitles(true);
            }
        });

        btnCloseSubtitle.setOnClickListener(v -> {
            findViewById(R.id.subtitleHeader).setVisibility(View.GONE);
        });

        // Game footer buttons
        findViewById(R.id.btnGameBack).setOnClickListener(v -> {
            layoutChooseWord.setVisibility(View.GONE);
            layoutSelection.setVisibility(View.VISIBLE);
            isGameMode = false;
            resetToThumbnailState();
        });

        findViewById(R.id.btnGameReplay).setOnClickListener(v -> {
            // Restart game from beginning
            if (activePlayer != null) {
                activePlayer.seekTo(0f);
                activePlayer.play();
            }
            isGamePaused = false;
            lastGamePausedIndex = -1;
            currentGameIndex = -1;
            correctWords.clear();
            skippedWords.clear();
            // Reset all game subtitles
            if (gameSubtitleList != null) {
                for (ChooseWordAdapter.GameSubtitle gs : gameSubtitleList) {
                    gs.isRevealed = false;
                    gs.isCorrect = false;
                    gs.isSkipped = false;
                }
                if (chooseWordAdapter != null) chooseWordAdapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.btnGameStats).setOnClickListener(v -> showWordStatsDialog());
        findViewById(R.id.btnGameHelp).setOnClickListener(v -> showGameHelpDialog());

        // Listen Again button
        btnListenAgain.setOnClickListener(v -> {
            if (activePlayer != null && currentGameIndex >= 0 && gameSubtitleList != null
                    && currentGameIndex < gameSubtitleList.size()) {
                Subtitle sub = gameSubtitleList.get(currentGameIndex).subtitle;
                // Allow pausing again at the end of the same sentence after replay.
                lastGamePausedIndex = -1;
                isGamePaused = false;
                activePlayer.seekTo(sub.getStartTime());
                activePlayer.play();
            }
        });

        // Skip button
        btnSkipWord.setOnClickListener(v -> {
            if (currentGameIndex >= 0 && gameSubtitleList != null
                    && currentGameIndex < gameSubtitleList.size()) {
                ChooseWordAdapter.GameSubtitle gs = gameSubtitleList.get(currentGameIndex);
                if (!gs.isRevealed) {
                    gs.isRevealed = true;
                    gs.isSkipped = true;
                    gs.isCorrect = false;
                    
                    // Lấy từ chính xác cần hiện trong thống kê
                    String correctWord = gs.hiddenWord.replaceAll("[^a-zA-Z']", "");
                    if (correctWord.isEmpty()) correctWord = gs.hiddenWord;
                    String normalized = normalizeWord(correctWord);
                    
                    if (!normalized.isEmpty()) skippedWords.add(normalized);
                    if (chooseWordAdapter != null) chooseWordAdapter.notifyItemChanged(currentGameIndex);
                }
                continueGameAfterDecision(currentGameIndex);
            }
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
                    public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState state) {
                        super.onStateChange(youTubePlayer, state);
                        if (state == com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.PLAYING) {
                            isPlayerPlaying = true;
                        } else if (state == com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.PAUSED
                                || state == com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.ENDED
                                || state == com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.VIDEO_CUED
                                || state == com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.BUFFERING
                                || state == com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.UNSTARTED
                                || state == com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.UNKNOWN) {
                            isPlayerPlaying = false;
                        }

                        if (chooseWordAdapter != null) {
                            chooseWordAdapter.setPlayerPlaying(isPlayerPlaying);
                        }

                        if (subtitleAdapter != null) {
                            if (state == com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.PLAYING) {
                                subtitleAdapter.setPlaying(true);
                            } else if (state == com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.PAUSED 
                                || state == com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.ENDED) {
                                subtitleAdapter.setPlaying(false);
                            }
                        }
                    }

                    @Override
                    public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float second) {
                        latestPlaybackSecond = second;
                        // Subtitle mode tracking
                        if (subtitleAdapter != null) {
                            subtitleAdapter.updateActiveSubtitle(second);
                            int index = subtitleAdapter.getCurrentActiveIndex();
                            if (index != -1) {
                                rvSubtitles.post(() -> rvSubtitles.smoothScrollToPosition(index));
                            }
                        }

                        // Game mode tracking: pause when a full sentence has finished.
                        if (isGameMode && chooseWordAdapter != null && gameSubtitleList != null && !isGamePaused) {
                            chooseWordAdapter.updateActiveByTime(second);
                            int activeIdx = chooseWordAdapter.getCurrentActiveIndex();
                            if (activeIdx != -1 && activeIdx != currentGameIndex) {
                                currentGameIndex = activeIdx;
                                rvGameSubtitles.post(() -> rvGameSubtitles.smoothScrollToPosition(activeIdx));
                                prepareGameQuestion(activeIdx);
                            }
                            if (activeIdx != -1 && activeIdx < gameSubtitleList.size()) {
                                ChooseWordAdapter.GameSubtitle gs = gameSubtitleList.get(activeIdx);
                                float endTime = gs.subtitle.getEndTime();
                                if (!gs.isRevealed
                                        && gs.hiddenWordIndex >= 0
                                        && second >= endTime - GAME_PAUSE_TOLERANCE_SECONDS
                                        && lastGamePausedIndex != activeIdx) {
                                    isGamePaused = true;
                                    lastGamePausedIndex = activeIdx;
                                    youTubePlayer.pause();
                                }
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

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        View layoutSubtitleList = findViewById(R.id.layoutSubtitleList);
        View layoutChooseWord = findViewById(R.id.layoutChooseWord);
        View layoutSelection = findViewById(R.id.layoutSelection);
        
        if (layoutSubtitleList.getVisibility() == View.VISIBLE) {
            layoutSubtitleList.setVisibility(View.GONE);
            layoutSelection.setVisibility(View.VISIBLE);
            resetToThumbnailState();
        } else if (layoutChooseWord.getVisibility() == View.VISIBLE) {
            layoutChooseWord.setVisibility(View.GONE);
            layoutSelection.setVisibility(View.VISIBLE);
            resetToThumbnailState();
        } else {
            super.onBackPressed();
        }
    }

    private void resetToThumbnailState() {
        // Dừng video và hiện lại thumbnail ban đầu
        if (activePlayer != null) {
            activePlayer.pause();
            activePlayer.cueVideo(videoId, 0); // Reset về đầu video
        }
        playRequested = false;
        isGameMode = false;
        isGamePaused = false;
        isPlayerPlaying = false;
        ivThumbnail.setVisibility(View.VISIBLE);
        ivPlayOverlay.setVisibility(View.VISIBLE);
    }

    private void loadAvailableTracks(boolean startGame) {
        layoutSubtitleLoading.setVisibility(View.VISIBLE);
        rvSubtitles.setVisibility(View.GONE);

        SubtitleFetcher.fetchAvailableTracks(this, videoId, new YouTubeSubtitleApi.VideoInfoCallback() {
            @Override
            public void onSuccess(VideoInfo info) {
                runOnUiThread(() -> {
                    availableTracks = info.getAvailableTracks();
                    isTracksLoaded = true;
                    selectDefaultTrack(startGame);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Log.e("VideoPlayer", "Failed to load tracks", e);
                    // Fallback to old behavior if new logic fails
                    loadSubtitles(startGame);
                });
            }
        });
    }

    private void selectDefaultTrack(boolean startGame) {
        if (availableTracks == null || availableTracks.isEmpty()) return;

        SubtitleTrack selectedTrack = availableTracks.get(0);
        for (SubtitleTrack track : availableTracks) {
            String lang = track.getLanguageCode();
            if (lang != null) {
                if (lang.equals("vi") && !track.isAutoGenerated()) {
                    selectedTrack = track;
                    break;
                } else if (lang.equals("en")) {
                    selectedTrack = track;
                }
            }
        }

        final SubtitleTrack finalSelectedTrack = selectedTrack;
        new Thread(() -> {
            List<Subtitle> cached = new ArrayList<>();
            try {
                List<SubtitleEntity> ents = subtitleDao.getByVideoAndLanguage(videoId, finalSelectedTrack.getLanguageCode());
                for (SubtitleEntity e : ents) cached.add(new Subtitle(e.text, e.start_time, e.end_time));
            } catch (Exception ex) {
                cached = dbHelper.getSavedSubtitles(videoId, finalSelectedTrack.getLanguageCode());
            }
            final List<Subtitle> cachedFinal = (cached == null) ? new ArrayList<>() : new ArrayList<>(cached);
            if (!cachedFinal.isEmpty()) {
                allSubtitles = cachedFinal;
                runOnUiThread(() -> {
                    layoutSubtitleLoading.setVisibility(View.GONE);
                    if (startGame) {
                        startChooseWordGame();
                    } else {
                        rvSubtitles.setVisibility(View.VISIBLE);
                        setupSubtitleList(cachedFinal);
                    }
                });
            } else {
                runOnUiThread(() -> {
                    layoutSubtitleLoading.setVisibility(View.VISIBLE);
                    rvSubtitles.setVisibility(View.GONE);
                });
                loadTrackSubtitles(finalSelectedTrack, startGame);
            }
        }).start();
    }

    private void loadTrackSubtitles(SubtitleTrack track, boolean startGame) {
        layoutSubtitleLoading.setVisibility(View.VISIBLE);
        rvSubtitles.setVisibility(View.GONE);

        SubtitleFetcher.fetchTrack(this, videoId, track, new YouTubeSubtitleApi.SubtitlesCallback() {
            @Override
            public void onSuccess(List<Subtitle> subs) {
                allSubtitles = subs;

                // Lưu vào DB ở background
                new Thread(() -> {
                    try {
                        String lang = track.getLanguageCode() != null ? track.getLanguageCode() : "en";
                        String trackId = track.getBaseUrl() != null ? track.getBaseUrl() : "";
                        List<SubtitleEntity> ents = new ArrayList<>();
                        long now = System.currentTimeMillis();
                        for (Subtitle s : subs) {
                            ents.add(new SubtitleEntity(videoId, s.getText(), s.getStartTime(), s.getEndTime(), lang, trackId, "youtube_api", now));
                        }
                        subtitleDao.replaceForVideoAndLanguage(videoId, lang, ents);
                    } catch (Exception e) {
                        Log.w("VideoPlayer", "Failed to save subtitles batch: " + e.getMessage());
                        // fallback to old helper
                        try { dbHelper.saveSubtitlesBatch(videoId, track.getLanguageCode() != null ? track.getLanguageCode() : "en", track.getBaseUrl(), "youtube_api", subs); } catch (Exception ex) {}
                    }
                }).start();

                runOnUiThread(() -> {
                    layoutSubtitleLoading.setVisibility(View.GONE);
                    if (startGame) {
                        startChooseWordGame();
                    } else {
                        rvSubtitles.setVisibility(View.VISIBLE);
                        setupSubtitleList(subs);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    layoutSubtitleLoading.setVisibility(View.GONE);
                    Toast.makeText(VideoPlayerActivity.this, "Không thể tải ngôn ngữ này", Toast.LENGTH_SHORT).show();
                    String lang = track.getLanguageCode() == null ? "" : track.getLanguageCode().toLowerCase();
                    if ("en".equals(lang) || allSubtitles == null || allSubtitles.isEmpty()) {
                        loadSubtitles(startGame);
                    }
                });
            }
        });
    }

    private void loadSubtitles(boolean startGame) {

        layoutSubtitleLoading.setVisibility(View.VISIBLE);
        rvSubtitles.setVisibility(View.GONE);

        new Thread(() -> {
            List<Subtitle> cached = readLocalSubtitles(videoId, "en");
            if (!cached.isEmpty()) {
                allSubtitles = cached;
                runOnUiThread(() -> {
                    layoutSubtitleLoading.setVisibility(View.GONE);
                    if (startGame) {
                        startChooseWordGame();
                    } else {
                        rvSubtitles.setVisibility(View.VISIBLE);
                        setupSubtitleList(cached);
                    }
                });
                return;
            }

            try {
                List<Subtitle> serverSubs = requestServerSubtitles(videoId, "en");
                if (serverSubs == null || serverSubs.isEmpty()) {
                    runOnUiThread(() -> {
                        layoutSubtitleLoading.setVisibility(View.GONE);
                        Toast.makeText(this, "Không lấy được nội dung video từ server", Toast.LENGTH_LONG).show();
                        if (startGame) {
                            layoutChooseWord.setVisibility(View.GONE);
                            findViewById(R.id.layoutSelection).setVisibility(View.VISIBLE);
                        }
                    });
                    return;
                }

                allSubtitles = serverSubs;
                saveSubtitlesToRoom(videoId, "en", "server", "server_asr", serverSubs);

                runOnUiThread(() -> {
                    layoutSubtitleLoading.setVisibility(View.GONE);
                    if (startGame) {
                        startChooseWordGame();
                    } else {
                        rvSubtitles.setVisibility(View.VISIBLE);
                        setupSubtitleList(serverSubs);
                    }
                });
            } catch (Exception e) {
                Log.e("VideoPlayer", "Server subtitle error", e);
                runOnUiThread(() -> {
                    layoutSubtitleLoading.setVisibility(View.GONE);
                    Toast.makeText(this, "Không thể kết nối server subtitle", Toast.LENGTH_SHORT).show();
                });
            }

        }).start();
    }

    private List<Subtitle> readLocalSubtitles(String targetVideoId, String language) {
        List<Subtitle> cached = new ArrayList<>();
        try {
            List<SubtitleEntity> ents = subtitleDao.getByVideoAndLanguage(targetVideoId, language);
            for (SubtitleEntity e : ents) {
                cached.add(new Subtitle(e.text, e.start_time, e.end_time));
            }
        } catch (Exception ex) {
            cached = dbHelper.getSavedSubtitles(targetVideoId, language);
        }
        return cached == null ? new ArrayList<>() : cached;
    }

    private void saveSubtitlesToRoom(String targetVideoId, String language, String trackId, String source, List<Subtitle> subs) {
        new Thread(() -> {
            try {
                List<SubtitleEntity> ents = new ArrayList<>();
                long now = System.currentTimeMillis();
                for (Subtitle s : subs) {
                    ents.add(new SubtitleEntity(targetVideoId, s.getText(), s.getStartTime(), s.getEndTime(), language, trackId, source, now));
                }
                subtitleDao.replaceForVideoAndLanguage(targetVideoId, language, ents);
            } catch (Exception e) {
                Log.w("VideoPlayer", "Failed to save subtitles batch: " + e.getMessage());
                try {
                    dbHelper.saveSubtitlesBatch(targetVideoId, language, trackId, source, subs);
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    private List<Subtitle> requestServerSubtitles(String targetVideoId, String language) throws Exception {
        JSONObject transcript = fetchTranscriptJson(targetVideoId, language);
        String status = transcript.optString("status", "");

        if ("done".equals(status)) {
            return parseServerSubtitleList(transcript);
        }

        if ("not_found".equals(status) || "error".equals(status)) {
            enqueueTranscription(targetVideoId, language);
        }

        final int maxAttempts = 120;
        for (int i = 0; i < maxAttempts; i++) {
            Thread.sleep(2000);
            JSONObject poll = fetchTranscriptJson(targetVideoId, language);
            String pollStatus = poll.optString("status", "");
            if ("done".equals(pollStatus)) {
                return parseServerSubtitleList(poll);
            }
            if ("error".equals(pollStatus)) {
                throw new RuntimeException("Server trả về trạng thái error khi tạo transcript");
            }
            if ("not_found".equals(pollStatus) && i > 2) {
                enqueueTranscription(targetVideoId, language);
            }
        }

        throw new RuntimeException("Timeout khi chờ transcript từ server");
    }

    private JSONObject fetchTranscriptJson(String targetVideoId, String language) throws Exception {
        HttpUrl url = HttpUrl.parse(SERVER_BASE + "/transcript").newBuilder()
                .addQueryParameter("videoId", targetVideoId)
                .addQueryParameter("language", language)
                .build();

        Request getReq = new Request.Builder().url(url).get().build();
        try (Response resp = httpClient.newCall(getReq).execute()) {
            if (resp.code() == 404) {
                JSONObject j = new JSONObject();
                j.put("status", "not_found");
                return j;
            }
            if (!resp.isSuccessful()) {
                throw new RuntimeException("Transcript API HTTP " + resp.code());
            }
            String body = resp.body() != null ? resp.body().string() : "{}";
            return new JSONObject(body);
        }
    }

    private void enqueueTranscription(String targetVideoId, String language) throws Exception {
        JSONObject p = new JSONObject();
        p.put("videoId", targetVideoId);
        p.put("language", language);
        RequestBody rb = RequestBody.create(MediaType.parse("application/json"), p.toString());
        Request postReq = new Request.Builder()
                .url(SERVER_BASE + "/transcribe")
                .post(rb)
                .build();

        try (Response postResp = httpClient.newCall(postReq).execute()) {
            if (!postResp.isSuccessful()) {
                throw new RuntimeException("Transcribe API HTTP " + postResp.code());
            }
        }
    }

    private List<Subtitle> parseServerSubtitleList(JSONObject jsonObject) {
        List<Subtitle> result = new ArrayList<>();
        org.json.JSONArray segments = jsonObject.optJSONArray("segments");
        String transcript = jsonObject.optString("transcript", "");

        if (segments != null && segments.length() > 0) {
            boolean isWordLevel = false;
            JSONObject first = segments.optJSONObject(0);
            if (first != null && first.has("word")) {
                isWordLevel = true;
            }

            if (isWordLevel) {
                final int GROUP_SIZE = 6;
                List<String> bufferWords = new ArrayList<>();
                float groupStart = 0f;
                float groupEnd = 0f;
                int wordCount = 0;

                for (int i = 0; i < segments.length(); i++) {
                    JSONObject w = segments.optJSONObject(i);
                    if (w == null) {
                        continue;
                    }
                    String word = w.optString("word", "").trim();
                    if (word.isEmpty()) {
                        continue;
                    }
                    float wstart = (float) w.optDouble("start", 0.0);
                    float wend = (float) w.optDouble("end", 0.0);

                    if (wordCount == 0) {
                        groupStart = wstart;
                        groupEnd = wend;
                    } else {
                        groupEnd = Math.max(groupEnd, wend);
                    }

                    bufferWords.add(word);
                    wordCount++;

                    if (wordCount >= GROUP_SIZE) {
                        result.add(new Subtitle(String.join(" ", bufferWords), groupStart, groupEnd));
                        bufferWords.clear();
                        wordCount = 0;
                    }
                }

                if (!bufferWords.isEmpty()) {
                    result.add(new Subtitle(String.join(" ", bufferWords), groupStart, groupEnd));
                }
            } else {
                for (int i = 0; i < segments.length(); i++) {
                    JSONObject s = segments.optJSONObject(i);
                    if (s == null) {
                        continue;
                    }
                    float start = (float) s.optDouble("start", 0.0);
                    float end = (float) s.optDouble("end", 0.0);
                    String text = s.optString("text", "").trim();
                    if (!text.isEmpty()) {
                        result.add(new Subtitle(text, start, end));
                    }
                }
            }
        } else if (!transcript.trim().isEmpty()) {
            String[] parts = transcript.split("(?<=[.!?])\\s+");
            for (String p : parts) {
                String text = p.trim();
                if (!text.isEmpty()) {
                    result.add(new Subtitle(text, 0f, 0f));
                }
            }
        }

        return result;
    }

    // ---------------------- Vosk server integration helpers ----------------------
    private final OkHttpClient httpClient = new OkHttpClient();
    // Nếu chạy emulator dùng 10.0.2.2, nếu chạy máy thật hãy sửa thành IP LAN của PC (ví dụ: http://192.168.1.10:5000)
    private final String SERVER_BASE = "http://10.0.2.2:5000";

    // Ensure transcript exists on server; if not, enqueue transcription and poll until ready
    private void ensureTranscriptAndSave(String videoId, String language) {
        // Được giữ lại để tránh vỡ các luồng cũ; luồng mới dùng requestServerSubtitles() trong loadSubtitles().
        new Thread(() -> {
            try {
                List<Subtitle> list = requestServerSubtitles(videoId, language);
                if (list != null && !list.isEmpty()) {
                    saveSubtitlesToRoom(videoId, language, "server", "server_asr", list);
                    runOnUiThread(() -> {
                        allSubtitles = list;
                        layoutSubtitleLoading.setVisibility(View.GONE);
                        rvSubtitles.setVisibility(View.VISIBLE);
                        setupSubtitleList(list);
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Không thể kết nối server transcript", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void pollUntilDone(String videoId, String language) {
        // Deprecated: đã thay bằng requestServerSubtitles().
        try {
            List<Subtitle> list = requestServerSubtitles(videoId, language);
            if (list != null && !list.isEmpty()) {
                saveSubtitlesToRoom(videoId, language, "server", "server_asr", list);
            }
        } catch (Exception ignored) {
        }
    }

    private void saveTranscriptToRoom(String videoId, String language, String transcript, org.json.JSONArray segments) {
        // Deprecated: chuyển sang parseServerSubtitleList + saveSubtitlesToRoom.
        try {
            JSONObject j = new JSONObject();
            j.put("transcript", transcript == null ? "" : transcript);
            if (segments != null) {
                j.put("segments", segments);
            }
            List<Subtitle> list = parseServerSubtitleList(j);
            if (!list.isEmpty()) {
                saveSubtitlesToRoom(videoId, language, "server", "server_asr", list);
                runOnUiThread(() -> {
                    allSubtitles = list;
                    setupSubtitleList(list);
                });
            }
        } catch (Exception ex) {
            runOnUiThread(() -> Toast.makeText(this, "Lưu transcript vào DB thất bại", Toast.LENGTH_SHORT).show());
        }
    }

    private void setupSubtitleList(List<Subtitle> subs) {
        subtitleAdapter = new SubtitleAdapter(subs,
                new SubtitleAdapter.OnSubtitleClickListener() {

                    @Override
                    public void onPauseClick() {
                        if (activePlayer != null) {
                            activePlayer.pause();
                        }
                    }

                    @Override
                    public void onResumeClick() {
                        if (activePlayer != null) {
                            activePlayer.play();
                        }
                    }

                    @Override
                    public void onSubtitleClick(Subtitle subtitle) {
                        if (activePlayer != null) {
                            activePlayer.seekTo(subtitle.getStartTime());
                            activePlayer.play();
                        }

                        new Thread(() -> {
                            try {
                                long now = System.currentTimeMillis();
                                SubtitleEntity e = new SubtitleEntity(
                                        videoId,
                                        subtitle.getText(),
                                        subtitle.getStartTime(),
                                        subtitle.getEndTime(),
                                        "en",
                                        "",
                                        "manual",
                                        now
                                );
                                List<SubtitleEntity> list = new ArrayList<>();
                                list.add(e);
                                subtitleDao.insertAll(list);
                            } catch (Exception ex) {
                                dbHelper.saveSubtitle(videoId, subtitle);
                            }
                        }).start();

                        Toast.makeText(VideoPlayerActivity.this, "Đã lưu subtitle", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onWordClick(String word) {
                        showTranslationDialog(word);
                    }
                });

        rvSubtitles.setAdapter(subtitleAdapter);
    }

    private void startChooseWordGame() {
        if (allSubtitles == null || allSubtitles.isEmpty()) return;

        isGameMode = true;
        isGamePaused = false;
        lastGamePausedIndex = -1;
        currentGameIndex = -1;
        correctWords.clear();
        skippedWords.clear();

        List<Subtitle> gameSubtitles = buildChooseWordSegments(allSubtitles);

        // Generate game data
        gameSubtitleList = ChooseWordAdapter.generateGameData(gameSubtitles);

        chooseWordAdapter = new ChooseWordAdapter(gameSubtitleList, new ChooseWordAdapter.OnGameActionListener() {
            @Override
            public void onToggleSentencePlayback(int position, Subtitle subtitle) {
                if (activePlayer == null) return;

                boolean isSameActiveRow = (position == currentGameIndex);
                if (isSameActiveRow) {
                    if (isPlayerPlaying) {
                        activePlayer.pause();
                        isGamePaused = true;
                    } else {
                        lastGamePausedIndex = -1;
                        isGamePaused = false;
                        activePlayer.play();
                    }
                    return;
                }

                currentGameIndex = position;
                lastGamePausedIndex = -1;
                isGamePaused = false;
                activePlayer.seekTo(subtitle.getStartTime());
                activePlayer.play();
            }

            @Override
            public void onTranslateClick(String text) {
                showTranslationDialog(text);
            }
        });

        rvGameSubtitles.setAdapter(chooseWordAdapter);
        chooseWordAdapter.setPlayerPlaying(isPlayerPlaying);

        // Clear initial options
        btnGameOpt1.setText("");
        btnGameOpt2.setText("");
        btnGameOpt3.setText("");
        btnGameOpt4.setText("");
    }

    private List<Subtitle> buildChooseWordSegments(List<Subtitle> input) {
        List<Subtitle> result = new ArrayList<>();
        for (Subtitle s : input) {
            String text = s.getText() == null ? "" : s.getText().trim();
            if (text.isEmpty()) continue;

            String[] words = text.split("\\s+");
            if (words.length <= GAME_MAX_WORDS_PER_LINE || s.getEndTime() <= s.getStartTime()) {
                result.add(s);
                continue;
            }

            float start = s.getStartTime();
            float end = s.getEndTime();
            float duration = Math.max(0f, end - start);
            float secondsPerWord = duration / Math.max(1, words.length);

            int cursor = 0;
            while (cursor < words.length) {
                int chunkEnd = Math.min(words.length, cursor + GAME_MAX_WORDS_PER_LINE);
                String chunkText = String.join(" ", java.util.Arrays.copyOfRange(words, cursor, chunkEnd));
                float chunkStart = start + (cursor * secondsPerWord);
                float chunkEndTime = start + (chunkEnd * secondsPerWord);
                result.add(new Subtitle(chunkText, chunkStart, chunkEndTime));
                cursor = chunkEnd;
            }
        }
        return result;
    }

    private int findSubtitleIndex(Subtitle subtitle) {
        if (gameSubtitleList == null || subtitle == null) return -1;
        for (int i = 0; i < gameSubtitleList.size(); i++) {
            Subtitle item = gameSubtitleList.get(i).subtitle;
            if (Math.abs(item.getStartTime() - subtitle.getStartTime()) < 0.05f
                    && Math.abs(item.getEndTime() - subtitle.getEndTime()) < 0.05f) {
                return i;
            }
        }
        return -1;
    }

    private void prepareGameQuestion(int index) {
        if (gameSubtitleList == null || index < 0 || index >= gameSubtitleList.size()) return;

        ChooseWordAdapter.GameSubtitle gs = gameSubtitleList.get(index);
        if (gs.isRevealed || gs.hiddenWordIndex < 0) {
            // Already answered or no hidden word, skip
            return;
        }

        List<String> options = ChooseWordAdapter.generateOptions(gs, gameSubtitleList);
        String correctWord = gs.hiddenWord.replaceAll("[^a-zA-Z']", "");
        if (correctWord.isEmpty()) correctWord = gs.hiddenWord;

        btnGameOpt1.setText(options.get(0));
        btnGameOpt2.setText(options.get(1));
        btnGameOpt3.setText(options.get(2));
        btnGameOpt4.setText(options.get(3));

        final String finalCorrect = correctWord;
        final int gameIdx = index;

        View.OnClickListener optionListener = v -> {
            String selected = ((Button) v).getText().toString();
            checkGameAnswer(selected, finalCorrect, gameIdx);
        };

        btnGameOpt1.setOnClickListener(optionListener);
        btnGameOpt2.setOnClickListener(optionListener);
        btnGameOpt3.setOnClickListener(optionListener);
        btnGameOpt4.setOnClickListener(optionListener);
    }

    private void checkGameAnswer(String selected, String correct, int gameIndex) {
        if (gameSubtitleList == null || gameIndex < 0 || gameIndex >= gameSubtitleList.size()) return;

        ChooseWordAdapter.GameSubtitle gs = gameSubtitleList.get(gameIndex);
        if (gs.isRevealed) return;

        // Find the clicked button
        Button clickedBtn = null;
        if (btnGameOpt1.getText().toString().equals(selected)) clickedBtn = btnGameOpt1;
        else if (btnGameOpt2.getText().toString().equals(selected)) clickedBtn = btnGameOpt2;
        else if (btnGameOpt3.getText().toString().equals(selected)) clickedBtn = btnGameOpt3;
        else if (btnGameOpt4.getText().toString().equals(selected)) clickedBtn = btnGameOpt4;

        if (selected.equalsIgnoreCase(correct)) {
            // Correct! Flash button blue/green
            if (clickedBtn != null) {
                clickedBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF43A047)); // Green
                final Button btn = clickedBtn;
                new Handler().postDelayed(() -> {
                    btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF283593)); // Reset
                }, 500);
            }
            gs.isRevealed = true;
            gs.isCorrect = true;
            gs.isSkipped = false;
            correctWords.add(normalizeWord(correct));
            if (chooseWordAdapter != null) chooseWordAdapter.notifyItemChanged(gameIndex);

            new Handler().postDelayed(() -> continueGameAfterDecision(gameIndex), 450);
        } else {
            // Wrong - flash button red then reset
            if (clickedBtn != null) {
                clickedBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFE53935)); // Red
                final Button btn = clickedBtn;
                new Handler().postDelayed(() -> {
                    btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF283593)); // Reset
                }, 600);
            }
        }
    }

    private String normalizeWord(String rawWord) {
        if (rawWord == null) return "";
        String cleaned = rawWord.replaceAll("[^a-zA-Z']", "").trim();
        return cleaned.isEmpty() ? rawWord.trim() : cleaned;
    }

    private void continueGameAfterDecision(int answeredIndex) {
        isGamePaused = false;
        if (chooseWordAdapter != null) {
            chooseWordAdapter.setCurrentActiveIndex(-1);
        }

        float resumeAt = latestPlaybackSecond + GAME_RESUME_EPSILON_SECONDS;
        if (gameSubtitleList != null && answeredIndex >= 0 && answeredIndex < gameSubtitleList.size()) {
            float end = gameSubtitleList.get(answeredIndex).subtitle.getEndTime();
            if (end > 0f) {
                resumeAt = Math.max(resumeAt, end + GAME_RESUME_EPSILON_SECONDS);
            }
        }

        currentGameIndex = -1;
        if (activePlayer != null) {
            activePlayer.seekTo(resumeAt);
            activePlayer.play();
        }
    }

    private void showWordStatsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_word_stats_video, null);
        builder.setView(dialogView);

        int totalWords = (gameSubtitleList != null) ? gameSubtitleList.size() : 0;

        TextView tvTitle = dialogView.findViewById(R.id.tvStatsTitle);
        tvTitle.setText("Tổng số từ (" + totalWords + ")");

        TextView tvTabCorrect = dialogView.findViewById(R.id.tvTabCorrect);
        TextView tvTabSkipped = dialogView.findViewById(R.id.tvTabSkipped);
        tvTabCorrect.setText("TỪ CHỌN ĐÚNG(" + correctWords.size() + ")");
        tvTabSkipped.setText("TỪ BỎ QUA(" + skippedWords.size() + ")");

        android.widget.ListView lvCorrect = dialogView.findViewById(R.id.lvCorrectWords);
        android.widget.ListView lvSkipped = dialogView.findViewById(R.id.lvSkippedWords);
        TextView tvEmpty = dialogView.findViewById(R.id.tvEmptyState);

        // Custom colored word adapter for correct words (blue)
        ArrayAdapter<String> correctAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, correctWords) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                tv.setTextColor(0xFF1565C0); // Blue
                tv.setTextSize(18);
                tv.setPadding(16, 12, 16, 12);
                return view;
            }
        };

        // Custom colored word adapter for skipped words (orange)
        ArrayAdapter<String> skippedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, skippedWords) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                tv.setTextColor(0xFFFF8F00); // Orange
                tv.setTextSize(18);
                tv.setPadding(16, 12, 16, 12);
                return view;
            }
        };

        lvCorrect.setAdapter(correctAdapter);
        lvSkipped.setAdapter(skippedAdapter);

        // Show correct words by default
        tvEmpty.setVisibility(correctWords.isEmpty() ? View.VISIBLE : View.GONE);
        lvCorrect.setVisibility(correctWords.isEmpty() ? View.GONE : View.VISIBLE);

        tvTabCorrect.setOnClickListener(v -> {
            tvTabCorrect.setTextColor(0xFF1A237E);
            tvTabCorrect.setTextSize(13);
            tvTabSkipped.setTextColor(0xFF666666);
            lvCorrect.setVisibility(correctWords.isEmpty() ? View.GONE : View.VISIBLE);
            lvSkipped.setVisibility(View.GONE);
            tvEmpty.setVisibility(correctWords.isEmpty() ? View.VISIBLE : View.GONE);
        });

        tvTabSkipped.setOnClickListener(v -> {
            tvTabSkipped.setTextColor(0xFF1A237E);
            tvTabCorrect.setTextColor(0xFF666666);
            lvCorrect.setVisibility(View.GONE);
            lvSkipped.setVisibility(skippedWords.isEmpty() ? View.GONE : View.VISIBLE);
            tvEmpty.setVisibility(skippedWords.isEmpty() ? View.VISIBLE : View.GONE);
        });

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();

        dialogView.findViewById(R.id.btnStatsOk).setOnClickListener(v -> dialog.dismiss());
    }

    private void showGameHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_game_help_video, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();

        dialogView.findViewById(R.id.btnHelpOk).setOnClickListener(v -> dialog.dismiss());
    }

    private void showTranslationDialog(String word) {
        if (activePlayer != null) {
            activePlayer.pause();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_translation_video, null);
        builder.setView(dialogView);

        TextView tvWord = dialogView.findViewById(R.id.tvDialogWord);
        TextView tvMeaning = dialogView.findViewById(R.id.tvDialogMeaning);
        View btnClose = dialogView.findViewById(R.id.btnDialogClose);

        tvWord.setText(word);
        tvMeaning.setText("Đang tra nghĩa cho từ '" + word + "'...");

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();

        btnClose.setOnClickListener(v -> dialog.dismiss());

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                tvMeaning.setText("Nghĩa của từ '" + word + "' sẽ được hiển thị ở đây. (Tính năng đang phát triển)");
            }
        }, 1000);
    }
}
