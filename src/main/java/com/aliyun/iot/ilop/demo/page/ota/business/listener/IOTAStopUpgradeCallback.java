package com.aliyun.iot.ilop.demo.page.ota.business.listener;

/**
 * Created by david on 2018/4/13.
 *
 * @author david
 * @date 2018/04/13
 *
 * 停止ota回调（暂时没有暂停功能）
 */
public interface IOTAStopUpgradeCallback {
    /**
     * 暂停升级成功回调
     */
    void onSuccess();

    /**
     * 暂停升级失败回调
     * @param e
     */
    void onFailure(Exception e);
}
