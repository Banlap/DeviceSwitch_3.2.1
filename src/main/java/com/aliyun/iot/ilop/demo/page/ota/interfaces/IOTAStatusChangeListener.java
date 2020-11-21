package com.aliyun.iot.ilop.demo.page.ota.interfaces;

import com.aliyun.iot.ilop.demo.page.ota.bean.OTAStatusInfo;

/**
 * Created by david on 2018/5/2.
 *
 * @author david
 * @date 2018/05/02
 *
 * OTA 状态变更监听
 */
public interface IOTAStatusChangeListener {
    /**
     * ota状态变更回调
     * @param info
     */
    void onStatusChange(OTAStatusInfo info);
}
