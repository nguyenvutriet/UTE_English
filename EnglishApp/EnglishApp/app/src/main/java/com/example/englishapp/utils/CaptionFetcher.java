package com.example.englishapp.utils;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Caption;
import com.google.api.services.youtube.model.CaptionListResponse;

import java.util.Collections;
import java.util.List;

public class CaptionFetcher {

    public static String getCaptionId(String videoId){

        try{

            YouTube youtube = YouTubeService.getService(null);

            YouTube.Captions.List request =
                    youtube.captions()
                            .list(Collections.singletonList("snippet"), videoId);

            request.setKey(YouTubeService.getApiKey());

            CaptionListResponse response = request.execute();

            List<Caption> captions = response.getItems();

            if(captions == null || captions.isEmpty())
                return null;

            return captions.get(0).getId();

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
