package com.aliyun.iot.ilop.demo.page.ota.adapter.holder;

import android.view.View;
import com.aliyun.iot.ilop.demo.page.ota.base.BaseViewHolder;
import com.aliyun.iot.ilop.demo.page.ota.bean.OTADeviceSimpleInfo;
import com.aliyun.iot.ilop.demo.page.ota.view.MineOTAListItem;

/**
 * Created by david on 2018/4/9.
 *
 * @author david
 * @date 2018/04/09
 */
public class DeviceViewHolder extends BaseViewHolder<OTADeviceSimpleInfo> {
    private MineOTAListItem mItem;

    public DeviceViewHolder(View itemView) {
        super(itemView);

        if (itemView instanceof MineOTAListItem) {
            mItem = (MineOTAListItem)itemView;
        }
    }

    @Override
    public void bindData(OTADeviceSimpleInfo data, boolean maybeLatest) {
        if (null == mItem) {
            return;
        }

        if (null == data) {
            return;
        }

        mItem.setTitle(data.deviceName);
        mItem.showDeviceImage(data.image);

        if (maybeLatest) {
            mItem.showUnderLine(false);
        }
    }
}
