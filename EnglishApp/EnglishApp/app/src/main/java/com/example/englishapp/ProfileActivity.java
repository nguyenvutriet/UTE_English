package com.example.englishapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ProfileActivity extends AppCompatActivity {

    ImageView imgAvatar;
    EditText edtName;

    SharedPreferences prefs;
    ActivityResultLauncher<Intent> pickImageLauncher;
    MaterialButton btnReminder;

    MaterialButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgAvatar = findViewById(R.id.imgAvatar);
        edtName = findViewById(R.id.edtName);
        btnLogout = findViewById(R.id.btnLogout);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);

        // Load tên đã lưu
        edtName.setText(prefs.getString("name", "Hoàng Bang B9"));

        // Load avatar đã lưu
        String avatarUri = prefs.getString("avatar", "");
        if (!avatarUri.equals("")) {
            try {
                imgAvatar.setImageURI(Uri.parse(avatarUri));
            } catch (Exception e) {
                imgAvatar.setImageResource(R.drawable.animal);
            }
        }

        // Launcher chọn ảnh
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        Uri uri = result.getData().getData();

                        if (uri != null) {

                            // Hiển thị avatar mới ngay lập tức
                            imgAvatar.setImageURI(uri);

                            // Lưu ảnh vào bộ nhớ app
                            String localUri = saveImage(uri);

                            if (localUri != null) {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("avatar", localUri);
                                editor.apply();
                            }
                        }
                    }
                }
        );

        // Click avatar để đổi ảnh
        imgAvatar.setOnClickListener(v -> openGallery());

        // Bắt sự kiện nút Lưu trên toolbar
        toolbar.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.menu_save) {

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("name", edtName.getText().toString());
                editor.apply();

                Toast.makeText(this, "Đã lưu thông tin", Toast.LENGTH_SHORT).show();

                return true;
            }

            return false;
        });
        btnReminder = findViewById(R.id.btnReminder);

        btnReminder.setOnClickListener(v -> {

            Intent intent = new Intent(ProfileActivity.this, ReminderActivity.class);
            startActivity(intent);

        });

        btnLogout.setOnClickListener(v -> {

            AlertDialog dialog = new MaterialAlertDialogBuilder(ProfileActivity.this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc muốn đăng xuất không?")
                    .setNegativeButton("Huỷ", null)
                    .setPositiveButton("Đăng xuất", (d, w) -> {

                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                        finishAffinity();

                    })
                    .create();

            dialog.show();

// màu nút
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(getResources().getColor(android.R.color.holo_red_dark));

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(getResources().getColor(android.R.color.darker_gray));


        });
    }

    // Mở gallery
    void openGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        pickImageLauncher.launch(intent);
    }

    // Lưu ảnh vào bộ nhớ trong app
    private String saveImage(Uri uri) {

        try {

            InputStream input = getContentResolver().openInputStream(uri);

            File file = new File(getFilesDir(), "avatar.jpg");

            OutputStream output = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.close();
            input.close();

            return Uri.fromFile(file).toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}