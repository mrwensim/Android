package com.android.wensim.chatonsocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 *@author Andrew.Lee
 *@create 2011-5-28 下午02:26:20
 *@version 1.0
 *@see
 */

public class MainActivity extends Activity {
    EditText editText = null;
    Button sendButton = null;
    TextView display = null;
    Socket client = null;
    MyHandler myHandler;
    OutputStream dout;
    InputStream din;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.message);
        sendButton = (Button) findViewById(R.id.send);
        display = (TextView) findViewById(R.id.display);
        sendButton.setOnClickListener(listener);
//        try {
////            client = new Socket("59.173.13.207", 8888);
//            client = new Socket();
//            client.connect(new InetSocketAddress("59.173.13.207",8888), 5000);
//            dout = new DataOutputStream(client.getOutputStream());
//            din = new DataInputStream(client.getInputStream());
//        } catch (UnknownHostException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        myHandler = new MyHandler();

        MyThread m = new MyThread();
        m.start();
    }

    class MyHandler extends Handler {
        public MyHandler() {
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d("MyHandler", "handleMessage......");
            super.handleMessage(msg);
            // 此处可以更新UI

            if (client != null && client.isConnected()) {
                Log.i("handler..", "*-----*");
                try {
                    //dout.writeUTF("1");
                    byte ibuf[] = new byte[512];
                    int len = din.read(ibuf);
                    String message = new String(ibuf, 0, len);
                    if (!message.equals(""))
                        display.setText(display.getText().toString() + "\n"
                                + "服务器发来的消息--：" + message);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

    class MyThread extends Thread {
        public void run() {
            while (true) {
                try {
                    try {
//            client = new Socket("59.173.13.207", 8888);
                        client = new Socket();
                        client.connect(new InetSocketAddress("59.173.13.207",8888), 5000);
                        dout = client.getOutputStream();
                        din = client.getInputStream();
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Message msg = new Message();
                MainActivity.this.myHandler.sendMessage(msg);
            }
        }
    }

    OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            String sendText = editText.getText().toString();
            try {
                // din = new DataInputStream(client.getInputStream());
//                dout.writeUTF(sendText);
                dout.write(sendText.getBytes());
                dout.flush();
				/*
				 * display.setText(display.getText().toString() + "\n" +
				 * "服务器发来的消息：" + din.readUTF());
				 */
				/*
				 * display.setText(display.getText().toString() + "\n" +
				 * "服务器发来的消息--：" + din.readUTF());
				 */
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };
}

