package com.tess.tesseractapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TesseractApiApplication {
    static {
        // 设置Tesseract库路径（根据实际安装路径调整）
        System.setProperty("jna.library.path", "/usr/local/Cellar/tesseract/5.5.0/lib");
    }
    public static void main(String[] args) {
        SpringApplication.run(TesseractApiApplication.class, args);
    }

}
