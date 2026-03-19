package com.example.englishapp.ai;

import okhttp3.*;
import org.json.*;

import java.io.IOException;

public class OpenAIService { // Bạn có thể đổi tên thành GeminiService cho đúng bản chất nhé

    private static final String API_KEY = "API_KEY";
    // Endpoint của Gemini (model gemini-1.5-flash hoặc gemini-1.5-pro)
// Sử dụng gemini-2.0-flash (vì nó có trong danh sách curl của bạn)
// Đổi model sang 1.5 Flash
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=" + API_KEY;

    public interface Callback {
        void onResult(String json);
    }

    public static void generate(String prompt, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        try {
            /* Cấu trúc JSON của Gemini khác OpenAI:
               {
                 "contents": [{
                   "parts":[{"text": "nội dung prompt"}]
                 }]
               }
            */
            JSONObject body = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject contentObj = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject textPart = new JSONObject();

            textPart.put("text", prompt);
            parts.put(textPart);
            contentObj.put("parts", parts);
            contents.put(contentObj);

            body.put("contents", contents);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(RequestBody.create(body.toString(),
                            MediaType.parse("application/json")))
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    System.out.println("GEMINI API FAIL: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        System.out.println("GEMINI API RESPONSE: " + res);
                        callback.onResult(res);
                    } else {
                        System.out.println("GEMINI API ERROR CODE: " + response.code());
                        System.out.println("ERROR BODY: " + response.body().string());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}