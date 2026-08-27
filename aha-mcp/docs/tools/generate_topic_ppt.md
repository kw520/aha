# generate_topic_ppt

根据主题搜索 Pexels 图片并用 AI 生成文案，制作一份 PPT，返回生成的 `.pptx` 文件路径。

## 输入参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| `topic` | string | 是 | — | PPT 主题，例如「人工智能在医疗中的应用」 |
| `pages` | integer | 否 | 6 | PPT 页数，小于 1 时按 6 处理 |

## 处理流程

1. `ContentGenerator.generateOutline(topic, pages)`：调用通义千问生成每页大纲与文案
2. `PexelsClient.search(topic, max(pages, 6))`：按主题搜索配图，至少取 6 张
3. `PptGenerator.generate(topic, outlines, photos)`：用 Apache POI 组装 `.pptx`
4. 返回本地文件路径字符串

## 输出

- 类型：`string`
- 示例：`PPT 已生成：C:\Users\xxx\AppData\Local\Temp\aha-ppt-20260727-113045.pptx`

## 示例调用

```text
generate_topic_ppt(topic="咖啡文化差异", pages=8)
```

## 依赖

- 通义千问 API：环境变量 `AI_DASHSCOPE_API_KEY`，默认模型 `qwen3.7-max-2026-05-17`
- Pexels API：环境变量 `PEXELS_API_KEY`
