package com.example.englishapp;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AccountManageActivity extends AppCompatActivity {

    private Map<String, String> userHistories = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manage);

        ListView lvAccounts = findViewById(R.id.lvAccounts);
        MaterialButton btnBack = findViewById(R.id.btnBack);

        ArrayList<String> displayList = new ArrayList<>();
        setupData(displayList);

        // CUSTOM ADAPTER ĐỂ CHỈNH MÀU RIÊNG TỪNG DÒNG
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.account_list_item, R.id.tv_account_name, displayList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView tvName = view.findViewById(R.id.tv_account_name);
                TextView tvEmail = view.findViewById(R.id.tv_account_email);

                // Tách chuỗi bằng dấu xuống dòng \n
                String[] parts = displayList.get(position).split("\n");
                if (parts.length >= 2) {
                    tvName.setText(parts[0]);  // Tên (Xanh biển)
                    tvEmail.setText(parts[1]); // Email (Đen)
                }

                return view;
            }
        };

        lvAccounts.setAdapter(adapter);

        lvAccounts.setOnItemClickListener((parent, view, position, id) -> {
            String fullInfo = displayList.get(position);
            String name = fullInfo.split("\n")[0];
            String email = fullInfo.split("\n")[1];
            String history = userHistories.getOrDefault(email, "Chưa có hoạt động.");

            new AlertDialog.Builder(this)
                    .setTitle("Lịch sử: " + name)
                    .setMessage(history)
                    .setPositiveButton("Đóng", null)
                    .show();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupData(ArrayList<String> list) {
        // --- NHÓM 1: DỮ LIỆU ĐẶC BIỆT (HAND-MADE) ---
        list.add("Hệ Thống Admin\nadmin@englishapp.com");
        list.add("Nguyễn Văn A\nnguyenvana_pro@gmail.com");
        list.add("Trần Thị Học Bé\ntran_thi_hoc_be@yahoo.com");
        list.add("Lê Văn Luyện\nle_van_luyen_english@gmail.com");
        list.add("Cao Thủ Học Đường\ntop_1_server@gmail.com");
        list.add("Gà Con Tập Sự\nga_con_hoc_tieng_anh@gmail.com");
        list.add("Star Boy 99\nstar_boy_99@hotmail.com");
        list.add("Chí Phèo\nchi_pheo_nha_nam@gmail.com");
        list.add("Thị Nở Xinh Đẹp\nthi_no_xinh_dep@gmail.com");
        list.add("Cậu Vàng Thông Minh\ncau_vang_hoc_tap@gmail.com");
        list.add("Lão Hạc\nlaohac_hoc_tienganh@gmail.com");
        list.add("Cô Gái Đến Từ Hôm Qua\ncogai_homqua@yahoo.com");
        list.add("Hoàng Tử Bé\nlittle_prince@gmail.com");

        // Đổ lịch sử chi tiết (Dữ liệu mẫu phong phú theo yêu cầu)
        userHistories.put("admin@englishapp.com",
                "• 07:00: Đăng nhập quyền Admin\n" +
                        "• 07:15: Duyệt 20 bộ đề từ PDF mới\n" +
                        "• 08:30: Cập nhật đáp án Quiz Unit 20");

        userHistories.put("nguyenvana_pro@gmail.com",
                "• 08:30: Nghe Podcast '6 Minute English'\n" +
                        "• 09:00: Xem video hướng dẫn Pronunciation\n" +
                        "• 09:30: Hoàn thành bài Quiz đạt 10/10");

        userHistories.put("tran_thi_hoc_be@yahoo.com",
                "• 10:00: Làm đề luyện thi từ file PDF\n" +
                        "• 11:00: Tra từ điển 15 từ mới\n" +
                        "• 11:15: Chơi game từ vựng đạt hạng 5");

        userHistories.put("top_1_server@gmail.com",
                "• 00:01: Cày 3 đề thi thử từ PDF\n" +
                        "• 01:00: Chơi game từ vựng vượt 50 cấp\n" +
                        "• 02:30: Học ngữ pháp 'Mệnh đề quan hệ'");

        userHistories.put("chi_pheo_nha_nam@gmail.com",
                "• 15:00: Xem video 'English for Beginners'\n" +
                        "• 15:30: Làm Quiz Unit 1 sai 8 câu\n" +
                        "• 15:45: Tra từ điển tìm từ 'Lương Thiện'");

        userHistories.put("laohac_hoc_tienganh@gmail.com",
                "• 06:00: Nghe Podcast về nông nghiệp\n" +
                        "• 06:45: Học ngữ pháp 'Thì hiện tại đơn'\n" +
                        "• 07:15: Tra từ điển 20 cụm từ khó");

        userHistories.put("cogai_homqua@yahoo.com",
                "• 20:00: Chơi game từ vựng cùng bạn bè\n" +
                        "• 20:45: Làm bài Quiz tổng hợp tháng 3\n" +
                        "• 21:30: Xem video Daily English");

        userHistories.put("le_van_luyen_english@gmail.com",
                "• 12:15: Nghe Podcast lúc nghỉ trưa\n" +
                        "• 12:45: Làm đề từ PDF\n" +
                        "• 13:00: Tra từ điển các cấu trúc lạ");

        userHistories.put("star_boy_99@hotmail.com",
                "• 22:00: Học ngữ pháp 'Câu điều kiện'\n" +
                        "• 22:30: Làm Quiz ngữ pháp nâng cao\n" +
                        "• 23:00: Chơi game từ vựng");

        userHistories.put("cau_vang_hoc_tap@gmail.com",
                "• 14:00: Xem video hoạt hình tiếng Anh\n" +
                        "• 14:30: Chơi game từ vựng chủ đề Động vật\n" +
                        "• 15:00: Hoàn thành Quiz 5 câu");

        // --- NHÓM 2: DỮ LIỆU TỰ ĐỘNG (BOT DATA - Tăng số lượng lên 50 user) ---
        for (int i = 1; i <= 40; i++) {
            String name = "Học Viên Thứ " + i;
            String email = "student_active_" + i + "@englishapp.vn";
            list.add(name + "\n" + email);

            // Tạo lịch sử ngẫu nhiên cho từng đứa
            String activity = "• 08:00: Đăng nhập\n• 09:00: Làm bài tập Unit " + (i % 10 + 1) + "\n• 10:00: Thoát ứng dụng";
            userHistories.put(email, activity);
        }
    }
}