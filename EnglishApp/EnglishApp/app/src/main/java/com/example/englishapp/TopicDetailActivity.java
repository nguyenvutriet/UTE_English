package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TopicDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TOPIC_ID = "topic_id";

    private Topic topic;
    private List<TopicWord> words;
    private PronunciationHelper pronunciationHelper;
    private TopicWordAdapter adapter;
    private int currentIndex;
    private boolean showingBack;
    private boolean isFlipping;

    private TextView topicHeaderProgress;
    private TextView flashcardIndex;
    private ImageView flashcardImage;
    private TextView flashcardFaceLabel;
    private TextView flashcardWord;
    private TextView flashcardType;
    private TextView flashcardMeaning;
    private TextView flashcardExample;
    private TextView flashcardHint;
    private TextView flashcardUk;
    private TextView flashcardUs;
    private View flashcardUkRow;
    private View flashcardUsRow;
    private Button toggleLearnedButton;
    private View flashcardCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);

        String topicId = getIntent().getStringExtra(EXTRA_TOPIC_ID);
        topic = TopicRepository.getTopicById(topicId);
        words = TopicRepository.getWordsForTopic(topicId);

        if (topic == null || words.isEmpty()) {
            finish();
            return;
        }

        pronunciationHelper = new PronunciationHelper(this);
        bindViews();
        setupHeader();
        setupRecycler();
        setupActions();
        updateProgress();
        updateFlashcard();
    }

    @Override
    protected void onDestroy() {
        if (pronunciationHelper != null) {
            pronunciationHelper.release();
        }
        super.onDestroy();
    }

    private void bindViews() {
        topicHeaderProgress = findViewById(R.id.topicHeaderProgress);
        flashcardIndex = findViewById(R.id.flashcardIndex);
        flashcardImage = findViewById(R.id.flashcardImage);
        flashcardFaceLabel = findViewById(R.id.flashcardFaceLabel);
        flashcardWord = findViewById(R.id.flashcardWord);
        flashcardType = findViewById(R.id.flashcardType);
        flashcardMeaning = findViewById(R.id.flashcardMeaning);
        flashcardExample = findViewById(R.id.flashcardExample);
        flashcardHint = findViewById(R.id.flashcardHint);
        flashcardUk = findViewById(R.id.flashcardUk);
        flashcardUs = findViewById(R.id.flashcardUs);
        flashcardUkRow = findViewById(R.id.flashcardUkRow);
        flashcardUsRow = findViewById(R.id.flashcardUsRow);
        toggleLearnedButton = findViewById(R.id.btnToggleLearned);
        flashcardCard = findViewById(R.id.flashcardCard);

        float scale = getResources().getDisplayMetrics().density;
        flashcardCard.setCameraDistance(12000f * scale);
    }

    private void setupHeader() {
        ImageView topicHeaderImage = findViewById(R.id.topicHeaderImage);
        TextView topicHeaderTitle = findViewById(R.id.topicHeaderTitle);
        topicHeaderImage.setImageResource(topic.image);
        topicHeaderTitle.setText(topic.name);
    }

    private void setupRecycler() {
        RecyclerView recyclerView = findViewById(R.id.topicWordsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new TopicWordAdapter(this, words, pronunciationHelper, new TopicWordAdapter.Listener() {
            @Override
            public void onWordSelected(int position) {
                currentIndex = position;
                showingBack = false;
                resetFlipState();
                updateFlashcard();
            }

            @Override
            public void onLearnedStateChanged() {
                updateProgress();
                updateFlashcard();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupActions() {
        ImageView backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

//        View openMemoryStudy = findViewById(R.id.btnOpenMemoryStudy);
//        if (openMemoryStudy != null) {
//            openMemoryStudy.setOnClickListener(v -> {
//                Intent intent = new Intent(this, MemoryStudyActivity.class);
//                intent.putExtra(EXTRA_TOPIC_ID, topic.id);
//                startActivity(intent);
//            });
//        }

        flashcardCard.setOnClickListener(v -> animateFlashcardFlip());

        findViewById(R.id.btnPreviousCard).setOnClickListener(v -> moveCard(-1));
        findViewById(R.id.btnNextCard).setOnClickListener(v -> moveCard(1));
        findViewById(R.id.btnFlashcardSpeakUk).setOnClickListener(v -> pronunciationHelper.speakUk(getCurrentWord().word));
        findViewById(R.id.btnFlashcardSpeakUs).setOnClickListener(v -> pronunciationHelper.speakUs(getCurrentWord().word));

        if (toggleLearnedButton != null) {
            toggleLearnedButton.setEnabled(false);
        }
    }

    private void moveCard(int direction) {
        if (words.isEmpty()) {
            return;
        }

        currentIndex = (currentIndex + direction + words.size()) % words.size();
        showingBack = false;
        resetFlipState();
        updateFlashcard();
    }

    private void animateFlashcardFlip() {
        if (isFlipping || flashcardCard == null) {
            return;
        }

        isFlipping = true;
        flashcardCard.animate()
                .rotationY(90f)
                .setDuration(140)
                .withEndAction(() -> {
                    showingBack = !showingBack;
                    updateFlashcard();

                    flashcardCard.setRotationY(-90f);
                    flashcardCard.animate()
                            .rotationY(0f)
                            .setDuration(140)
                            .withEndAction(() -> isFlipping = false)
                            .start();
                })
                .start();
    }

    private void resetFlipState() {
        isFlipping = false;
        if (flashcardCard != null) {
            flashcardCard.animate().cancel();
            flashcardCard.setRotationY(0f);
        }
    }

    private void updateProgress() {
        topic.total = words.size();
        topic.learned = TopicProgressStore.getLearnedCount(this, topic.id, words);
        int percent = topic.total == 0 ? 0 : (topic.learned * 100) / topic.total;
        topicHeaderProgress.setText(topic.learned + "/" + topic.total + " từ đã học • " + percent + "%");
    }

    private void updateFlashcard() {
        TopicWord currentWord = getCurrentWord();
        TopicProgressStore.WordProgress progress = TopicProgressStore.getProgress(this, currentWord);

        adapter.setSelectedIndex(currentIndex);
        flashcardIndex.setText("Thẻ " + (currentIndex + 1) + "/" + words.size());
        flashcardImage.setImageResource(currentWord.imageRes);
        flashcardWord.setText(currentWord.word);
        flashcardType.setText(currentWord.type);
        flashcardMeaning.setText(currentWord.meaning);
        flashcardExample.setText("Ví dụ: " + currentWord.example);
        flashcardUk.setText(currentWord.uk);
        flashcardUs.setText(currentWord.us);
        if (toggleLearnedButton != null) {
            toggleLearnedButton.setText("Level " + progress.level);
        }

        int frontVisibility = showingBack ? View.GONE : View.VISIBLE;
        flashcardImage.setVisibility(frontVisibility);
        flashcardWord.setVisibility(frontVisibility);
        flashcardType.setVisibility(frontVisibility);

        int detailVisibility = showingBack ? View.VISIBLE : View.GONE;
        flashcardMeaning.setVisibility(detailVisibility);
        flashcardExample.setVisibility(detailVisibility);
        flashcardUkRow.setVisibility(detailVisibility);
        flashcardUsRow.setVisibility(detailVisibility);

        if (showingBack) {
            flashcardFaceLabel.setText("Mặt sau");
            flashcardHint.setText("Chạm vào thẻ để quay lại mặt trước.");
        } else {
            flashcardFaceLabel.setText("Mặt trước");
            flashcardHint.setText("Chạm vào thẻ để xem nghĩa, ví dụ và phiên âm.");
        }
    }

    private TopicWord getCurrentWord() {
        return words.get(currentIndex);
    }
}
