#!/usr/bin/env bash
# Local inspection only: no fetch, writes, builds or network.
set -euo pipefail
export GIT_OPTIONAL_LOCKS=0
bootstrap=0
require_clean=0
for arg in "$@"; do
    case "$arg" in
        --bootstrap) bootstrap=1 ;;
        --require-clean) require_clean=1 ;;
        *) printf 'Usage: bash scripts/verify-repo-readonly.sh [--bootstrap] [--require-clean]\n' >&2; exit 2 ;;
    esac
done
command -v git >/dev/null
command -v python3 >/dev/null
root="$(git rev-parse --show-toplevel)"
cd "$root"
origin="$(git remote get-url origin)"
case "$origin" in
    https://github.com/smatrauade/Los-Force-Push.git|https://github.com/smatrauade/Los-Force-Push|git@github.com:smatrauade/Los-Force-Push.git|ssh://git@github.com/smatrauade/Los-Force-Push.git) ;;
    *) printf 'FAIL: unexpected origin (value withheld)\n' >&2; exit 1 ;;
esac
branch="$(git branch --show-current)"
if (( bootstrap )) && [[ "$branch" != docs/bootstrap-sokoban-autocontenido-v1 ]]; then
    printf 'FAIL: unexpected bootstrap branch\n' >&2; exit 1
fi
if (( require_clean )) && [[ -n "$(git status --porcelain=v1)" ]]; then
    printf 'FAIL: dirty worktree\n' >&2; exit 1
fi
git diff --check
git diff --cached --check
python3 -B - "$bootstrap" <<'PY'
import hashlib, pathlib, re, subprocess, sys
from urllib.parse import unquote
root=pathlib.Path.cwd()
bootstrap=sys.argv[1]=='1'
required='''README.md AGENTS.md CONTRIBUTING.md TODO.md .gitignore .editorconfig .gitattributes
docs/README.md docs/requirements/REQUIREMENTS.md docs/requirements/OPEN_QUESTIONS.md
docs/architecture/ARCHITECTURE.md docs/architecture/RESPONSIBILITIES.md
docs/architecture/uml/sokoban-candidate.puml docs/architecture/uml/STARUML_STATUS.md
docs/decisions/DECISION_LOG.md docs/traceability/TRACEABILITY_MATRIX.md
docs/testing/TEST_STRATEGY.md docs/testing/HISTORICAL_EVIDENCE.md
docs/references/BIBLIOGRAPHY.md docs/references/GITHUB_REFERENCES.md docs/assets/ASSET_POLICY.md
docs/continuity/CURRENT_STATE.md docs/continuity/FRESHNESS_POLICY.md docs/continuity/SOURCE_REGISTRY.md
docs/continuity/BOOTSTRAP_PREFLIGHT.md docs/continuity/capsules/BOOTSTRAP_2026-10-08.md
scripts/verify-repo-readonly.sh'''.split()
historical='docs/continuity/capsules/SOKOBAN_CAPSULA_EQUIPO_AUTOCONTENIDA_SCHEMA9_v4.0_2026-10-07.md'
expected={historical:'4250c89752214242f35203237539024cbad54423054ccd0387263100fee61859',
'docs/architecture/uml/sokoban-candidate.puml':'07e86f32bc2ea5c7091508698b415efbf18e9bce45464bb477cb154e5a268901'}
errors=[]
for name in required+[historical]:
    if not (root/name).is_file(): errors.append('Missing: '+name)
# Index + untracked, including hidden files; never inspect .git internals.
result=subprocess.run(['git','ls-files','-z','--cached','--others','--exclude-standard'],capture_output=True,check=True)
paths=sorted(set(result.stdout.decode().split('\0'))-{''})
link_count=0
for name in paths:
    p=root/name
    if p.is_symlink(): errors.append('Symlink: '+name); continue
    if not p.is_file(): errors.append('Absent indexed file: '+name); continue
    if '.github/workflows/' in name or p.suffix.lower() in {'.class','.log','.tmp','.zip','.pdf','.jar','.pyc'}:
        errors.append('Forbidden/residue path: '+name)
    data=p.read_bytes()
    if len(data)>10*1024*1024: errors.append('Large file: '+name)
    try: text=data.decode('utf-8')
    except UnicodeDecodeError: errors.append('Non UTF-8/binary: '+name); continue
    if p.suffix in {'.md', '.puml'} and re.search(r'(?:/mnt/data/|/home/[^\s`]+|file://)',text): errors.append('Local/sandbox path: '+name)
    # Report location only, never the matching credential.
    patterns=[r'gh[pousr]_[A-Za-z0-9]{20,}',r'github_pat_[A-Za-z0-9_]{20,}',
              r'AKIA[0-9A-Z]{16}',r'-----BEGIN (?:RSA |EC |OPENSSH )?PRIVATE KEY-----',
              r'(?i)(?:token|password|secret)\s*[:=]\s*["\']?[A-Za-z0-9+/=_-]{24,}']
    if any(re.search(pattern,text) for pattern in patterns): errors.append('Possible secret: '+name)
    if p.suffix=='.md':
        # Only real Markdown links outside fenced examples; existence, not HTTP availability.
        in_fence=False
        for lineno,line in enumerate(text.splitlines(),1):
            if re.match(r'^\s*(```|~~~)',line): in_fence=not in_fence; continue
            if in_fence: continue
            for target in re.findall(r'!?\[[^\]]*\]\(([^)]+)\)',line):
                target=target.strip().strip('<>')
                if re.match(r'^[A-Za-z][A-Za-z0-9+.-]*:',target) or target.startswith('#'): continue
                local=unquote(target.split('#',1)[0])
                resolved=(p.parent/local).resolve()
                if not resolved.is_relative_to(root.resolve()) or not resolved.exists():
                    errors.append(f'Broken/escaping link: {name}:{lineno}')
                link_count+=1
if bootstrap:
    allowed=set(required+[historical]+['src/main/java/.gitkeep','src/test/java/.gitkeep',
        'src/main/resources/levels/.gitkeep','src/main/resources/assets/.gitkeep'])
    for name in paths:
        if name not in allowed: errors.append('Outside bootstrap scope: '+name)
    for name,digest in expected.items():
        if (root/name).is_file() and hashlib.sha256((root/name).read_bytes()).hexdigest()!=digest:
            errors.append('Historical bytes modified: '+name)
    if (root/'docs/architecture/uml/sokoban-candidate.puml').is_file():
        uml=(root/'docs/architecture/uml/sokoban-candidate.puml').read_text()
        if uml.count('@startuml')!=1 or uml.count('@enduml')!=1: errors.append('Invalid UML boundaries')
        capsule=(root/historical).read_text()
        embedded=re.search(r'```plantuml\n(.*?)\n```',capsule,re.S)
        if not embedded or embedded.group(1).strip()!=uml.strip(): errors.append('UML/capsule mismatch')
    oq=(root/'docs/requirements/OPEN_QUESTIONS.md').read_text()
    for number in range(1,12):
        if f'OQ-{number:02}' not in oq: errors.append('Missing open question')
    if 'REPRODUCIBILITY_IN_CURRENT_REPOSITORY = NOT_VERIFIED' not in (root/'docs/testing/HISTORICAL_EVIDENCE.md').read_text():
        errors.append('Historical reproducibility label missing')
if errors:
    for error in errors: print('FAIL:',error)
    sys.exit(1)
print(f'PASS: {len(paths)} files; {link_count} relative links; required docs, paths and secret-pattern scan')
if bootstrap: print('PASS: bootstrap allowlist, immutable source hashes, UML boundaries/source and open questions')
print('NOTE: no Java tests, UML compiler, remote permission audit or manual authority review performed by this script')
PY
printf 'HEAD=%s\nBRANCH=%s\nRESULT=PASS\n' "$(git rev-parse HEAD)" "$branch"
