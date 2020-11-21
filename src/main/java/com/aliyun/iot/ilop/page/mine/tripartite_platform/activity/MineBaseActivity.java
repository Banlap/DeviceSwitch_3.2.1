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

package com.aliyun.iot.ilop.page.mine.tripartite_platform.activity;

import android.os.Bundle;
import android.os.Handler;

import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.alink.linksdk.tools.ALog;

/**
 * Created by david on 2018/4/8.
 *
 * @author david
 * @date 2018/04/08
 */
public abstract class MineBaseActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();


    protected Handler mHandler;

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initEvent();

    protected abstract void initHandler();

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
        initHandler();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getPointerCount() > 1) {
            ALog.d(TAG, "Multitouch detected!");
            return true;
        }

        return super.dispatchTouchEvent(ev);
    }
}
