package com.aliyun.iot.ilop.page.mine.tripartite_platform.business;


import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.ilop.page.mine.MineConstants;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.business.listener.TmallGenieListActivityBusinessListener;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.handler.TmallGenieHandler;

import java.util.Map;

import static com.aliyun.iot.APIConfig.ME_THIRD_BIND_UNBIND_ACCOUNT;
import static com.aliyun.iot.APIConfig.ME_THIRD_BIND_UNBIND_ACCOUNT_VERSION;
import static com.aliyun.iot.APIConfig.ME_THIRD_SUPPORT;
import static com.aliyun.iot.APIConfig.ME_THIRD_SUPPORT_ACCOUNT;
import static com.aliyun.iot.APIConfig.ME_THIRD_SUPPORT_ACCOUNT_VERSION;
import static com.aliyun.iot.APIConfig.ME_THIRD_SUPPORT_VERSION;
import static com.aliyun.iot.APIConfig.ME_THIRD_TAOBAO_BIND_ACCOUNT;
import static com.aliyun.iot.APIConfig.ME_THIRD_TAOBAO_BIND_ACCOUNT_VERSION;

public class TmallGenieBusiness {
    private IoTAPIClient mIoTAPIClient;
    private TmallGenieListActivityBusinessListener mListener;

    public TmallGenieBusiness(TmallGenieHandler mHandler) {
        mListener = new TmallGenieListActivityBusinessListener(mHandler);

        IoTAPIClientFactory factory = new IoTAPIClientFactory();
        mIoTAPIClient = factory.getClient();
    }

    private IoTRequestBuilder getBaseIoTRequestBuilder(String apiVersion) {
        IoTRequestBuilder builder = new IoTRequestBuilder();
        builder.setAuthType(MineConstants.APICLIENT_IOTAUTH)
                .setApiVersion(apiVersion);
        return builder;
    }

    public void getSupportedDevices(String channel, int pageNo, int pageSize) {
        if (null == mIoTAPIClient) {
            return;
        }

        JSONObject params = new JSONObject();
        if (null != channel) {
            params.put("channel", channel);
        }
        if (null != channel) {
            params.put("pageNo", pageNo);
        }
        if (null != channel) {
            params.put("pageSize", pageSize);
        }


        Map<String, Object> requestMap = params.getInnerMap();

        IoTRequest ioTRequest = getBaseIoTRequestBuilder(ME_THIRD_SUPPORT_VERSION)
                .setPath(ME_THIRD_SUPPORT)
                .setParams(requestMap)
                .setScheme(Scheme.HTTPS)
                .build();
        mIoTAPIClient.send(ioTRequest, mListener);
    }


    public void bindAccount(String authCode) {
        if (null == mIoTAPIClient) {
            return;
        }

        JSONObject params = new JSONObject();
        if (null != authCode) {
            params.put("authCode", authCode);
        }
        Map<String, Object> requestMap = params.getInnerMap();

        IoTRequest ioTRequest = getBaseIoTRequestBuilder(ME_THIRD_TAOBAO_BIND_ACCOUNT_VERSION)
                .setPath(ME_THIRD_TAOBAO_BIND_ACCOUNT)
                .setParams(requestMap)
                .setScheme(Scheme.HTTPS)
                .build();
        mIoTAPIClient.send(ioTRequest, mListener);

    }

    public void queryAccount(String accountType) {
        ALog.d("TmallGenieBusiness", "queryAccount");
        if (null == mIoTAPIClient) {
            return;
        }

        JSONObject params = new JSONObject();
        if (null != accountType) {
            params.put("accountType", accountType);
        }
        Map<String, Object> requestMap = params.getInnerMap();

        IoTRequest ioTRequest = getBaseIoTRequestBuilder(ME_THIRD_SUPPORT_ACCOUNT_VERSION)
                .setPath(ME_THIRD_SUPPORT_ACCOUNT)
                .setParams(requestMap)
                .setScheme(Scheme.HTTPS)
                .build();
        mIoTAPIClient.send(ioTRequest, mListener);
    }

    public void unBindAccount(String accountId, String accountType) {
        if (null == mIoTAPIClient) {
            return;
        }

        JSONObject params = new JSONObject();
        if (null != accountType) {
            params.put("accountType", accountType);
        }
        Map<String, Object> requestMap = params.getInnerMap();

        IoTRequest ioTRequest = getBaseIoTRequestBuilder(ME_THIRD_BIND_UNBIND_ACCOUNT_VERSION)
                .setPath(ME_THIRD_BIND_UNBIND_ACCOUNT)
                .setParams(requestMap)
                .setScheme(Scheme.HTTPS)
                .build();
        mIoTAPIClient.send(ioTRequest, mListener);
    }
}
