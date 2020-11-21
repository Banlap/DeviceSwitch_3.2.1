package com.aliyun.iot.ilop.page.mine.tripartite_platform.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.iot.ilop.page.mine.MineConstants;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.BindTaoBaoAccountBean;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.GetThirdpartyBean;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.TripartitePlatformListBean;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.UnBindBean;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.business.TmallGenieBusiness;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.interfaces.ITmallGenieActivity;

public class TmallGenieHandler extends Handler {
    private String TAG = "TmallGenieHandler";
    private ITmallGenieActivity mIActivity;
    private TmallGenieBusiness mBusiness;

    public TmallGenieHandler(ITmallGenieActivity iTmallGenieActivity) {
        super(Looper.getMainLooper());
        this.mIActivity = iTmallGenieActivity;
        mBusiness = new TmallGenieBusiness(this);
    }

    /**
     * 请求设备列表
     */
    public void requestDeviceList(String channel, int pageNo, int pageSize) {
        if (null == mBusiness) {
            return;
        }

        mBusiness.getSupportedDevices(channel, pageNo, pageSize);

        if (null != mIActivity) {
            mIActivity.showLoading();
        }
    }

    /**
     * 绑定账号
     *
     * @param authCode
     */
    public void bindAccount(String authCode) {
        if (null == mBusiness) {
            return;
        }
        mBusiness.bindAccount(authCode);
        if (null != mIActivity) {
            mIActivity.showLoading();
        }
    }

    /**
     * 查询是否绑定
     *
     * @param accountType
     */
    public void queryAccount(String accountType) {
        if (null == mBusiness) {
            return;
        }
        mBusiness.queryAccount(accountType);
        if (null != mIActivity) {
            mIActivity.showLoading();
        }
    }

    /**
     * 解绑
     *
     * @param accountId
     * @param accountType
     */
    public void unBindAccount(String accountId, String accountType) {
        if (null == mBusiness) {
            return;
        }
        mBusiness.unBindAccount(accountId, accountType);
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
        if (MineConstants.MINE_MESSAGE_RESPONSE_GET_SUOOPRTED_DEVICES_SUCCESS == msg.what) {
            try {
                mIActivity.showLoaded();
                TripartitePlatformListBean data = (TripartitePlatformListBean) msg.obj;
                mIActivity.showList(data);
            } catch (Exception e) {
                mIActivity.showEmptyList();
                ALog.e(TAG, "handler control activity showList error " + e.getMessage());
                e.printStackTrace();
            }
        } else if (MineConstants.MINE_MESSAGE_RESPONSE_GET_SUOOPRTED_DEVICES_FAIL == msg.what) {
            mIActivity.showLoaded();
            mIActivity.showLoadError();
        }
//        ————————————————————————————————————————————————————————
        else if (MineConstants.MINE_MESSAGE_RESPONSE_BIND_TAOBAO_ACCOUNT_SUCCESS == msg.what) {
            mIActivity.showLoaded();
            BindTaoBaoAccountBean data = (BindTaoBaoAccountBean) msg.obj;
            if (null != data) {
                if (null != data.getLinkIdentityIds()) {
                    mIActivity.bindAccountSuccess(data);
                } else {
                    mIActivity.bindAccountFail();
                }
            }
        } else if (MineConstants.MINE_MESSAGE_RESPONSE_BIND_TAOBAO_ACCOUNT_FAIL == msg.what) {
            mIActivity.showLoaded();
            mIActivity.showLoadError();
        }
//        ————————————————————————————————————————————————————————
        else if (MineConstants.MINE_MESSAGE_RESPONSE_GET_TAOBAO_ACCOUNT_SUCCESS == msg.what) {
            mIActivity.showLoaded();
            GetThirdpartyBean data = (GetThirdpartyBean) msg.obj;
            if (null != data && null != data.getLinkIndentityId()) {
                mIActivity.queryTAOBAOAccountSuccess(data);
            } else {
                mIActivity.queryTAOBAOAccountFail();
            }
        } else if (MineConstants.MINE_MESSAGE_RESPONSE_GET_TAOBAO_ACCOUNT_FAIL == msg.what) {
            mIActivity.showLoaded();
            mIActivity.showLoadError();
        }
        //        ————————————————————————————————————————————————————————
        else if (MineConstants.MINE_MESSAGE_RESPONSE_UNBIND_TAOBAO_ACCOUNT_SUCCESS == msg.what) {
            mIActivity.showLoaded();
            UnBindBean data = (UnBindBean) msg.obj;
            mIActivity.unBindAccountSuccess(data);
        } else if (MineConstants.MINE_MESSAGE_RESPONSE_UNBIND_TAOBAO_ACCOUNT_FAIL == msg.what) {
            mIActivity.showLoaded();
            mIActivity.showLoadError();
            mIActivity.unBindAccountFail();
        }
//        ----------------------------------------------------------------------------------------------------------
        else if (MineConstants.MINE_MESSAGE_RESPONSE_ERROR == msg.what) {
            mIActivity.showLoaded();
            mIActivity.showLoadError();
        }
    }

    public void destroy() {
        removeMessages(MineConstants.MINE_MESSAGE_RESPONSE_GET_SUOOPRTED_DEVICES_SUCCESS);
        mBusiness = null;
        mIActivity = null;
    }
}
