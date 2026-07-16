@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - Location of a JDK home directory
@REM
@REM Optional ENV vars
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM     set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@REM Begin all vars with double underscores to distinguish them from user vars
@setlocal

@IF "%MAVEN_BATCH_ECHO%"=="on" echo %MAVEN_BATCH_ECHO%

@REM set %~dp0 is the directory containing this bat file and target name is mvnw.cmd
@set "PRG=%~dp0"
@set "PRG=%PRG:~0,-1%"

@REM Find the project base directory, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current working directory if not found.

@set "MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%"
@IF NOT "%MAVEN_PROJECTBASEDIR%"=="" goto endDetectBaseDir

@set "EXEC_DIR=%CD%"
@set "WDIR=%EXEC_DIR%"
:findBaseDir
@IF EXIST "%WDIR%"\.mvn goto baseDirFound
@set "WDIR=%WDIR%\.."
@IF NOT "%EXEC_DIR%"=="%WDIR%" goto findBaseDir
@set "WDIR=%EXEC_DIR%"
:baseDirFound
@set "MAVEN_PROJECTBASEDIR=%WDIR%"
:endDetectBaseDir

@IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" (
    goto downloadWrapper
)
@goto runWrapper

:downloadWrapper
@set "DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
@FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties") DO (
    @IF "%%A"=="wrapperUrl" set "DOWNLOAD_URL=%%B"
)
@if "%MVNW_VERBOSE%"=="true" echo Downloading from: %DOWNLOAD_URL%
@powershell -Command "&{"^
  "$webclient = new-object System.Net.WebClient;"^
  "if (-not ([string]::IsNullOrEmpty('%MVNW_USERNAME%') -and [string]::IsNullOrEmpty('%MVNW_PASSWORD%'))) {"^
  "  $webclient.Credentials = new-object System.Net.NetworkCredential('%MVNW_USERNAME%', '%MVNW_PASSWORD%');"^
  "}"^
  "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $webclient.DownloadFile('%DOWNLOAD_URL%', '%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar')"^
  "}"
@if %ERRORLEVEL% NEQ 0 (
  echo Error: Failed to download maven-wrapper.jar
  exit /b %ERRORLEVEL%
)

:runWrapper
@REM ==== START VALIDATION ====
@IF NOT "%JAVA_HOME%"=="" goto OkJHome

@FOR %%i IN (java.exe) DO @SET "JAVA_EXEC=%%~$PATH:i"
@IF NOT "%JAVA_EXEC%"=="" (
    @FOR %%i IN ("%JAVA_EXEC%") DO @SET "JAVA_HOME=%%~dpi.."
    @goto OkJHome
)

@ECHO.
@ECHO Error: JAVA_HOME not found in your environment. >&2
@ECHO Please set the JAVA_HOME variable in your environment to match the >&2
@ECHO location of your Java installation. >&2
@ECHO.
@goto error

:OkJHome
@IF NOT EXIST "%JAVA_HOME%\bin\java.exe" (
    @ECHO.
    @ECHO Error: JAVA_HOME is set to an invalid directory. >&2
    @ECHO JAVA_HOME = "%JAVA_HOME%" >&2
    @ECHO Please set the JAVA_HOME variable in your environment to match the >&2
    @ECHO location of your Java installation. >&2
    @ECHO.
    @goto error
)

@SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
@set "WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
@set "WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain"

"%JAVA_EXE%" %MAVEN_OPTS% %MAVEN_DEBUG_OPTS% -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %WRAPPER_LAUNCHER% %*
@if ERRORLEVEL 1 goto error
@goto end

:error
@exit /b 1

:end
@endlocal
