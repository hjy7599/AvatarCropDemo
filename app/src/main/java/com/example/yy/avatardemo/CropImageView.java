package com.example.yy.avatardemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * Created by YY on 2017/2/26.
 */

public class CropImageView extends ImageView {

    /**
     * 水平方向与View的边距
     */
    private int mHorizontalPadding = 30;//——屏幕边缘离截图区的宽度
    /**
     * 垂直方向与View的边距
     */
    private int mVerticalPadding = 200;//——屏幕顶部离截图区的高度


    public CropImageView(Context context) {
        super(context);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Bitmap clip(int sX, int sY, int width, int height)
    {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        if (sX+ width +  mHorizontalPadding >= bitmap.getWidth()) {
            width = bitmap.getWidth() - sX - mHorizontalPadding;
        }
        if (sY + height +  mVerticalPadding >= bitmap.getHeight()) {
            height = bitmap.getHeight() - sY - mVerticalPadding;
        }
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, sX + mHorizontalPadding, sY + mVerticalPadding, width, height);
        return bitmap1;
    }




}
