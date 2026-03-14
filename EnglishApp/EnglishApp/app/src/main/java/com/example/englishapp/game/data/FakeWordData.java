package com.example.englishapp.game.data;

import com.example.englishapp.game.model.Word;
import java.util.ArrayList;

public class FakeWordData {

    public static ArrayList<Word> getEasyWords(){

        ArrayList<Word> list = new ArrayList<>();

        list.add(new Word("leak","đóng cửa","rò rỉ","phát triển","giữ kín","rò rỉ"));
        list.add(new Word("ancient","hiện đại","cổ đại","tương lai","tạm thời","cổ đại"));
        list.add(new Word("improve","cải thiện","giảm","phá","xóa","cải thiện"));
        list.add(new Word("protect","bảo vệ","tấn công","phá hủy","mở rộng","bảo vệ"));
        list.add(new Word("discover","khám phá","giấu","bỏ","ngăn","khám phá"));

        return list;
    }

}