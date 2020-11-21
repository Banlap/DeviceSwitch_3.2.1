package com.aliyun.iot.ilop.demo.utils;

import android.util.Log;

/**
 * 输出日志工具
 * Created by 15696 on 2017/12/9.
 */

public class LogUtil {
    private String TAG;
    private boolean isRelease = false;
    private boolean isDebug;

    public LogUtil(Class c, boolean isDebug) {
        this.TAG = c.getName();
        this.isDebug = isDebug;
    }

    public void d(String msg) {
        if (!isRelease && isDebug) {
            Log.d(TAG, "--------->" + msg);
        }
    }

    public void i(String msg) {
        if (!isRelease && isDebug) {
            Log.i(TAG, "--------->" + msg);
        }
    }

    public void w(String msg) {
        if (!isRelease && isDebug) {
            Log.w(TAG, "--------->" + msg);
        }
    }

    public void e(String msg) {
        if (!isRelease && isDebug) {
            Log.e(TAG, "--------->" + msg);
        }
    }
}
