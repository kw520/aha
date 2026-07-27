# aha-mcp

一个用 **Java + Spring AI** 写的 MCP（Model Context Protocol）Server，接通义千问，把"一个主题"直接变成可用的内容成品。

已支持 **主题 → PPT**、**主题 → 口播稿**、**主题 → 思维导图** 三种内容成品，一次输入可扇出多种形态。

## 特性

- 基于 Spring AI 1.1.2 官方 MCP starter，支持 **STDIO** 与 **HTTP（Streamable）** 双模传输
- 接通义千问 DashScope（OpenAI 兼容端点）生成文案
- 调用 Pexels API 自动配图（PPT 工具）
- 用 Apache POI 生成 `.pptx`，无需本地安装 Office
- 纯文本工具（口播稿 / 思维导图）无需配图，零外部依赖即可交付

## 工具一览

| 工具 | 说明 |
| --- | --- |
| `generate_topic_ppt` | 输入主题与页数，自动搜图 + 生成文案，输出 `.pptx` 路径 |
| `generate_voiceover_script` | 输入主题，生成约 60 秒短视频口播稿，输出 `.txt` 路径 |
| `generate_mindmap` | 输入主题，生成思维导图大纲（Markdown），输出 `.md` 路径（可用 markmap 渲染） |

## 快速开始

### 1. 配置环境变量

复制 `.env.example` 为 `.env` 并填入你的 key：

```bash
AI_DASHSCOPE_API_KEY=你的通义千问Key
PEXELS_API_KEY=你的PexelsKey
```

### 2. 启动（两种模式）

**STDIO 模式**（本地客户端如 WorkBuddy / Claude Desktop 直连）：

```json
{
  "mcpServers": {
    "aha-mcp": {
      "command": "java",
      "args": ["-jar", "aha-mcp-1.0.0.jar", "--spring.profiles.active=stdio"]
    }
  }
}
```

**HTTP 模式**（远程 / 多客户端共享）：

```json
{
  "mcpServers": {
    "aha-mcp": {
      "url": "http://localhost:8080/mcp"
    }
  }
}
```

HTTP 模式启动后服务监听 `http://localhost:8080/mcp`。

### 3. 调用示例

向 MCP 客户端发送工具调用：

```text
generate_topic_ppt(topic="咖啡文化差异", pages=8)
generate_voiceover_script(topic="为什么年轻人开始攒钱")
generate_mindmap(topic="人工智能")
```

返回：

```text
PPT 已生成：C:\Users\xxx\AppData\Local\Temp\aha-ppt-20260727-113045.pptx
口播稿已生成：C:\Users\xxx\AppData\Local\Temp\aha-voiceover-20260727-113100.txt
思维导图已生成：C:\Users\xxx\AppData\Local\Temp\aha-mindmap-20260727-113120.md
```

## 架构

```text
用户主题
   │
   ├─ generate_topic_ppt (MCP @Tool)
   │     ├─ ContentGenerator  → 通义千问生成大纲 + 文案
   │     ├─ PexelsClient      → 搜索配图
   │     └─ PptGenerator      → Apache POI 生成 .pptx
   ├─ generate_voiceover_script (MCP @Tool)
   │     └─ ContentGenerator  → 通义千问生成口播稿 → .txt
   └─ generate_mindmap (MCP @Tool)
         └─ ContentGenerator  → 通义千问生成大纲 → .md (markmap)
```

## 文档

- [generate_topic_ppt 工具文档](docs/tools/generate_topic_ppt.md)
- [generate_voiceover_script 工具文档](docs/tools/generate_voiceover_script.md)
- [generate_mindmap 工具文档](docs/tools/generate_mindmap.md)

## License

[Apache 2.0](LICENSE)
