#!/usr/bin/env bash
# Core-only Replay/Command; JDK 17, java.base, zero writes inside the repository.
set -Eeuo pipefail
export LC_ALL=C.UTF-8
ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd -P)"
JDK="${JAVA_HOME:-/usr/lib/jvm/java-17-openjdk-amd64}"
JAVAC="$JDK/bin/javac"
JAVA="$JDK/bin/java"
[[ -x "$JAVAC" && -x "$JAVA" ]] || { echo 'BLOCKED_JDK17_MISSING' >&2; exit 1; }
[[ "$("$JAVAC" -version 2>&1)" == javac\ 17* ]] || { echo 'BLOCKED_WRONG_JAVAC' >&2; exit 1; }
[[ "$("$JAVA" -version 2>&1 | head -1)" == *'"17'* ]] || { echo 'BLOCKED_WRONG_JAVA' >&2; exit 1; }
TEST="src/test/java/ar/edu/uade/sokoban/replay/ReplayIntegrationTest.java"
[[ -f "$ROOT/$TEST" ]] || { echo 'BLOCKED_REPLAY_TEST_MISSING' >&2; exit 1; }
mapfile -d '' SOURCES < <(cd "$ROOT" && find src/main/java -type f -name '*.java' -print0)
(( ${#SOURCES[@]} > 0 )) || { echo 'BLOCKED_NO_CORE_SOURCES' >&2; exit 1; }
SCRATCH="$(mktemp -d "${TMPDIR:-/tmp}/forcepush-replay-i3d1.XXXXXXXX")"
trap 'find "$SCRATCH" -type f -delete; find "$SCRATCH" -depth -type d -empty -delete' EXIT
mkdir -p "$SCRATCH/classes"
(
    cd "$ROOT"
    "$JAVAC" --release 17 --limit-modules java.base -encoding UTF-8 -Xlint:all -Werror \
        -d "$SCRATCH/classes" "${SOURCES[@]}" "$TEST"
)
"$JAVA" --limit-modules java.base -cp "$SCRATCH/classes" \
    ar.edu.uade.sokoban.replay.ReplayIntegrationTest
