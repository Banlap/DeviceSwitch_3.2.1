package com.aliyun.iot.ilop.demo.page.ota.view;

import android.app.Dialog;
import android.content.Context;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aliyun.iot.demo.R;

/**
 * Created by david on 2018/4/11.
 *
 * @author david
 * @date 2018/04/11
 */
public class MineOTADialog extends Dialog implements OnClickListener {
    private TextView mTitle;
    private Button mNegativeButton, mPositiveButton;

    private OnDialogButtonClickListener mListener;

    public MineOTADialog(@NonNull Context context) {
        super(context, R.style.MineDialog);
        init();
    }

    private void init() {
        View root = getLayoutInflater().inflate(R.layout.ilop_ota_dialog, null);
        setContentView(root);

        mTitle = root.findViewById(R.id.mine_dialog_title_textview);
        mNegativeButton = root.findViewById(R.id.mine_dialog_negative_button);
        mPositiveButton = root.findViewById(R.id.mine_dialog_positive_button);

        mNegativeButton.setOnClickListener(this);
        mPositiveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (R.id.mine_dialog_negative_button == v.getId()) {
            if (null != mListener) {
                mListener.onNegativeClick(this);
            }
        } else if (R.id.mine_dialog_positive_button == v.getId()) {
            if (null != mListener) {
                mListener.onPositiveClick(this);
            }
        }
    }

    public void setTitle(String title) {
        if (null != mTitle && !TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
    }

    public void setNegativeButtonText(String negativeTips) {
        if (null != mNegativeButton) {
            mNegativeButton.setText(negativeTips);
        }
    }

    public void setPositiveButtonText(String positiveTips) {
        if (null != mPositiveButton) {
            mPositiveButton.setText(positiveTips);
        }
    }

    public void setOnDialogButtonClickListener(OnDialogButtonClickListener listener) {
        mListener = listener;
    }

    public interface OnDialogButtonClickListener {
        /**
         * negative 按钮点击回调
         * @param dialog
         */
        void onNegativeClick(MineOTADialog dialog);

        /**
         * positive 按钮点击回调
         * @param dialog
         */
        void onPositiveClick(MineOTADialog dialog);
    }

}
