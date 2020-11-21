package com.aliyun.iot.ilop.page.mine.tripartite_platform.adapter.holder;


import com.aliyun.iot.ilop.page.mine.tripartite_platform.adapter.BaseViewHolder;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.TripartitePlatformListBean;
import com.aliyun.iot.ilop.page.mine.view.TmallGenieItem;

public class TmallGenieViewHolder extends BaseViewHolder<TripartitePlatformListBean> {
    private TmallGenieItem mItem;

    public TmallGenieViewHolder(TmallGenieItem itemView) {
        super(itemView);
        mItem = itemView;

    }

    @Override
    public void bindData(TripartitePlatformListBean data, boolean maybeLatest) {

    }

    public TmallGenieItem getView() {
        return mItem;
    }

}
