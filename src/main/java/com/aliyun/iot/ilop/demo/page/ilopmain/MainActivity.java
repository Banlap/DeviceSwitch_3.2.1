package com.aliyun.iot.ilop.demo.page.ilopmain;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.alibaba.fastjson.JSON;
import com.aliyun.alink.business.devicecenter.api.add.AddDeviceBiz;
import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo;
import com.aliyun.alink.business.devicecenter.api.add.IAddDeviceListener;
import com.aliyun.alink.business.devicecenter.api.add.ProvisionStatus;
import com.aliyun.alink.business.devicecenter.api.config.ProvisionConfigCenter;
import com.aliyun.alink.business.devicecenter.api.config.ProvisionConfigParams;
import com.aliyun.alink.business.devicecenter.api.discovery.DiscoveryType;
import com.aliyun.alink.business.devicecenter.api.discovery.GetTokenParams;
import com.aliyun.alink.business.devicecenter.api.discovery.IDeviceDiscoveryListener;
import com.aliyun.alink.business.devicecenter.api.discovery.IOnDeviceTokenGetListener;
import com.aliyun.alink.business.devicecenter.api.discovery.LocalDeviceMgr;
import com.aliyun.alink.business.devicecenter.base.DCErrorCode;
import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.component.scan.ScanManager;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.ilop.demo.kaiguan.activity.FacilityDetailsActivity;
import com.aliyun.iot.ilop.demo.kaiguan.fragment.FacilityFragment;
import com.aliyun.iot.ilop.demo.kaiguan.fragment.HomeFragment;
import com.aliyun.iot.ilop.demo.kaiguan.fragment.MainFragment;
import com.aliyun.iot.ilop.demo.kaiguan.fragment.TroubleFragment;
import com.aliyun.iot.ilop.demo.kaiguan.utils.PermissionsActivity;
import com.aliyun.iot.ilop.demo.kaiguan.utils.PermissionsChecker;
import com.aliyun.iot.ilop.demo.utils.DialogTools;
import com.aliyun.iot.ilop.demo.utils.PermissionPageUtils;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.device.bind.BindAndUseActivity;
import com.aliyun.iot.ilop.demo.page.device.scan.AddDeviceScanPlugin;
import com.aliyun.iot.ilop.demo.utils.TcpConnection;
import com.aliyun.iot.ilop.demo.utils.WifiConnectManager;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;

import org.mozilla.javascript.tools.jsc.Main;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity {

    public static final int REQUEST_PERMISSION = RESULT_FIRST_USER + 1;
    private static final int REQUEST_CODE = 0; // 请求码
    private static final int REQUEST_CODE_SCAN_ONE = 0X01;
    private int deviceWidth;
    private int deviceHeight;
    WifiManager wifiManager;
    WifiConnectManager mWifiConnect = null;     // wifi连接管理
    private String qrCodeInfo = null;           // 获取qr码信息
    private String mCurrentWifiIP = null;       // 获取当前wifi IP
    SharedPreferences mContextWifi;             // 临时存储已直连的IP


    private String mProductKey = null;
    private String mDeviceName = null;
    private String mToken = null;

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器

    private Handler handler;
    private final int GETTOKEN = 100;

    private String TAG = "MainActivity";

    //private Class[] fragmentClass = {HomeTabFragment.class, HomeFragment.class, FacilityFragment.class,  DebugTabFragment.class,MainFragment.class};
    //private Class[] fragmentClass = {HomeFragment.class,HomeTabFragment.class, FacilityFragment.class,  TroubleFragment.class,MainFragment.class,DebugTabFragment.class};
    //private String[] textViewArray = {"首页","场景","设备","故障", "我的","调试"};
    //private Integer[] drawables = {R.drawable.tab_home_btn,R.drawable.tab_home_btn, R.drawable.tab_view_fa,R.drawable.tab_my_guzha, R.drawable.tab_my_btn,R.drawable.tab_my_btn};
    private Class[] fragmentClass = {HomeFragment.class, FacilityFragment.class, TroubleFragment.class, MainFragment.class};
    private String[] textViewArray = {"首页", "设备", "故障", "我的"};
    private Integer[] drawables = {R.drawable.tab_home_btn, R.drawable.tab_view_fa, R.drawable.tab_my_guzha, R.drawable.tab_my_btn};
    View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!Settings.canDrawOverlays(getApplicationContext())) {
//                //启动Activity让用户授权
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                intent.setData(Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, 100);
//            } else {
//                FloatWindowHelper helper = FloatWindowHelper.getInstance(getApplication());
//                if (helper != null) {
//                    helper.setNeedShowFloatWindowFlag(true);
//                }
//            }
//        } else {
//            FloatWindowHelper helper = FloatWindowHelper.getInstance(getApplication());
//            if (helper != null) {
//                helper.setNeedShowFloatWindowFlag(true);
//            }
//        }

        WindowManager manager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        deviceWidth = metrics.widthPixels;     //以要素为单位
        deviceHeight = metrics.heightPixels;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        transparencyBar(MainActivity.this);
        setContentView(R.layout.activity_main);
        decorView = getWindow().getDecorView();
        mPermissionsChecker = new PermissionsChecker(this);

        MyFragmentTabLayout fragmentTabHost = findViewById(R.id.tab_layout);
        fragmentTabHost.init(getSupportFragmentManager())
                .setFragmentTabLayoutAdapter(new DefaultFragmentTabAdapter(Arrays.asList(fragmentClass), Arrays.asList(textViewArray), Arrays.asList(drawables)) {
                    @Override
                    public View createView(int pos) {
                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_item, null);
                        ImageView imageView = view.findViewById(R.id.img);
                        imageView.setImageResource(drawables[pos]);
                        TextView textView = view.findViewById(R.id.tab_text);
                        textView.setText(textViewArray[pos]);
                        return view;
                    }

                    @Override
                    public void onClick(int pos) {
                        ALog.d(TAG, "onClick:" + pos);
                    }
                }).creat();

        //扫码添加设备 注册
        ScanManager.getInstance().registerPlugin(AddDeviceScanPlugin.NAME, new AddDeviceScanPlugin(this));
        /*if (!checkLocationCompetence(this)) {
            requesLocation(this);
        }*/
        initWifiManager();
        mContextWifi = this.getSharedPreferences("connectWifiInfo", Context.MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT < 19 || !checkDeviceHasNavigationBar(this)) {
            return;
        } else {
            // 主要就是通过设置特定的属性，来控制Navigationbar的显示，有兴趣的同学可以去查查相关介绍
            int flag = (View.SYSTEM_UI_FLAG_VISIBLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            decorView.setSystemUiVisibility(flag);
        }
    }

    private boolean checkDeviceHasNavigationBar(Context context) {

        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        }


        return hasNavigationBar;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //需要取消注册，否则会造成内存泄露
        ScanManager.getInstance().unRegisterPlugin(AddDeviceScanPlugin.NAME);
//        //退出首页不显示浮窗
//        FloatWindowHelper helper = FloatWindowHelper.getInstance(getApplication());
//        if (helper != null) {
//            helper.setNeedShowFloatWindowFlag(false);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
        initHandler();
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
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

    private void initWifiManager() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiConnect = new WifiConnectManager(wifiManager);
    }


    //banlap： wifi数据类型转换
    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }


    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(MainActivity.this, "requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data.getData(), Toast.LENGTH_LONG).show();
        if (requestCode == 1) {
            Log.d(TAG, "onActivityResult");
            if (data != null && data.getStringExtra("productKey") != null) {
                Bundle bundle = new Bundle();
                bundle.putString("productKey", data.getStringExtra("productKey"));
                bundle.putString("deviceName", data.getStringExtra("deviceName"));
                bundle.putString("token", data.getStringExtra("token"));
                Intent intent = new Intent(this, BindAndUseActivity.class);
                intent.putExtras(bundle);
                this.startActivity(intent);
            }
        } else if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
            finish();
        }
        //banlap：
        if (data != null) {
            if (data.getData() != null) {
                try {
                    Uri selectedImage = data.getData();//获取路径
                    Toast.makeText(MainActivity.this, "获取头像:" + selectedImage, Toast.LENGTH_LONG).show();

                    Bitmap bitmap = BitmapFactory.decodeStream(getApplicationContext().getContentResolver().openInputStream(selectedImage));
                    //Bitmap bitmap = BitmapFactory.decodeStream(getImageStream(data.getData().toString()));

                    MainFragment mainFragment = (MainFragment) MainActivity.this.getSupportFragmentManager().findFragmentByTag("我的");
                    mainFragment.setPhoto(bitmap, selectedImage);

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }
        //banlap： 扫描二维码直连设备
        if (requestCode == REQUEST_CODE_SCAN_ONE) {

            try {
                if(data != null) {
                    HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
                    if (obj != null) {
                        //banlap： wifi信息： ssid与密码
                        qrCodeInfo = obj.originalValue;

                        //String productKey = (qrCodeInfo.substring(0, obj.originalValue.indexOf("DeviceName:"))).substring(11);
                        //String deviceName = (qrCodeInfo.substring(obj.originalValue.indexOf("DeviceName:"), obj.originalValue.indexOf("DeviceSecre:"))).substring(11);
                        String productKey = (qrCodeInfo.substring(obj.originalValue.indexOf("&pk="), obj.originalValue.indexOf("&dn="))).substring(4);
                        String deviceName = (qrCodeInfo.substring(obj.originalValue.indexOf("&dn=:"), obj.originalValue.indexOf("&HotWifiInfo:"))).substring(3);

                        String wifiInfo = qrCodeInfo.substring(obj.originalValue.indexOf("ssid:"));
                        String wifiSSID = wifiInfo.substring(5, wifiInfo.indexOf("password:")).trim();
                        String wifiPass = (wifiInfo.substring(wifiInfo.indexOf("password:"))).substring(9);
                        //Toast.makeText(this, "ssid: " + wifiSSID + "  and pass: " + wifiPass , Toast.LENGTH_LONG).show();



                        //获取当前wifi的DHCP
                        mCurrentWifiIP = intToIp(wifiManager.getDhcpInfo().serverAddress);
                        //mWifiConnect.connect("dingyingdyage", "Dingying*888", WifiConnectManager.WifiCipherType.WIFICIPHER_WPA);
                        //mWifiConnect.connect("82-7D-3A-5", wifiPass, WifiConnectManager.WifiCipherType.WIFICIPHER_WPA);
                        mWifiConnect.connect(wifiSSID, wifiPass, WifiConnectManager.WifiCipherType.WIFICIPHER_WPA);
                        Toast.makeText(MainActivity.this, "正在连接中... "+ mCurrentWifiIP , Toast.LENGTH_LONG).show();

                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "无效的二维码数据 ", Toast.LENGTH_LONG).show();
            }

            //banlap： 判断设备是否连接
            mWifiConnect.mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Toast.makeText(MainActivity.this, "" + msg.obj, Toast.LENGTH_LONG).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                SharedPreferences.Editor editor = mContextWifi.edit();
                                Thread.sleep(4000);

                                //banlap： 判断当前是否已网络连接
                                if (wifiManager.getConnectionInfo().getSupplicantState() != SupplicantState.COMPLETED) {
                                    Looper.prepare();
                                    Toast.makeText(MainActivity.this, "无法连接到该设备: 请检查网络", Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }

                                if (mCurrentWifiIP.equals(intToIp(wifiManager.getDhcpInfo().serverAddress))) {
                                    if (mContextWifi.getString("wifiIP", "000") != "000" && mContextWifi.getString("wifiIP", "000").equals(intToIp(wifiManager.getDhcpInfo().serverAddress))) {

                                        //banlap： 临时存储连接ip地址
                                        editor.putString("wifiIP", intToIp(wifiManager.getDhcpInfo().serverAddress));
                                        editor.apply();

                                        Intent intent = new Intent(MainActivity.this, FacilityDetailsActivity.class);
                                        intent.putExtra("qrCodeInfo", qrCodeInfo);
                                        startActivity(intent);
                                    } else if (mContextWifi.getString("wifiIP", "000") == "000") {
                                        //banlap： 临时存储连接ip地址
                                        editor.putString("wifiIP", intToIp(wifiManager.getDhcpInfo().serverAddress));
                                        editor.apply();

                                        Intent intent = new Intent(MainActivity.this, FacilityDetailsActivity.class);
                                        intent.putExtra("qrCodeInfo", qrCodeInfo);
                                        startActivity(intent);
                                    } else {
                                        Looper.prepare();
                                        Toast.makeText(MainActivity.this, "无法连接到该设备: " + intToIp(wifiManager.getDhcpInfo().serverAddress), Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }
                                } else {

                                    //临时存储连接ip地址
                                    editor.putString("wifiIP", intToIp(wifiManager.getDhcpInfo().serverAddress));
                                    editor.apply();

                                    Intent intent = new Intent(MainActivity.this, FacilityDetailsActivity.class);
                                    intent.putExtra("qrCodeInfo", qrCodeInfo);
                                    startActivity(intent);
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, "无法连接到该设备", Toast.LENGTH_LONG).show();
                                Looper.loop();
                            }
                        }
                    }).start();


                    super.handleMessage(msg);
                }
            };


        }

        if (requestCode == 2) {
            if (Activity.RESULT_OK != resultCode) {
                // 配网失败
                Toast.makeText(MainActivity.this, "配网失败", Toast.LENGTH_LONG).show();
                return;
            }

            String productKey = data.getStringExtra("productKey");
            String deviceName = data.getStringExtra("deviceName");
            // 配网成功
            Toast.makeText(MainActivity.this, "配网成功" + productKey + " and " + deviceName, Toast.LENGTH_LONG).show();
        }

//        if (requestCode == 100) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (Settings.canDrawOverlays(this)) {
//                    FloatWindowHelper helper = FloatWindowHelper.getInstance(getApplication());
//                    if (helper != null) {
//                        helper.setNeedShowFloatWindowFlag(true);
//                    }
//                }
//            }
//        }

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
     * 请求定位权限
     */
    private void requesLocation(Activity context) {
        if (context == null || context.isFinishing()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[1];
            permissions[0] = Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(context, permissions, REQUEST_PERMISSION);
        } else {
            try {
                new PermissionPageUtils(context).jumpPermissionPage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 检测定位权限
     */
    public static boolean checkLocationCompetence(Context context) {
        //检测是否有权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            int checkPermission = context.getPackageManager().checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, context.getPackageName());
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                //未获取到定位权限
                return false;
            } else {
                return true;
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //未获取到定位权限
                return false;
            } else {
                return true;
            }
        }


    }

    private void bindDeviceByQrCode(String qrCodeInfo) {
        Map<String, Object> params = new HashMap<>();
        params.put("qrKey", qrCodeInfo);

        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/uc/scanBindByShareQrCode") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
                .setApiVersion("1.0.8")  // 参照API文档，设置API接口的版本号，本示例为1.0.2
                .setAuthType("iotAuth") // 当云端接口需要用户身份鉴权时需要设置该参数，反之则不需要设置
                .setParams(params) // 参照API文档，设置API接口的参数，也可以使用.setParams(Map<Strign,Object> params)来设置
                .build();
        // 获取Client实例，并发送请求
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {

            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                // TODO根据e，处理异常
                Log.e("onFailure", e.getMessage() + "----");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                int code = ioTResponse.getCode();
                Log.e("code5", code + "----");
                // 200 代表成功
                if (200 != code) {
                    //失败示例，参见 "异常数据返回示例"
                    String mesage = ioTResponse.getMessage();
                    String localizedMsg = ioTResponse.getLocalizedMsg();
                    //TODO，根据mesage和localizedMsg，处理失败信息
                    Log.e("error5--", "----" + mesage);
                    return;
                }
                Log.e("data5--", "----");
                Object data = ioTResponse.getData();
            }
        });
    }

    private void checkToken(String productKey, String deviceName, String token) {
        Map<String, Object> params = new HashMap<>();
        params.put("productKey", productKey);
        params.put("deviceName", deviceName);
        params.put("token", token);

        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/awss/token/check") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
                .setApiVersion("1.0.10")  // 参照API文档，设置API接口的版本号，本示例为1.0.2
                .setAuthType("iotAuth") // 当云端接口需要用户身份鉴权时需要设置该参数，反之则不需要设置
                .setParams(params) // 参照API文档，设置API接口的参数，也可以使用.setParams(Map<Strign,Object> params)来设置
                .build();
        // 获取Client实例，并发送请求
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {

            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                // TODO根据e，处理异常
                Log.e("onFailure", e.getMessage() + "----");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                int code = ioTResponse.getCode();
                Log.e("code7", code + "----");
                // 200 代表成功
                if (200 != code) {
                    //失败示例，参见 "异常数据返回示例"
                    String mesage = ioTResponse.getMessage();
                    String localizedMsg = ioTResponse.getLocalizedMsg();
                    //TODO，根据mesage和localizedMsg，处理失败信息
                    Log.e("error7--", "----" + mesage);
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, "???code:" + code + " message: " + mesage + " localizedMsg: " + localizedMsg, Toast.LENGTH_LONG).show();
                    Looper.loop();
                    return;
                }
                Log.e("data5--", "----");
                Object data = ioTResponse.getData();


            }
        });
    }


    private void bindDeviceByToken(String productKey, String deviceName, String token) {
        Toast.makeText(MainActivity.this, "productKey:" + productKey + ":deviceName:" + deviceName + ":token: " + token, Toast.LENGTH_LONG).show();
        Map<String, Object> params = new HashMap<>();
        params.put("productKey", productKey);
        params.put("deviceName", deviceName);
        params.put("token", token);
        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/awss/token/user/bind") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
                .setApiVersion("1.0.8")  // 参照API文档，设置API接口的版本号，本示例为1.0.2
                .setAuthType("iotAuth") // 当云端接口需要用户身份鉴权时需要设置该参数，反之则不需要设置
                .setParams(params) // 参照API文档，设置API接口的参数，也可以使用.setParams(Map<Strign,Object> params)来设置
                .build();
        // 获取Client实例，并发送请求
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {

            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                // TODO根据e，处理异常
                Log.e("onFailure", e.getMessage() + "----");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                int code = ioTResponse.getCode();
                Log.e("code7", code + "----");
                // 200 代表成功
                if (200 != code) {
                    //失败示例，参见 "异常数据返回示例"
                    String mesage = ioTResponse.getMessage();
                    String localizedMsg = ioTResponse.getLocalizedMsg();
                    //TODO，根据mesage和localizedMsg，处理失败信息
                    Log.e("error7--", "----" + mesage);
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, "code:" + code + " message: " + mesage + " localizedMsg: " + localizedMsg, Toast.LENGTH_LONG).show();
                    Looper.loop();
                    return;
                }
                Log.e("data5--", "----");
                Object data = ioTResponse.getData();
            }
        });

    }

    private void getDeviceToken() {
        Map<String, Object> params = new HashMap<>();

        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/living/awss/token/create") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
                .setApiVersion("1.0.0")  // 参照API文档，设置API接口的版本号，本示例为1.0.2
                .setAuthType("iotAuth") // 当云端接口需要用户身份鉴权时需要设置该参数，反之则不需要设置
                .setParams(params) // 参照API文档，设置API接口的参数，也可以使用.setParams(Map<Strign,Object> params)来设置
                .build();
        // 获取Client实例，并发送请求
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {

            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                // TODO根据e，处理异常
                Log.e("onFailure", e.getMessage() + "----");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                int code = ioTResponse.getCode();
                Log.e("code6", code + "----");
                // 200 代表成功
                if (200 != code) {
                    //失败示例，参见 "异常数据返回示例"
                    String mesage = ioTResponse.getMessage();
                    String localizedMsg = ioTResponse.getLocalizedMsg();
                    //TODO，根据mesage和localizedMsg，处理失败信息
                    Log.e("error6--", "----" + mesage);
                   /* Looper.prepare();
                    Toast.makeText(MainActivity.this, "code:" + code + " message: " + mesage + " localizedMsg: " + localizedMsg, Toast.LENGTH_LONG).show();
                    Looper.loop();*/
                    return;
                }
                Log.e("data5--", "----");
                Object data = ioTResponse.getData();


                Message message = new Message();
                message.what = GETTOKEN;
                message.obj = data;
                handler.sendMessage(message);
            }
        });


    }



    private void findPeiWanDevice(String productKey, String deviceName) {


        Toast.makeText(MainActivity.this, "获取Token： ", Toast.LENGTH_LONG).show();
        LocalDeviceMgr.getInstance().getDeviceToken(MainActivity.this, productKey, deviceName, 60 * 1000, 2 * 1000, new IOnDeviceTokenGetListener() {
            @Override
            public void onSuccess(String token) {
                // TODO bind
                Toast.makeText(MainActivity.this, "成功： " + token, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String reason) {
                Toast.makeText(MainActivity.this, "失败： " + reason.toString(), Toast.LENGTH_LONG).show();
            }
        });


    }


    //进行设备配网
    private void startPeiWanDevice(DeviceInfo deviceInfo) {

        //设置待添加设备的基本信息
        AddDeviceBiz.getInstance().setDevice(deviceInfo);

        /**
         * 第二步：开始配网
         * 前置步骤，设置待配信息并开始配网
         */
        AddDeviceBiz.getInstance().startAddDevice(MainActivity.this, new IAddDeviceListener() {
            @Override
            public void onPreCheck(boolean b, DCErrorCode dcErrorCode) {
                // 参数检测回调
                Toast.makeText(MainActivity.this, "配网检查: " + b, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProvisionPrepare(int prepareType) {
                /**
                 * 第三步：配网准备阶段，传入Wi-Fi信息
                 * TODO 修改使用手机当前连接的Wi-Fi的SSID和password
                 */
                if (prepareType == 1) {
                    Log.e("Wi-Fi", "-------");
                    AddDeviceBiz.getInstance().toggleProvision("dingying24G", "dingyinglol888", 80);
                }
            }

            @Override
            public void onProvisioning() {
                // 配网中
                Log.e(TAG, "配网中" + "---");
                Toast.makeText(MainActivity.this, "配网..", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProvisionStatus(ProvisionStatus provisionStatus) {
                Toast.makeText(MainActivity.this, "配网状态 " + provisionStatus.message(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProvisionedResult(boolean isSuccess, DeviceInfo deviceInfo, DCErrorCode errorCode) {
                /**
                 * 第四步：监听配网结果
                 */
                Log.e(TAG, isSuccess + "---");
                // 如果配网结果包含token，请使用配网成功带的token做绑定。
                // 获取设备绑定的token
                Toast.makeText(MainActivity.this, "配网状态：" + isSuccess + " errorCode: " + errorCode.code + " - " + errorCode.subcode + " 原因：" + errorCode.msg, Toast.LENGTH_LONG).show();


            }
        });


    }

    private void initHandler() {

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {

                    case GETTOKEN:
                        Object data=msg.obj;
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(data.toString());
                        mToken = jsonObject.getString("token");

                        Toast.makeText(MainActivity.this, "输出： " + mToken, Toast.LENGTH_LONG).show();
                        bindDeviceByToken(mProductKey, mDeviceName, mToken);
                        //checkToken(mProductKey, mDeviceName, mToken);
                        break;
                }
            }
        };
    }
}
