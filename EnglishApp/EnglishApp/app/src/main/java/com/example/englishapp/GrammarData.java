package com.example.englishapp;

import java.util.ArrayList;

public class GrammarData {

    public static ArrayList<Grammar> getGrammarList(){

        ArrayList<Grammar> list = new ArrayList<>();

        list.add(new Grammar(
                "Present Simple",
                "S + V(s/es)",
                "She plays soccer.",
                "Thì hiện tại đơn dùng để diễn tả thói quen hoặc sự thật hiển nhiên."
        ));

        list.add(new Grammar(
                "Present Continuous",
                "S + am/is/are + V-ing",
                "She is playing soccer.",
                "Diễn tả hành động đang xảy ra tại thời điểm nói."
        ));

        list.add(new Grammar(
                "Past Simple",
                "S + V2/ed",
                "She played soccer yesterday.",
                "Diễn tả hành động đã xảy ra trong quá khứ."
        ));

        list.add(new Grammar(
                "Future Simple",
                "S + will + V",
                "She will play soccer tomorrow.",
                "Diễn tả hành động sẽ xảy ra trong tương lai."
        ));

        return list;
    }
}