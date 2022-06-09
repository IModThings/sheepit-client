#!/bin/bash
set -euo pipefail # Unofficial strict mode, see http://redsymbol.net/articles/unofficial-bash-strict-mode/

cd "$(dirname "$(readlink -f "$0")")"
# see https://stackoverflow.com/questions/3349105/how-can-i-set-the-current-working-directory-to-the-directory-of-the-script-in-ba
echo "Please only pin the first window - the launcher to the Dock"
echo "============================="
echo "Loading and launching SheepIt"
nohup "$BIN" -Xdock:icon=./$APP.png -Djna.nosys=true -jar "$JAR" &>/dev/null & disown
osascript -e 'tell application "Terminal" to close (every window whose name contains "SheepIt-Run.sh")' &
exit 0
