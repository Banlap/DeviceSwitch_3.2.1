package com.aliyun.iot.ilop.demo.kaiguan.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.kaiguan.activity.AboutActivity;
import com.aliyun.iot.ilop.demo.kaiguan.activity.DataGliActivity;
import com.aliyun.iot.ilop.demo.kaiguan.activity.NiChengActivity;
import com.aliyun.iot.ilop.demo.kaiguan.activity.PersonageActivity;
import com.aliyun.iot.ilop.demo.kaiguan.activity.StingActivity;
import com.aliyun.iot.ilop.demo.kaiguan.activity.WiFiActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainFragment extends Fragment implements View.OnClickListener {


    private RelativeLayout yij_layout, sting_layout, my_about_layout, wifi_layout;
    private ImageView personage_image;
    private TextView name_text_view;
    private RelativeLayout data_gl_layout;
    private CircleImageView circleImageView;
    //打开相册的请求码
    private static final int MY_ADD_CASE_CALL_PHONE2 = 7;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        yij_layout = view.findViewById(R.id.yij_layout);
        yij_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = "link://router/feedback";
                Bundle bundle = new Bundle();
                bundle.putString("mobileModel", android.os.Build.MODEL);
                bundle.putString("mobileSystem", "android-" + android.os.Build.BRAND + "-" + android.os.Build.VERSION.RELEASE);
                bundle.putString("appVersion", getAppVersionCode(getActivity()));
                //bundle.putString("key","value"); // 传入插件参数，没有参数则不需要这一行
                Router.getInstance().toUrlForResult(getActivity(), code, 1, bundle);
            }
        });
        yij_layout.setVisibility(View.GONE);
        sting_layout = view.findViewById(R.id.sting_layout);
        sting_layout.setOnClickListener(this);
        personage_image = view.findViewById(R.id.personage_image);
        personage_image.setOnClickListener(this);
        my_about_layout = view.findViewById(R.id.my_about_layout);
        my_about_layout.setOnClickListener(this);
        name_text_view = view.findViewById(R.id.name_text_view);
        name_text_view.setOnClickListener(this);
        wifi_layout = view.findViewById(R.id.wifi_layout);
        wifi_layout.setOnClickListener(this);
        data_gl_layout = view.findViewById(R.id.data_gl_layout);
        data_gl_layout.setOnClickListener(this);
        circleImageView = view.findViewById(R.id.open_vip_headicon);
        circleImageView.setOnClickListener(this);


    }

    @Override
    public void onResume() {
        super.onResume();
        if (LoginBusiness.isLogin()) {
            UserInfo userInfo = LoginBusiness.getUserInfo();
            //获取账号名称
            if ((userInfo.userNick).equals("null")) {
                name_text_view.setText("点击修改用户名称");
            } else {
                name_text_view.setText(userInfo.userNick);
            }
            //获取账号头像
            if((userInfo.userAvatarUrl).equals("")) {
                circleImageView.setImageResource(R.mipmap.yonghutouxiang);
            } else {
                //Toast.makeText(getActivity(), "uri:" + userInfo.userAvatarUrl, Toast.LENGTH_LONG).show();
                Uri selectedImage = Uri.parse(userInfo.userAvatarUrl);
                try {
                    Bitmap bitmap = null;
                    bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage));
                    //bitmap = BitmapFactory.decodeStream(getImageStream(userInfo.userAvatarUrl));
                    circleImageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public InputStream getImageStream(String path) throws Exception {
        URL url =  new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setReadTimeout( 10 *  1000);
        conn.setConnectTimeout( 10 *  1000);
        conn.setRequestMethod( " GET ");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }
        return  null;
    }

    /**
     * 返回当前程序版本号
     */
    public static String getAppVersionCode(Context context) {
        int versioncode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            // versionName = pi.versionName;
            versioncode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versioncode + "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //个人中心 - 设置
            case R.id.sting_layout:
                Intent intentSettings = new Intent(getActivity(), StingActivity.class);
                getActivity().startActivity(intentSettings);
                break;
            //个人中心 - 用户设置
            case R.id.personage_image:
                Intent intentMyAccount = new Intent(getActivity(), PersonageActivity.class);
                getActivity().startActivity(intentMyAccount);
                break;
            //个人中心 - 关于我们
            case R.id.my_about_layout:
                //Router.getInstance().toUrl(getActivity(), "page/about");
                Intent intentAboutMe = new Intent(getActivity(), AboutActivity.class);
                getActivity().startActivity(intentAboutMe);
                break;
            //个人中心 - 网络连接
            case R.id.wifi_layout:
                /*Intent intentwifi = new Intent(getActivity(), WiFiActivity.class);
                startActivity(intentwifi);*/
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
            //个人中心 - 数据管理
            case R.id.data_gl_layout:
                Intent intentData = new Intent(getActivity(), DataGliActivity.class);
                startActivity(intentData);
                break;
            //个人中心 - 点击名称 修改
            case R.id.name_text_view:
                Intent intentName = new Intent(getActivity(), NiChengActivity.class);
                startActivity(intentName);
                break;
            //个人中心 - 点击头像 修改
            case R.id.open_vip_headicon:
                choosePhoto();
                break;

        }
    }

    //选择头像
    private void choosePhoto() {
        Intent intentPicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intentPicture, 2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_ADD_CASE_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            } else {
                //"权限拒绝");
                // TODO: 2018/12/4 这里可以给用户一个提示,请求权限被拒绝了
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //设置头像
    public void setPhoto(Bitmap bitmap, Uri imageUri) {
        circleImageView.setImageBitmap(bitmap);
        changeByAccountPhoto(bitmap, imageUri);
    }
    //更新账号的头像信息
    private void changeByAccountPhoto(Bitmap bitmap, Uri imageUri) {
            Map<String, Object> map = new LinkedHashMap<>();
            //map.put("userNick", text);
            //map.put("displayName", text);
            map.put("avatarUrl", imageUri);
            OpenAccountUIService oas = OpenAccountSDK.getService(OpenAccountUIService.class);
            oas.updateProfile(getContext(), map, new LoginCallback() {
                @Override
                public void onSuccess(OpenAccountSession openAccountSession) {
                    Toast.makeText(getActivity(),"修改头像成功",Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(int i, String s) {
                    Toast.makeText(getActivity(),"修改头像失败"+s,Toast.LENGTH_SHORT).show();
                }
            });

    }


}