package com.aliyun.iot.ilop.demo.page.ota.executor;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileDownstreamListener;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileSubscrbieListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.ilop.demo.page.ota.OTAConstants;
import com.aliyun.iot.ilop.demo.page.ota.bean.OTADeviceInfo;
import com.aliyun.iot.ilop.demo.page.ota.bean.OTAStatusInfo;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAQueryStatusCallback;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAStartUpgradeCallback;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAStopUpgradeCallback;
import com.aliyun.iot.ilop.demo.page.ota.interfaces.IOTAExecutor;
import com.aliyun.iot.ilop.demo.page.ota.interfaces.IOTAStatusChangeListener;

/**
 * Created by david on 2018/4/13.
 *
 * @author david
 * @date 2018/04/13
 *
 * wifi ota
 */
public class WifiOTAExecutor implements IOTAExecutor {
    private static final String TAG = "WifiOTAExecutor";

    private IoTAPIClient mIoTAPIClient;
    private String TOPIC_PATH = "/ota/device/forward";
    private IOTAStatusChangeListener mStatusListener;
    private boolean maybeRegister = false;

    public WifiOTAExecutor(IOTAStatusChangeListener listener) {
        IoTAPIClientFactory factory = new IoTAPIClientFactory();
        mIoTAPIClient = factory.getClient();

        this.mStatusListener = listener;

        registerListener();
    }

    @Override
    public void destroy() {
        unRegisterListener();
    }

    private IMobileSubscrbieListener mTopicListener = new IMobileSubscrbieListener() {
        @Override
        public void onSuccess(String topic) {
            ALog.d(TAG, "subscribe onSuccess, topic = " + topic);

        }

        @Override
        public void onFailed(String topic, AError error) {
            ALog.d(TAG, "subscribe onFailed, topic = " + topic);
        }

        @Override
        public boolean needUISafety() {
            return false;
        }
    };

    private IMobileDownstreamListener mDownStreamListener = new IMobileDownstreamListener() {
        @Override
        public void onCommand(String method, String data) {
            ALog.d(TAG, "接收到Topic = " + method + ", data=" + data);
            try {
                JSONObject jsonObject = JSON.parseObject(data);
//                JSONObject params = jsonObject.getJSONObject("params");
                OTAStatusInfo info = JSONObject.toJavaObject(jsonObject, OTAStatusInfo.class);
                if (null != mStatusListener) {
                    mStatusListener.onStatusChange(info);
                }
            } catch (Exception e) {
                ALog.e(TAG, "onCommand parse Object failed!", e);
            }

        }

        @Override
        public boolean shouldHandle(String method) {
            return TOPIC_PATH.equalsIgnoreCase(method);

        }
    };

    /* ==========================implement IOTAExecutor @start========================== */

    @Override
    public void queryOTAStatus(String iotId, final IOTAQueryStatusCallback callback) {
        if (null == callback) {
            return;
        }

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

    @Override
    public void startUpgrade(String iotId, final IOTAStartUpgradeCallback callback) {
        if (null == callback) {
            return;
        }

        JSONObject params = new JSONObject();
        List<String> iotIds = new ArrayList<>();
        iotIds.add(iotId);
        params.put("iotIds", iotIds);

        IoTRequest request = getBaseIoTRequestBuilder()
            .setPath(OTAConstants.APICLIENT_PATH_DOUPGRADE)
            .setParams(params.getInnerMap())
            .build();

        ALog.d(TAG, "request:" + request.toString());

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
                        callback.onSuccess();
                    } else {
                        ALog.e(TAG, "request path:" + ioTRequest.getPath() + "error " + ioTResponse.getLocalizedMsg());
                        callback.onFailure(ioTResponse.getLocalizedMsg());
                    }
                }
            });
        }
    }

    @Override
    public void stopUpgrade(String iotId, IOTAStopUpgradeCallback callback) {
        if (null == callback) {
            return;
        }

        //暂时没有该功能
        //callback.onFailure(new Exception("wifi ota can not stop"));
    }

    /* ==========================implement IOTAExecutor @end========================== */

    /* ==========================private methods @start========================== */

    private IoTRequestBuilder getBaseIoTRequestBuilder() {
        IoTRequestBuilder builder = new IoTRequestBuilder();
        builder.setAuthType(OTAConstants.APICLIENT_IOTAUTH)
            .setApiVersion(OTAConstants.APICLIENT_VERSION);
        return builder;
    }

    private void registerListener() {
        if (maybeRegister) {
            return;
        }

        if (null != mTopicListener) {
            MobileChannel.getInstance().subscrbie(TOPIC_PATH, mTopicListener);
            ALog.d(TAG, "subscribe " + TOPIC_PATH);
        }

        if (null != mDownStreamListener) {
            MobileChannel.getInstance().registerDownstreamListener(true, mDownStreamListener);
            ALog.d(TAG, "register " + TOPIC_PATH);
        }

        maybeRegister = true;
    }

    private void unRegisterListener() {
        if (null != mTopicListener) {
            MobileChannel.getInstance().unSubscrbie(TOPIC_PATH, mTopicListener);
            ALog.d(TAG, "unSubscribe " + TOPIC_PATH);
        }

        if (null != mDownStreamListener) {
            MobileChannel.getInstance().unRegisterDownstreamListener(mDownStreamListener);
            ALog.d(TAG, "unRegister " + TOPIC_PATH);
        }

        maybeRegister = false;
    }
    /* ==========================private methods @end========================== */
}
