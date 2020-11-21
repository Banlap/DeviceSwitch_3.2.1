package com.aliyun.iot.ilop.demo.page.alog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.aep.sdk.framework.AActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ALogActivity extends AActivity {
    String mKeyWords = "";
    EditText mALogKeyWordsET;
    ScrollView mALogShowSv;
    final Handler mH = new Handler();
    boolean mLock = false;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alog_demo_activity);

        final TextView aLogShowTv = (TextView) findViewById(R.id.alog_show_tv);
        aLogShowTv.setMovementMethod(ScrollingMovementMethod.getInstance());

        mALogKeyWordsET = (EditText) findViewById(R.id.alog_keywords_et);

        mALogShowSv = (ScrollView) findViewById(R.id.alog_show_sv);

        TextView topBarTitleTv = (TextView) findViewById(R.id.topbar_title_textview);
        topBarTitleTv.setText(R.string.alog_title);

        ImageView topBarBackIv = (ImageView) findViewById(R.id.topbar_back_imageview);
        topBarBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button aLogClearBtn = (Button) findViewById(R.id.alog_clear_btn);
        aLogClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aLogShowTv.setText("");
            }
        });

        Button aLogCopyBtn = findViewById(R.id.alog_copy_btn);
        aLogCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                copyLog(radioButton.getText().toString());
            }
        });

        Button aLogVBtn = (Button) findViewById(R.id.alog_v_btn);
        aLogVBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aLogShowTv.setText("");
                if (mLock) {
                    return;
                }
                mLock = true;
                String logstr = getCurLogcat("V");
                aLogShowTv.setText(Html.fromHtml(logstr));
                refreshShowLog();

            }
        });

        Button aLogDBtn = (Button) findViewById(R.id.alog_d_btn);
        aLogDBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aLogShowTv.setText("");
                if (mLock) {
                    return;
                }
                mLock = true;
                String logstr = getCurLogcat("D");
                aLogShowTv.setText(Html.fromHtml(logstr));
                refreshShowLog();
            }
        });

        Button aLogIBtn = (Button) findViewById(R.id.alog_i_btn);
        aLogIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aLogShowTv.setText("");
                if (mLock) {
                    return;
                }
                mLock = true;
                String logstr = getCurLogcat("I");
                aLogShowTv.setText(Html.fromHtml(logstr));
                refreshShowLog();
            }
        });

        Button aLogEBtn = (Button) findViewById(R.id.alog_e_btn);
        aLogEBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aLogShowTv.setText("");
                if (mLock) {
                    return;
                }
                mLock = true;
                String logstr = getCurLogcat("E");
                aLogShowTv.setText(Html.fromHtml(logstr));
                refreshShowLog();
            }
        });

        Button aLogWBtn = (Button) findViewById(R.id.alog_w_btn);
        aLogWBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aLogShowTv.setText("");
                if (mLock) {
                    return;
                }
                mLock = true;
                String logstr = getCurLogcat("W");
                aLogShowTv.setText(Html.fromHtml(logstr));
                refreshShowLog();
            }
        });

        radioGroup = (RadioGroup) findViewById(R.id.alog_rg);
        radioGroup.check(R.id.alog_v_btn);
        aLogVBtn.callOnClick();

        mALogKeyWordsET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //完成自己的事件
                    RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                    radioButton.callOnClick();
                }
                return false;
            }
        });

    }

    private void refreshShowLog() {
        mH.post(new Runnable() {
            @Override
            public void run() {
                mALogShowSv.fullScroll(ScrollView.FOCUS_DOWN);
                mLock = false;
            }
        });
    }

    private String getCurLogcat(String level) {
        ALog.d("JC", "getCurLogcat   " + level);
        mKeyWords = mALogKeyWordsET.getText().toString();
        StringBuilder log = new StringBuilder();
        log.append("log start");
        log.append("<br /><br />");
        try {
            int count = 150;
            if (mKeyWords.equalsIgnoreCase("")) {
                count = 50;
            } else {
                if (mKeyWords.length() < 2) {
                    count = 50;
                }
            }

            java.lang.Process process = Runtime.getRuntime().exec("logcat -v time -t " + count + " *:" + level);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (mKeyWords.equalsIgnoreCase("")) {
                    String levelstr = line.substring(15, 25);
                    if (levelstr.contains("W/")) {
                        line = "<font color=\"#EE9A00\">" + line + "</font>";
                    } else if (levelstr.contains("E/")) {
                        line = "<font color=\"#FF0000\">" + line + "</font>";
                    } else if (levelstr.contains("I/")) {
                        line = "<font color=\"#00CD00\">" + line + "</font>";
                    }
                    if (levelstr.contains("D/")) {
                        line = "<font color=\"#4169E1\">" + line + "</font>";
                    }
                    log.append(line + "<br /><br />");
                } else {
                    if (line.contains(mKeyWords)) {
                        line = line.replace(mKeyWords, "<font color=\"#FF0000\"><em>" + mKeyWords + "</em></font>");
                        log.append(line + "<br /><br />");
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return log.toString();
    }


    private void copyLog(String level) {
        ThreadPool.DefaultThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                final StringBuilder log = new StringBuilder();
                BufferedReader bufferedReader = null;
                try {
                    Process process = Runtime.getRuntime().exec("logcat -v time -t " + 150 + " *:" + level);
                    bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        log.append(line);
                        log.append("\n");
                    }
                    ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            //获取剪贴板管理器：
                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                            // 创建普通字符型ClipData
                            ClipData mClipData = ClipData.newPlainText("LOG", log);
                            // 将ClipData内容放到系统剪贴板里。
                            cm.setPrimaryClip(mClipData);
                            Toast.makeText(ALogActivity.this, "已复制到剪切板", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    ThreadPool.MainThreadHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ALogActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }


            }
        });
    }


}
