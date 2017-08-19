package com.example.administrator.wechat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.wechat.R;
import com.example.administrator.wechat.adapter.IContactAdapter;
import com.example.administrator.wechat.utils.StringUtil;
import com.hyphenate.util.DensityUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public class SlideBar extends View {

    public static final int TEXT_SIZE = 12;
    private static String[] SECTIONS = {"搜","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    private int mWidth;
    private int mHeight;
    private float mAvgHeight;
    private Paint mPaint;
    private TextView mFloatView;
    private RecyclerView mRecyclerview;

    public SlideBar(Context context) {
        this(context,null);
    }

    public SlideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //抗锯齿
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);//设置文字对其方式
        mPaint.setTextSize(DensityUtil.sp2px(context,TEXT_SIZE));
        mPaint.setColor(0xff9c9c9c);
    }

    public SlideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public SlideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mAvgHeight = (mHeight+0.f)/SECTIONS.length;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawColor(Color.RED);
        for (int i = 0; i < SECTIONS.length; i++) {
            String section = SECTIONS[i];
            float x = mWidth / 2;
            float y = mAvgHeight*(i+0.6f);
            canvas.drawText(section,x,y,mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                /**
                 * 1,获取down到的文本,然后显示到floattextview上
                 * 2,如果点中的文字,正好和好友的名字匹配了,则滚动recycleview
                 * 3,按下时给slidebar设置背景
                 */

                setBackgroundResource(R.drawable.slide_bar_bg);
                float y = event.getY();
                showFloatViewAndScrollRecyclerView(y);
                break;
            case MotionEvent.ACTION_UP:
                /**
                 * 1,手指抬起是,隐藏floattextview
                 * 2,取消背景,把背景色设置为全透明
                 */
                setBackgroundColor(Color.TRANSPARENT);
                mFloatView.setVisibility(GONE);
                break;
        }
        return true;
    }

    private void showFloatViewAndScrollRecyclerView(float currentY) {
        //计算点击到的文本
        int position = (int)(currentY/mAvgHeight);
        if(position<0){
            position = 0;
        }else if(position>SECTIONS.length-1){
            position = SECTIONS.length-1;
        }

        String section = SECTIONS[position];
        if (mFloatView==null) {

            ViewGroup parent = (ViewGroup) getParent();
            mFloatView = (TextView) parent.findViewById(R.id.tv_float);
            mRecyclerview = (RecyclerView) parent.findViewById(R.id.recyclerview);
        }

        mFloatView.setText(section);
        mFloatView.setVisibility(VISIBLE);

        //滚动recyclerview
        IContactAdapter adapter = (IContactAdapter) mRecyclerview.getAdapter();
        List<String> contactList = adapter.getItems();
        int scroll2position = 0;
        for(int i = 0;i<contactList.size();i++){
            String contact = contactList.get(i);
            if(section.equalsIgnoreCase(StringUtil.getInitial(contact))){
                scroll2position = i;
                break;
            }
        }

//        mRecyclerview.scrollToPosition(scroll2position);
       LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerview.getLayoutManager();
        layoutManager.scrollToPositionWithOffset(scroll2position,0);

    }
}
