package com.aliyun.iot.ilop.demo.page.device.adddevice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSON;
import com.aliyun.alink.linksdk.tmp.TmpSdk;
import com.aliyun.alink.linksdk.tmp.api.DeviceBasicData;
import com.aliyun.alink.linksdk.tmp.api.DeviceManager;
import com.aliyun.alink.linksdk.tmp.api.IDiscoveryFilter;
import com.aliyun.alink.linksdk.tmp.api.OutputParams;
import com.aliyun.alink.linksdk.tmp.listener.IDevListener;
import com.aliyun.alink.linksdk.tmp.utils.ErrorInfo;
import com.aliyun.iot.aep.sdk.framework.AActivity;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.device.bean.BleFoundDevice;
import com.aliyun.iot.ilop.demo.view.BleAddView;

import java.util.ArrayList;
import java.util.List;

public class BluetoothAddActivity extends AActivity implements OnBleDeviceAddListener {


    private static final String TAG = BluetoothAddActivity.class.getSimpleName();

    public static final int DEVICE_DETAIL_REQUEST_CODE = 1005;

    private LinearLayout llBluetoothList;
    private AddBleDeviceHandler addBleDeviceHandler;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_deviceadd_bluetooth);
        //申请定位权限（蓝牙扫描需要定位权限，否则会扫描不到设备）还要保证位置服务打开
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0x1111);
        }
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initView();
        addBleDeviceHandler = new AddBleDeviceHandler(this);
    }

    private void initView() {
        llBluetoothList = findViewById(R.id.ll_bluetooth_list);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        llBluetoothList.removeAllViews();
        addBleDeviceHandler.reset();

        // 设备发现（20分钟发现不了设备就停止扫描）
        TmpSdk.getDeviceManager().discoverDevices("scan_ble", false, 20 * 60 * 1000, new IDiscoveryFilter() {

            @Override
            public boolean doFilter(DeviceBasicData deviceBasicData) {
                Log.d(TAG, "doFilter: " + JSON.toJSONString(deviceBasicData));
//                List<BleFoundDevice> localDevices = new ArrayList<>();
//                BleFoundDevice device = new BleFoundDevice();
//                device.copyDeviceBasicData(deviceBasicData);
//                localDevices.add(device);
//                addBleDeviceHandler.addBleDevices(localDevices);
                return false;
            }
        }, new IDevListener() {
            @Override
            public void onSuccess(Object tag, OutputParams outputParams) {
                if (outputParams != null) {
                    List<DeviceBasicData> deviceDataList = DeviceManager.getInstance().getAllDeviceDataList();
                    ALog.d(TAG, "onSuccess: " + JSON.toJSONString(deviceDataList) + "\nOutputParams：" + JSON.toJSONString(outputParams));
                    String dn = (String) outputParams.get("device_name").getValue();
                    String pk = (String) outputParams.get("product_key").getValue();
                    String mt = (String) outputParams.get("model_type").getValue();

                    List<BleFoundDevice> localDevices = new ArrayList<>();
                    for (DeviceBasicData deviceInfo : deviceDataList) {
                        if (dn.equals(deviceInfo.getDeviceName()) && pk.equals(deviceInfo.getProductKey()) && mt.equals(deviceInfo.getModelType())) {
                            BleFoundDevice device = new BleFoundDevice();
                            device.copyDeviceBasicData(deviceInfo);
                            localDevices.add(device);
                        }
                    }
                    addBleDeviceHandler.addBleDevices(localDevices);
                }
            }

            @Override
            public void onFail(Object tag, ErrorInfo errorInfo) {
                ALog.e(TAG, "onFail: " + errorInfo);
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        TmpSdk.getDeviceManager().stopDiscoverDevices();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DEVICE_DETAIL_REQUEST_CODE) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        addBleDeviceHandler.onDestory();
    }

    @Override
    public void onBleDeviceFilterSuccess(List<BleFoundDevice> deviceListItems) {
        for (BleFoundDevice bleFoundDevice : deviceListItems) {
            BleAddView bleAddView = new BleAddView(this);

            bleAddView.setBleDevice(bleFoundDevice);
            llBluetoothList.addView(bleAddView);
        }

    }
}
