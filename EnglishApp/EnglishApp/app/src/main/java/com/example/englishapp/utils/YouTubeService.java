package com.example.englishapp.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;

import java.io.IOException;
import java.security.MessageDigest;

public class YouTubeService {

    private static final String API_KEY = "";
    private static YouTube cachedService = null;

    /**
     * Khởi tạo YouTube client với HTTP headers cho Android API Key Restriction.
     * Google Cloud chặn (403 lỗi) các yêu cầu từ API Key bị giới hạn "Android apps" 
     * nếu không có 2 HTTP header: X-Android-Package và X-Android-Cert
     */
    public static YouTube getService(Context context){
        if (cachedService != null) return cachedService;

        // Lấy package name và SHA-1 fingerprint động từ ứng dụng
        String packageName = "com.example.englishapp";
        String sha1cert = "2025136C494D128C8C6B082FE54D2D5E8CD91756";

        if (context != null) {
            try {
                packageName = context.getPackageName();
                PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA1");
                    md.update(signature.toByteArray());
                    byte[] digest = md.digest();
                    StringBuilder hexString = new StringBuilder();
                    for (int i = 0; i < digest.length; i++) {
                        String hex = Integer.toHexString(0xFF & digest[i]);
                        if (hex.length() == 1) hexString.append('0');
                        hexString.append(hex.toUpperCase());
                    }
                    sha1cert = hexString.toString(); // API thường nhận hex không khoảng/hai chấm
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final String finalPackageName = packageName;
        final String finalSha1 = sha1cert;

        cachedService = new YouTube.Builder(
                AndroidHttp.newCompatibleTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) throws IOException {
                        request.getHeaders().set("X-Android-Package", finalPackageName);
                        request.getHeaders().set("X-Android-Cert", finalSha1);
                    }
                }
        )
                .setApplicationName("MyYoutubeApp")
                .setYouTubeRequestInitializer(new YouTubeRequestInitializer(API_KEY))
                .build();

        return cachedService;
    }

    public static String getApiKey(){
        return API_KEY;
    }
}