package com.aha.mcp.service;

import com.aha.mcp.model.SlideOutline;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用通义百炼（DashScope）ChatModel 根据主题生成 PPT 大纲。
 * 若未配置 AI_DASHSCOPE_API_KEY，ChatModel Bean 不会创建，这里做友好兜底。
 */
@Service
public class ContentGenerator {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ChatModel chatModel;

    public ContentGenerator(@Autowired(required = false) @Lazy ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public List<SlideOutline> generateOutline(String topic, int pages) {
        if (chatModel == null) {
            throw new IllegalStateException(
                    "未配置 AI_DASHSCOPE_API_KEY，无法使用 LLM 生成文案。请在环境变量中配置后重试。");
        }
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        String prompt = String.format(
                "请为主题「%s」生成一份 %d 页的 PPT 大纲。" +
                "严格只输出一个 JSON 数组，不要包含任何额外文字或 markdown 标记。" +
                "数组每个元素是一个对象，格式为 {\"title\":\"页标题\",\"bullets\":[\"要点1\",\"要点2\"]}。" +
                "内容需专业、与主题强相关。",
                topic, pages);
        String json = chatClient.prompt().user(prompt).call().content();
        return parseOutlines(json);
    }

    /**
     * 生成约 60 秒的短视频口播稿：开场钩子 + 3 个核心论点 + 收尾行动号召。
     */
    public String generateVoiceoverScript(String topic) {
        if (chatModel == null) {
            throw new IllegalStateException(
                    "未配置 AI_DASHSCOPE_API_KEY，无法使用 LLM 生成文案。请在环境变量中配置后重试。");
        }
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        String prompt = String.format(
                "请为主题「%s」写一段约 60 秒的短视频口播稿（中文）。" +
                "严格只输出正文文本，不要包含任何额外说明、标题或 markdown 标记。" +
                "结构要求：" +
                "1. 开场钩子：1 句话抓住注意力（约 5 秒）；" +
                "2. 三个核心论点：每个论点配一句解释（每个约 15 秒）；" +
                "3. 收尾行动号召：1 句话引导观众（约 5 秒）。" +
                "语气自然口语化，像在和观众聊天，避免书面腔。",
                topic);
        return chatClient.prompt().user(prompt).call().content();
    }

    /**
     * 生成思维导图大纲（Markdown 多级列表），可直接用 markmap 渲染。
     */
    public String generateMindmapOutline(String topic) {
        if (chatModel == null) {
            throw new IllegalStateException(
                    "未配置 AI_DASHSCOPE_API_KEY，无法使用 LLM 生成文案。请在环境变量中配置后重试。");
        }
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        String prompt = String.format(
                "请为主题「%s」生成一份思维导图大纲，用于 markmap 渲染。" +
                "严格只输出 Markdown 格式的多级无序列表（用 - 列表项，缩进表示层级），" +
                "不要包含任何额外说明、标题或代码块标记（不要使用 ``` 包裹）。" +
                "要求：" +
                "- 第一层是中心主题本身；" +
                "- 展开 2-4 个主要分支；" +
                "- 每个主要分支下再展开 2-4 个子节点；" +
                "- 内容需专业、与主题强相关，覆盖关键概念与脉络。",
                topic);
        return chatClient.prompt().user(prompt).call().content();
    }

    private List<SlideOutline> parseOutlines(String json) {
        try {
            String cleaned = extractJsonArray(json);
            return objectMapper.readValue(cleaned, new TypeReference<List<SlideOutline>>() {});
        } catch (Exception e) {
            throw new RuntimeException("解析 LLM 返回的文案失败: " + e.getMessage(), e);
        }
    }

    private String extractJsonArray(String text) {
        Matcher m = Pattern.compile("\\[.*\\]", Pattern.DOTALL).matcher(text);
        return m.find() ? m.group() : text;
    }
}
