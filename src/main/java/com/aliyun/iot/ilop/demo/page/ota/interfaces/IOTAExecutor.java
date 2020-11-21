package com.aliyun.iot.ilop.demo.page.ota.interfaces;

import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAQueryStatusCallback;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAStartUpgradeCallback;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAStopUpgradeCallback;

/**
 * Created by david on 2018/4/13.
 *
 * @author david
 * @date 2018/04/13
 */
public interface IOTAExecutor {
    /**
     * 查询ota状态
     * @param iotId
     * @param callback
     */
    void queryOTAStatus(String iotId, IOTAQueryStatusCallback callback);

    /**
     * 开始ota升级
     * @param iotId
     * @param callback
     */
    void startUpgrade(String iotId, IOTAStartUpgradeCallback callback);

    /**
     * 停止ota升级
     * @param iotId
     * @param callback
     */
    void stopUpgrade(String iotId, IOTAStopUpgradeCallback callback);

    /**
     * 释放
     * destroy
     */
    void destroy();
}
