package com.example.englishapp.activity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishapp.R;

public class FillInTheBlanksResultActivity extends AppCompatActivity {
    private TextView txtScore;
    private TextView txtPercentage;
    private TextView txtFeedback;
    private LinearLayout scoreContainer;
    private Button btnRetry;
    private Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fill_in_the_blanks_result);

        applyWindowInsets();
        initializeViews();
        displayResults();
    }

    private void initializeViews() {
        txtScore = findViewById(R.id.txtScore);
        txtPercentage = findViewById(R.id.txtPercentage);
        txtFeedback = findViewById(R.id.txtFeedback);
        scoreContainer = findViewById(R.id.scoreContainer);
        btnRetry = findViewById(R.id.btnRetry);
        btnHome = findViewById(R.id.btnHome);

        btnRetry.setOnClickListener(v -> {
            startActivity(new Intent(this, FillInTheBlanksActivity.class));
            finish();
        });

        btnHome.setOnClickListener(v -> finish());
    }

    private void displayResults() {
        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 10);
        int percentage = getIntent().getIntExtra("percentage", 0);

        txtScore.setText(score + "/" + total);
        txtPercentage.setText(percentage + "%");

        String feedback;
        int backgroundColor;
        if (percentage >= 80) {
            feedback = "Xuất sắc! 🎉";
            backgroundColor = getResources().getColor(R.color.success_green);
        } else if (percentage >= 60) {
            feedback = "Tốt lắm! 👍";
            backgroundColor = getResources().getColor(R.color.blue_primary);
        } else if (percentage >= 40) {
            feedback = "Cần cải thiện! 📚";
            backgroundColor = getResources().getColor(android.R.color.holo_orange_dark);
        } else {
            feedback = "Tiếp tục luyện tập! 💪";
            backgroundColor = getResources().getColor(R.color.error_red);
        }

        txtFeedback.setText(feedback);

        GradientDrawable cardBackground = new GradientDrawable();
        cardBackground.setShape(GradientDrawable.RECTANGLE);
        cardBackground.setColor(backgroundColor);
        cardBackground.setCornerRadius(dpToPx(18));
        scoreContainer.setBackground(cardBackground);
    }

    private float dpToPx(int dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

