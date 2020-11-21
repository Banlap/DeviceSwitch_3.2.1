package com.aliyun.iot.ilop.demo.page.adapter;


import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {




    public BaseViewHolder(View itemView) {
        super(itemView);
    }




    public void onBind(T data,int position){

    }


}
