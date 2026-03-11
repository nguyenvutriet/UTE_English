package com.example.englishapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder>{

    Context context;
    ArrayList<Question> list;

    public ResultAdapter(Context context, ArrayList<Question> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_result,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Question q = list.get(position);

        holder.txtQuestion.setText(q.getQuestion());
        holder.txtYourAnswer.setText("Your answer: " + q.getUserAnswer());
        holder.txtCorrect.setText("Correct: " + q.getCorrect());

        if(q.getUserAnswer().equals(q.getCorrect())){
            holder.txtResult.setText("✔ Correct");
        }else{
            holder.txtResult.setText("✘ Wrong");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtQuestion, txtYourAnswer, txtCorrect, txtResult;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtQuestion = itemView.findViewById(R.id.txtQuestion);
            txtYourAnswer = itemView.findViewById(R.id.txtYourAnswer);
            txtCorrect = itemView.findViewById(R.id.txtCorrect);
            txtResult = itemView.findViewById(R.id.txtResult);
        }
    }
}