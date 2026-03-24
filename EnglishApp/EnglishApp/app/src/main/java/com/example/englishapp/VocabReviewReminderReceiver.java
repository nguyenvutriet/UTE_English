package com.example.englishapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class VocabReviewReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "vocab_review_reminder";

    @Override
    public void onReceive(Context context, Intent intent) {
        int dueCount = TopicProgressStore.getGlobalDueCount(context);
        if (dueCount <= 0) {
            return;
        }

        createChannel(context);

        Intent openTopicIntent = new Intent(context, TopicActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                2002,
                openTopicIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Nhắc ôn từ vựng")
                .setContentText("Bạn có " + dueCount + " từ đến hạn ôn lại hôm nay.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        try {
            NotificationManagerCompat.from(context).notify(2002, builder.build());
        } catch (SecurityException ignored) {
            // POST_NOTIFICATIONS may be denied by user.
        }
    }

    private void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Vocabulary Review",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}

