package com.aliyun.iot.ilop.demo.kaiguan.bean;

public class DeviceInfoBean {
    private String   iotId;
    private String productKey;
    private String deviceName;

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
