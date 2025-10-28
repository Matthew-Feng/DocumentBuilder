@echo off
REM Launcher for DocumentBuilder.
REM This launcher prefers a fat (uber) JAR if present: build\libs\document-builder-1.0.0-all.jar
REM Usage: run.bat <imageSource> <imageDestination> <documentDestination> <fileName>

setlocal enabledelayedexpansion
set "JAR_ALL=%~dp0build\libs\document-builder-1.0.0-all.jar"
set "JAR=%~dp0build\libs\document-builder-1.0.0.jar"

if exist "%JAR_ALL%" (
  echo Running fat jar: %JAR_ALL%
  java -jar "%JAR_ALL%" %*
  if "%ERRORLEVEL%"=="0" (
    endlocal
    exit /b 0
  )
  echo.
  echo "java -jar" failed (exit code %ERRORLEVEL%) when running fat jar. Trying plain jar...
)

if exist "%JAR%" (
  echo Running jar: %JAR%
  java -jar "%JAR%" %*
  if "%ERRORLEVEL%"=="0" (
    endlocal
    exit /b 0
  )
  echo.
  echo "java -jar" failed (exit code %ERRORLEVEL%) when running jar. Attempting to run via Gradle to include dependencies...
) else (
  echo No jar found in build\libs.
)

REM Fall back to Gradle run (uses runtime classpath with dependencies)
if exist "%~dp0gradlew.bat" (
  call "%~dp0gradlew.bat" run --args="%*"
  endlocal
  exit /b %ERRORLEVEL%
) else (
  echo gradlew.bat not found in %~dp0. Please build the project with gradlew.bat build or run from an environment that includes dependencies.
  endlocal
  exit /b 1
)