#!/bin/bash
set -euo pipefail # Unofficial strict mode, see http://redsymbol.net/articles/unofficial-bash-strict-mode/

cd "${0%/*}"
# see https://stackoverflow.com/questions/3349105/how-can-i-set-the-current-working-directory-to-the-directory-of-the-script-in-ba

# This is bash on macOS, some stuff works different
BIN=""
APP="SheepIt"
LAUDIR="$HOME/Library/Application Support/SheepIt Launcher"
ARCH="$(uname -m)"
if [ "$ARCH" = "x86_64" ]; then
    REPO="https://api.github.com/repos/adoptium/temurin11-binaries/releases"
    JREDISTRO="OpenJDK11U-jre_x64_mac_hotspot"
elif [ "$ARCH" = "arm64" ]; then
    REPO="https://api.github.com/repos/adoptium/temurin17-binaries/releases"
    JREDISTRO="OpenJDK17U-jre_aarch64_mac_hotspot"
else
    echo "What the fudge, what kind of Mac are you running ths under, PowerPC? Get outta here!"
    exit 1
fi
echo "NOTICE:"
echo "Please only pin the first window - the launcher to the Dock"
if [ ! -e "./$APP" ]; then #If JRE exists in the dmg, don't download it
    mkdir -p "$LAUDIR"
    BIN="$LAUDIR/$APP"
    JREVERSION="$LAUDIR/JRE-Version"
    echo "Checking for JRE updates"
    JRE_LATEST="$(curl --connect-timeout 20 --retry 4 --silent --show-error $REPO | grep $JREDISTRO | grep name | grep tar | grep -v json | grep -v txt | cut -d '"' -f 4 | head -n 1)"
    if [ -z "$JRE_LATEST" ]; then
        # Empty JRE_LATEST is a good indicator of a critical failure
        echo "!!! Failed parsing JRE information! Aborting !!!"
        echo "Possible causes and troubleshooting steps:"
        echo "1. Check for internet connectivity \(Routes, DNS, Proxy\)"
        echo "2. Github or the JRE repository may be down/unavailable."
        echo "   This might require a new version of the launcher from the SheepIt website: https://www.sheepit-renderfarm.com/"
        echo "3. Open an issue on GitLab/Ask on the Discord if problems persists after 1. and 2."
        exit 4
    elif [[ ! -f "$JREVERSION" || "$JRE_LATEST" != $(cat "$JREVERSION") ]]; then
        echo "Updates detected, updating JRE to $JRE_LATEST"
        rm -rf "$LAUDIR/jdk*" "$BIN" || true # The "|| true" is that we expect the command to fail (If there is nothing to remove), needed since we are running in strict mode
        echo "$JRE_LATEST" > "$JREVERSION"
        curl --connect-timeout 20 --retry 4 -# -L -o "$LAUDIR/$JRE_LATEST" "$(curl --connect-timeout 20 --retry 4 -s $REPO | grep $JREDISTRO | grep browser_download_url | grep tar | grep -v json | grep -v txt | cut -d '"' -f 4 | head -n 1)"
        tar -xf "$LAUDIR/$JRE_LATEST" -C "$LAUDIR"
        rm "$LAUDIR/$JRE_LATEST"
        ln -s "$(find "$LAUDIR" -name java)" "$LAUDIR/$APP" # We symlink java to make the title appearing in the dock to read "SheepIt", not "java"
    else
        echo "No JRE updates detected"
    fi
else
    BIN="./$APP"
fi
JAR="$APP.jar"
if [  ! -e ./$JAR  ]; then #If Jar exists in the dmg, don't download it
    URL=https://www.sheepit-renderfarm.com/media/applet/client-latest.php
    if [ -e "$LAUDIR/JOINBETA" ]; then
        URL=https://www.sheepit-renderfarm.com/media/applet/client-beta.php
    fi
    echo "Checking for $APP Jar updates"
    JAR_LATEST="$(curl --connect-timeout 20 --retry 4 --silent --show-error --head $URL | grep -i content-disposition | cut -d '=' -f 2 | tr -ud '\r ')"
    JAR="$LAUDIR/$JAR_LATEST"
        if [ -z "$JAR_LATEST" ]; then
            # Empty JAR_LATEST is a good indicator of a critical failure
            echo "!!! Failed parsing $APP Jar information! Aborting !!!"
            echo "Possible causes and troubleshooting steps:"
            echo "1. Check for internet connectivity \(Routes, DNS, Proxy\)"
            echo "2. Check the status of SheepIt via the SheepIt website: https://www.sheepit-renderfarm.com/"
            echo "3. Open an issue on GitLab/Ask on the Discord if problems persists after 1. and 2."
            exit 2
        elif [ ! -e "$JAR" ]; then
            echo "Updates detected, updating $APP Jar to $JAR_LATEST"
            rm -f "$LAUDIR/sheepit-client*.jar" || true # The "|| true" is that we expect the command to fail (If there is nothing to remove), needed since we are running in strict mode
            curl --connect-timeout 20 --retry 4 -# -L -o "$JAR" "$URL"
        else
            echo "No $APP Jar updates found"
        fi
else
    JAR="./$JAR"
fi

echo "============================="
echo "Loading and launching SheepIt"
nohup "$BIN" -Xdock:icon=./$APP.png -Djna.nosys=true -jar "$JAR" &>/dev/null & disown
osascript -e 'tell application "Terminal" to close (every window whose name contains "SheepIt-Run.sh")' &
exit 0
