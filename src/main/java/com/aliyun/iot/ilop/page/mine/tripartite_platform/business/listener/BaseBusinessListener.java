package com.aliyun.iot.ilop.page.mine.tripartite_platform.business.listener;

import android.os.Handler;

import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;


import org.json.JSONObject;


public abstract class BaseBusinessListener implements IoTCallback {
    public static final String TAG = "BaseBusinessListener";
    protected Handler mHandler;

    public BaseBusinessListener(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public void onFailure(IoTRequest ioTRequest, Exception e) {
        ALog.e(TAG, "request error : " + ioTRequest.getPath(), e);
        onRequestFailure(ioTRequest, e);
    }

    @Override
    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
        if (ioTResponse.getCode() != 200) {
            ALog.e(TAG, "request path:" + ioTRequest.getPath() + " error code:" + ioTResponse.getCode());
            onResponseFailure(ioTRequest, ioTResponse.getLocalizedMsg());
            return;
        }

        String response = "";
        if (null != ioTResponse.getData()) {
            if (ioTResponse.getData() instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) ioTResponse.getData();
                response = jsonObject.toString();
            } else {
                response = ioTResponse.getData().toString();
            }
        }

        ALog.e(TAG, "request path:==" + ioTRequest.getPath() + " response:" + response);
        onResponseSuccess(ioTRequest, response);
    }

    /**
     * 成功返回
     *
     * @param ioTRequest  请求参数
     * @param ioTResponse 返回参数
     */
    protected abstract void onResponseSuccess(IoTRequest ioTRequest, String ioTResponse);

    /**
     * 失败返回
     *
     * @param ioTRequest  请求参数
     * @param ioTResponse 返回参数
     */
    protected abstract void onResponseFailure(IoTRequest ioTRequest, String ioTResponse);

    /**
     * 失败返回
     *
     * @param ioTRequest 请求参数
     * @param e          异常信息
     */
    protected abstract void onRequestFailure(IoTRequest ioTRequest, Exception e);
}
