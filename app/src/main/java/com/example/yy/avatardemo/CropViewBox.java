package com.example.yy.avatardemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Farble on 2015/3/10.
 */
public class CropViewBox extends View {
    private static final String TAG = "PhotoCropView";

    private int MODE;
    private static final int MODE_OUTSIDE = 0x000000aa;/*170*/
    private static final int MODE_INSIDE = 0x000000bb;/*187*/
    private static final int MODE_POINT = 0X000000cc;/*204*/
    private static final int MODE_ILLEGAL = 0X000000dd;/*221*/

    private static final int minWidth = 100;/*the minimum width of the rectangle*/
    private static final int minHeight = 200;/*the minimum height of the rectangle*/

    private static final float EDGE_WIDTH = 1.8f;
    private static final int ACCURACY= 20;/*touch accuracy*/

    private int pointPosition;/*vertex of a rectangle*/

    private int sX = 0;/*start X location*/
    private int sY = 0;/*start Y location*/
    private int eX = 0;/*end X location*/
    private int eY = 0;/*end Y location*/

    private int pressX;/*X coordinate values while finger press*/
    private int pressY;/*Y coordinate values while finger press*/

    private int memonyX;/*the last time the coordinate values of X*/
    private int memonyY;/*the last time the coordinate values of Y*/

    private int coverWidth;/*width of selection box*/
    private int coverHeight;/*height of selection box  2*coverWidth */

    private Paint mPaint;
    private Paint mPaintLine;
    private Bitmap mBitmapRectBlack;
    private Paint mCirclePaint;
    private PorterDuffXfermode xfermode;/*paint mode*/
    private CropImageView mCropImageView;
    private boolean isPinchZoom = false;
    private double starteddistance = 1.0;
    private double scaleFactor = 1.0;/*scaleFactor*/
    double defaultScale = 1.0;
    private  float maxRatio=4.0f;
    private double lastScaleFactor = 1.0;//lastScaleFactor

    public int imageX = 0;  /*for background start X position*/
    public int imageY = 0;  /*for background start Y position*/
    private int width;      /*for screen width*/
    private int height;     /*for screen height*/

    public CropViewBox(Context context) {
        super(context);
        init();
    }

    public CropViewBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CropViewBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("deprecation")
    private void init() {

        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        width = manager.getDefaultDisplay().getWidth();
        height = manager.getDefaultDisplay().getHeight();
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        pressX = 0;
        pressY = 0;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaintLine = new Paint();
        mPaintLine.setColor(Color.parseColor("#FF0000"));
        mPaintLine.setStrokeWidth(4.0f);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.parseColor("#FF0000"));
    }

    /*生成bitmap*/
    private Bitmap makeBitmap(int mwidth, int mheight, int resource, int staX, int staY) {
        Bitmap bm = Bitmap.createBitmap(mwidth, mheight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(resource);
        c.drawRect(staX, staY, mwidth, mheight, p);
        return bm;
    }

    public Bitmap clip(){
        Bitmap bm = mCropImageView.clip(sX - imageX , sY - imageY, coverWidth, coverHeight);
        return bm;
    }

    public void setCropImageView(CropImageView cropView) {
        mCropImageView = cropView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) {
            return;
        }
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int imgWidth = mCropImageView.getWidth();
        int imgHeight = mCropImageView.getHeight();

        mPaint.setFilterBitmap(false);
        int sc = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null,
                Canvas.MATRIX_SAVE_FLAG |
                        Canvas.CLIP_SAVE_FLAG |
                        Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                        Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                        Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        mPaint.setXfermode(xfermode);
        mPaint.setXfermode(null);
        canvas.restoreToCount(sc);

        imageX = (int) ((canvasWidth - mCropImageView.getWidth()) / 2);
        imageY = (int) ((canvasHeight - mCropImageView.getHeight()) / 2);
        if(sX == 0 & sY == 0){
            coverWidth = imgWidth;
            coverHeight = imgHeight/2;
            sX = imageX;
            sY = imageY;
            eX = imageX + coverWidth;
            eY = imageY + coverHeight;
        }

        checkCropBoxBounds();
        canvas.drawLine((float) sX - EDGE_WIDTH, (float) sY - EDGE_WIDTH, (float) eX + EDGE_WIDTH, (float) sY - EDGE_WIDTH, mPaintLine);/*up -*/
        canvas.drawLine((float) sX - EDGE_WIDTH, (float) eY + EDGE_WIDTH, (float) eX + EDGE_WIDTH, (float) eY + EDGE_WIDTH, mPaintLine);/*down -*/
        canvas.drawLine((float) sX - EDGE_WIDTH, (float) sY - EDGE_WIDTH, (float) sX - EDGE_WIDTH, (float) eY + EDGE_WIDTH, mPaintLine);/*left |*/
        canvas.drawLine((float) eX + EDGE_WIDTH, (float) sY - EDGE_WIDTH, (float) eX + EDGE_WIDTH, (float) eY + EDGE_WIDTH, mPaintLine);/*righ |*/

        canvas.drawCircle((float)sX - EDGE_WIDTH, (float)sY- EDGE_WIDTH,15, mCirclePaint);
        canvas.drawCircle((float)eX + EDGE_WIDTH, (float)sY- EDGE_WIDTH,15, mCirclePaint);
        canvas.drawCircle((float)sX, (float)eY, 15, mCirclePaint);
        canvas.drawCircle((float)eX, (float)eY,15, mCirclePaint);


    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (event.getPointerCount() > 1)
            isPinchZoom = true;
        else
            isPinchZoom = false;

        if (!isPinchZoom) {
            //一个手指
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    memonyX = (int) event.getX();
                    memonyY = (int) event.getY();
                    checkMode(memonyX, memonyY);
                    break;
                case MotionEvent.ACTION_MOVE: {
                    switch (MODE) {
                        case MODE_ILLEGAL:
                            pressX = (int) event.getX();
                            pressY = (int) event.getY();
                            recoverFromIllegal(pressX, pressY);
                            postInvalidate();
                            break;
                        case MODE_OUTSIDE:
                            //do nothing;
                            break;
                        case MODE_INSIDE:
                            pressX = (int) event.getX();
                            pressY = (int) event.getY();
                            moveByTouch(pressX, pressY);
                            postInvalidate();
                            break;
                        default:
                        /*MODE_POINT*/
                            pressX = (int) event.getX();
                            pressY = (int) event.getY();
                            mPaintLine.setColor(Color.parseColor("#FBBA06"));
                            moveByPoint(pressX, pressY);
                            postInvalidate();
                            break;
                    }
                }
                break;
                case MotionEvent.ACTION_UP:
                    postInvalidate();
                    break;
                default:
                    break;
            }

        }else{
            //俩手指放大缩小
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    zoomCropBox(event);
                    postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_1_UP:
                case MotionEvent.ACTION_POINTER_2_UP: {
                    lastScaleFactor = scaleFactor;
                    defaultScale = scaleFactor;
                    break;
                }
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_1_DOWN:
                case MotionEvent.ACTION_POINTER_2_DOWN:
                    starteddistance = getDistanceBetweenToPoint(event);
                    break;

            }

        }
        return true;
    }

    private void zoomCropBox(MotionEvent event) {
        try {
            double currentDistance = getDistanceBetweenToPoint(event);
            scaleFactor = lastScaleFactor * (currentDistance / starteddistance);

            //刷新sX,sY,eX,eY,coverWidth, coverHeight
//            int width = eX - sX;   //width of box
//            int height = eY - sY;  /// height of box
//
//            int imgWidth = mCropImageView.getWidth();
//            int imgHeight = mCropImageView.getHeight();
//            if (((int) (width * scaleFactor) < imgWidth)
//                    && (((int) (height * scaleFactor)) < imgHeight)) {
//                int	rectWidth = (int) (width * scaleFactor);
//                int	rectHeight = (int) (height * scaleFactor);
//                float minRectValue=rectWidth<rectHeight?rectWidth:rectHeight;
//                int minImgValue=imgWidth<imgHeight?imgWidth:imgHeight;
//                if (minRectValue * maxRatio < minImgValue ) {
//                    scaleFactor= minImgValue / maxRatio *scaleFactor / minRectValue;
//                }
//                coverWidth = (int) (width * scaleFactor);
//                coverHeight = coverWidth/2;
//                defaultScale = scaleFactor;
//            }else{
//                scaleFactor = defaultScale;
//                lastScaleFactor = defaultScale;
//            }
//
//            sX = sX + (width - coverWidth )/2;
//            sY = sY + (height - coverHeight )/2;
//            eX = eX - (width - coverWidth)/2;
//            eY = eY - (height - coverHeight)/2;


            int width = eX - sX;   //width of box
            int height = eY - sY;  /// height of box

            int imgWidth = mCropImageView.getWidth();
            if ((int) (width * scaleFactor) < imgWidth) {
                int	rectWidth = (int) (width * scaleFactor);

                if (rectWidth * maxRatio < imgWidth ) {
                    scaleFactor= imgWidth / maxRatio *scaleFactor / rectWidth;
                }
                coverWidth = (int) (width * scaleFactor);
                coverHeight = coverWidth/2;
                defaultScale = scaleFactor;
            }else{
                scaleFactor = defaultScale;
                lastScaleFactor = defaultScale;
            }
            starteddistance = currentDistance;

            sX = sX + (width - coverWidth )/2;
            sY = sY + (height - coverHeight )/2;
            eX = eX - (width - coverWidth)/2;
            eY = eY - (height - coverHeight)/2;

//            int imgWidth=img.getWidth();
//            int imgHeight=img.getHeight();
//            if (((int) (width * scaleFactor) < imgWidth)
//                    && (((int) (height * scaleFactor)) < imgHeight)) {
//                int	rectWidth = (int) (width * scaleFactor);
//                int	rectHeight = (int) (height * scaleFactor);
//                float minRectValue=rectWidth<rectHeight?rectWidth:rectHeight;
//                int minImgValue=imgWidth<imgHeight?imgWidth:imgHeight;
//                if (minRectValue * maxRatio < minImgValue ) {
//                    scaleFactor= minImgValue / maxRatio *scaleFactor / minRectValue;
//                }
//                newWidth = (int) (width * scaleFactor);
//                newHeight = (int) (height * scaleFactor);
//                defaultScale = scaleFactor;
//            } else {
//                scaleFactor = defaultScale;
//                lastScaleFactor = defaultScale;
//            }
//
//            isZoomCropBoxUsed = true;
//
//            rect.left = rect.left + (rect.width() - newWidth) / 2;
//            rect.top = rect.top + (rect.height() - newHeight) / 2;
//            rect.right = rect.right - (rect.width() - newWidth) / 2;
//            rect.bottom = rect.bottom - (rect.height() - newHeight) / 2;

//            //刷新sX,sY,eX,eY,coverWidth, coverHeight
//            int width = eX - sX;   //width of box
//            int height = eY - sY;  /// height of box
//            int newCoverWidth = width;  //new width of box
//            int newCoverHeight = height; //new height of box
//            int imgWidth = mCropImageView.getWidth();
//            int imgHeight = mCropImageView.getHeight();
//            if (((int) (width * scaleFactor) < imgWidth)
//                    && (((int) (height * scaleFactor)) < imgHeight)) {
//                int	rectWidth = (int) (width * scaleFactor);
//                int	rectHeight = (int) (height * scaleFactor);
//                float minRectValue=rectWidth<rectHeight?rectWidth:rectHeight;
//                int minImgValue=imgWidth<imgHeight?imgWidth:imgHeight;
//                if (minRectValue * maxRatio < minImgValue ) {
//                    scaleFactor= minImgValue / maxRatio *scaleFactor / minRectValue;
//                }
//                newCoverWidth = (int) (width * scaleFactor);
//                newCoverHeight = (int) (height * scaleFactor);
//                defaultScale = scaleFactor;
//            }else{
//                scaleFactor = defaultScale;
//                lastScaleFactor = defaultScale;
//            }
//
//            sX = sX + (width - newCoverWidth )/2;
//            sY = sY + (height - newCoverHeight )/2;
//            eX = eX - (width - newCoverWidth)/2;
//            eY = eY - (height - newCoverHeight)/2;

            checkCropBoxBounds();
        } catch (Exception ex) {
            Log.e(TAG, "!!!!!!!!" + ex.getMessage());
        }
    }

    private double getDistanceBetweenToPoint(MotionEvent event) {
        double x = event.getX(0) - event.getX(1);
        double y = event.getY(0) - event.getY(1);
        return Math.sqrt(x * x + y * y);
    }


    /*从非法状态恢复，这里处理的是达到最小值后能拉伸放大*/
    private void recoverFromIllegal(int rx, int ry) {
        if ((rx > sX && ry > sY) && (rx < eX && ry < eY)) {
            MODE = MODE_ILLEGAL;
        } else {
            MODE = MODE_POINT;
        }
    }

    private void checkMode(int cx, int cy) {
        if (cx > sX && cx < eX && cy > sY && cy < eY) {
            MODE = MODE_INSIDE;
        } else if (nearbyPoint(cx, cy) < 4) {
            MODE = MODE_POINT;
        } else {
            MODE = MODE_OUTSIDE;
        }
    }

    /*判断点(inX,inY)是否靠近矩形的4个顶点*/
    private int nearbyPoint(int inX, int inY) {
        if ((Math.abs(sX - inX) <= ACCURACY && (Math.abs(inY - sY) <= ACCURACY))) {/*left-up angle*/
            pointPosition = 0;
            return 0;
        }
        if ((Math.abs(eX - inX) <= ACCURACY && (Math.abs(inY - sY) <= ACCURACY))) {/*right-up  angle*/
            pointPosition = 1;
            return 1;
        }
        if ((Math.abs(sX - inX) <= ACCURACY && (Math.abs(inY - eY) <= ACCURACY))) {/*left-down angle*/
            pointPosition = 2;
            return 2;
        }
        if ((Math.abs(eX - inX) <= ACCURACY && (Math.abs(inY - eY) <= ACCURACY))) {/*right-down angle*/
            pointPosition = 3;
            return 3;
        }
        pointPosition = 100;
        return 100;
    }

    /*刷新矩形的坐标*/
    private void refreshLocation(int isx, int isy, int iex, int iey) {
        this.sX = isx;
        this.sY = isy;
        this.eX = iex;
        this.eY = iey;
    }

    /*矩形随手指移动*/
    private void moveByTouch(int mx, int my) {/*move center point*/
        int dX = mx - memonyX;
        int dY = my - memonyY;

        sX += dX;
        sY += dY;

        eX = sX + coverWidth;
        eY = sY + coverHeight;

        memonyX = mx;
        memonyY = my;
        checkCropBoxBounds();

    }

    /**
     * Description: invoke to stop moving cropBox out of the bounds to fix frame
     * Add by: Kaly At Jan 21, 2015 10:24:56 AM
     */
    public void checkCropBoxBounds() {
        int minWith = (int) (coverWidth<mCropImageView.getWidth()?coverWidth:mCropImageView.getWidth());
        int minHeight=(int) (coverHeight <mCropImageView.getHeight()? coverHeight :mCropImageView.getHeight());

        if (sX < imageX) {
            sX = imageX;
            eX = sX + minWith;
        }else if (eX > (imageX + mCropImageView.getWidth())) {
            eX = (imageX + mCropImageView.getWidth());
            sX = eX - minWith;
        }
        if (sY < imageY) {
            sY = imageY;
            eY =  sY + minHeight;
        }else if (eY > (imageY + mCropImageView.getHeight())) {
            eY = imageY + mCropImageView.getHeight() ;
            sY = eY - minHeight;
        }

    }

    /*检测矩形是否达到最小值*/
    private boolean checkLegalRect(int cHeight, int cWidth) {
        return (cHeight > minHeight && cWidth > minWidth);
    }

    /*点击顶点附近时的缩放处理*/
    @SuppressWarnings("SuspiciousNameCombination")
    private void moveByPoint(int bx, int by) {
        switch (pointPosition) {
            case 0:/*left-up*/    //靠左 底不变
                sX = imageX;
                coverWidth = eX - sX;
                coverHeight = coverWidth/2;
                sY = eY - coverHeight;

                //noinspection SuspiciousNameCombination
                if (!checkLegalRect(coverWidth, coverHeight)) {
                    MODE = MODE_ILLEGAL;
                } else {
                    refreshLocation(sX, sY, eX, eY);
                }
                break;
            case 1:/*right-up*/

                eX = imageX + mCropImageView.getWidth();
                coverWidth = eX - sX;
                coverHeight = coverWidth/2;
                sY = eY - coverHeight;

                if (!checkLegalRect(coverWidth, coverHeight)) {
                    MODE = MODE_ILLEGAL;
                } else {
                    refreshLocation(sX, sY, eX, eY);
                }
                break;
            case 2:/*left-down*/
                int rellX = bx - eX;
                int rellY = by - eY;
                if(bx <= imageX || bx >= imageX + mCropImageView.getWidth() || by <= imageY || by >= imageY + mCropImageView.getHeight()){
                    break;
                }

                if (rellX != 0) {
                    if (Math.abs(rellY/rellX -0.5) > 0) {
                        //Y
                        eY = by;
                        coverHeight = eY - sY;
                        coverWidth = coverHeight*2;
                        sX = eX - coverWidth;
                    }else{
                        //X
                        sX = bx;
                        coverWidth = eX - sX;
                        coverHeight = coverWidth/2;
                        eY = sY + coverHeight;

                    }
                } else {
                    sX = bx;
                    coverWidth = eX - sX;
                    coverHeight = coverWidth/2;
                    eY = sY + coverHeight;
                }

                if (coverWidth >= mCropImageView.getWidth()) {
                    coverWidth = mCropImageView.getWidth();
                    coverHeight = coverWidth/2;
                    sY = eY - coverHeight;
                }
                if (!checkLegalRect(coverWidth, coverHeight)) {
                    MODE = MODE_ILLEGAL;
                } else {
                    refreshLocation(sX, sY, eX, eY);
                }
                break;
            case 3:/*right-down*/

                int relX = bx - eX;
                int relY = by - eY;
                if(bx <= imageX || bx >= imageX + mCropImageView.getWidth() || by <= imageY || by >= imageY + mCropImageView.getHeight()){
                    break;
                }
                if (relX != 0) {
                    if (Math.abs(relY/relX -0.5) > 0) {
                        //Y
                        eY = by;
                        coverHeight = eY - sY;
                        coverWidth = coverHeight*2;
                        eX = sX + coverWidth;
                    }else{
                        //X
                        eX = bx;
                        coverWidth = eX - sX;
                        coverHeight = coverWidth/2;
                        eY = sY + coverHeight;

                    }
                } else {
                    eX = bx;
                    coverWidth = eX - sX;
                    coverHeight = coverWidth/2;
                    eY = sY + coverHeight;
                }

                if (coverWidth >= mCropImageView.getWidth()) {
                    coverWidth = mCropImageView.getWidth();
                    coverHeight = coverWidth/2;
                    eY = sY + coverHeight;
                }


                if (!checkLegalRect(coverWidth, coverHeight)) {
                    MODE = MODE_ILLEGAL;
                } else {
                    refreshLocation(sX, sY, eX, eY);
                }
                break;
            default:
                break;
        }
    }

}