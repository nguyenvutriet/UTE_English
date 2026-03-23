package com.example.englishapp.pdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.example.englishapp.ocr.ImageTextExtractor;

public class PdfTextExtractor {

    public interface Callback {
        void onComplete(String text);
    }

    public static void extract(Context context, Uri uri, Callback callback) {
        new Thread(() -> {
            StringBuilder finalText = new StringBuilder();
            try {
                // Mở file PDF
                ParcelFileDescriptor fd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (fd == null) {
                    returnResult("", callback);
                    return;
                }

                PdfRenderer renderer = new PdfRenderer(fd);
                int pageCount = renderer.getPageCount();

                // Bắt đầu quét tuần tự từng trang
                processPage(renderer, 0, pageCount, finalText, callback);

            } catch (Exception e) {
                Log.e("PDF_ERROR", "Lỗi mở file: " + e.getMessage());
                returnResult("", callback);
            }
        }).start();
    }

    private static void processPage(PdfRenderer renderer, int index, int total, StringBuilder sb, Callback callback) {
        // Nếu đã duyệt hết các trang
        if (index >= total) {
            try { renderer.close(); } catch (Exception ignored) {}
            returnResult(sb.toString().trim(), callback);
            return;
        }

        try {
            PdfRenderer.Page page = renderer.openPage(index);

            // Tạo Bitmap và tô nền trắng (Tránh lỗi nền đen khiến OCR không đọc được)
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);

            // Vẽ trang PDF lên Bitmap
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            page.close();

            // Gửi Bitmap sang ML Kit
            ImageTextExtractor.extract(bitmap, new ImageTextExtractor.Callback() {
                @Override
                public void onSuccess(String text) {
                    if (text != null && !text.isEmpty()) {
                        sb.append(text).append("\n\n");
                    }
                    // Đệ quy sang trang tiếp theo
                    processPage(renderer, index + 1, total, sb, callback);
                }

                @Override
                public void onError(Exception e) {
                    // Nếu lỗi trang này, vẫn cố gắng làm trang sau
                    processPage(renderer, index + 1, total, sb, callback);
                }
            });

        } catch (Exception e) {
            processPage(renderer, index + 1, total, sb, callback);
        }
    }

    private static void returnResult(String result, Callback callback) {
        // Đảm bảo kết quả luôn trả về Main Thread để tắt ProgressBar
        new Handler(Looper.getMainLooper()).post(() -> callback.onComplete(result));
    }
}