package com.example.yy.avatardemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by YY on 2017/2/26.
 */
public class MCropActivity extends Activity{

    private ImageView mNewImage;
    private CropViewBox mBorder;
    private Button mDone;
    private CropImageView mImage;

    //test004

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_crop);
        initData();
        initEvent();
    }

    private void initData() {
//        ImageView img = (ImageView) findViewById(R.id.img);
        mBorder = (CropViewBox) findViewById(R.id.border);
        mNewImage = (ImageView) findViewById(R.id.new_img);
        mImage = (CropImageView) findViewById(R.id.img);
        mDone = (Button) findViewById(R.id.done);
        mBorder.setCropImageView(mImage);
    }

    private void initEvent() {
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bm = mBorder.clip();
                mNewImage.setImageBitmap(bm);
                mBorder.setVisibility(View.GONE);
                mImage.setVisibility(View.GONE);
            }
        });
    }
}
