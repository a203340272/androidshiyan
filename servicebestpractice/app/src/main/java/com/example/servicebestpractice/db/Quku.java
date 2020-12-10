package com.example.servicebestpractice.db;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Quku extends LitePalSupport {
    private String author;
    private String songname;
    private String songURL;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public String getSongURL() {
        return songURL;
    }

    public void setSongURL(String songURL) {
        this.songURL = songURL;
    }
}
