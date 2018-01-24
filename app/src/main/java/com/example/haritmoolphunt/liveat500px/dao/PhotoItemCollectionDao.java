package com.example.haritmoolphunt.liveat500px.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Harit Moolphunt on 29/10/2560.
 */

public class PhotoItemCollectionDao implements Parcelable{
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<PhotoListDao> data;

    public PhotoItemCollectionDao(){

    }

    protected PhotoItemCollectionDao(Parcel in) {
        byte tmpSuccess = in.readByte();
        success = tmpSuccess == 0 ? null : tmpSuccess == 1;
        data = in.createTypedArrayList(PhotoListDao.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success == null ? 0 : success ? 1 : 2));
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoItemCollectionDao> CREATOR = new Creator<PhotoItemCollectionDao>() {
        @Override
        public PhotoItemCollectionDao createFromParcel(Parcel in) {
            return new PhotoItemCollectionDao(in);
        }

        @Override
        public PhotoItemCollectionDao[] newArray(int size) {
            return new PhotoItemCollectionDao[size];
        }
    };

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<PhotoListDao> getData() {
        return data;
    }

    public void setData(List<PhotoListDao> data) {
        this.data = data;
    }
}
