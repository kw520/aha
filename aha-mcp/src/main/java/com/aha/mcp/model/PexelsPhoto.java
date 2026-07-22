package com.aha.mcp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Pexels 单张图片信息（仅保留用到的字段）。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PexelsPhoto {
    private long id;
    private String url;
    private String photographer;
    private String alt;
    private PexelsSrc src;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public PexelsSrc getSrc() {
        return src;
    }

    public void setSrc(PexelsSrc src) {
        this.src = src;
    }
}
