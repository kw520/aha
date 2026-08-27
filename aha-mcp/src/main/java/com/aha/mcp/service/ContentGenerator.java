package com.aha.mcp.service;

import com.aha.mcp.model.SlideOutline;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 用通义百炼（DashScope）ChatClient 生成 PPT 大纲、口播稿、思维导图大纲。
 * ChatClient 由 ChatClientConfig 统一创建为 Bean，此处复用。
 * Prompt 模板外部化到 resources/prompts/*.st，修改 prompt 无需改代码。
 */
@Service
public class ContentGenerator {

    private final ChatClient chatClient;
    private final PromptTemplate outlinePrompt;
    private final PromptTemplate voiceoverPrompt;
    private final PromptTemplate mindmapPrompt;

    public ContentGenerator(@Autowired(required = false) @Lazy ChatClient chatClient) {
        this.chatClient = chatClient;
        this.outlinePrompt = loadTemplate("prompts/outline.st");
        this.voiceoverPrompt = loadTemplate("prompts/voiceover.st");
        this.mindmapPrompt = loadTemplate("prompts/mindmap.st");
    }

    /**
     * 加载 classpath 下的 .st prompt 模板文件。
     */
    private PromptTemplate loadTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return new PromptTemplate(content);
        } catch (IOException e) {
            throw new RuntimeException("加载 prompt 模板失败: " + path, e);
        }
    }

    /**
     * 生成 PPT 大纲：用 BeanOutputConverter 结构化输出，无需手写 JSON 解析。
     */
    public List<SlideOutline> generateOutline(String topic, int pages) {
        ensureChatClient();
        String prompt = outlinePrompt.render(Map.of("topic", topic, "pages", pages));
        return chatClient.prompt(prompt)
                .call()
                .entity(new ParameterizedTypeReference<List<SlideOutline>>() {});
    }

    /**
     * 生成约 60 秒的短视频口播稿：开场钩子 + 3 个核心论点 + 收尾行动号召。
     */
    public String generateVoiceoverScript(String topic) {
        ensureChatClient();
        String prompt = voiceoverPrompt.render(Map.of("topic", topic));
        return chatClient.prompt(prompt).call().content();
    }

    /**
     * 生成思维导图大纲（Markdown 多级列表），可直接用 markmap 渲染。
     */
    public String generateMindmapOutline(String topic) {
        ensureChatClient();
        String prompt = mindmapPrompt.render(Map.of("topic", topic));
        return chatClient.prompt(prompt).call().content();
    }

    private void ensureChatClient() {
        if (chatClient == null) {
            throw new IllegalStateException(
                    "未配置 AI_DASHSCOPE_API_KEY，无法使用 LLM 生成文案。请在环境变量中配置后重试。");
        }
    }
}
