# generate_voiceover_script

根据主题调用通义千问生成一段约 60 秒的短视频口播稿（开场钩子 + 3 个核心论点 + 收尾行动号召），输出为 `.txt` 文件并返回路径。

## 输入参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| `topic` | string | 是 | — | 口播稿主题，例如「为什么年轻人开始攒钱」 |

## 处理流程

1. `ContentGenerator.generateVoiceoverScript(topic)`：调用通义千问按固定结构生成口播稿正文
2. `TempFileWriter.write("aha-voiceover", ".txt", script)`：写入系统临时目录
3. 返回本地文件路径字符串

## 输出

- 类型：`string`
- 示例：`口播稿已生成：C:\Users\xxx\AppData\Local\Temp\aha-voiceover-20260727-113100.txt`

## 示例调用

```text
generate_voiceover_script(topic="为什么年轻人开始攒钱")
```

## 依赖

- 通义千问 API：环境变量 `AI_DASHSCOPE_API_KEY`，默认模型 `qwen3.7-max-2026-05-17`
