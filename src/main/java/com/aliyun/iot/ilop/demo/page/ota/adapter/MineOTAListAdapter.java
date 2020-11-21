package com.aliyun.iot.ilop.demo.page.ota.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.aliyun.iot.ilop.demo.page.ota.adapter.holder.DeviceViewHolder;
import com.aliyun.iot.ilop.demo.page.ota.base.BaseAdapter;
import com.aliyun.iot.ilop.demo.page.ota.base.BaseViewHolder;
import com.aliyun.iot.ilop.demo.page.ota.bean.OTADeviceSimpleInfo;
import com.aliyun.iot.ilop.demo.page.ota.view.MineOTAListItem;

/**
 * Created by david on 2018/4/9.
 *
 * @author david
 * @date 2018/04/09
 */
public class MineOTAListAdapter extends BaseAdapter<OTADeviceSimpleInfo> {
    private Context mContext;
    private List<OTADeviceSimpleInfo> deviceList = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public MineOTAListAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<OTADeviceSimpleInfo> list) {
        if (null == list) {
            return;
        }

        deviceList = list;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public BaseViewHolder<OTADeviceSimpleInfo> onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = new MineOTAListItem(mContext);
        BaseViewHolder<OTADeviceSimpleInfo> holder = new DeviceViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<OTADeviceSimpleInfo> holder, final int position) {
        if (null != mOnItemClickListener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickListener) {
                        mOnItemClickListener.onItemClick(deviceList.get(position), position);
                    }
                }
            });
        } else {
            holder.itemView.setOnClickListener(null);
        }

        boolean maybeLatest = (position == getItemCount() - 1);

        holder.bindData(deviceList.get(position), maybeLatest);
    }

    @Override
    public int getItemCount() {
        if (null == deviceList) {
            return 0;
        }
        return deviceList.size();
    }

    public interface OnItemClickListener {
        /**
         * 点击item的时候触发回调
         * @param info
         * @param position
         */
        void onItemClick(OTADeviceSimpleInfo info, int position);
    }
}
