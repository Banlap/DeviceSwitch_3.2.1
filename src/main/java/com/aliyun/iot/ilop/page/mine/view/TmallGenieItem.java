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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.iot.demo.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class TmallGenieItem extends FrameLayout {

    private TextView title;
    private ImageView imageView;
    private View lineView;
    private TextView controlledDeviceTv;
    private TextView noDeviceTv;
    private LinearLayout ctLl;

    public TmallGenieItem(@NonNull Context context) {
        super(context);
        init();
    }

    public TmallGenieItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TmallGenieItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.ilop_mine_tmall_genie_item, this);
        imageView = findViewById(R.id.mine_tp_device_item_iv);
        title = findViewById(R.id.mine_tp_device_item_tv);
        lineView = findViewById(R.id.mine_tp_device_item_line);
        controlledDeviceTv = findViewById(R.id.mine_tm_head_item_controlled_device_tv);
        noDeviceTv = findViewById(R.id.mine_tm_head_item_no_device_tv);
        ctLl = findViewById(R.id.mine_tm_head_item_device_ll);
    }

    public void isShowBlankTv(boolean flag) {
        if (noDeviceTv == null || ctLl == null) {
            return;
        }
        if (flag) {
            noDeviceTv.setVisibility(VISIBLE);
            ctLl.setVisibility(GONE);
            isShowLine(false);
        } else {
            noDeviceTv.setVisibility(GONE);
            ctLl.setVisibility(VISIBLE);
        }
    }

    public void isShowLine(boolean flag) {
        if (flag) {
            lineView.setVisibility(VISIBLE);
        } else {
            lineView.setVisibility(GONE);
        }
    }

    public void isShowBar(boolean flag) {
        if (flag) {
            controlledDeviceTv.setVisibility(VISIBLE);
        } else {
            controlledDeviceTv.setVisibility(GONE);
        }
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setImageUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Glide.with(this)
                .load(url)
                .into(imageView);
    }
}
