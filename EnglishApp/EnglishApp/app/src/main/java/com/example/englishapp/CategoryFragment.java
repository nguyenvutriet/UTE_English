package com.example.englishapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Category> list;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.recyclerCategory);

        list = new ArrayList<>();

        // GIỮ NGUYÊN DANH MỤC CỦA BẠN
        list.add(new Category(
                "Học Tiếng Anh Qua Bài Hát",
                "Learn English through popular songs.",
                R.drawable.hoc_tieng_anh_qua_bai_hat));

        list.add(new Category(
                "Truyện Cổ Tích Việt Nam",
                "Vietnamese fairy tales in bilingual format.",
                R.drawable.truyen_co_tich));

        list.add(new Category(
                "Từ Vựng Thú Vị",
                "Expand your vocabulary with fun words.",
                R.drawable.ngu_phap_menh_de));

        list.add(new Category(
                "Tóm Tắt Truyện Bất Hủ",
                "Short summaries of timeless stories.",
                R.drawable.hoc_tieng_anh_qua_bai_hat));

        list.add(new Category(
                "Tóm Tắt Phim Kinh Điển",
                "Quick summaries of classic movies.",
                R.drawable.truyen_co_tich));

        list.add(new Category(
                "Ngữ Pháp Về Mệnh Đề",
                "Learn English clauses and sentence structure.",
                R.drawable.ngu_phap_menh_de));

        CategoryAdapter adapter = new CategoryAdapter(list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}