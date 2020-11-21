package com.aliyun.iot.aep.sdk.base.delegate.page;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.alibaba.sdk.android.openaccount.ui.callback.EmailRegisterCallback;
import com.alibaba.sdk.android.openaccount.ui.callback.EmailResetPasswordCallback;
import com.alibaba.sdk.android.openaccount.ui.ui.LoginActivity;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.dialog.ASlideDialog;

/**
 * Created by feijie.xfj on 2018/6/26.
 */

public class DemoLoginActivity extends LoginActivity {

    // 显示手机号/邮箱 注册，改密入口
    ASlideDialog registerDialog, forgetPwdDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getNextStepButtonWatcher().setNextStepButton((Button) findViewById(R.id.next));

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountShowMenuDialog();
            }
        });

        findViewById(R.id.reset_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgetPwdShowMenuDialog();
            }
        });

        //隐藏三方登录按钮
        findViewById(R.id.oauth).setVisibility(View.GONE);
    }

    public void emailRegister(View view) {
        OpenAccountUIService openAccountService = OpenAccountSDK.getService(OpenAccountUIService.class);
        try {
            openAccountService.showEmailRegister(this, new EmailRegisterCallback() {

                @Override
                public void onSuccess(OpenAccountSession session) {
                    LoginCallback callback = getLoginCallback();
                    if (callback != null) {
                        callback.onSuccess(session);
                    }
                    finishWithoutCallback();
                }

                @Override
                public void onFailure(int code, String message) {
                    LoginCallback callback = getLoginCallback();
                    if (callback != null) {
                        callback.onFailure(code, message);
                    }
                }

                @Override
                public void onEmailSent(String email) {
                    Toast.makeText(getApplicationContext(), email + " 已经发送了", Toast.LENGTH_LONG).show();
                }

            });
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }
    }


    public void emailResetPassword(View view) {
        OpenAccountUIService openAccountService = OpenAccountSDK.getService(OpenAccountUIService.class);
        try {
            openAccountService.showEmailResetPassword(this, new EmailResetPasswordCallback() {


                @Override
                public void onSuccess(OpenAccountSession session) {
                    LoginCallback callback = getLoginCallback();
                    if (callback != null) {
                        callback.onSuccess(session);
                    }
                    finishWithoutCallback();
                }


                @Override
                public void onFailure(int code, String message) {
                    LoginCallback callback = getLoginCallback();
                    if (callback != null) {
                        callback.onFailure(code, message);
                    }
                }

                @Override
                public void onEmailSent(String email) {
                    Toast.makeText(getApplicationContext(), email + " 已经发送了", Toast.LENGTH_LONG).show();
                }

            });
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }
    }

    private void accountShowMenuDialog() {
        if (registerDialog == null) {
            registerDialog = ASlideDialog.newInstance(this, ASlideDialog.Gravity.Bottom, R.layout.menu_dialog_login);
            ((TextView) registerDialog.findViewById(R.id.menu_op1_textview)).setText("手机注册");
            ((TextView) registerDialog.findViewById(R.id.menu_op2_textview)).setText("邮箱注册");
            registerDialog.findViewById(R.id.menu_op1_textview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerUser(view);
                    registerDialog.dismiss();
                }
            });
            registerDialog.findViewById(R.id.menu_op2_textview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    emailRegister(view);
                    registerDialog.dismiss();
                }
            });

            registerDialog.findViewById(R.id.menu_cancel_textview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerDialog.dismiss();
                }
            });

            registerDialog.setCanceledOnTouchOutside(true);
        }

        registerDialog.show();
    }

    private void forgetPwdShowMenuDialog() {
        if (forgetPwdDialog == null) {
            forgetPwdDialog = ASlideDialog.newInstance(this, ASlideDialog.Gravity.Bottom, R.layout.menu_dialog_login);
            ((TextView) forgetPwdDialog.findViewById(R.id.menu_op1_textview)).setText("手机号码密码找回");
            ((TextView) forgetPwdDialog.findViewById(R.id.menu_op2_textview)).setText("邮箱密码找回");
            forgetPwdDialog.findViewById(R.id.menu_op1_textview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    forgetPassword(view);
                    forgetPwdDialog.dismiss();
                }
            });
            forgetPwdDialog.findViewById(R.id.menu_op2_textview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    emailResetPassword(view);
                    forgetPwdDialog.dismiss();
                }
            });
            forgetPwdDialog.findViewById(R.id.menu_cancel_textview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    forgetPwdDialog.dismiss();
                }
            });
            forgetPwdDialog.setCanceledOnTouchOutside(true);
        }

        forgetPwdDialog.show();
    }


}
