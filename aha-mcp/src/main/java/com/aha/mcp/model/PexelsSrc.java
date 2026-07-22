package com.aha.mcp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Pexels 图片多种尺寸 URL。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PexelsSrc {
    private String original;
    private String large2x;
    private String large;
    private String medium;
    private String small;

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getLarge2x() {
        return large2x;
    }

    public void setLarge2x(String large2x) {
        this.large2x = large2x;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }
}
