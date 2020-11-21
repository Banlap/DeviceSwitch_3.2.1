package com.aliyun.iot.ilop.demo.page.ota.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.ilop.demo.page.ota.OTAConstants;
import com.aliyun.iot.ilop.demo.page.ota.bean.OTADeviceInfo;
import com.aliyun.iot.ilop.demo.page.ota.bean.OTADeviceSimpleInfo;
import com.aliyun.iot.ilop.demo.page.ota.bean.OTAStatusInfo;
import com.aliyun.iot.ilop.demo.page.ota.business.OTAActivityBusiness;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAConnectCallBack;
import com.aliyun.iot.ilop.demo.page.ota.interfaces.IOTAActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by david on 2018/4/10.
 *
 * @author david
 * @date 2018/04/10
 */
public class OTAActivityHandler extends Handler {
    private static final String TAG = "OTAActivityHandler";
    private IOTAActivity mIActivity;
    private OTAActivityBusiness mBusiness;
    private OTADeviceSimpleInfo mSimpleInfo;
    private boolean isUpgrade = false;

    public OTAActivityHandler(IOTAActivity iOTAActivity) {
        super(Looper.getMainLooper());
        mIActivity = iOTAActivity;
        mBusiness = new OTAActivityBusiness(this);
    }

    /**
     * 刷新数据
     *
     * @param info
     */
    public void refreshData(OTADeviceSimpleInfo info) {
        if (null == mBusiness) {
            return;
        }

        mSimpleInfo = info;

        if (null != info) {
            mBusiness.requestProductInfo(mSimpleInfo.iotId);
        }

        if (null != mIActivity) {
            mIActivity.showLoading();
        }
    }

    /**
     * 请求升级
     *
     * @param
     */
    public void requestUpdate() {
        if (null == mBusiness) {
            return;
        }

        mBusiness.requestUpgrade();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (null == mIActivity) {
            return;
        }

        if (null == mBusiness) {
            return;
        }

        if (null == mSimpleInfo) {
            return;
        }
        if (OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_INFO_SUCCESS == msg.what) {
            showCurrentVersion(isUpgrade, msg.obj);
        } else if (OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_PRODUCT_INFO_SUCCESS == msg.what) {
            //产品详情获取成功
            String netType = "";
            if (null != msg.obj) {
                netType = msg.obj.toString();
            }
            mBusiness.generateOTAManager(this, mSimpleInfo.iotId, netType);
            mBusiness.requestDeviceInfo();
        } else if (OTAConstants.MINE_MESSAGE_RESPONSE_OTA_UPGRADE_SUCCESS == msg.what) {
            if (msg.obj != null) {
                String netType = (String) msg.obj;
                //升级请求成功
                if (netType.equalsIgnoreCase("NET_BT")) {
                    mIActivity.showUpgradeStatus(OTAConstants.OTA_STATUS_DONE);
                } else {
                    mIActivity.showUpgradeStatus(OTAConstants.OTA_STATUS_LOADING);
                }
                isUpgrade = true;
                //成功后再次查询ota信息
                mBusiness.requestUpgradeStatus();
            }
        } else if (OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_STATUS_SUCCESS == msg.what) {
            //升级状态(蓝牙和wifi)
            try {
                if (null != msg.obj) {
                    OTAStatusInfo info = (OTAStatusInfo) msg.obj;
                    int status = Integer.parseInt(info.upgradeStatus);
                    if (info.desc.equals("breeze_ble")) {
                        if (status == OTAConstants.OTA_STATUS_BUL_FAILUER) {
                            connectMac.onConnect(false);
                        } else if (status == OTAConstants.OTA_STATUS_BUL_SUCCESS) {
                            connectMac.onConnect(true);
                            mIActivity.showUpgradeStatus(status);
                        } else if (status == OTAConstants.OTA_STATUS_BUL_LOADING) {
                            connectMac.isLoading();
                        }
                    } else {
                        mIActivity.showUpgradeStatus(status);
                    }

                }
            } catch (Exception e) {
                ALog.e(TAG, "get status error", e);
            }
        } else if (
                OTAConstants.OTA_MESSAGE_NETWORK_ERROR == msg.what
                        || OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_PRODUCT_INFO_FAILED == msg.what
                        || OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_INFO_FAILED == msg.what) {
            //请求失败(网络错误、产品详情获取、设备详情获取)
            mIActivity.showLoaded(null);
            mIActivity.showLoadError();
        } else if (OTAConstants.MINE_MESSAGE_RESPONSE_OTA_UPGRADE_FAILED == msg.what) {
            //升级请求失败
            if (null != msg.obj) {
                mIActivity.showLoaded(msg.obj.toString());
            }
            mIActivity.showUpgradeStatus(OTAConstants.OTA_STATUS_FAILURE);
        }
    }

    public void destroy() {
        removeMessages(OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_INFO_FAILED);
        removeMessages(OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_INFO_SUCCESS);
        removeMessages(OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_STATUS_SUCCESS);
        removeMessages(OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_PRODUCT_INFO_SUCCESS);
        removeMessages(OTAConstants.MINE_MESSAGE_RESPONSE_OTA_DEVICE_PRODUCT_INFO_FAILED);
        removeMessages(OTAConstants.MINE_MESSAGE_RESPONSE_OTA_UPGRADE_SUCCESS);
        removeMessages(OTAConstants.MINE_MESSAGE_RESPONSE_OTA_UPGRADE_FAILED);
        removeMessages(OTAConstants.OTA_MESSAGE_NETWORK_ERROR);

        if (null != mBusiness) {
            mBusiness.destroy();
            mBusiness = null;
        }
        mIActivity = null;
    }

    IOTAConnectCallBack connectMac;

    public void setConnectMac(IOTAConnectCallBack connectMac) {
        this.connectMac = connectMac;
    }

    /**
     * 查询OTA的两次处理
     *
     * @param isSuccess
     * @param object
     */
    private void showCurrentVersion(boolean isSuccess, Object object) {
        //设备详情获取成功
        OTADeviceInfo info = (OTADeviceInfo) object;

        if (null == info) {
            ALog.e(TAG, "info is null");
            return;
        }

        if (null != info.otaFirmwareDTO) {
            String newVersion = info.otaFirmwareDTO.version;
            String currentVersion = info.otaFirmwareDTO.currentVersion;
            String newVersionTime = "";
            String currentVersionTime = "";

            if (!TextUtils.isEmpty(info.otaFirmwareDTO.timestamp)) {
                try {
                    Date date = new Date(Long.valueOf(info.otaFirmwareDTO.timestamp));
                    Locale locale = AApplication.getInstance().getResources().getConfiguration().locale;
                    newVersionTime = new SimpleDateFormat("YYYY/MM/dd", locale).format(date);
                } catch (Exception e) {
                    ALog.e(TAG, "format new version date error" + e);
                }
            }

            if (!TextUtils.isEmpty(info.otaFirmwareDTO.currentTimestamp)) {
                try {
                    Date date = new Date(Long.valueOf(info.otaFirmwareDTO.currentTimestamp));
                    Locale locale = AApplication.getInstance().getResources().getConfiguration().locale;
                    currentVersionTime = new SimpleDateFormat("YYYY/MM/dd", locale).format(date);
                } catch (Exception e) {
                    ALog.e(TAG, "format current date error" + e);
                }
            }
            //判断升级是否成功
            if (isSuccess) {
                mIActivity.showCurrentVersion(newVersion + " " + newVersionTime);
            } else {
                mIActivity.showTips(newVersion + " " + newVersionTime);
                mIActivity.showCurrentVersion(currentVersion + " " + currentVersionTime);
            }

        }

        if (null != info.otaUpgradeDTO) {
            int status = Integer.valueOf(info.otaUpgradeDTO.upgradeStatus);
            mIActivity.showUpgradeStatus(status);
        }

        mIActivity.showLoaded(null);
    }
}

