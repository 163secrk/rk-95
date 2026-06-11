@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.
@REM ----------------------------------------------------------------------------
@echo off
set MAVEN_PROJECTBASEDIR=%~dp0..
set MAVEN_CMD_LINE_ARGS=%*
set EXEC_DIR=%cd%
cd /d "%MAVEN_PROJECTBASEDIR%"
if exist "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" (
    java -jar "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" %MAVEN_CMD_LINE_ARGS%
) else (
    echo Maven wrapper JAR not found. Please run: mvn wrapper:wrapper
)
cd /d "%EXEC_DIR%"
