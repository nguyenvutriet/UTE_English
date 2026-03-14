package com.example.englishapp.game;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.game.adapter.LeaderboardAdapter;
import com.example.englishapp.game.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class LeaderboardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView txtMyRank;

    ArrayList<Player> list;

    String currentPlayer;
    TextView txtMyScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity_leaderboard);

        recyclerView = findViewById(R.id.recyclerLeaderboard);
        txtMyRank = findViewById(R.id.txtMyRank);
        txtMyScore = findViewById(R.id.txtMyScore);

        // lấy player từ activity trước
        currentPlayer = getIntent().getStringExtra("player");

        list = loadLeaderboard();

        // tính rank của player
        int rank = getPlayerRank();

        txtMyRank.setText("Your Rank: #" + rank);
        int score = getPlayerScore();
        txtMyScore.setText("Your Score: " + score);

        LeaderboardAdapter adapter = new LeaderboardAdapter(list, currentPlayer);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    ArrayList<Player> loadLeaderboard(){

        ArrayList<Player> players = new ArrayList<>();

        SharedPreferences prefs =
                getSharedPreferences("leaderboard",MODE_PRIVATE);

        Map<String,?> all = prefs.getAll();

        for(Map.Entry<String,?> entry : all.entrySet()){

            String name = entry.getKey();

            int score = (int) entry.getValue();

            players.add(new Player(name,score));
        }

        // sắp xếp điểm cao → thấp
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return p2.getScore() - p1.getScore();
            }
        });

        // chỉ lấy top 10
        if(players.size() > 10){
            players = new ArrayList<>(players.subList(0,10));
        }

        return players;
    }

    int getPlayerRank(){

        SharedPreferences prefs =
                getSharedPreferences("leaderboard",MODE_PRIVATE);

        Map<String,?> all = prefs.getAll();

        ArrayList<Player> players = new ArrayList<>();

        for(Map.Entry<String,?> entry : all.entrySet()){

            String name = entry.getKey();
            int score = (int) entry.getValue();

            players.add(new Player(name,score));
        }

        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return p2.getScore() - p1.getScore();
            }
        });

        for(int i = 0; i < players.size(); i++){

            if(players.get(i).getName().equals(currentPlayer)){

                return i + 1;
            }
        }

        return players.size();
    }
    int getPlayerScore(){

        SharedPreferences prefs =
                getSharedPreferences("leaderboard",MODE_PRIVATE);

        return prefs.getInt(currentPlayer,0);
    }
}