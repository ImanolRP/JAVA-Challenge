@echo off
setlocal

REM 1) Environment variables only for this execution
set "JAVA_HOME=J:\app\jdk\jdk-21.0.11"
set "MAVEN_HOME=J:\app\lib\apache-maven-3.9.8"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

REM 2) Navigate to the project root
for %%I in ("%~dp0..\..") do set "PROJECT_ROOT=%%~fI"
cd /d "%PROJECT_ROOT%"
if errorlevel 1 (
    echo [ERROR] Could not access %PROJECT_ROOT%
    exit /b 1
)

REM 3) Build the project and generate the .jar
call "%MAVEN_HOME%\bin\mvn.cmd" clean install -DskipTests
if errorlevel 1 (
    echo [ERROR] Maven failed to build/package the project.
    exit /b 1
)

REM 4) Go to to_deploy
cd /d "%PROJECT_ROOT%\to_deploy"
if errorlevel 1 (
    echo [ERROR] Could not access %PROJECT_ROOT%\to_deploy
    exit /b 1
)

set "JAR_FILE=comercia-challenge.jar"
if not exist "%JAR_FILE%" (
    echo [ERROR] %JAR_FILE% was not found in %CD%
    exit /b 1
)

REM 5) Run the .jar (uses the Java 21 defined above)
if /I "%~1"=="--skip-run" (
    echo [INFO] Build completed. Execution skipped by --skip-run.
    exit /b 0
)

call "%JAVA_HOME%\bin\java.exe" -jar "%JAR_FILE%"

