package com.aliyun.iot.ilop.demo.page.language;

import android.graphics.drawable.Drawable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.iot.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sinyuk on 09,December,2019
 **/

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.MyViewHolder> {
    private List<LanguageModel> list = new ArrayList<>();

    public List<LanguageModel> getList() {
        return list;
    }

    public void setList(List<LanguageModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    private int selected = RecyclerView.NO_POSITION;

    int getSelected() {
        return selected;
    }

    void setSelected(int selected) {
        this.selected = selected;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.language_setting_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        LanguageModel item = list.get(i);
        holder.bind(item, selected == holder.getAdapterPosition());
        holder.textView.setOnClickListener(view -> {
            if (selected != holder.getAdapterPosition()) {
                selected = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }

        void bind(LanguageModel item, boolean selected) {
            textView.setText(item.language);
            Drawable drawableEnd;
            if (selected) {
                drawableEnd = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_check_1);
            } else {
                drawableEnd = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_check_0);
            }
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawableEnd, null);
        }
    }
}
