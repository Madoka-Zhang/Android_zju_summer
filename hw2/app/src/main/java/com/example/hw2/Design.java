package com.example.hw2;

import com.example.hw2.MPager.MpAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class Design extends AppCompatActivity {
    private ViewPager mPicView;
    private int [] mImage = new int [3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_design);
        initView();
        MpAdapter mpadapter = new MpAdapter(mImage);
        mPicView.setAdapter(mpadapter);

        Button btn_1 = findViewById(R.id.btn_9);
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Log.d("Design", "return to MainActivity");
            }
        });
    }

    private void initView() {
        mPicView = findViewById(R.id.vp);
        mImage[0] = R.drawable.pic1;
        mImage[1] = R.drawable.pic2;
        mImage[2] = R.drawable.pic3;
    }
}
