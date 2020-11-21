package com.aliyun.iot.ilop.demo.kaiguan.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

public class DialogUtil extends Dialog {

    public DialogUtil(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }



}
