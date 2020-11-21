/*
 *   Copyright (c) 2020 Sinyuk
 *   All rights reserved.

 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.aliyun.iot.ilop.page.mine.view;

import android.content.Context;

import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.page.mine.MineConstants;

public class TmallGenieHeadItem extends FrameLayout {

    private String channel;
    private ImageView imageView;
    private TextView bindTv;
    private LinearLayout bindStepLl;
    private boolean isBindAccountFlag;

    public TmallGenieHeadItem(@NonNull Context context) {
        super(context);
        init();
    }

    public TmallGenieHeadItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TmallGenieHeadItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.ilop_mine_tmall_genie_head_item, this);
        imageView = findViewById(R.id.mine_tm_head_item_iv);
        bindTv = findViewById(R.id.mine_tm_head_item_binding_account_tv);
        bindStepLl = findViewById(R.id.mine_tm_head_item_binding_step_ll);
    }

    public void setImageResource(@DrawableRes int resId) {
        imageView.setImageResource(resId);
    }

    public void isBindAccountFlag(boolean isBindAccountFlag, CharSequence text) {
        this.isBindAccountFlag = isBindAccountFlag;
        bindTv.setText(text);
        if (isBindAccountFlag) {
            bindTv.setTextColor(ContextCompat.getColor(getContext(), R.color.mine_color_FF3838));
        } else {
            bindTv.setTextColor(ContextCompat.getColor(getContext(), R.color.mine_color_1FC88B));
        }
    }

    public void setChannel(String channel) {
        this.channel = channel;
        if (channel.equals(MineConstants.TM)) {
            bindTv.setVisibility(VISIBLE);
            bindStepLl.setVisibility(GONE);
        } else {
            bindStepLl.setVisibility(VISIBLE);
            bindTv.setVisibility(GONE);
        }
    }


    public void onBindClick(@Nullable OnClickListener l) {
        bindTv.setOnClickListener(l);
    }

    public void onBindStepClick(@Nullable OnClickListener l) {
        bindStepLl.setOnClickListener(l);
    }
}
