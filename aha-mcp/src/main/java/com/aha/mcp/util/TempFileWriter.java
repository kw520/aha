package com.aha.mcp.util;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 把生成的文本内容写到系统临时目录，返回绝对路径。
 * 与 PPT 的临时落盘路径约定保持一致（java.io.tmpdir 即 Windows 的 %TEMP%）。
 */
@Component
public class TempFileWriter {

    public String write(String prefix, String ext, String content) throws IOException {
        String dir = System.getProperty("java.io.tmpdir");
        String path = dir + File.separator + prefix + "-" + System.currentTimeMillis() + ext;
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(content);
        }
        return path;
    }
}
