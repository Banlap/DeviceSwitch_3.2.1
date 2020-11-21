package com.aliyun.iot.ilop.demo.page.device.bind;

import android.text.TextUtils;

import com.aliyun.alink.business.devicecenter.api.discovery.IOnDeviceTokenGetListener;
import com.aliyun.alink.business.devicecenter.api.discovery.LocalDeviceMgr;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iot.ilop.demo.page.device.bean.Device;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 设备绑定业务的封装
 * <br/>
 * 支持以下设备类型绑定: <br/>
 * 1. WiFi/以太网 类型
 * 2. GPRS 类型
 * 3. Zigbee 子设备入网（无需绑定）
 * 4. BLE 设备入网
 *
 * @author guikong on 18/4/8.
 */
public class DeviceBindBusiness {

    private static final String TAG = "DeviceBindBusiness";

    private static final int QUREY_STATUS_NONE = 0;
    private static final int QUREY_STATUS_DOING = 1;
    private static final int QUREY_STATUS_SUCCESS = 2;
    private static final int QUREY_STATUS_FAILED = 3;

    private int qureyStatus = QUREY_STATUS_NONE;

    private static final int BIND_STATUS_NONE = 10;
    private static final int BIND_STATUS_DOING = 11;
    private static final int BIND_STATUS_SUCCESS = 12;
    private static final int BIND_STATUS_FAILED = 13;
    private int bindStatus = BIND_STATUS_NONE;

    private Device mDevice;

    private String groupId;

    private OnBindDeviceCompletedListener onBindDeviceCompletedListener;


    public DeviceBindBusiness setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }


    /**
     * 查询产品信息
     */
    public void queryProductInfo(final Device device) {
        if (null == device) {
            throw new IllegalArgumentException("device can not be null");
        }
        qureyStatus = QUREY_STATUS_DOING;//正在查询
        IoTRequest request = new IoTRequestBuilder()
                .setPath("/thing/detailInfo/queryProductInfoByProductKey")
                .setApiVersion("1.1.1")
                .addParam("productKey", device.pk)
                .setAuthType("iotAuth")
                .build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, final Exception e) {
                qureyStatus = QUREY_STATUS_FAILED;//查询失败
                bindStatus = BIND_STATUS_FAILED;
                ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (onBindDeviceCompletedListener != null) {
                                onBindDeviceCompletedListener.onFailed(e);
                            }

                        } catch (Exception ex) {
                            ALog.e(TAG, "exception happen when call listener.onFailed", ex);
                            ex.printStackTrace();
                        }
                        onBindDeviceCompletedListener = null;
                    }
                });
            }

            @Override
            public void onResponse(final IoTRequest ioTRequest, final IoTResponse ioTResponse) {
                if (200 != ioTResponse.getCode() || !(ioTResponse.getData() instanceof JSONObject)) {
                    qureyStatus = QUREY_STATUS_FAILED;//查询失败
                    bindStatus = BIND_STATUS_FAILED;
                    ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (onBindDeviceCompletedListener != null) {
                                    onBindDeviceCompletedListener.onFailed(ioTResponse.getCode(), ioTResponse.getMessage(), ioTResponse.getLocalizedMsg());
                                }
                            } catch (Exception ex) {
                                ALog.e(TAG, "exception happen when call listener.onFailed", ex);
                                ex.printStackTrace();
                            }
                            onBindDeviceCompletedListener = null;
                        }
                    });
                    return;
                }
                String netType;

                JSONObject data = (JSONObject) ioTResponse.getData();
                netType = data.optString("netType");
                device.netType = netType;


                // WiFi and ethernet is same
                if ("NET_WIFI".equalsIgnoreCase(netType)
                        || "NET_ETHERNET".equalsIgnoreCase(netType)) {
                    bindWithWiFi(device);
                } else if ("NET_CELLULAR".equalsIgnoreCase(netType)
                        || "NET_ZIGBEE".equalsIgnoreCase(netType)
                        || "NET_OTHER".equalsIgnoreCase(netType)
                        || "NET_BT".equalsIgnoreCase(netType)) {
                    qureyStatus = QUREY_STATUS_SUCCESS;//查询成功
                    Device cloneDevice = new Device();
                    cloneDevice.pk = device.pk;
                    cloneDevice.dn = device.dn;
                    cloneDevice.netType = device.netType;
                    mDevice = cloneDevice;
                    if (bindStatus == BIND_STATUS_DOING) {//如果已经点击了绑定按钮
                        bindDeviceInternal(onBindDeviceCompletedListener);
                    }
                } else {
                    bindStatus = BIND_STATUS_FAILED;
                    ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (onBindDeviceCompletedListener != null) {
                                    onBindDeviceCompletedListener.onFailed(new IllegalArgumentException("unsupported net type"));
                                }
                            } catch (Exception ex) {
                                ALog.e(TAG, "exception happen when call listener.onFailed", ex);
                                ex.printStackTrace();
                            }
                            onBindDeviceCompletedListener = null;
                        }
                    });
                }


            }

        });
    }

    //绑定设备
    private void bindDeviceInternal(final OnBindDeviceCompletedListener listener) {
        String path = getPathByDevice(mDevice);
        if (TextUtils.isEmpty(path)) {
            listener.onFailed(new UnsupportedOperationException("ble bind is not support at present@" + mDevice.toString()));
        }
        Map<String, Object> maps = new HashMap<>();
        maps.put("productKey", mDevice.pk);
        maps.put("deviceName", mDevice.dn);
        if (!TextUtils.isEmpty(mDevice.token)) {
            maps.put("token", mDevice.token);
        }
        if (!TextUtils.isEmpty(groupId)) {
            List<String> groupIds = new ArrayList<>(1);
            groupIds.add(groupId);
            maps.put("groupIds", groupIds);
        }


        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath(path)
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, final Exception e) {
                ALog.d(TAG, "onFailure");
                bindStatus = BIND_STATUS_FAILED;
                ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listener.onFailed(e);
                        } catch (Exception ex) {
                            ALog.e(TAG, "exception happen when call listener.onFailed", ex);
                            ex.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, final IoTResponse ioTResponse) {
                ALog.d(TAG, "onResponse bindWithWiFi ok");

                if (200 != ioTResponse.getCode() || !(ioTResponse.getData() instanceof String)) {
                    bindStatus = BIND_STATUS_FAILED;
                    ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                listener.onFailed(ioTResponse.getCode(), ioTResponse.getMessage(), ioTResponse.getLocalizedMsg());
                            } catch (Exception ex) {
                                ALog.e(TAG, "exception happen when call listener.onFailed", ex);
                                ex.printStackTrace();
                            }
                        }
                    });
                    return;
                }


                final String iotId = (String) ioTResponse.getData();
                bindStatus = BIND_STATUS_SUCCESS;
                ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listener.onSuccess(iotId);
                        } catch (Exception ex) {
                            ALog.e(TAG, "exception happen when call listener.onSuccess", ex);
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });

    }


    public void bindDevice(Device device, final OnBindDeviceCompletedListener listener) {
        if (bindStatus == BIND_STATUS_DOING) {
            listener.onFailed(new IllegalStateException("bindStatus = BIND_STATUS_DOING"));
            return;
        }
        bindStatus = BIND_STATUS_DOING;
        if (qureyStatus == QUREY_STATUS_SUCCESS) {
            //产品信息查询已经完成
            bindDeviceInternal(listener);
        } else if (qureyStatus == QUREY_STATUS_DOING) {
            //正在查询
            onBindDeviceCompletedListener = listener;
        } else {//未查询或查询失败
            onBindDeviceCompletedListener = listener;
            queryProductInfo(device);
        }

    }


    private void bindWithWiFi(final Device device) {
        ALog.d(TAG, "bindWithWiFi");
        qureyStatus = QUREY_STATUS_DOING;//查询中。。。。
        final AtomicBoolean handled = new AtomicBoolean(false);
        LocalDeviceMgr.getInstance().getDeviceToken(AApplication.getInstance().getApplicationContext(), device.pk, device.dn, 60 * 1000, 2000, new IOnDeviceTokenGetListener() {
            @Override
            public void onSuccess(String token) {

                ALog.d(TAG, "getDeviceToken onSuccess token = " + token);
                if (handled.get()) {
                    return;
                }
                handled.set(true);

                qureyStatus = QUREY_STATUS_SUCCESS;//查询成功
                Device cloneDevice = new Device();
                cloneDevice.pk = device.pk;
                cloneDevice.dn = device.dn;
                cloneDevice.netType = device.netType;
                cloneDevice.token = token;
                mDevice = cloneDevice;
                if (bindStatus == BIND_STATUS_DOING) {
                    bindDeviceInternal(onBindDeviceCompletedListener);
                }
            }

            @Override
            public void onFail(final String s) {
                ALog.e(TAG, "getDeviceToken onFail s = " + s);
                qureyStatus = QUREY_STATUS_FAILED;//查询失败
                bindStatus = BIND_STATUS_FAILED;
                if (handled.get()) {
                    return;
                }

                handled.set(true);

                ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (onBindDeviceCompletedListener != null) {
                                onBindDeviceCompletedListener.onFailed(new RuntimeException(s));
                            }

                        } catch (Exception ex) {
                            ALog.e(TAG, "exception happen when call listener.onFailed", ex);
                            ex.printStackTrace();
                        }
                        onBindDeviceCompletedListener = null;
                    }
                });
            }
        });
    }


    private String getPathByDevice(Device device) {
        String netType = device.netType.toUpperCase();
        switch (netType) {
            case "NET_WIFI":
            case "NET_ETHERNET":
                return "/awss/enrollee/user/bind";
            case "NET_CELLULAR":
                return "/awss/gprs/user/bind";
            case "NET_ZIGBEE":
            case "NET_OTHER":
                return "/awss/subdevice/bind";
            case "NET_BT":
            default:
                return null;

        }


    }


}
