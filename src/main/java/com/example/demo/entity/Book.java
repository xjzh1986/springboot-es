package com.example.demo.entity;

import com.example.demo.utils.ReflexUtils;

import java.io.Serializable;
import java.util.Date;

public class Book implements Serializable {
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 封面
     */
    private String frontImage;
    /**
     * 作者
     */
    private String authro;
    /**
     * 词量
     */
    private Integer wordCount;
    /**
     * 发布时间
     */
    private Date publishDate;
    /**
     * 类别
     */
    private String blogType;
    /**
     * 排序
     */
    private Integer blogOrder;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrontImage() {
        return frontImage;
    }

    public void setFrontImage(String frontImage) {
        this.frontImage = frontImage;
    }

    public String getAuthro() {
        return authro;
    }

    public void setAuthro(String authro) {
        this.authro = authro;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getBlogType() {
        return blogType;
    }

    public void setBlogType(String blogType) {
        this.blogType = blogType;
    }

    public Integer getBlogOrder() {
        return blogOrder;
    }

    public void setBlogOrder(Integer blogOrder) {
        this.blogOrder = blogOrder;
    }

    public static void main(String[] args) {

        Book blog = new Book();
        blog.setTitle("111");
        blog.setAuthro("222");
        blog.setBlogOrder(2);
        blog.setPublishDate(new Date());
        ReflexUtils.getEntityNameAndVal(blog);
    }



}
