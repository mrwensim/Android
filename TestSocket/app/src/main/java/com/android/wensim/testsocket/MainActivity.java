package com.android.wensim.testsocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
    Socket socket = null;
    String buffer = "";
    TextView txt1;
    Button send;
    EditText ed1;
    String geted1;
    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            txt1.append("server:"+bundle.getString("msg")+"\n");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt1 = (TextView) findViewById(R.id.txt1);
        send = (Button) findViewById(R.id.send);
        ed1 = (EditText) findViewById(R.id.ed1);
        send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                geted1 = ed1.getText().toString();
                txt1.append("client:"+geted1+"\n");
                //启动线程 向服务器发送和接收信息
                new MyThread(geted1).start();
            }
        });

    }

    class MyThread extends Thread {

        public String txt1;

        public MyThread(String str) {
            txt1 = str;
        }

        @Override
        public void run() {
            //定义消息
            Message msg = new Message();
            msg.what = 0x11;
            Bundle bundle = new Bundle();
            bundle.clear();
            try {
                //连接服务器 并设置连接超时为5秒
                socket = new Socket();
                socket.connect(new InetSocketAddress("59.173.13.207",8888), 50000);
                //获取输入输出流
                OutputStream ou = socket.getOutputStream();
                InputStream sin = socket.getInputStream();
                byte ibuf[] = new byte[512];
                int len = sin.read(ibuf);
                String buffer = new String(ibuf, 0, len-1);


                //向服务器发送信息
                ou.write(ed1.getText().toString().getBytes());
                ou.flush();

//                //接收
//                byte ibuf[] = new byte[512];
//                int len = sin.read(ibuf);
//                String buffer = new String(ibuf, 0, len-1);
                bundle.putString("msg", buffer);
                msg.setData(bundle);
                //发送消息 修改UI线程中的组件
                myHandler.sendMessage(msg);
                //关闭各种输入输出流
                ou.close();
                socket.close();
            } catch (SocketTimeoutException aa) {
                //连接超时 在UI界面显示消息
                bundle.putString("msg", "服务器连接失败！请检查网络是否打开");
                msg.setData(bundle);
                //发送消息 修改UI线程中的组件
                myHandler.sendMessage(msg);
            }   catch (ConnectException e) {
                e.printStackTrace();
            } catch (BindException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}