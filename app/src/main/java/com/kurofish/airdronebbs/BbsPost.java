package com.kurofish.airdronebbs;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class BbsPost {
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
}
