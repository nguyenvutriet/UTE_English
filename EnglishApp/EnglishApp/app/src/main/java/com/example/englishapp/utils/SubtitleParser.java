package com.example.englishapp.utils;



import com.example.englishapp.model.Subtitle;

import java.util.ArrayList;
import java.util.List;

public class SubtitleParser {

    public static List<Subtitle> parse(String srt){

        List<Subtitle> list = new ArrayList<>();

        String[] blocks = srt.split("\n\n");

        for(String block : blocks){

            String[] lines = block.split("\n");

            if(lines.length >= 3){

                String time = lines[1];

                String text = lines[2];

                float start =
                        parseTime(time.split(" --> ")[0]);

                float end =
                        parseTime(time.split(" --> ")[1]);

                list.add(new Subtitle(text,start,end));
            }
        }

        return list;
    }

    private static float parseTime(String time){

        String[] parts = time.split(":");

        float hour = Float.parseFloat(parts[0]);
        float minute = Float.parseFloat(parts[1]);

        String[] secParts = parts[2].split(",");

        float second = Float.parseFloat(secParts[0]);

        return hour*3600 + minute*60 + second;
    }
}