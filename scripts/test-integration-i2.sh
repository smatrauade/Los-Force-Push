#!/usr/bin/env bash
set -Eeuo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"
compiler="$(readlink -f "$(command -v javac)")"
runtime="$(dirname "$compiler")/java"
[[ "$("$compiler" -version 2>&1)" == javac\ 17* ]]
[[ "$("$runtime" -version 2>&1 | head -1)" == *'"17'* ]]
scratch="$(mktemp -d "${TMPDIR:-/tmp}/forcepush-i2.XXXXXXXX")"
trap 'find "$scratch" -type f -delete; find "$scratch" -depth -type d -empty -delete' EXIT
find "$root/src/main/java" "$root/src/test/java" -type f -name '*.java' | LC_ALL=C sort > "$scratch/sources"
"$compiler" --release 17 --limit-modules java.base -encoding UTF-8 -Xlint:all -Werror -d "$scratch" "@$scratch/sources"
"$runtime" --limit-modules java.base -cp "$scratch" ar.edu.uade.sokoban.session.GameSessionUndoTest
(cd "$root" && bash scripts/test-integration-i1.sh)
