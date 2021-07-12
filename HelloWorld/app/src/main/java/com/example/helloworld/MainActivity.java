package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView test2 = findViewById(R.id.vi_1);

        Button test1 = findViewById(R.id.btn_1);
        test1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (test2.getText().toString() == "ads yds yyds!")
                    test2.setText("I know you failed Ads!");
                test2.setText("ads yds yyds!");
            }
        });
    }
}
