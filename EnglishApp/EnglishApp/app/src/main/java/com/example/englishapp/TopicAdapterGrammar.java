package com.example.englishapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TopicAdapterGrammar extends RecyclerView.Adapter<TopicAdapterGrammar.ViewHolder>{

    Context context;
    ArrayList<TopicGrammar> list;

    public TopicAdapterGrammar(Context context, ArrayList<TopicGrammar> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_topic_grammar,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TopicGrammar topic = list.get(position);

        holder.txtTopic.setText(topic.getName());

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, LearningGrammarMethodActivity.class);
            intent.putExtra("topic",topic.getName());
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtTopic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTopic = itemView.findViewById(R.id.txtTopic);
        }
    }
}
