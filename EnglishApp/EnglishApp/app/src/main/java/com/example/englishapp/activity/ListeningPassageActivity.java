package com.example.englishapp.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishapp.DatabaseHelper;
import com.example.englishapp.PronunciationHelper;
import com.example.englishapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ListeningPassageActivity extends AppCompatActivity {

    public static final String EXTRA_LISTENING_TEST_ID = "listening_passage_test_id";

    private static class PassageQuestion {
        final String question;
        final List<String> options;
        final int correctIndex;

        PassageQuestion(String question, List<String> options, int correctIndex) {
            this.question = question;
            this.options = options;
            this.correctIndex = correctIndex;
        }
    }

    private static class PassageTest {
        final String title;
        final String transcript;
        final String translation;
        final List<PassageQuestion> questions;

        PassageTest(String title, String transcript, String translation, List<PassageQuestion> questions) {
            this.title = title;
            this.transcript = transcript;
            this.translation = translation;
            this.questions = questions;
        }
    }

    private final List<PassageTest> tests = new ArrayList<>();
    private final List<RadioGroup> answerGroups = new ArrayList<>();
    private final Random random = new Random();

    private PronunciationHelper pronunciationHelper;
    private PassageTest currentTest;
    private int currentTestId = -1;
    private boolean resultSaved = false;

    private ImageView btnBack;
    private Button btnPlayAudio;
    private Button btnSubmit;
    private TextView txtTestName;
    private LinearLayout questionsContainer;

    private LinearLayout resultLayout;
    private TextView txtScore;
    private TextView txtTranscript;
    private TextView txtTranslation;
    private TextView txtAnswerKey;

    private static final int LISTENING_PASSAGE_BASE_TEST_ID = 2200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listening_passage);

        applyWindowInsets();
        bindViews();
        buildData();

        pronunciationHelper = new PronunciationHelper(this);

        btnBack.setOnClickListener(v -> finish());
        btnPlayAudio.setOnClickListener(v -> playTranscript());
        btnSubmit.setOnClickListener(v -> submitAnswers());

        loadFromIntentOrRandom();
    }

    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        btnPlayAudio = findViewById(R.id.btnPlayAudio);

        btnSubmit = findViewById(R.id.btnSubmit);
        txtTestName = findViewById(R.id.txtTestName);
        questionsContainer = findViewById(R.id.questionsContainer);

        resultLayout = findViewById(R.id.resultLayout);
        txtScore = findViewById(R.id.txtScore);
        txtTranscript = findViewById(R.id.txtTranscript);
        txtTranslation = findViewById(R.id.txtTranslation);
        txtAnswerKey = findViewById(R.id.txtAnswerKey);
    }

    private void loadRandomTest() {
        if (tests.isEmpty()) {
            return;
        }
        currentTestId = random.nextInt(tests.size()) + 1;
        currentTest = tests.get(currentTestId - 1);
        txtTestName.setText(currentTest.title + " (ngẫu nhiên 1/5)");
        renderQuestions(currentTest.questions);
        resultLayout.setVisibility(View.GONE);
        btnSubmit.setEnabled(true);
    }

    private void renderQuestions(List<PassageQuestion> questions) {
        questionsContainer.removeAllViews();
        answerGroups.clear();

        for (int i = 0; i < questions.size(); i++) {
            PassageQuestion q = questions.get(i);

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(14), dp(14), dp(14), dp(14));
            card.setBackgroundResource(R.drawable.bg_white_rounded);

            LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardLp.setMargins(0, 0, 0, dp(12));
            card.setLayoutParams(cardLp);

            TextView txtQuestion = new TextView(this);
            txtQuestion.setText((i + 1) + ". " + q.question);
            txtQuestion.setTextSize(16);
            txtQuestion.setTextColor(getResources().getColor(R.color.text_dark));
            txtQuestion.setPadding(0, 0, 0, dp(8));
            card.addView(txtQuestion);

            RadioGroup group = new RadioGroup(this);
            group.setOrientation(LinearLayout.VERTICAL);

            for (int j = 0; j < q.options.size(); j++) {
                RadioButton rb = new RadioButton(this);
                char letter = (char) ('A' + j);
                rb.setText(letter + ". " + q.options.get(j));
                rb.setTextSize(15);
                rb.setTypeface(rb.getTypeface());
                rb.setTextColor(ContextCompat.getColor(this, R.color.text_dark));
                rb.setId(View.generateViewId());
                group.addView(rb);
            }

            card.addView(group);
            questionsContainer.addView(card);
            answerGroups.add(group);
        }
    }

    private void submitAnswers() {
        if (currentTest == null) {
            return;
        }

        int correct = 0;
        for (int i = 0; i < answerGroups.size(); i++) {
            RadioGroup group = answerGroups.get(i);
            int checkedId = group.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(this, "Vui lòng chọn đáp án cho tất cả câu hỏi", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedIndex = group.indexOfChild(group.findViewById(checkedId));
            if (selectedIndex == currentTest.questions.get(i).correctIndex) {
                correct++;
            }

            for (int c = 0; c < group.getChildCount(); c++) {
                group.getChildAt(c).setEnabled(false);
            }
        }

        btnSubmit.setEnabled(false);
        showResult(correct, currentTest.questions.size());
    }

    private void showResult(int correct, int total) {
        int percent = (correct * 100) / total;
        txtScore.setText("Điểm: " + correct + "/" + total + " (" + percent + "%)");
        txtTranscript.setText(currentTest.transcript);
        txtTranslation.setText(currentTest.translation);

        saveResultToDatabase(correct, total);

        StringBuilder key = new StringBuilder();
        for (int i = 0; i < currentTest.questions.size(); i++) {
            char letter = (char) ('A' + currentTest.questions.get(i).correctIndex);
            key.append(i + 1).append(") ").append(letter);
            if (i < currentTest.questions.size() - 1) {
                key.append("\n");
            }
        }
        txtAnswerKey.setText(key.toString());
        resultLayout.setVisibility(View.VISIBLE);
    }

    private void saveResultToDatabase(int score, int totalQuestions) {
        if (resultSaved || currentTest == null || totalQuestions <= 0) {
            return;
        }

        int safeTestId = currentTestId > 0 ? currentTestId : 1;
        String title = "Nghe doan van - " + currentTest.title;
        String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date());

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        long rowId = dbHelper.insertTestResult(
                LISTENING_PASSAGE_BASE_TEST_ID + safeTestId,
                title,
                score,
                totalQuestions,
                currentDate
        );

        if (rowId > 0) {
            resultSaved = true;
        }
    }

    private void playTranscript() {
        if (pronunciationHelper == null || currentTest == null) {
            return;
        }
        pronunciationHelper.speakUs(currentTest.transcript);
    }

    private void buildData() {
        tests.clear();

        tests.add(new PassageTest(
                "Bài 1",
                "Good morning everyone. This is a reminder that the company training session will take place this Friday in Conference Room B on the second floor. The session will begin at 9 a.m. and is expected to last about three hours. During the session, we will introduce new company policies and provide hands-on practice. All employees are required to attend, so please make sure to arrive on time. Also, don't forget to bring a notebook and a pen for taking notes.",
                "Chào buổi sáng mọi người. Đây là thông báo nhắc nhở rằng buổi đào tạo của công ty sẽ diễn ra vào thứ Sáu này tại phòng họp B ở tầng hai. Buổi học bắt đầu lúc 9 giờ sáng và dự kiến kéo dài khoảng 3 tiếng. Trong buổi học, chúng tôi sẽ giới thiệu các chính sách mới và thực hành trực tiếp. Tất cả nhân viên bắt buộc phải tham gia, vì vậy hãy đến đúng giờ. Ngoài ra, đừng quên mang theo sổ và bút để ghi chép.",
                Arrays.asList(
                        new PassageQuestion("What is the main purpose of the announcement?", Arrays.asList("To cancel a meeting", "To announce a training session", "To introduce a new employee", "To schedule a trip"), 1),
                        new PassageQuestion("Where will the event be held?", Arrays.asList("First floor", "Conference Room A", "Conference Room B", "Online"), 2),
                        new PassageQuestion("What will happen during the session?", Arrays.asList("A test will be given", "Policies will be introduced", "A party will be held", "Employees will rest"), 1),
                        new PassageQuestion("What are employees asked to bring?", Arrays.asList("Laptop", "ID card", "Notebook and pen", "Food"), 2)
                )
        ));

        tests.add(new PassageTest(
                "Bài 2",
                "Hello, this is Linda from customer service calling regarding your recent order. We're happy to inform you that your package has already been shipped and is currently on its way. According to our system, it should arrive within three business days, depending on your location. If you are not available at the time of delivery, the courier will leave a notice. Please feel free to contact us if you need further assistance or have any questions.",
                "Xin chào, tôi là Linda từ bộ phận chăm sóc khách hàng gọi về đơn hàng gần đây của bạn. Chúng tôi vui mừng thông báo rằng gói hàng của bạn đã được gửi đi và đang trên đường đến. Theo hệ thống, nó sẽ đến trong vòng 3 ngày làm việc tùy thuộc vào vị trí của bạn. Nếu bạn không có mặt khi giao hàng, nhân viên giao hàng sẽ để lại thông báo. Hãy liên hệ với chúng tôi nếu bạn cần hỗ trợ thêm hoặc có câu hỏi.",
                Arrays.asList(
                        new PassageQuestion("Why is Linda calling?", Arrays.asList("To cancel an order", "To give delivery information", "To ask for payment", "To make a complaint"), 1),
                        new PassageQuestion("What might affect delivery time?", Arrays.asList("Weather", "Location", "Traffic", "Staff"), 1),
                        new PassageQuestion("What happens if the customer is not home?", Arrays.asList("Package is returned", "Courier waits", "A notice is left", "Delivery is canceled"), 2),
                        new PassageQuestion("What can the customer do for help?", Arrays.asList("Visit a store", "Call a friend", "Contact customer service", "Ignore it"), 2)
                )
        ));

        tests.add(new PassageTest(
                "Bài 3",
                "Attention passengers, the train to New York that was scheduled to depart at 4:15 p.m. will now be delayed by approximately 20 minutes due to a technical issue. Our maintenance team is currently working to resolve the problem as quickly as possible. We understand this may cause inconvenience, especially for those with connecting trains. Please remain in the waiting area and listen for further announcements. Thank you for your patience and cooperation.",
                "Kính gửi hành khách, chuyến tàu đi New York dự kiến khởi hành lúc 4:15 chiều sẽ bị hoãn khoảng 20 phút do sự cố kỹ thuật. Đội bảo trì đang cố gắng khắc phục nhanh nhất có thể. Chúng tôi hiểu điều này có thể gây bất tiện, đặc biệt với những người có chuyến nối. Vui lòng ở lại khu vực chờ và lắng nghe thông báo tiếp theo. Cảm ơn sự kiên nhẫn của quý khách.",
                Arrays.asList(
                        new PassageQuestion("What caused the delay?", Arrays.asList("Bad weather", "Technical issue", "Staff shortage", "Late passengers"), 1),
                        new PassageQuestion("How long is the delay?", Arrays.asList("10 minutes", "15 minutes", "20 minutes", "30 minutes"), 2),
                        new PassageQuestion("Who is fixing the problem?", Arrays.asList("Passengers", "Drivers", "Maintenance team", "Police"), 2),
                        new PassageQuestion("What are passengers advised to do?", Arrays.asList("Leave the station", "Wait for updates", "Board another train", "Ask for refund"), 1)
                )
        ));

        tests.add(new PassageTest(
                "Bài 4",
                "Hi John, I just wanted to remind you about the team meeting scheduled for tomorrow morning at 10 a.m. in the main conference room. During the meeting, we will discuss the progress of the new project and assign specific tasks to each team member. I've already sent you the related documents by email earlier this week. Please review them carefully so you can contribute to the discussion. Let me know if you have any questions before the meeting.",
                "Chào John, tôi muốn nhắc bạn về cuộc họp nhóm vào sáng mai lúc 10 giờ tại phòng họp chính. Trong cuộc họp, chúng ta sẽ thảo luận tiến độ dự án mới và phân công nhiệm vụ cụ thể cho từng thành viên. Tôi đã gửi tài liệu liên quan qua email đầu tuần này. Hãy xem kỹ để bạn có thể đóng góp ý kiến. Hãy cho tôi biết nếu bạn có câu hỏi trước cuộc họp.",
                Arrays.asList(
                        new PassageQuestion("What is the purpose of the meeting?", Arrays.asList("To hire staff", "To discuss a project", "To celebrate success", "To cancel work"), 1),
                        new PassageQuestion("When is the meeting?", Arrays.asList("Today", "Tomorrow morning", "Tomorrow afternoon", "Next week"), 1),
                        new PassageQuestion("What did the speaker send earlier?", Arrays.asList("A report", "An email", "Documents", "A message"), 2),
                        new PassageQuestion("What is John asked to do?", Arrays.asList("Prepare food", "Review documents", "Call clients", "Write a report"), 1)
                )
        ));

        tests.add(new PassageTest(
                "Bài 5",
                "Welcome to Green Hotel. We hope you have a pleasant stay with us. Breakfast is served daily from 6:30 to 10 a.m. on the first floor near the main lobby. Our swimming pool is open from 8 a.m. to 8 p.m., and towels are available at the front desk. If you would like to request room service or need any assistance, please dial zero from your room phone at any time. Thank you for choosing our hotel.",
                "Chào mừng quý khách đến với khách sạn Green. Chúng tôi hy vọng bạn có kỳ nghỉ dễ chịu. Bữa sáng được phục vụ mỗi ngày từ 6:30 đến 10 giờ sáng tại tầng một gần sảnh chính. Hồ bơi mở cửa từ 8 giờ sáng đến 8 giờ tối và khăn có sẵn tại quầy lễ tân. Nếu bạn cần dịch vụ phòng hoặc hỗ trợ, hãy bấm số 0 từ điện thoại trong phòng bất cứ lúc nào. Cảm ơn bạn đã chọn khách sạn của chúng tôi.",
                Arrays.asList(
                        new PassageQuestion("Where is this announcement most likely heard?", Arrays.asList("Airport", "Hotel", "Restaurant", "Office"), 1),
                        new PassageQuestion("Where is breakfast served?", Arrays.asList("Second floor", "Near the pool", "First floor", "In the room"), 2),
                        new PassageQuestion("What can guests get at the front desk?", Arrays.asList("Food", "Towels", "Tickets", "Maps"), 1),
                        new PassageQuestion("How can guests request help?", Arrays.asList("Send email", "Call number zero", "Visit office", "Ask staff outside"), 1)
                )
        ));
    }

    private int dp(int value) {
        return Math.round(getResources().getDisplayMetrics().density * value);
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pronunciationHelper != null) {
            pronunciationHelper.release();
        }
    }

    private void loadFromIntentOrRandom() {
        int selectedId = getIntent().getIntExtra(EXTRA_LISTENING_TEST_ID, -1);
        if (selectedId >= 1 && selectedId <= tests.size()) {
            currentTestId = selectedId;
            currentTest = tests.get(selectedId - 1);
            txtTestName.setText(currentTest.title);
            renderQuestions(currentTest.questions);
            resultLayout.setVisibility(View.GONE);
            btnSubmit.setEnabled(true);
            return;
        }
        loadRandomTest();
    }
}
