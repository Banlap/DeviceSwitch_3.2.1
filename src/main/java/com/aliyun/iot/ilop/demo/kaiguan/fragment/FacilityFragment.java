package com.aliyun.iot.ilop.demo.kaiguan.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
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
import com.aliyun.iot.ilop.demo.kaiguan.activity.FacilityDetailsActivity;
import com.aliyun.iot.ilop.demo.kaiguan.bean.FacilityListBean;
import com.aliyun.iot.ilop.demo.kaiguan.utils.DialogUtil;
import com.aliyun.iot.ilop.demo.utils.DialogTools;
import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanRVAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class FacilityFragment extends Fragment implements View.OnClickListener {

    private ImageView saom_iamge;
    private RecyclerView rv_eventlist;
    private SmartRefreshLayout srl_homelist;
    private CanRVAdapter adapter;
    private TextView talto_text;
    private Switch aSwitch;
    private int page=1;
    private Handler handler;
    private final int FINDFACILITYWHAT=0006;
    private final int SUCCESSSET=888;
    private final int ADDDEVICE = 1000;
    private DialogUtil dialogUtil;
    private String iotid;

    private String mQrCodeInfo="";

    public static final int CAMERA_REQ_CODE = 111;
    public static final int DECODE = 1;
    private static final int REQUEST_CODE_SCAN_ONE = 0X01;

    SharedPreferences mSPDeviceList;      //存储扫码的信息
    private List<String> mDeviceList = new ArrayList<String>();  //数据列表
    private String mDeviceListSP = "";    //临时数据

    private int mIsSwitch = 0;  //判断设备开关

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_facility, container, false);
        initView(view);
        mSPDeviceList = getActivity().getSharedPreferences( "mDeviceListInfo", Context.MODE_PRIVATE );
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.clear();
        page=1;
        findFacilityData();

        mDeviceListSP = mSPDeviceList.getString("deviceListInfo","");
        if(!mDeviceListSP.equals("")) {
            Gson gson = new Gson();
            mDeviceList = gson.fromJson(mDeviceListSP, new TypeToken<List<String>>() {}.getType());

            for (int i = 0; i < mDeviceList.size(); i++) {
                page++;
                //String qr = "ProductKey:a1ELVgicjs4DeviceName:AppTestLedDeviceSecre:77157019ea6fdd6f26ce77927a811ff6HotWifiInfo:ssid:dingyingdyagepassword:Dingying*888";
                String qr = mDeviceList.get(i).toString();
                showData(qr);
            }
        }


    }

    private void initView(View view) {
        talto_text=view.findViewById(R.id.talto_text);
        saom_iamge=view.findViewById(R.id.saom_iamge);
        saom_iamge.setVisibility(View.VISIBLE);
        saom_iamge.setOnClickListener(this);
        rv_eventlist=view.findViewById(R.id.rv_eventlist);
        srl_homelist=view.findViewById(R.id.srl_eventlist);
        rv_eventlist.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        talto_text.setText("设备开关");
        srl_homelist.setRefreshHeader(new ClassicsHeader(getActivity()));
        srl_homelist.setRefreshFooter(new ClassicsFooter(getActivity()));
        initHandler();
        initDailog();
        srl_homelist.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page++;
                findFacilityData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                adapter.clear();
                page = 1;
                findFacilityData();

                mDeviceListSP = mSPDeviceList.getString("deviceListInfo","");
                if(!mDeviceListSP.equals("")) {
                    Gson gson = new Gson();
                    mDeviceList = gson.fromJson(mDeviceListSP, new TypeToken<List<String>>() {}.getType());

                    for (int i = 0; i < mDeviceList.size(); i++) {
                        page++;
                        //String qr = "ProductKey:a1ELVgicjs4DeviceName:AppTestLedDeviceSecre:77157019ea6fdd6f26ce77927a811ff6HotWifiInfo:ssid:dingyingdyagepassword:Dingying*888";
                        String qr = mDeviceList.get(i).toString();
                        showData(qr);
                    }
                }
            }
        });
        //WaittingDialog.initWaittingDialog(getActivity(), "正在加载中...");



        adapter = new CanRVAdapter<FacilityListBean>(rv_eventlist, R.layout.item_switch_list) {
            @Override
            protected void setView(CanHolderHelper helper, int position, FacilityListBean bean) {
                helper.setText(R.id.name_text,bean.getProductName());
                helper.setText(R.id.sid_text,bean.getProductKey());
                aSwitch=helper.getView(R.id.kuaig_switch);
                if (bean.getStatus()==1){
                    helper.setText(R.id.status_text,"在线");
                }else if (bean.getStatus()==0){
                    helper.setText(R.id.status_text,"未激活");
                }else if (bean.getStatus()==3){
                    helper.setText(R.id.status_text,"离线");
                }else if (bean.getStatus()==8){
                    helper.setText(R.id.status_text,"禁用");
                }


                //item switch监听
                aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            //Toast.makeText(getActivity(), "open: ", Toast.LENGTH_LONG).show();
                            thingSetProperties(bean.getIotId(), 1);
                            aSwitch.setChecked(true);
                        } else {
                            //Toast.makeText(getActivity(), "close: ", Toast.LENGTH_LONG).show();
                            thingSetProperties(bean.getIotId(), 0);
                            aSwitch.setChecked(false);
                        }
                    }
                });

                //列表监听
                LinearLayout switch_layout=helper.getView(R.id.switch_layout);
                switch_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean isQrCodeInfo = false;
                        TextView tvProductKey = view.findViewById(R.id.sid_text);
                        TextView tvProductName = view.findViewById(R.id.name_text);

                        //Toast.makeText(getActivity(), "size: " + mDeviceList.size(), Toast.LENGTH_LONG).show();
                        for (int i = 0; i < mDeviceList.size(); i++) {
                            String qrCodeInfo = mDeviceList.get(i).toString();
                            //banlap： 判断列表点击按钮监听
                            if (!qrCodeInfo.equals("")
                                    && tvProductKey.getText().equals((qrCodeInfo.substring(0, qrCodeInfo.indexOf("DeviceName:"))).substring(11))
                                    && tvProductName.getText().equals((qrCodeInfo.substring(qrCodeInfo.indexOf("DeviceName:"), qrCodeInfo.indexOf("DeviceSecre:"))).substring(11))) {
                                Intent intent = new Intent(getActivity(), FacilityDetailsActivity.class);
                                intent.putExtra("qrCodeInfo", qrCodeInfo);
                                intent.putExtra("directWifi", false);
                                startActivity(intent);
                                isQrCodeInfo = true;
                            }
                        }

                        if (!isQrCodeInfo) {
                            Intent intent = new Intent(getActivity(), FacilityDetailsActivity.class);
                            intent.putExtra("facilityDetail", bean);
                            intent.putExtra("directWifi", false);
                            startActivity(intent);
                        }

                       /* dialogUtil.show();
                        iotid= bean.getIotId();*/
                    }
                });

                //长按解绑设备
                switch_layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        iotid= bean.getIotId();
                        Toast.makeText(getActivity(), "iotId:" + iotid , Toast.LENGTH_SHORT).show();
                        dialogUtil.show();
                        return true;
                    }
                });

                helper.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.e("jdhdh","sssss");
                        dialogUtil.show();
                        iotid= bean.getIotId();
                        return false;
                    }
                });

            }


            @Override
            protected void setItemListener(CanHolderHelper helper) {
            }
        };
        rv_eventlist.setAdapter(adapter);

    }

    //banlap： 获取二维码数据并加入list中
    public void setData(String addQrCodeInfo){

        mDeviceListSP = mSPDeviceList.getString("deviceListInfo","");
        if(!mDeviceListSP.equals("")) {
            Gson gson = new Gson();
            mDeviceList = gson.fromJson(mDeviceListSP, new TypeToken<List<String>>() {
            }.getType());
        }

        String addQRdeviceKey = (addQrCodeInfo.substring(0, addQrCodeInfo.indexOf("DeviceName:"))).substring(11);
        String addQRdeviceName = (addQrCodeInfo.substring(addQrCodeInfo.indexOf("DeviceName:"), addQrCodeInfo.indexOf("DeviceSecre:"))).substring(11);
        String addQRdeviceSecre = (addQrCodeInfo.substring(addQrCodeInfo.indexOf("DeviceSecre:"), addQrCodeInfo.indexOf("HotWifiInfo:"))).substring(12);

        if(mDeviceList.size() >0) {
            for(int i = 0; i<mDeviceList.size(); i++){
                String mDLdeviceData = mDeviceList.get(i).toString();
                String mDLdeviceKey = (mDLdeviceData.substring(0, mDLdeviceData.indexOf("DeviceName:"))).substring(11);
                String mDLdeviceName = (mDLdeviceData.substring(mDLdeviceData.indexOf("DeviceName:"), mDLdeviceData.indexOf("DeviceSecre:"))).substring(11);
                String mDLdeviceSecre = (mDLdeviceData.substring(mDLdeviceData.indexOf("DeviceSecre:"), mDLdeviceData.indexOf("HotWifiInfo:"))).substring(12);

                if(!addQRdeviceKey.equals(mDLdeviceKey) && !addQRdeviceSecre.equals(mDLdeviceSecre)) {
                    mDeviceList.add(addQrCodeInfo);
                    showData(addQrCodeInfo);
                }
            }
        } else {
            mDeviceList.add(addQrCodeInfo);
            showData(addQrCodeInfo);
        }

        SharedPreferences.Editor editor = mSPDeviceList.edit() ;

        Gson gson = new Gson();
        String jsonStr=gson.toJson(mDeviceList);
        editor.putString("deviceListInfo", jsonStr);
        editor.apply();

        //mQrCodeInfo = addQrCodeInfo;

    }

    //banlap： 在当前fragment的列表中显示二维码数据
    public void showData(String qrCodeInfo) {
        Message message=new Message();
        message.what = ADDDEVICE;
        message.obj = qrCodeInfo;
        handler.sendMessage(message);
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
                        ArrayList<FacilityListBean> arrayList=new ArrayList<>();
                        if (sceneArray1 != null) {
                            for (int i = 0; i < sceneArray1.size(); i++) {
                                com.alibaba.fastjson.JSONObject devJsonObject = sceneArray1.getJSONObject(i);
                                // TODO 从 devJsonObject 解析出各个字段
                                FacilityListBean scenesListBean=new FacilityListBean();
                                scenesListBean.setProductName(devJsonObject.getString("productName"));
                                scenesListBean.setProductKey(devJsonObject.getString("productKey"));
                                scenesListBean.setEdgeGateway(devJsonObject.getBoolean("isEdgeGateway"));
                                scenesListBean.setStatus(devJsonObject.getInteger("status"));
                                scenesListBean.setDeviceName(devJsonObject.getString("deviceName"));
                                scenesListBean.setIotId(devJsonObject.getString("iotId"));
                                arrayList.add(scenesListBean);

                            }


                            //Toast.makeText(getActivity(), "sceneArray1: " + sceneArray1.getJSONObject(0), Toast.LENGTH_LONG).show();
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

                    //添加设备
                    case ADDDEVICE:

                        String deviceData = msg.obj.toString();
                        String deviceKey = deviceData.substring(0, deviceData.indexOf("DeviceName:"));
                        String deviceName = deviceData.substring(deviceData.indexOf("DeviceName:"), deviceData.indexOf("DeviceSecre:"));
                        ArrayList<FacilityListBean> addDeviceList=new ArrayList<>();
                        FacilityListBean addDeviceListBean=new FacilityListBean();
                        addDeviceListBean.setProductName(deviceName.substring(11));
                        addDeviceListBean.setProductKey(deviceKey.substring(11));
                        addDeviceListBean.setEdgeGateway(false);
                        addDeviceListBean.setStatus(3);
                        addDeviceList.add(addDeviceListBean);

                        adapter.addMoreList(addDeviceList);
                        adapter.notifyDataSetChanged();
                        srl_homelist.finishRefresh();
                        srl_homelist.finishLoadMore();
                        break;

                    //更新开关按钮状态
                    case SUCCESSSET:

                        break;
                }
            }
        };


    }
    private void initDailog() {
        dialogUtil=new DialogUtil(getActivity(),R.style.custom_dialog);
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dailo_utils_layout,null);
        dialogUtil.setContentView(view);
        TextView messga_text= view.findViewById(R.id.messga_text);
        messga_text.setText("你确定删除？");
        TextView ok_btn=view.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deleteDate();
                deleteDevice();
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

    private void deleteDate() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("deviceId",iotid);
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/uc/unbindPushChannel")
                .setScheme(Scheme.HTTPS)
                .setApiVersion("1.0.6")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();

        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                Log.e("jsjsj","解绑"+e.getMessage());
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                final int code = response.getCode();
                final String localizeMsg = response.getLocalizedMsg();
                Log.e("jsjsj"," 解绑成功"+code+localizeMsg);
                if(200 != code){
                    //失败示例，参见 "异常数据返回示例"
                    String message = response.getMessage();
                    String localizedMsg = response.getLocalizedMsg();
                    //TODO，根据mesage和localizedMsg，处理失败信息

                    return;
                }
                Looper.prepare();
                Toast.makeText(getActivity(), "code:" + code, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        });

    }
    //解绑设备
    private void deleteDevice() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("iotId",iotid);
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/uc/unbindAccountAndDev")
                .setScheme(Scheme.HTTPS)
                .setApiVersion("1.0.8")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();

        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                Log.e("jsjsj","解绑"+e.getMessage());
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                final int code = response.getCode();
                final String localizeMsg = response.getLocalizedMsg();
                Log.e("jsjsj"," 解绑成功"+code+localizeMsg);
                if(200 != code){
                    //失败示例，参见 "异常数据返回示例"
                    String message = response.getMessage();
                    String localizedMsg = response.getLocalizedMsg();
                    //TODO，根据mesage和localizedMsg，处理失败信息

                    return;
                }
                Looper.prepare();
                Toast.makeText(getActivity(), "code:" + code, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        });

    }


    // 列表显示账号绑定的数据
    private void findFacilityData() {
        Map<String, Object> params = new HashMap<>();
        params.put("pageNo", page);
        // iotId获取当前账号绑定设备列表的时候可以拿到，对应唯一设备
        params.put("pageSize", 10);
        IoTRequest request = new IoTRequestBuilder()
                .setScheme(Scheme.HTTPS) // 设置Scheme方式，取值范围：Scheme.HTTP或Scheme.HTTPS，默认为Scheme.HTTPS
                .setPath("/uc/listBindingByAccount") // 参照API文档，设置API接口描述中的Path，本示例为uc/listBindingByDev
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

    //实现阿里云控制设备
    private void thingSetProperties(String iotId, int value) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("iotId", iotId);

        Map<String, Object> mapItem = new HashMap<>();
        mapItem.put("LightSwitch", value);
        maps.put("items", mapItem);

        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/thing/properties/set")
                .setScheme(Scheme.HTTPS)
                .setApiVersion("1.0.5")
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
                    String message = response.getMessage();
                    String localizedMsg = response.getLocalizedMsg();

                    Looper.prepare();
                    //Toast.makeText(FacilityDetailsActivity.this, "code: " + code + " message: " + message + " localizedMsg: " + localizedMsg , Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), " localizedMsg: " + localizedMsg , Toast.LENGTH_LONG).show();
                    //mivSwitch.setImageResource(R.mipmap.button_daguan);
                    //mDeviceStatus.setText("离线");
                    Looper.loop();

                    return;
                }

                Object data = response.getData();
                if (data == null) {
                    return;
                }
                Message message=new Message();
                message.what=SUCCESSSET;
                message.obj=data;
                handler.sendMessage(message);
            }

        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.saom_iamge:
                //banlap： 是否wifi直连提示框
                View viewQR = getLayoutInflater().inflate(R.layout.popwindow_wifi, null);
                DialogTools mDialogTools = new DialogTools(getActivity(), 0, 0, viewQR, R.style.DialogTheme);
                mDialogTools.setCancelable(true);
                mDialogTools.show();
                //banlap： 是
                mDialogTools.findViewById(R.id.popw_tv_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialogTools.hide();
                        //banlap： 使用huawei 扫码功能
                        requestPermission(CAMERA_REQ_CODE, DECODE);

                    }
                });

                mDialogTools.findViewById(R.id.popw_tv_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialogTools.hide();
                        //banlap： 官方扫码绑定设备
                        Router.getInstance().toUrl(getContext(), "page/scan");

                    }
                });

                break;
        }
    }

    //banlap： 权限 摄像头
    private void requestPermission(int requestCode, int mode) {
        FacilityFragment.this.requestPermissions(
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                requestCode);
    }

    //banlap： 权限返回结果
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions == null || grantResults == null) {
            return;
        }
        if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //banlap： 打开摄像头
        if (requestCode == CAMERA_REQ_CODE) {
            ScanUtil.startScan(getActivity(), REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().create());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            Toast.makeText(getContext(),"??",Toast.LENGTH_LONG).show();
        }
    }





}