package com.example.englishapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReaderActivity extends AppCompatActivity {

    TextView txtEnglish, txtVietnamese;
    ImageView btnFont;
    TextView txtTitle;

    String currentBook;
    ScrollView scrollView;
    SharedPreferences readerPref;



    int currentChapter = 1;
    boolean isLoadingNext = false;

    // PANEL UI
    View overlay;
    LinearLayout fontPanel;
    LinearLayout layoutReader;
    SeekBar fontSeek;
    TextView btnDay, btnNight;
    ImageView btnMenu;
    ImageView btnClose;

    List<Word> dictionary = new ArrayList<>();

    TextToSpeech tts;

    SpannableString spannableText;

    float fontSize = 20f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        scrollView = findViewById(R.id.scrollView);
        btnMenu = findViewById(R.id.btnMenu);
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> finish());
        txtTitle = findViewById(R.id.txtTitle);
        txtEnglish = findViewById(R.id.txtEnglish);
        txtVietnamese = findViewById(R.id.txtVietnamese);
        btnFont = findViewById(R.id.btnFont);
        readerPref = getSharedPreferences("reader", MODE_PRIVATE);
        // NEW UI
        overlay = findViewById(R.id.viewOverlay);
        fontPanel = findViewById(R.id.fontPanel);
        layoutReader = findViewById(R.id.layoutReader);
        fontSeek = findViewById(R.id.fontSeek);
        btnDay = findViewById(R.id.btnDay);
        btnNight = findViewById(R.id.btnNight);
        btnMenu.setOnClickListener(v -> showChapterMenu());

        // ⭐ NHẬN SÁCH TỪ BOOKADAPTER
        String bookFile = getIntent().getStringExtra("book");

        currentBook = bookFile;
        String title = getIntent().getStringExtra("title");
        int image = getIntent().getIntExtra("image",0);

        TextView txtTitle = findViewById(R.id.txtTitle);
        ImageView imgBook = findViewById(R.id.imgBook);

        if(title != null) txtTitle.setText(title);
        if(image != 0) imgBook.setImageResource(image);

        currentChapter = readerPref.getInt(currentBook + "_chapter",1);

        if(bookFile != null){
            loadBook(bookFile + "_ch" + currentChapter);
        }else{
            loadBook("uncle_tom_ch" + currentChapter);
        }

        // ⭐ restore scroll position
        scrollView.post(() -> {

            int scroll = readerPref.getInt(currentBook + "_scroll",0);

            scrollView.scrollTo(0,scroll);

        });

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {

            int scrollY = scrollView.getScrollY();

            readerPref.edit()
                    .putInt(currentBook + "_scroll", scrollY)
                    .apply();

        });

        // OPEN PANEL
        btnFont.setOnClickListener(v -> openPanel());

        overlay.setOnClickListener(v -> closePanel());

        // SEEK FONT SIZE
        fontSeek.setProgress((int) fontSize);

        fontSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){

                if(progress < 14) progress = 14;

                fontSize = progress;
                txtTitle.setTextSize(fontSize + 6);
                txtEnglish.setTextSize(fontSize);
                txtVietnamese.setTextSize(fontSize-2);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar){}
            @Override public void onStopTrackingTouch(SeekBar seekBar){}
        });

        // DAY MODE
        btnDay.setOnClickListener(v -> {

            layoutReader.setBackgroundColor(Color.WHITE);

            txtEnglish.setTextColor(Color.BLACK);
            txtVietnamese.setTextColor(Color.DKGRAY);

        });

        // NIGHT MODE
        btnNight.setOnClickListener(v -> {

            layoutReader.setBackgroundColor(Color.BLACK);

            txtEnglish.setTextColor(Color.WHITE);
            txtVietnamese.setTextColor(Color.LTGRAY);

        });


        // TTS
        tts = new TextToSpeech(this, status -> {

            if(status == TextToSpeech.SUCCESS){

                tts.setLanguage(Locale.US);
                tts.setSpeechRate(0.75f);

            }

        });

        loadDictionary();

        enableWordTap();
    }


    // ======================
    // PANEL OPEN
    // ======================

    private void openPanel(){

        fontPanel.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);

        fontPanel.setTranslationY(fontPanel.getHeight());

        fontPanel.animate()
                .translationY(0)
                .setDuration(250)
                .start();
    }

    // ======================
    // PANEL CLOSE
    // ======================

    private void closePanel(){

        fontPanel.animate()
                .translationY(fontPanel.getHeight())
                .setDuration(200)
                .withEndAction(() -> {

                    fontPanel.setVisibility(View.GONE);
                    overlay.setVisibility(View.GONE);

                })
                .start();
    }


    // ======================
    // LOAD DICTIONARY
    // ======================

    private void loadDictionary(){

        try{

            InputStream is = getResources().openRawResource(R.raw.dictionary_1);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();

            String json = new String(buffer,"UTF-8");

            JSONArray array = new JSONArray(json);

            for(int i=0;i<array.length();i++){

                JSONObject obj = array.getJSONObject(i);

                dictionary.add(new Word(

                        obj.getString("word"),
                        obj.getString("type"),
                        obj.getString("uk"),
                        obj.getString("us"),
                        obj.getString("meaning")

                ));
            }

        }catch(Exception e){

            e.printStackTrace();

        }
    }


    // ======================
    // LOAD BOOK
    // ======================

    private void loadBook(String book){

        try{

            int id = getResources().getIdentifier(book,"raw",getPackageName());

            InputStream is = getResources().openRawResource(id);

            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String json = new String(buffer,"UTF-8");

            JSONObject obj = new JSONObject(json);

            String chapter = obj.getString("chapter");
            String title = obj.getString("title");
            String image = obj.getString("image");

            JSONArray array = obj.getJSONArray("content");

            TextView txtTitle = findViewById(R.id.txtTitle);
            ImageView imgBook = findViewById(R.id.imgBook);

            // ===== TITLE STYLE =====

            SpannableString titleSpan =
                    new SpannableString(chapter + "\n" + title);

            // Chapter bold
            titleSpan.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    0,
                    chapter.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            // Title bold + italic
            titleSpan.setSpan(
                    new StyleSpan(Typeface.BOLD_ITALIC),
                    chapter.length() + 1,
                    titleSpan.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            // ↓ giảm kích thước title
            titleSpan.setSpan(
                    new RelativeSizeSpan(0.8f),
                    chapter.length() + 1,
                    titleSpan.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            txtTitle.setText(titleSpan);

            int imgId = getResources().getIdentifier(image,"drawable",getPackageName());
            imgBook.setImageResource(imgId);


            // ===== BUILD TEXT =====

            String fullText = "";

            for(int i=0;i<array.length();i++){

                JSONObject line = array.getJSONObject(i);

                String en = line.getString("en");
                String vi = line.getString("vi");

                fullText += "    " + en + "\n\n";
                fullText += "    " + vi + "\n\n";
            }

            spannableText = new SpannableString(fullText);

            int index = 0;

            for(int i=0;i<array.length();i++){

                JSONObject line = array.getJSONObject(i);

                String en = "    " + line.getString("en");
                String vi = "    " + line.getString("vi");

                int enStart = index;
                int enEnd = index + en.length();

                index = enEnd + 2;

                int viStart = index;
                int viEnd = index + vi.length();

                index = viEnd + 2;

                // Vietnamese italic
                spannableText.setSpan(
                        new StyleSpan(Typeface.ITALIC),
                        viStart,
                        viEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                // Vietnamese sans-serif
                spannableText.setSpan(
                        new android.text.style.TypefaceSpan("sans-serif"),
                        viStart,
                        viEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                // Vietnamese gray
                spannableText.setSpan(
                        new ForegroundColorSpan(Color.parseColor("#666666")),
                        viStart,
                        viEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }

            txtEnglish.setText(spannableText, TextView.BufferType.SPANNABLE);
            txtVietnamese.setText("");

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    // ======================
    // TAP WORD
    // ======================

    private void enableWordTap(){

        txtEnglish.setOnTouchListener((v,event)->{

            if(event.getAction()==MotionEvent.ACTION_UP){

                int x = (int) event.getX();
                int y = (int) event.getY();

                Layout layout = txtEnglish.getLayout();

                if(layout == null) return false;

                int line = layout.getLineForVertical(y);
                int offset = layout.getOffsetForHorizontal(line,x);


                String text = spannableText.toString();

                int start = offset;
                int end = offset;

                while(start > 0 && Character.isLetter(text.charAt(start-1))){
                    start--;
                }

                while(end < text.length() && Character.isLetter(text.charAt(end))){
                    end++;
                }

                if(start >= end) return false;

                String word = text.substring(start,end);

                highlightWord(start,end);

                showTranslation(word);

            }

            return true;

        });
    }


    // ======================
    // HIGHLIGHT WORD
    // ======================

    private void highlightWord(int start,int end){

        spannableText.setSpan(

                new ForegroundColorSpan(Color.parseColor("#2ECC71")),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE

        );

        txtEnglish.setText(spannableText, TextView.BufferType.SPANNABLE);
    }


    // ======================
    // SHOW DICTIONARY
    // ======================

    private void showTranslation(String wordText){

        Word word = findWord(wordText);

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_word,null);

        TextView w = view.findViewById(R.id.dialogWord);
        TextView type = view.findViewById(R.id.dialogType);
        TextView uk = view.findViewById(R.id.dialogUk);
        TextView us = view.findViewById(R.id.dialogUs);
        TextView meaning = view.findViewById(R.id.dialogMeaning);

        ImageView speakUk = view.findViewById(R.id.dialogSpeakUk);
        ImageView speakUs = view.findViewById(R.id.dialogSpeakUs);
        ImageView star = view.findViewById(R.id.dialogStar);

        SharedPreferences pref =
                getSharedPreferences("fav", Context.MODE_PRIVATE);

        if(word != null){

            w.setText(word.word);
            type.setText(word.type);
            uk.setText(word.uk);
            us.setText(word.us);
            meaning.setText(word.meaning);

            speakUk.setOnClickListener(v->{

                tts.setLanguage(Locale.UK);

                tts.speak(word.word,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null);

            });

            speakUs.setOnClickListener(v->{

                tts.setLanguage(Locale.US);

                tts.speak(word.word,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null);

            });

            boolean saved = pref.getBoolean(word.word,false);

            star.setImageResource(saved ?
                    android.R.drawable.btn_star_big_on
                    : android.R.drawable.btn_star_big_off);

            star.setOnClickListener(v->{

                SharedPreferences.Editor editor = pref.edit();

                boolean newState =
                        !pref.getBoolean(word.word,false);

                editor.putBoolean(word.word,newState);

                editor.apply();

                star.setImageResource(newState ?
                        android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off);

            });

        }else{

            w.setText(wordText);
            meaning.setText("Không có trong từ điển");

        }

        new AlertDialog.Builder(this)
                .setView(view)
                .show();
    }


    // ======================
    // FIND WORD
    // ======================

    private Word findWord(String text){

        for(Word w : dictionary){

            if(w.word.equalsIgnoreCase(text)){

                return w;

            }
        }

        return null;
    }

    private void showChapterMenu(){

        List<String> list = new ArrayList<>();

        int i = 1;

        while(true){

            int id = getResources().getIdentifier(
                    currentBook + "_ch" + i,
                    "raw",
                    getPackageName()
            );

            if(id == 0) break;   // không còn chương

            list.add("Chapter " + i);

            i++;
        }

        String[] chapters = list.toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle("Select Chapter")
                .setItems(chapters,(dialog,which)->{

                    currentChapter = which + 1;

                    readerPref.edit()
                            .putInt(currentBook + "_chapter", currentChapter)
                            .apply();

                    loadBook(currentBook + "_ch" + currentChapter);

                })
                .show();
    }
}