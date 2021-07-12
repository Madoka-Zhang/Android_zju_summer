package com.example.hw1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch sw1 = findViewById(R.id.switch1);
        final ImageView iv1 = findViewById(R.id.image_1);
        Button btn1 = findViewById(R.id.btn_1);
        Button btn2 = findViewById(R.id.btn_2);
        final EditText et1 = findViewById(R.id.et_1);
        final TextView tv1 = findViewById(R.id.tv_1);

        iv1.setVisibility(View.INVISIBLE);
        Log.d("MainActivity", "initial: no picture, helloworld");

        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    iv1.setVisibility(View.VISIBLE);
                    Log.d("MainActivity", "having picture");
                }
                else
                {
                    iv1.setVisibility(View.INVISIBLE);
                    Log.d("MainActivity", "no picture");
                }
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pic = (pic + 1)%3;
                switch (pic)
                {
                    case 0:
                    {
                        iv1.setImageResource(R.mipmap.pic1);
                        Log.d("MainActivity", "picture change to 1");
                        break;
                    }
                    case 1:
                    {
                        iv1.setImageResource(R.mipmap.pic2);
                        Log.d("MainActivity", "picture change to 2");
                        break;
                    }
                    case 2:
                    {
                        iv1.setImageResource(R.mipmap.pic3);
                        Log.d("MainActivity", "picture change to 3");
                        break;
                    }
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = et1.getText().toString();
                tv1.setText(text.toCharArray(), 0, text.length());
                Log.d("MainActivity", "change text to "+text);
            }
        });
    }

    private String text;
    private int pic = 0;
}
