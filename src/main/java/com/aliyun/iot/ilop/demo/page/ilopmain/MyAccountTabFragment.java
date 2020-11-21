package com.aliyun.iot.ilop.demo.page.ilopmain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.dialog.ASlideDialog;
import com.aliyun.iot.ilop.demo.page.language.LanguageSettingActivity;
import com.aliyun.iot.ilop.demo.page.main.StartActivity;
import com.aliyun.iot.ilop.page.mine.MineConstants;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.activity.TripartitePlatformListActivity;

import org.jetbrains.annotations.NotNull;


public class MyAccountTabFragment extends Fragment {
    private View myInfoView;

    private View myAboutView;

    private View myOTAView;
    private View mMessgae;

    private View mFeedback;

    private TextView myUserNameTV;

    // account
    ASlideDialog menuDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.myaccounttab_fragment_layout, null);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myInfoView = view.findViewById(R.id.my_userinfo);
        myAboutView = view.findViewById(R.id.my_about);
        myUserNameTV = view.findViewById(R.id.my_username_textview);
        myOTAView = view.findViewById(R.id.my_ota);
        mMessgae = view.findViewById(R.id.my_message);
        mFeedback = view.findViewById(R.id.my_feedback);

        view.findViewById(R.id.my_thd).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TripartitePlatformListActivity.class);
            startActivity(intent);
        });

        myAboutView.setOnClickListener(v -> Router.getInstance().toUrl(getActivity(), "page/about"));

        myOTAView.setOnClickListener(v -> Router.getInstance().toUrl(getActivity(), "page/ota/list"));

        mMessgae.setOnClickListener(v -> {
            //调用消息插件
            String code = "link://router/devicenotices";
            Bundle bundle = new Bundle();
            Router.getInstance().toUrlForResult(getActivity(), code, 1, bundle);
        });

        mFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用意见反馈插件
                Bundle bundle = new Bundle();
                //手机型号
                bundle.putString("mobileModel", Build.MODEL);
                //手机系统
                bundle.putString("mobileSystem", android.os.Build.VERSION.RELEASE);
                bundle.putString("appVersion", String.valueOf(getPackageInfo(MyAccountTabFragment.this.getActivity()).versionName));
                Activity activity = (Activity) view.getContext();
                Router.getInstance().toUrlForResult(getActivity(), MineConstants.MINE_URL_FEEDBACK, 1, bundle);
            }
        });


        myInfoView.setOnClickListener(v -> {
            if (!LoginBusiness.isLogin()) {
                LoginBusiness.login(new ILoginCallback() {
                    @Override
                    public void onLoginSuccess() {
                        myUserNameTV.setText(getUserNick());
                    }

                    @Override
                    public void onLoginFailed(int i, String s) {
                        Toast.makeText(getActivity(), getString(R.string.account_login_failed), Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            accountShowMenuDialog();
        });
        String userName = getUserNick();
        myUserNameTV.setText(userName);

        view.findViewById(R.id.my_language).setOnClickListener(view1 -> LanguageSettingActivity.start(view1.getContext()));
    }


    private String getUserNick() {
        //设置当前登录用户名
        if (LoginBusiness.isLogin()) {
            UserInfo userInfo = LoginBusiness.getUserInfo();
            String userName = "";
            if (userInfo != null) {
                if (!TextUtils.isEmpty(userInfo.userNick) && !"null".equalsIgnoreCase(userInfo.userNick)) {
                    userName = userInfo.userNick;
                } else if (!TextUtils.isEmpty(userInfo.userPhone)) {
                    userName = userInfo.userPhone;
                } else if (!TextUtils.isEmpty(userInfo.userEmail)) {
                    userName = userInfo.userEmail;
                } else if (!TextUtils.isEmpty(userInfo.openId)) {
                    userName = userInfo.openId;
                } else {
                    userName = "未获取到用户名";
                }
            }
            return userName;
        }
        return null;
    }

    private void accountShowMenuDialog() {
        if (menuDialog == null) {
            menuDialog = ASlideDialog.newInstance(getContext(), ASlideDialog.Gravity.Bottom, R.layout.menu_dialog);
            menuDialog.findViewById(R.id.menu_logout_textview).setOnClickListener(v -> {

                LoginBusiness.logout(new ILogoutCallback() {
                    @Override
                    public void onLogoutSuccess() {
                        Toast.makeText(getActivity(), getString(R.string.account_logout_success), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity().getApplicationContext(), StartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        if (menuDialog != null) {
                            menuDialog.dismiss();
                        }
                        getActivity().finish();
                        getActivity().overridePendingTransition(0, 0);
                    }

                    @Override
                    public void onLogoutFailed(int code, String error) {
                        Toast.makeText(getActivity(), getString(R.string.account_logout_failed) + error, Toast.LENGTH_SHORT);
                    }
                });
                accountHideMenuDialog();
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
        ((TextView) menuDialog.findViewById(R.id.menu_name_textview)).setText(getUserNick());
        menuDialog.show();
    }

    private void accountHideMenuDialog() {
        if (menuDialog != null) {
            menuDialog.hide();
        }
    }

    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }
}
