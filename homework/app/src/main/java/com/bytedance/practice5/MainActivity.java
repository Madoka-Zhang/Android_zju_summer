package com.bytedance.practice5;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ReportFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
//import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bytedance.practice5.socket.SocketActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.bytedance.practice5.model.*;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "chapter5";
    private FeedAdapter adapter = new FeedAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UploadActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_mine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(Constants.STUDENT_ID);
            }
        });

        findViewById(R.id.btn_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(null);
            }
        });
        findViewById(R.id.btn_socket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SocketActivity.class);
                startActivity(intent);
            }
        });



    }

    //TODO 2
    // 用HttpUrlConnection实现获取留言列表数据，用Gson解析数据，更新UI（调用adapter.setData()方法）
    // 注意网络请求和UI更新分别应该放在哪个线程中
    private void getData(String studentId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlStr;
                if (studentId != null) {
                    urlStr = String.format("%smessages?student_id=%s", Constants.BASE_URL, studentId);
                } else {
                    urlStr = String.format("%smessages", Constants.BASE_URL);
                }
                List<Message> result = null;
                try {
                    URL url = new URL(urlStr);
                    Log.d("RealURL", urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(6000);

                    conn.setRequestMethod("GET");

                    conn.setRequestProperty("token", Constants.token);

                    if (conn.getResponseCode() == 200) {
                        InputStream in =conn.getInputStream();
                        BufferedReader reader =new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                        MessageListResponse returnRes = new Gson().fromJson(reader, new TypeToken<MessageListResponse>(){}.getType());
                        result = returnRes.feeds;

                        reader.close();
                        in.close();
                    } else {
                    }
                    conn.disconnect();
                    List<Message> finalResult = result;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setData(finalResult);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "网络异常" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }).start();
    }


}