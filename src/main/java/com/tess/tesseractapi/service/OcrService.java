package com.tess.tesseractapi.service;
import com.tess.tesseractapi.dto.BoundingBox;
import com.tess.tesseractapi.dto.OcrResult;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Word;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableAsync
public class OcrService {
    private static final String TESSDATA_PATH;

    static {
        try {
            TESSDATA_PATH = new ClassPathResource("tessdata").getURI().getPath();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load tessdata path", e);
        }
    }
    private final ThreadLocal<Tesseract> tesseractThreadLocal = ThreadLocal.withInitial(() -> {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(TESSDATA_PATH);
        tesseract.setLanguage("eng");
        return tesseract;
    });
    private BufferedImage readImageAsBufferedImage(File imageFile) throws IOException {
        return ImageIO.read(imageFile);
    }
    public String doOcr(BufferedImage file) throws Exception {
        Tesseract tesseract = tesseractThreadLocal.get();
        try {
            return tesseract.doOCR(file);
        } finally {
            // 清理 ThreadLocal 资源
            tesseractThreadLocal.remove();
        }
    }
    /**
     * 新增方法：返回带坐标信息的OCR识别结果
     */
    public List<OcrResult> doOcrWithCoordinates(BufferedImage image) throws Exception {
        Tesseract tesseract = tesseractThreadLocal.get();
        try {
            // 设置页面迭代级别为单词级识别
            List<Word> words = tesseract.getWords(image, ITessAPI.TessPageIteratorLevel.RIL_WORD);

            return words.stream().map(word -> {
                OcrResult result = new OcrResult();
                result.setText(word.getText());

                // 转换坐标信息 [[5]]
                BoundingBox box = new BoundingBox();
                box.setX(word.getBoundingBox().x);
                box.setY(word.getBoundingBox().y);
                box.setWidth(word.getBoundingBox().width);
                box.setHeight(word.getBoundingBox().height);
                result.setCoordinates(box);

                return result;
            }).collect(Collectors.toList());

        } finally {
            tesseractThreadLocal.remove(); // 释放线程局部变量
        }
    }

}