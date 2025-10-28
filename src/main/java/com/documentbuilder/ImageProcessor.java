package com.documentbuilder;
 
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
 
public class ImageProcessor {
    
    public List<Path> getSortedPngImages(Path sourcePath) throws IOException {
        List<Path> imageFiles = new ArrayList<>();
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcePath, "*.png")) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    imageFiles.add(entry);
                }
            }
        }
        
        // Sort by creation time (if available) or by filename
        imageFiles.sort(Comparator.comparingLong(this::getCreationTime));
        
        return imageFiles;
    }
    
    private long getCreationTime(Path path) {
        try {
            Object attr = Files.getAttribute(path, "creationTime");
            if (attr != null) {
                return ((java.nio.file.attribute.FileTime) attr).toMillis();
            } else {
                return Files.getLastModifiedTime(path).toMillis();
            }
        } catch (IOException e) {
            return Files.exists(path) ? 0 : Long.MAX_VALUE;
        }
    }
    
    public void moveImages(List<Path> images, Path destinationPath) throws IOException {
        for (Path image : images) {
            Path destinationFile = destinationPath.resolve(image.getFileName());
            Files.move(image, destinationFile);
        }
    }
    
    public boolean hasPngImages(Path sourcePath) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcePath, "*.png")) {
            return stream.iterator().hasNext();
        }
    }
}