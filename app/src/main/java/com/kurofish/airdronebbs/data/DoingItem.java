package com.kurofish.airdronebbs.data;

import java.util.Date;

public class DoingItem {
    private String announcer;
    private long id;
    private String name;
    private Date date;
    private long cur_participant;
    private long full_participant;

    public String getAnnouncer() {
        return announcer;
    }

    public void setAnnouncer(String announcer) {
        this.announcer = announcer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getCur_participant() {
        return cur_participant;
    }

    public void setCur_participant(long cur_participant) {
        this.cur_participant = cur_participant;
    }

    public long getFull_participant() {
        return full_participant;
    }

    public void setFull_participant(long full_participant) {
        this.full_participant = full_participant;
    }
}
