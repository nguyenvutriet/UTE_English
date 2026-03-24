package com.example.englishapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.englishapp.R;
import com.example.englishapp.model.QuizResult;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<QuizResult> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onRename(QuizResult result);
        void onItemClick(QuizResult result); // Thêm cái này
    }

    public HistoryAdapter(List<QuizResult> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {
        QuizResult r = list.get(pos);
        h.tvTitle.setText(r.title);
        h.tvScore.setText("Điểm: " + r.score + "/" + r.total);
        h.tvTime.setText(r.dateTime);
        h.btnEdit.setOnClickListener(v -> listener.onRename(r));
        h.itemView.setOnClickListener(v -> listener.onItemClick(r));
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvScore, tvTime;
        ImageButton btnEdit;
        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHistoryTitle);
            tvScore = itemView.findViewById(R.id.tvHistoryScore);
            tvTime = itemView.findViewById(R.id.tvHistoryTime);
            btnEdit = itemView.findViewById(R.id.btnEditTitle);
        }
    }
}