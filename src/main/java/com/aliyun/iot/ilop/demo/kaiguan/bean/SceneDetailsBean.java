package com.aliyun.iot.ilop.demo.kaiguan.bean;

import java.io.Serializable;
import java.util.List;

public class SceneDetailsBean implements Serializable {
    private String deviceNickName;
    private String productKey;
    private String iotId;
    private boolean ignoreRepeatedPropertyValue;

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public String getDeviceNickName() {
        return deviceNickName;
    }

    public void setDeviceNickName(String deviceNickName) {
        this.deviceNickName = deviceNickName;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public boolean isIgnoreRepeatedPropertyValue() {
        return ignoreRepeatedPropertyValue;
    }

    public void setIgnoreRepeatedPropertyValue(boolean ignoreRepeatedPropertyValue) {
        this.ignoreRepeatedPropertyValue = ignoreRepeatedPropertyValue;
    }
}
