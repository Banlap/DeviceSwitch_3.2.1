package com.aliyun.iot.ilop.demo.kaiguan.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.kaiguan.bean.ActionMsgSendBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.CAConditionsBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.ConditonBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.CustomDataBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.MsgSendBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.SceneDetailsBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.SceneParamsBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.ScenesBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.ScenesListBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.SwitchDataBean;
import com.aliyun.iot.ilop.demo.kaiguan.utils.DialogUtil;
import com.aliyun.iot.ilop.demo.page.device.adddevice.AddDeviceActivity;
import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanRVAdapter;
import com.google.gson.JsonArray;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SceneDetailsActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int SCENEDETAILSWHAT = 0003;
    private RecyclerView rv_eventlist;
    private CanRVAdapter adapter;
    private TextView time_text;
    private Handler handler;
    private CircleImageView open_vip_headicon;
    private TextView name_text;
    private TextView sid_text;
    private DialogUtil dialogUtil;
    private TextView delete_text;
    private String sceneId;
    private static final int DELETATAGE=0004;
    private TextView sve_data;
    private com.alibaba.fastjson.JSONObject jsonObject;
    private List<SceneDetailsBean> list;
    private ArrayList<ScenesBean> mArrayList;
    private ImageView add_device;

    //选择设备 回调数据
    private String mProductName ="";
    private String mProductKey ="";
    private Integer mStatus =0;
    private String mIotId ="";
    private static final int ADDNEWDEVICE = 0005;

    //选择时间 回调数据
    private String mTime = "";
    private String mWeek = "";
    private List<SwitchDataBean> mArrayList2;

    private int mRequestCode =0; //请求码 - 回调
    private int mResultCode =0;  //结果码 - 回调
    private static final int UPDATESCENE = 0006;

    //test
    private JSONArray newJsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparencyBar(SceneDetailsActivity.this);
        setContentView(R.layout.activity_scene_details);
        initView();
        initHadler();
        initDailog();

    }

    private void initHadler() {
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case SCENEDETAILSWHAT:
                        Object data=msg.obj;
                        jsonObject = JSON.parseObject(data.toString());
                        name_text.setText(jsonObject.getString("name"));
                        sid_text.setText(jsonObject.getString("id"));
                        //Toast.makeText(SceneDetailsActivity.this,"数据：" + jsonObject,Toast.LENGTH_SHORT).show();

                        //获取action动作
                        JSONArray jsonArray=jsonObject.getJSONArray("actionsJson");
                        newJsonArray = jsonArray;
                        list=new ArrayList<>();
                        mArrayList=new ArrayList<>();
                        for (int i = 0; i <jsonArray.size() ; i++) {
                            JSONObject jsonObject1=JSON.parseObject(jsonArray.get(i).toString());
                            JSONObject jsonObject2=jsonObject1.getJSONObject("params");


                            SceneDetailsBean sceneDetailsBean=new SceneDetailsBean();
                            sceneDetailsBean.setDeviceNickName(jsonObject2.getString("deviceNickName"));
                            sceneDetailsBean.setIgnoreRepeatedPropertyValue(jsonObject2.getBoolean("ignoreRepeatedPropertyValue"));
                            sceneDetailsBean.setProductKey(jsonObject2.getString("productKey"));
                            list.add(sceneDetailsBean);

                            //传入当前设备开关到列表，用于保存场景
                            ScenesBean statusBean=new ScenesBean();
                            statusBean.setUri("action/device/setProperty");
                            SceneParamsBean sceneParamsBean=new SceneParamsBean();
                            sceneParamsBean.setIotId(jsonObject2.getString("iotId"));
                            sceneParamsBean.setPropertyName(jsonObject2.getString("propertyName"));
                            sceneParamsBean.setPropertyValue(jsonObject2.getInteger("propertyValue"));
                            statusBean.setParams(sceneParamsBean);
                            mArrayList.add(statusBean);
                        }
                        //Toast.makeText(SceneDetailsActivity.this, "jsonArray: " + jsonArray.get(0).toString(), Toast.LENGTH_LONG).show();
                        adapter.setList(list);
                        adapter.notifyDataSetChanged();


                        mArrayList2 = new ArrayList<>();
                        //获取action动作
                        JSONArray jsonArray2=jsonObject.getJSONArray("caConditionsJson");
                        if(jsonArray2!=null){
                            for (int i = 0; i <jsonArray2.size() ; i++) {
                                JSONObject jsonObject3=JSON.parseObject(jsonArray2.get(i).toString());
                                JSONObject jsonObject4=jsonObject3.getJSONObject("params");

                                //获取字符串分钟
                                String cronStr = jsonObject4.getString("cron");
                                String timeMinute = cronStr.substring(0, cronStr.indexOf(" "));
                                //获取字符串小时
                                String cronStr2 = (cronStr.substring(cronStr.indexOf(" "))).substring(1);
                                String timeHour = cronStr2.substring(0, cronStr2.indexOf(" "));
                                //获取字符串星期
                                String cronStr3 = (cronStr2.substring(cronStr2.indexOf(" "))).substring(1);
                                cronStr3 = (cronStr3.substring(cronStr3.indexOf(" "))).substring(1);
                                String timeWeek = (cronStr3.substring(cronStr3.indexOf(" "))).substring(1);

                                String strWeek = "";
                                int timewWeekLength = timeWeek.length();
                                if(timewWeekLength==1){
                                    strWeek = setStrWeekValue(timeWeek);
                                } else if(timewWeekLength==3) {
                                    String week1 = timeWeek.substring(0, 1);
                                    String week2 = timeWeek.substring(2);
                                    strWeek = setStrWeekValue(week1) + setStrWeekValue(week2);
                                } else if(timewWeekLength==5){
                                    String week1 = timeWeek.substring(0, 1);
                                    String week2 = timeWeek.substring(2,3);
                                    String week3 = timeWeek.substring(4);
                                    strWeek = setStrWeekValue(week1) + setStrWeekValue(week2) + setStrWeekValue(week3);
                                } else if(timewWeekLength==7){
                                    String week1 = timeWeek.substring(0, 1);
                                    String week2 = timeWeek.substring(2,3);
                                    String week3 = timeWeek.substring(4,5);
                                    String week4 = timeWeek.substring(6);
                                    strWeek = setStrWeekValue(week1) + setStrWeekValue(week2) + setStrWeekValue(week3) + setStrWeekValue(week4);
                                } else if(timewWeekLength==9){
                                    String week1 = timeWeek.substring(0, 1);
                                    String week2 = timeWeek.substring(2,3);
                                    String week3 = timeWeek.substring(4,5);
                                    String week4 = timeWeek.substring(6,7);
                                    String week5 = timeWeek.substring(8);
                                    strWeek = setStrWeekValue(week1) + setStrWeekValue(week2) + setStrWeekValue(week3) + setStrWeekValue(week4) + setStrWeekValue(week5);
                                } else if(timewWeekLength==11){
                                    String week1 = timeWeek.substring(0, 1);
                                    String week2 = timeWeek.substring(2,3);
                                    String week3 = timeWeek.substring(4,5);
                                    String week4 = timeWeek.substring(6,7);
                                    String week5 = timeWeek.substring(8,9);
                                    String week6 = timeWeek.substring(10);
                                    strWeek = setStrWeekValue(week1) + setStrWeekValue(week2) + setStrWeekValue(week3) + setStrWeekValue(week4) + setStrWeekValue(week5) + setStrWeekValue(week6);
                                }
                                //字符串合并成时间
                                String time = timeHour + ":" + timeMinute;
                                SwitchDataBean switchDataBean = new SwitchDataBean();
                                switchDataBean.setName(time);
                                switchDataBean.setWeek(strWeek);
                                if(jsonObject3.getInteger("status") ==1){
                                    switchDataBean.setEnable(true);
                                } else {
                                    switchDataBean.setEnable(false);
                                }
                                mArrayList2.add(switchDataBean);
                                //Toast.makeText(SceneDetailsActivity.this, "timeMin: " + cronStr + "|", Toast.LENGTH_LONG).show();
                                //Toast.makeText(SceneDetailsActivity.this, "timeWeek: " + timeWeek + "|", Toast.LENGTH_LONG).show();
                                //Toast.makeText(SceneDetailsActivity.this, "strWeek: " + strWeek + "|", Toast.LENGTH_LONG).show();

                            }
                        }
                        break;
                    case DELETATAGE:
                        Toast.makeText(SceneDetailsActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                        finish();
                        break;

                    case ADDNEWDEVICE:
                        adapter.clear();

                        List<SceneDetailsBean> addListDevice = new ArrayList<>();
                        SceneDetailsBean sDBean=new SceneDetailsBean();
                        sDBean.setDeviceNickName(mProductName);
                        sDBean.setIgnoreRepeatedPropertyValue(true);
                        sDBean.setProductKey(mProductKey);
                        addListDevice.add(sDBean);

                        //刷新设备开关列表
                        list.addAll(addListDevice);
                        adapter.addMoreList(list);
                        adapter.notifyDataSetChanged();

                        //传入新增的设备开关到列表，用于保存场景
                        ScenesBean statusBean=new ScenesBean();
                        statusBean.setUri("action/device/setProperty");
                        SceneParamsBean sceneParamsBean=new SceneParamsBean();
                        sceneParamsBean.setIotId(mIotId);
                        sceneParamsBean.setPropertyName(mProductName);
                        sceneParamsBean.setPropertyValue(mArrayList.size()+1);
                        statusBean.setParams(sceneParamsBean);
                        mArrayList.add(statusBean);

                        break;

                }



            }
        };


    }

    @Override
    protected void onResume() {
        super.onResume();
        //banlap： 选好设备开关后，返回该页面时，不再调用 findSceneDetails(sceneId)
        if(mResultCode != 3 && mRequestCode !=1) {
            sceneId=getIntent().getStringExtra("id");
            if (!TextUtils.isEmpty(sceneId)){
                findSceneDetails(sceneId);
            }
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

    private void initView() {
        rv_eventlist=findViewById(R.id.rv_eventlist);
        time_text=findViewById(R.id.time_text);
        time_text.setOnClickListener(this);
        open_vip_headicon=findViewById(R.id.open_vip_headicon);
        name_text=findViewById(R.id.name_text);
        sid_text=findViewById(R.id.sid_text);
        delete_text=findViewById(R.id.delete_text);
        delete_text.setOnClickListener(this);
        sve_data=findViewById(R.id.sve_data);
        sve_data.setOnClickListener(this);
        add_device=findViewById(R.id.add_device);
        add_device.setOnClickListener(this);
        rv_eventlist.setLayoutManager(new GridLayoutManager(SceneDetailsActivity.this, 1));
        //WaittingDialog.initWaittingDialog(getActivity(), "正在加载中...");

        adapter = new CanRVAdapter<SceneDetailsBean>(rv_eventlist, R.layout.item_scene_facility_list) {
            @Override
            protected void setView(CanHolderHelper helper, int position, SceneDetailsBean bean) {
                helper.setText(R.id.name_text,bean.getDeviceNickName());
                helper.setText(R.id.devid_text,bean.getProductKey());
                if (bean.isIgnoreRepeatedPropertyValue()){
                    helper.setText(R.id.swtch_dvei_text,"开启");
                }else {
                    helper.setText(R.id.swtch_dvei_text,"关闭");
                }

                LinearLayout llSceneDetailList = helper.getView(R.id.ll_scene_detail_list);
                llSceneDetailList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(SceneDetailsActivity.this, "ProductKey: " + bean.getProductKey(), Toast.LENGTH_SHORT).show();
                        addSceneData();
                        Intent intent = new Intent(SceneDetailsActivity.this, FacilityDetailsActivity.class);
                        intent.putExtra("normalDetail", bean);
                        intent.putExtra("directWifi", false);
                        startActivity(intent);
                    }
                });
            }

            @Override
            protected void setItemListener(CanHolderHelper helper) {
            }
        };
        rv_eventlist.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.time_text:
                //String code = "link://router/cloudtime";
                //Bundle bundle = new Bundle();
                //bundle.putString("key","value"); // 传入插件参数，没有参数则不需要这一行
                //Router.getInstance().toUrlForResult(SceneDetailsActivity.this, code, 1, bundle);
                Intent intentTime=new Intent(SceneDetailsActivity.this, TimingListActivity.class);
                intentTime.putExtra("timeList", (Serializable) mArrayList2);
                startActivityForResult(intentTime,  2);
                break;
            case R.id.delete_text:
                dialogUtil.show();
                break;
            case R.id.sve_data:
                //保存场景数据
                updateScene();
                Toast.makeText(SceneDetailsActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.add_device:
                Intent intent=new Intent(SceneDetailsActivity.this, AddFacilityActivity.class);
                startActivityForResult(intent,1);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1&&resultCode==2){
            Log.e("redata",data.toString()+"---");

        }
        //选择设备 返回数据
        if(resultCode == 3) {
            if(requestCode ==1){
                mProductName = data.getStringExtra("faclityProductName").toString();
                mProductKey = data.getStringExtra("faclityProductKey").toString();
                mStatus = Integer.valueOf(data.getStringExtra("faclityStatus"));
                mIotId = data.getStringExtra("faclityIotId").toString();
                /*Toast.makeText(SceneDetailsActivity.this, "mProductName: "
                        + mProductName + "  mProductKey：" + mProductKey, Toast.LENGTH_LONG).show();*/
                Message message=new Message();
                message.what=ADDNEWDEVICE;
                handler.sendMessage(message);
            }
        }
        //选择定时 返回数据
        if(resultCode == 4) {
            if (requestCode == 2) {

            }
        }

        //banlap： 数据回调时，存入请求和结果值， 用于onResume时是否刷新列表
        mRequestCode = requestCode;
        mResultCode = resultCode;
    }

    //banlap： 保存场景
    private void updateScene() {
        Map<String, Object> params = new HashMap<>();
        //params.put("groupId","0");
        params.put("sceneId",sceneId);
        params.put("enable",jsonObject.getBoolean("enable"));
        params.put("name",jsonObject.getString("name"));
        params.put("icon",jsonObject.getString("icon"));

        JSONArray jsonArray=(JSONArray) JSONArray.toJSON(mArrayList);
        params.put("actions", jsonArray);
        params.put("sceneType","CA");

        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/scene/update") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
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
                Toast.makeText(SceneDetailsActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(IoTRequest request, IoTResponse response) {
                int code = response.getCode();
                Log.e("code22",code+"----");
                // 200 代表成功
                if(200 != code){
                    //失败示例，参见 "异常数据返回示例"
                    String mesage = response.getMessage();
                    String localizedMsg = response.getLocalizedMsg();
                    //TODO，根据mesage和localizedMsg，处理失败信息
                    return;
                }
                //以下消息无效
                Message message=new Message();
                message.what=UPDATESCENE;
                handler.sendMessage(message);
                Log.e("data--","----");
            }
        });


    }

    //banlap: str转换设置星期
    private String setStrWeekValue(String str){
        String strWeek ="";
        if(str.equals("*")){
            strWeek = " 每天";
            //Toast.makeText(AddSceneActivity.this, "值:" + "*" + "|", Toast.LENGTH_LONG).show();
        } else if(str.equals("1")){
            strWeek = " 周一";
        } else if(str.equals("2")){
            strWeek = " 周二";
        } else if(str.equals("3")){
            strWeek = " 周三";
        } else if(str.equals("4")){
            strWeek = " 周四";
        } else if(str.equals("5")){
            strWeek = " 周五";
        } else if(str.equals("6")){
            strWeek = " 周六";
        } else if(str.equals("7")){
            strWeek = " 周日";
        }
        return strWeek;
    }

    //banlap： 弹出对话框
    private void initDailog() {
        dialogUtil=new DialogUtil(SceneDetailsActivity.this,R.style.custom_dialog);
        View view= LayoutInflater.from(SceneDetailsActivity.this).inflate(R.layout.dailo_utils_layout,null);
        dialogUtil.setContentView(view);
        TextView messga_text= view.findViewById(R.id.messga_text);
        messga_text.setText("你确定要删除？");
        TextView ok_btn=view.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  if (!TextUtils.isEmpty(sceneId)){
                      deleteScene(sceneId);
                  }else {
                      Toast.makeText(SceneDetailsActivity.this,"删除失败，请重新操作！",Toast.LENGTH_SHORT).show();
                  }

                dialogUtil.hide();
            }
        });
        TextView cancel_bt= view.findViewById(R.id.cancel_bt);
        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUtil.hide();
            }
        });

    }

    //banlap： 删除场景
    private void deleteScene(String sceneId) {
        Map<String, Object> params = new HashMap<>();
        params.put("sceneId",sceneId);
        Log.e("id",sceneId);
        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/scene/delete") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
                .setApiVersion("1.0.2")  // 参照API文档，设置API接口的版本号，本示例为1.0.2
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
                Toast.makeText(SceneDetailsActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(IoTRequest request, IoTResponse response) {
                int code = response.getCode();
                Log.e("code22",code+"----");
                // 200 代表成功
                if(200 != code){
                    //失败示例，参见 "异常数据返回示例"
                    String mesage = response.getMessage();
                    String localizedMsg = response.getLocalizedMsg();
                    //TODO，根据mesage和localizedMsg，处理失败信息
                    return;
                }
                Message message=new Message();
                message.what=DELETATAGE;
                handler.sendMessage(message);
                Log.e("data--","----");

            }
        });


    }


    //banlap： 显示场景信息
    private void findSceneDetails(String id) {
        Map<String, Object> params = new HashMap<>();
        params.put("sceneId", id);
        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/scene/info/get") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
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
                message.what=SCENEDETAILSWHAT;
                message.obj=data;
                handler.sendMessage(message);
            }
        });


    }


    //banlap： 点击列表明细后 添加新场景
    private void addSceneData() {
        ArrayList<CAConditionsBean> arrayList1=new ArrayList<>();
        CAConditionsBean caConditionsBean = new CAConditionsBean();
        caConditionsBean.setUri("condition/device/property");

        ConditonBean conditonBean = new ConditonBean();
        conditonBean.setPropertyName("LightSwitch");
        conditonBean.setProductKey("a1BWrU8YpMa");
        conditonBean.setCompareValue(1);
        conditonBean.setCompareType("==");
        conditonBean.setDeviceName("AppTestLed");

        caConditionsBean.setParams(conditonBean);
        arrayList1.add(caConditionsBean);
        JSONArray jsonArray1 =(JSONArray) JSONArray.toJSON(arrayList1);

        ArrayList<ActionMsgSendBean> arrayList2=new ArrayList<>();
        ActionMsgSendBean actionMsgSendBean = new ActionMsgSendBean();
        actionMsgSendBean.setUri("action/mq/send");

        CustomDataBean paramsBean =  new CustomDataBean();
        MsgSendBean msgSendBean = new MsgSendBean();
        msgSendBean.setMessage("已打开开关");

        paramsBean.setCustomData(msgSendBean);
        actionMsgSendBean.setParams(paramsBean);
        arrayList2.add(actionMsgSendBean);
        JSONArray jsonArray2 =(JSONArray) JSONArray.toJSON(arrayList2);

        Map<String, Object> maps = new HashMap<>();
        maps.put("enable", true);
        maps.put("name", "控制设备开关");
        maps.put("icon", "http://media-cdn.tripadvisor.com/media/photo-s/01/3e/05/40/the-sandbar-that-links.jpg");
        maps.put("iconColor","#FFFFFF");
        maps.put("catalogId", 0);
        maps.put("groupId", 0);
        maps.put("sceneType", "CA");
        maps.put("caConditions", jsonArray1);
        maps.put("actions", jsonArray2);

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
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                // TODO根据e，处理异常
                Log.e("onFailure--",e.getMessage());
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
}