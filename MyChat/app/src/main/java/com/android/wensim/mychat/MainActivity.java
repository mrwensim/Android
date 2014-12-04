package com.android.wensim.mychat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.os.Handler;
import android.widget.TextView;


public class MainActivity extends Activity {
    Handler mHandler,mHandlerSend;
    boolean isRun = true;
    EditText et_send;
    Button btn_send;
    TextView textView;
    SocThread socketThread;
    private String msg_to_send = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.txt1);
        et_send = (EditText)findViewById(R.id.ed1);
        btn_send = (Button)findViewById(R.id.send);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (msg.obj != null) {
                        String s = msg.obj.toString();
                        if (s.trim().length() > 0) {
                            textView.append("Server: " + s + "\n");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        mHandlerSend = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    String s = msg.obj.toString();
                    if (msg.what == 1) {
                        textView.append("Client: " + s + "      发送成功\n");
                    } else {
                        textView.append("Client: " + s + "     发送失败\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        socketThread = new SocThread(mHandler, mHandlerSend, MainActivity.this);
        socketThread.start();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg_to_send = et_send.getText().toString();
                socketThread.Send(msg_to_send);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
