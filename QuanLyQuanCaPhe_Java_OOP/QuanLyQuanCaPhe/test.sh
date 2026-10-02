#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
sh build.sh
exec java -Dfile.encoding=UTF-8 -cp out KiemThu
