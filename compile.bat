@echo off
echo Compiling Document Builder Application...

REM Create output directories
if not exist "build\classes" mkdir "build\classes"
if not exist "build\lib" mkdir "build\lib"

REM Download dependencies
echo Downloading dependencies...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/apache/poi/poi/5.2.5/poi-5.2.5.jar' -OutFile 'build\lib\poi-5.2.5.jar'"
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml/5.2.5/poi-ooxml-5.2.5.jar' -OutFile 'build\lib\poi-ooxml-5.2.5.jar'"
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml-full/5.2.5/poi-ooxml-full-5.2.5.jar' -OutFile 'build\lib\poi-ooxml-full-5.2.5.jar'"
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.23.1/log4j-core-2.23.1.jar' -OutFile 'build\lib\log4j-core-2.23.1.jar'"
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-api/2.23.1/log4j-api-2.23.1.jar' -OutFile 'build\lib\log4j-api-2.23.1.jar'"

REM Compile Java source files
echo Compiling source code...
javac -cp "build\lib\*" -d build\classes src\main\java\com\documentbuilder\*.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    exit /b 1
)

echo Compilation successful!

REM Create JAR file
echo Creating JAR file...
jar cfm build\document-builder.jar MANIFEST.MF -C build\classes .

echo Build completed successfully!
echo.
echo To run the application:
echo   java -jar build\document-builder.jar ^<p1^> ^<p2^> ^<p3^> ^<fileName^>
echo.
echo Example:
echo   java -jar build\document-builder.jar ./images ./processed ./docs myDocument