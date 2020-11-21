package com.aliyun.iot.ilop.demo.kaiguan.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.demo.R;

import com.aliyun.iot.ilop.demo.kaiguan.bean.SceneDetailsBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.SceneParamsBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.ScenesBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.SwitchDataBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.TimeScenesBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.TriggersTimeScenesBean;
import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanRVAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddSceneActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView add_facility_text,timing_text, all_open_close_text, save_text;
    private EditText name_edit;
    private RecyclerView rv_devicelist;
    private CanRVAdapter mAdapter;
    private List<SceneDetailsBean> mList;

    private final static int REQUESTCODE =1;
    private final static int REQUESTCODE2 =2;
    private final static int FINDTHINGSINFO=111;

    private Handler handler;

    //设备回调数据
    private String mProductName ="";
    private String mProductKey ="";
    private Integer mStatus =0;
    private String mIotId ="";
    private boolean mIsSwitch =false;

    //时间回调数据
    private String mTime = "";
    private String mWeek = "";
    private String mWeekValue = "";
    private List<SwitchDataBean> mList2;
    private boolean mIsTime=false;

    //一键开启或关闭
    private boolean mIsOpenOrClose = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparencyBar(AddSceneActivity.this);
        setContentView(R.layout.activity_add_scene);
        initView();
        //addSceeneData();
        initHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();

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


    private void initView() {
        add_facility_text=findViewById(R.id.add_facility_text);
        add_facility_text.setOnClickListener(this);
        timing_text=findViewById(R.id.timing_text);
        timing_text.setOnClickListener(this);
        name_edit=findViewById(R.id.name_edit);
        all_open_close_text=findViewById(R.id.all_open_close_text);
        all_open_close_text.setOnClickListener(this);
        save_text = findViewById(R.id.save_text);
        save_text.setOnClickListener(this);
        rv_devicelist = findViewById(R.id.rv_devicelist);
        rv_devicelist.setLayoutManager(new GridLayoutManager(AddSceneActivity.this, 1));

        mAdapter = new CanRVAdapter<SceneDetailsBean>(rv_devicelist, R.layout.item_scene_facility_list) {

            @Override
            protected void setView(CanHolderHelper helper, int position, SceneDetailsBean bean) {
                helper.setText(R.id.name_text,bean.getDeviceNickName());
                helper.setText(R.id.devid_text,bean.getProductKey());
                if (bean.isIgnoreRepeatedPropertyValue()){
                    helper.setText(R.id.swtch_dvei_text,"开启");
                }else {
                    helper.setText(R.id.swtch_dvei_text,"关闭");
                }
            }

            @Override
            protected void setItemListener(CanHolderHelper helper) {

            }
        };
        rv_devicelist.setAdapter(mAdapter);

        mList = new ArrayList<>();
        mAdapter.setList(mList);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //添加设备 回调
        if (resultCode==3){
            if(requestCode == REQUESTCODE ) {
                /*Toast.makeText(AddSceneActivity.this, "IotId:" + data.getStringExtra("faclityIotId")
                        + " and ProductKey: " + data.getStringExtra("faclityProductKey")
                        + " and ProductName: " + data.getStringExtra("faclityProductName")
                        + " and faclityStatus: " + data.getStringExtra("faclityStatus")
                        , Toast.LENGTH_LONG).show();*/
                //返回设备信息
                mProductName = data.getStringExtra("faclityProductName").toString();
                mProductKey = data.getStringExtra("faclityProductKey").toString();
                mStatus = Integer.valueOf(data.getStringExtra("faclityStatus"));
                mIotId = data.getStringExtra("faclityIotId").toString();
                mIsSwitch = data.getBooleanExtra("faclitySwitch",false);

                //UI更新
                add_facility_text.setText("已添加设备");
                add_facility_text.setBackground(getResources().getDrawable(R.drawable.selector_btn_wsit));
                add_facility_text.setTextColor(getResources().getColor(R.color.app_nomal_color));

                //name_edit.setText(mProductName);

                //清除当前列表数据
                mAdapter.clear();
                //添加新设备到列表中
                List<SceneDetailsBean> addListDevice = new ArrayList<>();
                SceneDetailsBean sDBean=new SceneDetailsBean();
                sDBean.setDeviceNickName(mProductName);
                if(mIsSwitch){
                    sDBean.setIgnoreRepeatedPropertyValue(true);
                } else {
                    sDBean.setIgnoreRepeatedPropertyValue(false);
                }
                sDBean.setProductKey(mProductKey);
                sDBean.setIotId(mIotId);
                addListDevice.add(sDBean);

                //刷新添加设备的列表
                mList.addAll(addListDevice);
                mAdapter.addMoreList(mList);
                mAdapter.notifyDataSetChanged();

                thingTSL();

            }
        }

        //添加时间 回调
        if (resultCode==4) {
            if (requestCode == REQUESTCODE2) {
                if(data!=null){

                    /*if((List<SwitchDataBean>) data.getSerializableExtra("TimingTimeList")!=null){
                        mList2 = (List<SwitchDataBean>) data.getSerializableExtra("TimingTimeList");

                        for(int i=0; i<mList2.size(); i++){
                            if(mList2.get(i).isEnable()){
                                mIsTime = true;
                            }
                        }
                    }

                    if(mIsTime){
                        //UI更新
                        timing_text.setText("已添加定时");
                        timing_text.setBackground(getResources().getDrawable(R.drawable.selector_btn_wsit));
                        timing_text.setTextColor(getResources().getColor(R.color.app_nomal_color));
                    } else {
                        Toast.makeText(AddSceneActivity.this, "选择的时间没有启动: mlist2 size:" + mList2.size() , Toast.LENGTH_LONG).show();

                        timing_text.setText("已添加定时");
                        timing_text.setBackground(getResources().getDrawable(R.drawable.selector_btn));
                        timing_text.setTextColor(getResources().getColor(R.color.lvse));
                    }*/

                    //回调 选择时间值
                    if(data.getStringExtra("TimingTime")!=null){
                        mTime = data.getStringExtra("TimingTime").toString();
                    } else {
                        mTime="";
                    }
                    if(data.getStringExtra("TimingWeek")!=null){
                        mWeek = data.getStringExtra("TimingWeek").toString();
                    } else {
                        mWeek ="";
                    }
                    //判断时间及星期是否有值
                    if(!mTime.equals("")&&!mWeek.equals("")) {
                        mIsTime = true;
                        int strLength = mWeek.length();
                        //Toast.makeText(AddSceneActivity.this, "时间:" + mTime +"星期:" + mWeek + "|" + "长度:" + strLength, Toast.LENGTH_LONG).show();
                        if(strLength==3){
                            mWeekValue = setStrWeek(mWeek);
                        } else if (strLength==6){
                            String week1 = mWeek.substring(0, 3);
                            String week2 = mWeek.substring(3);
                            mWeekValue = setStrWeek(week1) + "," + setStrWeek(week2);
                        } else if (strLength==9) {
                            String week1 = mWeek.substring(0, 3);
                            String week2 = mWeek.substring(3, 6);
                            String week3 = mWeek.substring(6);
                            mWeekValue = setStrWeek(week1) + "," + setStrWeek(week2) + "," + setStrWeek(week3);
                        } else if (strLength==12) {
                            String week1 = mWeek.substring(0, 3);
                            String week2 = mWeek.substring(3, 6);
                            String week3 = mWeek.substring(6, 9);
                            String week4 = mWeek.substring(9);
                            mWeekValue = setStrWeek(week1) + "," + setStrWeek(week2) + "," + setStrWeek(week3) + "," + setStrWeek(week4);
                        } else if (strLength==15) {
                            String week1 = mWeek.substring(0, 3);
                            String week2 = mWeek.substring(3, 6);
                            String week3 = mWeek.substring(6, 9);
                            String week4 = mWeek.substring(9, 12);
                            String week5 = mWeek.substring(12);
                            mWeekValue = setStrWeek(week1) + "," + setStrWeek(week2) + "," + setStrWeek(week3) + "," + setStrWeek(week4) + "," + setStrWeek(week5);
                        } else if (strLength==18) {
                            String week1 = mWeek.substring(0, 3);
                            String week2 = mWeek.substring(3, 6);
                            String week3 = mWeek.substring(6, 9);
                            String week4 = mWeek.substring(9, 12);
                            String week5 = mWeek.substring(12, 15);
                            String week6 = mWeek.substring(15);
                            mWeekValue = setStrWeek(week1) + "," + setStrWeek(week2) + "," + setStrWeek(week3) + "," + setStrWeek(week4) + "," + setStrWeek(week5) + "," + setStrWeek(week6);
                        }
                        //Toast.makeText(AddSceneActivity.this, "返回:" + mWeekValue + "|", Toast.LENGTH_LONG).show();
                       /* Toast.makeText(AddSceneActivity.this, "时间:" + mTime
                                + "星期:" + mWeek + "|", Toast.LENGTH_LONG).show();*/
                        //UI更新
                        timing_text.setText("已添加定时");
                        timing_text.setBackground(getResources().getDrawable(R.drawable.selector_btn_wsit));
                        timing_text.setTextColor(getResources().getColor(R.color.app_nomal_color));
                    } else {
                        mIsTime = false;
                        Toast.makeText(AddSceneActivity.this, "选择的时间没有启动" , Toast.LENGTH_LONG).show();
                        timing_text.setText("设置定时");
                        timing_text.setBackground(getResources().getDrawable(R.drawable.selector_btn));
                        timing_text.setTextColor(getResources().getColor(R.color.lvse));
                    }


                }
            }
        }


        if(requestCode ==8){
            //Toast.makeText(AddSceneActivity.this, "Data:" + data, Toast.LENGTH_LONG).show();
        }
    }

    //str转换设置星期
    private String setStrWeek(String str){
        String strWeek ="";
        if(str.equals(" 每天")){
            strWeek = "*";
            //Toast.makeText(AddSceneActivity.this, "值:" + "*" + "|", Toast.LENGTH_LONG).show();
        } else if(str.equals(" 周一")){
            strWeek = "1";
        } else if(str.equals(" 周二")){
            strWeek = "2";
        } else if(str.equals(" 周三")){
            strWeek = "3";
        } else if(str.equals(" 周四")){
            strWeek = "4";
        } else if(str.equals(" 周五")){
            strWeek = "5";
        } else if(str.equals(" 周六")){
            strWeek = "6";
        } else if(str.equals(" 周日")){
            strWeek = "7";
        }
        return strWeek;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_facility_text:
                Intent intentAdd=new Intent(AddSceneActivity.this, AddFacilityActivity.class);
                startActivityForResult(intentAdd,  REQUESTCODE);
                break;
            case R.id.timing_text:
                if(mIotId.equals("")) {
                    Toast.makeText(AddSceneActivity.this, "还没有选择开关设备，无法设置定时", Toast.LENGTH_SHORT).show();
                } else {
                    //String code = "link://router/localTimer";
                    //Bundle bundle = new Bundle();
                    //bundle.putString("iotId","ZvZKxr79facEn8T7U03p000100"); // 传入插件参数，没有参数则不需要这一行
                    //bundle.putString("iotId", mIotId);
                    //Router.getInstance().toUrlForResult(AddSceneActivity.this, code, 8, bundle);
                    //重置 是否选择时间
                    mIsTime = false;
                    Intent intentTime=new Intent(AddSceneActivity.this, TimingListActivity.class);
                    startActivityForResult(intentTime,  REQUESTCODE2);
                }
                break;
            case R.id.all_open_close_text:
                if(mIsOpenOrClose){
                    all_open_close_text.setText("一键关闭");
                    all_open_close_text.setTextColor(getResources().getColor(R.color.lvse));
                    all_open_close_text.setBackgroundResource(R.color.color_gray);
                    mIsOpenOrClose = false;
                } else {
                    all_open_close_text.setText("一键开启");
                    all_open_close_text.setTextColor(getResources().getColor(R.color.white));
                    all_open_close_text.setBackgroundResource(R.drawable.selector_btn_wsit);
                    mIsOpenOrClose = true;
                }
                break;
            case R.id.save_text:
                if(mIotId.equals("")) {
                    Toast.makeText(AddSceneActivity.this, "还没有选择开关设备，无法保存", Toast.LENGTH_SHORT).show();
                } else if (name_edit.getText().toString().equals("")) {
                    Toast.makeText(AddSceneActivity.this, "还没有填写场景名称，无法保存", Toast.LENGTH_SHORT).show();
                } else {
                    //更新： 已更新 定时场景和非定时场景在同一方法里面
                    addSceneData();
                    //addTimerSceneData();
                    finish();
                }
                break;
        }
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

    //banlap： 添加新场景
    private void addSceneData() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("catalogId","0");
        maps.put("enable",true);
        maps.put("name",name_edit.getText().toString());
        maps.put("icon","http://media-cdn.tripadvisor.com/media/photo-s/01/3e/05/40/the-sandbar-that-links.jpg");
        maps.put("iconColor","#FFFFFF");

        //设定时间即设置如下参数
        if(mIsTime){
            maps.put("mode","any");
            ArrayList<TriggersTimeScenesBean> arrayList1=new ArrayList<>();

            /*for (int i=0; i<mList2.size(); i++) {
                mTime = mList2.get(i).getName();
                mWeek = mList2.get(i).getWeek();
                String timeHour = mTime.substring(0, mTime.indexOf(":"));
                String timeMinute = (mTime.substring(mTime.indexOf(":"))).substring(1);

                TriggersTimeScenesBean triggersTimeScenesBean = new TriggersTimeScenesBean();
                triggersTimeScenesBean.setUri("condition/timer");

                TimeScenesBean timeScenesBean = new TimeScenesBean();
                timeScenesBean.setCron(timeMinute+ " " + timeHour + " * * *");
                timeScenesBean.setCronType("linux");
                timeScenesBean.setTimezoneID("Asia/Shanghai");
                triggersTimeScenesBean.setParams(timeScenesBean);
                arrayList1.add(triggersTimeScenesBean);
            }*/


            String timeHour = mTime.substring(0, mTime.indexOf(":"));
            String timeMinute = (mTime.substring(mTime.indexOf(":"))).substring(1);


            TriggersTimeScenesBean triggersTimeScenesBean = new TriggersTimeScenesBean();
            triggersTimeScenesBean.setUri("condition/timer");

            TimeScenesBean timeScenesBean = new TimeScenesBean();
            timeScenesBean.setCron(timeMinute + " " + timeHour + " * * " + mWeekValue);
            timeScenesBean.setCronType("linux");
            timeScenesBean.setTimezoneID("Asia/Shanghai");
            triggersTimeScenesBean.setParams(timeScenesBean);
            arrayList1.add(triggersTimeScenesBean);

            JSONArray jsonArray1=(JSONArray) JSONArray.toJSON(arrayList1);
            maps.put("caConditions", jsonArray1);

        }

        //设定设备开关的参数
        ArrayList<ScenesBean> arrayList2=new ArrayList<>();
        for (int i=0; i<mList.size(); i++){
            ScenesBean statusBean=new ScenesBean();
            statusBean.setUri("action/device/setProperty");
            SceneParamsBean paramsBean=new SceneParamsBean();
            paramsBean.setIotId(mList.get(i).getIotId()); //tKLmQ7zPkIYOnKdXMxKt000000
            paramsBean.setPropertyName("LightSwitch");
            if (mList.get(i).isIgnoreRepeatedPropertyValue()){
                paramsBean.setPropertyValue(1);
            } else {
                paramsBean.setPropertyValue(0);
            }
            statusBean.setParams(paramsBean);
            arrayList2.add(statusBean);
        }
        JSONArray jsonArray2=(JSONArray) JSONArray.toJSON(arrayList2);
        //Toast.makeText(AddSceneActivity.this, "json: " + jsonArray2.toString(), Toast.LENGTH_LONG).show();

        maps.put("actions",jsonArray2);
        //maps.put("groupId", 1);
        maps.put("sceneType","CA");
        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/scene/create") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
                .setApiVersion("1.0.5")  // 参照API文档，设置API接口的版本号，本示例为1.0.2
                .setAuthType("iotAuth") // 当云端接口需要用户身份鉴权时需要设置该参数，反之则不需要设置
                .setParams(maps) // 参照API文档，设置API接口的参数，也可以使用.setParams(Map<Strign,Object> params)来设置
                .build();

        // 获取Client实例，并发送请求
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest request, Exception e) {
                // TODO根据e，处理异常
                Log.e("onFailure--",e.getMessage());
                if ("request auth error.".equals(e.getMessage())) {
                    //onLogout();
                }
            }

            @Override
            public void onResponse(IoTRequest request, IoTResponse response) {
                int code = response.getCode();
                final String localizeMsg = response.getLocalizedMsg();
                Log.e("onResponse--",code+"---"+localizeMsg);
                // 200 代表成功
                if(200 != code){
                    //失败示例，参见 "异常数据返回示例"
                    String mesage = response.getMessage();
                    String localizedMsg = response.getLocalizedMsg();
                    //TODO，根据mesage和localizedMsg，处理失败信息

                    return;
                }
                Object data = response.getData();
                //TODO，可以将data转成一个本地的对象或者直接使用JSONObject进行数据解析

                /**
                 * 解析data，data示例参见"正常数据返回示例"
                 * 以下解析示例采用fastjson针对"正常数据返回示例"，解析各个数据节点
                 */
               /* if (data == null) {
                    return;
                }
                JSONObject jsonObject = JSON.parseObject(data.toString());
                //获取业务层code
                String codeBiz = jsonObject.getString("code");
                //获取业务返回的数据
                JSONObject dataBizJsonObject = jsonObject.getJSONObject("data");
                //获取data，data数据是一个JSONArray，即设备列表
                JSONArray devListJsonArray = dataBizJsonObject.getJSONArray("data");
                //后续具体设备信息，则是对devListJsonArray进行一个遍历解析了
                if （devListJsonArray != null） {
                    for (int i = 0; i < devListJsonArray.size(); i++) {
                        JSONObject devJsonObject = devListJsonArray.getJSONObject(i);
                        // TODO 从 devJsonObject 解析出各个字段
                    }
                }*/
            }
        });


    }

    //banlap： 添加新场景 - 预约
    private void addTimerSceneData() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("associatedId",mIotId);
        maps.put("idType","DEVICE");
        maps.put("enable",true);
        maps.put("name",name_edit.getText().toString());
        maps.put("icon","http://media-cdn.tripadvisor.com/media/photo-s/01/3e/05/40/the-sandbar-that-links.jpg");
        maps.put("iconColor","#FFFFFF");

        ArrayList<TriggersTimeScenesBean> arrayList1=new ArrayList<>();
        TriggersTimeScenesBean triggersTimeScenesBean = new TriggersTimeScenesBean();
        triggersTimeScenesBean.setUri("trigger/timer");

        TimeScenesBean timeScenesBean = new TimeScenesBean();
        timeScenesBean.setCron("0 15 17 17 11 ? 2020");
        timeScenesBean.setCronType("quartz_cron");
        timeScenesBean.setTimezoneID("Asia/Shanghai");
        triggersTimeScenesBean.setParams(timeScenesBean);
        arrayList1.add(triggersTimeScenesBean);

        JSONArray jsonArray1=(JSONArray) JSONArray.toJSON(arrayList1);
        Toast.makeText(AddSceneActivity.this, "json1: " + jsonArray1.toString(), Toast.LENGTH_LONG).show();

        Map<String, Object> mapsItem = new HashMap<>();
        mapsItem.put("items", jsonArray1);
        mapsItem.put("uri", "logical/or");
        maps.put("triggers", mapsItem);

        ArrayList<ScenesBean> arrayList2=new ArrayList<>();
        ScenesBean statusBean=new ScenesBean();
        statusBean.setUri("action/device/setProperty");

        SceneParamsBean paramsBean=new SceneParamsBean();
        paramsBean.setIotId(mIotId); //tKLmQ7zPkIYOnKdXMxKt000000
        paramsBean.setPropertyName("LightSwitch");
        paramsBean.setPropertyValue(1);
        statusBean.setParams(paramsBean);
        arrayList2.add(statusBean);

        JSONArray jsonArray2=(JSONArray) JSONArray.toJSON(arrayList2);
        Toast.makeText(AddSceneActivity.this, "json2: " + jsonArray2.toString(), Toast.LENGTH_LONG).show();

        maps.put("actions",jsonArray2);

        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/scene/timing/create") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
                .setApiVersion("1.0.5")  // 参照API文档，设置API接口的版本号，本示例为1.0.2
                .setAuthType("iotAuth") // 当云端接口需要用户身份鉴权时需要设置该参数，反之则不需要设置
                .setParams(maps) // 参照API文档，设置API接口的参数，也可以使用.setParams(Map<Strign,Object> params)来设置
                .build();

        // 获取Client实例，并发送请求
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest request, Exception e) {
                // TODO根据e，处理异常
                Log.e("onFailure--", e.getMessage());
            }

            @Override
            public void onResponse(IoTRequest request, IoTResponse response) {
                int code = response.getCode();
                final String localizeMsg = response.getLocalizedMsg();
                Log.e("onResponse--", code + "---" + localizeMsg);
                // 200 代表成功
                if (200 != code) {
                    //失败示例，参见 "异常数据返回示例"
                    String mesage = response.getMessage();
                    String localizedMsg = response.getLocalizedMsg();
                    //TODO，根据mesage和localizedMsg，处理失败信息
                    return;
                }
                Object data = response.getData();
            }
        });

    }


    private void initHandler() {

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case FINDTHINGSINFO:

                        Object data1 = msg.obj;
                        com.alibaba.fastjson.JSONObject jsonObject1 = JSON.parseObject(data1.toString());
                        com.alibaba.fastjson.JSONObject jsonObject11 = jsonObject1.getJSONObject("LightSwitch");


                        //Toast.makeText(AddSceneActivity.this, "data: " + jsonObject1.toString(), Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };
    }
}