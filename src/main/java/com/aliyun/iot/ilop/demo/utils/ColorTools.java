package com.aliyun.iot.ilop.demo.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.aliyun.iot.ilop.demo.page.bean.PalettesDialogBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class ColorTools {

    public static float[] RGBtoHSV(String colorStr) {
        int color = Color.parseColor("#" + colorStr);
        Log.d("ColorTools", "n:" + color);
        int r = (color & 0xff0000) >> 16;
        int g = (color & 0x00ff00) >> 8;
        int b = (color & 0x0000ff);
        Log.d("ColorTools", "r:" + r);
        Log.d("ColorTools", "g:" + g);
        Log.d("ColorTools", "b:" + b);
        float[] hsv = new float[3];
        Color.RGBToHSV(r, g, b, hsv);
        Log.d("ColorTools", "h:" + hsv[0]);
        Log.d("ColorTools", "s:" + hsv[1]);
        Log.d("ColorTools", "v:" + hsv[2]);
        Log.d("ColorTools", hsv.length + "");
        return hsv;
    }

    public static List<PalettesDialogBean> getColorData(Context context) {
        String s = JsonTools.getJson("lamps_hsv.json", context);
        Type type = new TypeToken<List<PalettesDialogBean>>() {
        }.getType();
        List<PalettesDialogBean> arr = new Gson().fromJson(s, type);
        return arr;
    }

}

