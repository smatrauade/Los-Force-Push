#!/usr/bin/env bash
# Bash 3.2+; compilation and mutable fixtures stay in an owned temporary directory.
set -euo pipefail
include_movement=0
if [[ "$#" -eq 1 && "$1" == --movement ]]; then
    include_movement=1
elif [[ "$#" -ne 0 ]]; then
    printf 'Usage: bash test-level-loading.sh [--movement]\n' >&2
    exit 2
fi
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"
selected_jdk="${SPIKE_JDK_HOME:-${JAVA_HOME:-}}"
if [[ -n "$selected_jdk" ]]; then
    compiler="$selected_jdk/bin/javac"
    runtime="$selected_jdk/bin/java"
else
    compiler="$(command -v javac)"
    runtime="$(command -v java)"
fi
[[ -x "$compiler" && -x "$runtime" ]] || {
    printf 'A complete JDK is required (SPIKE_JDK_HOME, JAVA_HOME or PATH).\n' >&2
    exit 1
}
"$compiler" -version
"$runtime" -version
temp_parent="$(cd "${TMPDIR:-/tmp}" && pwd -P)"
case "$temp_parent/" in
    "$root/"*) printf 'Temporary directory must be outside the checkout.\n' >&2; exit 1 ;;
esac
spike_run_dir="$(mktemp -d "$temp_parent/sokoban-level-loading.XXXXXXXX")"
cleanup() {
    # Only this invocation's generated directory; no preexisting paths or symlinks.
    find "$spike_run_dir" -type f -delete
    find "$spike_run_dir" -depth -type d -empty -delete
}
trap cleanup EXIT
mkdir "$spike_run_dir/classes"
cd "$root"
# Relative source paths remain independent of spaces/Unicode in the checkout path.
find src/main/java src/test/java -name '*.java' -type f | LC_ALL=C sort > "$spike_run_dir/sources"
"$compiler" --release 17 --limit-modules java.base -encoding UTF-8 -Xlint:all -Werror \
    -d "$spike_run_dir/classes" @"$spike_run_dir/sources"
"$runtime" --limit-modules java.base -Djava.io.tmpdir="$spike_run_dir" \
    -cp "$spike_run_dir/classes" ar.edu.uade.sokoban.level.LevelLoadingTest \
    "$root/src/test/resources/levels"
if [[ "$include_movement" -eq 1 ]]; then
    "$runtime" --limit-modules java.base -Djava.io.tmpdir="$spike_run_dir" \
        -cp "$spike_run_dir/classes" ar.edu.uade.sokoban.movement.MovementTest \
        "$root/src/test/resources/levels"
fi
