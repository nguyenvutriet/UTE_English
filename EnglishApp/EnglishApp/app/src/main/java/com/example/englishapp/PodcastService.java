package com.example.englishapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

public class PodcastService extends Service {

    MediaPlayer mediaPlayer;

    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_PAUSE = "PAUSE";
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PREV = "PREV";

    public static final String ACTION_STATE = "PLAYER_STATE";
    public static final String EXTRA_PLAYING = "playing";

    public static final String ACTION_TRACK = "TRACK_CHANGED";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_IMAGE = "image";

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        PodcastService getService(){
            return PodcastService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void play(int audio){

        if(mediaPlayer != null){
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, audio);
        mediaPlayer.start();

        showNotification();
        sendState();
    }

    public void pause(){

        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            showNotification();
            sendState();
        }
    }

    public void resume(){

        if(mediaPlayer != null){
            mediaPlayer.start();
            showNotification();
            sendState();
        }
    }

    public boolean isPlaying(){

        if(mediaPlayer != null){
            return mediaPlayer.isPlaying();
        }

        return false;
    }

    public int getCurrentPosition(){

        if(mediaPlayer != null){
            return mediaPlayer.getCurrentPosition();
        }

        return 0;
    }

    public int getDuration(){

        if(mediaPlayer != null){
            return mediaPlayer.getDuration();
        }

        return 0;
    }

    public void seekTo(int pos){

        if(mediaPlayer != null){
            mediaPlayer.seekTo(pos);
        }
    }

    private void sendState(){

        Intent intent = new Intent(ACTION_STATE);
        intent.putExtra(EXTRA_PLAYING, isPlaying());
        sendBroadcast(intent);
    }

    private void sendTrack(Podcast p){

        Intent intent = new Intent(ACTION_TRACK);
        intent.putExtra(EXTRA_TITLE, p.getTitle());
        intent.putExtra(EXTRA_IMAGE, p.getImage());

        sendBroadcast(intent);
    }

    private void playNext(){

        if(PlayerManager.currentList == null) return;

        int index = PlayerManager.currentIndex;

        if(index < PlayerManager.currentList.size() - 1){

            index++;

            PlayerManager.currentIndex = index;

            Podcast next = PlayerManager.currentList.get(index);

            PlayerManager.currentTitle = next.getTitle();
            PlayerManager.currentImage = next.getImage();

            play(next.getAudio());

            sendTrack(next);   // THÊM DÒNG NÀY
        }

    }

    private void playPrev(){

        if(PlayerManager.currentList == null) return;

        int index = PlayerManager.currentIndex;

        if(index > 0){

            index--;

            PlayerManager.currentIndex = index;

            Podcast prev = PlayerManager.currentList.get(index);

            PlayerManager.currentTitle = prev.getTitle();
            PlayerManager.currentImage = prev.getImage();

            play(prev.getAudio());

            sendTrack(prev);
        }

    }

    private void showNotification(){

        String channelId = "podcast_channel";

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel =
                    new NotificationChannel(
                            channelId,
                            "Podcast Player",
                            NotificationManager.IMPORTANCE_LOW
                    );

            manager.createNotificationChannel(channel);
        }

        Intent playIntent = new Intent(this, PodcastService.class);
        playIntent.setAction(ACTION_PLAY);

        Intent pauseIntent = new Intent(this, PodcastService.class);
        pauseIntent.setAction(ACTION_PAUSE);

        Intent nextIntent = new Intent(this, PodcastService.class);
        nextIntent.setAction(ACTION_NEXT);

        Intent prevIntent = new Intent(this, PodcastService.class);
        prevIntent.setAction(ACTION_PREV);

        PendingIntent playPending =
                PendingIntent.getService(this,1,playIntent,PendingIntent.FLAG_IMMUTABLE);

        PendingIntent pausePending =
                PendingIntent.getService(this,2,pauseIntent,PendingIntent.FLAG_IMMUTABLE);

        PendingIntent nextPending =
                PendingIntent.getService(this,3,nextIntent,PendingIntent.FLAG_IMMUTABLE);

        PendingIntent prevPending =
                PendingIntent.getService(this,4,prevIntent,PendingIntent.FLAG_IMMUTABLE);

        Notification notification =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle("English Podcast")
                        .setContentText(PlayerManager.currentTitle)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setLargeIcon(
                                BitmapFactory.decodeResource(
                                        getResources(),
                                        PlayerManager.currentImage
                                )
                        )

                        .addAction(R.drawable.ic_rewind,"Prev",prevPending)

                        .addAction(
                                isPlaying()?R.drawable.ic_pause_1:R.drawable.ic_play_1,
                                "Play",
                                isPlaying()?pausePending:playPending
                        )

                        .addAction(R.drawable.ic_fast,"Next",nextPending)

                        .setStyle(new MediaStyle().setShowActionsInCompactView(0,1,2))

                        .setOnlyAlertOnce(true)
                        .setOngoing(isPlaying())
                        .build();

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){

        if(intent != null){

            String action = intent.getAction();

            if(ACTION_PAUSE.equals(action)){
                pause();
            }

            else if(ACTION_PLAY.equals(action)){
                resume();
            }

            else if(ACTION_NEXT.equals(action)){
                playNext();
            }

            else if(ACTION_PREV.equals(action)){
                playPrev();
            }
        }

        return START_STICKY;
    }
}