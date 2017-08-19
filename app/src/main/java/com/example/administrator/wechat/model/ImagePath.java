package com.example.administrator.wechat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/8/16.
 */

public class ImagePath implements Parcelable {
    public String localPath;
    public String remotePath;

    @Override
    public String toString() {
        return "ImagePath{" +
                "localPath='" + localPath + '\'' +
                ", remotePath='" + remotePath + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.localPath);
        dest.writeString(this.remotePath);
    }

    public ImagePath() {
    }

    private ImagePath(Parcel in) {
        this.localPath = in.readString();
        this.remotePath = in.readString();
    }


    public static final Parcelable.Creator<ImagePath> CREATOR = new Parcelable.Creator<ImagePath>() {
        public ImagePath createFromParcel(Parcel source) {
            return new ImagePath(source);
        }

        public ImagePath[] newArray(int size) {
            return new ImagePath[size];
        }
    };
}
