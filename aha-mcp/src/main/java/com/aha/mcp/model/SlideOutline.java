package com.aha.mcp.model;

import java.util.List;

/**
 * 单页 PPT 大纲，由 LLM 生成，用于驱动 POI 排版。
 */
public class SlideOutline {
    private String title;
    private List<String> bullets;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getBullets() {
        return bullets;
    }

    public void setBullets(List<String> bullets) {
        this.bullets = bullets;
    }
}
