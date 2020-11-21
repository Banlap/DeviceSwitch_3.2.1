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
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo;
import com.aliyun.alink.business.devicecenter.api.discovery.DiscoveryType;
import com.aliyun.alink.business.devicecenter.api.discovery.IDeviceDiscoveryListener;
import com.aliyun.alink.business.devicecenter.api.discovery.LocalDeviceMgr;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.kaiguan.bean.FacilityListBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.SwitchDataBean;
import com.aliyun.iot.ilop.demo.page.ilopmain.MainActivity;
import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanRVAdapter;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFacilityActivity extends AppCompatActivity implements View.OnClickListener {



    private RecyclerView rv_eventlist;
    private SmartRefreshLayout srl_homelist;
    private CanRVAdapter adapter;
    private TextView talto_text;
    private Handler handler;
    private int page=1;
    private final int FINDFACILITYWHAT=0006;
    private ImageView saom_iamge;
    private ArrayList<FacilityListBean> arrayList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        transparencyBar(AddFacilityActivity.this);
        setContentView(R.layout.activity_add_facility);
        initView();
        initHandler();

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

    @Override
    protected void onResume() {
        super.onResume();
        findFacilityData();

       /* ArrayList<DeviceInfo> arrayList = new ArrayList<>();
        EnumSet<DiscoveryType> discoveryTypeEnumSet = EnumSet.allOf(DiscoveryType.class);
        LocalDeviceMgr.getInstance().startDiscovery(this, discoveryTypeEnumSet, null, new IDeviceDiscoveryListener() {

            @Override
            public void onDeviceFound(DiscoveryType discoveryType, List<DeviceInfo> list) {
                if (list != null && !list.isEmpty() ) {
                    // 建议将发现的设备在UI上做展示，让用户触发对某个设备进行配网
                    // 发现的待配设备列表缓存在内存中，用户触发配网的时候将待配信息设置到SDK配网接口
                    DeviceInfo deviceInfo = new DeviceInfo();
                    deviceInfo.token = list.get(0).token;
                    deviceInfo.productKey = list.get(0).productKey;
                    deviceInfo.deviceName = list.get(0).deviceName;
                    arrayList.add(deviceInfo);
                }
            }

        });
        if(arrayList.size()>0) {
            Toast.makeText(AddFacilityActivity.this, "proKey:" + arrayList.get(0).productKey +
                            " deviName:" + arrayList.get(0).deviceName + " token:" + arrayList.get(0).token
                    , Toast.LENGTH_LONG).show();
        }*/

    }

    private void initHandler() {

        handler=new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){

                    case FINDFACILITYWHAT:
                        Object data=msg.obj;
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(data.toString());
                        com.alibaba.fastjson.JSONArray sceneArray1=jsonObject.getJSONArray("data");
                        arrayList=new ArrayList<>();
                        if (sceneArray1 != null) {
                            for (int i = 0; i < sceneArray1.size(); i++) {
                                com.alibaba.fastjson.JSONObject devJsonObject = sceneArray1.getJSONObject(i);
                                // TODO 从 devJsonObject 解析出各个字段
                                FacilityListBean scenesListBean=new FacilityListBean();
                                scenesListBean.setIotId(devJsonObject.getString("iotId"));
                                scenesListBean.setProductName(devJsonObject.getString("productName"));
                                scenesListBean.setProductKey(devJsonObject.getString("productKey"));
                                scenesListBean.setEdgeGateway(devJsonObject.getBoolean("isEdgeGateway"));
                                scenesListBean.setStatus(devJsonObject.getInteger("status"));
                                arrayList.add(scenesListBean);

                            }
                            //Toast.makeText(AddFacilityActivity.this, "d:" + sceneArray1.get(0).toString(), Toast.LENGTH_LONG).show();
                        }
                        if (page==1){
                            adapter.setList(arrayList);
                        }else {
                            adapter.addMoreList(arrayList);
                        }
                        adapter.notifyDataSetChanged();
                        srl_homelist.finishRefresh();
                        srl_homelist.finishLoadMore();
                        break;
                }
            }
        };


    }

    private void initView() {
        talto_text=findViewById(R.id.talto_text);
        saom_iamge=findViewById(R.id.saom_iamge);
        saom_iamge.setVisibility(View.VISIBLE);
        saom_iamge.setOnClickListener(this);
        rv_eventlist=findViewById(R.id.rv_eventlist);
        srl_homelist=findViewById(R.id.srl_eventlist);
        rv_eventlist.setLayoutManager(new GridLayoutManager(AddFacilityActivity.this, 1));
        talto_text.setText("设备开关");
        srl_homelist.setRefreshHeader(new ClassicsHeader(AddFacilityActivity.this));
        srl_homelist.setRefreshFooter(new ClassicsFooter(AddFacilityActivity.this));

        srl_homelist.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page++;
                findFacilityData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                findFacilityData();
            }
        });
        //WaittingDialog.initWaittingDialog(getActivity(), "正在加载中...");

        adapter = new CanRVAdapter<FacilityListBean>(rv_eventlist, R.layout.item_sele_switch_list) {
            @Override
            protected void setView(CanHolderHelper helper, int position, FacilityListBean bean) {
                helper.setText(R.id.name_text,bean.getProductName());
                helper.setText(R.id.sid_text,bean.getProductKey());
                if (bean.getStatus()==1){
                    helper.setText(R.id.status_text,"在线");
                }else if (bean.getStatus()==0){
                    helper.setText(R.id.status_text,"未激活");
                }else if (bean.getStatus()==3){
                    helper.setText(R.id.status_text,"离线");
                }else if (bean.getStatus()==8){
                    helper.setText(R.id.status_text,"禁用");
                }
                Switch aSwitch=helper.getView(R.id.kuaig_switch);
                if (bean.isEdgeGateway()){
                    aSwitch.setChecked(true);
                }else {
                    aSwitch.setChecked(false);
                }
                CheckBox checkBox=helper.getView(R.id.select_devite);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            Intent intent=new Intent();
                            intent.putExtra("faclityIotId",bean.getIotId().toString());//返回值
                            intent.putExtra("faclityProductKey",bean.getProductKey().toString());
                            intent.putExtra("faclityProductName",bean.getProductName().toString());
                            intent.putExtra("faclityStatus",bean.getStatus().toString());
                            if(aSwitch.isChecked()) {
                                intent.putExtra("faclitySwitch", true);
                            }else {
                                intent.putExtra("faclitySwitch",false);
                            }
                            setResult(3,intent);//有返回值的使用这个，没有要返回的值用setResult(0);
                            Log.e("jsjsj",arrayList.get(position).toString());
                            finish();

                        }
                    }
                });



            }

            @Override
            protected void setItemListener(CanHolderHelper helper) {
            }
        };

        rv_eventlist.setAdapter(adapter);


    }


    private void findFacilityData() {
        Map<String, Object> params = new HashMap<>();
        params.put("pageNo", page);
        // iotId获取当前账号绑定设备列表的时候可以拿到，对应唯一设备
        params.put("pageSize", 10);
        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/uc/listBindingByAccount") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
                .setApiVersion("1.0.8")  // 参照API文档，设置API接口的版本号，本示例为1.0.2
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
                message.what=FINDFACILITYWHAT;
                message.obj=data;
                handler.sendMessage(message);
            }
        });


    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.saom_iamge:
                Router.getInstance().toUrl(AddFacilityActivity.this, "page/scan");
                break;
            
        }
    }


    //重写onKeyDown
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(0);
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }
}