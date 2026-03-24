package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class MemoryStudyActivity extends AppCompatActivity {

    private static final int SESSION_SIZE = 4;

    private enum PracticeMode {
        COPY,
        MEANING,
        LISTENING
    }

    private static final class PracticeItem {
        final TopicWord word;
        final PracticeMode mode;

        PracticeItem(TopicWord word, PracticeMode mode) {
            this.word = word;
            this.mode = mode;
        }
    }

    private Topic topic;
    private List<TopicWord> allWords;
    private List<TopicWord> sessionWords;
    private final List<PracticeItem> practiceItems = new ArrayList<>();
    private final Set<String> gradedWords = new HashSet<>();
    private final Map<String, Integer> initialLevels = new HashMap<>();
    private int currentPracticeIndex;
    private PronunciationHelper pronunciationHelper;
    private final Random random = new Random();

    private TextView titleText;
    private TextView meaningText;
    private TextView sessionInfoText;
    private TextView modeText;
    private TextView promptText;
    private TextView statusText;
    private TextView progressPercentText;
    private EditText answerInput;
    private ProgressBar progressBar;
    private Button checkButton;
    private Button nextButton;
    private Button speakButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_study);

        String topicId = getIntent().getStringExtra(TopicDetailActivity.EXTRA_TOPIC_ID);
        topic = TopicRepository.getTopicById(topicId);
        allWords = TopicRepository.getWordsForTopic(topicId);

        if (topic == null || allWords.isEmpty()) {
            finish();
            return;
        }

        pronunciationHelper = new PronunciationHelper(this);
        bindViews();
        setupActions();
        startNewSession();
        renderCurrentQuestion();
    }

    @Override
    protected void onDestroy() {
        if (pronunciationHelper != null) {
            pronunciationHelper.release();
        }
        super.onDestroy();
    }

    private void bindViews() {
        titleText = findViewById(R.id.memoryStudyTitle);
        meaningText = findViewById(R.id.memoryMeaning);
        sessionInfoText = findViewById(R.id.memorySessionInfo);
        modeText = findViewById(R.id.memoryMode);
        promptText = findViewById(R.id.memoryPrompt);
        statusText = findViewById(R.id.memoryStatus);
        progressPercentText = findViewById(R.id.memoryProgressPercent);
        answerInput = findViewById(R.id.memoryAnswerInput);
        progressBar = findViewById(R.id.memoryProgressBar);
        checkButton = findViewById(R.id.btnMemoryCheck);
        nextButton = findViewById(R.id.btnMemoryNext);
        speakButton = findViewById(R.id.btnMemorySpeak);

        titleText.setText(topic.name + " - Ôn theo cấp độ nhớ");
    }

    private void setupActions() {
        ImageView backButton = findViewById(R.id.btnMemoryBack);
        backButton.setOnClickListener(v -> finish());

        checkButton.setOnClickListener(v -> checkAnswer());
        nextButton.setOnClickListener(v -> goNextQuestion());
        speakButton.setOnClickListener(v -> {
            PracticeItem item = getCurrentPracticeItem();
            if (item != null) {
                pronunciationHelper.speakUs(item.word.word);
            }
        });
    }

    private void startNewSession() {
        sessionWords = TopicProgressStore.buildStudySessionWords(this, allWords, SESSION_SIZE);
        
        // Fill from other topics if we don't have enough
        if (sessionWords.size() < SESSION_SIZE) {
            int needed = SESSION_SIZE - sessionWords.size();
            List<Topic> allTopics = TopicRepository.getTopics();
            for (Topic t : allTopics) {
                if (!t.id.equals(topic.id)) {
                    List<TopicWord> otherWords = TopicRepository.getWordsForTopic(t.id);
                    List<TopicWord> additional = TopicProgressStore.buildStudySessionWords(this, otherWords, needed);
                    for (TopicWord w : additional) {
                        boolean exists = false;
                        for (TopicWord sw : sessionWords) {
                            if (sw.topicId.equals(w.topicId) && sw.word.equals(w.word)) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            sessionWords.add(w);
                            needed--;
                            if (needed <= 0) break;
                        }
                    }
                }
                if (needed <= 0) break;
            }
        }

        practiceItems.clear();
        gradedWords.clear();
        currentPracticeIndex = 0;

        if (sessionWords.isEmpty()) {
            return;
        }

        initialLevels.clear();
        for (TopicWord word : sessionWords) {
            initialLevels.put(word.word, TopicProgressStore.getProgress(this, word).level);
        }

        List<TopicWord> shuffledSessionWords = new ArrayList<>(sessionWords);
        Collections.shuffle(shuffledSessionWords, random);
        sessionWords = shuffledSessionWords;

        // New words are copied first, then all follow-up tasks are mixed.
        List<PracticeItem> mixedItems = new ArrayList<>();
        for (TopicWord word : sessionWords) {
            TopicProgressStore.WordProgress progress = TopicProgressStore.getProgress(this, word);
            boolean isNewWord = progress.lastReviewedAt == 0L;
            if (isNewWord) {
                practiceItems.add(new PracticeItem(word, PracticeMode.COPY));
                mixedItems.add(new PracticeItem(word, randomMixedMode()));
                mixedItems.add(new PracticeItem(word, randomMixedMode()));
            } else {
                mixedItems.add(new PracticeItem(word, randomMixedMode()));
                mixedItems.add(new PracticeItem(word, randomMixedMode()));
            }
        }

        Collections.shuffle(mixedItems, random);
        practiceItems.addAll(mixedItems);

        if (practiceItems.isEmpty()) {
            for (TopicWord word : sessionWords) {
                practiceItems.add(new PracticeItem(word, PracticeMode.MEANING));
            }
        }
    }

    private PracticeMode randomMixedMode() {
        return random.nextBoolean() ? PracticeMode.MEANING : PracticeMode.LISTENING;
    }

    private void renderCurrentQuestion() {
        if (practiceItems.isEmpty() || currentPracticeIndex >= practiceItems.size()) {
            showDoneState();
            return;
        }

        PracticeItem item = practiceItems.get(currentPracticeIndex);
        int currentStep = currentPracticeIndex + 1;
        int totalSteps = practiceItems.size();

        sessionInfoText.setText("Bước " + currentStep + "/" + totalSteps + " - Phiên " + SESSION_SIZE + " từ");
        statusText.setText("");
        answerInput.setText("");
        answerInput.setEnabled(true);
        checkButton.setEnabled(true);
        checkButton.setVisibility(View.VISIBLE);
        checkButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#8AB4F8")));
        nextButton.setEnabled(false);
        nextButton.setVisibility(View.GONE);
        nextButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#444444")));
        nextButton.setText("Từ tiếp theo");

        progressBar.setMax(totalSteps);
        progressBar.setProgress(currentPracticeIndex);
        progressPercentText.setText(((currentPracticeIndex * 100) / totalSteps) + "%");

        if (item.mode == PracticeMode.COPY) {
            meaningText.setVisibility(View.VISIBLE);
            meaningText.setText("Nghĩa: " + item.word.meaning);
            modeText.setText("Từ mới: chép lại từ");
            promptText.setText("Viết lại đúng từ tiếng Anh: " + item.word.word);
            speakButton.setVisibility(View.GONE);
        } else if (item.mode == PracticeMode.LISTENING) {
            meaningText.setVisibility(View.GONE);
            modeText.setText("Nghe + chép từ");
            promptText.setText("Nhấn nghe phát âm và nhập từ tiếng Anh (không hiện nghĩa).");
            speakButton.setVisibility(View.VISIBLE);
        } else {
            meaningText.setVisibility(View.VISIBLE);
            meaningText.setText("Nghĩa: " + item.word.meaning);
            modeText.setText("Nhìn nghĩa + điền từ");
            promptText.setText("Dựa vào nghĩa ở trên, nhập từ tiếng Anh đúng.");
            speakButton.setVisibility(View.GONE);
        }
    }

    private void checkAnswer() {
        PracticeItem item = getCurrentPracticeItem();
        if (item == null) {
            return;
        }

        String answer = normalize(answerInput.getText().toString());
        String target = normalize(item.word.word);

        if (TextUtils.isEmpty(answer)) {
            statusText.setText("Bạn chưa nhập câu trả lời.");
            return;
        }

        boolean correct = target.equals(answer);

        answerInput.setEnabled(false);
        checkButton.setEnabled(false);
        checkButton.setVisibility(View.GONE);
        checkButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#444444")));
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50")));

        if (correct) {
            if (item.mode != PracticeMode.COPY && !gradedWords.contains(item.word.word)) {
                TopicProgressStore.review(this, item.word, true);
                gradedWords.add(item.word.word);
            }
            statusText.setText("Kết quả: Đúng. Bạn đã trả lời chính xác.");
            statusText.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Green
            nextButton.setText("Tiếp tục");
            nextButton.setEnabled(true);
            return;
        }

        statusText.setTextColor(android.graphics.Color.parseColor("#FF6B6B")); // Red
        boolean canAdjustLevel = item.mode != PracticeMode.COPY && !gradedWords.contains(item.word.word);
        
        if (canAdjustLevel) {
            TopicProgressStore.WordProgress progress = TopicProgressStore.getProgress(this, item.word);
            if (progress.level > 0) {
                statusText.setText("Kết quả: Sai. Đáp án đúng: " + item.word.word);
                showWrongAnswerDialog(item);
            } else {
                TopicProgressStore.review(this, item.word, false);
                gradedWords.add(item.word.word);
                statusText.setText("Sai rồi. Đáp án đúng: " + item.word.word + ". Từ này đang ở Level 0 nên không bị giảm cấp độ nhớ.");
                nextButton.setText("Tiếp tục");
                nextButton.setEnabled(true);
            }
        } else {
            statusText.setText("Kết quả: Sai. Đáp án đúng: " + item.word.word);
            nextButton.setText("Tiếp tục");
            nextButton.setEnabled(true);
        }
    }

    private void showWrongAnswerDialog(PracticeItem item) {
        nextButton.setEnabled(false);

        TopicProgressStore.WordProgress progress = TopicProgressStore.getProgress(this, item.word);
        int currentLevel = progress.level;
        int decreasedLevel = Math.max(0, currentLevel - 1);

        new AlertDialog.Builder(this)
                .setTitle("Trả lời chưa đúng")
                .setMessage(
                        "Đáp án: " + item.word.word
                                + "\nLevel hiện tại: " + currentLevel
                                + "\nNếu giảm sẽ về level: " + decreasedLevel
                                + "\n\nBạn có muốn giảm cấp độ nhớ của từ này không?"
                )
                .setCancelable(false)
                .setPositiveButton("Giảm cấp độ", (dialog, which) -> {
                    TopicProgressStore.review(this, item.word, false);
                    gradedWords.add(item.word.word);
                    statusText.setText("Đã giảm cấp độ nhớ. Đáp án: " + item.word.word + ".");
                    nextButton.setText("Tiếp tục");
                    nextButton.setEnabled(true);
                })
                .setNegativeButton("Giữ nguyên", (dialog, which) -> {
                    gradedWords.add(item.word.word);
                    statusText.setText("Giữ nguyên cấp độ. Đáp án: " + item.word.word + ".");
                    nextButton.setText("Tiếp tục");
                    nextButton.setEnabled(true);
                })
                .show();
    }

    private void goNextQuestion() {
        // If user is on completion state, 'Hoàn tất' returns to topic list.
        if (practiceItems.isEmpty() || currentPracticeIndex >= practiceItems.size()) {
            openTopicScreen();
            return;
        }

        currentPracticeIndex++;
        renderCurrentQuestion();
    }

    private void showDoneState() {
        progressBar.setMax(100);
        progressBar.setProgress(100);
        progressPercentText.setText("100%");
        sessionInfoText.setText("Hoàn thành phiên học");

        int due = TopicProgressStore.getDueCount(this, topic.id, allWords);
        int learned = TopicProgressStore.getLearnedCount(this, topic.id, allWords);

        modeText.setText("Kết thúc");
        meaningText.setVisibility(View.VISIBLE);
        meaningText.setText("Tu da hoc (Level >= 1): " + learned + "/" + allWords.size());
        promptText.setText("Số từ đến hạn còn lại: " + due + ". Nếu không có từ đến hạn hoặc từ mới thì không cần học.");
        statusText.setText("Tiến độ đã cập nhật theo cấp độ nhớ.");

        answerInput.setEnabled(false);
        checkButton.setEnabled(false);
        nextButton.setEnabled(true);
        nextButton.setText("Hoàn tất");
        speakButton.setVisibility(View.GONE);

        // Hide quiz UI and build result list
        View quizCard = findViewById(R.id.quizCard);
        if (quizCard != null) {
            quizCard.setVisibility(View.GONE);
        }

        android.widget.LinearLayout resultContainer = findViewById(R.id.resultContainer);
        if (resultContainer != null) {
            resultContainer.setVisibility(View.VISIBLE);
            resultContainer.removeAllViews();
            
            android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
            Set<String> processed = new HashSet<>();
            
            for (PracticeItem item : practiceItems) {
                if (processed.add(item.word.word)) {
                    View itemView = inflater.inflate(R.layout.item_memory_result, resultContainer, false);
                    TextView tvWord = itemView.findViewById(R.id.tvResultWord);
                    TextView tvInit = itemView.findViewById(R.id.tvResultInitialLevel);
                    TextView tvFinal = itemView.findViewById(R.id.tvResultFinalLevel);
                    
                    int initLvl = initialLevels.containsKey(item.word.word) ? initialLevels.get(item.word.word) : 0;
                    int finalLvl = TopicProgressStore.getProgress(this, item.word).level;
                    
                    tvWord.setText(item.word.word);
                    tvInit.setText("Lv " + initLvl);
                    tvFinal.setText("Lv " + finalLvl);
                    
                    if (finalLvl > initLvl) {
                        tvFinal.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Green
                    } else if (finalLvl < initLvl) {
                        tvFinal.setTextColor(android.graphics.Color.parseColor("#FF6B6B")); // Red
                    } else {
                        tvFinal.setTextColor(android.graphics.Color.parseColor("#8AB4F8")); // Blue (no change)
                    }
                    
                    resultContainer.addView(itemView);
                }
            }
        }
    }

    private void openTopicScreen() {
        Intent intent = new Intent(this, TopicActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private PracticeItem getCurrentPracticeItem() {
        if (practiceItems.isEmpty() || currentPracticeIndex < 0 || currentPracticeIndex >= practiceItems.size()) {
            return null;
        }
        return practiceItems.get(currentPracticeIndex);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
