package com.aliyun.iot.ilop.demo.page.ota.handler;

import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.ilop.demo.page.ota.OTAConstants;
import com.aliyun.iot.ilop.demo.page.ota.bean.OTADeviceSimpleInfo;
import com.aliyun.iot.ilop.demo.page.ota.business.OTAListActivityBusiness;
import com.aliyun.iot.ilop.demo.page.ota.interfaces.IOTAListActivity;

/**
 * Created by david on 2018/4/12.
 *
 * @author david
 * @date 2018/04/12
 */
public class OTAListActivityHandler extends Handler {
    private static final String TAG = "OTAListActivityHandler";

    private OTAListActivityBusiness mBusiness;
    private IOTAListActivity mIActivity;

    public OTAListActivityHandler(IOTAListActivity iOTAListActivity) {
        super(Looper.getMainLooper());

        mIActivity = iOTAListActivity;
        mBusiness = new OTAListActivityBusiness(this);
    }

    /**
     * 请求可升级设备列表
     */
    public void requestOTAList() {
        if (null == mBusiness) {
            return;
        }

        mBusiness.requestOTAList(null);

        if (null != mIActivity) {
            mIActivity.showLoading();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (null == mIActivity) {
            return;
        }
        if (OTAConstants.MINE_MESSAGE_RESPONSE_OTA_LIST_SUCCESS == msg.what) {
            try {
                mIActivity.showLoaded();
                List<OTADeviceSimpleInfo> list = (List<OTADeviceSimpleInfo>)msg.obj;

                if (null == list || list.size() == 0) {
                    mIActivity.showEmptyList();
                } else {
                    mIActivity.showList(list);
                }

            } catch (Exception e) {
                mIActivity.showEmptyList();
                ALog.e(TAG, "handler control activity showList error " + e.getMessage());
                e.printStackTrace();
            }
        } else if (OTAConstants.MINE_MESSAGE_RESPONSE_OTA_LIST_FAILED == msg.what) {
            mIActivity.showLoaded();
            mIActivity.showLoadError();
        } else if (OTAConstants.OTA_MESSAGE_RESQUEST_ERROR == msg.what) {
            mIActivity.showLoaded();
            mIActivity.showLoadError();
        }
    }

    public void destroy() {
        removeMessages(OTAConstants.MINE_MESSAGE_RESPONSE_OTA_LIST_SUCCESS);

        mBusiness = null;
        mIActivity = null;
    }
}
