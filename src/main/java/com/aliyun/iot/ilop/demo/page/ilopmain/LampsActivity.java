package com.aliyun.iot.ilop.demo.page.ilopmain;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.alink.linksdk.tmp.TmpSdk;
import com.aliyun.alink.linksdk.tmp.api.OutputParams;
import com.aliyun.alink.linksdk.tmp.device.panel.PanelDevice;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelCallback;
import com.aliyun.alink.linksdk.tmp.device.panel.listener.IPanelEventCallback;
import com.aliyun.alink.linksdk.tmp.listener.IDevListener;
import com.aliyun.alink.linksdk.tmp.utils.ErrorInfo;
import com.aliyun.iot.aep.sdk.framework.AActivity;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.dialog.HSVDialog;
import com.aliyun.iot.ilop.demo.page.bean.EventCallbackbean;
import com.aliyun.iot.ilop.demo.page.bean.PalettesDialogBean;
import com.aliyun.iot.ilop.demo.page.bean.RequestInvokeServiceBean;
import com.aliyun.iot.ilop.demo.page.bean.RequestPropertiesBean;
import com.aliyun.iot.ilop.demo.page.bean.ResponsePropertiesBean;
import com.aliyun.iot.ilop.demo.page.bean.StatusBean;
import com.aliyun.iot.ilop.demo.utils.ColorTools;
import com.aliyun.iot.ilop.demo.view.CircleView;
import com.aliyun.iot.ilop.demo.view.LampsSwitchView;
import com.aliyun.iot.ilop.demo.view.SimpleToolBar;
import com.google.gson.Gson;

import java.util.List;

public class LampsActivity extends AActivity {
    private static final String TAG = "LampsActivity";
    private static final String OFF_LINE_HINT = "设备离线，无法操作";
    private SimpleToolBar simpleToolBar;
    private LinearLayout palettesLl;
    private CircleView hsvCircleView;
    private String iotId;
    private PanelDevice panelDevice;
    private LampsSwitchView leftLampsSwitchView, rigthLampsSwitchView;
    private TextView statusTv;
    private Gson gson;
    private ResponsePropertiesBean responsePropertiesBean;
    private RequestPropertiesBean requestPropertiesBean = new RequestPropertiesBean();
    private boolean rightOnLineFlag;
    private boolean leftOnLineFlag;
    private static int requestCode = 96;
    public static int finishResultCode = 97;
    public static int titleResultCode = 98;
    private String title;
    private int status = 0;
    private boolean firstPropertiesFlag = false;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.lamps_activity);
        initView();
        initData();
        initToolBar();
        initSdk();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.requestCode && resultCode == this.finishResultCode) {
            finish();
        }
        if (requestCode == this.requestCode && resultCode == this.titleResultCode) {
            title = data.getStringExtra("title");
            simpleToolBar.setTitle(title);
        }
    }

    /**
     * initView
     **/
    private void initView() {
        simpleToolBar = findViewById(R.id.lamps_toolbar);
        palettesLl = findViewById(R.id.lamps_palettes_ll);
        palettesLl.setOnClickListener(onPalettesClick);
        hsvCircleView = findViewById(R.id.lamps_palettes_circle);
        leftLampsSwitchView = findViewById(R.id.lamps_lamps_left_lsv);
        rigthLampsSwitchView = findViewById(R.id.lamps_lamps_right_lsv);
        statusTv = findViewById(R.id.lamps_lamps_status_tv);
        gson = new Gson();
        leftLampsSwitchView.setOnClickListener(v -> {
            if (status == 0) {
                Toast.makeText(this, OFF_LINE_HINT, Toast.LENGTH_SHORT).show();
                return;
            }

            requestPropertiesBean.getItems()
                    .setLightSwitch(switchOpposite(requestPropertiesBean.getItems().getLightSwitch()));
            setProperties(requestPropertiesBean);

        });
        rigthLampsSwitchView.setOnClickListener(v -> {
            if (status == 0) {
                Toast.makeText(this, OFF_LINE_HINT, Toast.LENGTH_SHORT).show();
                return;
            }
            //翻转开关
            RequestInvokeServiceBean bean = new RequestInvokeServiceBean(iotId, "reverseSwitch",
                    new RequestInvokeServiceBean.Args(1));
            invokeService(gson.toJson(bean));
        });
    }

    public int switchOpposite(int statues) {
        if (statues == 0) return 1;
        return 0;
    }

    /**
     * toolbar
     **/
    private void initToolBar() {
        simpleToolBar.setTitle(title)
                .setTitleColor(Color.WHITE)
                .setBack(true)
                .setMenu(true)
                .isShowBottomLine(false)
                .setBackGround(Color.TRANSPARENT)
                .setOnToolBarClickListener(new SimpleToolBar.OnToolBarClickListener() {
                    @Override
                    public void onBackClick(View var1) {
                        finish();
                    }

                    @Override
                    public void onTitleClick(View var1) {

                    }

                    @Override
                    public void onMenuClick(View var1) {
                        Intent intent = new Intent(LampsActivity.this, EquipmentSettingActivity.class);
                        intent.putExtra("iotId", iotId);
                        intent.putExtra("title", title);
                        startActivityForResult(intent, requestCode);
                    }
                });
        leftLampsSwitchView.setTitle("主灯开关");
        rigthLampsSwitchView.setTitle("翻转开关");
    }

    private void initData() {
        iotId = getIntent().getStringExtra("iotId");
        title = !TextUtils.isEmpty(getIntent().getStringExtra("title")) ?
                getIntent().getStringExtra("title") :
                "智能灯";
        requestPropertiesBean.setIotId(iotId);
        firstPropertiesFlag = false;
    }

    private void initSdk() {
//        设备创建
        panelDevice = new PanelDevice(iotId);
//        初始化
        panelDevice.init(this, initCallback);
        TmpSdk.getDeviceManager().discoverDevices(null, false, 5000, new IDevListener() {
            @Override
            public void onSuccess(Object o, OutputParams outputParams) {
            }

            @Override
            public void onFail(Object o, ErrorInfo errorInfo) {
            }
        });
    }

    /**
     * 上报 （第一次给虚拟设备设置相关属性）
     */
    private void setFirstProperties() {
        firstPropertiesFlag = true;
        RequestPropertiesBean.Items.HSVColor color = new RequestPropertiesBean.Items.HSVColor(0.0948, 1.0000, 1.0000);
        RequestPropertiesBean bean = new RequestPropertiesBean(iotId, new RequestPropertiesBean.Items(0, 0, color));
        setProperties(bean);
    }

    /**
     * 获取状态
     */
    public void getEqStatus() {
        panelDevice.getStatus(statusCallback);
    }

    /**
     * 获取设备属性
     */
    public void getProperties() {
        ALog.d(TAG, "getProperties");
        panelDevice.getProperties(getEqPropsCallBack);
    }

    /**
     * 设置设备属性
     */
    public void setProperties(RequestPropertiesBean bean) {
        String params = gson.toJson(bean, RequestPropertiesBean.class);
        ALog.d(TAG, "==params==" + params);
        panelDevice.setProperties(params, setEqPropsCallBack);
    }

    /**
     * 调用服务
     */
    public void invokeService(String params) {
        panelDevice.invokeService(params, invokeServiceCallBack);
    }

    /**
     * 订阅所有事件
     */
    public void subAllEvents() {
        ALog.d(TAG, "subAllEvents");
        panelDevice.subAllEvents(eventCallback, (b, o) -> ALog.d(TAG, b + "subAllEvents==" + String.valueOf(o)));
    }

    /**
     * 调色板LinearLayout点击事件
     **/
    private View.OnClickListener onPalettesClick = v -> {
        if (status == 0) {
            Toast.makeText(this, OFF_LINE_HINT, Toast.LENGTH_SHORT).show();
            return;
        }
        HSVDialog.getInstance().showMenuDialog(this, queryColor(responsePropertiesBean), ((arr, position) -> {
            //设置颜色
            hsvCircleView.setColor(arr.get(position).getColor());
            // 发给服务端HSV颜色
            float[] hsv = arr.get(position).getHsv();
            requestPropertiesBean.getItems().setHSVColor(new RequestPropertiesBean.Items.HSVColor(hsv));
            setProperties(requestPropertiesBean);
        }));
    };

    /**
     * 更新ui
     *
     * @param responsePropertiesBean
     */
    private void refreshUi(ResponsePropertiesBean responsePropertiesBean) {
        leftOnLineFlag = false;
        if (responsePropertiesBean.getData().getLightSwitch().getValue() == 1) {
            leftOnLineFlag = true;
        }
        leftLampsSwitchView.setOnline(leftOnLineFlag);
        rigthLampsSwitchView.setOnline(false);
        /**
         *
         * 设置颜色
         */
        new Handler(getMainLooper()).post(() -> {
            hsvCircleView.setColor(queryColor(responsePropertiesBean));
        });
    }

    /**
     * 根据HSV查找16进制颜色
     */
    public String queryColor(ResponsePropertiesBean responsePropertiesBean) {
        if (null == responsePropertiesBean) {
            return "";
        }
        List<PalettesDialogBean> arr = ColorTools.getColorData(this);
        for (int i = 0; i < arr.size(); i++) {
            if ((int) (arr.get(i).getHsv()[0] * 100) == responsePropertiesBean.getData().getHSVColor().getValue().getHue()) {
                return arr.get(i).getColor();
            }
        }
        return "";
    }

    //=========================== 回调==================================

    /**
     * 初始化
     * 成功后获取设备状态 和 设备属性
     */
    private IPanelCallback initCallback = (initFlag, o) -> {
        if (initFlag) {
            getEqStatus();
            getProperties();
            subAllEvents();
        }
        if (!initFlag) ALog.e(TAG, "initSdk fail");
        if (TextUtils.isEmpty(String.valueOf(o))) ALog.e(TAG, "initCallback Object is null");
    };

    /**
     * 获取设备状态
     */
    private IPanelCallback statusCallback = (bSuc, o) -> {

        StatusBean bean = gson.fromJson(String.valueOf(o), StatusBean.class);
        status = bean.getData().getStatus();
        if (bean.getData() != null && bean.getData().getStatus() == 1) {
            statusTv.post(() -> statusTv.setText("设备在线"));
        } else {
            statusTv.post(() -> statusTv.setText("设备离线"));
        }
    };
    /**
     * 获取设备属性
     */
    private IPanelCallback getEqPropsCallBack = (bSuc, o) -> {
        firstPropertiesFlag = false;
        ALog.d(TAG, "getEqPropsCallBack" + String.valueOf(o));
        //解析
        responsePropertiesBean = gson.fromJson(String.valueOf(o), ResponsePropertiesBean.class);
        //如果没有属性 需要进行设置属性
        if (null == responsePropertiesBean.getData().getLightSwitch()) {
            setFirstProperties();
            return;
        }
        /**
         * 订阅mqtt
         */

        //设置请求实体类
        requestPropertiesBean.setItems(
                new RequestPropertiesBean.Items(responsePropertiesBean.getData().getLightSwitch().getValue(),
                        0));
        refreshUi(responsePropertiesBean);
    };


    /**
     * 设置设备属性
     */
    private IPanelCallback setEqPropsCallBack = (bSuc, o) -> {
        if (!TextUtils.isEmpty(String.valueOf(o))) {
            ALog.d(TAG, "setEqPropsCallBack" + String.valueOf(o));
        }
        if (bSuc && firstPropertiesFlag) {
            getProperties();
        }
    };

    /**
     * 调用服务
     */
    private IPanelCallback invokeServiceCallBack = (bSuc, o) -> {
        ALog.d(TAG, bSuc + "invokeServiceCallBack" + String.valueOf(o));
        if (bSuc) {
            rigthLampsSwitchView.post(() -> {
                Toast.makeText(this, "请求成功", Toast.LENGTH_SHORT).show();
            });
        }
    };

    /**
     * 订阅事件回调
     *
     * @iotid 参数是设备iotid
     * @topic 参数是回调的事件主题字符串
     * @Object data 是触发事件的内容
     */
    private IPanelEventCallback eventCallback = (iotid, topic, data) -> {
        ALog.d(TAG, "eventCallback_data:" + data);
        if (iotid.equals(iotId)) {
            EventCallbackbean bean = gson.fromJson(String.valueOf(data), EventCallbackbean.class);
            if (null == responsePropertiesBean || null == responsePropertiesBean.getData().getLightSwitch())
                return;
            if (TextUtils.isEmpty(String.valueOf(bean.getItems().getLightSwitch().getTime()))) {
                return;
            }
            if (null != bean.getItems().getLightSwitch() || !TextUtils.isEmpty(String.valueOf(bean.getItems().getLightSwitch().getTime()))) {
                responsePropertiesBean.getData().setLightSwitch(new ResponsePropertiesBean.DataBean.LightSwitchBean(bean.getItems().getLightSwitch().getValue()));
            }
            if (null != bean.getItems().getHSVColor() && bean.getItems().getHSVColor().getValue().getHue() != 999) {
                responsePropertiesBean.getData().getHSVColor().getValue().setHue(bean.getItems().getHSVColor().getValue().getHue());
                responsePropertiesBean.getData().getHSVColor().getValue().setSaturation(bean.getItems().getHSVColor().getValue().getSaturation());
                responsePropertiesBean.getData().getHSVColor().getValue().setValue(bean.getItems().getHSVColor().getValue().getValue());
            }
            refreshUi(responsePropertiesBean);
        }
    };
}
