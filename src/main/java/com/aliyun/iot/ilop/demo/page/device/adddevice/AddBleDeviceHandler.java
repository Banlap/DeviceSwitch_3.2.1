package com.aliyun.iot.ilop.demo.page.device.adddevice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.aliyun.iot.ilop.demo.page.device.bean.BleFoundDevice;

import java.util.List;

public class AddBleDeviceHandler extends Handler {

    private AddBleDeviceBusiness addBleDeviceBusiness;
    private OnBleDeviceAddListener onBleDeviceAddListener;

    public AddBleDeviceHandler(OnBleDeviceAddListener onBleDeviceAddListener) {
        super(Looper.getMainLooper());
        addBleDeviceBusiness = new AddBleDeviceBusiness(this);
        this.onBleDeviceAddListener = onBleDeviceAddListener;
    }
    public void reset(){
        addBleDeviceBusiness.reset();
    }

    public void addBleDevices(List<BleFoundDevice> localDevices) {
        addBleDeviceBusiness.filterDevice(localDevices);
    }


    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (onBleDeviceAddListener == null) {
            return;
        }
        if (msg.what == AddBleDeviceBusiness.MESSAGE_RESPONSE_FILTERDEVICE) {
            List<BleFoundDevice> deviceListItems= (List<BleFoundDevice>) msg.obj;
            onBleDeviceAddListener.onBleDeviceFilterSuccess(deviceListItems);
        }

    }


    public void onDestory() {
        removeCallbacksAndMessages(null);
        onBleDeviceAddListener = null;
    }


}
