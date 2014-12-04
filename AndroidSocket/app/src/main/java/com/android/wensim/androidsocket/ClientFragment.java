package com.android.wensim.androidsocket;



import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ClientFragment extends Fragment {
    private EditText mEditText;
    private Button mButtonCon,mButtonSend;
    private TextView textView;

    private Socket clientSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private byte[] buf;
    private String str = null;

    private Handler mHandler;

    private ReceiveThread mReceiveThread;
    private boolean stop = true;

    public ClientFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client,container,false);
        mButtonCon = (Button)view.findViewById(R.id.btn_connect);
        mButtonSend = (Button)view.findViewById(R.id.btn_send);
        mEditText = (EditText)view.findViewById(R.id.et_content);
        textView = (TextView)view.findViewById(R.id.text_rec);

        mButtonSend.setEnabled(false);
        mButtonCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    clientSocket = new Socket("59.173.13.201",8888);
                }
                catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                mButtonCon.setEnabled(false);
                mButtonSend.setEnabled(true);
                mReceiveThread = new ReceiveThread(clientSocket);
                stop = false;
                mReceiveThread.start();
            }
        });

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] msgBuffer = null;
                String text = mEditText.getText().toString();
                try {
                    msgBuffer = text.getBytes("GB2312");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                try {
                    outputStream = clientSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.write(msgBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mEditText.setText("");
                Toast.makeText(getActivity(), "发送成功！", Toast.LENGTH_SHORT).show();
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                textView.setText((msg.obj).toString());
            }
        };
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiveThread != null) {
            stop = true;
            mReceiveThread.interrupt();
        }
    }

    private class ReceiveThread extends Thread
    {
        private InputStream inStream = null;

        private byte[] buf;
        private String str = null;

        ReceiveThread(Socket s)
        {
            try {
                //获得输入流
                this.inStream = s.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run()
        {
            while(!stop)
            {
                this.buf = new byte[512];

                try {
                    //读取输入数据（阻塞）
                    this.inStream.read(this.buf);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                //字符编码转换
                try {
                    this.str = new String(this.buf, "GB2312").trim();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.obj = this.str;
                //发送消息
                mHandler.sendMessage(msg);

            }
        }


    }

}
