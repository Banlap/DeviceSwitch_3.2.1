package com.aliyun.iot.ilop.page.mine.tripartite_platform.adapter.holder;

import com.aliyun.iot.ilop.page.mine.MineConstants;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.adapter.BaseViewHolder;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.TripartitePlatformListBean;
import com.aliyun.iot.ilop.page.mine.view.TmallGenieFootItem;

public class TmallGenieFootViewHolder extends BaseViewHolder<TripartitePlatformListBean> {
    private String instruction;
    private TmallGenieFootItem mItem;

    public TmallGenieFootViewHolder(TmallGenieFootItem itemView, String instruction, String title, String channel) {
        super(itemView);
        this.mItem = itemView;
        this.instruction = instruction;
        mItem.setCt(title, instruction);
        if (MineConstants.IFTTT.equalsIgnoreCase(channel)) {
            mItem.setIFTTT();
        }

    }

    @Override
    public void bindData(TripartitePlatformListBean data, boolean maybeLatest) {

    }

    public TmallGenieFootItem getmItem() {
        return mItem;
    }
}
