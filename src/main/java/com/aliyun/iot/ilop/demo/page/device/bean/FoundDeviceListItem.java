package com.aliyun.iot.ilop.demo.page.device.bean;

import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo;
import com.aliyun.alink.business.devicecenter.api.discovery.DiscoveryType;

/**
 * 本地发现的wifi，网关等设备
 * Created by jiangchao on 18-1-25.
 */

public class FoundDeviceListItem extends FoundDevice {
    public static final String NEED_CONNECT = "need_connect";
    public static final String NEED_BIND = "need_bind";
    public DeviceInfo deviceInfo;
    public String deviceStatus;

    public String productName;
    

    public DiscoveryType discoveryType;

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public DiscoveryType getDiscoveryType() {
        return discoveryType;
    }

    public void setDiscoveryType(DiscoveryType discoveryType) {
        this.discoveryType = discoveryType;
    }
}
