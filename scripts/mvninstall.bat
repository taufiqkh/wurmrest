@echo off
GOTO START
:USAGE
echo Usage: mvninstall.bat buildId [steamDir]
echo Installs the Wurm Unlimited libraries and non-maven dependencies into the
echo local maven repository.
echo:
echo buildId     build ID of the Wurm Unlimited dedicated server, as found in
echo             your STEAM_DIR\steamapps\common\appmanifest_402370.acf file.
echo steamDir    Steam directory, for example "C:\Program Files (x86)\Steam".
echo             This should be in double quotes.
GOTO END

:START
SETLOCAL
REM Check arguments

REM Strip quotes from arg 2, if it exists
SET ARG_STEAM_DIR=%~2
IF NOT "ARG_STEAM_DIR" == "" set "STEAM_DIR=%ARG_STEAM_DIR%"
IF "%STEAM_DIR%" == "" GOTO ERROR_SET_STEAM_DIR
echo STEAM_DIR set to %STEAM_DIR%

IF "%1" == "" GOTO ERROR_SET_BUILD_ID

set "STEAM_COMMON_DIR=%STEAM_DIR%\steamapps\common"
set "WU_DEDI_DIR=%STEAM_COMMON_DIR%\Wurm Unlimited Dedicated Server"
set WU_VERSION=%1

REM Might be better in a loop but possibly more verbose
call mvn install:install-file "-Dfile=%WU_DEDI_DIR%\common.jar" -DgroupId=com.wurmunlimited -DartifactId=WurmCommon -Dversion=%WU_VERSION% -Dpackaging=jar
call mvn install:install-file "-Dfile=%WU_DEDI_DIR%\server.jar" -DgroupId=com.wurmunlimited -DartifactId=WurmServer -Dversion=%WU_VERSION% -Dpackaging=jar
call mvn install:install-file "-Dfile=%WU_DEDI_DIR%\lib\controlsfx-8.20.8.jar" -DgroupId=controlsfx -DartifactId=controlsfx -Dversion=8.20.8 -Dpackaging=jar
call mvn install:install-file "-Dfile=%WU_DEDI_DIR%\lib\jtwitter.jar" -DgroupId=winterwell -DartifactId=jtwitter -Dversion=2.8.5 -Dpackaging=jar
call mvn install:install-file "-Dfile=%WU_DEDI_DIR%\lib\mail.jar" -DgroupId=com.sun -DartifactId=JavaMail -Dversion=1.4 -Dpackaging=jar

GOTO END

:ERROR_SET_BUILD_ID
echo Please pass in the build id in %STEAM_DIR%\steamapps\appmanifest_402370.acf
GOTO USAGE

:ERROR_SET_STEAM_DIR
echo You need to set STEAM_DIR or specify a directory as an argument
GOTO USAGE

ENDLOCAL
:END
