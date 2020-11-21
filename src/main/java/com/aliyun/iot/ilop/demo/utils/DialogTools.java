package com.aliyun.iot.ilop.demo.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class DialogTools extends Dialog {

    //    style引用style样式
    public DialogTools(Context context, int width, int height, View layout, int style) {
        super(context, style);
        setContentView(layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if(width==1 && height ==1){
            params.gravity = Gravity.BOTTOM;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            params.gravity = Gravity.CENTER;
        }
        window.setAttributes(params);
    }


}