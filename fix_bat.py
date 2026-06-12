import os
import shutil
from pathlib import Path

base_dir = Path(r'C:\Users\andre\Documents\2026-05-17-Work-FastJava\FastTUI')

# Delete FastTerminal Engine stuff from FastTUI
demoscene_dir = base_dir / 'examples/Demo/src/main/java/fasttui/demoscene'
if demoscene_dir.exists():
    shutil.rmtree(str(demoscene_dir))

run_demoscene = base_dir / 'examples/Demo/src/main/java/fasttui/RunDemoscene.java'
if run_demoscene.exists():
    run_demoscene.unlink()

# Delete redundant or broken bat files
to_delete = ['run-ui.bat', 'run-gradient-swing.bat']
for f in to_delete:
    p = base_dir / f
    if p.exists():
        p.unlink()

# Template for typical bat files
bat_template = '''@echo off\r
chcp 65001 >nul\r
cls\r
\r
echo ⚡ Building Main Project...\r
call mvn clean install -DskipTests -q\r
if %ERRORLEVEL% NEQ 0 ( echo ❌ Build failed. & pause & exit /b %ERRORLEVEL% )\r
\r
echo 🛠  Compiling {name}...\r
cd {cd_dir}\r
call mvn clean compile dependency:build-classpath -Dmdep.outputFile=cp.txt -DincludeScope=runtime -q\r
if %ERRORLEVEL% NEQ 0 ( echo ❌ Compile failed. & pause & exit /b %ERRORLEVEL% )\r
\r
echo 🚀 Running {name}...\r
set /p CP=<cp.txt\r
java --enable-native-access=ALL-UNNAMED -cp "target\\classes;%CP%" {main_class}\r
\r
cd ..\\..\r
pause\r
'''

def write_bat(filename, name, cd_dir, main_class):
    content = bat_template.format(name=name, cd_dir=cd_dir, main_class=main_class)
    p = base_dir / filename
    p.write_bytes(content.encode('utf-8'))
    print(f'Wrote {filename}')

write_bat('run-demo.bat', 'UI Demo', r'examples\Demo', 'fasttui.Demo')
write_bat('run-overlay.bat', 'Overlay Demo', r'examples\Demo', 'fasttui.Overlay')
write_bat('run-palette.bat', 'Palette Demo', r'examples\Palette', 'fasttui.RunPalette')
write_bat('run-gradient.bat', 'Gradient Demo', r'examples\Palette', 'fasttui.RunGradient')

# Benchmark has a different format
bench_bat = '''@echo off\r
chcp 65001 >nul\r
cls\r
\r
echo ⚡ Building Main Project...\r
call mvn clean install -DskipTests -q\r
if %ERRORLEVEL% NEQ 0 ( echo ❌ Build failed. & pause & exit /b %ERRORLEVEL% )\r
\r
echo 🛠  Compiling Benchmark...\r
cd examples\\Benchmark\r
call mvn clean package -DskipTests -q\r
if %ERRORLEVEL% NEQ 0 ( echo ❌ Benchmark build failed. & pause & exit /b %ERRORLEVEL% )\r
\r
echo 🚀 Running Benchmark...\r
java -jar target\\benchmarks.jar -v EXTRA 2>nul\r
\r
cd ..\\..\r
pause\r
'''
bench_p = base_dir / 'run-benchmark.bat'
bench_p.write_bytes(bench_bat.encode('utf-8'))
print('Wrote run-benchmark.bat')
