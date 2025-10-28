package com.documentbuilder;

import java.nio.file.Path;
import java.util.List;

public class DocumentBuilderApp {

    public static void main(String[] args) {
        try {
//            if (args.length != 2) {
//                args = new String[]{
//                        "sample_images",
//                        "processed_images",
//                        "output_docs",
//                        "testWord"
//                };
//            }
            // Load configuration
            if (args.length == 0) {
                // Demo mode - create a simple test document
                System.out.println("Running in demo mode - creating test document...");
                createTestDocument();
                return;
            }

            AppConfig config = AppConfig.loadFromArgs(args);

            // Validate configuration
            config.validate();

            // Process images and create document
            DocumentProcessor processor = new DocumentProcessor(config);
            processor.process();

            System.out.println("Document created successfully!");
            System.out.println("Images moved to: " + config.getImageDestinationPath());
            System.out.println("Document saved to: " + config.getDocumentDestinationPath());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            printUsage();
            System.exit(1);
        }
    }

    private static void createTestDocument() {
        try {
            // Create a simple test document to demonstrate functionality
            WordDocumentCreator creator = new WordDocumentCreator();
            java.nio.file.Path docPath = java.nio.file.Path.of("./output_docs/test_demo.docx");
            // Create an empty list since we don't have images for demo
            java.util.List<java.nio.file.Path> emptyList = java.util.Collections.emptyList();
            creator.createDocumentWithImages(emptyList, docPath);
            System.out.println("Demo document created successfully at: ./output_docs/test_demo.docx");
            System.out.println("Note: Document created without images (demo mode)");
        } catch (Exception e) {
            System.err.println("Error creating demo document: " + e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar document-builder.jar <p1> <p2> <p3> <fileName>");
        System.out.println("  p1: Source folder containing PNG images");
        System.out.println("  p2: Destination folder for processed images");
        System.out.println("  p3: Destination folder for the Word document");
        System.out.println("  fileName: Name of the output Word document (without extension)");
        System.out.println();
        System.out.println("Example: java -jar document-builder.jar ./images ./processed ./docs myDocument");
    }
}