#!/usr/bin/env bash
set -euo pipefail
root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd -P)"
mode="${1:-menu}"
if [[ $# -gt 1 ]]; then echo 'FAIL_CLOSED: modo unico' >&2; exit 2; fi
[[ -f "$root/scripts/run-swing.sh" ]] || { echo 'FAIL_CLOSED: falta run-swing.sh' >&2; exit 1; }
command -v javac >/dev/null && command -v java >/dev/null || {
    echo 'FAIL_CLOSED: se necesita JDK completo 17+' >&2; exit 1;
}
if [[ "$root" =~ [[:space:]] ]]; then
    echo 'FAIL_CLOSED: el runner Bash requiere un clon en ruta sin espacios' >&2; exit 1
fi
case "$mode" in
    menu) exec bash "$root/scripts/run-swing.sh" ;;
    txt)
        level="$root/src/desktop/resources/levels/demo.txt"
        [[ -f "$level" ]] || { echo 'FAIL_CLOSED: falta demo.txt' >&2; exit 1; }
        exec bash "$root/scripts/run-swing.sh" "$level" ;;
    *) echo 'FAIL_CLOSED: modo desconocido; usar menu o txt' >&2; exit 2 ;;
esac
