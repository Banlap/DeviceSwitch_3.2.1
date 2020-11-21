package com.aliyun.iot.ilop.startpage.list.main.utils;

import android.content.Context;
import android.location.Address;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.aliyun.iot.aep.sdk.IoTSmart;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class LocateTask {
    public static final String TAG = "LocateTask";
    private boolean start = true;
    private Timer timer = new Timer();
    private LocateHandler locateHandler = new LocateHandler();
    private int ellipsis_num = 0;
    private CopyOnWriteArrayList<IoTSmart.Country> countryList = new CopyOnWriteArrayList<>();
    private Context context;

    public LocateTask(@NonNull Context context, List<IoTSmart.Country> countries, LocateHandler.OnLocationListener locationListener) {
        this.context = context;
        if (countries != null) {
            this.countryList.addAll(countries);
        }
        this.locateHandler.setLocationListener(locationListener);
    }


    //核对是否可以获取定位国家信息
    private IoTSmart.Country getCountryInfo(String code, String name) {
        if (!countryList.isEmpty()) {
            for (IoTSmart.Country bean : countryList) {
                if (Objects.equals(code, bean.code) || Objects.equals(code, bean.domainAbbreviation) || Objects.equals(name, bean.areaName)) {
                    return bean;
                }
            }
        }
        return null;
    }

    public synchronized void startLocation() {
        ThreadPool.DefaultThreadPool.getInstance().submit(() -> {
            while (start) {
                //获取国家码
                Address address = LocationUtil.getCountryCode(context.getApplicationContext());
                if (address != null) {
                    IoTSmart.Country hitCountry = getCountryInfo(address.getCountryCode(), address.getCountryName());
                    Log.d(TAG, "<====" + address.getCountryCode() + ", " + address.getCountryName() + " =====>");
                    //通过国家码获取国家信息
                    if (hitCountry != null) {
                        Message message = new Message();
                        message.obj = hitCountry;
                        message.what = 4;
                        locateHandler.sendMessage(message);
                        start = false;
                        //停止计时
                        timer.cancel();
                        timer = null;
                    } else {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (start) {
                            Message message = new Message();
                            message.what = ellipsis_num % 4;
                            ellipsis_num++;
                            locateHandler.sendMessage(message);
                        }
                    }
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (start) {
                        Message message = new Message();
                        message.what = ellipsis_num % 4;
                        ellipsis_num++;
                        locateHandler.sendMessage(message);
                    }
                }
            }
        });

        //开始计时
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                start = false;
                Message message = new Message();
                message.what = 5;
                locateHandler.sendMessage(message);
            }
        }, 30 * 1000);
    }

    public synchronized void stopLocation() {
        start = false;
        if (timer != null) {
            timer.cancel();
        }
        if (locateHandler != null) {
            locateHandler.removeCallbacksAndMessages(null);
        }
    }
}
