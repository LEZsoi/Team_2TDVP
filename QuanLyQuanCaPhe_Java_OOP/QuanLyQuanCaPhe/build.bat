@echo off
setlocal
cd /d "%~dp0"
chcp 65001 >nul
if not exist out mkdir out
(for %%f in (src\*.java) do @echo "%%f") > out\sources.txt
where javac >nul 2>nul
if errorlevel 1 (
    java -m jdk.compiler/com.sun.tools.javac.Main -encoding UTF-8 --release 17 -Xlint:all -d out @out\sources.txt
) else (
    javac -encoding UTF-8 --release 17 -Xlint:all -d out @out\sources.txt
)
if errorlevel 1 (
    echo Bien dich that bai. Can JDK 17 tro len va PATH dung.
    exit /b 1
)
echo Bien dich thanh cong.
exit /b 0
