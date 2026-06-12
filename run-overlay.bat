@echo off
chcp 65001 >nul
cls

echo ⚡ Building Main Project...
call mvn clean install -DskipTests -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Build failed. & pause & exit /b %ERRORLEVEL% )

echo 🛠  Compiling Overlay Demo...
cd examples\Demo
call mvn clean compile dependency:build-classpath -Dmdep.outputFile=cp.txt -DincludeScope=runtime -q
if %ERRORLEVEL% NEQ 0 ( echo ❌ Compile failed. & pause & exit /b %ERRORLEVEL% )

echo 🚀 Running Overlay Demo...
set /p CP=<cp.txt
java --enable-native-access=ALL-UNNAMED -cp "target\classes;%CP%" fasttui.Overlay

cd ..\..
pause
