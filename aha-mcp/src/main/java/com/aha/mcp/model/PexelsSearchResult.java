package com.aha.mcp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Pexels 搜索接口返回结构（仅保留用到的字段）。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PexelsSearchResult {
    private List<PexelsPhoto> photos;

    public List<PexelsPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PexelsPhoto> photos) {
        this.photos = photos;
    }
}
