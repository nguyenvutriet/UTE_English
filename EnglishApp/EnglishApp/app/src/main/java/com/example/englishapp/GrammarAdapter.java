package com.example.englishapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GrammarAdapter extends RecyclerView.Adapter<GrammarAdapter.ViewHolder>{

    Context context;
    ArrayList<Grammar> list;

    public GrammarAdapter(Context context, ArrayList<Grammar> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_grammar,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Grammar g = list.get(position);

        holder.txtTitle.setText(g.getTitle());
        holder.txtFormula.setText("Formula: " + g.getFormula());
        holder.txtExample.setText("Example: " + g.getExample());
        holder.txtExplain.setText(g.getExplanation());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtTitle,txtFormula,txtExample,txtExplain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtFormula = itemView.findViewById(R.id.txtFormula);
            txtExample = itemView.findViewById(R.id.txtExample);
            txtExplain = itemView.findViewById(R.id.txtExplain);
        }
    }
}
