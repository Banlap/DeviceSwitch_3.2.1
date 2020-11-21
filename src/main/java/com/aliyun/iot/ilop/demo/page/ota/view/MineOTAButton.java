package com.aliyun.iot.ilop.demo.page.ota.view;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;

import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.ota.OTAConstants;

/**
 * Created by david on 2018/4/10.
 *
 * @author david
 * @date 2018/04/10
 */
public class MineOTAButton extends FrameLayout implements AnimatorUpdateListener, OnClickListener {
    private static final String TAG = "MineOTAButton";

    private static final int PROGRESS_START_ANGEL = -90;
    private static final int PROGRESS_END_ANGEL = 270;
    private static final int PROGRESS_CIRCLE_DURATION = 500;

    private ValueAnimator mRotateAnimator;

    private RelativeLayout mContainer;
    private ImageView mProgress;
    private TextView mMessage;

    private int mStatus;

    private OnOTAButtonClickListener mListener;

    public MineOTAButton(@NonNull Context context) {
        super(context);
        init();
    }

    public MineOTAButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MineOTAButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        ALog.d(TAG, "onDetachedFromWindow");

        if (null != mRotateAnimator) {
            if (mRotateAnimator.isRunning()) {
                mRotateAnimator.cancel();
            }
            mRotateAnimator.removeAllUpdateListeners();
            mRotateAnimator = null;
        }

        if (null != mListener) {
            mListener = null;
        }
    }

    private void init() {
        inflate(getContext(), R.layout.ilop_ota_view_button, this);
        mContainer = (RelativeLayout) findViewById(R.id.mine_button_ota_container_relativelayout);
        mProgress = (ImageView) findViewById(R.id.mine_button_ota_progress_imageview);
        mMessage = (TextView) findViewById(R.id.mine_button_ota_msg_textview);

        mContainer.setOnClickListener(this);

        mStatus = OTAConstants.OTA_STATUS_PRE_LOAD;
        setStatus(mStatus);
    }

    /**
     * 开始旋转动画
     */
    private void startAnimation() {
        if (null == mRotateAnimator) {

            mRotateAnimator = ValueAnimator.ofInt(PROGRESS_START_ANGEL, PROGRESS_END_ANGEL);
            mRotateAnimator.setDuration(PROGRESS_CIRCLE_DURATION);
            mRotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mRotateAnimator.setInterpolator(new LinearInterpolator());
        }

        stopAnimation();

        mRotateAnimator.addUpdateListener(this);
        mRotateAnimator.start();
    }

    /**
     * 停止旋转动画
     */
    private void stopAnimation() {
        if (null == mRotateAnimator) {
            return;
        }

        if (mRotateAnimator.isRunning()) {
            mRotateAnimator.cancel();
            mRotateAnimator.removeAllUpdateListeners();
        }
    }

    /**
     * 设置button状态
     *
     * @param status @see {@link OTAConstants#OTA_STATUS_PRE_LOAD,OTAConstants#OTA_STATUS_LOADING,OTAConstants#OTA_STATUS_DONE}
     */
    public void setStatus(int status) {
        if (OTAConstants.OTA_STATUS_PRE_LOAD == status) {
            mContainer.setActivated(true);
            mProgress.setVisibility(GONE);
            mMessage.setText(getContext().getString(R.string.ota_pre_upgrade));
            stopAnimation();
            mStatus = OTAConstants.OTA_STATUS_PRE_LOAD;
        } else if (OTAConstants.OTA_STATUS_LOADING == status) {
            mContainer.setActivated(false);
            mProgress.setVisibility(VISIBLE);
            mMessage.setText(getContext().getString(R.string.ota_doing_upgrade));
            startAnimation();
            mStatus = OTAConstants.OTA_STATUS_LOADING;
        } else if (OTAConstants.OTA_STATUS_DONE == status) {
            mContainer.setActivated(true);
            mProgress.setVisibility(GONE);
            mMessage.setText(getContext().getString(R.string.ota_did_upgrade));
            stopAnimation();
            mStatus = OTAConstants.OTA_STATUS_DONE;
        } else if (OTAConstants.OTA_STATUS_BUL_FAILUER == status) {
            mContainer.setActivated(true);
            mProgress.setVisibility(GONE);
            mMessage.setText("蓝牙连接失败,请重试");
            stopAnimation();
            mStatus = OTAConstants.OTA_STATUS_BUL_FAILUER;
        } else if (status == OTAConstants.OTA_STATUS_BUL_LOADING) {
            mContainer.setActivated(false);
            mProgress.setVisibility(VISIBLE);
            mMessage.setText("蓝牙连接中...");
            startAnimation();
            mStatus = OTAConstants.OTA_STATUS_BUL_LOADING;
        } else if (OTAConstants.OTA_STATUS_BUL_SUCCESS == status) {
            mContainer.setActivated(false);
            mProgress.setVisibility(VISIBLE);
            mMessage.setText(getContext().getString(R.string.ota_doing_upgrade));
            mStatus = OTAConstants.OTA_STATUS_LOADING;
        }
        ALog.d(TAG, "Status:" + status);
    }

    public void setOnOTAButtonClickListener(OnOTAButtonClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (null != mProgress) {
            mProgress.setRotation((int) animation.getAnimatedValue());
        }
    }

    @Override
    public void onClick(View v) {
        if (null == mListener) {
            return;
        }

        ALog.d(TAG, "status = " + mStatus + ", do onClick");
        if (OTAConstants.OTA_STATUS_PRE_LOAD == mStatus || OTAConstants.OTA_STATUS_BUL_FAILUER == mStatus) {
            mListener.onUpgradeClick();
        } else if (OTAConstants.OTA_STATUS_DONE == mStatus) {
            mListener.onFinishClick();
        } else {

        }
    }

    public interface OnOTAButtonClickListener {
        /**
         * 点击立刻升级时回调
         */
        void onUpgradeClick();

        /**
         * 点击升级完成时回调
         */
        void onFinishClick();
    }
}
