package com.aliyun.iot.ilop.demo.page.ota.business.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import android.os.Handler;
import android.os.Message;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.ilop.demo.page.ota.OTAConstants;
import com.aliyun.iot.ilop.demo.page.ota.base.OTABaseBusinessListener;
import com.aliyun.iot.ilop.demo.page.ota.bean.OTADeviceDetailInfo;

/**
 * Created by david on 2018/4/13.
 *
 * @author david
 * @date 2018/04/13
 */
public class OTAActivityBusinessListener extends OTABaseBusinessListener {
    public OTAActivityBusinessListener(Handler handler) {
        super(handler);
    }

    /**
     * 请求成功
     * @param ioTRequest  请求参数
     * @param ioTResponse 返回参数
     */
    @Override
    protected void onResponseSuccess(IoTRequest ioTRequest, String ioTResponse) {
        if (null == mHandler) {
            return;
        }

        if (OTAConstants.APICLIENT_PATH_QUERYSTATUSINFO.equals(ioTRequest.getPath())) {
            try {
                OTADeviceDetailInfo detailInfo = JSON.parseObject(ioTResponse, OTADeviceDetailInfo.class);
                Message.obtain(mHandler, OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_INFO_SUCCESS, detailInfo)
                    .sendToTarget();
            } catch (Exception e) {
                ALog.e(TAG, "parse detailInfo error", e);
                onResponseFailure(ioTRequest, ioTResponse);
            }
        } else if (OTAConstants.APICLIENT_PATH_QUERYPRODUCTINFO.equals(ioTRequest.getPath())) {
            JSONObject jsonObject = JSONObject.parseObject(ioTResponse);
            String netType = jsonObject.getString("netType");
            Message.obtain(mHandler, OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_PRODUCT_INFO_SUCCESS, netType)
                .sendToTarget();
        }
    }

    /**
     * 服务端返回异常
     * @param ioTRequest  请求参数
     * @param ioTResponse 返回参数
     */
    @Override
    protected void onResponseFailure(IoTRequest ioTRequest, String ioTResponse) {
        if (null == mHandler) {
            return;
        }

        if (OTAConstants.APICLIENT_PATH_QUERYPRODUCTINFO.equals(ioTRequest.getPath())) {
            Message.obtain(mHandler, OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_PRODUCT_INFO_FAILED).sendToTarget();
        } else if (OTAConstants.APICLIENT_PATH_QUERYSTATUSINFO.equals(ioTRequest.getPath())) {
            Message.obtain(mHandler, OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_INFO_FAILED).sendToTarget();
        }

    }

    /**
     * 本地请求错误
     * @param ioTRequest 请求参数
     * @param e          异常信息
     */
    @Override
    protected void onRequestFailure(IoTRequest ioTRequest, Exception e) {
        if (null == mHandler) {
            return;
        }

        Message.obtain(mHandler, OTAConstants.OTA_MESSAGE_RESQUEST_ERROR).sendToTarget();
    }
}
