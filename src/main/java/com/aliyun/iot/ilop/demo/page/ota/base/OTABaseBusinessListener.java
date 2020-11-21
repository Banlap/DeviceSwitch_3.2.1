package com.aliyun.iot.ilop.demo.page.ota.base;

import android.os.Handler;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.log.ALog;
import org.json.JSONObject;

/**
 * Created by david on 2018/4/13.
 *
 * @author david
 * @date 2018/04/13
 */
public abstract class OTABaseBusinessListener implements IoTCallback {
    public static final String TAG = "OTABaseBusinessListener";
    protected Handler mHandler;

    public OTABaseBusinessListener(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    final public void onFailure(IoTRequest ioTRequest, Exception e) {
        ALog.e(TAG, "request error : " + ioTRequest.getPath(), e);
        onRequestFailure(ioTRequest, e);
    }

    @Override
    final public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
        if (ioTResponse.getCode() != 200) {
            ALog.e(TAG, "request path:" + ioTRequest.getPath() + " error code:" + ioTResponse.getCode());
            onResponseFailure(ioTRequest, ioTResponse.getLocalizedMsg());
            return;
        }

        String response = "";
        if (null != ioTResponse.getData()) {
            if (ioTResponse.getData() instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject)ioTResponse.getData();
                response = jsonObject.toString();
            } else {
                response = ioTResponse.getData().toString();
            }
        }

        ALog.d(TAG, "request path:" + ioTRequest.getPath() + " response:" + response);
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
