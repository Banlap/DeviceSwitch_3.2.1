package com.aliyun.iot.ilop.demo.kaiguan.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.kaiguan.bean.ScenesListBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.SwitchDataBean;
import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanRVAdapter;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneListActivity extends AppCompatActivity implements View.OnClickListener {


    private RecyclerView rv_eventlist;
    private SmartRefreshLayout srl_homelist;
    private CanRVAdapter adapter;
    private ImageView add_event,saom_iamge;
    private TextView talto_text;
    private Integer page=1;
    private final int FINDSCENE=111;
    private final int FINDSCENEAUTO=112;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        transparencyBar(SceneListActivity.this);
        setContentView(R.layout.activity_scene_list);
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
        adapter.clear();
        page = 1;
        initData();
        initDataAuto();
    }

    private void initView() {
        talto_text=findViewById(R.id.talto_text);
        add_event=findViewById(R.id.add_event);
        add_event.setVisibility(View.VISIBLE);
        add_event.setOnClickListener(this);
        talto_text.setText("场景");
        saom_iamge=findViewById(R.id.saom_iamge);
        saom_iamge.setOnClickListener(this);
        rv_eventlist=findViewById(R.id.rv_eventlist);
        srl_homelist=findViewById(R.id.srl_eventlist);
        rv_eventlist.setLayoutManager(new GridLayoutManager(SceneListActivity.this, 1));

        srl_homelist.setRefreshHeader(new ClassicsHeader(SceneListActivity.this));
        srl_homelist.setRefreshFooter(new ClassicsFooter(SceneListActivity.this));

        srl_homelist.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page++;
                initData();
                initDataAuto();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                initData();
                initDataAuto();
            }
        });
        //WaittingDialog.initWaittingDialog(getActivity(), "正在加载中...");

        adapter = new CanRVAdapter<ScenesListBean>(rv_eventlist, R.layout.item_switch_list) {
            @Override
            protected void setView(CanHolderHelper helper, int position, ScenesListBean bean) {
                helper.setText(R.id.name_text,bean.getName());
                helper.setText(R.id.sid_text,bean.getId());
                if (bean.getStatus()==1){
                    helper.setText(R.id.status_text,"在线");
                }else {
                    helper.setText(R.id.status_text,"离线");
                }
                Switch aSwitch= helper.getView(R.id.kuaig_switch);
                aSwitch.setChecked(false);

                LinearLayout switch_layout=helper.getView(R.id.switch_layout);
                switch_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(SceneListActivity.this, SceneDetailsActivity.class);
                        intent.putExtra("id",bean.getId());
                        startActivity(intent);
                    }
                });

                Switch sceneSwitch = helper.getView(R.id.kuaig_switch);
                sceneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                        if(isCheck) {
                            Toast.makeText(SceneListActivity.this, "场景执行成功", Toast.LENGTH_SHORT).show();
                            runScene(bean.getId());
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

    private void initHandler() {

        handler=new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){

                    case FINDSCENE:
                        Object data=msg.obj;
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(data.toString());

                        //获取业务层scene1
                        //com.alibaba.fastjson.JSONObject scene1 = jsonObject.getJSONObject("scene1");
                        com.alibaba.fastjson.JSONArray sceneArray1=jsonObject.getJSONArray("scenes");
                        ArrayList<ScenesListBean> arrayList=new ArrayList<>();
                        if (sceneArray1 != null) {
                            for (int i = 0; i < sceneArray1.size(); i++) {
                                com.alibaba.fastjson.JSONObject devJsonObject = sceneArray1.getJSONObject(i);
                                // TODO 从 devJsonObject 解析出各个字段
                                ScenesListBean scenesListBean=new ScenesListBean();
                                scenesListBean.setName(devJsonObject.getString("name"));
                                scenesListBean.setId(devJsonObject.getString("id"));
                                scenesListBean.setEnable(devJsonObject.getBoolean("enable"));
                                scenesListBean.setStatus(devJsonObject.getInteger("status"));
                                arrayList.add(scenesListBean);

                            }
                            //Toast.makeText(SceneListActivity.this, "显示数据： " + jsonObject.toString(), Toast.LENGTH_LONG).show();
                        }
                        if (page==1){
                            adapter.setList(arrayList);
                            page++;
                        }else {
                            adapter.addMoreList(arrayList);
                        }
                        adapter.notifyDataSetChanged();
                        srl_homelist.finishRefresh();
                        srl_homelist.finishLoadMore();
                        Log.e("jsjdjjd","-----11----");
                        break;

                    case FINDSCENEAUTO:
                        Object data2=msg.obj;
                        com.alibaba.fastjson.JSONObject jsonObject2 = JSON.parseObject(data2.toString());


                        com.alibaba.fastjson.JSONArray sceneArray2=jsonObject2.getJSONArray("scenes");
                        ArrayList<ScenesListBean> arrayList2=new ArrayList<>();
                        if (sceneArray2 != null) {
                            for (int i = 0; i < sceneArray2.size(); i++) {
                                com.alibaba.fastjson.JSONObject devJsonObject = sceneArray2.getJSONObject(i);
                                // TODO 从 devJsonObject 解析出各个字段
                                ScenesListBean scenesListBean=new ScenesListBean();
                                scenesListBean.setName(devJsonObject.getString("name"));
                                scenesListBean.setId(devJsonObject.getString("id"));
                                scenesListBean.setEnable(devJsonObject.getBoolean("enable"));
                                scenesListBean.setStatus(devJsonObject.getInteger("status"));
                                arrayList2.add(scenesListBean);

                            }
                            //Toast.makeText(SceneListActivity.this, "显示数据： " + jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } else {

                        }
                        if (page==1){
                            adapter.setList(arrayList2);
                            page++;
                        }else {
                            adapter.addMoreList(arrayList2);
                        }
                        adapter.notifyDataSetChanged();
                        srl_homelist.finishRefresh();
                        srl_homelist.finishLoadMore();
                        Log.e("jsjdjjd","-----11----");
                        break;
                }
            }
        };


    }

    //查询场景列表 -自动
    private void initData() {
        Map<String, Object> params = new HashMap<>();
        params.put("groupId","1");  //设置 自动化或非自动化，自动化值为1， 非自动化为0
        params.put("pageNo", page);
        // iotId获取当前账号绑定设备列表的时候可以拿到，对应唯一设备
        params.put("pageSize", 10);
        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/scene/list/get") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
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
                message.what=FINDSCENE;
                message.obj=data;
                handler.sendMessage(message);
            }
        });



    }

    //查询场景列表 -非自动
    private void initDataAuto() {
        Map<String, Object> params = new HashMap<>();
        params.put("groupId","0");  //设置 自动化或非自动化，自动化值为1， 非自动化为0
        params.put("pageNo", page);
        // iotId获取当前账号绑定设备列表的时候可以拿到，对应唯一设备
        params.put("pageSize", 10);
        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/scene/list/get") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
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
                message.what=FINDSCENEAUTO;
                message.obj=data;
                handler.sendMessage(message);
            }
        });



    }

    //运行场景
    private void runScene(String sceneId) {
        Map<String, Object> params = new HashMap<>();
        params.put("sceneId", sceneId);
        // iotId获取当前账号绑定设备列表的时候可以拿到，对应唯一设备
        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/scene/fire") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
                .setApiVersion("1.0.4")  // 参照API文档，设置API接口的版本号，本示例为1.0.2
                .setAuthType("iotAuth") // 当云端接口需要用户身份鉴权时需要设置该参数，反之则不需要设置
                .setParams(params) // 参照API文档，设置API接口的参数，也可以使用.setParams(Map<Strign,Object> params)来设置
                .build();
        // 获取Client实例，并发送请求
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {

            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                // TODO根据e，处理异常
                Log.e("onFailure1", e.getMessage() + "----");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                int code = ioTResponse.getCode();
                Log.e("code1", code + "----");
                // 200 代表成功
                if (200 != code) {
                    //失败示例，参见 "异常数据返回示例"
                    String message = ioTResponse.getMessage();
                    String localizedMsg = ioTResponse.getLocalizedMsg();
                    //TODO，根据mesage和localizedMsg，处理失败信息

                    Looper.prepare();
                    Toast.makeText(SceneListActivity.this, "code: " + code + " message: " + message + " localizedMsg: " + localizedMsg, Toast.LENGTH_LONG).show();
                    Looper.loop();
                    return;
                }
                Log.e("data--", "----");
                Object data = ioTResponse.getData();
                //TODO，可以将data转成一个本地的对象或者直接使用JSONObject进行数据解析
                Log.e("data1--", data.toString() + "----");
                /**
                 * 解析data，data示例参见"正常数据返回示例"
                 * 以下解析示例采用fastjson针对"正常数据返回示例"，解析各个数据节点
                 */
                if (data == null) {
                    return;
                }


            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_event:
                Intent intent=new Intent(SceneListActivity.this, AddSceneActivity.class);
                startActivity(intent);

                //String code1 = "link://router/scene";
                //Bundle bundle1 = new Bundle();
                //Router.getInstance().toUrlForResult(SceneListActivity.this, code1, 8, bundle1);
                break;
            case R.id.saom_iamge:
                Router.getInstance().toUrl(SceneListActivity.this, "page/scan");
                break;
        }
    }
}