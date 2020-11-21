package com.aliyun.iot.ilop.demo.page.ota.business.listener;

import com.aliyun.iot.ilop.demo.page.ota.bean.OTADeviceInfo;

/**
 * Created by david on 2018/4/13.
 *
 * @author david
 * @date 2018/04/13
 *
 * 查询ota状态callback
 */
public interface IOTAQueryStatusCallback {
    /**
     * 查询设备状态回调
     * @param deviceInfo
     */
    void onResponse(OTADeviceInfo deviceInfo);

    /**
     * 查询设备状态失败回调
     * @param msg 失败描述
     */
    void onFailure(String msg);
}
