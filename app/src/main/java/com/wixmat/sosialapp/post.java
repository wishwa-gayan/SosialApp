package com.wixmat.sosialapp;

import android.content.Intent;
import android.view.View;

public class post implements View.OnClickListener
{
    public String uid, time, date, postimage, description, profileimage, fullname,title,timestamp;

    public post()
    {

    }

    public post(String uid, String time, String date, String postimage, String description,String title, String profileimage, String fullname,String timestamp) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postimage = postimage;
        this.description = description;
        this.title = title;
        this.profileimage = profileimage;
        this.fullname = fullname;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public String getTitle() {
        return title;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }


    @Override
    public void onClick(View v) {

    }
}
