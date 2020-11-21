package com.aliyun.iot.ilop.demo.page.device.bind;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.framework.AActivity;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.device.bean.Device;


public class BindAndUseActivity extends AActivity {
    private String TAG = BindAndUseActivity.class.getSimpleName();
    private Button bindAndUseBtn;
    private Button mBackBtn;
    private Handler mHandler = new Handler();
    private DeviceBindBusiness deviceBindBusiness;
    String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_and_use_activity);

        mBackBtn = (Button) findViewById(R.id.ilop_bind_back_btn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String pk = "";
        String dn = "";
        String token = "";
        String iotId = "";
        Intent intent = getIntent();
        if (null != intent) {
            pk = intent.getStringExtra("productKey");
            dn = intent.getStringExtra("deviceName");
            token = intent.getStringExtra("token");
            iotId = intent.getStringExtra("iotId");
            groupId = intent.getStringExtra("groupId");
        }
        // should not happen
        if (TextUtils.isEmpty(pk)
                || TextUtils.isEmpty(dn)) {
            ALog.e(TAG, "pk & dn can not be empty");
            finish();
        }

        final Device device = new Device();
        device.pk = pk;
        device.dn = dn;
        device.token = token;
        device.iotId = iotId;
        deviceBindBusiness = new DeviceBindBusiness();
        //查询产品信息
        Log.e(TAG, "onCreate: " + pk + "   " + dn);
        deviceBindBusiness.setGroupId(groupId);
        deviceBindBusiness.queryProductInfo(device);

        bindAndUseBtn = (Button) findViewById(R.id.bind_and_use_btn);
        bindAndUseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deviceBindBusiness.bindDevice(device, new OnBindDeviceCompletedListener() {
                    @Override
                    public void onSuccess(String iotId) {
                        Router.getInstance().toUrl(BindAndUseActivity.this, "page/ilopmain");
                        finish();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        ALog.e("TAG", "bindDevice onFail s = " + e);
                        Toast.makeText(getApplicationContext(), "bindDeviceFailed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int code, String message, String localizedMsg) {
                        ALog.d("TAG", "onFailure");
                        Toast.makeText(getApplicationContext(), "code = " + code + " msg =" + message, Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }


}
