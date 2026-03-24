package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.adapter.RecentWordAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TopicActivity extends AppCompatActivity {

    private TextView tvLearnedCount, tvTotalCount, tvDueWords, tvReviewTitle, tvReviewDueTime;
    private android.widget.Button btnReviewNow;
    private ProgressBar progressLearned;
    private TextView[] tvBarCounts = new TextView[6];
    private View[] vBars = new View[6];

    private RecyclerView rvRecentWords, topicRecycler;
    private TopicAdapter topicAdapter;
    private RecentWordAdapter recentWordAdapter;
    private PronunciationHelper pronunciationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        pronunciationHelper = new PronunciationHelper(this);

        tvLearnedCount = findViewById(R.id.tvLearnedCount);
        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvDueWords = findViewById(R.id.tvDueWords);
        tvReviewTitle = findViewById(R.id.tvReviewTitle);
        tvReviewDueTime = findViewById(R.id.tvReviewDueTime);
        btnReviewNow = findViewById(R.id.btnReviewNow);
        progressLearned = findViewById(R.id.progressLearned);

        setupBarChart();

        rvRecentWords = findViewById(R.id.rvRecentWords);
        rvRecentWords.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recentWordAdapter = new RecentWordAdapter(pronunciationHelper, new ArrayList<>());
        rvRecentWords.setAdapter(recentWordAdapter);

        topicRecycler = findViewById(R.id.topicRecycler);
        topicRecycler.setLayoutManager(new LinearLayoutManager(this));
        topicAdapter = new TopicAdapter(new ArrayList<>(), topic -> {
            Intent intent = new Intent(TopicActivity.this, TopicDetailActivity.class);
            intent.putExtra(TopicDetailActivity.EXTRA_TOPIC_ID, topic.id);
            startActivity(intent);
        });
        topicRecycler.setAdapter(topicAdapter);

        View backBtn = findViewById(R.id.btnBack);
        if (backBtn != null) backBtn.setOnClickListener(v -> finish());

        if (btnReviewNow != null) btnReviewNow.setOnClickListener(v -> reviewDueGlobal());
    }

    private void setupBarChart() {
        int[] barIds = {R.id.bar1, R.id.bar2, R.id.bar3, R.id.bar4, R.id.bar5, R.id.barDeep};
        String[] labels = {"1", "2", "3", "4", "5", "Nhớ sâu"};

        for (int i = 0; i < 6; i++) {
            View b = findViewById(barIds[i]);
            if (b != null) {
                tvBarCounts[i] = b.findViewById(R.id.tvBarCount);
                vBars[i] = b.findViewById(R.id.vBar);
                TextView tvLabel = b.findViewById(R.id.tvBarLabel);
                if (tvLabel != null) tvLabel.setText(labels[i]);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboard();
    }

    @Override
    protected void onDestroy() {
        if (pronunciationHelper != null) {
            pronunciationHelper.release();
        }
        super.onDestroy();
    }

    private void refreshDashboard() {
        List<Topic> topics = TopicRepository.getTopics();

        long totalWords = 0;
        long totalLearned = 0;
        long totalDue = 0;
        long totalNew = 0;
        long nextReviewAt = Long.MAX_VALUE;

        int[] levelCounts = new int[6];
        List<TopicWord> allReviewedWords = new ArrayList<>();

        for (Topic topic : topics) {
            List<TopicWord> words = TopicRepository.getWordsForTopic(topic.id);
            topic.total = words.size();
            topic.learned = TopicProgressStore.getLearnedCount(this, topic.id, words);

            totalWords += topic.total;
            totalLearned += topic.learned;
            totalDue += TopicProgressStore.getDueCount(this, topic.id, words);

            for (TopicWord w : words) {
                TopicProgressStore.WordProgress progress = TopicProgressStore.getProgress(this, w);
                if (progress.level > 0 && progress.lastReviewedAt > 0L && progress.nextReviewAt > 0L) {
                    nextReviewAt = Math.min(nextReviewAt, progress.nextReviewAt);
                }

                if (progress.lastReviewedAt == 0) {
                    totalNew++;
                } else if (progress.lastReviewedAt > 0) {
                    allReviewedWords.add(w);

                    int lvl = progress.level;
                    boolean deep = (progress.nextReviewAt - progress.lastReviewedAt >= 8L * 24L * 60L * 60L * 1000L);

                    if (lvl >= 1 && lvl <= 5) {
                        if (deep && lvl == 5) {
                            levelCounts[5]++;
                        } else {
                            if (lvl == 1) levelCounts[0]++;
                            else if (lvl == 2) levelCounts[1]++;
                            else if (lvl == 3) levelCounts[2]++;
                            else if (lvl == 4) levelCounts[3]++;
                            else if (lvl == 5) levelCounts[4]++;
                        }
                    }
                }
            }
        }
        topicAdapter.updateTopics(topics);

        if (tvTotalCount != null) tvTotalCount.setText("/" + totalWords);
        if (tvLearnedCount != null) tvLearnedCount.setText(String.valueOf(totalLearned));
        if (progressLearned != null) {
            int percent = (int) (totalWords == 0 ? 0 : (totalLearned * 100 / totalWords));
            progressLearned.setProgress(percent);
        }

        if (tvReviewTitle != null && tvDueWords != null && btnReviewNow != null) {
            if (totalDue > 0) {
                tvReviewTitle.setText("Đã đến lúc ôn tập");
                tvDueWords.setText(totalDue + " từ");
                tvDueWords.setTextColor(android.graphics.Color.parseColor("#FF6B6B"));
                if (tvReviewDueTime != null) {
                    tvReviewDueTime.setVisibility(View.VISIBLE);
                    tvReviewDueTime.setText("Có từ đã đến hạn, nên ôn ngay.");
                }
                btnReviewNow.setText("Ôn tập ngay");
                btnReviewNow.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#A8C5FD")));
                btnReviewNow.setTextColor(android.graphics.Color.parseColor("#1A2B4C"));
            } else if (totalNew > 0) {
                tvReviewTitle.setText("Học từ mới?");
                tvDueWords.setText("Chưa có từ cần ôn tập" + '\n' + totalNew + " từ chưa học");
                tvDueWords.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
                if (tvReviewDueTime != null) {
                    tvReviewDueTime.setVisibility(View.VISIBLE);
                    if (nextReviewAt != Long.MAX_VALUE) {
                        tvReviewDueTime.setText("Lần ôn tiếp theo: " + formatDueTime(nextReviewAt));
                    } else {
                        tvReviewDueTime.setText("Chưa có lịch ôn tập từ cũ.");
                    }
                }
                btnReviewNow.setText("Học ngay");
                btnReviewNow.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50")));
                btnReviewNow.setTextColor(android.graphics.Color.WHITE);
            } else {
                tvReviewTitle.setText("Tuyệt vời!");
                tvDueWords.setText("Bạn đã thuộc mọi từ");
                tvDueWords.setTextColor(android.graphics.Color.parseColor("#AAAAAA"));
                if (tvReviewDueTime != null) {
                    tvReviewDueTime.setVisibility(View.VISIBLE);
                    if (nextReviewAt != Long.MAX_VALUE) {
                        tvReviewDueTime.setText("Lần ôn tiếp theo: " + formatDueTime(nextReviewAt));
                    } else {
                        tvReviewDueTime.setText("Không còn lịch ôn tập.");
                    }
                }
                btnReviewNow.setText("Ôn tập ngẫu nhiên");
                btnReviewNow.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#555555")));
                btnReviewNow.setTextColor(android.graphics.Color.WHITE);
            }
        }

        allReviewedWords.sort((w1, w2) -> {
            TopicProgressStore.WordProgress p1 = TopicProgressStore.getProgress(this, w1);
            TopicProgressStore.WordProgress p2 = TopicProgressStore.getProgress(this, w2);
            return Long.compare(p2.lastReviewedAt, p1.lastReviewedAt);
        });

        int maxRecent = Math.min(10, allReviewedWords.size());
        if (recentWordAdapter != null) {
            recentWordAdapter.updateData(allReviewedWords.subList(0, maxRecent));
        }

        updateBarChartViews(levelCounts);
    }

    private String formatDueTime(long dueAtMillis) {
        long now = System.currentTimeMillis();
        long diffMillis = dueAtMillis - now;

        if (diffMillis <= 0L) {
            return "Đến hạn ngay bây giờ";
        }

        long totalMinutes = (diffMillis + 59_999L) / 60_000L;
        String relative;
        if (totalMinutes < 60L) {
            relative = "sau " + totalMinutes + " phút";
        } else if (totalMinutes < 24L * 60L) {
            long hours = totalMinutes / 60L;
            long minutes = totalMinutes % 60L;
            relative = minutes == 0L
                    ? "sau " + hours + " giờ"
                    : "sau " + hours + " giờ " + minutes + " phút";
        } else {
            long days = totalMinutes / (24L * 60L);
            long hours = (totalMinutes % (24L * 60L)) / 60L;
            relative = hours == 0L
                    ? "sau " + days + " ngày"
                    : "sau " + days + " ngày " + hours + " giờ";
        }

        String exact = new SimpleDateFormat("HH:mm, dd/MM", Locale.getDefault())
                .format(new Date(dueAtMillis));
        return relative + " (" + exact + ")";
    }

    private void updateBarChartViews(int[] levelCounts) {
        int maxCount = 1;
        for (int c : levelCounts) {
            if (c > maxCount) maxCount = c;
        }

        int[] colors = {
                0xFFF44336, // Red
                0xFFFF9800, // Orange
                0xFFFFC107, // Yellow
                0xFF8BC34A, // Light Green
                0xFF4CAF50, // Green
                0xFF2196F3  // Blue
        };

        for (int i = 0; i < 6; i++) {
            if (tvBarCounts[i] == null || vBars[i] == null) continue;

            tvBarCounts[i].setText(levelCounts[i] + " từ");

            float barWeight = (float) levelCounts[i] / (float) maxCount;
            if (barWeight < 0.01f) barWeight = 0.01f;
            float spaceWeight = 1.0f - barWeight;

            View parent = (View) vBars[i].getParent();
            View spaceAbove = parent.findViewById(R.id.spaceAbove);

            LinearLayout.LayoutParams vParams = (LinearLayout.LayoutParams) vBars[i].getLayoutParams();
            vParams.weight = barWeight;
            vBars[i].setLayoutParams(vParams);

            if (spaceAbove != null) {
                LinearLayout.LayoutParams sParams = (LinearLayout.LayoutParams) spaceAbove.getLayoutParams();
                sParams.weight = spaceWeight;
                spaceAbove.setLayoutParams(sParams);
            }

            vBars[i].setBackgroundColor(colors[i]);
        }
    }

    private void reviewDueGlobal() {
        Topic targetTopic = null;

        // 1. Prioritize topics with due words
        for (Topic t : TopicRepository.getTopics()) {
            int due = TopicProgressStore.getDueCount(this, t.id, TopicRepository.getWordsForTopic(t.id));
            if (due > 0) {
                targetTopic = t;
                break;
            }
        }

        // 2. If no due words, find a topic with new words
        if (targetTopic == null) {
            for (Topic t : TopicRepository.getTopics()) {
                List<TopicWord> words = TopicRepository.getWordsForTopic(t.id);
                boolean hasNew = false;
                for (TopicWord w : words) {
                    if (TopicProgressStore.isNew(this, w)) {
                        hasNew = true;
                        break;
                    }
                }
                if (hasNew) {
                    targetTopic = t;
                    break;
                }
            }
        }

        // 3. Fallback to the very first topic
        if (targetTopic == null) {
            List<Topic> topics = TopicRepository.getTopics();
            if (!topics.isEmpty()) {
                targetTopic = topics.get(0);
            }
        }

        if (targetTopic != null) {
            Intent intent = new Intent(this, MemoryStudyActivity.class);
            intent.putExtra(TopicDetailActivity.EXTRA_TOPIC_ID, targetTopic.id);
            startActivity(intent);
        }
    }
}