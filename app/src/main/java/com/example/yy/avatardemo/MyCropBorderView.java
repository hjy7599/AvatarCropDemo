package com.example.yy.avatardemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class MyCropBorderView extends View
{
	private int mHorizontalPadding;
	private int mBorderWidth = 2;

	private Paint mPaint;

	public MyCropBorderView(Context context)
	{
		this(context, null);
	}

	public MyCropBorderView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public MyCropBorderView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		mBorderWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources()
						.getDisplayMetrics());
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		mPaint.setColor(Color.parseColor("#FBBA06"));
		mPaint.setStrokeWidth(mBorderWidth);
		mPaint.setStyle(Style.STROKE);
		canvas.drawCircle( getWidth()/2, getHeight()/2, getWidth()/2-mHorizontalPadding, mPaint);

	}

	public void setHorizontalPadding(int mHorizontalPadding)
	{
		this.mHorizontalPadding = mHorizontalPadding;

	}

}
