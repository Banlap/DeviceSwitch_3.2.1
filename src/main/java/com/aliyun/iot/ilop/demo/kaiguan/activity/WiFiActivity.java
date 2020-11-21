package com.aliyun.iot.ilop.demo.kaiguan.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.kaiguan.utils.DialogUtil;

public class WiFiActivity extends AppCompatActivity {


    private Switch wifi_switch;
    private DialogUtil dialogUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_activity);
        initView();
        initDailog();
    }

    private void initView() {
        wifi_switch=findViewById(R.id.wifi_switch);
        wifi_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (isWifiEnabled()){
                        Log.e("ssshsh",getWIFIName(WiFiActivity.this));
                        dialogUtil.show();
                    }else {
                        Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                        startActivity(wifiSettingsIntent);
                    }
                }
            }
        });
    }

    public boolean isWifiEnabled() {
        Context myContext = WiFiActivity.this;
        if (myContext == null) {
            throw new NullPointerException("Global context is null");
        }
        WifiManager wifiMgr = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            ConnectivityManager connManager = (ConnectivityManager) myContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiInfo.isConnected();
        } else {
            return false;
        }
    }


    public static String getWIFIName(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("wifiInfo", wifiInfo.toString());
        Log.d("SSID",wifiInfo.getSSID());
        return wifiInfo.getSSID();
    }

    public String getSSID() {
        Context myContext = WiFiActivity.this;
        if (myContext == null) {
            throw new NullPointerException("Global context is null");
        }
        WifiManager wm = (WifiManager) myContext.getSystemService(WIFI_SERVICE);
        if (wm != null) {
            WifiInfo winfo = wm.getConnectionInfo();
            if (winfo != null) {
                String s = winfo.getSSID();
                if (s.length() > 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                    return s.substring(1, s.length() - 1);
                }
            }
        }
        return "";
    }

    private void initDailog() {
        dialogUtil=new DialogUtil(WiFiActivity.this,R.style.custom_dialog);
        View view= LayoutInflater.from(WiFiActivity.this).inflate(R.layout.dailo_util_layout,null);
        dialogUtil.setContentView(view);
        /*TextView messga_text= view.findViewById(R.id.messga_text);
        messga_text.setText("你确定退出登录？");*/
        TextView ok_btn=view.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogUtil.hide();
            }
        });
        TextView cancel_bt= view.findViewById(R.id.cancel_bt);
        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUtil.hide();
            }
        });

    }

}
