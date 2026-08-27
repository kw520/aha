package com.aha.mcp.tool;

import com.aha.mcp.model.SlideOutline;
import com.aha.mcp.service.ContentGenerator;
import com.aha.mcp.service.PdfGenerator;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MCP 工具入口：主题 -> 生成文案 -> 生成 PDF。
 * 作为 PPT 的轻量替代方案，覆盖"快速阅读文档"场景。
 */
@Service
public class PdfGeneratorTool {

    private final ContentGenerator contentGenerator;
    private final PdfGenerator pdfGenerator;

    public PdfGeneratorTool(@Lazy ContentGenerator contentGenerator,
                            PdfGenerator pdfGenerator) {
        this.contentGenerator = contentGenerator;
        this.pdfGenerator = pdfGenerator;
    }

    @Tool(name = "generate_topic_pdf",
          description = "根据主题用 AI 生成文案，制作一份 PDF 文档。返回生成的 .pdf 文件路径。")
    public String generateTopicPdf(
            @ToolParam(description = "文档主题，例如：人工智能在医疗中的应用") String topic,
            @ToolParam(description = "文档页数，默认 6") Integer pages) throws Exception {
        int n = (pages == null || pages < 1) ? 6 : pages;
        List<SlideOutline> outlines = contentGenerator.generateOutline(topic, n);
        String path = pdfGenerator.generate(topic, outlines);
        return "PDF 已生成：" + path;
    }
}
