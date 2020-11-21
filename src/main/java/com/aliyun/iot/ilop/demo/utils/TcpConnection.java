package com.aliyun.iot.ilop.demo.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpConnection {

    private static TcpConnection tcpConnection;
    private Context mContext;
    private Socket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private CallBack mCallBack;

    //banlap： 回调连接消息
    public interface CallBack {
        void deviceMsg(String msg);
    }
    public void getDeviceMsg(CallBack callBack) { this.mCallBack = callBack; }


    public static TcpConnection getInstance() {
        if (tcpConnection == null) {
            tcpConnection = new TcpConnection();
        }
        return tcpConnection;
    }

    public void init(Context context) {
        mContext = context;
    }

    //banlap： socket 进行tcp连接 ip地址
    public void connectToDevice(String ip){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    mSocket = new Socket(ip, 30000);
                    if(mSocket.isConnected()&&!mSocket.isClosed()){
                        mInputStream = mSocket.getInputStream();
                        mOutputStream = mSocket.getOutputStream();
                        //接收数据
                        //receiveMsg();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mCallBack.deviceMsg("连接失败");
                }
            }
        }).start();
    }

    //banlap： 发送数据
    public void writeMsg(String msg, String ip){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //banlap： 当tcp断开时重新连接
                    connectToDevice(ip);
                    Thread.sleep(500);

                    byte[] buf = ByteUtil.stringToAsc(msg);
                    mOutputStream.write(buf);
                    mOutputStream.flush();

                    receiveMsg();
                } catch (Exception e) {
                    e.printStackTrace();
                    mCallBack.deviceMsg("发送数据失败");
                }
            }
        }).start();

    }

    //banlap： 接收数据
    public void receiveMsg(){

        while(mSocket.isConnected()){
            try{
                byte[] bt = new byte[100];
                int length = mInputStream.available();
                byte[] bs = new byte[length];
                int l = mInputStream.read(bs);
                String str = new String(bs, "UTF-8");
                mCallBack.deviceMsg("接收数据: " + str);
                /*Looper.prepare();
                Toast.makeText(mContext,"接收成功: " + str, android.widget.Toast.LENGTH_LONG).show();
                Looper.loop();*/
            } catch (Exception e){
              e.printStackTrace();
            }
        }

    }

    //关闭连接
    public void disconnect(){

    }



}
