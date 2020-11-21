package com.aliyun.iot.ilop.demo.kaiguan.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.nfc.Tag;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.alibaba.fastjson.JSON;
import com.aliyun.alink.business.devicecenter.api.add.AddDeviceBiz;
import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo;
import com.aliyun.alink.business.devicecenter.api.add.IAddDeviceListener;
import com.aliyun.alink.business.devicecenter.api.add.ProvisionStatus;
import com.aliyun.alink.business.devicecenter.base.DCErrorCode;
import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.kaiguan.activity.AddSceneActivity;
import com.aliyun.iot.ilop.demo.kaiguan.activity.SceneDetailsActivity;
import com.aliyun.iot.ilop.demo.kaiguan.activity.SceneListActivity;
import com.aliyun.iot.ilop.demo.kaiguan.bean.SceneParamsBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.ScenesBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.ScenesListBean;
import com.aliyun.iot.ilop.demo.kaiguan.bean.SwitchDataBean;
import com.aliyun.iot.ilop.demo.page.bean.DeviceInfoBean;
import com.aliyun.iot.ilop.demo.page.ilopmain.HomeTabFragment;
import com.aliyun.iot.ilop.demo.page.ilopmain.LampsActivity;
import com.aliyun.iot.ilop.demo.page.ilopmain.MainActivity;
import com.aliyun.iot.ilop.demo.page.share.ShareActivity;
import com.aliyun.iot.ilop.demo.utils.LogUtil;
import com.aliyun.iot.ilop.demo.view.DevicePanelView;
import com.aliyun.iot.link.ui.component.LinkToast;
import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanRVAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.zxing.qrcode.QRCodeReader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private ImageView add_event,saom_iamge;
    private RecyclerView rv_eventlist;
    private SmartRefreshLayout srl_homelist;
    private CanRVAdapter adapter;
    private TextView talto_text, switchAll_text, switchOnAll_text, switchOffAll_text;
    private ImageView more_text;
    private String url = "/living/virtual/qrcode/bind";
    private String TAG="HomeFragment";
    private String displayingMessage = null;
    private Handler mHandler = new Handler();
    private int page=1;
    private Handler handler;
    private final int FINDSCENE=111;
    private final int FINDSCENEAUTO=112;
    private final int RUNSCENE=122;
    private final int FINDDEVICE=222;

    //com.test.wifirelay

    private int mSwitchCount=0;
    private int mOnlineCount=0;   //统计在线设备数量
    private int mOfflineCount=0;   //统计离线设备数量

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.clear();
        page=1;
        findScene();
        findSceneAuto();
        listByAccount();
    }



    private void initView(View view) {
        talto_text=view.findViewById(R.id.talto_text);
        add_event=view.findViewById(R.id.add_event);
        more_text=view.findViewById(R.id.more_text);
        saom_iamge=view.findViewById(R.id.saom_iamge);
        switchAll_text= view.findViewById(R.id.tv_switchAll);
        switchOnAll_text=view.findViewById(R.id.tv_switch_onlineAll);
        switchOffAll_text=view.findViewById(R.id.tv_switch_offlineAll);

        saom_iamge.setOnClickListener(this);
        more_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), SceneListActivity.class);
                getActivity().startActivity(intent);
            }
        });
        add_event.setVisibility(View.VISIBLE);
        add_event.setOnClickListener(this);
        rv_eventlist=view.findViewById(R.id.rv_eventlist);
        srl_homelist=view.findViewById(R.id.srl_eventlist);
        rv_eventlist.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        talto_text.setText("场景");
        srl_homelist.setRefreshHeader(new ClassicsHeader(getActivity()));
        srl_homelist.setRefreshFooter(new ClassicsFooter(getActivity()));
        initHandler();


        srl_homelist.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page++;
                findScene();
                findSceneAuto();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                findScene();
                findSceneAuto();
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
                /*if (bean.isEnable()){
                    aSwitch.setChecked(true);
                }else {
                    aSwitch.setChecked(false);
                }*/
                aSwitch.setChecked(false);
                //列表点击 场景详情
                LinearLayout switch_layout=helper.getView(R.id.switch_layout);
                switch_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getActivity(), SceneDetailsActivity.class);
                        intent.putExtra("id",bean.getId());
                        startActivity(intent);
                    }
                });

                Switch sceneSwitch = helper.getView(R.id.kuaig_switch);
                sceneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                        if(isCheck) {
                            //Toast.makeText(getActivity(), "1 : and id: " + bean.getId(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), "场景执行成功", Toast.LENGTH_SHORT).show();
                            runScene(bean.getId());
                        } else {
                            //Toast.makeText(getActivity(), "0 : and id:" + bean.getId(), Toast.LENGTH_SHORT).show();
                        }
                    }

                });

            }

            @Override
            protected void setItemListener(CanHolderHelper helper) {
            }
        };

        rv_eventlist.setAdapter(adapter);
        listByAccount();

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
                        }
                        //Toast.makeText(getActivity(), "查找场景成功", Toast.LENGTH_SHORT).show();
                        if (page==1){
                            adapter.setList(arrayList);
                            page++;
                        }else {
                            adapter.addMoreList(arrayList);
                        }
                        adapter.notifyDataSetChanged();
                        srl_homelist.finishRefresh();
                        srl_homelist.finishLoadMore();
                        break;

                    case FINDSCENEAUTO:
                        Object data3=msg.obj;
                        com.alibaba.fastjson.JSONObject jsonObject3 = JSON.parseObject(data3.toString());

                        com.alibaba.fastjson.JSONArray sceneArray3=jsonObject3.getJSONArray("scenes");
                        ArrayList<ScenesListBean> arrayList3=new ArrayList<>();
                        if (sceneArray3 != null) {
                            for (int i = 0; i < sceneArray3.size(); i++) {
                                com.alibaba.fastjson.JSONObject devJsonObject = sceneArray3.getJSONObject(i);
                                // TODO 从 devJsonObject 解析出各个字段
                                ScenesListBean scenesListBean=new ScenesListBean();
                                scenesListBean.setName(devJsonObject.getString("name"));
                                scenesListBean.setId(devJsonObject.getString("id"));
                                scenesListBean.setEnable(devJsonObject.getBoolean("enable"));
                                scenesListBean.setStatus(devJsonObject.getInteger("status"));
                                arrayList3.add(scenesListBean);

                            }
                        } else {

                        }

                        if (page==1){
                            adapter.setList(arrayList3);
                            page++;
                        }else {
                            adapter.addMoreList(arrayList3);
                        }
                        adapter.notifyDataSetChanged();
                        srl_homelist.finishRefresh();
                        srl_homelist.finishLoadMore();
                        break;


                    case FINDDEVICE:
                        mSwitchCount=0;
                        mOnlineCount=0;   //统计在线设备数量
                        mOfflineCount=0;
                        Object data2=msg.obj;
                        com.alibaba.fastjson.JSONObject jsonObject2 = JSON.parseObject(data2.toString());
                        com.alibaba.fastjson.JSONArray deviceArray2 = jsonObject2.getJSONArray("data");
                        if (deviceArray2 != null) {
                            for (int i = 0; i < deviceArray2.size(); i++) {
                                com.alibaba.fastjson.JSONObject devJsonObject = deviceArray2.getJSONObject(i);
                                if(devJsonObject.getInteger("status") ==1){
                                    mOnlineCount++;
                                } else if(devJsonObject.getInteger("status") ==3){
                                    mOfflineCount++;
                                }
                            }
                            mSwitchCount = deviceArray2.size();
                            //统计设备、在线、离线 数量
                            switchAll_text.setText(""+mSwitchCount);
                            switchOnAll_text.setText(""+mOnlineCount);
                            switchOffAll_text.setText(""+mOfflineCount);
                        }
                        break;

                    case RUNSCENE:

                        break;
                }
            }
        };


    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_event:
                Intent intent=new Intent(getActivity(), AddSceneActivity.class);
                getActivity().startActivity(intent);

                //String code1 = "link://router/scene";
                //Bundle bundle1 = new Bundle();
                //Router.getInstance().toUrlForResult(getActivity(), code1, 8, bundle1);
                //creataScene();

                break;
            case R.id.saom_iamge:

                String code = "page/scan";
                Bundle bundle = new Bundle();
                Router.getInstance().toUrlForResult(getActivity(), code,1, bundle);

                /*addVirtualDevice("a1BWrU8YpMa");
                String code = "link://router/scene";
                Bundle bundle = new Bundle();
                //bundle.putString("key","value"); // 传入插件参数，没有参数则不需要这一行
                Router.getInstance().toUrlForResult(getActivity(), code, 1, bundle);*/
                break;
        }

    }

    // 查询账号绑定的数据
    private void listByAccount() {
        Map<String, Object> maps = new HashMap<>();
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/uc/listBindingByAccount")
                .setScheme(Scheme.HTTPS)
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();

        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                ALog.d(TAG, "onFailure");
                if ("request auth error.".equals(e.getMessage())) {
                    onLogout();
                }
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                final int code = response.getCode();
                final String localizeMsg = response.getLocalizedMsg();
                if (code != 200) {
                    if (!Objects.equals(displayingMessage, localizeMsg)) {
                        displayingMessage = localizeMsg;
                        mHandler.post(() -> LinkToast.makeText(Objects.requireNonNull(getActivity()), localizeMsg).show());
                    }

                    if (code == 401) onLogout();
                    return;
                }

                Object data = response.getData();
                if (data == null) {
                    return;
                }
                if (!(data instanceof JSONObject)) {
                    return;
                }

                Message message=new Message();
                message.what=FINDDEVICE;
                message.obj=data;
                handler.sendMessage(message);
                try {
                    JSONObject jsonObject = (JSONObject) data;
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    Log.e(TAG,jsonArray.toString()+"--");

                    List<DeviceInfoBean> deviceInfoBeanList = JSON.parseArray(jsonArray.toString(), DeviceInfoBean.class);
                    /*mHandler.post(() -> initDevicePanel(deviceInfoBeanList));
                    if (deviceInfoBeanList == null) {
                        return;
                    }
                    //注册虚拟设备
                    Set<String> set = new HashSet<String>();
                    ArrayList<String> deviceStrList = new ArrayList<>();
                    for (DeviceInfoBean deviceInfoBean : deviceInfoBeanList) {
                        set.add(deviceInfoBean.getProductKey());
                        deviceStrList.add(deviceInfoBean.getProductKey() + deviceInfoBean.getDeviceName());
                    }
                    mBundle.putStringArrayList("deviceList", deviceStrList);
                    //noinspection MismatchedReadAndWriteOfArray
                    mRegisterCount = 0;
                    mVirtualCount = 0;
                    //noinspection RedundantOperationOnEmptyContainer
                    for (String pk : pks) {
                        if (set.add(pk)) {
                            mRegisterCount++;
                            //registerVirtualDevice(pk);
                            addVirtualDevice(pk);
                        }
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    //查询场景列表 - 自动
    private void findScene() {
        Map<String, Object> params = new HashMap<>();
        params.put("groupId","1"); //设置 自动化或非自动化，自动化值为1， 非自动化为0
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

    //查询场景列表 - 非自动
    private void findSceneAuto() {
        Map<String, Object> params = new HashMap<>();
        params.put("groupId","0"); //设置 自动化或非自动化，自动化值为1， 非自动化为0
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
                Log.e("onFailure1",e.getMessage()+"----");
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                int code = ioTResponse.getCode();
                Log.e("code1",code+"----");
                // 200 代表成功
                if(200 != code){
                    //失败示例，参见 "异常数据返回示例"
                    String message = ioTResponse.getMessage();
                    String localizedMsg = ioTResponse.getLocalizedMsg();
                    //TODO，根据mesage和localizedMsg，处理失败信息

                    Looper.prepare();
                    Toast.makeText(getActivity(), "code: " + code + " message: " + message + " localizedMsg: " + localizedMsg , Toast.LENGTH_LONG).show();
                    Looper.loop();
                    return;
                }
                Log.e("data--","----");
                Object data = ioTResponse.getData();
                //TODO，可以将data转成一个本地的对象或者直接使用JSONObject进行数据解析
                Log.e("data1--",data.toString()+"----");
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

    //创建场景
    private void creataScene() {
         // 构建请求
        Map<String, Object> params = new HashMap<>();
        params.put("iotId", "8UFyajigXmhtXKXH56GW000000");
        params.put("enable", true);
        // iotId获取当前账号绑定设备列表的时候可以拿到，对应唯一设备
        params.put("name", "shshhhdhdhd");
        params.put("icon","http://media-cdn.tripadvisor.com/media/photo-s/01/3e/05/40/the-sandbar-that-links.jpg");
        ArrayList<ScenesBean> arrayList=new ArrayList<>();
        ScenesBean statusBean=new ScenesBean();
        statusBean.setUri("action/device/setProperty");
        SceneParamsBean paramsBean=new SceneParamsBean();
        paramsBean.setIotId("PzzQemA96qIptPZ8z9tx000000");
        paramsBean.setPropertyName("VD_SM8SY4wZyJ");
        paramsBean.setPropertyValue(1);
        statusBean.setParams(paramsBean);
        arrayList.add(statusBean);
        com.alibaba.fastjson.JSONArray jsonArray=(com.alibaba.fastjson.JSONArray) com.alibaba.fastjson.JSONArray.toJSON(arrayList);
        Log.e("json",jsonArray.toString());
        params.put("actions",jsonArray);
                IoTRequest request = new IoTRequestBuilder()
                        .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                        .setPath("/scene/timing/create") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
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
                Log.e("onFailure",e.getMessage()+"----");
            }

            @Override
            public void onResponse(IoTRequest request, IoTResponse response) {
                int code = response.getCode();
                Log.e("code",code+"----");
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
                if (data == null) {
                    return;
                }
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(data.toString());
                //获取业务层code
                String codeBiz = jsonObject.getString("code");
                //获取业务返回的数据
                com.alibaba.fastjson.JSONObject dataBizJsonObject = jsonObject.getJSONObject("data");
                //获取data，data数据是一个JSONArray，即设备列表
                com.alibaba.fastjson.JSONArray devListJsonArray = dataBizJsonObject.getJSONArray("data");
                //后续具体设备信息，则是对devListJsonArray进行一个遍历解析了
                if (devListJsonArray != null) {
                    for (int i = 0; i < devListJsonArray.size(); i++) {
                        com.alibaba.fastjson.JSONObject devJsonObject = devListJsonArray.getJSONObject(i);
                        // TODO 从 devJsonObject 解析出各个字段
                    }
                }
            }
        });




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("jsjsdhdh",data.toString()+"-----");
        /*if (requestCode == 1) {
            Log.d(TAG, "onActivityResult");
            if (data.getStringExtra("productKey") != null) {
                Bundle bundle = new Bundle();
                bundle.putString("productKey", data.getStringExtra("productKey"));
                bundle.putString("deviceName", data.getStringExtra("deviceName"));
                bundle.putString("token", data.getStringExtra("token"));
                Intent intent = new Intent(getActivity(), HomeTabFragment.class);
                intent.putExtras(bundle);
                this.startActivity(intent);
            }
        }*/
        if(requestCode ==1) {
            Toast.makeText(getContext(),"w",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(),"e",Toast.LENGTH_LONG).show();
        }

    }


    /**
     * 添加虚拟设备
     */
    private void addVirtualDevice(String pk) {


        String identity = getIdentity();
        if (TextUtils.isEmpty(identity)) return;
        Map<String, Object> params = new HashMap<>();
        params.put("productKey", pk);
        params.put("identityId", identity);
        IoTRequest request = buildRequest(params, url);
        IoTAPIClient mIoTAPIClient = new IoTAPIClientFactory().getClient();
        mIoTAPIClient.send(request, new IoTCallback() {
                @Override
                public void onFailure(IoTRequest ioTRequest, final Exception e) {
                    ALog.e(TAG, "onFail " + this, e);

                }

            @Override
            public void onResponse(IoTRequest ioTRequest, final IoTResponse ioTResponse) {
                if (200 != ioTResponse.getCode()) {
                    ALog.e(TAG, "code：" + ioTResponse.getCode() + "message：" + ioTResponse.getMessage() + "LocalizedMsg：" + ioTResponse.getLocalizedMsg());
                    final String localizeMsg = ioTResponse.getLocalizedMsg();
                    if (!Objects.equals(displayingMessage, localizeMsg)) {
                        displayingMessage = localizeMsg;
                        mHandler.post(() -> LinkToast.makeText(Objects.requireNonNull(getActivity()), localizeMsg).show());
                    }
                } else {
                    ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            final int code = ioTResponse.getCode();
                            /*mVirtualCount++;
                            if (mRegisterCount == mVirtualCount) {

                            }*/
                            Log.e(TAG,"添加成功"+ioTResponse.getData());
                            /*Gson gson=new Gson();
                            DeviceInfoBean deviceInfoBean= gson.fromJson((ioTResponse.getData()).toString(),DeviceInfoBean.class);

                            DeviceInfo deviceInfo=new DeviceInfo();
                            deviceInfo.productKey=deviceInfoBean.getProductKey();
                            deviceInfo.deviceName=deviceInfoBean.getDeviceName();
                            deviceInfo.id=deviceInfoBean.getIotId();
                            deviceInfo.linkType = "ForceAliLinkTypeBroadcast";
                            Log.e(TAG,deviceInfo.toString());
                            startPeiWanDevice(deviceInfo);
                            listByAccount();*/

                        }
                    });

                }
            }
        });

    }

    private void startPeiWanDevice(DeviceInfo deviceInfo) {
        //设置待添加设备的基本信息
        AddDeviceBiz.getInstance().setDevice(deviceInfo);

        /**
        * 第二步：开始配网
        * 前置步骤，设置待配信息并开始配网
        */
        AddDeviceBiz.getInstance().startAddDevice(getActivity(), new IAddDeviceListener(){
            @Override
            public void onPreCheck(boolean b, DCErrorCode dcErrorCode) {
                // 参数检测回调
            }

            @Override
            public void onProvisionPrepare(int prepareType) {
                /**
                 * 第三步：配网准备阶段，传入Wi-Fi信息
                 * TODO 修改使用手机当前连接的Wi-Fi的SSID和password
                 */
                if (prepareType == 1) {
                    Log.e("Wi-Fi","-------");
                    AddDeviceBiz.getInstance().toggleProvision("A0FRU9V6R", "1234567890", 60);
                }
            }

            @Override
            public void onProvisioning() {
                // 配网中
                Log.e(TAG,"配网中"+"---");
            }

            @Override
            public void onProvisionStatus(ProvisionStatus provisionStatus) {


            }

            @Override
            public void onProvisionedResult(boolean isSuccess, DeviceInfo deviceInfo, DCErrorCode errorCode) {
                /**
                 * 第四步：监听配网结果
                 */
                Log.e(TAG,isSuccess+"---");
                // 如果配网结果包含token，请使用配网成功带的token做绑定。
            }
        });


    }

    /**
     * 获取Identity
     *
     * @return
     */
    private String getIdentity() {
        IoTCredentialManageImpl ioTCredentialManage = IoTCredentialManageImpl.getInstance(AApplication.getInstance());
        if (null == ioTCredentialManage) {
            return null;
        }
        return ioTCredentialManage.getIoTIdentity();
    }

    public IoTRequest buildRequest(Map params, String url) {
        IoTRequestBuilder builder = new IoTRequestBuilder();
        builder.setApiVersion("1.0.0");
        builder.setScheme(Scheme.HTTPS);
        builder.setPath(url);
        builder.setParams(params);
        builder.setAuthType("iotAuth");
        IoTRequest request = builder.build();
        return request;
    }


    private void onLogout() {
        /*Intent intent = new Intent(getContext(), CountryListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (getActivity() != null) {
            getActivity().getApplicationContext().startActivity(intent);
            getActivity().finish();
        }*/

        //跳转到登录界面
        LoginBusiness.login(new ILoginCallback() {
            @Override
            public void onLoginSuccess() {
                MainActivity.start(HomeFragment.this.getActivity());
            }

            @Override
            public void onLoginFailed(int i, String s) {
                // LinkToast.makeText(getApplicationContext(), s).show();
            }
        });
    }


}