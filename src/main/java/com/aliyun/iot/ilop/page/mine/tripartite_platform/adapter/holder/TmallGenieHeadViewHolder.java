package com.aliyun.iot.ilop.page.mine.tripartite_platform.adapter.holder;


import android.view.View;

import androidx.annotation.Nullable;

import com.aliyun.iot.ilop.page.mine.tripartite_platform.adapter.BaseViewHolder;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.TripartitePlatformListBean;
import com.aliyun.iot.ilop.page.mine.view.TmallGenieHeadItem;

public class TmallGenieHeadViewHolder extends BaseViewHolder<TripartitePlatformListBean> {
    private TmallGenieHeadItem view;
    private String channel;

    public TmallGenieHeadViewHolder(TmallGenieHeadItem itemView, String channel,
                                    @Nullable View.OnClickListener bindListener,
                                    @Nullable View.OnClickListener bindStepListener
    ) {
        super(itemView);
        this.view = itemView;
        this.channel = channel;
        this.view.setChannel(channel);
        this.view.onBindClick(bindListener);
        this.view.onBindStepClick(bindStepListener);
    }

    @Override
    public void bindData(TripartitePlatformListBean data, boolean maybeLatest) {
        view.setImageResource(data.getImg());
    }

    public TmallGenieHeadItem getView() {
        return view;
    }
}
