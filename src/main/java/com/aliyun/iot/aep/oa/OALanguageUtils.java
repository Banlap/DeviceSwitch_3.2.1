package com.aliyun.iot.aep.oa;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.openaccount.ConfigManager;
import com.alibaba.sdk.android.openaccount.config.LanguageCode;
import com.aliyun.iot.aep.sdk.IoTSmart;

import java.util.Locale;

/**
 * Created by feijie.xfj on 18/5/22.
 */

public class OALanguageUtils {
    public static Context attachBaseContext(Context newBase) {
        setLanguage(newBase);
        return newBase;
    }

    private static void setLanguage(@SuppressWarnings("unused") Context context) {
        String language = IoTSmart.getLanguage();
        Log.d("sinyuk", "setLanguage: " + language);
        if (language.equals("zh-CN")) {
            ConfigManager.getInstance().setLanguageCode(LanguageCode.CHINESE);
        } else {
            ConfigManager.getInstance().setLanguageCode(LanguageCode.ENGLISH);
        }
        if ("zh-CN".equalsIgnoreCase(language)) {
            OALanguageHelper.setLanguageCode(Locale.SIMPLIFIED_CHINESE);
        } else if ("en-US".equalsIgnoreCase(language)) {
            OALanguageHelper.setLanguageCode(Locale.US);
        } else if ("fr-FR".equalsIgnoreCase(language)) {
            OALanguageHelper.setLanguageCode(Locale.FRANCE);
        } else if ("de-DE".equalsIgnoreCase(language)) {
            OALanguageHelper.setLanguageCode(Locale.GERMANY);
        } else if ("ja-JP".equalsIgnoreCase(language)) {
            OALanguageHelper.setLanguageCode(Locale.JAPAN);
        } else if ("ko-KR".equalsIgnoreCase(language)) {
            OALanguageHelper.setLanguageCode(Locale.KOREA);
        } else if ("es-ES".equalsIgnoreCase(language)) {
            OALanguageHelper.setLanguageCode(new Locale("es", "ES"));
        } else if ("ru-RU".equalsIgnoreCase(language)) {
            OALanguageHelper.setLanguageCode(new Locale("ru", "RU"));
        } else {
            OALanguageHelper.setLanguageCode(Locale.US);
        }
    }

}
