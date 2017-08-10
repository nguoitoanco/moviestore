package com.fsoft.sonic_larue.khanhnv10.moviestore.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by KhanhNV10 on 2015/11/20.
 */
public class Cast implements Serializable, Parcelable {

    /** Movie Id */
    @SerializedName("id")
    private String actorId;

    /** Cast Id */
    @SerializedName("cast_id")
    private String castId;

    /** Cast Name */
    @SerializedName("name")
    private String actorName;

    /** Cast Credit Id */
    @SerializedName("credit_id")
    private String creditId;

    /** Cast Character */
    @SerializedName("character")
    private String character;

    /** Cast Order */
    @SerializedName("order")
    private int castOrder;

    /** Cast Profile Path */
    @SerializedName("profile_path")
    private String proFilePath;

    /** Actor overview*/
    @SerializedName("biography")
    private String overView;

    /** Actor birthday*/
    @SerializedName("birthday")
    private String birthDay;

    public Cast() {
    }

    public Cast(String actorId, String castId, String actorName, String creditId, String character, int castOrder, String proFilePath) {
        this.actorId = actorId;
        this.castId = castId;
        this.actorName = actorName;
        this.creditId = creditId;
        this.character = character;
        this.castOrder = castOrder;
        this.proFilePath = proFilePath;
    }

    public String getActorId() {
        return actorId;
    }

    public String getActorName() {
        return actorName;
    }

    public String getProFilePath() {
        return proFilePath;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.actorId);
        dest.writeString(this.castId);
        dest.writeString(this.actorName);
        dest.writeString(this.creditId);
        dest.writeString(this.character);
        dest.writeInt(this.castOrder);
        dest.writeString(this.proFilePath);
        dest.writeString(this.overView);
        dest.writeString(this.birthDay);
    }

    protected Cast(Parcel in) {
        this.actorId = in.readString();
        this.castId = in.readString();
        this.actorName = in.readString();
        this.creditId = in.readString();
        this.character = in.readString();
        this.castOrder = in.readInt();
        this.proFilePath = in.readString();
        this.overView = in.readString();
        this.birthDay = in.readString();
    }

    public static final Creator<Cast> CREATOR = new Creator<Cast>() {
        public Cast createFromParcel(Parcel source) {
            return new Cast(source);
        }

        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };
}
