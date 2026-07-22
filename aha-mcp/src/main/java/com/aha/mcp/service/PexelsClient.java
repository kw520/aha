package com.aha.mcp.service;

import com.aha.mcp.model.PexelsPhoto;
import com.aha.mcp.model.PexelsSearchResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 对接 Pexels API：搜索图片 + 下载图片字节。
 * 使用 JDK 自带 HttpClient，无需额外 Web 依赖。
 */
@Component
public class PexelsClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();

    @Value("${pexels.api-key:}")
    private String apiKey;

    @Value("${pexels.base-url:https://api.pexels.com/v1}")
    private String baseUrl;

    public List<PexelsPhoto> search(String query, int perPage) {
        try {
            String url = baseUrl + "/search?query="
                    + URLEncoder.encode(query, StandardCharsets.UTF_8)
                    + "&per_page=" + perPage + "&orientation=landscape";
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", apiKey)
                    .GET()
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new RuntimeException("Pexels API 错误: " + resp.statusCode() + " " + resp.body());
            }
            PexelsSearchResult result = objectMapper.readValue(resp.body(), PexelsSearchResult.class);
            return result.getPhotos() == null ? List.of() : result.getPhotos();
        } catch (Exception e) {
            throw new RuntimeException("调用 Pexels 失败: " + e.getMessage(), e);
        }
    }

    public byte[] download(String url) {
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<byte[]> resp = http.send(req, HttpResponse.BodyHandlers.ofByteArray());
            return resp.body();
        } catch (Exception e) {
            throw new RuntimeException("下载图片失败: " + e.getMessage(), e);
        }
    }
}
