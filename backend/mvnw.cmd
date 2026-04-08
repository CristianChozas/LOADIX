@echo off
setlocal

if "%JAVA_HOME%"=="" (
  echo.
  echo Error: JAVA_HOME not found in your environment.
  echo Please set the JAVA_HOME variable in your environment to match the location of your Java installation.
  echo.
  exit /b 1
)

if not exist "%JAVA_HOME%\bin\java.exe" (
  echo.
  echo Error: JAVA_HOME is set to an invalid directory.
  echo JAVA_HOME = "%JAVA_HOME%"
  echo Please set the JAVA_HOME variable in your environment to match the location of your Java installation.
  echo.
  exit /b 1
)

set MAVEN_PROJECTBASEDIR=%~dp0
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

set WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
set WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.4/maven-wrapper-3.3.4.jar

for /f "usebackq tokens=1,2 delims==" %%A in ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties") do (
  if "%%A"=="wrapperUrl" set WRAPPER_URL=%%B
)

if not exist "%WRAPPER_JAR%" (
  powershell -Command "&{$ProgressPreference='SilentlyContinue'; [Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile('%WRAPPER_URL%', '%WRAPPER_JAR%')}"
)

"%JAVA_HOME%\bin\java.exe" -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*
exit /b %ERRORLEVEL%
