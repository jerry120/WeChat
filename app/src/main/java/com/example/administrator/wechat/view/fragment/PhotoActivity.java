package com.example.administrator.wechat.view.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.wechat.R;
import com.example.administrator.wechat.model.ImagePath;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {

    private ViewPager mViewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        mViewpager = (ViewPager) findViewById(R.id.viewpager);
        Intent intent = getIntent();
        int imagePosition = intent.getIntExtra("imagePosition", 0);
        final ArrayList<ImagePath> imagePathList = intent.getParcelableArrayListExtra("imagePathList");
        if(imagePathList==null||imagePathList.size()==0){
            //没有找到要显示的图片
            finish();
            return;
        }

        mViewpager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return imagePathList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImagePath imagePath = imagePathList.get(position);

                PhotoView photoView = new PhotoView(PhotoActivity.this);

                if (imagePath.localPath.equals(imagePath.remotePath)) {//本地图片
                    Glide.with(PhotoActivity.this).load(imagePath.localPath).into(photoView);
                }else{//服务器上的图片
                    RequestOptions requestOptions = new RequestOptions();
                    Drawable drawable = BitmapDrawable.createFromPath(imagePath.localPath);
                    requestOptions.placeholder(drawable);
                    Glide.with(PhotoActivity.this).load(imagePath.remotePath).apply(requestOptions).into(photoView);
                }

                container.addView(photoView);

                return photoView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });

        //定位到当前可见的页
        mViewpager.setCurrentItem(imagePosition);
    }
}
