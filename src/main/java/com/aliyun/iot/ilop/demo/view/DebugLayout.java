package com.aliyun.iot.ilop.demo.view;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.aliyun.alink.alirn.dev.BoneDevHelper;
import com.aliyun.alink.linksdk.tools.ThreadTools;
import com.aliyun.iot.aep.component.bundlemanager.BundleManager;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.ilop.demo.dialog.ASlideDialog;
import com.aliyun.iot.ilop.demo.page.main.StartActivity;
import com.aliyun.iot.aep.sdk.EnvConfigure;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iot.demo.R;

import java.util.regex.Pattern;

/**
 * 调试页面整合组件
 * <p/>
 * 提供了基于Style的定制功能
 * TODO:尚未提供基于SDK的定制功能
 */
public class DebugLayout extends LinearLayout {

    //===========================
    // top bar.begin
    //===========================
    ImageView backImageView;
    TextView titleTextView;
    TextView menuTextView;
    //===========================
    // top bar.end
    //===========================

    //===========================
    // sdk list.begin
    //===========================
    LinearLayout sdkListLinearLayout;

    // bone mobile rn
    String bonePluginAPIDemo;
    String bonePluginUIDemo;

    // account
    ASlideDialog menuDialog;
    LocalBroadcastManager localBroadcastManager;
    IntentFilter intentFilter;
    LoginChangeReceiver loginChangeReceiver;



    //===========================
    // sdk list.end
    //===========================

    public DebugLayout(Context context) {
        this(context, null);
    }

    public DebugLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DebugLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // must be vertical
        setOrientation(LinearLayout.VERTICAL);

        findViews(context);
        readAttrs(context, attrs, defStyleAttr);
        inflateSDKs(context);
    }

    private void findViews(Context context) {
        View.inflate(context, R.layout.debug_layout_view, this);

        // title bar
        backImageView = findViewById(R.id.topbar_back_imageview);
        titleTextView = findViewById(R.id.topbar_title_textview);
        menuTextView = findViewById(R.id.topbar_menu_textview);

        // sdk list
        sdkListLinearLayout = findViewById(R.id.sdk_list);
    }

    private void readAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.DebugLayout, defStyleAttr, 0);

        String title = typeArray.getString(R.styleable.DebugLayout_title);
        titleTextView.setText(title);

        float titleFontSize = typeArray.getDimension(R.styleable.DebugLayout_titleFontSize, 0);
        if (titleFontSize > 0) {
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleFontSize);
        }

        bonePluginAPIDemo = typeArray.getString(R.styleable.DebugLayout_bonePluginAPIDemo);
        bonePluginUIDemo = typeArray.getString(R.styleable.DebugLayout_bonePluginUIDemo);
    }

    private void inflateSDKs(final Context context) {

        // Bone mobile rn container
        {
            IndexItemView boneMobileSDK = new IndexItemView(context);
            boneMobileSDK.setTitle("BoneMobile 插件");

            boneMobileSDK.addAction("环境切换", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Context context = v.getContext();

                    final Dialog dialog = ASlideDialog.newInstance(context, ASlideDialog.Gravity.Bottom, R.layout.rncontainer_env_dialog);
                    dialog.findViewById(R.id.rncontainer_env_release_textview).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boneMobileHandleEnv(context, "release");
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.rncontainer_env_test_textview).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boneMobileHandleEnv(context, "test");
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.menu_cancel_textview).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();

                }
            });
            boneMobileSDK.addAction("本地调试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context context = v.getContext();
                    final String ip = BoneDevHelper.readBoneDebugServer(context);
                    String port = BoneDevHelper.getBoneDebugServerPort(context);
                    final Dialog dialog = ASlideDialog.newInstance(context, ASlideDialog.Gravity.Center, R.layout.rncontainer_debug_ip_dialog);

                    final EditText editText = dialog.findViewById(R.id.rncontainer_debug_ip_edittext);
                    editText.setText(ip);
                    final EditText portText = dialog.findViewById(R.id.rncontainer_debug_port_edittext);
                    portText.setText(port);


                    dialog.findViewById(R.id.rncontainer_debug_ip_ok_textview).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boneMobileHandleIP(editText.getText().toString(), portText.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.rncontainer_debug_ip_cancel_textview).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();


                }
            });
            String env = "生产环境";
            if ("test".equalsIgnoreCase(BoneDevHelper.readPluginEnv(getContext(), "test"))) {
                env = "开发环境";
            }
            boneMobileSDK.setStatus(env);
            sdkListLinearLayout.addView(boneMobileSDK);
        }

        // api client
        {
            IndexItemView apiGate = new IndexItemView(getContext());
            apiGate.setTitle("API 通道");
            apiGate.addAction("调试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Router.getInstance().toUrl(getContext(), "page/apiClient");
                }
            });
            sdkListLinearLayout.addView(apiGate);
        }

        // channel
        {
            IndexItemView socketSDK = new IndexItemView(getContext());
            socketSDK.setTitle("长连接通道");
            socketSDK.addAction("调试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Router.getInstance().toUrl(getContext(), "page/channel");
                }
            });
            sdkListLinearLayout.addView(socketSDK);
        }

        // account
        {
            IndexItemView openAccount = new IndexItemView(context);
            openAccount.setTitle("账号和用户");
            openAccount.addAction("界面展示", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Router.getInstance().toUrl(context, "page/login");
                }
            });
            sdkListLinearLayout.addView(openAccount);

            accountInitBroadcastReceiver();
            accountRefreshUILogo();

            ImageView leftIcon = findViewById(R.id.topbar_back_imageview);
            leftIcon.setVisibility(GONE);
            leftIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 左上角头像
                    if (LoginBusiness.isLogin()) {
                        accountShowMenuDialog();
                    } else {
                        accountLogin();
                    }
                }
            });
        }

        // push
        {
            IndexItemView mobilePush = new IndexItemView(getContext());
            mobilePush.setTitle("移动推送SDK");
            mobilePush.addAction("查看DeviceID", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String deviceId = PushServiceFactory.getCloudPushService().getDeviceId();
                    if (TextUtils.isEmpty(deviceId)) {
                        deviceId = "没有获取到";
                    }
                    EnvConfigure.putEnvArg(EnvConfigure.KEY_DEVICE_ID, deviceId);

                    Router.getInstance().toUrl(getContext(), "page/about");
                }
            });
            sdkListLinearLayout.addView(mobilePush);
        }
    }

    //===========================
    // helper methods.start
    //===========================

    private void boneMobileHandleEnv(Context context, String pluginEnv) {

        if (TextUtils.equals(pluginEnv, BoneDevHelper.readPluginEnv(context, "test"))) {
            String message = "test".equalsIgnoreCase(pluginEnv) ? "当前已经是开发环境" : "当前已经是生产环境";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            return;
        }

        BoneDevHelper.savetPluginEnv(context, pluginEnv);
       /* // 清空文件缓存
        new BundleManager().destroy();
        // 清空路由信息
        PluginConfigManager.getInstance().cleanConfig();*/

        Toast.makeText(context, "环境设置成功，应用将在3秒钟后自动关闭，请重新启动应用", Toast.LENGTH_SHORT).show();

        ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
            @Override
            public void run() {

                Intent mStartActivity = new Intent(getContext(), StartActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(getContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                mgr.setExact(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);

            }
        }, 3000);
    }

    private void boneMobileHandleIP(String ip, String port) {

        // check ip
        boolean match = Pattern.matches("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))", ip);

        if (!match) {
            Toast.makeText(getContext(), R.string.rncontainer_illeagel_ip_address, Toast.LENGTH_SHORT).show();
            return;
        }

        final Context context = getContext();

        BoneDevHelper.setDebugServerAndPort(context, ip, Integer.valueOf(port));

        new BoneDevHelper().getBundleInfoAsync(context, ip, port, new BoneDevHelper.OnBondBundleInfoGetListener() {
            @Override
            public void onSuccess(BoneDevHelper.BoneBundleInfo boneBundleInfo) {
                BoneDevHelper.RouterInfo info = new BoneDevHelper().handleBundleInfo(context, boneBundleInfo);

                if (null == info) {
                    return;
                }

                Router.getInstance().toUrl(context, info.url, info.bundle);
            }

            @Override
            public void onError(String message, Exception e) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                if (null != e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void accountInitBroadcastReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        intentFilter = new IntentFilter();
        intentFilter.addAction(LoginBusiness.LOGIN_CHANGE_ACTION);
        loginChangeReceiver = new LoginChangeReceiver();
        localBroadcastManager.registerReceiver(loginChangeReceiver, intentFilter);
    }

    private void accountRefreshUILogo() {
        if (!LoginBusiness.isLogin()) {
            backImageView.setImageResource(R.drawable.avatar_default_svg);
        } else {
            backImageView.setImageResource(R.drawable.avatar_login_svg);
        }
    }

    private void accountShowMenuDialog() {
        if (menuDialog == null) {
            menuDialog = ASlideDialog.newInstance(getContext(), ASlideDialog.Gravity.Bottom, R.layout.menu_dialog);
            menuDialog.findViewById(R.id.menu_logout_textview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginBusiness.logout(new ILogoutCallback() {
                        @Override
                        public void onLogoutSuccess() {
                            toast(getContext().getString(R.string.account_logout_success));
                            accountRefreshUILogo();
                        }

                        @Override
                        public void onLogoutFailed(int code, String error) {
                            toast(getContext().getString(R.string.account_logout_failed) + error);
                        }
                    });
                    accountHideMenuDialog();
                }
            });
            menuDialog.findViewById(R.id.menu_cancel_textview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accountHideMenuDialog();
                }
            });
            menuDialog.setCanceledOnTouchOutside(true);
        }
        //设置当前登录用户名
        if (LoginBusiness.isLogin()) {
            UserInfo userInfo = LoginBusiness.getUserInfo();
            String userName = "";
            if (userInfo != null) {
                userName = userInfo.userNick;
                if (TextUtils.isEmpty(userName)) {
                    userName = userInfo.userPhone;
                    if (TextUtils.isEmpty(userName)) {
                        userName = "未获取到用户名";
                    }
                }
                ((TextView) menuDialog.findViewById(R.id.menu_name_textview)).setText(userName);
            }

        }

        menuDialog.show();
    }

    private void accountHideMenuDialog() {
        if (menuDialog != null) {
            menuDialog.hide();
        }
    }

    private void accountLogin() {
        LoginBusiness.login(new ILoginCallback() {
            @Override
            public void onLoginSuccess() {
                toast(getContext().getString(R.string.account_login_success));
                accountRefreshUILogo();
            }

            @Override
            public void onLoginFailed(int code, String error) {
                toast(getContext().getString(R.string.account_login_failed) + error);
            }
        });
    }

    private void toast(final String toast) {
        ThreadTools.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
    //===========================
    // helper method.start
    //===========================

    //===========================
    // helper classes.start
    //===========================
    private class LoginChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            accountRefreshUILogo();
        }
    }

    //===========================
    // helper classes.end
    //===========================
}
