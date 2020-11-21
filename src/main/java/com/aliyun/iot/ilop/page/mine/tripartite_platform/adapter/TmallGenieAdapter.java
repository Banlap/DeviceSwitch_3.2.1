package com.aliyun.iot.ilop.page.mine.tripartite_platform.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.page.mine.MineConstants;
import com.aliyun.iot.ilop.page.mine.SdkFrameworkUtils;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.activity.H5Activity;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.activity.TmallGenieActivity;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.adapter.holder.TmallGenieFootViewHolder;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.adapter.holder.TmallGenieHeadViewHolder;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.adapter.holder.TmallGenieViewHolder;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.TripartitePlatformListBean;
import com.aliyun.iot.ilop.page.mine.view.TmallGenieFootItem;
import com.aliyun.iot.ilop.page.mine.view.TmallGenieHeadItem;
import com.aliyun.iot.ilop.page.mine.view.TmallGenieItem;

import java.util.ArrayList;
import java.util.List;

import static com.aliyun.iot.ilop.page.mine.tripartite_platform.activity.TmallGenieActivity.REQUEST_CODE;

public class TmallGenieAdapter extends BaseAdapter<TripartitePlatformListBean> {
    private TmallGenieActivity mContext;
    private List<TripartitePlatformListBean.DataBean> mList = new ArrayList<>();
    private TripartitePlatformListBean data;
    private OnItemClickListener onItemClickListener;
    private boolean isShowFoot = false;
    private String channel;

    private boolean isBindAccountFlag = false;

    public void isBindAccountFlag(boolean bindAccountFlag) {
        isBindAccountFlag = bindAccountFlag;
        notifyDataSetChanged();
    }

    public TmallGenieAdapter(TmallGenieActivity mContext, String channel, boolean isShowFoot) {
        this.mContext = mContext;
        this.isShowFoot = isShowFoot;
        this.channel = channel;
    }

    public void setList(List<TripartitePlatformListBean.DataBean> list, TripartitePlatformListBean data) {
        this.mList = list;
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public BaseViewHolder<TripartitePlatformListBean> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.HEAD.ordinal()) {
            //创建第一种holder
            return new TmallGenieHeadViewHolder(new TmallGenieHeadItem(mContext), channel, bindUserListener, bindStepListener);
        } else if (viewType == ItemType.FOOT.ordinal()) {
            return new TmallGenieFootViewHolder(new TmallGenieFootItem(mContext), getInstruction(channel),
                    channel.equals(MineConstants.IFTTT) ?
                            mContext.getString(R.string.thirdparty_you_can_use_that) :
                            mContext.getString(R.string.thirdparty_you_can_say_that)
                    , channel);
        } else {
            TmallGenieItem view = new TmallGenieItem(mContext);
            return new TmallGenieViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<TripartitePlatformListBean> holder, int position) {
        if (holder instanceof TmallGenieViewHolder) {
            if (mList.size() > 0) {
                String url = TextUtils.isEmpty(mList.get(position - 1).getIcon())
                        ? ""
                        : mList.get(position - 1).getIcon();
                ((TmallGenieViewHolder) holder).getView().setImageUrl(url);
                ((TmallGenieViewHolder) holder).getView().isShowBlankTv(false);
                ((TmallGenieViewHolder) holder).getView().setTitle(mList.get(position - 1).getLabel());
                ((TmallGenieViewHolder) holder).getView().isShowLine(position != mList.size());
                ((TmallGenieViewHolder) holder).getView().isShowBar(position == 1);
            } else {
                ((TmallGenieViewHolder) holder).getView().isShowBlankTv(true);
            }
        } else if (holder instanceof TmallGenieHeadViewHolder) {
            if (null != data) {
                ((TmallGenieHeadViewHolder) holder).getView().setImageResource(data.getImg());
                ((TmallGenieHeadViewHolder) holder).getView().isBindAccountFlag(isBindAccountFlag,
                        isBindAccountFlag ? mContext.getString(R.string.thirdparty_unbinded_account) :
                                mContext.getString(R.string.thirdparty_binded_account));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (null == mList) {
            mList = new ArrayList<>();
        }
        int count = mList.size() + 1;
        if (isShowFoot) {
            count += 1;
        }
        if (!channel.equals(MineConstants.IFTTT) && mList.size() == 0) {
            count += 1;
        }
        return count;
    }

    public interface OnItemClickListener {
        void onItemClick(TripartitePlatformListBean data, int position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ItemType.HEAD.ordinal();
        } else if (isShowFoot && getItemCount() - 1 == position) {
            return ItemType.FOOT.ordinal();
        } else {
            return ItemType.CONTENT.ordinal();
        }
    }

    public enum ItemType {
        HEAD, CONTENT, FOOT
    }

    private View.OnClickListener bindUserListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isBindAccountFlag) {
                // TODO: 2018/7/13 解绑
                mContext.unBindAccount();
            } else {
                String mAppKey = SdkFrameworkUtils.getAppKey();
                String url = "https://oauth.taobao.com/authorize?response_type=code&client_id=" + mAppKey + "&redirect_uri=https://living.tmallauth.com&view=wap";
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                Intent intent = new Intent(v.getContext(), H5Activity.class);
                intent.putExtras(bundle);
                ((Activity) v.getContext()).startActivityForResult(intent, REQUEST_CODE);
                //SDK 淘宝授权 暂时不用
//                mContext.BindAccount();

            }

        }
    };
    private View.OnClickListener bindStepListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = getBindUrl(channel);
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            Intent intent = new Intent(v.getContext(), H5Activity.class);
            intent.putExtras(bundle);
            ((Activity) v.getContext()).startActivityForResult(intent, REQUEST_CODE);
        }
    };

    private String getInstruction(String channel) {
        switch (channel) {
            case MineConstants.TM:
                return mContext.getString(R.string.thirdparty_instruction_TmallGenie);
            case MineConstants.AA:
                return mContext.getString(R.string.mine_tp_aa_instruction);
            case MineConstants.GA:
                return mContext.getString(R.string.mine_tp_ga_instruction);
            default:
                return mContext.getString(R.string.mine_tp_ifttt_instruction);
        }

    }

    private String getBindUrl(String channel) {
        switch (channel) {
            case MineConstants.TM:
                return mContext.getString(R.string.thirdparty_instruction_TmallGenie);
            case MineConstants.AA:
                return mContext.getString(R.string.minie_tp_aa_html);
            case MineConstants.GA:
                return mContext.getString(R.string.minie_tp_ga_html);
            default:
                return mContext.getString(R.string.minie_tp_ifttt_html);
        }

    }
}
