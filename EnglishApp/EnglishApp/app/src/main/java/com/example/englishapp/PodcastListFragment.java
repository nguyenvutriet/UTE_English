package com.example.englishapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PodcastListFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Podcast> list;
    TextView txtCategory;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_podcast_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerPodcast);
        txtCategory = view.findViewById(R.id.txtCategory);

        String category = getArguments().getString("category");

        txtCategory.setText(category);

        list = new ArrayList<>();

        // Chỉ danh mục "Học Tiếng Anh Qua Bài Hát" có podcast
        if(category.equals("Học Tiếng Anh Qua Bài Hát")){

            list.add(new Podcast(
                    "Beautiful In White",
                    "Váy cưới tinh khôi",
                    R.drawable.beautiful_in_white,
                    R.raw.beautiful_in_white));

            list.add(new Podcast(
                    "I Like You So Much You'll Know It",
                    "Em thích anh nhiều lắm, rồi anh sẽ biết",
                    R.drawable.i_like_you_so_much_you_ll_know_it,
                    R.raw.i_like_you_so_much_you_ll_know_it));

            list.add(new Podcast(
                    "Endless Love",
                    "Thần thoại",
                    R.drawable.endless_love,
                    R.raw.endless_love));

            list.add(new Podcast(
                    "Something Just Like This",
                    "Chỉ cần một điều như thế thôi",
                    R.drawable.something_just_like_this,
                    R.raw.something_just_like_this));

            list.add(new Podcast(
                    "Aloha",
                    "Xin chào",
                    R.drawable.aloha,
                    R.raw.aloha));

            list.add(new Podcast(
                    "Pround Of You",
                    "Xin chào",
                    R.drawable.pround_of_you,
                    R.raw.pround_of_you));
        } else if (category.equals("Truyện Cổ Tích Việt Nam")){

            list.add(new Podcast(
                    "Little Red Riding Hood",
                    "Cô bé quàng khăn đỏ",
                    R.drawable.co_be_quang_khan_do,
                    R.raw.story2));

            list.add(new Podcast(
                    "The Golden Squirrel Dreams of Flying",
                    "Sóc vàng ước mơ bay",
                    R.drawable.soc_vang_uoc_mo_bay,
                    R.raw.story1));

            list.add(new Podcast(
                    "The Fox and the Grapes",
                    "Chú sói và chùm nho",
                    R.drawable.chu_soi_va_chum_nho,
                    R.raw.story3));
        }

        PodcastAdapter adapter = new PodcastAdapter(list);
        PlayerManager.currentList = list;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.startProgressUpdate();
        ImageButton btnBackCategory = view.findViewById(R.id.btnBackCategory);

        btnBackCategory.setOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );

        return view;
    }
}