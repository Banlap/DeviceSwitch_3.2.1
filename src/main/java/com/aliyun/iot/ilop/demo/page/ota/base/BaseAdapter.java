package com.aliyun.iot.ilop.demo.page.ota.base;


import android.view.LayoutInflater;

import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.iot.aep.sdk.framework.AApplication;

/**
 * Created by david on 2018/4/9.
 *
 * @author david
 * @date 2018/04/09
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder<T>> {
    protected LayoutInflater inflater;

    public BaseAdapter() {
        inflater = LayoutInflater.from(AApplication.getInstance());
    }
}
