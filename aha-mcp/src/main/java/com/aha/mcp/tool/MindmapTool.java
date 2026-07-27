package com.aha.mcp.tool;

import com.aha.mcp.service.ContentGenerator;
import com.aha.mcp.util.TempFileWriter;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * MCP 工具：主题 -> 通义千问生成 Markdown 多级列表大纲 -> 输出 .md（markmap 输入）。
 * 零图片依赖，纯文本交付。
 */
@Service
public class MindmapTool {

    private final ContentGenerator contentGenerator;
    private final TempFileWriter tempFileWriter;

    public MindmapTool(@Lazy ContentGenerator contentGenerator,
                       TempFileWriter tempFileWriter) {
        this.contentGenerator = contentGenerator;
        this.tempFileWriter = tempFileWriter;
    }

    @Tool(name = "generate_mindmap",
          description = "根据主题生成一份思维导图大纲（Markdown 多级列表），输出为 .md 文件（可用 markmap 渲染）并返回路径。")
    public String generateMindmap(
            @ToolParam(description = "思维导图中心主题，例如：人工智能") String topic) throws Exception {
        String md = contentGenerator.generateMindmapOutline(topic);
        String path = tempFileWriter.write("aha-mindmap", ".md", md);
        return "思维导图已生成：" + path;
    }
}
