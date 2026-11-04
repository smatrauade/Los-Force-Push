#!/usr/bin/env bash
# I1 core + desktop regression; JDK17, repository-independent, no Git writes.
set -Eeuo pipefail
export GIT_OPTIONAL_LOCKS=0
export LC_ALL=C.UTF-8 LANG=C.UTF-8

root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"
fail() { printf 'RESULTADO_GLOBAL=BLOCKED\nCAUSE=%s\n' "$1" >&2; exit 1; }
[[ -d "$root/src/main/java" && -d "$root/src/desktop/java" ]] || fail "NOT_PRODUCT_ROOT"
[[ "$(git -C "$root" rev-parse --show-toplevel)" == "$root" ]] || fail "WRONG_GIT_WORKTREE"

# Read-only test execution must not modify index, worktree content or refs.
status_before="$(git -C "$root" status --porcelain=v1 -z --untracked-files=all | sha256sum | cut -d' ' -f1)"
index_before="$(git -C "$root" ls-files --stage -z | sha256sum | cut -d' ' -f1)"
head_before="$(git -C "$root" rev-parse HEAD)"
merge_before="$(git -C "$root" rev-parse -q --verify MERGE_HEAD 2>/dev/null || true)"
files_hash() {
    (cd "$root" && find src scripts -type f -print0 | LC_ALL=C sort -z | xargs -0 sha256sum | sha256sum | cut -d' ' -f1)
}
files_before="$(files_hash)"

compiler="$(readlink -f "$(command -v javac)")"
runtime="$(dirname "$compiler")/java"
[[ -x "$runtime" ]] || fail "JAVA_PAIR_MISSING"
[[ "$("$compiler" -version 2>&1)" == javac\ 17* ]] || fail "JAVAC_NOT_17"
[[ "$("$runtime" -version 2>&1 | head -1)" == *'"17'* ]] || fail "JAVA_NOT_17"
export PATH="$(dirname "$compiler"):$PATH"
echo "JAVAC=$("$compiler" -version 2>&1)"
echo "JAVA=$("$runtime" -version 2>&1 | head -1)"

parent="$(cd "${TMPDIR:-/tmp}" && pwd -P)"
case "$parent/" in "$root/"*) fail "TEMP_INSIDE_REPOSITORY";; esac
scratch="$(mktemp -d "$parent/forcepush-i1c.XXXXXXXX")"
cleanup() {
    if [[ -n "${scratch:-}" && -d "$scratch" && ! -L "$scratch" ]]; then
        find "$scratch" -type f -delete
        find "$scratch" -type l -delete
        find "$scratch" -depth -type d -empty -delete
    fi
}
trap cleanup EXIT
mkdir "$scratch/classes"
find "$root/src/main/java" "$root/src/test/java" -type f -name '*.java' | LC_ALL=C sort > "$scratch/core-sources"
"$compiler" --release 17 --limit-modules java.base -encoding UTF-8 -Xlint:all -Werror \
    -d "$scratch/classes" "@$scratch/core-sources" || fail "STRICT_CORE_COMPILATION"
echo "CORE_COMPILE=PASS"

failures=0
run_core() {
    local label="$1" class="$2" expected="$3"
    local output="$scratch/$label.log" rc=0
    (cd "$scratch" && "$runtime" --limit-modules java.base -Dfile.encoding=UTF-8 \
        -Djava.io.tmpdir="$scratch" -cp "$scratch/classes" "$class" \
        "$root/src/test/resources/levels") >"$output" 2>&1 || rc=$?
    printf '=== %s ===\n' "$label"
    if [[ "$rc" -eq 0 ]] && grep -Fxq "$expected" "$output"; then
        printf '%s\n' "$expected"
    else
        echo "EXIT_CODE=$rc OR SUMMARY_MISMATCH"; tail -n 30 "$output"; failures=1
    fi
}
run_core LOADING ar.edu.uade.sokoban.level.LevelLoadingTest 'RESULT: 40 PASS, 0 FAIL'
run_core MOVEMENT ar.edu.uade.sokoban.movement.MovementTest 'MOVEMENT RESULT: 37 PASS, 0 FAIL'
run_core EXTENDED ar.edu.uade.sokoban.level.ExtendedLevelTest 'EXTENDED RESULT: 26 PASS, 0 FAIL'
run_core DYNAMIC ar.edu.uade.sokoban.movement.DynamicWorldTest 'DYNAMIC WORLD RESULT: 6 PASS, 0 FAIL'
run_core COMPLETION ar.edu.uade.sokoban.session.CompletionTest 'COMPLETION RESULT: 38 PASS, 0 FAIL'
run_core C2 ar.edu.uade.sokoban.level.C2LevelLoadingTest 'C2 RESULT: 36 PASS, 0 FAIL'
run_core FRAGILE ar.edu.uade.sokoban.level.ExperimentalFragileParserTest 'FRAGILE RESULT: 41 PASS, 0 FAIL'
run_core RUNTIME_I1C ar.edu.uade.sokoban.movement.RuntimeIceFragileTest 'RUNTIME ICE FRAGILE RESULT: 34 PASS, 0 FAIL'

echo '=== DESKTOP ==='
if (cd "$root" && bash scripts/run-swing.sh --test) >"$scratch/desktop.log" 2>&1 && \
    grep -Fxq 'DESKTOP RESULT: 40 PASS, 0 FAIL' "$scratch/desktop.log" && \
    grep -Eq '^CAMPAIGN RESULT: [1-9][0-9]* PASS, 0 FAIL$' "$scratch/desktop.log" && \
    grep -Eq '^THEME RESULT: [1-9][0-9]* PASS, 0 FAIL$' "$scratch/desktop.log" && \
    grep -Eq '^ANIMATION RESULT: [1-9][0-9]* PASS, 0 FAIL$' "$scratch/desktop.log" && \
    grep -Eq '^REPLAY CONTROLLER RESULT: [1-9][0-9]* PASS, 0 FAIL$' "$scratch/desktop.log" && \
    grep -Eq '^REPLAY PRESENTATION RESULT: [1-9][0-9]* PASS, 0 FAIL$' "$scratch/desktop.log" && \
    grep -Fxq 'VISUAL ASSETS RESULT: 44 PASS, 0 FAIL' "$scratch/desktop.log"; then
    echo 'DESKTOP RESULT: 40 PASS, 0 FAIL'
    grep -E '^CAMPAIGN RESULT: [1-9][0-9]* PASS, 0 FAIL$' "$scratch/desktop.log"
    grep -E '^THEME RESULT: [1-9][0-9]* PASS, 0 FAIL$' "$scratch/desktop.log"
    grep -E '^ANIMATION RESULT: [1-9][0-9]* PASS, 0 FAIL$' "$scratch/desktop.log"
    grep -E '^REPLAY (CONTROLLER|PRESENTATION) RESULT:' "$scratch/desktop.log"
    echo 'VISUAL ASSETS RESULT: 44 PASS, 0 FAIL'
else
    echo 'DESKTOP_OR_VISUAL_ASSETS=FAILED'; tail -n 45 "$scratch/desktop.log"; failures=1
fi

echo '=== GIT AND FILE POSTCHECK ==='
[[ "$status_before" == "$(git -C "$root" status --porcelain=v1 -z --untracked-files=all | sha256sum | cut -d' ' -f1)" ]] || fail "GIT_STATUS_CHANGED"
[[ "$index_before" == "$(git -C "$root" ls-files --stage -z | sha256sum | cut -d' ' -f1)" ]] || fail "GIT_INDEX_CHANGED"
[[ "$head_before" == "$(git -C "$root" rev-parse HEAD)" ]] || fail "HEAD_CHANGED"
[[ "$merge_before" == "$(git -C "$root" rev-parse -q --verify MERGE_HEAD 2>/dev/null || true)" ]] || fail "MERGE_HEAD_CHANGED"
[[ "$files_before" == "$(files_hash)" ]] || fail "SOURCE_OR_SCRIPT_CONTENT_CHANGED"
git -C "$root" diff --check || fail "WORKTREE_WHITESPACE"
git -C "$root" diff --cached --check || fail "INDEX_WHITESPACE"
echo 'GIT_STATE_PRESERVED=YES'
if [[ "$failures" -ne 0 ]]; then
    echo 'RESULTADO_GLOBAL=I1C_TEST_FAILURES_IDENTIFIED'
    exit 1
fi
echo 'CORE_TOTAL=258/258'
echo 'DESKTOP_TOTAL=40/40'
echo 'SHIPPED_CAMPAIGN_SUITE=PASS'
echo 'THEME_SUITE=PASS'
echo 'ANIMATION_SUITE=PASS'
echo 'ASSET_TOTAL=44/44'
echo 'RESULTADO_GLOBAL=PASS_I1_REGRESSION_I2_TREE'
