package com.aliyun.iot.ilop.demo.kaiguan.bean;

import java.io.Serializable;

public class FacilityListBean implements Serializable {
    private String productName;
    private String productKey;
    private Integer status;
    private boolean isEdgeGateway;
    private String iotId;
    private String deviceName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean isEdgeGateway() {
        return isEdgeGateway;
    }

    public void setEdgeGateway(boolean edgeGateway) {
        isEdgeGateway = edgeGateway;
    }
}
