package com.example.englishapp.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.R;
import com.example.englishapp.model.Subtitle;
import com.example.englishapp.model.SubtitleTrack;
import com.example.englishapp.model.VideoInfo;
import com.example.englishapp.utils.SubtitleFetcher;
import com.example.englishapp.utils.YouTubeSubtitleApi;

import java.util.ArrayList;
import java.util.List;

public class CaptionsActivity extends AppCompatActivity {

    private EditText videoIdInput;
    private Button fetchButton;
    private ListView tracksList;
    private TextView transcriptText;

    private List<SubtitleTrack> tracks = new ArrayList<>();
    private String currentVideoId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captions_video);

        videoIdInput = findViewById(R.id.video_id_input);
        fetchButton = findViewById(R.id.fetch_button);
        tracksList = findViewById(R.id.tracks_list);
        transcriptText = findViewById(R.id.transcript_text);

        fetchButton.setOnClickListener(v -> fetchTracks());

        tracksList.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < tracks.size()) {
                SubtitleTrack t = tracks.get(position);
                transcriptText.setText("Loading track: " + t.getDisplayName());
                if (currentVideoId == null || currentVideoId.isEmpty()) {
                    Toast.makeText(CaptionsActivity.this, "Video ID missing", Toast.LENGTH_SHORT).show();
                    return;
                }
                SubtitleFetcher.fetchTrack(CaptionsActivity.this, currentVideoId, t, new YouTubeSubtitleApi.SubtitlesCallback() {
                    @Override
                    public void onSuccess(List<Subtitle> subtitles) {
                        runOnUiThread(() -> {
                            StringBuilder sb = new StringBuilder();
                            for (Subtitle s : subtitles) {
                                sb.append(s.getText()).append("\n");
                            }
                            transcriptText.setText(sb.toString());
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> Toast.makeText(CaptionsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                });
            }
        });
    }

    private void fetchTracks() {
        String videoId = videoIdInput.getText().toString().trim();
        if (videoId.isEmpty()) {
            Toast.makeText(this, "Enter a YouTube video ID", Toast.LENGTH_SHORT).show();
            return;
        }

        currentVideoId = videoId;
        transcriptText.setText("Fetching available caption tracks...");

        SubtitleFetcher.fetchAvailableTracks(this, videoId, new YouTubeSubtitleApi.VideoInfoCallback() {
            @Override
            public void onSuccess(VideoInfo info) {
                runOnUiThread(() -> {
                    tracks.clear();
                    tracks.addAll(info.getAvailableTracks());
                    List<String> names = new ArrayList<>();
                    for (SubtitleTrack t : tracks) {
                        names.add(t.getDisplayName() + " (" + t.getLanguageCode() + ")");
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CaptionsActivity.this, android.R.layout.simple_list_item_1, names);
                    tracksList.setAdapter(adapter);
                    transcriptText.setText("Select a track to download transcript.");
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> transcriptText.setText("Error fetching tracks: " + e.getMessage()));
            }
        });
    }
}
