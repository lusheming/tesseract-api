package com.tess.tesseractapi.dto;

import lombok.Data;

@Data
public class BoundingBox {
    private int x;
    private int y;
    private int width;
    private int height;
    // 省略getter/setter
}