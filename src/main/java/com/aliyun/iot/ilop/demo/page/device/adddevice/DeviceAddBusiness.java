package com.aliyun.iot.ilop.demo.page.device.adddevice;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.ilop.demo.page.device.bean.FoundDevice;
import com.aliyun.iot.ilop.demo.page.device.bean.FoundDeviceListItem;
import com.aliyun.iot.ilop.demo.page.device.bean.SupportDeviceListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DeviceAddBusiness {


    public static final int MESSAGE_RESPONSE_SUPPORTDEVICE_SUCCESS = 0x11001;
    public static final int MESSAGE_RESPONSE_SUPPORTDEVICE_FAILED = 0x21001;

    public static final int MESSAGE_RESPONSE_FILTERDEVICE = 0x31001;
    private static String TAG = "DeviceAddBusiness";

    private List<FoundDevice> localFoundDevice;


    private Handler handler;

    public DeviceAddBusiness(Handler handler) {
        this.handler = handler;
        localFoundDevice = new ArrayList<>();
    }


    public void reset() {
        localFoundDevice.clear();
    }

    /**
     * 获取支持添加的设备列表
     */
    public void getSupportDeviceListFromSever() {
        Map<String, Object> maps = new HashMap<>();
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/thing/productInfo/getByAppKey")
                .setApiVersion("1.1.1")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                if (handler == null) {
                    return;
                }
                Message.obtain(handler, MESSAGE_RESPONSE_SUPPORTDEVICE_FAILED).sendToTarget();
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                if (handler == null) {
                    return;
                }

                final int code = ioTResponse.getCode();
                final String msg = ioTResponse.getMessage();
                if (code != 200) {
                    Message.obtain(handler, MESSAGE_RESPONSE_SUPPORTDEVICE_FAILED, msg).sendToTarget();
                    return;
                }

                ArrayList<SupportDeviceListItem> mSupportDeviceListItems = new ArrayList<>();
                Object data = ioTResponse.getData();
                if (null != data) {
                    if (data instanceof JSONArray) {
                        mSupportDeviceListItems = parseSupportDeviceListFromSever((JSONArray) data);
                    }
                }

                Message.obtain(handler, MESSAGE_RESPONSE_SUPPORTDEVICE_SUCCESS, mSupportDeviceListItems).sendToTarget();

            }
        });
    }


    private ArrayList<SupportDeviceListItem> parseSupportDeviceListFromSever(JSONArray jsonArray) {
        ArrayList<SupportDeviceListItem> arrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                SupportDeviceListItem device = new SupportDeviceListItem();
                device.deviceName = jsonObject.getString("name");
                device.productKey = jsonObject.getString("productKey");
                arrayList.add(device);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }


    /**
     * 过滤
     *
     * @param foundDeviceListItems
     */
    public void filterDevice(List<FoundDeviceListItem> foundDeviceListItems) {
        ALog.d(TAG, "--find Data--" + JSON.toJSONString(localFoundDevice));
        //如果设备已经过滤过或正在过滤中，不在重复执行过滤操作
        Iterator<FoundDeviceListItem> iterator = foundDeviceListItems.iterator();
        while (iterator.hasNext()) {
            FoundDeviceListItem foundDeviceListItem = iterator.next();
            if (localFoundDevice.contains(foundDeviceListItem)) {
                iterator.remove();
            }
        }
        localFoundDevice.addAll(localFoundDevice);
        ALog.d(TAG, "--Data--" + JSON.toJSONString(localFoundDevice));
        List<Map<String, String>> devices = new ArrayList<>();

        for (FoundDeviceListItem deviceListItem : foundDeviceListItems) {
            Map<String, String> device = new HashMap<>(2);
            device.put("productKey", deviceListItem.productKey);
            device.put("deviceName", deviceListItem.deviceName);
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
                localFoundDevice.removeAll(foundDeviceListItems);
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                if (200 != ioTResponse.getCode()) {
                    localFoundDevice.removeAll(foundDeviceListItems);
                    return;
                }

                if (!(ioTResponse.getData() instanceof JSONArray)) {
                    localFoundDevice.removeAll(foundDeviceListItems);
                    return;
                }

                JSONArray items = (JSONArray) ioTResponse.getData();
                //有返回数据，表示服务端支持此pk，dn
                if (null != items && items.length() > 0) {
                    ALog.d("JC", "有返回数据，表示服务端支持此pk，dn");
                    List<FoundDeviceListItem> deviceListItems = parseFilterDevice(items); //过滤后可以配网绑定的设备
                    Iterator<FoundDeviceListItem> iterator1 = foundDeviceListItems.iterator();
                    while (iterator1.hasNext()) {//只保留过滤后的设备
                        FoundDeviceListItem foundDeviceListItem = iterator1.next();
                        if (!deviceListItems.contains(foundDeviceListItem)) {
                            iterator1.remove();
                        } else {
                            FoundDeviceListItem sameItem = deviceListItems.get(deviceListItems.indexOf(foundDeviceListItem));
                            foundDeviceListItem.deviceName = sameItem.deviceName;
                            foundDeviceListItem.productName = sameItem.productName;
                        }
                    }
                    //将过滤后的设备展示出来
                    Message.obtain(handler, MESSAGE_RESPONSE_FILTERDEVICE, foundDeviceListItems).sendToTarget();
                } else {
                    localFoundDevice.removeAll(foundDeviceListItems);
                }
            }
        });
    }


    private List<FoundDeviceListItem> parseFilterDevice(JSONArray jsonArray) {
        List<FoundDeviceListItem> foundDeviceListItems = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                FoundDeviceListItem device = new FoundDeviceListItem();
                if (jsonObject.has("deviceName"))
                    device.deviceName = jsonObject.getString("deviceName");
                else ALog.w(TAG, "no value passing for deviceName");
                if (jsonObject.has("productKey"))
                    device.productKey = jsonObject.getString("productKey");
                else ALog.w(TAG, "no value passing for deviceName");
                if (jsonObject.has("productName"))
                    device.productName = jsonObject.getString("productName");
                foundDeviceListItems.add(device);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return foundDeviceListItems;
    }
}
