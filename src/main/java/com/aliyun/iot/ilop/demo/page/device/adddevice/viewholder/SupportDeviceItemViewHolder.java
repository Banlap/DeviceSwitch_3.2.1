package com.aliyun.iot.ilop.demo.page.device.adddevice.viewholder;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.adapter.BaseViewHolder;
import com.aliyun.iot.ilop.demo.page.device.adddevice.AddDeviceActivity;
import com.aliyun.iot.ilop.demo.page.device.bean.FoundDevice;

/**
 * 支持添加的设备
 */
public class SupportDeviceItemViewHolder extends BaseViewHolder<FoundDevice>{


    private static String CODE = "link://router/connectConfig";


    private   ImageView iv_device_icon;
    private   TextView tv_device_name;
    private   Button btn_device_connect;


    public SupportDeviceItemViewHolder(View itemView) {
        super(itemView);
         iv_device_icon = (ImageView) itemView.findViewById(R.id.list_item_device_icon);
         tv_device_name = (TextView) itemView.findViewById(R.id.list_item_device_name);
         btn_device_connect = (Button) itemView.findViewById(R.id.list_item_device_action);


    }


    @Override
    public void onBind(FoundDevice data, int position) {
        super.onBind(data, position);

        tv_device_name.setText(data.deviceName);
        btn_device_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = CODE;
                Bundle bundle = new Bundle();
                bundle.putString("productKey", data.productKey);
                Router.getInstance().toUrlForResult((Activity) view.getContext(), code, 1, bundle);
            }
        });
    }
}





