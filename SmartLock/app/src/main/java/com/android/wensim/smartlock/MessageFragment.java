package com.android.wensim.smartlock;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MessageFragment extends Fragment {
    Handler mHandler,mHandlerSend;
    boolean isRun = true;
    EditText et_send;
    Button btn_send;
    TextView textView;
    SocThread socketThread;
    private String msg_to_send = "";

    public static MessageFragment newInstance() {
        MessageFragment messageFragment = new MessageFragment();
        return messageFragment;
    }
    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        textView = (TextView)getView().findViewById(R.id.textview_communication);
        et_send = (EditText)getView().findViewById(R.id.et_send_msg);
        btn_send = (Button)getView().findViewById(R.id.btn_send_msg);
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
        socketThread = new SocThread(mHandler, mHandlerSend, this.getActivity());
        socketThread.start();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg_to_send = et_send.getText().toString();
                socketThread.Send(msg_to_send);
            }
        });
    }

}
