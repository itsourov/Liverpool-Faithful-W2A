package com.liverpoolfaithful.app.model;

public class Comment {
    private String comment_id;
    private String author_name;
    private String date;
    private String content;
    private String author_avatar_urls;
    private int childNumber = 0;


    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor_avatar_urls() {
        return author_avatar_urls;
    }

    public void setAuthor_avatar_urls(String author_avatar_urls) {
        this.author_avatar_urls = author_avatar_urls;
    }

    public int getChildNumber() {
        return childNumber;
    }

    public void setChildNumber(int childNumber) {
        this.childNumber = childNumber;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }
}
