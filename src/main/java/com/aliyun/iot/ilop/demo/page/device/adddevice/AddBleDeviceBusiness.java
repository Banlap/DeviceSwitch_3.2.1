package com.aliyun.iot.ilop.demo.page.device.adddevice;

import android.os.Handler;
import android.os.Message;

import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.ilop.demo.page.device.bean.BleFoundDevice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AddBleDeviceBusiness {


    public static final int MESSAGE_RESPONSE_FILTERDEVICE = 0x31001;
    private List<BleFoundDevice> localFoundDevice;


    private Handler handler;

    public AddBleDeviceBusiness(Handler handler) {
        this.handler = handler;
        localFoundDevice = new ArrayList<>();
    }

    public void reset() {
        localFoundDevice.clear();
    }

    /**
     * 过滤
     *
     * @param bleFoundDevices
     */
    public void filterDevice(List<BleFoundDevice> bleFoundDevices) {
        //如果设备已经过滤过或正在过滤中，不在重复执行过滤操作
        Iterator<BleFoundDevice> iterator = bleFoundDevices.iterator();
        while (iterator.hasNext()) {
            BleFoundDevice bleFoundDevice = iterator.next();
            if (localFoundDevice.contains(bleFoundDevice)) {
                iterator.remove();
            }
        }
        localFoundDevice.addAll(bleFoundDevices);
        if (bleFoundDevices.size() == 0) {
            return;
        }
        List<Map<String, String>> devices = new ArrayList<>();

        for (BleFoundDevice bleFoundDevice : bleFoundDevices) {
            Map<String, String> device = new HashMap<>(2);
            device.put("productKey", bleFoundDevice.productKey);
            device.put("deviceName", bleFoundDevice.deviceName);
            devices.add(device);
        }
        IoTRequest request = new IoTRequestBuilder()
                .setPath("/awss/enrollee/product/filter")
                .setApiVersion("1.0.2")
                .addParam("iotDevices", devices)
                .setAuthType("iotAuth")
                .build();

        new IoTAPIClientFactory().getClient().send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                localFoundDevice.removeAll(bleFoundDevices);
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                if (200 != ioTResponse.getCode()) {
                    localFoundDevice.removeAll(bleFoundDevices);
                    return;
                }

                if (!(ioTResponse.getData() instanceof JSONArray)) {
                    localFoundDevice.removeAll(bleFoundDevices);
                    return;
                }

                JSONArray items = (JSONArray) ioTResponse.getData();
                //有返回数据，表示服务端支持此pk，dn
                if (null != items) {
                    ALog.d("JC", "有返回数据，表示服务端支持此pk，dn");
                    List<BleFoundDevice> deviceListItems = paraseFilterDevice(items);//过滤后可以配网绑定的设备
                    Iterator<BleFoundDevice> iterator1 = bleFoundDevices.iterator();
                    while (iterator1.hasNext()) {//只保留过滤后的设备
                        BleFoundDevice bleFoundDevice = iterator1.next();
                        if (!deviceListItems.contains(bleFoundDevice)) {
                            iterator1.remove();
                        }
                    }

                    //将过滤后的设备展示出来
                    Message.obtain(handler, MESSAGE_RESPONSE_FILTERDEVICE, bleFoundDevices).sendToTarget();
                } else {
                    localFoundDevice.removeAll(bleFoundDevices);
                }
            }
        });
    }


    private List<BleFoundDevice> paraseFilterDevice(JSONArray jsonArray) {
        List<BleFoundDevice> foundDeviceListItems = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                BleFoundDevice device = new BleFoundDevice();
                if (jsonObject.has("deviceName"))
                    device.deviceName = jsonObject.getString("deviceName");
                if (jsonObject.has("productKey"))
                    device.productKey = jsonObject.getString("productKey");
                foundDeviceListItems.add(device);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return foundDeviceListItems;
    }


}
