# Document Builder Application

A Java application that processes PNG images from a source folder, creates a Word document containing all images in creation order, moves the processed images to a destination folder, and saves the Word document to another folder.

## Requirements

- JDK 25
- Gradle

## Usage

### Command Line Usage

```bash
java -jar document-builder.jar <p1> <p2> <p3> <fileName>
```

**Parameters:**
- `p1`: Source folder containing PNG images
- `p2`: Destination folder for processed images
- `p3`: Destination folder for the Word document
- `fileName`: Name of the output Word document (without .docx extension)

**Example:**
```bash
java -jar document-builder.jar ./images ./processed ./docs myDocument
```

### Building the Application

```bash
# Build the application
gradle build

# Create a fat JAR with all dependencies
gradle shadowJar

# Run the application directly with Gradle
gradle run --args="./images ./processed ./docs myDocument"
```

### Running Tests

```bash
gradle test
```

## Features

- **Image Processing**: Automatically finds all PNG images in the source folder
- **Creation Order**: Sorts images by creation time (falls back to modification time if creation time is unavailable)
- **Word Document Creation**: Creates a professional Word document with each image on its own page
- **File Management**: Moves processed images to destination folder and saves document to specified location
- **Error Handling**: Comprehensive validation and error reporting
- **Configuration Validation**: Validates folder permissions and file paths before processing

## Example Workflow

1. Place PNG images in `./images` folder
2. Run the application:
   ```bash
   java -jar document-builder.jar ./images ./processed ./docs myReport
   ```
3. The application will:
   - Create `myReport.docx` in `./docs` folder with all images
   - Move all PNG files from `./images` to `./processed` folder
   - Display success message with file paths

## Project Structure

```
src/main/java/com/documentbuilder/
├── DocumentBuilderApp.java      # Main application entry point
├── AppConfig.java               # Configuration management
├── DocumentProcessor.java       # Main processing logic
├── ImageProcessor.java          # Image handling and sorting
└── WordDocumentCreator.java     # Word document creation

src/test/java/com/documentbuilder/
└── DocumentBuilderTest.java     # Unit tests
```

## Error Handling

The application provides clear error messages for common issues:
- Missing or invalid folder paths
- Insufficient file permissions
- No PNG images found in source folder
- Invalid file names
- Image format issues# DocumentBuilder
# DocumentBuilder
