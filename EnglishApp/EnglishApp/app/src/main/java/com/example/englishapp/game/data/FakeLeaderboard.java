package com.example.englishapp.game.data;

import com.example.englishapp.game.model.Player;
import java.util.ArrayList;

public class FakeLeaderboard {

    public static ArrayList<Player> getLeaderboard(){

        ArrayList<Player> list = new ArrayList<>();

        list.add(new Player("yeuthuonggia",2015));
        list.add(new Player("minh hang",1997));
        list.add(new Player("cao_thi_may",1982));
        list.add(new Player("kairi_sky",1924));
        list.add(new Player("thanh_bao",1877));

        return list;
    }

}