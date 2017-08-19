package com.example.administrator.wechat.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.administrator.wechat.R;
import com.example.administrator.wechat.model.ImagePath;
import com.example.administrator.wechat.widget.LoadingView;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;
import com.hyphenate.util.DensityUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/8/13.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatAdapterViewHolder> {


    public static final int THRESHOLD = 60000;
    private List<EMMessage> mEMMessageList;

    public ChatAdapter(List<EMMessage> EMMessageList) {
        mEMMessageList = EMMessageList;
    }

    @Override
    public int getItemCount() {
        return mEMMessageList == null ? 0 : mEMMessageList.size();
    }

    @Override
    public ChatAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case SEND_TXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_send_text, parent, false);
                break;
            case RECEIVE_TXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_receive_text, parent, false);
                break;
            case SEND_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_send_image, parent, false);
                break;
            case RECEIVE_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_receive_image, parent, false);
                break;
        }

        ChatAdapterViewHolder chatAdapterViewHolder = new ChatAdapterViewHolder(view);
        return chatAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(final ChatAdapterViewHolder holder, int position) {
        final EMMessage emMessage = mEMMessageList.get(position);
        //获取消息的时间
        long msgTime = emMessage.getMsgTime();
        //将时间转换成字符串形式
        String msgTimeStr = DateUtils.getTimestampString(new Date(msgTime));
        //处理时间
        processTime(holder, position, msgTime, msgTimeStr);

        //处理消息
        EMMessageBody messageBody = emMessage.getBody();
        if (emMessage.getType() == EMMessage.Type.TXT) {
            //将messageBody强转为textMessageBody
            EMTextMessageBody textMessageBody = (EMTextMessageBody) messageBody;
            //获取文本消息的文本内容
            String message = textMessageBody.getMessage();
            holder.mTvMsg.setText(message);
        } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
            //处理图片消息
            /**
             * 如果是收到的图片,则显示本地的缩略图
             * 如果是发送的图片,则显示本地的原图
             */
            EMImageMessageBody imageMessageBody = (EMImageMessageBody) emMessage.getBody();
            if (emMessage.direct() == EMMessage.Direct.RECEIVE) {//接受的图片
                //下载到本地的缩略图的地址
                String thumbnailLocalPath = imageMessageBody.thumbnailLocalPath();
                loadImageWithGlide(thumbnailLocalPath, holder.mIv, null);
            } else {//发送的图片
                //此处thumbnailLocalPath为要发送的本地图片地址
                String thumbnailLocalPath = imageMessageBody.thumbnailLocalPath();
                //让loadingview的宽高和imageview的宽高一致
                loadImageWithGlide(thumbnailLocalPath, holder.mIv, holder.mLoadingview);
                //处理图片发送的状态
                switch (emMessage.status()) {
                    case INPROGRESS:
                    case CREATE:
                        holder.mLoadingview.setError(false);
                        holder.mLoadingview.setVisibility(View.VISIBLE);
                        break;
                    case SUCCESS:
                        holder.mLoadingview.setVisibility(View.GONE);
                        break;
                    case FAIL:
                        holder.mLoadingview.setError(true);
                        break;
                }

                //实时监听图片发送的状态
                emMessage.setMessageStatusCallback(new MyEMCallback() {
                    @Override
                    public void onMainSuccess() {
                        holder.mLoadingview.setVisibility(View.GONE);
                    }

                    @Override
                    public void onMainError(int code, String message) {
                        holder.mLoadingview.setError(true);
                    }

                    @Override
                    public void onProgress(int progress, String s) {
                        super.onProgress(progress, s);
                        holder.mLoadingview.setError(false);
                        holder.mLoadingview.setProgress(progress);

                    }
                });
            }

            //给当前条目设置点击监听器
            holder.mIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnImageListener != null) {
                        //将聊天中的所有图片获取到并放到集合中
                        //获取当前点击的图片位于图片集合中的位置
                        ArrayList<ImagePath> imagePathList = getImagePathList();

                        int imagePosition = 0;

                        EMImageMessageBody imageBody = (EMImageMessageBody) emMessage.getBody();
                        for (int i = 0; i < imagePathList.size(); i++) {
                            ImagePath imagePath = imagePathList.get(i);
                            if (imageBody.thumbnailLocalPath().equals(imagePath.localPath)) {
                                imagePosition = i;
                                break;
                            }

                        }
                        mOnImageListener.onImageClick(imagePathList, imagePosition);
                    }
                }


            });

        }

        //处理消息的状态(发送出去的消息特有的状态)
        if (emMessage.direct() == EMMessage.Direct.SEND) {
            if (emMessage.getType() == EMMessage.Type.TXT) {
                //根据消息的状态,去修改imageview的状态
                //获取当前消息的状态
                EMMessage.Status status = emMessage.status();
                switch (status) {
                    case CREATE:
                    case INPROGRESS:
                        holder.mIvStatus.setVisibility(View.VISIBLE);
                        //将帧动画资源设置给ImageViewStatus
                        holder.mIvStatus.setImageResource(R.drawable.sending_frame_anim);
                        //播放帧动画
                        AnimationDrawable animationDrawable = (AnimationDrawable) holder.mIvStatus.getDrawable();
                        if (animationDrawable.isRunning()) {
                            animationDrawable.stop();
                        }
                        animationDrawable.start();
                        break;
                    case SUCCESS:
                        //去掉状态图片GONE
                        holder.mIvStatus.setVisibility(View.GONE);
                        break;
                    case FAIL:
                        //加载失败,将图片改为感叹号
                        holder.mIvStatus.setVisibility(View.VISIBLE);
                        holder.mIvStatus.setImageResource(R.mipmap.msg_error);
                        break;
                }
            }
        }


    }

    @NonNull
    private ArrayList<ImagePath> getImagePathList() {
        ArrayList<ImagePath> imagePathList = new ArrayList<ImagePath>();

        for (int i = 0; i < mEMMessageList.size(); i++) {
            EMMessage currentMessage = mEMMessageList.get(i);
            if (currentMessage.getType() == EMMessage.Type.IMAGE) {
                ImagePath imagePath = new ImagePath();
                EMImageMessageBody imageBody = (EMImageMessageBody) currentMessage.getBody();
                imagePath.localPath = imageBody.thumbnailLocalPath();
                if (currentMessage.direct() == EMMessage.Direct.RECEIVE) {//只有接收到的图片才有远程地址
                    imagePath.remotePath = imageBody.getRemoteUrl();
                } else {//如果是发送出去的图片
                    imagePath.localPath = imageBody.thumbnailLocalPath();
                }

                imagePathList.add(imagePath);
            }
        }
        return imagePathList;
    }

    private void loadImageWithGlide(String thumbnailLocalPath, final ImageView iv, final LoadingView loadingview) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        requestOptions.error(R.mipmap.msg_error);

        Glide.with(iv).load(thumbnailLocalPath).apply(requestOptions).listener(new RequestListener<Drawable>() {
            //加载失败
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;//返回fasle,就是让Glide自己去处理,即会去加载错误图片(前提是设置了错误图片,否则不显示任何内容)
            }

            //加载完成
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                int intrinsicHeight = bitmapDrawable.getIntrinsicHeight();
                int intrinsicWidth = bitmapDrawable.getIntrinsicWidth();

                //获取bitmap
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ViewGroup.LayoutParams layoutParams = iv.getLayoutParams();
                int width;
                int height;
                if (intrinsicHeight > intrinsicWidth) {//长图
                    //宽120dp  高 200dp
                    width = DensityUtil.dip2px(iv.getContext(), 120);
                    height = DensityUtil.dip2px(iv.getContext(), 200);
                } else {//宽图
                    //宽200dp 高120dp
                    width = DensityUtil.dip2px(iv.getContext(), 200);
                    height = DensityUtil.dip2px(iv.getContext(), 120);
                }

                layoutParams.height = height;
                layoutParams.width = width;

                iv.setLayoutParams(layoutParams);
                if (loadingview != null) {

                    loadingview.setLayoutParams(layoutParams);
                }

//                iv.setImageBitmap(bitmap);

                //返回false：glide依然会将加载好的图片显示到ImageView上
                //返回true：glide不会将加载好的图片显示到ImageView上，需要自动手动的将图片加载到ImageView上
                return false;
            }
        }).into(iv);
    }

    private void processTime(ChatAdapterViewHolder holder, int position, long msgTime, String msgTimeStr) {
        if (position == 0) {//默认第一条显示
            holder.mTvTime.setVisibility(View.VISIBLE);
            holder.mTvTime.setText(msgTimeStr);
        } else {
            //需要根据当前消息的事件跟上一个消息的事件对比
            EMMessage preMessage = mEMMessageList.get(position - 1);
            long preMsgTime = preMessage.getMsgTime();
            if (msgTime - preMsgTime < THRESHOLD) {
                holder.mTvTime.setVisibility(View.GONE);
            } else {
                holder.mTvTime.setVisibility(View.VISIBLE);
                holder.mTvTime.setText(msgTimeStr);
            }
        }
    }

    public static final int SEND_TXT = 1;
    public static final int RECEIVE_TXT = 2;
    public static final int SEND_IMAGE = 3;
    public static final int RECEIVE_IMAGE = 4;

    @Override
    public int getItemViewType(int position) {
        //根据position获取到对应的数据
        EMMessage emMessage = mEMMessageList.get(position);
        EMMessage.Type type = emMessage.getType();
        //进一步区分是发送的还是接收的
        EMMessage.Direct direct = emMessage.direct();
        switch (type) {
            case TXT:
                if (direct == EMMessage.Direct.SEND) {
                    return SEND_TXT;
                } else {
                    return RECEIVE_TXT;
                }

            case IMAGE:
                if (direct == EMMessage.Direct.SEND) {
                    return SEND_IMAGE;
                } else {
                    return RECEIVE_IMAGE;
                }

            default:
                break;

        }

        return 0;
    }

    class ChatAdapterViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTvTime;
        private final TextView mTvMsg;
        private final ImageView mIvStatus;
        private final ImageView mIv;
        private final LoadingView mLoadingview;

        public ChatAdapterViewHolder(View itemView) {
            super(itemView);
            /**
             * 如果有多个不同的子条目布局，则需要把他们各自的子控件的并集找出来
             */
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvMsg = (TextView) itemView.findViewById(R.id.tv_msg);
            mIvStatus = (ImageView) itemView.findViewById(R.id.iv_status);
            mIv = (ImageView) itemView.findViewById(R.id.iv);
            mLoadingview = (LoadingView) itemView.findViewById(R.id.loadingview);
        }
    }

    //定义图片点击监听
    public interface OnImageListener {
        void onImageClick(ArrayList<ImagePath> imagePathList, int imagePosition);
    }

    private OnImageListener mOnImageListener;

    public void setOnImageListener(OnImageListener onImageListener) {
        mOnImageListener = onImageListener;
    }
}
