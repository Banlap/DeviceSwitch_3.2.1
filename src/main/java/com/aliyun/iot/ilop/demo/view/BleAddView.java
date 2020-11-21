package com.aliyun.iot.ilop.demo.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;

import com.aliyun.alink.linksdk.tmp.extbone.BoneSubDeviceService;
import com.aliyun.alink.linksdk.tmp.service.DevService;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.device.adddevice.BluetoothAddActivity;
import com.aliyun.iot.ilop.demo.page.device.bean.BleFoundDevice;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BleAddView extends LinearLayout implements View.OnClickListener {


    private static final String TAG = BleAddView.class.getSimpleName();
    private TextView mTitle;
    private FrameLayout btnArraw;
    private BleFoundDevice bleDevice;
    private FrameLayout btnAdd;
    private String addrMac;
    private static Status status = Status.NONE;
    private Status localstatus = Status.NONE;

    private enum Status {
        NONE, LOADING, DONE;
    }


    public BleAddView(Context context) {
        this(context, null);
    }

    public BleAddView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BleAddView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_add_ble, this, true);
        mTitle = findViewById(R.id.tv_title);
        btnAdd = findViewById(R.id.device_add);
        btnAdd.setOnClickListener(this);
        btnArraw = findViewById(R.id.device_add_arraw);
        btnArraw.setOnClickListener(this);
    }


    public void setBleDevice(BleFoundDevice bleDevice) {
        this.bleDevice = bleDevice.clone();
        addrMac = bleDevice.deviceName;
        mTitle.setText(this.bleDevice.deviceName);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.device_add) {
            if (status == Status.NONE || (status == Status.DONE && localstatus == Status.NONE)) {
                findViewById(R.id.deviceadd_iv_bleadd_bg).setSelected(true);
                breezeSubDevLogin(bleDevice);
            } else if (status == Status.LOADING) {//提示，正在由设备进行绑定
                Toast.makeText(getContext(), "正在绑定设备，请稍后", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.device_add_arraw) {
            Bundle bundle = new Bundle();
            bundle.putString("iotId", bleDevice.getIotId());
            String url = "link://router/" + bleDevice.productKey;
            Router.getInstance().toUrlForResult((Activity) v.getContext(), url, BluetoothAddActivity.DEVICE_DETAIL_REQUEST_CODE, bundle);
        }


    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        status = Status.NONE;
    }


    //蓝牙设备上线
    private void breezeSubDevLogin(final BleFoundDevice localDevice) {
        status = Status.LOADING;
        localstatus = Status.LOADING;
        DevService.breezeSubDevLogin(localDevice.productKey, localDevice.deviceName, new DevService.ServiceListener() {
            @Override
            public void onComplete(boolean b, Object o) {
                Log.d(TAG, "connect result: status:" + b + " bundle:" + o);
                if (b && o instanceof Map) {
                    status = Status.LOADING;
                    localstatus = Status.LOADING;
                    Map<String, Object> bundle = (Map<String, Object>) o;
                    String deviceName = bundle.get(DevService.BUNDLE_KEY_DEVICENAME).toString();
                    String productKey = bundle.get(DevService.BUNDLE_KEY_PRODUCTKEY).toString();
                    localDevice.deviceName = deviceName;
                    localDevice.productKey = productKey;
                    Log.d(TAG, "localDevice: " + localDevice.toString());
                    userBindByTimeWindow(localDevice);
                } else {
                    status = Status.DONE;
                    localstatus = Status.NONE;
                    onBindError();
                }
            }
        });
    }

    //蓝牙绑定
    private void userBindByTimeWindow(final BleFoundDevice bleDevice) {
        Map<String, Object> device = new HashMap<>(2);
        device.put("productKey", bleDevice.productKey);
        device.put("deviceName", bleDevice.deviceName);
        IoTRequest request = new IoTRequestBuilder()
                .setPath("/awss/time/window/user/bind")
                .setApiVersion("1.0.3")
                .setParams(device)
                .setAuthType("iotAuth")
                .build();
        new IoTAPIClientFactory().getClient().send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                ALog.e(TAG, "onFailure: " + e);
                onBindError();
                status = Status.DONE;
                localstatus = Status.NONE;
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                if (ioTResponse.getCode() == 200) {
                    bleDevice.iotId = ioTResponse.getData().toString();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("deviceName", addrMac);
                        jsonObject.put("productKey", bleDevice.productKey);
                        jsonObject.put("iotId", bleDevice.iotId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "notifySubDeviceBinded: " + jsonObject);
                    //通知sdk
                    new BoneSubDeviceService().notifySubDeviceBinded(jsonObject, null);

                    localstatus = Status.DONE;
                    onBindSuccess();

                } else {
                    localstatus = Status.NONE;
                    onBindError();
                }
                status = Status.DONE;
            }
        });

    }


    private void onBindError() {
        ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.deviceadd_iv_bleadd_bg).setSelected(false);
                Toast.makeText(getContext(), "设备添加失败,请靠近蓝牙或重启设备", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void onBindSuccess() {
        ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.deviceadd_iv_bleadd_bg).setSelected(false);
                //显示进入插件的箭头
                btnAdd.setVisibility(GONE);
                btnArraw.setVisibility(VISIBLE);
            }
        });
    }


}
