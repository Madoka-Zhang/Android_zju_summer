package com.bytedance.practice5;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.practice5.model.Message;
import com.bytedance.practice5.model.UploadResponse;
import com.facebook.common.internal.Objects;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity {
    private static final String TAG = "chapter5";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private IApi api;
    private Uri coverImageUri;
    private SimpleDraweeView coverSD;
    private EditText toEditText;
    private EditText contentEditText ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNetwork();
        setContentView(R.layout.activity_upload);
        coverSD = findViewById(R.id.sd_cover);
        toEditText = findViewById(R.id.et_to);
        contentEditText = findViewById(R.id.et_content);
        findViewById(R.id.btn_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "选择图片");
            }
        });


        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
//                submitMessageWithURLConnection();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                coverSD.setImageURI(coverImageUri);

                if (coverImageUri != null) {
                    Log.d(TAG, "pick cover image " + coverImageUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }
    }

    private void initNetwork() {
        //TODO 3
        // 创建Retrofit实例
        // 生成api对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(IApi.class);
    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    private void submit() {
        byte[] coverImageData = readDataFromUri(coverImageUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String to = toEditText.getText().toString();
        if (TextUtils.isEmpty(to)) {
            Toast.makeText(this, "请输入TA的名字", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入想要对TA说的话", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( coverImageData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("Submit", "ready to submit");
        //TODO 5
        // 使用api.submitMessage()方法提交留言
        // 如果提交成功则关闭activity，否则弹出toast
        try {
            MultipartBody.Part fromPart = MultipartBody.Part.createFormData("from", Constants.USER_NAME);
            MultipartBody.Part toPart = MultipartBody.Part.createFormData("to", to);
            MultipartBody.Part contentPart = MultipartBody.Part.createFormData("content", content);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", "cover.png",
                            RequestBody.create(MediaType.parse("multipart/from_data"), coverImageData));
            Call<UploadResponse> response = api.submitMessage(Constants.STUDENT_ID, "", fromPart, toPart, contentPart, imagePart, Constants.token);
            Log.d("Submit", response.toString());
            Toast.makeText(this, "开始提交", Toast.LENGTH_SHORT).show();
            response.enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    Log.d("Submit", "response" + response.toString());
                    if (!response.isSuccessful()) {
                        Toast.makeText(getApplication(), "提交失败", Toast.LENGTH_SHORT).show();
                        Log.d("Submit", "提交失败");
                        return;
                    } else {
                        Toast.makeText(getApplication(), "提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "wtf!!!", Toast.LENGTH_SHORT).show();
        }
    }


    // TODO 7 选做 用URLConnection的方式实现提交
    private void submitMessageWithURLConnection(){
        byte[] coverImageData = readDataFromUri(coverImageUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String to = toEditText.getText().toString();
        if (TextUtils.isEmpty(to)) {
            Toast.makeText(this, "请输入TA的名字", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入想要对TA说的话", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( coverImageData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("Submit", "ready to submit");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String boundary = java.util.UUID.randomUUID().toString();
                String urlStr = Constants.BASE_URL + "messages" + "?student_id=" + Constants.STUDENT_ID + "&extra_value=" + "";
                String twoHyphens = "--";
                String Charset = "UTF-8";
                String line_feed = "\r\n";

                Log.d("Submit URL", urlStr);
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setConnectTimeout(6000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "multipart/from-data; boundary=" + twoHyphens + boundary);
                    connection.setRequestProperty("token", Constants.token);



//                    connection.setRequestProperty("from", Constants.USER_NAME);
//                    connection.setRequestProperty("to", to);
//                    connection.setRequestProperty("content", content);
                    StringBuilder sb = new StringBuilder();

                    DataOutputStream os = new DataOutputStream(connection.getOutputStream());


                    sb.append(twoHyphens + boundary + line_feed);
                    sb.append("Content-Disposition: form-data; name=\"from\"" + line_feed);
//                    sb.append("Content-Type: multipart/form-data" + line_feed);
//                    sb.append("Content-Transfer-Encoding: 8bit" + line_feed);
                    sb.append(line_feed);
                    sb.append(Constants.USER_NAME);
                    sb.append(line_feed);

                    sb.append(twoHyphens + boundary + line_feed);
                    sb.append("Content-Disposition: form-data; name=\"to\"" + line_feed);
//                    sb.append("Content-Type: multipart/form-data" + line_feed);
//                    sb.append("Content-Transfer-Encoding: 8bit" + line_feed);
                    sb.append(line_feed);
                    sb.append(to);
                    sb.append(line_feed);

                    sb.append(twoHyphens + boundary + line_feed);
                    sb.append("Content-Disposition: form-data; name=\"content\"" + line_feed);
//                    sb.append("Content-Type: multipart/form-data" + line_feed);
//                    sb.append("Content-Transfer-Encoding: 8bit" + line_feed);
                    sb.append(line_feed);
                    sb.append(content);
                    sb.append(line_feed);

                    os.write(sb.toString().getBytes());
                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", "cover.png",
                            RequestBody.create(MediaType.parse("multipart/from_data"), coverImageData));

                    Log.d("Submit Image", imagePart.body().toString());
                    os.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"cover.jpg\"" + line_feed);
                    os.writeBytes("Content-Type: image/jpeg" + line_feed);
//                    os.writeBytes("Content-Transfer-Encoding: binary" + line_feed);
                    os.writeBytes(line_feed);
                    os.write(imagePart.body().toString().getBytes());
                    os.writeBytes(line_feed);
                    os.writeBytes(twoHyphens + boundary + twoHyphens + line_feed);

                    os.flush();

                    int res = connection.getResponseCode();
                    if (res == 200) {
                        InputStream is = connection.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is, "utf-8");
                        BufferedReader br = new BufferedReader(isr);
                        String result = br.readLine();
                        Log.d("Submit Response", result);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result.isEmpty()) {
                                    Toast.makeText(getApplication(), "提交失败", Toast.LENGTH_SHORT).show();
                                    Log.d("Submit", "提交失败");
                                    return;
                                } else {
                                    Toast.makeText(getApplication(), "提交成功", Toast.LENGTH_SHORT).show();
                                    Log.d("Submit", "提交成功");
                                    finish();
                                }
                            }
                        });
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplication(), "wtf!!!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


}
