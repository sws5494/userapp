package com.example.user.userapp;

import android.graphics.drawable.Drawable;

/**
 * Created by user on 2017-04-17.
 */

public class ListViewItem {
    private String reason ;
    private String start ;
    private String descStr ;
    private String allow ;

    public void setReason(String _reason) {
        reason = _reason ;
    }
    public void setStart(String _start) {
        start = _start ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }
    public void setAllow(String _allow) {
        allow = _allow ;
    }


    public String getReason() {
        return this.reason ;
    }
    public String getStart() {
        return this.start ;
    }
    public String getDesc() {
        return this.descStr ;
    }
    public String getAllow() {
        return this.allow ;
    }
}