package com.aliyun.iot.ilop.demo.page.ota.manager;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.ilop.demo.page.ota.OTAConstants;
import com.aliyun.iot.ilop.demo.page.ota.bean.OTADeviceInfo;
import com.aliyun.iot.ilop.demo.page.ota.bean.OTAStatusInfo;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAQueryStatusCallback;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAStartUpgradeCallback;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAStopUpgradeCallback;
import com.aliyun.iot.ilop.demo.page.ota.executor.BreezeOTAExecutor;
import com.aliyun.iot.ilop.demo.page.ota.executor.WifiOTAExecutor;
import com.aliyun.iot.ilop.demo.page.ota.interfaces.IOTAExecutor;
import com.aliyun.iot.ilop.demo.page.ota.interfaces.IOTAStatusChangeListener;

/**
 * Created by david on 2018/4/12.
 *
 * @author david
 * @date 2018/04/12
 */
public class OTAManager {
    private static final String TAG = "OTAManager";
    private static final String NET_WIFI = "NET_WIFI";
    private static final String NET_ETHERNET = "NET_ETHERNET";
    private static final String NET_CELLULAR = "NET_CELLULAR";


    private static final String NET_BT = "NET_BT";
    private Handler mHandler;
    private IOTAExecutor mIOTAExecutor;
    private String mIotId;
    private String netType;

    private IOTAStatusChangeListener mStatusListener = new IOTAStatusChangeListener() {
        @Override
        public void onStatusChange(OTAStatusInfo info) {
            if (null == info) {
                return;
            }

            if (mIotId == null) {
                return;
            }

            ALog.d(TAG, "iotId:" + info.iotId + ", onStatusChange:" + info.upgradeStatus);

            if (info.iotId.equalsIgnoreCase(mIotId)) {
                Message.obtain(mHandler, OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_STATUS_SUCCESS, info)
                        .sendToTarget();
            }
        }
    };

    public OTAManager(Handler handler, String iotId, String netType) {
        this.mHandler = handler;
        this.mIotId = iotId;
        this.netType = netType;
        generateExecutorByModelType(netType);
    }

    private void generateExecutorByModelType(String netType) {
        if (null != mIOTAExecutor) {
            return;
        }

        //判断是wifi还是蓝牙
        if (TextUtils.isEmpty(netType)) {
            Message.obtain(mHandler, OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_INFO_FAILED)
                    .sendToTarget();
            ALog.e(TAG, "netType is null !!!");
        } else if (netType.equalsIgnoreCase(NET_WIFI) || netType.equalsIgnoreCase(NET_ETHERNET) || netType.equalsIgnoreCase(NET_CELLULAR)) {
            //wifi设备
            mIOTAExecutor = new WifiOTAExecutor(mStatusListener);
        } else if (netType.equalsIgnoreCase(NET_BT)) {
            //蓝牙设备
            mIOTAExecutor = new BreezeOTAExecutor(mStatusListener);
        } else {
            ALog.e(TAG, "type:" + netType + "not support");
            Message.obtain(mHandler, OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_INFO_FAILED)
                    .sendToTarget();
        }
    }

    /**
     * 查询ota状态
     */
    public void queryOTAStatus() {
        if (null == mHandler) {
            Message.obtain(mHandler, OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_INFO_FAILED)
                    .sendToTarget();
            return;
        }
        if (null == mIotId) {
            ALog.e(TAG, "iotId is null");
            Message.obtain(mHandler, OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_INFO_FAILED)
                    .sendToTarget();
            return;
        }

        if (mIOTAExecutor == null) {
            Message.obtain(mHandler, OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_INFO_FAILED)
                    .sendToTarget();
            return;
        }
        mIOTAExecutor.queryOTAStatus(mIotId, new IOTAQueryStatusCallback() {
            @Override
            public void onResponse(OTADeviceInfo deviceInfo) {
                if (null == mHandler) {
                    return;
                }

                Message.obtain(mHandler, OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_INFO_SUCCESS, deviceInfo)
                        .sendToTarget();
            }

            @Override
            public void onFailure(String msg) {
                if (null == mHandler) {
                    return;
                }

                ALog.e(TAG, msg);
                Message.obtain(mHandler, OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_INFO_FAILED)
                        .sendToTarget();
            }
        });
    }

    /**
     * 开始升级
     */
    public void startUpgrade() {
        if (null == mIotId) {
            ALog.e(TAG, "iotId is null");
            return;
        }

        mIOTAExecutor.startUpgrade(mIotId, new IOTAStartUpgradeCallback() {
            @Override
            public void onSuccess() {
                if (null == mHandler) {
                    return;
                }

                Message.obtain(mHandler, OTAConstants.MINE_MESSAGE_RESPONSE_OTA_UPGRADE_SUCCESS, netType).sendToTarget();
            }

            @Override
            public void onFailure(String msg) {
                if (null == mHandler) {
                    return;
                }

                Message.obtain(mHandler, OTAConstants.MINE_MESSAGE_RESPONSE_OTA_UPGRADE_FAILED, msg).sendToTarget();
            }
        });
    }

    /**
     * 停止升级（暂时没有该功能）
     */
    public void stopUpgrade() {
        if (null == mIotId) {
            ALog.e(TAG, "iotId is null");
            return;
        }

        mIOTAExecutor.stopUpgrade(mIotId, new IOTAStopUpgradeCallback() {
            @Override
            public void onSuccess() {
                if (null == mHandler) {
                    return;
                }
                //待补充
            }

            @Override
            public void onFailure(Exception e) {
                if (null == mHandler) {
                    return;
                }
                //待补充
            }
        });
    }

    public void destroy() {
        if (null != mIOTAExecutor) {
            mIOTAExecutor.destroy();
            mIOTAExecutor = null;
        }
    }
}
