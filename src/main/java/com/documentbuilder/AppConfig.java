package com.documentbuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppConfig {
    private final Path imageSourcePath;
    private final Path imageDestinationPath;
    private final Path documentDestinationPath;
    private final String fileName;
    
    public AppConfig(Path imageSourcePath, Path imageDestinationPath, 
                    Path documentDestinationPath, String fileName) {
        this.imageSourcePath = imageSourcePath;
        this.imageDestinationPath = imageDestinationPath;
        this.documentDestinationPath = documentDestinationPath;
        this.fileName = fileName;
    }
    
    public static AppConfig loadFromArgs(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("Expected 4 arguments: p1 p2 p3 fileName");
        }
        
        Path p1 = Paths.get(args[0]);
        Path p2 = Paths.get(args[1]);
        Path p3 = Paths.get(args[2]);
        String fileName = args[3];
        
        return new AppConfig(p1, p2, p3, fileName);
    }
    
    public void validate() {
        // Validate source folder exists
        if (!Files.exists(imageSourcePath) || !Files.isDirectory(imageSourcePath)) {
            throw new IllegalArgumentException("Source folder does not exist or is not a directory: " + imageSourcePath);
        }
        
        // Validate source folder is readable
        if (!Files.isReadable(imageSourcePath)) {
            throw new IllegalArgumentException("Source folder is not readable: " + imageSourcePath);
        }
        
        // Create destination folders if they don't exist
        try {
            Files.createDirectories(imageDestinationPath);
            Files.createDirectories(documentDestinationPath);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create destination folders: " + e.getMessage());
        }
        
        // Validate destination folders are writable
        if (!Files.isWritable(imageDestinationPath)) {
            throw new IllegalArgumentException("Image destination folder is not writable: " + imageDestinationPath);
        }
        
//        if (!Files.isWritable(documentDestinationPath)) {
//            throw new IllegalArgumentException("Document destination folder is not writable: " + documentDestinationPath);
//        }
        
        // Validate file name
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }
        
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }
    }
    
    // Getters
    public Path getImageSourcePath() {
        return imageSourcePath;
    }
    
    public Path getImageDestinationPath() {
        return imageDestinationPath;
    }
    
    public Path getDocumentDestinationPath() {
        return documentDestinationPath;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public Path getDocumentPath() {
        return documentDestinationPath.resolve(fileName + ".docx");
    }
}