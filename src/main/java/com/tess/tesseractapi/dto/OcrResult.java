package com.tess.tesseractapi.dto;

import lombok.Data;

// 需要添加的辅助类
@Data
public class OcrResult {
    private String text;
    private BoundingBox coordinates;
    // 省略getter/setter
}