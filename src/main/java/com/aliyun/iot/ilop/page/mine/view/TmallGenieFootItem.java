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
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.iot.demo.R;


public class TmallGenieFootItem extends FrameLayout {

    private InstructionView ct1;
    private TextView titleTv;
    private TextView tvControl;

    public TmallGenieFootItem(@NonNull Context context) {
        super(context);
        init();
    }

    public TmallGenieFootItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TmallGenieFootItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.ilop_mine_tmall_genie_foot_item, this);
        ct1 = findViewById(R.id.mine_tm_foot_item_ct1);
        titleTv = findViewById(R.id.mine_tm_foot_item_title_tv);
        tvControl= findViewById(R.id.mine_tm_foot_item_controlled_device_tv);
    }

    public void setCt(String title, String text) {
        ct1.setContent(text);
        titleTv.setText(title);
    }


    public void setIFTTT(){
        tvControl.setText("");
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,20,getResources().getDisplayMetrics()));
        tvControl.setLayoutParams(layoutParams);
    }
}
