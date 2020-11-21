package com.aliyun.iot.ilop.demo.page.ilopmain;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.framework.AActivity;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.view.EQSettingItemView;
import com.aliyun.iot.ilop.demo.view.SimpleToolBar;
import com.aliyun.iot.link.ui.component.LinkAlertDialog;

public class EquipmentSettingActivity extends AActivity {

    private SimpleToolBar simpleToolBar;
    private LinearLayout unBindEqLl;
    private String iotId;
    private EQSettingItemView eqSettingItemView;
    private final int requestCode = 99;
    private String title;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.equipment_setting_activity);
        initView();
        initData();
        initToolBar();
    }

    private void initData() {
        iotId = getIntent().getStringExtra("iotId");
        title = getIntent().getStringExtra("title");
    }


    private void initView() {
        simpleToolBar = findViewById(R.id.equipment_setting_toolbar);
        unBindEqLl = findViewById(R.id.unbind_eq_ll);
        unBindEqLl.setOnClickListener(unBindEqListener);
        eqSettingItemView = findViewById(R.id.equipment_setting_changeName);
        eqSettingItemView.setOnClickListener(v -> {
            Intent intent = new Intent(EquipmentSettingActivity.this, ChangeEqNameActivity.class);
            intent.putExtra("iotId", iotId);
            intent.putExtra("title", title);
            startActivityForResult(intent, requestCode);
        });
    }

    @Override
    public void onBackPressed() {
        onPressBack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.requestCode && resultCode == LampsActivity.titleResultCode) {
            title = data.getStringExtra("title");
        }
    }

    private void initToolBar() {
        simpleToolBar.setTitle("设备设置")
                .setMenu(false)
                .setBackGround(Color.WHITE)
                .setBack(R.drawable.back_arrow_gray)
                .setOnToolBarClickListener(new SimpleToolBar.OnToolBarClickListener() {
                    @Override
                    public void onBackClick(View var1) {
                        onPressBack();
                    }

                    @Override
                    public void onTitleClick(View var1) {

                    }

                    @Override
                    public void onMenuClick(View var1) {

                    }
                });
    }

    public void onPressBack() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Intent intent = new Intent();
            intent.putExtra("title", title);
            setResult(LampsActivity.titleResultCode, intent);
            finish();
        });
    }

    private View.OnClickListener unBindEqListener = (View v) -> {
        dialog(v);
    };

    public void dialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("确定解绑设备？")
                .setPositiveButton("确定", (dialog, id) -> {
                    unBind();
                    dialog.dismiss();
                })
                .setNegativeButton("取消", (dialog, id) -> {
                    dialog.dismiss();
                });
        builder.show();
    }

    public void unBind() {
        // TODO: 2018/6/15 解绑
        EqSettingHelp.requestUnbind(iotId, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                e.printStackTrace();
                unBindEqLl.post(() -> Toast.makeText(getApplicationContext(), "解绑失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                Log.d("EqSettingHelp", "Success");
                unBindEqLl.post(() -> Toast.makeText(getApplicationContext(), "解绑成功", Toast.LENGTH_SHORT).show());
                setResult(LampsActivity.finishResultCode, new Intent());
                finish();
            }
        });
    }


}
