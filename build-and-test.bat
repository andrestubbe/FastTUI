@echo off
chcp 65001 >nul
echo =========================================
echo  Building FastTUI & Updating Local Maven  
echo =========================================

cd /d "c:\Users\andre\Documents\2026-06-14-Work-FastJava\FastTUI"
call mvn clean install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ❌ FastTUI build/install failed.
    exit /b %ERRORLEVEL%
)

echo.
echo =========================================
echo  Compiling CreamCLI with new FastTUI      
echo =========================================

cd /d "c:\Users\andre\Documents\2026-07-16-Work-Cream\2026-07-16-Work-CreamCLI"
call mvn clean compile dependency:build-classpath "-Dmdep.outputFile=cp.txt" "-DincludeScope=runtime" -q
if %ERRORLEVEL% NEQ 0 (
    echo ❌ CreamCLI compilation failed.
    exit /b %ERRORLEVEL%
)

echo.
echo ✅ FastTUI installed to local Maven repo and CreamCLI recompiled successfully!
