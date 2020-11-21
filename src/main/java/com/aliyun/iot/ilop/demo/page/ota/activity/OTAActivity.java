package com.aliyun.iot.ilop.demo.page.ota.activity;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.aliyun.iot.aep.sdk.framework.AActivity;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.ota.OTAConstants;
import com.aliyun.iot.ilop.demo.page.ota.bean.OTADeviceSimpleInfo;
import com.aliyun.iot.ilop.demo.page.ota.business.listener.IOTAConnectCallBack;
import com.aliyun.iot.ilop.demo.page.ota.handler.OTAActivityHandler;
import com.aliyun.iot.ilop.demo.page.ota.interfaces.IOTAActivity;
import com.aliyun.iot.ilop.demo.page.ota.view.MineOTAButton;
import com.aliyun.iot.ilop.demo.page.ota.view.MineOTAButton.OnOTAButtonClickListener;
import com.aliyun.iot.ilop.demo.page.ota.view.MineOTADialog;
import com.aliyun.iot.ilop.demo.page.ota.view.MineOTADialog.OnDialogButtonClickListener;
import com.aliyun.iot.ilop.demo.page.ota.view.SimpleTopbar;
import com.aliyun.iot.ilop.demo.page.ota.view.SimpleTopbar.onBackClickListener;
import com.aliyun.iot.link.ui.component.simpleLoadview.SimpleLoadingDialog;

/**
 * Created by david on 2018/4/10.
 *
 * @author david
 * @date 2018/04/10
 */
public class OTAActivity extends AActivity
        implements IOTAActivity, OnOTAButtonClickListener, onBackClickListener,
        OnDialogButtonClickListener, OnClickListener, IOTAConnectCallBack {
    private static final String TAG = "OTAActivity";

    private OTADeviceSimpleInfo mSimpleInfo;

    private MineOTAButton mButton;
    private ImageView mUpArrowImageView, mCircleImageView;
    private TextView mTipsTextView, mCurrentVersionTextView;
    private SimpleTopbar mTopbar;
    private TextView mRefreshTextView;
    private LinearLayout mWrapper;
    private SimpleLoadingDialog mLoadingDialog;

    private OTAActivityHandler mHandler;
    private MineOTADialog mRetryDialog;

    /* =====================lifecycle @start =====================*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ilop_ota_activity);

        initView();
        initEvent();
        initData();
        initHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (null == mSimpleInfo) {
            ALog.e(TAG, "onResume() OTADeviceSimpleInfo is null!");
            return;
        }

        if (null != mHandler) {
            mHandler.refreshData(mSimpleInfo);
        }
    }

    @Override
    protected void onDestroy() {
        if (null != mHandler) {
            mHandler.destroy();
        }

        if (null != mRetryDialog) {
            mRetryDialog.cancel();
            mRetryDialog = null;
        }

        if (null != mLoadingDialog) {
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }
        super.onDestroy();
    }

    /* =====================lifecycle @end =====================*/

    protected void initView() {
        mTopbar = findViewById(R.id.mine_topbar);
        mWrapper = findViewById(R.id.mine_setting_ota_detail_wrapper_linearlayout);
        mUpArrowImageView = findViewById(R.id.mine_setting_ota_detail_up_arrow_imageview);
        mCircleImageView = findViewById(R.id.mine_setting_ota_detail_success_imageview);
        mTipsTextView = findViewById(R.id.mine_setting_ota_detail_tips_textview);
        mCurrentVersionTextView = findViewById(R.id.mine_setting_ota_detail_current_version_textview);
        mRefreshTextView = findViewById(R.id.mine_setting_ota_detail_refresh_textview);
        mButton = findViewById(R.id.mine_setting_ota_detail_button_mineotabutton);

        mLoadingDialog = new SimpleLoadingDialog(this);
        mLoadingDialog.setLoadingViewStyle(R.style.OTALoadingStyle);
    }

    protected void initEvent() {
        if (null != mTopbar) {
            mTopbar.setOnBackClickListener(this);
        }

        if (null != mButton) {
            mButton.setOnOTAButtonClickListener(this);
        }

        if (null != mRefreshTextView) {
            mRefreshTextView.setActivated(true);
            mRefreshTextView.setOnClickListener(this);
        }
    }

    protected void initData() {
        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mSimpleInfo = (OTADeviceSimpleInfo) bundle.getParcelable(OTAConstants.OTA_KEY_DEVICESIMPLE_INFO);
            }
        } catch (Exception e) {
            mSimpleInfo = null;
            e.printStackTrace();
        }

        setTitle();
    }

    protected void initHandler() {
        mHandler = new OTAActivityHandler(this);
        mHandler.setConnectMac(this);
    }

    /* =====================implement IOTAActivity @start =====================*/

    @Override
    public void setTitle() {
        if (isFinishing()) {
            return;
        }

        if (null != mTopbar && null != mSimpleInfo) {
            mTopbar.setTitle(mSimpleInfo.deviceName + getString(R.string.ota));
        }
    }

    @Override
    public void showLoading() {
        if (isFinishing()) {
            return;
        }

        if (null != mLoadingDialog && !mLoadingDialog.isShowing()) {
            mLoadingDialog.showLoading(getString(R.string.ota_loading));
        }

        if (null != mWrapper) {
            mWrapper.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoaded(String msg) {
        if (isFinishing()) {
            return;
        }

        if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }

        if (null != mWrapper) {
            mWrapper.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(msg)) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showLoadError() {
        showDeviceInfoError();
    }

    @Override
    public void showUpgradeFailureDialog() {
        if (isFinishing()) {
            return;
        }

        if (null == mRetryDialog) {
            mRetryDialog = new MineOTADialog(this);
            mRetryDialog.setNegativeButtonText(getString(R.string.ota_dialog_negative));
            mRetryDialog.setPositiveButtonText(getString(R.string.ota_dialog_positive));
            mRetryDialog.setTitle(getString(R.string.ota_did_upgrade_failure));
            mRetryDialog.setCancelable(false);
            mRetryDialog.setOnDialogButtonClickListener(this);
        }

        if (!mRetryDialog.isShowing()) {
            mRetryDialog.show();
        }
    }

    @Override
    public void showTips(String latestVersion) {
        if (isFinishing()) {
            return;
        }

        if (null != mTipsTextView) {
            mTipsTextView.setTextColor(getResources().getColor(R.color.mine_color_1FC88B));
            mTipsTextView.setText(latestVersion);
        }
    }

    @Override
    public void showNoNetToast() {
        if (isFinishing()) {
            return;
        }
        Toast.makeText(this, getString(R.string.ota_network_error), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showCurrentVersion(String currentVersion) {
        if (isFinishing()) {
            return;
        }

        if (null != mCurrentVersionTextView) {
            String currentVersionTips = getString(R.string.ota_current_version) + " " + currentVersion;
            mCurrentVersionTextView.setVisibility(View.VISIBLE);
            mCurrentVersionTextView.setText(currentVersionTips);
        }
    }

    @Override
    public void showUpgradeStatus(int status) {
        if (isFinishing()) {
            return;
        }

        if (null == mButton) {
            return;
        }

        if (null == mRefreshTextView) {
            return;
        }

        if (OTAConstants.OTA_STATUS_PRE_LOAD == status || OTAConstants.OTA_STATUS_LOADING == status) {
            mUpArrowImageView.setVisibility(View.VISIBLE);
            mCircleImageView.setVisibility(View.GONE);
            mRefreshTextView.setVisibility(View.GONE);
            mButton.setVisibility(View.VISIBLE);
            mButton.setStatus(status);
        } else if (OTAConstants.OTA_STATUS_DONE == status) {
            mUpArrowImageView.setVisibility(View.GONE);
            mCircleImageView.setVisibility(View.VISIBLE);
            showTips(getString(R.string.ota_upgrade_success));
            mRefreshTextView.setVisibility(View.GONE);
            mButton.setVisibility(View.VISIBLE);
            mButton.setStatus(status);
        } else if (OTAConstants.OTA_STATUS_FAILURE == status) {
            mUpArrowImageView.setVisibility(View.VISIBLE);
            mCircleImageView.setVisibility(View.GONE);
            mRefreshTextView.setVisibility(View.GONE);
            //升级失败变为preload 等待触发重新升级
            mButton.setVisibility(View.VISIBLE);
            mButton.setStatus(OTAConstants.OTA_STATUS_PRE_LOAD);
            showUpgradeFailureDialog();
        } else if (OTAConstants.OTA_STATUS_ERROR == status) {
            showDeviceNoResponseError();
        }
    }

    /* =====================implement IOTAActivity @end =====================*/

    /* =====================OnOTAButtonClickListener @start =====================*/

    @Override
    public void onUpgradeClick() {
        if (isFinishing()) {
            return;
        }

        mHandler.requestUpdate();
    }

    @Override
    public void onFinishClick() {
        if (isFinishing()) {
            return;
        }

        this.finish();
    }

    /* =====================OnOTAButtonClickListener @end =====================*/

    /* =====================onBackClickListener @start =====================*/

    @Override
    public void onBackClick() {
        if (isFinishing()) {
            return;
        }

        this.finish();
    }
    /* =====================onBackClickListener @end =====================*/

    //=====================OnDialogButtonClickListener @start=====================

    @Override
    public void onNegativeClick(MineOTADialog dialog) {
        if (isFinishing()) {
            return;
        }

        dialog.dismiss();
        OTAActivity.this.finish();
    }

    @Override
    public void onPositiveClick(MineOTADialog dialog) {
        if (isFinishing()) {
            return;
        }

        dialog.dismiss();
    }

    //=====================OnDialogButtonClickListener @end=====================

    //=====================OnClickListener @start=====================

    @Override
    public void onClick(View v) {
        if (isFinishing()) {
            return;
        }

        if (R.id.mine_setting_ota_detail_refresh_textview == v.getId()) {
            if (null != mHandler && null != mSimpleInfo) {
                mHandler.refreshData(mSimpleInfo);
            }
        }
    }

    //=====================OnClickListener @end=====================

    //-----------------private method @start-------------------

    private void showDeviceInfoError() {
        if (isFinishing()) {
            return;
        }

        if (null != mWrapper) {
            mWrapper.setVisibility(View.VISIBLE);
        }

        if (null != mCircleImageView) {
            mCircleImageView.setVisibility(View.VISIBLE);
            mCircleImageView.setBackgroundResource(R.drawable.ilop_ota_icon_error);
        }

        if (null != mUpArrowImageView) {
            mUpArrowImageView.setVisibility(View.GONE);
        }

        if (null != mTipsTextView) {
            mTipsTextView.setTextColor(getResources().getColor(R.color.mine_color_AFB8BD));
            mTipsTextView.setText(getString(R.string.ota_get_info_error));
        }

        if (null != mCurrentVersionTextView) {
            mCurrentVersionTextView.setVisibility(View.GONE);
        }

        if (null != mRefreshTextView) {
            mRefreshTextView.setText(getString(R.string.ota_refresh));
            mRefreshTextView.setVisibility(View.VISIBLE);
        }

        if (null != mButton) {
            mButton.setVisibility(View.GONE);
        }
    }

    private void showDeviceNoResponseError() {
        if (isFinishing()) {
            return;
        }

        if (null != mWrapper) {
            mWrapper.setVisibility(View.VISIBLE);
        }

        if (null != mCircleImageView) {
            mCircleImageView.setVisibility(View.VISIBLE);
            mCircleImageView.setBackgroundResource(R.drawable.ilop_ota_icon_error);
        }

        if (null != mUpArrowImageView) {
            mUpArrowImageView.setVisibility(View.GONE);
        }

        if (null != mTipsTextView) {
            mTipsTextView.setTextColor(getResources().getColor(R.color.mine_color_AFB8BD));
            mTipsTextView.setText(getString(R.string.ota_did_upgrade_error));
        }

        if (null != mCurrentVersionTextView) {
            mCurrentVersionTextView.setVisibility(View.GONE);
        }

        if (null != mRefreshTextView) {
            mRefreshTextView.setText(getString(R.string.ota_did_upgrade_try_again));
            mRefreshTextView.setVisibility(View.VISIBLE);
        }

        if (null != mButton) {
            mButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnect(boolean isConn) {
        if (isConn) {
            mButton.setStatus(OTAConstants.OTA_STATUS_BUL_SUCCESS);
        } else {
            mButton.setStatus(OTAConstants.OTA_STATUS_BUL_FAILUER);
        }
    }

    @Override
    public void isLoading() {
        mButton.setStatus(OTAConstants.OTA_STATUS_BUL_LOADING);
    }

    //-----------------private method @end-------------------
}
