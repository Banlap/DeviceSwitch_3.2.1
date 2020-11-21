package com.aliyun.iot.ilop.demo.page.ota.business.listener;

/**
 * Created by david on 2018/4/13.
 *
 * @author david
 * @date 2018/04/13
 *
 * 开始升级callback
 */
public interface IOTAStartUpgradeCallback {
    /**
     * 请求升级成功回调
     */
    void onSuccess();

    /**
     * 请求升级失败回调
     * @param msg 失败描述
     */
    void onFailure(String msg);
}
