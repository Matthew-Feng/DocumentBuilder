package com.documentbuilder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class DocumentProcessor {
    private final AppConfig config;
    private final ImageProcessor imageProcessor;
    private final WordDocumentCreator wordDocumentCreator;
    
    public DocumentProcessor(AppConfig config) {
        this.config = config;
        this.imageProcessor = new ImageProcessor();
        this.wordDocumentCreator = new WordDocumentCreator();
    }
    
    public void process() throws IOException {
        // Get sorted PNG images
        List<Path> images = imageProcessor.getSortedPngImages(config.getImageSourcePath());
        
        if (images.isEmpty()) {
            throw new IllegalStateException("No PNG images found in source folder: " + config.getImageSourcePath());
        }
        
        // Create Word document with images
        wordDocumentCreator.createDocumentWithImages(images, config.getDocumentPath());
        
        // Move images to destination folder
        imageProcessor.moveImages(images, config.getImageDestinationPath());
    }
}