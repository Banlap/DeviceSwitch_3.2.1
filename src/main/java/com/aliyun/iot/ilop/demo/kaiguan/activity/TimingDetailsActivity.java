package com.aliyun.iot.ilop.demo.kaiguan.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.kaiguan.bean.SwitchDataBean;
import com.aliyun.iot.ilop.demo.utils.DialogTools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimingDetailsActivity extends AppCompatActivity implements View.OnClickListener {


    private LinearLayout ll_time_settings, ll_time_open, ll_time_close;
    private TextView tv_saveTime, tv_time;
    private ImageView iv_time_open, iv_time_close;

    private boolean isClickMon=false, isClickTue=false, isClickWed=false, isClickThu=false, isClickFri=false, isClickSat=false, isClickSun=false;
    private int hour = 0;
    private int minu = 0;

    //点击popwindow完成按钮后 存入 时间 日期
    private String mTime;
    private String mWeek;
    private boolean mIsOpen = true;

    //判断是否点击时间
    private boolean mIsClickSelectTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparencyBar(TimingDetailsActivity.this);
        setContentView(R.layout.activity_timing_details);
        initView();
    }

    private void initView() {
        ll_time_settings=findViewById(R.id.ll_time_settings);
        ll_time_settings.setOnClickListener(this);
        ll_time_open=findViewById(R.id.ll_time_open);
        ll_time_open.setOnClickListener(this);
        ll_time_close=findViewById(R.id.ll_time_close);
        ll_time_close.setOnClickListener(this);
        tv_saveTime=findViewById(R.id.tv_saveTime);
        tv_saveTime.setOnClickListener(this);
        tv_time=findViewById(R.id.tv_time);
        tv_time.setText("点击选择时间");
        iv_time_open=findViewById(R.id.iv_time_open);
        iv_time_close=findViewById(R.id.iv_time_close);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ll_time_open:
                iv_time_open.setImageResource(R.mipmap.icon_dagou);
                iv_time_close.setImageResource(R.mipmap.txian);
                mIsOpen = true;
                break;

            case R.id.ll_time_close:
                iv_time_open.setImageResource(R.mipmap.txian);
                iv_time_close.setImageResource(R.mipmap.icon_dagou);
                mIsOpen = false;
                break;

            case R.id.tv_saveTime:
                //判断必须点击完成 选择时间后才能保存
                if(mIsClickSelectTime) {
                    Intent intent = new Intent();
                    intent.putExtra("saveTime", mTime);
                    intent.putExtra("saveWeek", mWeek);
                    intent.putExtra("saveIsOpen", mIsOpen);
                    setResult(1, intent);//有返回值的使用这个，没有要返回的值用setResult(0);
                    finish();
                } else {
                    Toast.makeText(TimingDetailsActivity.this, "请选择时间再保存", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.ll_time_settings:

                isClickMon=false;
                isClickTue=false;
                isClickWed=false;
                isClickThu=false;
                isClickFri=false;
                isClickSat=false;
                isClickSun=false;
                View viewEdit = getLayoutInflater().inflate(R.layout.popwindow_time_settings, null);

                TimePicker timePicker = (TimePicker) viewEdit.findViewById(R.id.time_picker);
                //初始化时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                timePicker.setIs24HourView(true);
                timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                timePicker.setCurrentMinute(Calendar.MINUTE);
                //默认获取时间
                hour = timePicker.getCurrentHour();
                minu = timePicker.getCurrentMinute();

                //滚动控件 更新时间信息
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        hour = timePicker.getCurrentHour();
                        minu = timePicker.getCurrentMinute();
                    }
                });

                DialogTools mDialogTools = new DialogTools(TimingDetailsActivity.this, 1, 1, viewEdit, R.style.DialogTheme);
                mDialogTools.setCancelable(true);
                mDialogTools.show();

                //点击选择 星期
                mDialogTools.findViewById(R.id.popw_ll_week).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View viewEdit2 = getLayoutInflater().inflate(R.layout.popwindow_dayweek_settings, null);
                        TextView tv_week_mon = viewEdit2.findViewById(R.id.tv_week_mon);
                        TextView tv_week_tue = viewEdit2.findViewById(R.id.tv_week_tue);
                        TextView tv_week_wed = viewEdit2.findViewById(R.id.tv_week_wed);
                        TextView tv_week_thu = viewEdit2.findViewById(R.id.tv_week_thu);
                        TextView tv_week_fri = viewEdit2.findViewById(R.id.tv_week_fri);
                        TextView tv_week_sat = viewEdit2.findViewById(R.id.tv_week_sat);
                        TextView tv_week_sun = viewEdit2.findViewById(R.id.tv_week_sun);

                        if(isClickMon) {
                            tv_week_mon.setBackgroundResource(R.mipmap.icon_fuxuankuang);
                        } else {
                            tv_week_mon.setBackgroundResource(R.mipmap.icon_fuxuankuang2);
                        }
                        if(isClickTue) {
                            tv_week_tue.setBackgroundResource(R.mipmap.icon_fuxuankuang);
                        } else {
                            tv_week_tue.setBackgroundResource(R.mipmap.icon_fuxuankuang2);
                        }
                        if(isClickWed) {
                            tv_week_wed.setBackgroundResource(R.mipmap.icon_fuxuankuang);
                        } else {
                            tv_week_wed.setBackgroundResource(R.mipmap.icon_fuxuankuang2);
                        }
                        if(isClickThu) {
                            tv_week_thu.setBackgroundResource(R.mipmap.icon_fuxuankuang);
                        } else {
                            tv_week_thu.setBackgroundResource(R.mipmap.icon_fuxuankuang2);
                        }
                        if(isClickFri) {
                            tv_week_fri.setBackgroundResource(R.mipmap.icon_fuxuankuang);
                        } else {
                            tv_week_fri.setBackgroundResource(R.mipmap.icon_fuxuankuang2);
                        }
                        if(isClickSat) {
                            tv_week_sat.setBackgroundResource(R.mipmap.icon_fuxuankuang);
                        } else {
                            tv_week_sat.setBackgroundResource(R.mipmap.icon_fuxuankuang2);
                        }
                        if(isClickSun) {
                            tv_week_sun.setBackgroundResource(R.mipmap.icon_fuxuankuang);
                        } else {
                            tv_week_sun.setBackgroundResource(R.mipmap.icon_fuxuankuang2);
                        }

                        DialogTools mDialogTools2 = new DialogTools(TimingDetailsActivity.this, 1, 1, viewEdit2, R.style.DialogTheme);
                        mDialogTools2.setCancelable(true);
                        mDialogTools2.show();
                        //日期选择 周一
                        mDialogTools2.findViewById(R.id.ll_week_mon).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!isClickMon) {
                                    tv_week_mon.setBackgroundResource(R.mipmap.icon_fuxuankuang);
                                    isClickMon = true;
                                } else {
                                    tv_week_mon.setBackgroundResource(R.mipmap.icon_fuxuankuang2);
                                    isClickMon = false;
                                }

                            }
                        });
                        //日期选择 周二
                        mDialogTools2.findViewById(R.id.ll_week_tue).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!isClickTue) {
                                    tv_week_tue.setBackgroundResource(R.mipmap.icon_fuxuankuang);
                                    isClickTue = true;
                                } else {
                                    tv_week_tue.setBackgroundResource(R.mipmap.icon_fuxuankuang2);
                                    isClickTue = false;
                                }

                            }
                        });
                        //日期选择 周三
                        mDialogTools2.findViewById(R.id.ll_week_wed).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!isClickWed) {
                                    tv_week_wed.setBackgroundResource(R.mipmap.icon_fuxuankuang);
                                    isClickWed = true;
                                } else {
                                    tv_week_wed.setBackgroundResource(R.mipmap.icon_fuxuankuang2);
                                    isClickWed = false;
                                }

                            }
                        });
                        //日期选择 周四
                        mDialogTools2.findViewById(R.id.ll_week_thu).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!isClickThu) {
                                    tv_week_thu.setBackgroundResource(R.mipmap.icon_fuxuankuang);
                                    isClickThu = true;
                                } else {
                                    tv_week_thu.setBackgroundResource(R.mipmap.icon_fuxuankuang2);
                                    isClickThu = false;
                                }

                            }
                        });
                        //日期选择 周五
                        mDialogTools2.findViewById(R.id.ll_week_fri).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!isClickFri) {
                                    tv_week_fri.setBackgroundResource(R.mipmap.icon_fuxuankuang);
                                    isClickFri = true;
                                } else {
                                    tv_week_fri.setBackgroundResource(R.mipmap.icon_fuxuankuang2);
                                    isClickFri = false;
                                }

                            }
                        });
                        //日期选择 周六
                        mDialogTools2.findViewById(R.id.ll_week_sat).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!isClickSat) {
                                    tv_week_sat.setBackgroundResource(R.mipmap.icon_fuxuankuang);
                                    isClickSat = true;
                                } else {
                                    tv_week_sat.setBackgroundResource(R.mipmap.icon_fuxuankuang2);
                                    isClickSat = false;
                                }

                            }
                        });
                        //日期选择 周日
                        mDialogTools2.findViewById(R.id.ll_week_sun).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!isClickSun) {
                                    tv_week_sun.setBackgroundResource(R.mipmap.icon_fuxuankuang);
                                    isClickSun = true;
                                } else {
                                    tv_week_sun.setBackgroundResource(R.mipmap.icon_fuxuankuang2);
                                    isClickSun = false;
                                }

                            }
                        });

                        //日期选择 确定
                        mDialogTools2.findViewById(R.id.popw_tv_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mDialogTools2.hide();
                            }
                        });
                        //日期选择 取消
                        mDialogTools2.findViewById(R.id.popw_tv_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mDialogTools2.hide();
                            }
                        });

                    }
                });

                //点击 保存时间
                mDialogTools.findViewById(R.id.popw_tv_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialogTools.hide();
                        //判断是否 点击完成 保存时间
                        mIsClickSelectTime = true;
                        //Toast.makeText(TimingListActivity.this, "时间：" + hour + ":" + minu , Toast.LENGTH_LONG).show();
                        String weekText = "";

                        if(isClickMon){weekText=weekText.concat(" 周一");};
                        if(isClickTue){weekText=weekText.concat(" 周二");};
                        if(isClickWed){weekText=weekText.concat(" 周三");};
                        if(isClickThu){weekText=weekText.concat(" 周四");};
                        if(isClickFri){weekText=weekText.concat(" 周五");};
                        if(isClickSat){weekText=weekText.concat(" 周六");};
                        if(isClickSun){weekText=weekText.concat(" 周日");};

                        if(isClickMon&&isClickTue&&isClickWed&&isClickThu&&isClickFri&&isClickSat&&isClickSun){
                            weekText=" 每天";
                        }
                        if(!isClickMon&&!isClickTue&&!isClickWed&&!isClickThu&&!isClickFri&&!isClickSat&&!isClickSun){
                            weekText=" 每天";
                        }

                        mTime = hour + ":" + minu;
                        mWeek = weekText;

                        tv_time.setText(mTime);
                        /*adapter.clear();
                        List<SwitchDataBean> switchDataBeanList = new ArrayList<>();
                        SwitchDataBean switchDataBean=new SwitchDataBean();
                        switchDataBean.setName(hour + ":" + minu);
                        switchDataBean.setWeek(weekText);
                        switchDataBean.setEnable(true);
                        switchDataBeanList.add(switchDataBean);

                        mList.addAll(switchDataBeanList);
                        adapter.addMoreList(mList);
                        adapter.notifyDataSetChanged();*/
                    }
                });

                //点击 取消
                mDialogTools.findViewById(R.id.popw_tv_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialogTools.hide();
                    }
                });

                break;
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
}
