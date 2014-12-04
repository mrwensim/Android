package com.android.wensim.smartlock;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by YWZ on 2014/12/2.
 */
public class SocThread extends Thread {
    private Socket client = null;
    private String ip = "59.173.13.207";
    private int port = 8888;
    private int timeout = 10000;
    private String TAG = "Socket Thread";
    private String TAG_SEND = "Send Method";

    PrintWriter out;
    BufferedReader in;
    public boolean isRun = true;
    Handler inHandler;
    Handler outHandler;
    Context ctx;

    public SocThread(Handler handlerin, Handler handlerout, Context context) {
        inHandler = handlerin;
        outHandler = handlerout;
        ctx = context;
    }

    @Override
    public void run() {
        try {
            Log.i(TAG, "Start Thread");
            String line = "";
            Log.i(TAG, "Socket Connecting...");
            client = new Socket();
            client.connect(new InetSocketAddress(ip, port), timeout);
            Log.i(TAG, "Socket Connect Success");
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
            Log.i(TAG, "输入输出流获取成功");
            while (isRun) {
                try {
                    Log.i(TAG, "Thread is running");
                    if (client != null) {
                        Log.i(TAG, "Read Message From Server");
                        while ((line = in.readLine()) != null) {
                            Message msg = inHandler.obtainMessage();
                            msg.obj = line;
                            Log.i(TAG, "Update UI");
                            inHandler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketTimeoutException aa) {
            aa.printStackTrace();
        }   catch (ConnectException e) {
            e.printStackTrace();
        } catch (BindException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Send(String msg_to_send) {
        try {
            if (client != null) {
                Log.i(TAG_SEND, "Send Start");
                out.println(msg_to_send);
                out.flush();
                Log.i(TAG_SEND, "Send Success");
                Message msg = outHandler.obtainMessage();
                msg.obj = msg_to_send;
                msg.what = 1;
                outHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            Log.i(TAG_SEND, "Send Error");
            e.printStackTrace();
        } finally {
            Log.i(TAG_SEND, "Send Finished");
        }
    }
}
