#!/usr/bin/env bash
# Optional Linux/macOS development runner, no build tool. Java 17+.
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
mkdir -p "$TMP/classes"
find "$ROOT/src/main/java" "$ROOT/src/desktop/java" "$ROOT/src/desktop-test/java" -name '*.java' -type f | sort > "$TMP/sources"
javac --release 17 --limit-modules java.base,java.desktop -encoding UTF-8 -Xlint:all -Werror -d "$TMP/classes" @"$TMP/sources"
CP="$TMP/classes:$ROOT/src/desktop/resources"
if [[ "${1:-}" == "--test" ]]; then
    java -Dfile.encoding=UTF-8 -Djava.awt.headless=true --limit-modules java.base,java.desktop -cp "$CP" ar.edu.uade.sokoban.desktop.DesktopIntegrationTest "$ROOT/src/desktop/resources/levels/demo.txt"
    (cd "$ROOT" && java -Dfile.encoding=UTF-8 -Djava.awt.headless=true --limit-modules java.base,java.desktop -cp "$CP" ar.edu.uade.sokoban.desktop.DesktopVisualAssetsTest)
elif [[ "${1:-}" == "--ui-test" ]]; then
    (cd "$ROOT" && java -Dfile.encoding=UTF-8 --limit-modules java.base,java.desktop -Dforcepush.repo.root="$ROOT" -cp "$CP" ar.edu.uade.sokoban.desktop.DesktopUiSmokeTest "${@:2}")
else
    java -Dfile.encoding=UTF-8 --limit-modules java.base,java.desktop -Dforcepush.repo.root="$ROOT" -cp "$CP" ar.edu.uade.sokoban.desktop.SwingApplication "${@:1}"
fi
