# generate_mindmap

根据主题调用通义千问生成一份思维导图大纲（Markdown 多级列表），输出为 `.md` 文件并返回路径。该 `.md` 可直接用 [markmap](https://markmap.js.org/) 渲染为可视化思维导图。

## 输入参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| `topic` | string | 是 | — | 思维导图中心主题，例如「人工智能」 |

## 处理流程

1. `ContentGenerator.generateMindmapOutline(topic)`：调用通义千问生成 Markdown 多级列表
2. `TempFileWriter.write("aha-mindmap", ".md", md)`：写入系统临时目录
3. 返回本地文件路径字符串

## 输出

- 类型：`string`
- 示例：`思维导图已生成：C:\Users\xxx\AppData\Local\Temp\aha-mindmap-20260727-113120.md`

## 渲染方式

- 在线：打开 [markmap 在线编辑器](https://markmap.js.org/repl) 粘贴 `.md` 内容
- 命令行：`npx markmap-cli aha-mindmap-xxxx.md` 生成可独立打开的 HTML

## 示例调用

```text
generate_mindmap(topic="人工智能")
```

## 依赖

- 通义千问 API：环境变量 `AI_DASHSCOPE_API_KEY`，默认模型 `qwen3.7-max-2026-05-17`
