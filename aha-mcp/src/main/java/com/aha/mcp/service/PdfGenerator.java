package com.aha.mcp.service;

import com.aha.mcp.model.SlideOutline;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * 用 Apache PDFBox 生成 .pdf：封面 + 内容页（标题 + 要点）。
 * 自动检测系统 CJK 字体，中文正常显示。
 */
@Service
public class PdfGenerator {

    private static final float MARGIN = 50f;
    private static final float TITLE_SIZE = 24f;
    private static final float BODY_SIZE = 14f;
    private static final float COVER_TITLE_SIZE = 36f;
    private static final float LINE_SPACING = 8f;

    public String generate(String topic, List<SlideOutline> outlines) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDFont font = loadCjkFont(doc);
            PDRectangle page = PDRectangle.A4;
            float pageW = page.getWidth();
            float pageH = page.getHeight();

            // 封面
            PDPage cover = new PDPage(page);
            doc.addPage(cover);
            try (PDPageContentStream cs = new PDPageContentStream(doc, cover)) {
                cs.setNonStrokingColor(new Color(41, 98, 163));
                cs.beginText();
                cs.setFont(font, COVER_TITLE_SIZE);
                float titleWidth = font.getStringWidth(topic) / 1000 * COVER_TITLE_SIZE;
                cs.newLineAtOffset((pageW - titleWidth) / 2, pageH / 2);
                cs.showText(topic);
                cs.endText();

                cs.setNonStrokingColor(Color.GRAY);
                cs.beginText();
                cs.setFont(font, BODY_SIZE);
                String sub = "由 aha-mcp 生成";
                float subWidth = font.getStringWidth(sub) / 1000 * BODY_SIZE;
                cs.newLineAtOffset((pageW - subWidth) / 2, pageH / 2 - 40);
                cs.showText(sub);
                cs.endText();
            }

            // 内容页
            for (SlideOutline o : outlines) {
                PDPage contentPage = new PDPage(page);
                doc.addPage(contentPage);
                try (PDPageContentStream cs = new PDPageContentStream(doc, contentPage)) {
                    float y = pageH - MARGIN;

                    // 标题
                    cs.setNonStrokingColor(new Color(41, 98, 163));
                    cs.beginText();
                    cs.setFont(font, TITLE_SIZE);
                    cs.newLineAtOffset(MARGIN, y);
                    cs.showText(o.getTitle() != null ? o.getTitle() : "");
                    cs.endText();
                    y -= TITLE_SIZE + LINE_SPACING;

                    // 要点
                    cs.setNonStrokingColor(Color.BLACK);
                    if (o.getBullets() != null) {
                        for (String bullet : o.getBullets()) {
                            String text = "• " + bullet;
                            if (y < MARGIN) {
                                // 超出页面，截断
                                break;
                            }
                            cs.beginText();
                            cs.setFont(font, BODY_SIZE);
                            cs.newLineAtOffset(MARGIN + 10, y);
                            cs.showText(text);
                            cs.endText();
                            y -= BODY_SIZE + LINE_SPACING;
                        }
                    }
                }
            }

            // 写入临时文件
            String dir = System.getProperty("java.io.tmpdir");
            String path = dir + File.separator + "aha-pdf-" + System.currentTimeMillis() + ".pdf";
            doc.save(path);
            return path;
        }
    }

    /**
     * 尝试加载系统 CJK 字体，找不到则回退到 Helvetica（中文可能无法显示）。
     */
    private PDFont loadCjkFont(PDDocument doc) {
        String[] candidates = {
                "C:\\Windows\\Fonts\\msyh.ttc",
                "C:\\Windows\\Fonts\\simsun.ttc",
                "/usr/share/fonts/truetype/wqy/wqy-microhei.ttc",
                "/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc",
                "/System/Library/Fonts/PingFang.ttc"
        };
        for (String path : candidates) {
            try (FileInputStream fis = new FileInputStream(path)) {
                return PDType0Font.load(doc, fis);
            } catch (Exception ignored) {
            }
        }
        return new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    }
}
