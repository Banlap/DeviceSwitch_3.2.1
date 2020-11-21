package com.aliyun.iot.ilop.startpage.list.main.utils;

import android.os.Handler;
import android.os.Message;

import com.aliyun.iot.aep.sdk.IoTSmart;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.demo.R;

import org.jetbrains.annotations.NotNull;


public class LocateHandler extends Handler {
    @Override
    public void handleMessage(@NotNull Message msg) {
        if (locationListener == null) {
            return;
        }
        super.handleMessage(msg);
        switch (msg.what) {
            case 0:
                locationListener.onContinuedLocate(AApplication.getInstance().getResources().getString(R.string.locating));
                break;
            case 1:
                locationListener.onContinuedLocate(AApplication.getInstance().getResources().getString(R.string.locating) + ".");
                break;
            case 2:
                locationListener.onContinuedLocate(AApplication.getInstance().getResources().getString(R.string.locating) + "..");
                break;
            case 3:
                locationListener.onContinuedLocate(AApplication.getInstance().getResources().getString(R.string.locating) + "...");
                break;
            case 4:
                IoTSmart.Country countryBean = (IoTSmart.Country) msg.obj;
                locationListener.onSuccessLocate(countryBean);
                LocationUtil.cancelLocating();
                break;
            case 5:
                locationListener.onFailLocate();
                LocationUtil.cancelLocating();
                break;
        }
    }

    public interface OnLocationListener {
        void onContinuedLocate(String text);

        void onFailLocate();

        void onSuccessLocate(IoTSmart.Country countryBean);
    }

    private OnLocationListener locationListener;

    void setLocationListener(OnLocationListener locationListener) {
        this.locationListener = locationListener;
    }
}
