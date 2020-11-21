package com.aliyun.iot.ilop.page.mine.tripartite_platform.business.listener;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;

import com.aliyun.iot.ilop.page.mine.MineConstants;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.BindTaoBaoAccountBean;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.GetThirdpartyBean;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.TripartitePlatformListBean;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.UnBindBean;

import static com.aliyun.iot.APIConfig.ME_THIRD_BIND_UNBIND_ACCOUNT;
import static com.aliyun.iot.APIConfig.ME_THIRD_SUPPORT;
import static com.aliyun.iot.APIConfig.ME_THIRD_SUPPORT_ACCOUNT;
import static com.aliyun.iot.APIConfig.ME_THIRD_TAOBAO_BIND_ACCOUNT;

public class TmallGenieListActivityBusinessListener extends BaseBusinessListener {
    public TmallGenieListActivityBusinessListener(Handler mHandler) {
        super(mHandler);
    }

    @Override
    protected void onResponseSuccess(IoTRequest ioTRequest, String ioTResponse) {
        if (null == mHandler) {
            return;
        }

        if (ME_THIRD_SUPPORT.equals(ioTRequest.getPath())) {
            try {
                TripartitePlatformListBean infos = JSON.parseObject(ioTResponse, TripartitePlatformListBean.class);
                Message.obtain(mHandler, MineConstants.MINE_MESSAGE_RESPONSE_GET_SUOOPRTED_DEVICES_SUCCESS, infos).sendToTarget();
            } catch (Exception e) {
                Message.obtain(mHandler, MineConstants.MINE_MESSAGE_RESPONSE_GET_SUOOPRTED_DEVICES_FAIL).sendToTarget();
            }
        } else if (ME_THIRD_TAOBAO_BIND_ACCOUNT.equals(ioTRequest.getPath())) {
            try {
                BindTaoBaoAccountBean infos = JSON.parseObject(ioTResponse, BindTaoBaoAccountBean.class);
                Message.obtain(mHandler, MineConstants.MINE_MESSAGE_RESPONSE_BIND_TAOBAO_ACCOUNT_SUCCESS, infos).sendToTarget();
            } catch (Exception e) {
                ALog.e(TAG, "parse OTADeviceSimpleInfo error", e);
                Message.obtain(mHandler, MineConstants.MINE_MESSAGE_RESPONSE_BIND_TAOBAO_ACCOUNT_FAIL).sendToTarget();
            }
        } else if (ME_THIRD_SUPPORT_ACCOUNT.equals(ioTRequest.getPath())) {
            try {
                GetThirdpartyBean infos = JSON.parseObject(ioTResponse, GetThirdpartyBean.class);
                Message.obtain(mHandler, MineConstants.MINE_MESSAGE_RESPONSE_GET_TAOBAO_ACCOUNT_SUCCESS, infos).sendToTarget();
            } catch (Exception e) {
                ALog.e(TAG, "parse OTADeviceSimpleInfo error", e);
                Message.obtain(mHandler, MineConstants.MINE_MESSAGE_RESPONSE_GET_TAOBAO_ACCOUNT_FAIL).sendToTarget();
            }
        } else if (ME_THIRD_BIND_UNBIND_ACCOUNT.equals(ioTRequest.getPath())) {
            try {
                UnBindBean infos = JSON.parseObject(ioTResponse, UnBindBean.class);
                Message.obtain(mHandler, MineConstants.MINE_MESSAGE_RESPONSE_UNBIND_TAOBAO_ACCOUNT_SUCCESS, infos).sendToTarget();
            } catch (Exception e) {
                ALog.e(TAG, "parse OTADeviceSimpleInfo error", e);
                Message.obtain(mHandler, MineConstants.MINE_MESSAGE_RESPONSE_UNBIND_TAOBAO_ACCOUNT_FAIL).sendToTarget();
            }
        }
    }

    @Override
    protected void onResponseFailure(IoTRequest ioTRequest, String ioTResponse) {
        if (null == mHandler) {
            return;
        }

        Message.obtain(mHandler, MineConstants.MINE_MESSAGE_RESPONSE_ERROR).sendToTarget();
    }

    @Override
    protected void onRequestFailure(IoTRequest ioTRequest, Exception e) {
        if (null == mHandler) {
            return;
        }

        Message.obtain(mHandler, MineConstants.MINE_MESSAGE_RESPONSE_ERROR).sendToTarget();
    }

}
