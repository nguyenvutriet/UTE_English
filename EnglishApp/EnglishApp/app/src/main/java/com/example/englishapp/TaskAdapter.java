package com.example.englishapp;

import android.content.Context;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

public class TaskAdapter extends ArrayAdapter<Task> {

    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Task task = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_task, parent, false);
        }

        CheckBox check = convertView.findViewById(R.id.checkTask);

        check.setText(task.name);
        check.setChecked(task.done);

        check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.done = isChecked;
        });

        return convertView;
    }
}