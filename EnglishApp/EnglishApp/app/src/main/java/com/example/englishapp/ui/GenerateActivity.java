package com.example.englishapp.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.R;
import com.example.englishapp.ai.OpenAIService;
import com.example.englishapp.ai.QuestionGenerator;
import com.example.englishapp.ocr.ImageTextExtractor;
import com.example.englishapp.pdf.PdfTextExtractor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GenerateActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> pdfPickerLauncher;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        progressBar = findViewById(R.id.progressBar);

        // 📸 XỬ LÝ CHỌN ẢNH
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        showLoading(true); // Bật quay quay
                        processSelectedImages(result.getData());
                    }
                }
        );

        // 📄 XỬ LÝ CHỌN PDF
        pdfPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        showLoading(true);
                        Uri uri = result.getData().getData();
                        PdfTextExtractor.extract(this, uri, text -> {
                            if (text != null && !text.isEmpty()) {
                                callAI(text);
                            } else {
                                stopLoadingWithError("Không thể đọc nội dung PDF!");
                            }
                        });
                    }
                }
        );

        findViewById(R.id.btnImage).setOnClickListener(v -> pickImage());
        findViewById(R.id.btnPdf).setOnClickListener(v -> pickPdf());
        findViewById(R.id.btnHistory).setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });
    }

    private void processSelectedImages(Intent data) {
        List<Bitmap> bitmaps = new ArrayList<>();
        try {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    bitmaps.add(rescaleBitmap(data.getClipData().getItemAt(i).getUri()));
                }
            } else if (data.getData() != null) {
                bitmaps.add(rescaleBitmap(data.getData()));
            }

            ImageTextExtractor.extractMultiple(bitmaps, new ImageTextExtractor.Callback() {
                @Override
                public void onSuccess(String text) {
                    callAI(text);
                }

                @Override
                public void onError(Exception e) {
                    stopLoadingWithError("Lỗi trích xuất ảnh: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            stopLoadingWithError("Lỗi xử lý tệp ảnh!");
        }
    }

    // 🤖 GỌI AI VÀ TẮT LOADING
    private void callAI(String text) {
        if (text == null || text.trim().isEmpty()) {
            stopLoadingWithError("Không tìm thấy văn bản để tạo câu hỏi!");
            return;
        }

        String prompt = QuestionGenerator.buildPrompt(text);
        OpenAIService.generate(prompt, json -> {
            runOnUiThread(() -> {
                showLoading(false); // ✅ TẮT LOADING KHI THÀNH CÔNG
                Intent i = new Intent(this, QuizActivity.class);
                i.putExtra("data", json);
                i.putExtra("original_text", text);
                startActivity(i);
            });
        });
    }

    // 📉 HÀM GIẢM DUNG LƯỢNG ẢNH (CHỐNG VĂNG APP)
    private Bitmap rescaleBitmap(Uri uri) throws Exception {
        InputStream is = getContentResolver().openInputStream(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2; // Giảm kích thước xuống 1/2 để tiết kiệm RAM
        return BitmapFactory.decodeStream(is, null, options);
    }

    private void stopLoadingWithError(String msg) {
        runOnUiThread(() -> {
            showLoading(false); // ✅ TẮT LOADING KHI LỖI
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });
    }

    private void pickImage() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(i);
    }

    private void pickPdf() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("application/pdf");
        pdfPickerLauncher.launch(i);
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }
}