package com.aliyun.iot.ilop.demo.view;

import android.content.Context;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.iot.demo.R;

public class LampsSwitchView extends FrameLayout {

    private TextView titleTv;
    private CircleView circleView;

    public LampsSwitchView(@NonNull Context context) {
        super(context);
    }

    public LampsSwitchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);

    }

    public LampsSwitchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        inflate(context, R.layout.lamps_switch_view, this);
        titleTv = findViewById(R.id.lamps_switch_title);
        circleView = findViewById(R.id.lamps_switch_circle);
    }

    public void setTitle(String title) {
        if (null == this.titleTv || TextUtils.isEmpty(title)) return;
        this.titleTv.setText(title);
    }

    public void setOnline(boolean flag) {
        Log.d("LampsSwitchView", "flag:" + flag);
        circleView.post(() -> {
            if (flag) {
                this.circleView.setVisibility(VISIBLE);
            } else {
                this.circleView.setVisibility(INVISIBLE);
            }
        });

    }
}
