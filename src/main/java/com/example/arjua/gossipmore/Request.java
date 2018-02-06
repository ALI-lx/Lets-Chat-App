package com.example.arjua.gossipmore;

/**
 * Created by arjua on 2/6/2018.
 */

public class Request {
    public String date;

    public Request(){

    }

    public Request(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
