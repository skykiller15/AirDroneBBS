package com.kurofish.airdronebbs.data;

import java.util.Date;

public class BbsReply {
    private String author;
    private String text;
    private Date time;

    public BbsReply() {

    }

    public BbsReply(String author) {
        this.author = author;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
