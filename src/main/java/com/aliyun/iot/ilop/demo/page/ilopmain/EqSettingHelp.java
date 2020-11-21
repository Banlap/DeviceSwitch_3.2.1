package com.aliyun.iot.ilop.demo.page.ilopmain;

import android.util.Log;

import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;

public class EqSettingHelp {
    public static final String UN_BIND = "/uc/unbindAccountAndDev";

    public static void requestUnbind(String iotId,IoTCallback ioTCallback) {
        Log.d("EqSettingHelp", "_______________" + iotId);
        String apiVersion = "1.0.2";
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setAuthType("iotAuth")
                .setScheme(Scheme.HTTPS)
                .setPath(UN_BIND)
                .setApiVersion(apiVersion)
                .addParam("iotId", iotId);
        IoTRequest request = builder.build();
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request,ioTCallback );
    }
}
