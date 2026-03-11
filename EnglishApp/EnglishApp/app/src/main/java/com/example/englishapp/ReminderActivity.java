package com.example.englishapp;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {

    TextView txtTime;
    Button btnChooseTime, btnSave, btnAddTask;
    MaterialSwitch switchReminder;

    CheckBox cbVocab, cbQuiz, cbGrammar;

    EditText edtNewTask;
    LinearLayout layoutTasks;

    int hour = 20;
    int minute = 0;

    ArrayList<String> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // Xin quyền notification Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1
                );
            }
        }

        // Ánh xạ view
        txtTime = findViewById(R.id.txtTime);
        btnChooseTime = findViewById(R.id.btnChooseTime);
        btnSave = findViewById(R.id.btnSave);
        switchReminder = findViewById(R.id.switchReminder);

        cbVocab = findViewById(R.id.cbVocab);
        cbQuiz = findViewById(R.id.cbQuiz);
        cbGrammar = findViewById(R.id.cbGrammar);

        edtNewTask = findViewById(R.id.edtNewTask);
        btnAddTask = findViewById(R.id.btnAddTask);
        layoutTasks = findViewById(R.id.layoutTasks);

        txtTime.setText(hour + ":" + minute);

        // load task đã lưu
        loadTasks();
        showTasks();

        // chọn giờ
        btnChooseTime.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            TimePickerDialog dialog = new TimePickerDialog(
                    ReminderActivity.this,
                    (view, hourOfDay, minute1) -> {

                        hour = hourOfDay;
                        minute = minute1;

                        txtTime.setText(hour + ":" + minute);

                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            );

            dialog.show();
        });

        // thêm task mới
        btnAddTask.setOnClickListener(v -> {

            String task = edtNewTask.getText().toString().trim();

            if (!task.isEmpty()) {

                taskList.add(task);

                saveTasks();

                addCheckBox(task);

                edtNewTask.setText("");

                Toast.makeText(this, "Đã thêm việc", Toast.LENGTH_SHORT).show();
            }

        });

        btnSave.setOnClickListener(v -> {

            if (switchReminder.isChecked()) {

                ArrayList<String> tasks = new ArrayList<>();

                if (cbVocab.isChecked())
                    tasks.add("Học 10 từ vựng");

                if (cbQuiz.isChecked())
                    tasks.add("Làm bài Quiz");

                if (cbGrammar.isChecked())
                    tasks.add("Ôn ngữ pháp");

                // lấy checkbox động đã tick
                for (int i = 0; i < layoutTasks.getChildCount(); i++) {

                    View view = layoutTasks.getChildAt(i);

                    if (view instanceof CheckBox) {

                        CheckBox cb = (CheckBox) view;

                        if (cb.isChecked()) {
                            tasks.add(cb.getText().toString());
                        }
                    }
                }

                ReminderManager.setReminder(
                        ReminderActivity.this,
                        hour,
                        minute,
                        tasks
                );

                TaskStorage.saveTasks(this, tasks);

                Toast.makeText(
                        ReminderActivity.this,
                        "Đã bật nhắc nhở",
                        Toast.LENGTH_LONG
                ).show();

            } else {

                Toast.makeText(
                        ReminderActivity.this,
                        "Nhắc nhở đang tắt",
                        Toast.LENGTH_LONG
                ).show();
            }

        });

    }

    // hiển thị checkbox task đã lưu
    private void showTasks() {

        layoutTasks.removeAllViews();

        for (String t : taskList) {
            addCheckBox(t);
        }

    }

    // tạo checkbox động
    private void addCheckBox(String text) {

        CheckBox cb = new CheckBox(this);

        cb.setText(text);
        cb.setTextSize(16);

        layoutTasks.addView(cb);

    }

    // lưu task
    private void saveTasks() {

        SharedPreferences pref =
                getSharedPreferences("TASK_DATA", MODE_PRIVATE);

        JSONArray arr = new JSONArray();

        for (String t : taskList) {
            arr.put(t);
        }

        pref.edit()
                .putString("TASK_LIST", arr.toString())
                .apply();
    }

    // load task
    private void loadTasks() {

        SharedPreferences pref =
                getSharedPreferences("TASK_DATA", MODE_PRIVATE);

        String json = pref.getString("TASK_LIST", null);

        if (json != null) {
            try {

                JSONArray arr = new JSONArray(json);

                for (int i = 0; i < arr.length(); i++) {
                    taskList.add(arr.getString(i));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}