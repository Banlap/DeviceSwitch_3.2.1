package com.aliyun.iot.ilop.demo.page.ota.view;

import android.content.Context;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aliyun.iot.demo.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by david on 2018/4/4.
 *
 * @author david
 * @date 2018/04/04
 */
public class MineOTAListItem extends FrameLayout {
    private TextView mTitle;
    private ImageView mDeviceImg;
    private View mUnderLine;

    public MineOTAListItem(Context context) {
        super(context);
        init();
    }

    public MineOTAListItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MineOTAListItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.ilop_ota_list_item, this);

        mTitle = (TextView)findViewById(R.id.mine_setting_item_title_textview);
        mDeviceImg = (ImageView)findViewById(R.id.mine_setting_item_device_imageview);
        mUnderLine = (View)findViewById(R.id.mine_item_underline_view);
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        mTitle.setText(title);
    }

    public void showDeviceImage(String url) {
        if (null == mDeviceImg) {
            return;
        }

        RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.ilop_ota_device_icon_default);
        Glide.with(getContext())
            .load(url)
            .apply(options)
            .into(mDeviceImg);
    }

    public void showUnderLine(boolean shouldShow) {
        if (shouldShow) {
            mUnderLine.setVisibility(VISIBLE);
        } else {
            mUnderLine.setVisibility(GONE);
        }
    }
}
