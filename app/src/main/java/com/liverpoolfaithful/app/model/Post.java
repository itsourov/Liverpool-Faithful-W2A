package com.liverpoolfaithful.app.model;
public class Post {
    private String title;
    private String details;
    private String date;
    private String id;
    private String selfUrl;
    private String category_name;
    private String feature_image_thumb;
    private String feature_image_full;
    private String ago_time;
    private int typeCode;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(String selfUrl) {
        this.selfUrl = selfUrl;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getFeature_image_thumb() {
        return feature_image_thumb;
    }

    public void setFeature_image_thumb(String feature_image_thumb) {
        this.feature_image_thumb = feature_image_thumb;
    }

    public String getFeature_image_full() {
        return feature_image_full;
    }

    public void setFeature_image_full(String feature_image_full) {
        this.feature_image_full = feature_image_full;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(int typeCode) {
        this.typeCode = typeCode;
    }

    public String getAgo_time() {
        return ago_time;
    }

    public void setAgo_time(String ago_time) {
        this.ago_time = ago_time;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}