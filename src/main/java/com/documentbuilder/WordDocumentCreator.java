package com.documentbuilder;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.wp.usermodel.HeaderFooterType;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;


public class WordDocumentCreator {
    
    // Page dimensions in inches (full page width for A4)
    private static final double PAGE_WIDTH_INCHES = 7.0; // Full page width
    private static final double PAGE_HEIGHT_INCHES = 9.2; // Full page height
    private static final double VERTICAL_SPACING_INCHES = 0.3; // Space between images
    
    public void createDocumentWithImages(List<Path> imagePaths, Path documentPath) throws IOException {
        // First pass: calculate total pages needed
        int totalPages = calculateTotalPages(imagePaths);

        try (XWPFDocument document = new XWPFDocument()) {
            double currentPageHeight = 0;
            int currentPage = 1;
            XWPFParagraph currentParagraph = document.createParagraph();
            XWPFRun currentRun = currentParagraph.createRun();

            for (Path imagePath : imagePaths) {
                // Get image dimensions
                BufferedImage originalImage = ImageIO.read(imagePath.toFile());
                if (originalImage == null) {
                    throw new IOException("Could not read image: " + imagePath.getFileName());
                }

                int originalWidth = originalImage.getWidth();
                int originalHeight = originalImage.getHeight();

                // Always scale images to fill the full page width
                double scaleFactor = PAGE_WIDTH_INCHES / (originalWidth / 96.0);
                double scaledWidthInches = PAGE_WIDTH_INCHES;
                double scaledHeightInches = (originalHeight / 96.0) * scaleFactor;

                // For very small original images, use a minimum scale factor
                if (scaleFactor > 3.0) {
                    scaleFactor = 3.0;
                    scaledWidthInches = (originalWidth / 96.0) * scaleFactor;
                    scaledHeightInches = (originalHeight / 96.0) * scaleFactor;
                }

                // Check if image fits on current page
                if (currentPageHeight + scaledHeightInches + VERTICAL_SPACING_INCHES > PAGE_HEIGHT_INCHES) {
                    // Before breaking to a new page, add a centered footer-like paragraph
                    // that shows the page number (static text using computed page indices).
                    XWPFParagraph footerPara = document.createParagraph();
                    footerPara.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun footerRun = footerPara.createRun();
                    footerRun.setText("Page " + currentPage + " / " + totalPages);
                    footerRun.setFontSize(10);

                    // Page break
                    currentParagraph = document.createParagraph();
                    currentRun = currentParagraph.createRun();
                    currentRun.addBreak(); // Page break
                    currentPageHeight = 0;
                    currentPage++;
                } else if (currentPageHeight > 0) {
                    // Add spacing between images
                    currentRun.addBreak();
                    currentPageHeight += VERTICAL_SPACING_INCHES;
                }

                // Add the image
                addImageToRun(currentRun, imagePath, scaledWidthInches, scaledHeightInches);

                // Update current page height
                currentPageHeight += scaledHeightInches;
            }

            // Add footer for final page
            XWPFParagraph footerPara = document.createParagraph();
            footerPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun footerRun = footerPara.createRun();
            footerRun.setText("Page " + currentPage + " / " + totalPages);
            footerRun.setFontSize(10);
            addFooters(document);
            // Save the document
            try (FileOutputStream out = new FileOutputStream(documentPath.toFile())) {
                document.write(out);
            }
        }
    }
    
    private int calculateTotalPages(List<Path> imagePaths) throws IOException {
        int pageCount = 1;
        double currentPageHeight = 0;
        
        for (Path imagePath : imagePaths) {
            BufferedImage originalImage = ImageIO.read(imagePath.toFile());
            if (originalImage == null) {
                throw new IOException("Could not read image: " + imagePath.getFileName());
            }
            
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            
            // Always scale images to fill the full page width
            double scaleFactor = PAGE_WIDTH_INCHES / (originalWidth / 96.0);
            double scaledHeightInches = (originalHeight / 96.0) * scaleFactor;
            
            // For very small original images, use a minimum scale factor
            if (scaleFactor > 3.0) {
                scaleFactor = 3.0;
                scaledHeightInches = (originalHeight / 96.0) * scaleFactor;
            }
            
            // Check if image fits on current page
            if (currentPageHeight + scaledHeightInches + VERTICAL_SPACING_INCHES > PAGE_HEIGHT_INCHES) {
                pageCount++;
                currentPageHeight = 0;
            } else if (currentPageHeight > 0) {
                currentPageHeight += VERTICAL_SPACING_INCHES;
            }
            
            currentPageHeight += scaledHeightInches;
        }
        
        return pageCount;
    }

    private void addFooters(XWPFDocument document) {
        // No-op: page numbers are added inline while creating the document body.
        // Keeping this method to preserve the API and for future extension.
    }

    
    private void addImageToRun(XWPFRun run, Path imagePath, double widthInches, double heightInches) throws IOException {
        try (FileInputStream imageStream = new FileInputStream(imagePath.toFile())) {
            String imageFileName = imagePath.getFileName().toString().toLowerCase();
            
            int pictureType;
            if (imageFileName.endsWith(".png")) {
                pictureType = XWPFDocument.PICTURE_TYPE_PNG;
            } else if (imageFileName.endsWith(".jpg") || imageFileName.endsWith(".jpeg")) {
                pictureType = XWPFDocument.PICTURE_TYPE_JPEG;
            } else if (imageFileName.endsWith(".gif")) {
                pictureType = XWPFDocument.PICTURE_TYPE_GIF;
            } else if (imageFileName.endsWith(".bmp")) {
                pictureType = XWPFDocument.PICTURE_TYPE_BMP;
            } else {
                throw new IOException("Unsupported image format: " + imageFileName);
            }
            
            // Add image at calculated size
            try {
                // Convert inches to points (1 inch = 72 points) before converting to EMU
                double widthPoints = widthInches * 72;
                double heightPoints = heightInches * 72;
                run.addPicture(imageStream, pictureType, imageFileName,
                              Units.toEMU(widthPoints),
                              Units.toEMU(heightPoints));
            } catch (InvalidFormatException e) {
                throw new IOException("Invalid image format for file: " + imageFileName, e);
            }
        }
    }
}