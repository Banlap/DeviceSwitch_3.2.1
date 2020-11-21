package com.aliyun.iot.ilop.demo.kaiguan.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.aliyun.alink.linksdk.tmp.device.panel.PanelDevice;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.kaiguan.bean.ActionSetLightSwitch;
import com.aliyun.iot.ilop.demo.kaiguan.bean.FacilityListBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.LightSwitchBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.SceneDetailsBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.ScenesBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.ScenesListBean;
import com.aliyun.iot.ilop.demo.page.bean.DeviceInfoBean;
import com.aliyun.iot.ilop.demo.page.ilopmain.MainActivity;
import com.aliyun.iot.ilop.demo.utils.DialogTools;
import com.aliyun.iot.ilop.demo.utils.TcpConnection;
import com.aliyun.iot.ilop.demo.utils.WifiConnectManager;
import com.aliyun.iot.link.ui.component.LinkToast;
import com.google.android.gms.common.util.JsonUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class FacilityDetailsActivity extends AppCompatActivity {

    private ImageView mivSwitch;
    private TextView mDeviceName;
    private TextView mDeviceNo;
    private TextView mDeviceSwitch;
    private TextView mDeviceStatus;
    private TextView mWifiMode;
    private TextView mWifiSSID;
    private TextView mWifiPass;
    private TextView mLocalTimer;

    private TextView mNetworkClick;
    private TextView mOkClick;
    WifiManager wifiManager2;
    WifiConnectManager mWifiConnect2 = null;     // wifi连接管理

    private String mDeviceIp = null;                //获取当前wifi IP
    private String mQrCodeInfo = null;              //传递qr码信息
    private String mDeviceInfo = null;              //传递列表设备信息
    private String mDeviceMsgCallback = "";         //用于回调直连信息
    private String mDeviceNetworkCallback = "";     //用于回调配网信息
    private boolean mIsSwitch = true;               //控制开关判断
    private boolean mDirectWifi = true;             //判断直连控制还是局域网控制

    private String deviceName =null;
    private String deviceNo = null;
    private String wifiSSID = null;
    private String wifiPass = null;
    private boolean mIsSendWifiInfo = false;

    private Handler handler;
    private final int FINDSCENE=111;
    private final int FINDDATAINFO=222;
    private final int FINDTHINGSINFO=333;
    private final int SUCCESSSET=888;

    private String mIotId = null;
    private int mLightSwitchValue = 0;
    private int mLightSwitchTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparencyBar(FacilityDetailsActivity.this);
        setContentView(R.layout.activity_facility_details);
        initHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initWifiManager();
        init();
    }

    private void initWifiManager() {
        wifiManager2 = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiConnect2 = new WifiConnectManager(wifiManager2);
    }

    private void init() {
        mivSwitch = findViewById(R.id.facility_iv_switch);
        mDeviceName = findViewById(R.id.facility_tv_deviceName);
        mDeviceNo = findViewById(R.id.facility_tv_deviceNo);
        mDeviceSwitch = findViewById(R.id.facility_tv_deviceSwitch);
        mDeviceStatus = findViewById(R.id.facility_tv_deviceStatus);
        mWifiMode = findViewById(R.id.facility_tv_wifiMode);
        mWifiSSID = findViewById(R.id.facility_tv_wifiSSID);
        mWifiPass = findViewById(R.id.facility_tv_wifiPass);
        mLocalTimer = findViewById(R.id.tv_localTimer);

        mNetworkClick = findViewById(R.id.facility_tv_network_config);
        mOkClick = findViewById(R.id.add_facility_text);
        //获取intent传递数据
        SceneDetailsBean sceneDetailsBean = (SceneDetailsBean) getIntent().getSerializableExtra("normalDetail");
        FacilityListBean facilityListBean = (FacilityListBean) getIntent().getSerializableExtra("facilityDetail");
        mQrCodeInfo = getIntent().getStringExtra("qrCodeInfo");
        mDirectWifi = getIntent().getBooleanExtra("directWifi",true);


        //通过扫码传递信息
        if(mQrCodeInfo!=null){
            //deviceName = (mQrCodeInfo.substring(mQrCodeInfo.indexOf("DeviceName:"), mQrCodeInfo.indexOf("DeviceSecre:"))).substring(11);
            //deviceNo = (mQrCodeInfo.substring(0, mQrCodeInfo.indexOf("DeviceName:"))).substring(11);
            deviceName = (mQrCodeInfo.substring(mQrCodeInfo.indexOf("&pk="), mQrCodeInfo.indexOf("&dn="))).substring(4);
            deviceNo = (mQrCodeInfo.substring(mQrCodeInfo.indexOf("&dn=:"), mQrCodeInfo.indexOf("&HotWifiInfo:"))).substring(3);

            wifiSSID  = (mQrCodeInfo.substring(mQrCodeInfo.indexOf("ssid:"), mQrCodeInfo.indexOf("password:"))).substring(5);
            wifiPass = (mQrCodeInfo.substring(mQrCodeInfo.indexOf("password:"))).substring(9);

            mDeviceName.setText(deviceName);
            mDeviceNo.setText(deviceNo);
            mWifiSSID.setText("");
            mWifiPass.setText("");
            //mWifiSSID.setText(wifiSSID);
            //mWifiPass.setText(wifiPass);

            mDeviceSwitch.setText("关闭");
        } else {
            mDirectWifi = false;
        }

        //场景里点击列表传递信息
        if (sceneDetailsBean !=null) {
            mDeviceName.setText(sceneDetailsBean.getDeviceNickName());
            mDeviceNo.setText(sceneDetailsBean.getProductKey());

            mDeviceStatus.setText("在线");
            mDeviceSwitch.setText("关闭");

            mWifiSSID.setText("");
            mWifiPass.setText("");
            //Toast.makeText(FacilityDetailsActivity.this, "jsonObject: " , Toast.LENGTH_LONG).show();
            listByAccount();
        }

        //设备列表点击传递信息
        if(facilityListBean != null) {
            mDeviceName.setText(facilityListBean.getDeviceName());
            mDeviceNo.setText(facilityListBean.getProductKey());

            if(facilityListBean.getStatus() == 1) {
                mDeviceStatus.setText("在线");
            } else if (facilityListBean.getStatus() == 0) {
                mDeviceStatus.setText("未激活");
            } else if (facilityListBean.getStatus() == 3) {
                mDeviceStatus.setText("离线");
            } else if (facilityListBean.getStatus() == 8) {
                mDeviceStatus.setText("禁用");
            }

            mDeviceSwitch.setText("关闭");
            mWifiSSID.setText("");
            mWifiPass.setText("");

            listByAccount();
        }
        //本地计时器
        mLocalTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String code = "link://router/localTimer";
                //Bundle bundle = new Bundle();
                //bundle.putString("iotId",mIotId);
                //Router.getInstance().toUrlForResult(FacilityDetailsActivity.this, code, 1, bundle);

                Intent intentTime=new Intent(FacilityDetailsActivity.this, TimingListActivity.class);
                startActivityForResult(intentTime,  2);

            }
        });

        //banlap： 判断直连模式 还是 互联网模式
        if(mDirectWifi) {
            mWifiMode.setText("Wifi-直连");
            mNetworkClick.setVisibility(View.VISIBLE);
            //banlap： wifi密码 明文显示
            mWifiPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            TcpConnection tcpConnection = TcpConnection.getInstance();
            //wifiManager2 = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            mDeviceIp = intToIp(wifiManager2.getDhcpInfo().serverAddress);
            Toast.makeText(FacilityDetailsActivity.this, "ip: " + mDeviceIp, Toast.LENGTH_SHORT).show();

            tcpConnection.init(getApplicationContext());
            tcpConnection.connectToDevice(mDeviceIp);

            //banlap：  回调连接消息
            tcpConnection.getDeviceMsg(new TcpConnection.CallBack() {
                @Override
                public void deviceMsg(String msg) {
                    mDeviceMsgCallback = msg;
                }
            });

            //banlap：  控制开关
            mivSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deviceSwitch(mIsSwitch, tcpConnection, mDirectWifi, mDeviceIp);
                }
            });

            //banlap： 配网按钮
            mNetworkClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //mWifiConnect2.connect("82-7D-3A-5", wifiPass, WifiConnectManager.WifiCipherType.WIFICIPHER_WPA);

                    if(mWifiSSID.getText().toString().equals("") || mWifiPass.getText().toString().equals("") ){
                        Toast.makeText(FacilityDetailsActivity.this, "请填写需配网的WIFI名称和密码" , Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FacilityDetailsActivity.this, "正在为设备发送配网信息..." , Toast.LENGTH_LONG).show();
                        setWifiInfo(tcpConnection, mDeviceIp, mWifiSSID.getText().toString(), mWifiPass.getText().toString());

                        //断开当前设备发出热点
                        /*int removeNetID = wifiManager2.getConnectionInfo().getNetworkId();
                        wifiManager2.removeNetwork(removeNetID);
                        wifiManager2.disconnect();
                        wifiManager2.reconnect();
                        finish();*/
                    }

                }
            });
            //banlap： wifi账号输入
            mWifiSSID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View viewEdit = getLayoutInflater().inflate(R.layout.popwindow_edit_wifi_ssid, null);
                    EditText editWifiSSID = viewEdit.findViewById(R.id.popw_et_wifiSSID);
                    editWifiSSID.setText(mWifiSSID.getText());
                    DialogTools mDialogTools = new DialogTools(FacilityDetailsActivity.this, 0, 0, viewEdit, R.style.DialogTheme);
                    mDialogTools.setCancelable(true);
                    mDialogTools.show();

                    mDialogTools.findViewById(R.id.popw_tv_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mWifiSSID.setText(editWifiSSID.getText());
                            mDialogTools.hide();
                        }
                    });
                    mDialogTools.findViewById(R.id.popw_tv_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDialogTools.hide();
                        }
                    });
                }
            });
            //banlap： wifi密码输入
            mWifiPass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View viewEdit = getLayoutInflater().inflate(R.layout.popwindow_edit_wifi_pass, null);
                    EditText editWifiPass = viewEdit.findViewById(R.id.popw_et_wifiPass);
                    editWifiPass.setText(mWifiPass.getText());
                    DialogTools mDialogTools = new DialogTools(FacilityDetailsActivity.this, 0, 0, viewEdit, R.style.DialogTheme);
                    mDialogTools.setCancelable(true);
                    mDialogTools.show();

                    mDialogTools.findViewById(R.id.popw_tv_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mWifiPass.setText(editWifiPass.getText());
                            mDialogTools.hide();
                        }
                    });
                    mDialogTools.findViewById(R.id.popw_tv_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDialogTools.hide();
                        }
                    });
                }
            });
            //banlap：  确认按钮返回
            mOkClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(FacilityDetailsActivity.this, "当前设备info: " + wifiManager2.getConnectionInfo(), Toast.LENGTH_LONG).show();
                    int removeNetID = wifiManager2.getConnectionInfo().getNetworkId();
                    wifiManager2.removeNetwork(removeNetID);
                    wifiManager2.disconnect();
                    wifiManager2.reconnect();

                    finish();
                }
            });

        } else {
            mWifiMode.setText("Wifi-互联网模式");
            mNetworkClick.setVisibility(View.GONE);
            //banlap： wifi密码 密文显示
            mWifiPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            TcpConnection tcpConnection2 = TcpConnection.getInstance();

            tcpConnection2.init(getApplicationContext());

            //banlap： 控制开关按钮
            mivSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(FacilityDetailsActivity.this, "开关控制: ip: " + intToIp(wifiManager2.getDhcpInfo().serverAddress), Toast.LENGTH_LONG).show();
                    //deviceSwitch(mIsSwitch, tcpConnection2, mDirectWifi, intToIp(wifiManager2.getDhcpInfo().serverAddress));
                    //runScene("111");
                    if(mLightSwitchValue == 0) {
                        mLightSwitchValue=1;
                        thingSetProperties(mLightSwitchValue);
                    } else {
                        mLightSwitchValue=0;
                        thingSetProperties(mLightSwitchValue);
                    }
                    //Toast.makeText(FacilityDetailsActivity.this, "开关控制: " + mIotId, Toast.LENGTH_LONG).show();


                }
            });
            //banlap： wifi账号输入
            mWifiSSID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View viewEdit = getLayoutInflater().inflate(R.layout.popwindow_edit_wifi_ssid, null);
                    EditText editWifiSSID = viewEdit.findViewById(R.id.popw_et_wifiSSID);
                    editWifiSSID.setText(mWifiSSID.getText());
                    DialogTools mDialogTools = new DialogTools(FacilityDetailsActivity.this, 0, 0, viewEdit, R.style.DialogTheme);
                    mDialogTools.setCancelable(true);
                    mDialogTools.show();

                    mDialogTools.findViewById(R.id.popw_tv_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mWifiSSID.setText(editWifiSSID.getText());
                            mDialogTools.hide();
                        }
                    });
                    mDialogTools.findViewById(R.id.popw_tv_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDialogTools.hide();
                        }
                    });
                }
            });
            //banlap： wifi密码输入
            mWifiPass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View viewEdit = getLayoutInflater().inflate(R.layout.popwindow_edit_wifi_pass, null);
                    EditText editWifiPass = viewEdit.findViewById(R.id.popw_et_wifiPass);
                    editWifiPass.setText(mWifiPass.getText());
                    DialogTools mDialogTools = new DialogTools(FacilityDetailsActivity.this, 0, 0, viewEdit, R.style.DialogTheme);
                    mDialogTools.setCancelable(true);
                    mDialogTools.show();

                    mDialogTools.findViewById(R.id.popw_tv_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mWifiPass.setText(editWifiPass.getText());
                            mDialogTools.hide();
                        }
                    });
                    mDialogTools.findViewById(R.id.popw_tv_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDialogTools.hide();
                        }
                    });
                }
            });
            //banlap： 配网按钮
            mNetworkClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mWifiConnect2.connect(wifiSSID, wifiPass, WifiConnectManager.WifiCipherType.WIFICIPHER_WPA);
                    //mWifiConnect2.connect("82-7D-3A-5", wifiPass, WifiConnectManager.WifiCipherType.WIFICIPHER_WPA);
                    Toast.makeText(FacilityDetailsActivity.this, "正在配网中..." , Toast.LENGTH_LONG).show();
                }
            });

            //banlap：  回调连接消息
            tcpConnection2.getDeviceMsg(new TcpConnection.CallBack() {
                @Override
                public void deviceMsg(String msg) {
                    mDeviceNetworkCallback = msg;
                }
            });

            //banlap： 确认按钮
            mOkClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(FacilityDetailsActivity.this, "确认: " , Toast.LENGTH_LONG).show();
                    finish();
                }
            });


            mWifiConnect2.mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(4000);

                                //banlap： 判断当前是否已网络连接
                                if(wifiManager2.getConnectionInfo().getSupplicantState() != SupplicantState.COMPLETED ){
                                    Looper.prepare();
                                    Toast.makeText(FacilityDetailsActivity.this, "无法连接到该设备: 请检查网络", Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }

                                String deviceIp = intToIp(wifiManager2.getDhcpInfo().serverAddress);
                                tcpConnection2.connectToDevice(deviceIp);

                                if(mDeviceNetworkCallback.equals("连接失败")){
                                    Looper.prepare();
                                    Toast.makeText(FacilityDetailsActivity.this, "配网失败: 连接设备不成功", Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                } else {
                                    //String msgSwitch = "ProductKey:a1ELVgicjs4DeviceName:AppTestLedDeviceSecre:77157019ea6fdd6f26ce77927a811ff6HotWifiInfo:ssid:MG-RX-93password:11111111";
                                    //String msgSwitch = "ProductKey:a1ELVgicjs4DeviceName:AppTestLedDeviceSecre:77157019ea6fdd6f26ce77927a811ff6HotWifiInfo:ssid:dingyingdyagepassword:Dingying*888";
                                    String msgSwitch = "";
                                    tcpConnection2.writeMsg(msgSwitch, mDeviceIp);
                                    if(mDeviceNetworkCallback.equals("发送数据失败")){
                                        Looper.prepare();
                                        Toast.makeText(FacilityDetailsActivity.this, "配网失败: 发送的wifi信息失败", Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    } else {
                                        Looper.prepare();
                                        Toast.makeText(FacilityDetailsActivity.this, "配网成功: " + mDeviceNetworkCallback + "，请重启设备的wifi", Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }

                                }

                            } catch(Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
            };
        }



    }
    private void initHandler() {

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {

                    case FINDSCENE:
                        Object data = msg.obj;
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(data.toString());

                        com.alibaba.fastjson.JSONArray sceneArray1 = jsonObject.getJSONArray("scenes");
                        //Toast.makeText(FacilityDetailsActivity.this, "输出： " + sceneArray1.toString(), Toast.LENGTH_LONG).show();
                        ArrayList<ScenesListBean> arrayList = new ArrayList<>();
                        if (sceneArray1 != null) {
                            for (int i = 0; i < sceneArray1.size(); i++) {

                            }
                        }
                        break;

                    case FINDDATAINFO:
                        Object data2 = msg.obj;
                        com.alibaba.fastjson.JSONObject jsonObject2 = JSON.parseObject(data2.toString());

                        com.alibaba.fastjson.JSONArray sceneArray2 = jsonObject2.getJSONArray("data");
                        if (sceneArray2 != null) {
                            for (int i = 0; i < sceneArray2.size(); i++) {
                                com.alibaba.fastjson.JSONObject devJsonObject2 = sceneArray2.getJSONObject(i);
                                if(mDeviceName.getText().toString().equals(devJsonObject2.getString("productName")) &&
                                        mDeviceNo.getText().toString().equals(devJsonObject2.getString("productKey")) ){
                                    if(devJsonObject2.getInteger("status") == 1) {
                                        mDeviceStatus.setText("在线");
                                    } else if (devJsonObject2.getInteger("status") == 0) {
                                        mDeviceStatus.setText("未激活");
                                    } else if (devJsonObject2.getInteger("status") == 3) {
                                        mDeviceStatus.setText("离线");
                                    } else if (devJsonObject2.getInteger("status") == 8) {
                                        mDeviceStatus.setText("禁用");
                                    }
                                }
                                //获取设备ID， 非平台key或secret
                                mIotId = devJsonObject2.getString("iotId");
                            }
                            thingTSL();
                        }
                        break;
                    case FINDTHINGSINFO:
                        Object data3 = msg.obj;
                        com.alibaba.fastjson.JSONObject jsonObject3 = JSON.parseObject(data3.toString());
                        com.alibaba.fastjson.JSONObject jsonObject31 = jsonObject3.getJSONObject("LightSwitch");

                        //Toast.makeText(FacilityDetailsActivity.this, "LightSwitch: " + jsonObject31.getString("value"), Toast.LENGTH_LONG).show();
                        mLightSwitchTime = jsonObject31.getInteger("time");
                        mLightSwitchValue = jsonObject31.getInteger("value");
                        break;

                    case SUCCESSSET:

                        //Toast.makeText(FacilityDetailsActivity.this, "success: ", Toast.LENGTH_LONG).show();
                        if(mLightSwitchValue == 0) {
                            mivSwitch.setImageResource(R.mipmap.button_daguan);
                            mDeviceSwitch.setText("关闭");
                            mDeviceStatus.setText("在线");
                        } else {
                            mivSwitch.setImageResource(R.mipmap.button_dakai);
                            mDeviceSwitch.setText("打开");
                            mDeviceStatus.setText("在线");
                        }
                        break;
                }
            }
        };
    }


    //banlap：  直连或互联网模式时 控制设备开关
    private void deviceSwitch(boolean isSwitch, TcpConnection tcpConn, boolean isDirectWifi, String deviceIp) {
        //String msgSwitch = "ProductKey:a1ELVgicjs4DeviceName:AppTestLedDeviceSecre:77157019ea6fdd6f26ce77927a811ff6\"thing.service.property.set\"\"LightSwitch\":1";
        String msgCallback="";
        if (isDirectWifi) {
            msgCallback = mDeviceMsgCallback;
        } else {
            msgCallback = mDeviceNetworkCallback;
        }
        //banlap； 回调消息为失败时，提醒设备已断线
        if (msgCallback.equals("连接失败")) {
            mivSwitch.setImageResource(R.mipmap.button_daguan);
            mDeviceSwitch.setText("关闭");
            mDeviceStatus.setText("离线");
            Toast.makeText(FacilityDetailsActivity.this, "当前设备已断线", Toast.LENGTH_LONG).show();
        } else {
            if (isSwitch) {
                String msgSwitch = mQrCodeInfo.substring(0, mQrCodeInfo.indexOf("HotWifiInfo:")).concat("\"thing.service.property.set\"\"LightSwitch\":1");
                tcpConn.writeMsg(msgSwitch, deviceIp);
                if (isDirectWifi) {
                    msgCallback = mDeviceMsgCallback;
                } else {
                    msgCallback = mDeviceNetworkCallback;
                }
                if (msgCallback.equals("发送数据失败") ) {
                    mivSwitch.setImageResource(R.mipmap.button_daguan);
                    mDeviceSwitch.setText("关闭");
                    mDeviceStatus.setText("离线");
                    Toast.makeText(FacilityDetailsActivity.this, "当前无法控制设备", Toast.LENGTH_LONG).show();
                } else {
                    mivSwitch.setImageResource(R.mipmap.button_dakai);
                    mDeviceSwitch.setText("开启");
                    mDeviceStatus.setText("离线");
                   // Toast.makeText(FacilityDetailsActivity.this, "信息：" + msgCallback, Toast.LENGTH_LONG).show();
                    mIsSwitch = false;
                }
            } else {
                String msgSwitch = mQrCodeInfo.substring(0, mQrCodeInfo.indexOf("HotWifiInfo:")).concat("\"thing.service.property.set\"\"LightSwitch\":0");
                tcpConn.writeMsg(msgSwitch, deviceIp);
                if (isDirectWifi) {
                    msgCallback = mDeviceMsgCallback;
                } else {
                    msgCallback = mDeviceNetworkCallback;
                }
                if (msgCallback.equals("发送数据失败")) {
                    mivSwitch.setImageResource(R.mipmap.button_daguan);
                    mDeviceSwitch.setText("关闭");
                    mDeviceStatus.setText("离线");
                    Toast.makeText(FacilityDetailsActivity.this, "当前无法控制设备", Toast.LENGTH_LONG).show();
                } else {
                    mivSwitch.setImageResource(R.mipmap.button_daguan);
                    mDeviceSwitch.setText("关闭");
                    mDeviceStatus.setText("离线");
                    //Toast.makeText(FacilityDetailsActivity.this, "信息：" + msgCallback, Toast.LENGTH_LONG).show();
                    mIsSwitch = true;
                }
            }

        }
    }

    //banlap： 传送wifi信息到，并使设备连接该wifi
    private void setWifiInfo(TcpConnection tcpConn, String deviceIp, String wifiSSID, String wifiPasss){
        mDeviceMsgCallback="";
        String setWifiInfo = "HotWifiInfo:ssid:"  + wifiSSID + "password:" + wifiPasss;
        String msgSwitch = mQrCodeInfo.substring(0, mQrCodeInfo.indexOf("HotWifiInfo:")).concat(setWifiInfo);
        Toast.makeText(FacilityDetailsActivity.this, "信息: " +msgSwitch + " ip: " +  deviceIp, Toast.LENGTH_LONG).show();
        tcpConn.writeMsg(msgSwitch, deviceIp);
        if(mDeviceMsgCallback.equals("发送数据失败")){
            Toast.makeText(FacilityDetailsActivity.this, "配网失败，请重试", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(FacilityDetailsActivity.this, "配网成功" , Toast.LENGTH_LONG).show();
        }

    }



    @TargetApi(19)
    public static void transparencyBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    //banlap：  wifi数据类型转换
    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    //banlap：  实体键返回
    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            //banlap： 直连模式断开当前wifi
            if(mDirectWifi){
                int removeNetID= wifiManager2.getConnectionInfo().getNetworkId();
                wifiManager2.removeNetwork(removeNetID);
                wifiManager2.disconnect();
                wifiManager2.reconnect();
            }
            finish();
        }
        return false;
    }



    //查询场景列表
    private void findScene() {
        Map<String, Object> params = new HashMap<>();
        params.put("groupId","0"); //设置 自动化或非自动化，自动化值为1， 非自动化为0
        params.put("pageNo", 1);
        params.put("pageSize", 10);
        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/scene/list/get") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
                .setApiVersion("1.0.5")  // 参照API文档，设置API接口的版本号，本示例为1.0.2
                .setAuthType("iotAuth") // 当云端接口需要用户身份鉴权时需要设置该参数，反之则不需要设置
                .setParams(params) // 参照API文档，设置API接口的参数，也可以使用.setParams(Map<Strign,Object> params)来设置
                .build();
        // 获取Client实例，并发送请求
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest request, Exception e) {
                // TODO根据e，处理异常
                Log.e("onFailure1",e.getMessage()+"----");
            }

            @Override
            public void onResponse(IoTRequest request, IoTResponse response) {
                int code = response.getCode();
                Log.e("code1",code+"----");
                // 200 代表成功
                if(200 != code){
                    //失败示例，参见 "异常数据返回示例"
                    String mesage = response.getMessage();
                    String localizedMsg = response.getLocalizedMsg();
                    //TODO，根据mesage和localizedMsg，处理失败信息
                    return;
                }
                Log.e("data--","----");
                Object data = response.getData();
                //TODO，可以将data转成一个本地的对象或者直接使用JSONObject进行数据解析
                Log.e("data1--",data.toString()+"----");
                /**
                 * 解析data，data示例参见"正常数据返回示例"
                 * 以下解析示例采用fastjson针对"正常数据返回示例"，解析各个数据节点
                 */
                if (data == null) {
                    return;
                }
                Message message=new Message();
                message.what=FINDSCENE;
                message.obj=data;
                handler.sendMessage(message);
            }
        });

    }


    // 控制开关， 即运行场景
    private void runScene(String sceneId) {
        Map<String, Object> params = new HashMap<>();
        params.put("sceneId", sceneId);
        // iotId获取当前账号绑定设备列表的时候可以拿到，对应唯一设备
        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/scene/fire") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
                .setApiVersion("1.0.4")  // 参照API文档，设置API接口的版本号，本示例为1.0.2
                .setAuthType("iotAuth") // 当云端接口需要用户身份鉴权时需要设置该参数，反之则不需要设置
                .setParams(params) // 参照API文档，设置API接口的参数，也可以使用.setParams(Map<Strign,Object> params)来设置
                .build();
        // 获取Client实例，并发送请求
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {

            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                // TODO根据e，处理异常
                Log.e("onFailure1",e.getMessage()+"----");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                int code = ioTResponse.getCode();
                // 200 代表成功
                if(200 != code){
                    //失败示例，参见 "异常数据返回示例"
                    String mesage = ioTResponse.getMessage();
                    String localizedMsg = ioTResponse.getLocalizedMsg();
                    //TODO，根据mesage和localizedMsg，处理失败信息
                    return;
                }

                Object data = ioTResponse.getData();

            }
        });

    }

    //查询用户绑定的设备
    private void listByAccount() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("pageNo", 1);
        maps.put("pageSize", 10);
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/uc/listBindingByAccount")
                .setScheme(Scheme.HTTPS)
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();

        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                // TODO根据e，处理异常
                Log.e("onFailure1",e.getMessage()+"----");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                int code = response.getCode();
                Log.e("code1",code+"----");
                // 200 代表成功
                if(200 != code){
                    //失败示例，参见 "异常数据返回示例"
                    String mesage = response.getMessage();
                    String localizedMsg = response.getLocalizedMsg();
                    return;
                }

                Object data = response.getData();
                if (data == null) {
                    return;
                }
                Message message=new Message();
                message.what=FINDDATAINFO;
                message.obj=data;
                handler.sendMessage(message);
            }

        });
    }

    //根据设备ID获取物的属性
    private void thingTSL() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("iotId", mIotId);

        IoTRequestBuilder builder = new IoTRequestBuilder()
                //.setPath("/thing/tsl/get")
                .setPath("/thing/properties/get")
                .setScheme(Scheme.HTTPS)
                .setApiVersion("1.0.4")
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = builder.build();
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();

        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                // TODO根据e，处理异常
                Log.e("onFailure1",e.getMessage()+"----");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                int code = response.getCode();
                Log.e("code1",code+"----");
                // 200 代表成功
                if(200 != code){
                    //失败示例，参见 "异常数据返回示例"
                    String mesage = response.getMessage();
                    String localizedMsg = response.getLocalizedMsg();
                    return;
                }

                Object data = response.getData();
                if (data == null) {
                    return;
                }
                Message message=new Message();
                message.what=FINDTHINGSINFO;
                message.obj=data;
                handler.sendMessage(message);
            }

        });
    }

    //实现阿里云控制设备
    private void thingSetProperties(int value) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("iotId", mIotId);

        Map<String, Object> mapItem = new HashMap<>();
        mapItem.put("LightSwitch", value);
        maps.put("items", mapItem);

        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/thing/properties/set")
                .setScheme(Scheme.HTTPS)
                .setApiVersion("1.0.5")
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = builder.build();
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                // TODO根据e，处理异常
                Log.e("onFailure1",e.getMessage()+"----");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                int code = response.getCode();
                Log.e("code1",code+"----");
                // 200 代表成功
                if(200 != code){
                    //失败示例，参见 "异常数据返回示例"
                    String message = response.getMessage();
                    String localizedMsg = response.getLocalizedMsg();

                    Looper.prepare();
                    //Toast.makeText(FacilityDetailsActivity.this, "code: " + code + " message: " + message + " localizedMsg: " + localizedMsg , Toast.LENGTH_LONG).show();
                    Toast.makeText(FacilityDetailsActivity.this, " localizedMsg: " + localizedMsg , Toast.LENGTH_LONG).show();
                    //mivSwitch.setImageResource(R.mipmap.button_daguan);
                    //mDeviceStatus.setText("离线");
                    Looper.loop();

                    return;
                }

                Object data = response.getData();
                if (data == null) {
                    return;
                }
                Message message=new Message();
                message.what=SUCCESSSET;
                message.obj=data;
                handler.sendMessage(message);
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1) {
            Toast.makeText(FacilityDetailsActivity.this, "返回：" + data.toString(), Toast.LENGTH_LONG).show();

        }
    }
}