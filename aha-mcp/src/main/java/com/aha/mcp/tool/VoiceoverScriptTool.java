package com.aha.mcp.tool;

import com.aha.mcp.service.ContentGenerator;
import com.aha.mcp.util.TempFileWriter;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * MCP 工具：主题 -> 通义千问生成 60 秒短视频口播稿 -> 输出 .txt。
 * 零图片依赖，纯文本交付。
 */
@Service
public class VoiceoverScriptTool {

    private final ContentGenerator contentGenerator;
    private final TempFileWriter tempFileWriter;

    public VoiceoverScriptTool(@Lazy ContentGenerator contentGenerator,
                               TempFileWriter tempFileWriter) {
        this.contentGenerator = contentGenerator;
        this.tempFileWriter = tempFileWriter;
    }

    @Tool(name = "generate_voiceover_script",
          description = "根据主题生成一段约 60 秒的短视频口播稿（开场钩子 + 3 个核心论点 + 收尾行动号召），输出为 .txt 文件并返回路径。")
    public String generateVoiceoverScript(
            @ToolParam(description = "口播稿主题，例如：为什么年轻人开始攒钱") String topic) throws Exception {
        String script = contentGenerator.generateVoiceoverScript(topic);
        String path = tempFileWriter.write("aha-voiceover", ".txt", script);
        return "口播稿已生成：" + path;
    }
}
