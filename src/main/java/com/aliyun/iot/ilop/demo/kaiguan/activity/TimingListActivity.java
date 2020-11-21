package com.aliyun.iot.ilop.demo.kaiguan.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.kaiguan.bean.SwitchDataBean;
import com.aliyun.iot.ilop.demo.utils.DialogTools;
import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanRVAdapter;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimingListActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_addTime;
    private RecyclerView rv_eventlist;
    private SmartRefreshLayout srl_homelist;
    private CanRVAdapter adapter;

    private List<SwitchDataBean> mList;

    private String mTime="";
    private String mWeek="";
    private boolean mIsOpen = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparencyBar(TimingListActivity.this);
        setContentView(R.layout.activity_timing_list);
        initView();
    }


    private void initView() {
       
        rv_eventlist=findViewById(R.id.rv_timelist);
        srl_homelist=findViewById(R.id.srl_timelist);
        tv_addTime=findViewById(R.id.tv_addTime);
        tv_addTime.setOnClickListener(this);
        rv_eventlist.setLayoutManager(new GridLayoutManager(TimingListActivity.this, 1));
        srl_homelist.setRefreshHeader(new ClassicsHeader(TimingListActivity.this));
        srl_homelist.setRefreshFooter(new ClassicsFooter(TimingListActivity.this));

        srl_homelist.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
               /* page++;
                initData();*/
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
               /* page = 1;
                initData();*/
            }
        });
        //WaittingDialog.initWaittingDialog(TimingListActivity.this, "正在加载中...");

        adapter = new CanRVAdapter<SwitchDataBean>(rv_eventlist, R.layout.item_timing_list) {
            @Override
            protected void setView(CanHolderHelper helper, int position, SwitchDataBean bean) {
                helper.setText(R.id.name_text,bean.getName());
                helper.setText(R.id.tv_week,bean.getWeek());
                Switch timer_switch = helper.getView(R.id.sw_timer_switch);
                if(bean.isEnable()){
                    timer_switch.setChecked(true);
                } else {
                    timer_switch.setChecked(false);
                }

                timer_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        /*ArrayList<SwitchDataBean> arrayList = new ArrayList<>();
                        SwitchDataBean switchDataBean=new SwitchDataBean();
                        switchDataBean.setName(bean.getName());
                        switchDataBean.setWeek(bean.getWeek());
                        switchDataBean.setEnable(isChecked);
                        arrayList.add(switchDataBean);

                        adapter.removeItem(position);
                        adapter.notifyDataSetChanged();

                        mList.addAll(arrayList);
                        adapter.addMoreList(mList);
                        adapter.notifyDataSetChanged();*/

                        //Toast.makeText(TimingListActivity.this, "list：" + mList.get(0).isEnable(), Toast.LENGTH_LONG).show();

                    }

                });
                /*helper.setText(R.id.name_text, bean.getEvent_name());
                helper.setText(R.id.fins_tuxedo_number,"开团："+ bean.getFins_tuxedo_number()+"人");
                helper.setText(R.id.tuxedo_number, "参团："+bean.getTuxedo_number()+"人");
                helper.setText(R.id.event_end_time,bean.getEvent_end_time());
                ImageView iv_shangpin = helper.getImageView(R.id.head_image);
                Glide.with(TimingListActivity.this).load(bean.getAdvertising_image().get(0))
                        .apply(new RequestOptions().placeholder(R.mipmap.image_default))
                        .into(iv_shangpin);
                iv_shangpin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(TimingListActivity.this, EventDetailActivity.class);
                        intent.putExtra("event_id",bean.getObjectId());
                        intent.putExtra("state","3");
                        TimingListActivity.this.startActivity(intent);
                    }
                });
                TextView noplay_button=helper.getTextView(R.id.noplay_button);
                noplay_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(TimingListActivity.this,"未支付",Toast.LENGTH_LONG).show();
                        Intent findIntent=new Intent(TimingListActivity.this, EventDetailActivity.class);
                        findIntent.putExtra("event_id",bean.getObjectId());
                        findIntent.putExtra("state","1");
                        startActivity(findIntent);
                    }
                });
                TextView paly_button=helper.getTextView(R.id.paly_button);
                paly_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent findIntent=new Intent(TimingListActivity.this, EventDetailActivity.class);
                        findIntent.putExtra("event_id",bean.getObjectId());
                        findIntent.putExtra("state","2");
                        startActivity(findIntent);
                    }
                });
                TextView start_text=helper.getTextView(R.id.start_text);
                TextView chage_time_text=helper.getTextView(R.id.chage_time_text);*/

                /*chage_time_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(TimingListActivity.this, ChageEventActivity.class);
                        intent.putExtra("ObjectId",bean.getObjectId());
                        intent.putExtra("eventEndTime",bean.getEvent_end_time());
                        if (DateUtils.isDateOneBigger(bean.getEvent_end_time(),getTimes())){
                            intent.putExtra("type","1");//代表活动进行中
                        }else {
                            intent.putExtra("type","2");//代表活动已经结束
                        }
                        startActivity(intent);
                    }
                });*/


            }

            @Override
            protected void setItemListener(CanHolderHelper helper) {
            }
        };
        initData();
        rv_eventlist.setAdapter(adapter);


    }

    private void initData() {

        /*SwitchDataBean switchDataBean=new SwitchDataBean();
        switchDataBean.setName("19:00");
        mList.add(switchDataBean);
        SwitchDataBean switchDataBean1=new SwitchDataBean();
        switchDataBean1.setName("23:59");
        mList.add(switchDataBean1);
        SwitchDataBean switchDataBean2=new SwitchDataBean();
        switchDataBean2.setName("8:50");
        mList.add(switchDataBean2);
        SwitchDataBean switchDataBean3=new SwitchDataBean();
        switchDataBean3.setName("9:01");
        mList.add(switchDataBean3);*/

        if((List<SwitchDataBean>) getIntent().getSerializableExtra("timeList")!=null){
            mList = (List<SwitchDataBean>) getIntent().getSerializableExtra("timeList");
        } else {
            mList=new ArrayList<>();
        }


        adapter.setList(mList);
        adapter.notifyDataSetChanged();
        srl_homelist.finishRefresh();
        srl_homelist.finishLoadMore();


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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_addTime:
                Intent intentAddTime = new Intent(TimingListActivity.this, TimingDetailsActivity.class);
                startActivityForResult(intentAddTime,  1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==1) {
            if (requestCode == 1) {
                mTime = data.getStringExtra("saveTime");
                mWeek = data.getStringExtra("saveWeek");
                mIsOpen = data.getBooleanExtra("saveIsOpen",true);
                adapter.clear();
                List<SwitchDataBean> switchDataBeanList = new ArrayList<>();
                SwitchDataBean switchDataBean=new SwitchDataBean();
                switchDataBean.setName(mTime);
                switchDataBean.setWeek(mWeek);
                switchDataBean.setEnable(mIsOpen);
                switchDataBeanList.add(switchDataBean);

                mList.addAll(switchDataBeanList);
                adapter.addMoreList(mList);
                adapter.notifyDataSetChanged();
            }
        }
    }

    //重写onKeyDown
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(getClass().getName(), "onKeyDown:keyCode === " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Toast.makeText(TimingListActivity.this, "back:", Toast.LENGTH_SHORT).show();
            List<SwitchDataBean> list = new ArrayList<>();
            if(mList.size()>0){
                Intent intent=new Intent();
                for (int i=0; i<mList.size(); i++){
                    if(mList.get(i).isEnable()){
                        intent.putExtra("TimingTime",mList.get(i).getName());//返回值
                        intent.putExtra("TimingWeek",mList.get(i).getWeek());//返回值
                        /*SwitchDataBean sdbean = new SwitchDataBean();
                        sdbean.setName(mList.get(i).getName());
                        sdbean.setWeek(mList.get(i).getWeek());
                        sdbean.setEnable(mList.get(i).isEnable());
                        list.add(sdbean);*/
                    }
                }
                //intent.putExtra("TimingTimeList", (Serializable) list);
                setResult(4,intent);//有返回值的使用这个，没有要返回的值用setResult(0);
                finish();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

