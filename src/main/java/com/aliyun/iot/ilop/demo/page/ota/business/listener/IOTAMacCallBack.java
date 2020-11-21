package com.aliyun.iot.ilop.demo.page.ota.business.listener;

public interface IOTAMacCallBack {

    void onSuccess(String mac);

    void onFailure(String msg);

}
