package com.aliyun.iot.ilop.demo.page.ilopmain;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.bean.DeviceInfoBean;
import com.aliyun.iot.ilop.demo.page.share.ShareActivity;
import com.aliyun.iot.ilop.demo.view.DevicePanelView;
import com.aliyun.iot.link.ui.component.LinkToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class HomeTabFragment extends Fragment {
    private String TAG = HomeTabFragment.class.getSimpleName();
    private FrameLayout mIlopMainMenu;
    private FrameLayout mVDevicePanel;
    private FrameLayout mMyDevicePanel;
    private FrameLayout mMyDevicePanelAdd;
    private Handler mHandler = new Handler();
    private int mRegisterCount = 0, mVirtualCount = 0;
    private Bundle mBundle = new Bundle();
    private String url = "/living/virtual/qrcode/bind";

    /**
     * 将自己项目内的pk填入，可以体验虚拟设备
     */
    //String[] pks = new String[]{"a1AzoSi5TMc", "a1B6cFQldpm", "a1XoFUJWkPr", "a1nZ7Kq7AG1", "a1OX20YJSLk"};
    String[] pks = new String[]{};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hometab_fragment_layout, container, false);
        View mIlopMainAddBtn = view.findViewById(R.id.ilop_main_add_btn);
        View mIlopMainAddBigBtn = view.findViewById(R.id.ilop_main_add_big_btn);
        Button mIlopMainMenuScanBtn = view.findViewById(R.id.ilop_main_menu_scan_btn);
        Button mIlopMainMenuAddDeviceBtn = view.findViewById(R.id.ilop_main_menu_add_device_btn);
        mIlopMainMenu = view.findViewById(R.id.ilop_main_menu);
        mMyDevicePanel = view.findViewById(R.id.my_device_panel);
        mVDevicePanel = view.findViewById(R.id.my_vdevice_panel);
        mMyDevicePanelAdd = view.findViewById(R.id.my_device_panel_add);

        mIlopMainAddBtn.setOnClickListener(view1 -> {
            mIlopMainMenu.setVisibility(View.VISIBLE);
            mIlopMainMenu.bringToFront();
        });

        mIlopMainAddBigBtn.setOnClickListener(view12 -> Router.getInstance().toUrl(getContext(), "page/ilopadddevice", mBundle));
        mIlopMainMenu.setOnClickListener(view13 -> mIlopMainMenu.setVisibility(View.INVISIBLE));
        mIlopMainMenuAddDeviceBtn.setOnClickListener(view14 -> Router.getInstance().toUrl(getContext(), "page/ilopadddevice", mBundle));
        mIlopMainMenuScanBtn.setOnClickListener(view15 -> Router.getInstance().toUrl(getContext(), "page/scan"));

        return view;
    }


    private String displayingMessage = null;

    private void listByAccount() {
        Map<String, Object> maps = new HashMap<>();
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
                ALog.d(TAG, "onFailure");
                if ("request auth error.".equals(e.getMessage())) {
                    onLogout();
                }
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                final int code = response.getCode();
                final String localizeMsg = response.getLocalizedMsg();
                if (code != 200) {
                    if (!Objects.equals(displayingMessage, localizeMsg)) {
                        displayingMessage = localizeMsg;
                        mHandler.post(() -> LinkToast.makeText(Objects.requireNonNull(getActivity()), localizeMsg).show());
                    }

                    if (code == 401) onLogout();
                    return;
                }

                Object data = response.getData();
                if (data == null) {
                    return;
                }
                if (!(data instanceof JSONObject)) {
                    return;
                }
                try {
                    JSONObject jsonObject = (JSONObject) data;
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    List<DeviceInfoBean> deviceInfoBeanList = JSON.parseArray(jsonArray.toString(), DeviceInfoBean.class);
                    mHandler.post(() -> initDevicePanel(deviceInfoBeanList));
                    if (deviceInfoBeanList == null) {
                        return;
                    }
                    //注册虚拟设备
                    Set<String> set = new HashSet<String>();
                    ArrayList<String> deviceStrList = new ArrayList<>();
                    for (DeviceInfoBean deviceInfoBean : deviceInfoBeanList) {
                        set.add(deviceInfoBean.getProductKey());
                        deviceStrList.add(deviceInfoBean.getProductKey() + deviceInfoBean.getDeviceName());
                    }
                    mBundle.putStringArrayList("deviceList", deviceStrList);
                    //noinspection MismatchedReadAndWriteOfArray
                    mRegisterCount = 0;
                    mVirtualCount = 0;
                    //noinspection RedundantOperationOnEmptyContainer
                    for (String pk : pks) {
                        if (set.add(pk)) {
                            mRegisterCount++;
                            //registerVirtualDevice(pk);
                            addVirtualDevice(pk);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void onLogout() {
        /*Intent intent = new Intent(getContext(), CountryListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (getActivity() != null) {
            getActivity().getApplicationContext().startActivity(intent);
            getActivity().finish();
        }*/

        //跳转到登录界面
        LoginBusiness.login(new ILoginCallback() {
            @Override
            public void onLoginSuccess() {
                MainActivity.start(HomeTabFragment.this.getActivity());
            }

            @Override
            public void onLoginFailed(int i, String s) {
                // LinkToast.makeText(getApplicationContext(), s).show();
            }
        });
    }

    private void bindVirturalToUser(String pk, String dn) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("productKey", pk);
        maps.put("deviceName", dn);
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/thing/virtual/binduser")
                .setApiVersion("1.0.0")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {

            }
        });
    }

    public IoTRequest buildRequest(Map params, String url) {
        IoTRequestBuilder builder = new IoTRequestBuilder();
        builder.setApiVersion("1.0.0");
        builder.setScheme(Scheme.HTTPS);
        builder.setPath(url);
        builder.setParams(params);
        builder.setAuthType("iotAuth");
        IoTRequest request = builder.build();
        return request;
    }


    /**
     * 添加虚拟设备
     */
    private void addVirtualDevice(String pk) {


        String identity = getIdentity();
        if (TextUtils.isEmpty(identity)) return;
        Map<String, Object> params = new HashMap<>();
        params.put("productKey", pk);
        params.put("identityId", identity);
        IoTRequest request = buildRequest(params, url);
        IoTAPIClient mIoTAPIClient = new IoTAPIClientFactory().getClient();
        mIoTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, final Exception e) {
                ALog.e(TAG, "onFail " + this, e);

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, final IoTResponse ioTResponse) {
                if (200 != ioTResponse.getCode()) {
                    ALog.e(TAG, "code：" + ioTResponse.getCode() + "message：" + ioTResponse.getMessage() + "LocalizedMsg：" + ioTResponse.getLocalizedMsg());
                    final String localizeMsg = ioTResponse.getLocalizedMsg();
                    if (!Objects.equals(displayingMessage, localizeMsg)) {
                        displayingMessage = localizeMsg;
                        mHandler.post(() -> LinkToast.makeText(Objects.requireNonNull(getActivity()), localizeMsg).show());
                    }
                } else {
                    ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            final int code = ioTResponse.getCode();
                            mVirtualCount++;
                            if (mRegisterCount == mVirtualCount) {
                                listByAccount();
                            }

                        }
                    });

                }
            }
        });

    }

    /**
     * 获取Identity
     *
     * @return
     */
    private String getIdentity() {
        IoTCredentialManageImpl ioTCredentialManage = IoTCredentialManageImpl.getInstance(AApplication.getInstance());
        if (null == ioTCredentialManage) {
            return null;
        }
        return ioTCredentialManage.getIoTIdentity();
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private void initDevicePanel(List<DeviceInfoBean> deviceInfoBeanList) {
        if (mMyDevicePanel.getChildCount() > 1) {
            mMyDevicePanel.removeViews(1, mMyDevicePanel.getChildCount() - 1);
        }
        if (mVDevicePanel.getChildCount() > 1) {
            mVDevicePanel.removeViews(1, mVDevicePanel.getChildCount() - 1);
        }

        if (deviceInfoBeanList == null) {
            return;
        }

        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;

        int index_my = 0;
        int index_v = 0;
        for (int i = 0; i < deviceInfoBeanList.size(); i++) {
            DeviceInfoBean device = deviceInfoBeanList.get(i);
            DevicePanelView devicePanelView = new DevicePanelView(getContext());

            devicePanelView.setDeviceInfo(device);
            final String title = device.getProductName();
            final String code = device.getProductKey();
            final String iotId = device.getIotId();

            devicePanelView.setOnClickListener(view -> {
                ALog.d(TAG, "devicePanelView onClick" + code);
                if (code.equals("a1AzoSi5TMc")) {
                    Intent intent = new Intent(getActivity(), LampsActivity.class);
                    Log.d("HomeTabFragment", iotId);
                    intent.putExtra("iotId", iotId);
                    intent.putExtra("title", title);
                    startActivity(intent);
                } else if (code.equals("a1OX20YJSLk")) {
                    /** FixMe
                     * 这个位置是打开LinkVisual摄像头界面的地方，如果需要这个demo能看到对应的视频
                     * 则需要修改上面判断条件内的"a1OX20YJSLk"为自己的飞燕摄像头产品的ProductKey
                     */
                    // Intent intent = new Intent(getActivity(), IPCameraActivity.class);
                    //intent.putExtra("iotId", iotId);
                    //startActivity(intent);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("iotId", iotId);
                    String url = "link://router/" + code;
                    Router.getInstance().toUrl(getActivity(), url, bundle);
                }
            });

            devicePanelView.setOnLongClickListener(view -> {
                ArrayList<String> iotIdList = new ArrayList<>();
                iotIdList.add(iotId);
                ShareActivity.start(view.getContext(), iotIdList);
                return false;
            });

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((width - 30) / 2,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            if ("VIRTUAL".equalsIgnoreCase(device.getThingType()) || "VIRTUAL_SHADOW".equalsIgnoreCase(device.getThingType())) {
                lp.setMargins(width / 2 * (index_v % 2) + dip2px(Objects.requireNonNull(getContext()), 5),
                        dip2px(getContext(), 100) * (index_v / 2) + dip2px(getContext(), 26),
                        0, 0);
                index_v++;
                mVDevicePanel.addView(devicePanelView, lp);
            } else {
                lp.setMargins(width / 2 * (index_my % 2) + dip2px(Objects.requireNonNull(getContext()), 5),
                        dip2px(getContext(), 100) * (index_my / 2) + dip2px(getContext(), 26),
                        0, 0);
                index_my++;
                mMyDevicePanel.addView(devicePanelView, lp);
                mMyDevicePanelAdd.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Log.d(TAG, "onActivityResult");
            if (data.getStringExtra("productKey") != null) {
                Bundle bundle = new Bundle();
                bundle.putString("productKey", data.getStringExtra("productKey"));
                bundle.putString("deviceName", data.getStringExtra("deviceName"));
                bundle.putString("token", data.getStringExtra("token"));
                Intent intent = new Intent(getActivity(), HomeTabFragment.class);
                intent.putExtras(bundle);
                this.startActivity(intent);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mIlopMainMenu.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (LoginBusiness.isLogin()) {
            listByAccount();
        }
    }
}
