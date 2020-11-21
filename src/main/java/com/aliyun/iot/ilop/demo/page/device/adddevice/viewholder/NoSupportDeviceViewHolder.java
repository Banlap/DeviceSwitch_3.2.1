package com.aliyun.iot.ilop.demo.page.device.adddevice.viewholder;

import android.view.View;

import com.aliyun.iot.ilop.demo.page.adapter.BaseViewHolder;
import com.aliyun.iot.ilop.demo.page.device.bean.FoundDevice;

/**
 * 没有支持的设备
 */
public class NoSupportDeviceViewHolder extends BaseViewHolder<FoundDevice> {

    public NoSupportDeviceViewHolder(View itemView) {
        super(itemView);
    }


    @Override
    public void onBind(FoundDevice data, int position) {
        super.onBind(data, position);
    }
}
