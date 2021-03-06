package com.kurofish.airdronebbs.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class BbsPost implements Parcelable {
    private String main_title;
    private String sub_title;
    private String author;
    private String text;
    private long id;
    private long click;
    private Date time;

    public BbsPost() {

    }

    public BbsPost(String main_title, String sub_title, String author) {
        this.main_title = main_title;
        this.sub_title = sub_title;
        this.author = author;
    }

    public String getMain_title() {
        return main_title;
    }

    public void setMain_title(String main_title) {
        this.main_title = main_title;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getClick() {
        return click;
    }

    public void setClick(long click) {
        this.click = click;
    }

    @ServerTimestamp
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.main_title);
        dest.writeString(this.sub_title);
        dest.writeString(this.author);
        dest.writeString(this.text);
        dest.writeLong(this.id);
        dest.writeLong(this.click);
        dest.writeLong(this.time != null ? this.time.getTime() : -1);
    }

    protected BbsPost(Parcel in) {
        this.main_title = in.readString();
        this.sub_title = in.readString();
        this.author = in.readString();
        this.text = in.readString();
        this.id = in.readLong();
        this.click = in.readLong();
        long tmpTime = in.readLong();
        this.time = tmpTime == -1 ? null : new Date(tmpTime);
    }

    public static final Parcelable.Creator<BbsPost> CREATOR = new Parcelable.Creator<BbsPost>() {
        @Override
        public BbsPost createFromParcel(Parcel source) {
            return new BbsPost(source);
        }

        @Override
        public BbsPost[] newArray(int size) {
            return new BbsPost[size];
        }
    };
}
