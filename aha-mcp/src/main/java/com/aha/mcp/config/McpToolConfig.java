package com.aha.mcp.config;

import com.aha.mcp.tool.MindmapTool;
import com.aha.mcp.tool.PptGeneratorTool;
import com.aha.mcp.tool.VoiceoverScriptTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpToolConfig {

    @Bean
    public ToolCallbackProvider generateTopicPptTool(PptGeneratorTool tool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(tool)
                .build();
    }

    @Bean
    public ToolCallbackProvider voiceoverScriptToolProvider(VoiceoverScriptTool tool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(tool)
                .build();
    }

    @Bean
    public ToolCallbackProvider mindmapToolProvider(MindmapTool tool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(tool)
                .build();
    }
}
