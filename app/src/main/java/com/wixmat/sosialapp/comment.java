package com.wixmat.sosialapp;

public class comment {
    private String cid;
    private String comment;
    private String uid;
    private String date;
    private String time;



    public comment(String cid, String comment,  String date, String time,String uid) {
        this.cid = cid;
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.uid = uid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
