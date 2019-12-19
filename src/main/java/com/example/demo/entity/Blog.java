package com.example.demo.entity;

import java.io.Serializable;
import java.util.Date;

public class Blog implements Serializable {

    private String title;

    private String authro;

    private String wordount;

    private Date publishDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthro() {
        return authro;
    }

    public void setAuthro(String authro) {
        this.authro = authro;
    }

    public String getWordount() {
        return wordount;
    }

    public void setWordount(String wordount) {
        this.wordount = wordount;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }
}
