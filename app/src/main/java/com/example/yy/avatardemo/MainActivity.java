package com.example.yy.avatardemo;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

//my test 001
//test 003
//test 005
//test 006 branch
//test 007 local master
//test 009 loacl master
//test 010 remote

public class MainActivity extends AppCompatActivity {

    private ImageView mMImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initEvent();
    }

    private void initEvent() {
        Button next = (Button) findViewById(R.id.next);
        mMImg = (ImageView) findViewById(R.id.main_img);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this,MCropActivity.class),1);
            }
        });

        Dialog dialog = new Dialog(MainActivity.this);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("huang","thread");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("huang","handler");
            }
        },4000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
