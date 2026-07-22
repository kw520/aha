package com.aha.mcp.service;

import com.aha.mcp.model.PexelsPhoto;
import com.aha.mcp.model.PexelsSrc;
import com.aha.mcp.model.SlideOutline;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * 用 Apache POI 生成 .pptx：封面 + 内容页（要点 + Pexels 图片 + 来源署名）。
 */
@Service
public class PptGenerator {

    private static final String FONT = "Microsoft YaHei";

    private final PexelsClient pexelsClient;

    public PptGenerator(PexelsClient pexelsClient) {
        this.pexelsClient = pexelsClient;
    }

    public String generate(String topic, List<SlideOutline> outlines, List<PexelsPhoto> photos) throws Exception {
        XMLSlideShow ppt = new XMLSlideShow();
        Dimension screen = ppt.getPageSize();
        double pageW = screen.getWidth();
        double pageH = screen.getHeight();

        // 封面
        XSLFSlide cover = ppt.createSlide();
        XSLFTextBox coverTitle = cover.createTextBox();
        coverTitle.setAnchor(new Rectangle2D.Double(60, pageH / 2 - 60, pageW - 120, 120));
        XSLFTextRun ct = coverTitle.addNewTextParagraph().addNewTextRun();
        ct.setText(topic);
        ct.setFontFamily(FONT);
        ct.setFontSize(40.0);
        ct.setBold(true);
        XSLFTextRun cs = coverTitle.addNewTextParagraph().addNewTextRun();
        cs.setText("由 aha-mcp 生成");
        cs.setFontFamily(FONT);
        cs.setFontSize(18.0);
        cs.setFontColor(Color.GRAY);

        // 内容页
        int photoIdx = 0;
        for (SlideOutline o : outlines) {
            XSLFSlide slide = ppt.createSlide();

            // 标题
            XSLFTextBox titleBox = slide.createTextBox();
            titleBox.setAnchor(new Rectangle2D.Double(50, 30, pageW - 100, 60));
            XSLFTextRun tr = titleBox.addNewTextParagraph().addNewTextRun();
            tr.setText(o.getTitle());
            tr.setFontFamily(FONT);
            tr.setFontSize(28.0);
            tr.setBold(true);

            // 要点
            XSLFTextBox body = slide.createTextBox();
            body.setAnchor(new Rectangle2D.Double(50, 110, pageW / 2 - 60, pageH - 200));
            if (o.getBullets() != null) {
                for (String b : o.getBullets()) {
                    XSLFTextParagraph p = body.addNewTextParagraph();
                    p.setBullet(true);
                    XSLFTextRun r = p.addNewTextRun();
                    r.setText(b);
                    r.setFontFamily(FONT);
                    r.setFontSize(16.0);
                }
            }

            // 图片 + 署名
            if (photos != null && !photos.isEmpty()) {
                PexelsPhoto photo = photos.get(photoIdx % photos.size());
                photoIdx++;
                try {
                    String imgUrl = pickUrl(photo.getSrc());
                    if (imgUrl != null) {
                        byte[] img = pexelsClient.download(imgUrl);
                        XSLFPictureData pd = ppt.addPicture(img, PictureData.PictureType.JPEG);
                        XSLFPictureShape pic = slide.createPicture(pd);
                        pic.setAnchor(new Rectangle2D.Double(pageW / 2 + 10, 110, pageW / 2 - 60, pageH - 260));

                        XSLFTextBox credit = slide.createTextBox();
                        credit.setAnchor(new Rectangle2D.Double(pageW / 2 + 10, pageH - 130, pageW / 2 - 60, 30));
                        XSLFTextRun cr = credit.addNewTextParagraph().addNewTextRun();
                        cr.setText("图片来源：Pexels / "
                                + (photo.getPhotographer() == null ? "未知" : photo.getPhotographer()));
                        cr.setFontFamily(FONT);
                        cr.setFontSize(10.0);
                        cr.setFontColor(Color.GRAY);
                    }
                } catch (Exception ignored) {
                    // 单张图失败不影响整体
                }
            }
        }

        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + File.separator + "aha-ppt-" + System.currentTimeMillis() + ".pptx";
        try (FileOutputStream out = new FileOutputStream(path)) {
            ppt.write(out);
        }
        ppt.close();
        return path;
    }

    private String pickUrl(PexelsSrc src) {
        if (src == null) return null;
        if (src.getLarge2x() != null) return src.getLarge2x();
        if (src.getLarge() != null) return src.getLarge();
        if (src.getOriginal() != null) return src.getOriginal();
        if (src.getMedium() != null) return src.getMedium();
        return src.getSmall();
    }
}
