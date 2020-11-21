package com.aliyun.iot.ilop.demo.view;

import android.content.Context;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.bean.DeviceInfoBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;

public class DevicePanelView extends RelativeLayout {

    TextView mNameTv;
    TextView mTypeTv;
    TextView mStatusTv;
    ImageView mImageView;

    public DevicePanelView(Context context) {
        this(context, null);
    }

    public DevicePanelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.device_panel_layout, this);
        mNameTv = findViewById(R.id.device_panel_name);
        mTypeTv = findViewById(R.id.device_panel_type);
        mStatusTv = findViewById(R.id.device_panel_status);
        mImageView = findViewById(R.id.iv_image);

    }

    public void setName(String name) {
        mNameTv.setText(name);
    }

    public void setType(String type) {
        mTypeTv.setText(type);
    }

    public void setStatus(String status) {
        mStatusTv.setText(status);
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        mNameTv.setAlpha(alpha);
        mTypeTv.setAlpha(alpha);
        mStatusTv.setAlpha(alpha);
    }

    public void setDeviceInfo(DeviceInfoBean device) {
        if (!TextUtils.isEmpty(device.getProductName())) {
            setName(device.getProductName());
        } else {
            setName(device.getDeviceName());
        }

        if ("VIRTUAL".equalsIgnoreCase(device.getThingType()) || "VIRTUAL_SHADOW".equalsIgnoreCase(device.getThingType())) {
            setType("虚拟");
        } else {
            setType(device.getNetType());
        }
        String statusStr = "离线";
        if (1 == device.getStatus()) {
            statusStr = "在线";
        } else {
            setAlpha(0.8f);
        }
        setStatus(statusStr);

        Glide.with(getContext())
                .load(device.getCategoryImage())
                .into(mImageView);

    }

}
