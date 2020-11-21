package com.aliyun.iot.ilop.demo.page.ota.executor;

import android.bluetooth.BluetoothProfile;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.breeze.api.IBreeze;
import com.aliyun.iot.breeze.api.IBreezeDevice;
import com.aliyun.iot.breeze.ota.api.Factory;
import com.aliyun.iot.breeze.ota.api.ILinkOTABusiness;
import com.aliyun.iot.ilop.demo.page.ota.OTAConstants;
import com.aliyun.iot.ilop.demo.page.ota.bean.OTADeviceInfo;
import com.aliyun.iot.ilop.demo.page.ota.bean.OTAStatusInfo;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAConnectCallBack;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAMacCallBack;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAQueryStatusCallback;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAStartUpgradeCallback;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAStopUpgradeCallback;
import com.aliyun.iot.ilop.demo.page.ota.interfaces.IOTAExecutor;
import com.aliyun.iot.ilop.demo.page.ota.interfaces.IOTAStatusChangeListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 2018/4/13.
 *
 * @author david
 * @date 2018/04/13
 * <p>
 * 蓝牙 ota
 */
public class BreezeOTAExecutor implements IOTAExecutor, IOTAMacCallBack {

    private static final String TAG = "BreezeOTAExecutor";
    private static final String desc = "breeze_ble";
    private IoTAPIClient mIoTAPIClient;
    private IOTAStatusChangeListener mStatusListener;
    private ILinkOTABusiness otaBusiness;
    private IBreeze breeze;
    private boolean isConnection = false;
    private String mac;
    private int isFirst = 0;
    //状态对象
    private OTAStatusInfo info;

    public BreezeOTAExecutor(IOTAStatusChangeListener listener) {
        //状态监听
        this.mStatusListener = listener;
        info = new OTAStatusInfo();
        info.desc = desc;
        //蓝牙OTA初始化
        breeze = com.aliyun.iot.breeze.api.Factory.createBreeze(AApplication.getInstance());
        IoTAPIClientFactory factory = new IoTAPIClientFactory();
        mIoTAPIClient = factory.getClient();
    }

    /**
     * @param iotId
     * @param callback
     */
    @Override
    public void queryOTAStatus(String iotId, final IOTAQueryStatusCallback callback) {
        if (null == callback) {
            return;
        }
        //获取蓝牙MAC地址
        receiveMac(iotId, this);
        //获取设备信息
        receiveDeviceUpdateInfo(iotId, callback);
    }


    @Override
    public void startUpgrade(final String iotId, final IOTAStartUpgradeCallback callback) {
        if (TextUtils.isEmpty(mac)) {
            return;
        }
        if (callback == null) {
            return;
        }
        //判断连接
        connect(mac, new IOTAConnectCallBack() {
            @Override
            public void onConnect(boolean isConn) {
                info.iotId = iotId;
                if (isConn) {
                    //更新
                    info.upgradeStatus = "5";
                    update(iotId, callback);
                } else {
                    info.upgradeStatus = "6";
                }
                mStatusListener.onStatusChange(info);
            }

            @Override
            public void isLoading() {
                info.iotId = iotId;
                info.upgradeStatus = "7";
                mStatusListener.onStatusChange(info);
            }
        });

    }

    /**
     * 蓝牙连接并判断第一次连接
     *
     * @param mac
     * @param connectCallBack
     */
    private void connect(String mac, final IOTAConnectCallBack connectCallBack) {
        connectCallBack.isLoading();
        //打开并连接
        breeze.open(false, mac, new IBreeze.ConnectionCallback() {
            @Override
            public void onConnectionStateChange(IBreezeDevice device, int state, int status) {
                ALog.d(TAG, "蓝牙status：" + state);
                if (isFirst == 0 && state == BluetoothProfile.STATE_CONNECTED) {
                    ALog.d(TAG, "蓝牙连接成功");
                    otaBusiness = Factory.create(device);
                    otaBusiness.init();
                    connectCallBack.onConnect(true);
                    isFirst = 1;
                } else if (state == BluetoothProfile.STATE_CONNECTING) {
                    ALog.d(TAG, "蓝牙正在连接");
                } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                    ALog.d(TAG, "蓝牙连接失败");
                    if (isFirst == 0)
                        connectCallBack.onConnect(false);
                } else if (state == BluetoothProfile.STATE_DISCONNECTING) {
                    ALog.d(TAG, "蓝牙连接断开");
                }
            }
        });
    }


    @Override
    public void stopUpgrade(String iotId, IOTAStopUpgradeCallback callback) {
        if (isConnection)
            otaBusiness.stopUpgrade();
    }

    @Override
    public void destroy() {
        if (isConnection)
            otaBusiness.deInit();
    }

    private IoTRequestBuilder getBaseIoTRequestBuilder() {
        IoTRequestBuilder builder = new IoTRequestBuilder();
        builder.setAuthType(OTAConstants.APICLIENT_IOTAUTH)
                .setApiVersion(OTAConstants.APICLIENT_VERSION);
        return builder;
    }

    @Override
    public void onSuccess(String mac) {
        this.mac = mac;
    }

    @Override
    public void onFailure(String msg) {
        ALog.e(TAG, "mac错误返回：" + msg);
    }

    /**
     * 更新过程
     *
     * @param iotId
     * @param callback
     */
    private void update(final String iotId, final IOTAStartUpgradeCallback callback) {
        otaBusiness.startUpgrade(iotId, false, ILinkOTABusiness.DEVICE_TYPE_BLE, new ILinkOTABusiness.IOtaListener() {
            @Override
            public void onNotification(int i, ILinkOTABusiness.IOtaError iOtaError) {
                // 升级状态 0：待升级/待确认， 1：升级中， 2：升级异常， 3：升级失败， 4：升级成功
                OTAStatusInfo info = new OTAStatusInfo();
                info.iotId = iotId;
                switch (i) {
                    case TYPE_DOWNLOAD:        //下载
                        ALog.d(TAG, "正在下载");
                        if (iOtaError.getCode() == 0) {
                            info.upgradeStatus = "1";
                            ALog.d(TAG, "下载成功");
                        } else {
                            info.upgradeStatus = "3";
                            ALog.d(TAG, "下载失败：" + iOtaError.getCode());
                        }
                        mStatusListener.onStatusChange(info);
                        break;
                    case TYPE_TRANSMIT:        //传输
                        ALog.d(TAG, "正在传输");
                        if (iOtaError.getCode() == 0) {
                            info.upgradeStatus = "1";
                            ALog.d(TAG, "传输成功");
                        } else {
                            info.upgradeStatus = "3";
                            ALog.d(TAG, "传输失败：" + iOtaError.getCode());
                        }
                        mStatusListener.onStatusChange(info);
                        break;
                    case TYPE_REBOOT:        //重启
                        ALog.d(TAG, "正在重启");
                        if (iOtaError.getCode() == 0) {
                            ALog.d(TAG, "重启成功");
                            info.upgradeStatus = "1";
                        } else {
                            info.upgradeStatus = "2";
                            ALog.d(TAG, "重启失败：" + iOtaError.getCode());
                        }
                        mStatusListener.onStatusChange(info);
                        break;
                    case TYPE_FINISH:        //完成
                        ALog.d(TAG, "升级完成");
                        if (iOtaError.getCode() == 0) {
                            ALog.d(TAG, "升级完成");
                            callback.onSuccess();
                        } else {
                            info.upgradeStatus = "2";
                            ALog.d(TAG, "升级失败" + iOtaError.getCode());
                            callback.onFailure("升级失败" + iOtaError.getCode());
                            mStatusListener.onStatusChange(info);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }


    /**
     * 获取mac地址
     *
     * @param iotId
     * @param macCallBack
     */
    private void receiveMac(String iotId, final IOTAMacCallBack macCallBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("iotId", iotId);
        map.put("dataKey", "MAC");
        IoTRequest request = getBaseIoTRequestBuilder()
                .setPath(OTAConstants.APICLIENT_PATH_MAC)
                .setParams(map)
                .build();
        if (macCallBack != null) {
            mIoTAPIClient.send(request, new IoTCallback() {
                @Override
                public void onFailure(IoTRequest ioTRequest, Exception e) {
                    ALog.e(TAG, "request path:" + ioTRequest.getPath() + " error", e);
                    macCallBack.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                    ALog.d(TAG, "path:" + ioTRequest.getPath() + ", response:" + ioTResponse.getData().toString());
                    if (ioTResponse.getCode() != 200) {
                        macCallBack.onFailure(ioTResponse.getLocalizedMsg());
                        return;
                    }
                    String mac = ioTResponse.getData().toString();
                    macCallBack.onSuccess(mac);
                }
            });
        }

    }

    /**
     * 获取设备升级信息
     *
     * @param iotId
     * @param callback
     */
    private void receiveDeviceUpdateInfo(String iotId, final IOTAQueryStatusCallback callback) {
        JSONObject params = new JSONObject();
        params.put("iotId", iotId);

        IoTRequest request = getBaseIoTRequestBuilder()
                .setPath(OTAConstants.APICLIENT_PATH_QUERYSTATUSINFO)
                .setParams(params.getInnerMap())
                .build();

        if (null != mIoTAPIClient) {
            mIoTAPIClient.send(request, new IoTCallback() {
                @Override
                public void onFailure(IoTRequest ioTRequest, Exception e) {
                    ALog.e(TAG, "request path:" + ioTRequest.getPath() + " error", e);
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                    ALog.d(TAG, "path:" + ioTRequest.getPath() + ", response:" + ioTResponse.getData().toString());

                    if (ioTResponse.getCode() == 200) {
                        try {
                            OTADeviceInfo deviceInfo = JSON.parseObject(ioTResponse.getData().toString(),
                                    OTADeviceInfo.class);
                            callback.onResponse(deviceInfo);
                        } catch (Exception e) {
                            ALog.e(TAG, "parse deviceInfo error", e);
                            callback.onFailure(ioTResponse.getLocalizedMsg());
                        }
                    } else {
                        ALog.e(TAG, "request path:" + ioTRequest.getPath() + "error " + ioTResponse.getLocalizedMsg());
                        callback.onFailure(ioTResponse.getLocalizedMsg());
                    }
                }
            });
        }
    }

}
