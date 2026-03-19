package com.example.englishapp.ocr;

import android.graphics.Bitmap;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import java.util.List;

public class ImageTextExtractor {

    public interface Callback {
        void onSuccess(String text);
        void onError(Exception e);
    }

    public static void extract(Bitmap bitmap, Callback callback) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(result -> {
                    String text = result.getText();
                    // Giải phóng bitmap để tránh treo máy ở các trang sau
                    if (!bitmap.isRecycled()) bitmap.recycle();
                    callback.onSuccess(text);
                })
                .addOnFailureListener(e -> {
                    if (!bitmap.isRecycled()) bitmap.recycle();
                    callback.onError(e);
                });
    }

    // ✅ HÀM NÀY: Dùng cho trường hợp chọn nhiều ảnh từ Gallery
    public static void extractMultiple(List<Bitmap> bitmaps, Callback callback) {
        if (bitmaps == null || bitmaps.isEmpty()) {
            callback.onSuccess("");
            return;
        }

        StringBuilder fullText = new StringBuilder();
        final int totalImages = bitmaps.size();
        final int[] processedCount = {0};

        for (Bitmap bmp : bitmaps) {
            extract(bmp, new Callback() {
                @Override
                public void onSuccess(String text) {
                    fullText.append(text).append("\n\n");
                    processedCount[0]++;
                    if (processedCount[0] == totalImages) {
                        callback.onSuccess(fullText.toString().trim());
                    }
                }

                @Override
                public void onError(Exception e) {
                    processedCount[0]++;
                    if (processedCount[0] == totalImages) {
                        callback.onSuccess(fullText.toString().trim());
                    }
                }
            });
        }
    }
}