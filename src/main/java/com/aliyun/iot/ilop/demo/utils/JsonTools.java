package com.aliyun.iot.ilop.demo.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.aliyun.iot.ilop.demo.page.bean.PalettesDialogBean;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsonTools {
    /**
     * 获取json
     * @param fileName
     * @param context
     * @return
     */
    public static String getJson(String fileName, Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
    public void set(){
//        Gson gson =new Gson();
//        PalettesDialogBean bean =gson.toJson()
    }
}
