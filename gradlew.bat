@echo off
REM Simple wrapper - requires Gradle installed OR Android Studio
REM If you have Android Studio, just open src/taarof as project and press Run.

if exist "%LOCALAPPDATA%\Android\Sdk" (
  echo SDK found at %LOCALAPPDATA%\Android\Sdk
) else (
  echo WARNING: Android SDK not found
)

where gradle >nul 2>nul
if %ERRORLEVEL%==0 (
  gradle assembleDebug
) else (
  echo.
  echo [TaarofBattle] Gradle not found in PATH.
  echo Easiest way:
  echo   1. Open Android Studio
  echo   2. Open folder: src/taarof
  echo   3. Let it sync, then Run ^> Run app  (or Build ^> Build APK)
  echo.
  echo Or install Gradle 8.7 from https://gradle.org/install/ then run: gradle assembleDebug
)
