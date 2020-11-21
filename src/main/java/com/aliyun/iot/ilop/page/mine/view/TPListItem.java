package com.aliyun.iot.ilop.page.mine.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.iot.demo.R;


public class TPListItem extends FrameLayout {

    private ImageView imageView;

    public TPListItem(@NonNull Context context) {
        super(context);
        init();
    }

    public TPListItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TPListItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.ilop_mine_tp_list_item, this);
        imageView = findViewById(R.id.mine_to_list_item_iv);

    }

    public void setImageView(@DrawableRes int resId) {
        imageView.setImageResource(resId);
    }
}
