package org.cooze.jmsmqtt;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.cooze.activemq.android.MqBuilder;

public class MainActivity extends AppCompatActivity {

    private EditText server_input;
    private EditText android_input;
    private EditText host_input;
    private TextView show_msg;
    private Button conn;
    private MqBuilder mqBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init
        server_input = (EditText) findViewById(R.id.server_id_edit);
        android_input = (EditText) findViewById(R.id.android_edit);
        host_input = (EditText) findViewById(R.id.server_host_edit);
        show_msg = (TextView) findViewById(R.id.show_msg);
        conn = (Button) findViewById(R.id.connect_server);
    }

    public void conn(View view){

        String server_id = server_input.getText().toString().trim();
        String android_id = android_input.getText().toString().trim();
        String host = host_input.getText().toString().trim();

        if (isEmpty(android_id) || isEmpty(server_id) || isEmpty(host)) {
            Message m = new Message();
            Bundle b = new Bundle();
            b.putCharSequence("conn_msg", "参数为空!");
            m.setData(b);
            //发送消息到Toast中
            msgHandler.sendMessage(m);
            return;
        }


        int port = 1883;
        mqBuilder = new MqBuilder(host, port, server_id, android_id);
        Log.i("Message:", "start connet");
        mqBuilder.setMonitor((s) -> {
            Log.i("Message:", s);
            //装载消息
            Message m = new Message();
            Bundle b = new Bundle();
            b.putCharSequence("receive", s);
            m.setData(b);

            //发送消息到Toast中
            msgHandler.sendMessage(m);
        });

        Thread thread = new Thread(() -> {
            try {
                mqBuilder.start();
            } catch (Exception e) {
                Log.i("Message:", e.getMessage());
            }
        });

        thread.start();

    }


    private final Handler msgHandler = new Handler() {
        public void handleMessage(Message msg) {

            if (msg.getData().containsKey("receive")) {
                Toast.makeText(getApplicationContext(), msg.getData().get("receive") + "", Toast.LENGTH_SHORT).show();
                show_msg.append(msg.getData().get("receive") + "\n");
            } else if (msg.getData().containsKey("conn_msg")) {
                Toast.makeText(getApplicationContext(), msg.getData().get("conn_msg") + "", Toast.LENGTH_SHORT).show();
            }

        }
    };

    public boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

}
