package com.tess.tesseractapi.controller;

import cn.hutool.core.util.StrUtil;
import com.tess.tesseractapi.dto.OcrResult;
import com.tess.tesseractapi.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ocr")
public class TesseractController {

    @Autowired
    private OcrService ocrService;
    @Qualifier("ocrExecutor")
    @Autowired
    private Executor executor;

    @PostMapping("/file")
    public CompletableFuture<Object> file(@RequestParam("file") MultipartFile file,  String predictWords) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("hit", false);
        return CompletableFuture.supplyAsync(() -> {
            try {
                BufferedImage image = ImageIO.read(file.getInputStream());
                // 调用 OCR 服务
                String result = ocrService.doOcr(image);
                resultMap.put("result", result);
                // 判断 result 是否包含 words words 是个逗号分割的多个字符串
                List<String> split = StrUtil.split(predictWords, ',');
                for (String word : split) {
                    if (result.toUpperCase().contains(word)) {
                        System.out.println("存在 " + word);
                        resultMap.put("hit", true);
                    } else {
                        System.out.println("不存在 " + word);
                    }
                }
                return resultMap;
            } catch (Exception e) {
                resultMap.put("errorMsg", "Failed to process the image: " + e.getMessage());
                return resultMap;
            }
        }, executor); // 使用自定义线程池
    }

    /**
     * 根据 url 地址获取图片并识别，是否包含的多个单词校验
     */
    @GetMapping("/url")
    public CompletableFuture<Object> url(@RequestParam("url") String url, String predictWords) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("hit", false);
        return CompletableFuture.supplyAsync(() -> {
            try {
                //Url 远程地址
                BufferedImage image = ImageIO.read(new URL(url).openStream());
                // 调用 OCR 服务
                String result = ocrService.doOcr(image);
                resultMap.put("result", result);
                // 判断 result 是否包含 words words 是个逗号分割的多个字符串
                List<String> split = StrUtil.split(predictWords, ',');
                for (String word : split) {
                    if (result.toUpperCase().contains(word)) {
                        System.out.println("存在 " + word);
                        resultMap.put("hit", true);
                    } else {
                        System.out.println("不存在 " + word);
                    }
                }
                return resultMap;
            } catch (Exception e) {
                resultMap.put("errorMsg", "Failed to process the image: " + e.getMessage());
                return resultMap;
            }
        }, executor); // 使用自定义线程池
    }

    /**
     * 坐标信息
     * @param url
     * @param predictWords
     * @return
     */
    @GetMapping("/urlToCoordinate")
    public CompletableFuture<Object> urlToCoordinate(@RequestParam("url") String url, String predictWords) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("hit", false);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 获取图片流
                BufferedImage image = ImageIO.read(new URL(url).openStream());

                // 调用OCR服务获取详细识别结果（包含坐标）
                List<OcrResult> ocrResults = ocrService.doOcrWithCoordinates(image); // [[6]]
                resultMap.put("ocrDetails", ocrResults);

                // 提取纯文本结果
                String fullText = ocrResults.stream()
                        .map(OcrResult::getText)
                        .collect(Collectors.joining(" "));
                resultMap.put("result", fullText);

                // 处理关键词校验
                List<String> words = StrUtil.split(predictWords, ',');
                Map<String, Boolean> hitMap = new HashMap<>();
                for (String word : words) {
                    boolean hit = fullText.toUpperCase().contains(word.trim().toUpperCase());
                    hitMap.put(word, hit);
                    if (hit) resultMap.put("hit", true); // 只要有一个命中即为true
                }
                resultMap.put("hitDetails", hitMap);

                return resultMap;

            } catch (Exception e) {
                resultMap.put("errorMsg", "处理失败: " + e.getMessage());
                return resultMap;
            }
        }, executor);
    }

}