package com.aliyun.iot.ilop.demo.page.channel;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileConnectListener;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileDownstreamListener;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileRequestListener;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileSubscrbieListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectConfig;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectState;
import com.aliyun.iot.ilop.demo.page.channel.switchbutton.SwitchButton;
import com.aliyun.iot.aep.sdk.EnvConfigure;
import com.aliyun.iot.aep.sdk.framework.AActivity;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.demo.R;

public class ChannelActivity extends AActivity implements View.OnClickListener {

    private static final String TAG = "ChannelActivity";
    private String allTopicWildcard = "#";

    private View topbarBackBtn;//返回按钮
    private TextView pageTitle;
    private TextView writeDefaultData;//写默认数据按钮
    private EditText subscribeET;//单条订阅Topic的编辑框
    private TextView subscribeTV;//单条订阅按钮
    private SwitchButton subscribeAllTV;//订阅全部按钮
    private SwitchButton bind2user;//绑定账号按钮
    private LinearLayout bind2userRoot;//绑定账号布局
    private EditText publishTopicET;//要发布的Toppic编辑框
    private ImageView publishTopicETDelete;//发布Topic框后的删除
    private EditText publishmsgET;//要发布的Topic的数据
    private TextView publishTV;//发布按钮
    private EditText msgET;//订阅回显信息
    private TextView clearPushMsgTv;//删除push消息按钮
    private ScrollView scrollView;

    private LinearLayout connectStatusRoot;//链接状态根部局
    private ImageView connectStatusIcon;//链接状态图标
    private TextView connectStatusDes;//链接状态描述

    private LinearLayout subscribeSingleTopicRoot;//订阅单个Topic的条目布局
    private int mCurrentSubSinTopicBtnSta = 0;//当前订阅单个Topic条目的按钮状态 -1：不可点 0：未定阅 1：已订阅

    private MobileConnectState mMobileConnectState;//当前建联状态

    private IMobileDownstreamListener listener = new IMobileDownstreamListener() {

        @Override
        public void onCommand(String s, String s1) {
            msgET.setText("topic:" + s + "\n" + "pushmsg:" + s1);
        }

        @Override
        public boolean shouldHandle(String s) {
            return true;
        }

    };
    private IMobileConnectListener connectListener = new IMobileConnectListener() {

        @Override
        public void onConnectStateChange(MobileConnectState mobileConnectState) {
            mMobileConnectState = mobileConnectState;
            refreshConnectStatusUI(mobileConnectState);
            if (MobileConnectState.CONNECTED == mobileConnectState) {
                //已连接

            } else if (MobileConnectState.DISCONNECTED == mobileConnectState) {
                //已断开

            } else if (MobileConnectState.CONNECTING == mobileConnectState) {
                //连接中

            } else if (MobileConnectState.CONNECTFAIL == mobileConnectState) {
                //连接失败

            }
            ALog.i(TAG, "onConnectStateChange " + mobileConnectState);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.channel_activity);
        super.onCreate(savedInstanceState);

        scrollView = (ScrollView) this.findViewById(R.id.channel_scroolview);
        topbarBackBtn =  this.findViewById(R.id.topbar_back_imageview);
        pageTitle =  this.findViewById(R.id.topbar_title_textview);
        writeDefaultData = (TextView) this.findViewById(R.id.topbar_menu_textview);
        subscribeSingleTopicRoot = (LinearLayout) this.findViewById(R.id.channel_subscribe_singletopic_linearlayout);
        subscribeET = (EditText) this.findViewById(R.id.channel_subscribe_topic_et);
        subscribeTV = (TextView) this.findViewById(R.id.channel_subscribe_btn);
        subscribeAllTV = (SwitchButton) this.findViewById(R.id.channel_subscribe_all_btn);
        bind2userRoot = (LinearLayout) this.findViewById(R.id.channel_bind2user_root_linearlayout);
        bind2user = (SwitchButton) this.findViewById(R.id.channel_subscrbie_bind2user);
        publishTopicET = (EditText) this.findViewById(R.id.channel_publish_topic_et);
        publishTopicETDelete = (ImageView) this.findViewById(R.id.channel_publish_topic_et_delete);
        publishmsgET = (EditText) this.findViewById(R.id.channel_publish_msg_et);
        publishTV = (TextView) this.findViewById(R.id.channel_publish_tv);
        msgET = (EditText) this.findViewById(R.id.channel_msg_et);
        clearPushMsgTv = (TextView) this.findViewById(R.id.channel_deletepushmsg_textview);

        connectStatusRoot = (LinearLayout) this.findViewById(R.id.channel_connect_status_linearlayout);
        connectStatusIcon = (ImageView) this.findViewById(R.id.channel_connect_status_icon);
        connectStatusDes = (TextView) this.findViewById(R.id.channel_connect_status_des);
        //appConnect();
        MobileChannel.getInstance().registerDownstreamListener(true, listener);
        MobileChannel.getInstance().registerConnectListener(true, connectListener);
        initEditTextAbout();//初始化页面内的EditText和ScrollView的冲突
        initView();//初始化视图
        publishTopicET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    publishTopicETDelete.setVisibility(View.VISIBLE);
                } else {
                    publishTopicETDelete.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //订阅-取消全部
        subscribeAllTV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //--如果链接没有建立，先执行建联,建联成功默认为订阅全部，不需再订阅-- 11.24 18:02
                    if (mMobileConnectState != MobileConnectState.CONNECTED) {
                        appConnect();
                        return;
                    }
                    subscrbie(allTopicWildcard, new IMobileSubscrbieListener() {
                        @Override
                        public void onSuccess(String s) {
                            refreshSubscribeSingleTopicTv(-1);//更新单个订阅通道为不可点
                            toast(getResources().getString(R.string.channel_subscribe_alltopic_sucess_msg));
                            //                            showTheMsgPushDown(getResources().getString(R.string.channel_subscribe_alltopic_sucess_msg));
                        }

                        @Override
                        public void onFailed(String s, AError aError) {
                            subscribeAllTV.setChecked(false);
                            toast(getResources().getString(R.string.channel_subscribe_alltopic_fail_msg));
                            //                            showTheMsgPushDown(getResources().getString(R.string.channel_subscribe_alltopic_fail_msg));
                        }

                        @Override
                        public boolean needUISafety() {
                            return true;
                        }
                    });
                } else {
                    unSubscrbie(allTopicWildcard, new IMobileSubscrbieListener() {
                        @Override
                        public void onSuccess(String s) {
                            toast(getResources().getString(R.string.channel_unsubscribe_alltopic_sucess_msg));
                            refreshSubscribeSingleTopicTv(0);//更新单个订阅通道为未定阅
                            //                            showTheMsgPushDown(getResources().getString(R.string.channel_unsubscribe_alltopic_sucess_msg));
                        }

                        @Override
                        public void onFailed(String s, AError aError) {
                            subscribeAllTV.setChecked(true);
                            toast(getResources().getString(R.string.channel_unsubscribe_alltopic_fail_msg));
                            //                            showTheMsgPushDown(getResources().getString(R.string.channel_unsubscribe_alltopic_fail_msg));
                        }

                        @Override
                        public boolean needUISafety() {
                            return true;
                        }
                    });
                }
            }
        });

        //绑定-解绑用户
        bind2user.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!LoginBusiness.isLogin()) {
                    bind2user.setChecked(false);
                    toast(getString(R.string.channel_unlogin_msg));
                    return;
                }
                if (isChecked) {
                    //appBind(LoginBusiness.getIoTToken());
                } else {
                    //appUnBind(LoginBusiness.getIoTToken());
                }
            }
        });

        writeDefaultData.setOnClickListener(this);
        topbarBackBtn.setOnClickListener(this);
        subscribeTV.setOnClickListener(this);
        publishTV.setOnClickListener(this);
        publishTopicETDelete.setOnClickListener(this);
        clearPushMsgTv.setOnClickListener(this);
        ALog.i(TAG, "MobileChannel clientID = " + MobileChannel.getInstance().getClientId());

    }

    private void initView() {
        //msgET.setEnabled(false); //需要复制日志信息
        writeDefaultData.setVisibility(View.VISIBLE);
        writeDefaultData.setText(getResources().getString(R.string.channel_topbar_righttv_text));
        pageTitle.setText(getResources().getString(R.string.channel_title));
        publishTopicETDelete.setVisibility(TextUtils.isEmpty(getPublishTopic()) ? View.GONE : View.VISIBLE);
        bind2userRoot.setVisibility(View.GONE);
        mMobileConnectState = MobileChannel.getInstance().getMobileConnectState();
        refreshConnectStatusUI(mMobileConnectState);//刷新当前建联状态条

        if (mMobileConnectState == MobileConnectState.CONNECTED) {//如果已经建联
            subscribeAllTV.setChecked(true);//默认为开启全部的订阅
            refreshSubscribeSingleTopicTv(-1);//同时设置单个订阅失效
        } else {//否则
            subscribeAllTV.setChecked(false);
            refreshSubscribeSingleTopicTv(-1);//同时设置单个订阅失效
        }
    }


    private String getSubscribeTopic() {
        return subscribeET.getText().toString();
    }

    private String getPublishTopic() {
        return publishTopicET.getText().toString();
    }

    private String getPublishMSG() {
        return publishmsgET.getText().toString();
    }

    private void toast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChannelActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //发布
    public void publish(String topic, String msg, IMobileRequestListener listener) {
        MobileChannel.getInstance().ayncSendPublishRequest(topic, msg, listener);
    }

    //订阅
    public void subscrbie(String topic, IMobileSubscrbieListener listener) {
        MobileChannel.getInstance().subscrbie(topic, listener);
    }

    //取消订阅
    public void unSubscrbie(String topic, IMobileSubscrbieListener listener) {
        MobileChannel.getInstance().unSubscrbie(topic, listener);
    }

    //建联
    public void appConnect() {
        //先取消之前的监听
        MobileChannel.getInstance().unRegisterConnectListener(connectListener);
        MobileChannel.getInstance().unRegisterDownstreamListener(listener);

        MobileConnectConfig config = new MobileConnectConfig();
        config.appkey = (String) EnvConfigure.getEnvArg(EnvConfigure.KEY_APPKEY);
        MobileChannel.getInstance().startConnect(this, config, connectListener);
        MobileChannel.getInstance().registerDownstreamListener(true, listener);
    }

    //刷新当前链接状态
    private void refreshConnectStatusUI(MobileConnectState state) {
        switch (state) {
            case DISCONNECTED:
            case CONNECTFAIL:
                connectStatusRoot.setBackgroundColor(getResources().getColor(R.color.channeldebug_pri_unenable_color));
                connectStatusIcon.setImageResource(R.mipmap.channel_statues_unconnect);
                connectStatusDes.setText(R.string.channle_connectstatus_uncon);
                if (!subscribeAllTV.isChecked() && state == MobileConnectState.CONNECTFAIL) {//如果建联失败
                    subscribeAllTV.setChecked(false);
                    toast("订阅失败！建立连接失败！请重试！");
                }
                break;
            case CONNECTED:
                connectStatusRoot.setBackgroundColor(getResources().getColor(R.color.channeldebug_color_connect));
                connectStatusIcon.setImageResource(R.mipmap.channel_statues_connected);
                connectStatusDes.setText(R.string.channle_connectstatus_con);
                if (!subscribeAllTV.isChecked()) {//如果此时订阅全部按钮没有开启，TODO 添加判断单个是否开启
                    subscribeAllTV.setChecked(true);
                }
                break;
            case CONNECTING:
                connectStatusRoot.setBackgroundColor(getResources().getColor(R.color.channeldebug_color_connect));
                connectStatusIcon.setImageResource(R.mipmap.channel_statues_connecting);
                connectStatusDes.setText(R.string.channle_connectstatus_con_ing);
                break;
        }
    }


    //绑定账号
    public void appBind(String iottoken) {
        MobileChannel.getInstance().bindAccount(iottoken, new IMobileRequestListener() {
            @Override
            public void onSuccess(String jsonData) {
                toast(getResources().getString(R.string.channel_bindaccount_suc_msg));
                Log.d(TAG, "appBind(), onSuccess, rsp = " + jsonData);
            }

            @Override
            public void onFailure(AError error) {
                Log.d(TAG, "appBind(), onFailure, error = " + error.getMsg());
                toast(getResources().getString(R.string.channel_bindaccount_suc_msg) + error.getMsg());
                if (bind2user.isChecked()) {
                    bind2user.setChecked(false);
                }
            }
        });
    }

    //解绑账号
    public void appUnBind(String iottoken) {
        MobileChannel.getInstance().unBindAccount(new IMobileRequestListener() {
            @Override
            public void onSuccess(String jsonData) {
                Log.d(TAG, "appUnBind(), onSuccess, rsp = " + jsonData);
            }

            @Override
            public void onFailure(AError error) {
                Log.d(TAG, "appUnBind(), onFailure, rsp = " + error.getMsg());
                if (!bind2user.isChecked()) {
                    bind2user.setChecked(true);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topbar_back_imageview:
                this.onBackPressed();
                break;
            case R.id.topbar_menu_textview://填写默认数据
                scrollView.scrollTo(0, 0);
                String defaultTopic = "/message/push";
                String defaultTopicMSg = "{\"msg\":\"test\"}";
                publishTopicET.setText(defaultTopic);
                publishTopicET.setSelection(defaultTopic.length());
                publishmsgET.setText(defaultTopicMSg);
                publishmsgET.setSelection(defaultTopicMSg.length());
                break;
            case R.id.channel_subscribe_btn:
                //subscribe
                if (TextUtils.isEmpty(getSubscribeTopic())) {
                    toast(getResources().getString(R.string.channel_please_input_correct_topic));
                    return;
                }
                if (mCurrentSubSinTopicBtnSta == 0) {//未定阅状态---执行订阅
                    subscrbie(getSubscribeTopic(), new IMobileSubscrbieListener() {

                        @Override
                        public void onSuccess(String s) {
                            toast(getResources().getString(R.string.channel_subscribe_singletopic_sucess_msg) + getSubscribeTopic());
                            refreshSubscribeSingleTopicTv(1);//更新为已订阅状态
                            //                            showTheMsgPushDown(getResources().getString(R.string.channel_subscribe_singletopic_sucess_msg)  + getSubscribeTopic() + "     response=" + s);
                        }

                        @Override
                        public void onFailed(String s, AError aError) {
                            toast(getResources().getString(R.string.channel_subscribe_singletopic_fail_msg) + s);
                            //                            showTheMsgPushDown(getResources().getString(R.string.channel_subscribe_singletopic_fail_msg) + getSubscribeTopic() + "     response=" + s + "  error=" + aError.getMsg());

                        }

                        @Override
                        public boolean needUISafety() {
                            return true;
                        }
                    });
                } else if (mCurrentSubSinTopicBtnSta == 1) {//已订阅状态---执行取消订阅
                    unSubscrbie(getSubscribeTopic(), new IMobileSubscrbieListener() {
                        @Override
                        public void onSuccess(String s) {
                            toast(getResources().getString(R.string.channel_unsubscribe_singletopic_sucess_msg) + getSubscribeTopic());
                            refreshSubscribeSingleTopicTv(0);//更新为未状态
                        }

                        @Override
                        public void onFailed(String s, AError aError) {
                            toast(getResources().getString(R.string.channel_unsubscribe_singletopic_fail_msg) + getSubscribeTopic());
                        }

                        @Override
                        public boolean needUISafety() {
                            return true;
                        }
                    });
                }
                break;
            case R.id.channel_publish_tv: //publish
                if (TextUtils.isEmpty(getPublishTopic()) || !getPublishTopic().startsWith("/") || getPublishTopic().contains("#") || getPublishTopic().contains("+")) {
                    toast(getResources().getString(R.string.channel_please_input_correct_topic));
                    return;
                }
                if (TextUtils.isEmpty(getPublishMSG())) {
                    toast(getResources().getString(R.string.channel_please_input_correct_topic_params));
                    return;
                }
                MobileChannel.getInstance().ayncSendPublishRequest(getPublishTopic(), null, new IMobileRequestListener() {

                    @Override
                    public void onSuccess(String s) {
                        toast(getResources().getString(R.string.channel_publish_sucess));
                        //showTheMsgPushDown("发布成功" + getPublishTopic() + "     response=" + s);

                    }

                    @Override
                    public void onFailure(AError aError) {
                        toast(getResources().getString(R.string.channel_publish_fail));
                        //showTheMsgPushDown("发布失败" + getPublishMSG() + "  error=" + aError.getMsg());
                    }
                });
                break;
            case R.id.channel_publish_topic_et_delete://删除显示内容
                publishTopicET.setText("");
                publishmsgET.setText("");
                break;
            case R.id.channel_deletepushmsg_textview:
                if (TextUtils.isEmpty(msgET.getText())) {
                    toast("信息已经全部清空");
                    return;
                }
                msgET.setText("");
                break;

        }
    }

    //TODO 是否需要添加缓存
    private void showTheMsgPushDown(String newMsg) {
        msgET.setText(msgET.getText() + "\n" + newMsg);
        msgET.setSelection(msgET.length());
    }

    //刷新单独发布按钮状态
    private void refreshSubscribeSingleTopicTv(int status) {
        mCurrentSubSinTopicBtnSta = status;
        switch (status) {
            case -1://不可用状态
                subscribeSingleTopicRoot.setBackgroundColor(getResources().getColor(R.color.channeldebug_pri_unenable_bgcolor));
                subscribeET.setBackground(new ColorDrawable(Color.TRANSPARENT));
                subscribeET.setText("");
                subscribeET.setEnabled(false);
                subscribeTV.setBackground(getResources().getDrawable(R.drawable.channeldebug_hollow_selector_btnbg));
                subscribeTV.setText(R.string.channel_subscribe_single);
                subscribeTV.setTextColor(getResources().getColor(R.color.channeldebug_pri_unenable_color));
                subscribeTV.setEnabled(false);
                break;
            case 0://未定阅状态
                subscribeSingleTopicRoot.setBackgroundColor(Color.WHITE);
                subscribeET.setBackground(new ColorDrawable(Color.WHITE));
                subscribeET.setEnabled(true);
                subscribeTV.setBackground(getResources().getDrawable(R.drawable.channel_sub_selector_btnbg));
                subscribeTV.setText(R.string.channel_subscribe_single);
                subscribeTV.setTextColor(Color.WHITE);
                subscribeTV.setEnabled(true);
                break;
            case 1://已订阅
                subscribeSingleTopicRoot.setBackgroundColor(Color.WHITE);
                subscribeET.setBackground(new ColorDrawable(Color.WHITE));
                subscribeET.setEnabled(false);
                subscribeTV.setBackground(getResources().getDrawable(R.drawable.channeldebug_hollow_selector_btnbg));
                subscribeTV.setText(R.string.channel_unsubscribe_single);
                subscribeTV.setTextColor(getResources().getColor(R.color.channeldebug_pri_color));
                subscribeTV.setEnabled(true);
                break;
        }

    }

    /*-----处理EditText和根部局ScroolView的冲突---start--*/
    private void initEditTextAbout() {
        ViewGroup acRootView = (ViewGroup) ((ViewGroup) (findViewById(android.R.id.content))).getChildAt(0);
        handleEditText(acRootView);
    }

    //递归处理
    private void handleEditText(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            final View subView = viewGroup.getChildAt(i);
            if (subView == null) {
                continue;
            }
            if (subView instanceof ViewGroup) {
                handleEditText((ViewGroup) subView);
            } else if (subView instanceof EditText) {
                subView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (v.getId() == subView.getId() && canVerticalScroll((EditText) v)) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                            }
                            Log.d("channel", "onTouch: true" + event.getAction());
                        }
                        return false;
                    }
                });
            }
        }
    }

    /**
     * 判断EditText是否可以滑动 @return true：可以滚动 false：不可以滚动
     */
    private boolean canVerticalScroll(EditText editText) {
        int scrollY = editText.getScrollY();
        int scrollRange = editText.getLayout().getHeight();
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() - editText.getCompoundPaddingBottom();
        int scrollDifference = scrollRange - scrollExtent;
        if (scrollDifference == 0) {
            return false;
        }
        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    /*-----处理EditText和根部局ScroolView的冲突---end--*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MobileChannel.getInstance().unRegisterDownstreamListener(listener);
        MobileChannel.getInstance().unRegisterConnectListener(connectListener);

    }
}
