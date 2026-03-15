package com.example.englishapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PodcastActivity extends AppCompatActivity {

    ImageView imgPodcast;

    ImageButton btnPlay;
    ImageButton btnBack;
    ImageButton btnPrev;
    ImageButton btnNext;
    ImageButton btnForward;
    ImageButton btnRewind;

    TextView txtPodcastTitle;

    SeekBar seekBar;

    Handler handler = new Handler();

    PodcastService service;
    boolean isBound = false;

    BroadcastReceiver stateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            boolean playing =
                    intent.getBooleanExtra(PodcastService.EXTRA_PLAYING,false);

            if(playing){
                btnPlay.setImageResource(R.drawable.ic_pause);
            }else{
                btnPlay.setImageResource(R.drawable.ic_play);
            }

        }
    };

    BroadcastReceiver trackReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String title = intent.getStringExtra(PodcastService.EXTRA_TITLE);
            int image = intent.getIntExtra(PodcastService.EXTRA_IMAGE,0);

            txtPodcastTitle.setText(title);
            imgPodcast.setImageResource(image);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast);

        Intent intent = new Intent(this, PodcastService.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        bindService(intent, connection, BIND_AUTO_CREATE);

        imgPodcast = findViewById(R.id.imgPodcast);
        btnPlay = findViewById(R.id.btnPlay);
        btnBack = findViewById(R.id.btnBack);

        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnForward = findViewById(R.id.btnForward10);
        btnRewind = findViewById(R.id.btnBack10);

        txtPodcastTitle = findViewById(R.id.txtPodcastTitle);
        seekBar = findViewById(R.id.seekBar);

        txtPodcastTitle.setText("Beautiful In White");
        PlayerManager.currentTitle = "Beautiful In White";

        imgPodcast.setImageResource(R.drawable.beautiful_in_white);

        updateSeekBar();

        btnPlay.setOnClickListener(v -> {

            if(isBound){

                if(service.isPlaying()){
                    service.pause();
                }else{
                    service.resume();
                }

            }

        });

        btnRewind.setOnClickListener(v -> {

            if(isBound){
                int pos = service.getCurrentPosition();
                service.seekTo(Math.max(pos - 10000, 0));
            }

        });

        btnForward.setOnClickListener(v -> {

            if(isBound){
                int pos = service.getCurrentPosition();
                service.seekTo(pos + 10000);
            }

        });

        btnPrev.setOnClickListener(v -> playPrev());

        btnNext.setOnClickListener(v -> playNext());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser && isBound){
                    service.seekTo(progress);
                }

            }

            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar) {}

        });

        btnBack.setOnClickListener(v -> finish());

        registerReceiver(
                stateReceiver,
                new IntentFilter(PodcastService.ACTION_STATE)
        );

        registerReceiver(
                trackReceiver,
                new IntentFilter(PodcastService.ACTION_TRACK)
        );

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new CategoryFragment())
                .commit();
    }

    private void updateSeekBar() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(isBound){

                    int pos = service.getCurrentPosition();
                    seekBar.setProgress(pos);

                    SharedPreferences pref =
                            getSharedPreferences("podcast", MODE_PRIVATE);

                    pref.edit().putInt(txtPodcastTitle.getText().toString(),
                            pos).apply();
                }

                handler.postDelayed(this,200);
            }
        },0);

    }

    public void playPodcast(Podcast p){

        PlayerManager.currentTitle = p.getTitle();
        PlayerManager.currentImage = p.getImage();
        PlayerManager.currentIndex =
                PlayerManager.currentList.indexOf(p);

        txtPodcastTitle.setText(p.getTitle());
        imgPodcast.setImageResource(p.getImage());

        if(isBound){

            service.play(p.getAudio());

            SharedPreferences pref =
                    getSharedPreferences("podcast", MODE_PRIVATE);

            int pos = pref.getInt(p.getTitle(),0);

            service.seekTo(pos);

            seekBar.setMax(service.getDuration());

            btnPlay.setImageResource(R.drawable.ic_pause);

        }

    }

    public void playNext(){

        if(PlayerManager.currentList == null) return;

        int index = PlayerManager.currentIndex;

        if(index < PlayerManager.currentList.size() - 1){

            index++;

            PlayerManager.currentIndex = index;

            Podcast next = PlayerManager.currentList.get(index);

            playPodcast(next);

        }

    }

    public void playPrev(){

        if(PlayerManager.currentList == null) return;

        int index = PlayerManager.currentIndex;

        if(index > 0){

            index--;

            PlayerManager.currentIndex = index;

            Podcast prev = PlayerManager.currentList.get(index);

            playPodcast(prev);

        }

    }

    ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

            PodcastService.LocalBinder b =
                    (PodcastService.LocalBinder) binder;

            service = b.getService();

            isBound = true;

            service.play(R.raw.beautiful_in_white);

            seekBar.setMax(service.getDuration());

            btnPlay.setImageResource(R.drawable.ic_pause);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            isBound = false;

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(isBound){
            unbindService(connection);
            isBound = false;
        }

        unregisterReceiver(stateReceiver);
        unregisterReceiver(trackReceiver);
    }
}