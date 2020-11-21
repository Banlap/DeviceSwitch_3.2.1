package com.aliyun.iot.ilop.demo.page.device.adddevice.viewholder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo;
import com.aliyun.alink.business.devicecenter.api.discovery.DiscoveryType;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.adapter.BaseViewHolder;
import com.aliyun.iot.ilop.demo.page.device.bean.FoundDevice;
import com.aliyun.iot.ilop.demo.page.device.bean.FoundDeviceListItem;

/**
 * wifi ，网关设备
 */
public class LocalDeviceFoundViewHolder extends BaseViewHolder<FoundDevice> {
    private static String CODE = "link://router/connectConfig";

    private TextView tv_device_name;
    private TextView tv_device_mac;
    private Button btn_device_connect;

    public LocalDeviceFoundViewHolder(View itemView) {
        super(itemView);
        tv_device_name = itemView.findViewById(R.id.list_item_device_name);
        btn_device_connect = itemView.findViewById(R.id.list_item_device_action);
        tv_device_mac = itemView.findViewById(R.id.list_item_device_mac);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBind(FoundDevice data, int position) {
        super.onBind(data, position);
        FoundDeviceListItem foundDeviceListItem = (FoundDeviceListItem) data;
        if (!TextUtils.isEmpty(foundDeviceListItem.deviceName)) {
            tv_device_name.setText(foundDeviceListItem.deviceName);
        } else if (!TextUtils.isEmpty(foundDeviceListItem.productName)) {
            tv_device_name.setText(foundDeviceListItem.productName);
        } else {
            tv_device_name.setText("");
        }
        if (foundDeviceListItem.discoveryType == DiscoveryType.CLOUD_ENROLLEE_DEVICE) {
            btn_device_connect.setText("连接");
        } else {
            btn_device_connect.setText("绑定");
        }

        final DeviceInfo deviceInfo = foundDeviceListItem.deviceInfo;
        if (null == deviceInfo) return;

        String mac = foundDeviceListItem.getDeviceInfo().mac;
        if (TextUtils.isEmpty(mac)) {
            tv_device_mac.setText(null);
        } else {
            tv_device_mac.setText("mac:" + mac);
        }

        btn_device_connect.setOnClickListener(view -> {
            if (foundDeviceListItem.discoveryType == DiscoveryType.LOCAL_ONLINE_DEVICE) {
                /**
                 * {
                 "awssVer": "xxx", //有就传
                 "productKey": "xxx",
                 "deviceName": "xxx",
                 "regProductKey": "", // 一般待配网设备有 有就透传
                 "regDeviceName": "", // 一般待配网设备有 有就透传
                 "mac": "xxx", // 可能没有  设备端返回就会传   有就透传
                 "token": "xxx", // 可能没有  设备端返回就会传  不需要传到setDevice
                 "provisionStatus": 0, // 0待配 1已配   有就透传
                 "devType": 0, // 0: wifi device, 1: ethernet device, ... 设备端返回就会传 有就透传
                 "bssid": "xxx", // 设备端返回就会传 有就透传
                 "addDeviceFrom": "ROUTER"， //  有就透传 可选值为ROUTER 或“”
                 "linkType": "ForceAliLinkTypeNone"
                 }
                 */
                String code = CODE;
                Bundle bundle = new Bundle();
                bundle.putString("awssVer", deviceInfo.awssVer.toString());
                bundle.putString("mac", deviceInfo.mac);
                bundle.putString("productKey", deviceInfo.productKey);
                bundle.putString("deviceName", deviceInfo.deviceName);
                bundle.putString("regProductKey", deviceInfo.regProductKey);
                bundle.putString("regDeviceName", deviceInfo.regDeviceName);
                bundle.putString("token", deviceInfo.token);
                bundle.putString("devType", deviceInfo.devType);
                bundle.putString("addDeviceFrom", deviceInfo.addDeviceFrom);
                bundle.putString("linkType", deviceInfo.linkType);
                Router.getInstance().toUrlForResult((Activity) view.getContext(), code, 1, bundle);
            } else {
                /**
                 * {
                 "awssVer": "xxx", //有就传
                 "productKey": "xxx",
                 "deviceName": "xxx",
                 "regProductKey": "", // 一般待配网设备有 有就透传
                 "regDeviceName": "", // 一般待配网设备有 有就透传
                 "mac": "xxx", // 可能没有  设备端返回就会传   有就透传
                 "token": "xxx", // 可能没有  设备端返回就会传  不需要传到setDevice
                 "provisionStatus": 0, // 0待配 1已配   有就透传
                 "devType": 0, // 0: wifi device, 1: ethernet device, ... 设备端返回就会传 有就透传
                 "bssid": "xxx", // 设备端返回就会传 有就透传
                 "addDeviceFrom": "ROUTER"， //  有就透传 可选值为ROUTER 或“”
                 "linkType": "ForceAliLinkTypeNone"
                 }
                 */
                String code = CODE;
                Bundle bundle = new Bundle();
                if (deviceInfo.awssVer != null) {
                    bundle.putString("awssVer", deviceInfo.awssVer.toString());
                }

                bundle.putString("mac", deviceInfo.mac);
                bundle.putString("productKey", deviceInfo.productKey);
                bundle.putString("deviceName", deviceInfo.deviceName);
                bundle.putString("regProductKey", deviceInfo.regProductKey);
                bundle.putString("regDeviceName", deviceInfo.regDeviceName);
                bundle.putString("token", deviceInfo.token);
                bundle.putString("devType", deviceInfo.devType);
                bundle.putString("addDeviceFrom", deviceInfo.addDeviceFrom);
                bundle.putString("linkType", deviceInfo.linkType);

                Router.getInstance().toUrlForResult((Activity) view.getContext(), code, 1, bundle);
            }
        });
    }


}
