package com.example.administrator.wechat.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.administrator.wechat.R;
import com.hyphenate.util.DensityUtil;

/**
 * Created by Administrator on 2017/8/16.
 */

public class LoadingView extends View {
    private int mProgress = 0;
    private int max = 100;
    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private boolean mError;
    private Bitmap mErrorBitmap;
    private Bitmap mNewBitmap;

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(DensityUtil.sp2px(context,22));

        mErrorBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.msg_error);
        mNewBitmap = Bitmap.createBitmap(mErrorBitmap.getWidth() * 2, mErrorBitmap.getHeight() * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mNewBitmap);
        Matrix matrix = new Matrix();
        float[] values = {
                2,0,0,
                0,2,0,
                0,0,1
        };
        matrix.setValues(values);

        canvas.drawBitmap(mErrorBitmap,matrix,null);
    }



    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mError) {
            //绘制一张错误图片

            canvas.drawBitmap(mNewBitmap,mWidth/2,mHeight/2,null);

            return;
        }
        String progressText= mProgress+"%";
        canvas.drawText(progressText,mWidth/2,mHeight/2,mPaint);//TODO
        //先裁剪出要绘制阴影的区域,在绘制颜色
        float left = 0;
        float top = ((mProgress+0.f)/max)*mHeight;
        float right = mWidth;
        float bottom = mHeight;
        canvas.clipRect(left,top,right,bottom);
        canvas.drawColor(Color.parseColor("#55000000"));
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        //当修改进度值的时候重绘界面
        postInvalidate();//可以在子线程被调用
    }

    public void setError(boolean error) {
        mError = error;
        postInvalidate();
    }
}
