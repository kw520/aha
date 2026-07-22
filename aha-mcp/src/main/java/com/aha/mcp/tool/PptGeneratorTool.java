package com.aha.mcp.tool;

import com.aha.mcp.model.PexelsPhoto;
import com.aha.mcp.model.SlideOutline;
import com.aha.mcp.service.ContentGenerator;
import com.aha.mcp.service.PexelsClient;
import com.aha.mcp.service.PptGenerator;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MCP 工具入口：主题 -> 搜图 + 生成文案 -> 生成 PPT。
 *
 * 说明（关于权限）：此处用 Spring AI 标准 @Tool 注解注册工具。
 * 若后续改 HTTP 模式并启用 Spring Security，可在方法上加 @PreAuthorize("hasAuthority('mcp:ppt:generate')")，
 * 由 SecurityFilterChain 保护端点。STDIO 本地模式下无需 HTTP 鉴权。
 */
@Service
public class PptGeneratorTool {

    private final ContentGenerator contentGenerator;
    private final PexelsClient pexelsClient;
    private final PptGenerator pptGenerator;

    // @Lazy 打破 “工具 <-> ChatModel” 循环依赖：
    // 工具方法上的 @Tool 被扫描注册为 MCP 工具时，会经由 toolCallbackResolver -> ChatModel -> contentGenerator 形成回路。
    // 启动时注入延迟代理，待上下文完全就绪、真正调用工具时再解析 ContentGenerator。
    public PptGeneratorTool(@Lazy ContentGenerator contentGenerator,
                            PexelsClient pexelsClient,
                            PptGenerator pptGenerator) {
        this.contentGenerator = contentGenerator;
        this.pexelsClient = pexelsClient;
        this.pptGenerator = pptGenerator;
    }

    @Tool(name = "generate_topic_ppt",
          description = "根据主题搜索 Pexels 图片并用 AI 生成文案，制作一份 PPT。返回生成的 .pptx 文件路径。")
    public String generateTopicPpt(
            @ToolParam(description = "PPT 主题，例如：人工智能在医疗中的应用") String topic,
            @ToolParam(description = "PPT 页数，默认 6") Integer pages) throws Exception {
        int n = (pages == null || pages < 1) ? 6 : pages;
        List<SlideOutline> outlines = contentGenerator.generateOutline(topic, n);
        List<PexelsPhoto> photos = pexelsClient.search(topic, Math.max(n, 6));
        String path = pptGenerator.generate(topic, outlines, photos);
        return "PPT 已生成：" + path;
    }
}
