#!/bin/bash
set -euo pipefail # Unofficial strict mode, see http://redsymbol.net/articles/unofficial-bash-strict-mode/

cd "$(dirname "$(readlink -f "$0")")"
# see https://stackoverflow.com/questions/3349105/how-can-i-set-the-current-working-directory-to-the-directory-of-the-script-in-ba

ARCH="$1"
JRETAR="$2"

APP="SheepIt"
DMG="$APP-$ARCH-raw.dmg"
FINAL="$APP-$ARCH.dmg"

BASE="$(pwd)"
BUILD="$BASE/build"
RES="$BUILD/SheepIt.app/Contents/Resources"
CL="$RES/client"

apt-get update
apt-get install -y --no-install-recommends genisoimage  # Add dependencies

mkdir "$BUILD"
tar -axf "$APP.tar.gz" -C "$BUILD"  # Extract base file structure + Platypus shell exec shim
tar -axf "$BASE/$JRETAR" -C "$RES"  # Extracts the JRE into Resources folder

mkdir -p "$CL"
cp ../sheepit-client-all.jar "$CL/$APP.jar"  # Copy client artifact to be packaged
cp "$BASE/$APP-Run.sh" "$CL"
cp "$BASE/$APP.png" "$CL"

cd "$CL"
ln -s "$(find ../ -name java)" $APP  # We symlink java to make the title appearing in the dock to read "SheepIt", not "java"
cd "$BASE"

genisoimage -D -V $APP -no-pad -r -apple -o "$DMG" "$BUILD"
./dmg "$DMG" "$FINAL"  # Use libdmg based dmg tool to compress the dmg (and also make it read-only)
