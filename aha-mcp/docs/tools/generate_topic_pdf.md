# generate_topic_pdf

根据主题用 AI 生成文案，制作一份 PDF 文档。作为 PPT 的轻量替代方案，覆盖"快速阅读文档"场景。

## 参数

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| topic | String | 是 | 文档主题，例如：人工智能在医疗中的应用 |
| pages | Integer | 否 | 文档页数，默认 6 |

## 调用示例

```text
generate_topic_pdf(topic="咖啡文化差异", pages=8)
```

## 返回

```text
PDF 已生成：C:\Users\xxx\AppData\Local\Temp\aha-pdf-20260729-143020.pdf
```

## 技术实现

- 使用通义千问生成大纲（复用 ContentGenerator）
- 使用 Apache PDFBox 生成 .pdf
- 自动检测系统 CJK 字体，中文正常显示
- 封面页 + 内容页（标题 + 要点列表）
