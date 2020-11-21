package com.aliyun.iot.ilop.demo.page.device.adddevice;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo;
import com.aliyun.alink.business.devicecenter.api.discovery.DiscoveryType;
import com.aliyun.alink.business.devicecenter.api.discovery.IDeviceDiscoveryListener;
import com.aliyun.alink.business.devicecenter.api.discovery.LocalDeviceMgr;
import com.aliyun.iot.aep.sdk.framework.AActivity;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.device.bean.FoundDeviceListItem;
import com.aliyun.iot.ilop.demo.page.device.bean.SupportDeviceListItem;
import com.aliyun.iot.ilop.demo.page.device.bind.BindAndUseActivity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class AddDeviceActivity extends AActivity implements OnDeviceAddListener {
    private String TAG = AddDeviceActivity.class.getSimpleName();

    private DeviceAddHandler deviceAddHandler;
    private AddDeviceAdapter addDeviceAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_device_activity);
        deviceAddHandler = new DeviceAddHandler(this);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initView();
        deviceAddHandler.getSupportDeviceListFromSever();
    }

    private void initView() {
        findViewById(R.id.ilop_main_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //蓝牙添加
        findViewById(R.id.btn_deviceadd_ble).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //蓝牙方式添加设备
                if (ActivityCompat.checkSelfPermission(AddDeviceActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddDeviceActivity.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 0x2222);
                } else {
                    openBle();
                }

            }
        });

        RecyclerView recyclerView = findViewById(R.id.rv_device);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        addDeviceAdapter = new AddDeviceAdapter(this);
        recyclerView.setAdapter(addDeviceAdapter);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && null != data) {
            Log.d("TAG", "onActivityResult");
            if (data.getStringExtra("productKey") != null) {
                final String productKey = data.getStringExtra("productKey");
                final String deviceName = data.getStringExtra("deviceName");
                String token = data.getStringExtra("token");
                String iotId = data.getStringExtra("iotId");

                Intent intent = new Intent(getApplicationContext(), BindAndUseActivity.class);
                final Bundle bundle = new Bundle();
                bundle.putString("productKey", productKey);
                bundle.putString("deviceName", deviceName);
                bundle.putString("token", token);
                bundle.putString("iotId", iotId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }

    private void openBle() {
        ThreadPool.DefaultThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                enable();
                ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        //蓝牙添加
                        Intent intent = new Intent(AddDeviceActivity.this, BluetoothAddActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

    }

    /**
     * 打开蓝牙
     *
     * @return
     */
    public static boolean enable() {
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            return mBluetoothAdapter.enable();
        } else {
            return true;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取之前发现的所有设备
        addDeviceAdapter.resetLocalDevice();
        deviceAddHandler.reset();
        //发现局域网内设备以及WiFi热点设备
        EnumSet<DiscoveryType> discoveryTypeEnumSet = EnumSet.allOf(DiscoveryType.class);
        LocalDeviceMgr.getInstance().startDiscovery(this, discoveryTypeEnumSet, null, new IDeviceDiscoveryListener() {
            @Override
            public void onDeviceFound(DiscoveryType discoveryType, List<DeviceInfo> list) {
                Log.d("DeviceAddBusiness", "--发现设备--" + JSON.toJSONString(list));
                List<FoundDeviceListItem> foundDeviceListItems = new ArrayList<>();
                for (DeviceInfo deviceInfo : list) {
                    final FoundDeviceListItem deviceListItem = new FoundDeviceListItem();
                    if (discoveryType == DiscoveryType.LOCAL_ONLINE_DEVICE) {
                        deviceListItem.deviceStatus = FoundDeviceListItem.NEED_BIND;
                    } else if (discoveryType == DiscoveryType.CLOUD_ENROLLEE_DEVICE) {
                        deviceListItem.deviceStatus = FoundDeviceListItem.NEED_CONNECT;
                    }
                    deviceListItem.discoveryType = discoveryType;
                    deviceListItem.deviceInfo = deviceInfo;
                    deviceListItem.deviceName = deviceInfo.deviceName;
                    deviceListItem.productKey = deviceInfo.productKey;
                    foundDeviceListItems.add(deviceListItem);

                    Toast.makeText(AddDeviceActivity.this, "发现设备token：" + deviceInfo.token, Toast.LENGTH_LONG).show();
                }
                deviceAddHandler.filterDevice(foundDeviceListItems);

            }

        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x2222) {
            if (permissions[0].equalsIgnoreCase(Manifest.permission.BLUETOOTH_ADMIN) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openBle();
            }
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        LocalDeviceMgr.getInstance().stopDiscovery();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        deviceAddHandler.onDestory();
    }


    @Override
    public void showToast(String message) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSupportDeviceSuccess(List<SupportDeviceListItem> mSupportDeviceListItems) {
        Log.e(TAG, "onSupportDeviceSsuccess: " + mSupportDeviceListItems);
        addDeviceAdapter.setSupportDevices(mSupportDeviceListItems);
    }

    @Override
    public void onFilterComplete(List<FoundDeviceListItem> foundDeviceListItems) {
        addDeviceAdapter.addLocalDevice(foundDeviceListItems);
    }
}
