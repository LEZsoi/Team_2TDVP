@echo off
setlocal
cd /d "%~dp0"
call build.bat
if errorlevel 1 (
    pause
    exit /b 1
)
java -Dfile.encoding=UTF-8 -cp out KiemThu
set "test_result=%errorlevel%"
pause
exit /b %test_result%
