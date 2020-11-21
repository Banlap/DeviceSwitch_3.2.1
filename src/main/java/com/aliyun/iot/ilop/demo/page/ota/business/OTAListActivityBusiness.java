package com.aliyun.iot.ilop.demo.page.ota.business;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import android.os.Handler;

import androidx.annotation.Nullable;

import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.ilop.demo.page.ota.OTAConstants;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.OTAListActivityBusinessListener;

/**
 * Created by david on 2018/4/12.
 *
 * @author david
 * @date 2018/04/12
 */
public class OTAListActivityBusiness {
    private IoTAPIClient mIoTAPIClient;
    private OTAListActivityBusinessListener mListener;

    public OTAListActivityBusiness(Handler handler) {
        mListener = new OTAListActivityBusinessListener(handler);

        IoTAPIClientFactory factory = new IoTAPIClientFactory();
        mIoTAPIClient = factory.getClient();
    }

    private IoTRequestBuilder getBaseIoTRequestBuilder() {
        IoTRequestBuilder builder = new IoTRequestBuilder();
        builder.setAuthType(OTAConstants.APICLIENT_IOTAUTH)
            .setApiVersion(OTAConstants.APICLIENT_VERSION);
        return builder;
    }

    /**
     * 请求ota列表
     * @param houseId
     */
    public void requestOTAList(@Nullable String houseId) {
        if (null == mIoTAPIClient) {
            return;
        }

        JSONObject params = new JSONObject();
        if (null != houseId) {
            params.put("houseId", houseId);
        } else {
            params.put("houseId", "");
        }

        Map<String, Object> requestMap = params.getInnerMap();

        IoTRequest ioTRequest = getBaseIoTRequestBuilder()
            .setPath(OTAConstants.APICLIENT_PATH_QUERYOTADEVICELIST)
            .setParams(requestMap)
            .build();
        mIoTAPIClient.send(ioTRequest, mListener);
    }
}
