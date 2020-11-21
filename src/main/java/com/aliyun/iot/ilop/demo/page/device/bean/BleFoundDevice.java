package com.aliyun.iot.ilop.demo.page.device.bean;

import com.alibaba.fastjson.JSON;
import com.aliyun.alink.linksdk.tmp.api.DeviceBasicData;

import java.util.Objects;

/**
 * 本地发现的蓝牙设备
 */
public class BleFoundDevice implements Cloneable{

    public String deviceName;
    public String productKey;
    public String modelType;
    public String desc;
    public String addr;
    public String deviceModelJson;
    public String iotId;
    public int port;


    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getDeviceModelJson() {
        return deviceModelJson;
    }

    public void setDeviceModelJson(String deviceModelJson) {
        this.deviceModelJson = deviceModelJson;
    }

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BleFoundDevice)) return false;
        BleFoundDevice that = (BleFoundDevice) o;
        return Objects.equals(deviceName, that.deviceName) &&
                Objects.equals(productKey, that.productKey);
    }

    @Override
    public BleFoundDevice clone() {
        try {
            return (BleFoundDevice) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }



    public void copyDeviceBasicData(DeviceBasicData deviceBasicData) {
        productKey = deviceBasicData.getProductKey();
        deviceName = deviceBasicData.getDeviceName();
        modelType = deviceBasicData.getModelType();
        desc = deviceBasicData.getDesc();
        addr = deviceBasicData.getAddr();
        deviceModelJson = deviceBasicData.getDeviceModelJson();
        iotId = deviceBasicData.getIotId();
        port = deviceBasicData.getPort();
    }
}
