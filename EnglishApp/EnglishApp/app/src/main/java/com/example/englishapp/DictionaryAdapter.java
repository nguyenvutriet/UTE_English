package com.example.englishapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.ViewHolder>{

    Context context;
    List<Word> list;
    TextToSpeech tts;

    public DictionaryAdapter(Context context, List<Word> list){

        this.context = context;
        this.list = list;

        // Khởi tạo TextToSpeech
        tts = new TextToSpeech(context, status -> {

            if(status == TextToSpeech.SUCCESS){

                tts.setLanguage(Locale.US);
                tts.setSpeechRate(0.75f); // tốc độ đọc

            }

        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_word,parent,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){

        Word w = list.get(position);

        holder.word.setText(w.word);
        holder.type.setText(w.type);
        holder.uk.setText(w.uk);
        holder.us.setText(w.us);
        holder.meaning.setText(w.meaning);

        // 🔊 UK pronunciation
        holder.speakUk.setOnClickListener(v -> {

            tts.setLanguage(Locale.UK);
            tts.setSpeechRate(0.75f);
            tts.speak(w.word, TextToSpeech.QUEUE_FLUSH, null, null);

        });

        // 🔊 US pronunciation
        holder.speakUs.setOnClickListener(v -> {

            tts.setLanguage(Locale.US);
            tts.setSpeechRate(0.75f);
            tts.speak(w.word, TextToSpeech.QUEUE_FLUSH, null, null);

        });

        // ⭐ Lưu từ yêu thích
        SharedPreferences pref = context.getSharedPreferences("fav", Context.MODE_PRIVATE);

        boolean saved = pref.getBoolean(w.word,false);

        holder.star.setImageResource(saved ?
                android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);

        holder.star.setOnClickListener(v -> {

            SharedPreferences.Editor editor = pref.edit();

            boolean newState = !pref.getBoolean(w.word,false);

            editor.putBoolean(w.word,newState);
            editor.apply();

            notifyItemChanged(position);

        });

    }

    @Override
    public int getItemCount(){
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView word,type,uk,us,meaning;
        ImageView speakUk,speakUs,star;

        public ViewHolder(View itemView){
            super(itemView);

            word = itemView.findViewById(R.id.txtWord);
            type = itemView.findViewById(R.id.txtType);
            uk = itemView.findViewById(R.id.txtUk);
            us = itemView.findViewById(R.id.txtUs);
            meaning = itemView.findViewById(R.id.txtMeaning);

            speakUk = itemView.findViewById(R.id.btnSpeakUk);
            speakUs = itemView.findViewById(R.id.btnSpeakUs);
            star = itemView.findViewById(R.id.btnStar);

        }
    }
}