@echo off
chcp 65001 >nul

call mvn clean install -DskipTests -q >nul 2>&1
if %ERRORLEVEL% NEQ 0 ( echo ❌ Build failed. & exit /b %ERRORLEVEL% )

cd examples\Demo
call mvn clean compile dependency:build-classpath -Dmdep.outputFile=cp.txt -DincludeScope=runtime -q >nul 2>&1
if %ERRORLEVEL% NEQ 0 ( echo ❌ Compile failed. & cd ..\.. & exit /b %ERRORLEVEL% )

set /p CP=<cp.txt
java --enable-native-access=ALL-UNNAMED -cp "target\classes;%CP%" fasttui.Window.Overlay %*
pause

cd ..\..
