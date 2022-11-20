#!/bin/bash
set -euo pipefail # Unofficial strict mode, see http://redsymbol.net/articles/unofficial-bash-strict-mode/

cd "$(dirname "$(readlink -f "$0")")"
# see https://stackoverflow.com/questions/3349105/how-can-i-set-the-current-working-directory-to-the-directory-of-the-script-in-ba

# jvm is taken from https://adoptium.net/releases.html?variant=openjdk11&jvmVariant=hotspot
JVM_NAME="jdk-11.0.13+8-jre"

# Creating folder structure
mkdir -p build/jre

# Downloading client to the target directory
#wget https://www.sheepit-renderfarm.com/media/applet/client-latest.php -O ./build/jre/sheepit-client.jar
#get the new file
cp ../build/libs/sheepit-client-all.jar ./build/jre/sheepit-client.jar

# Unzipping jre
unzip "$JVM_NAME".zip -d ./build/jre/  # Unzip JRE to be packaged
mv ./build/jre/"$JVM_NAME"/* ./build/jre/
rm -d ./build/jre/"$JVM_NAME"

# Compressing app package
cd build/jre
7zr a -mx=9 ../application.7z ./

# Building the exe bundle and cleaning up
cd ../..
cat starter.sfx config.cfg ./build/application.7z > sheepit-wrapper.exe
rm -rf build/application.7z build/jre
echo "Build of sheepit-wrapper.exe successful"
