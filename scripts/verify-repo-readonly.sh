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
# Git Bash may resolve python3 to an unusable Windows Store alias.
# Select an existing Python 3.9+ for this process only; never install anything.
python_command=''
for candidate in python3 python; do
    if command -v "$candidate" >/dev/null &&
        "$candidate" -c 'import sys; sys.exit(sys.version_info < (3, 9))' >/dev/null 2>&1; then
        python_command="$candidate"
        break
    fi
done
[[ -n "$python_command" ]] || { printf 'FAIL: Python 3.9+ required\n' >&2; exit 1; }
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
"$python_command" -B - "$bootstrap" <<'PY'
import csv, hashlib, io, pathlib, re, struct, subprocess, sys, wave, zlib
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
# Include ignored untracked files too: .gitignore must not hide build residues.
# Git enumerates the worktree; never inspect .git internals.
result=subprocess.run(['git','ls-files','-z','--cached','--others','--exclude-standard'],capture_output=True,check=True)
paths=sorted(set(result.stdout.decode().split('\0'))-{''})
ignored=subprocess.run(['git','ls-files','-z','--others','--ignored','--exclude-standard'],capture_output=True,check=True)
paths=sorted(set(paths+ignored.stdout.decode().split('\0'))-{''})
manifest_name='docs/punto5/ASSETS_REGISTRO_SHA256.csv'
asset_pattern=re.compile(r'src/desktop/resources/assets/(?:sprites/[a-z0-9_]+\.png|sfx/[a-z0-9_]+\.wav)')
manifest={}
if not bootstrap or (root/manifest_name).exists():
    try:
        with (root/manifest_name).open(encoding='utf-8', newline='') as source:
            reader=csv.DictReader(source, strict=True)
            if reader.fieldnames!=['path','sha256','origin','license_status','usage']:
                errors.append('Invalid asset manifest columns')
            for row in reader:
                name=row.get('path') or ''
                digest=row.get('sha256') or ''
                if None in row or any(value is None for value in row.values()):
                    errors.append('Invalid asset manifest row'); continue
                if not asset_pattern.fullmatch(name) or not re.fullmatch(r'[0-9a-f]{64}',digest):
                    errors.append('Invalid asset manifest path/hash'); continue
                if name in manifest: errors.append('Duplicate asset manifest entry: '+name)
                manifest[name]=digest
    except (OSError, UnicodeError, csv.Error):
        errors.append('Missing/unreadable UTF-8 asset manifest')
    if not manifest: errors.append('Empty asset manifest')
for name in manifest:
    if name not in paths or not (root/name).is_file():
        errors.append('Missing asset: '+name)

def valid_png(data):
    if data[:8]!=b'\x89PNG\r\n\x1a\n': return False
    offset=8; header=False; image=bytearray()
    while offset+12<=len(data):
        size=struct.unpack_from('>I',data,offset)[0]
        kind=data[offset+4:offset+8]; end=offset+12+size
        if end>len(data): return False
        payload=data[offset+8:end-4]
        if zlib.crc32(kind+payload)&0xffffffff!=struct.unpack_from('>I',data,end-4)[0]: return False
        if not header:
            if kind!=b'IHDR' or size!=13: return False
            if struct.unpack('>IIBBBBB',payload)!=(32,32,8,6,0,0,0): return False
            header=True
        elif kind==b'IHDR': return False
        if kind==b'IDAT': image.extend(payload)
        if kind==b'IEND':
            if size!=0 or end!=len(data) or not image: return False
            decoder=zlib.decompressobj()
            # Bound decompression to the exact RGBA scanline size plus one byte.
            raw=decoder.decompress(image,32*(1+32*4)+1)
            return (decoder.eof and not decoder.unused_data and not decoder.unconsumed_tail
                    and len(raw)==32*(1+32*4)
                    and all(raw[row*(1+32*4)]<=4 for row in range(32)))
        offset=end
    return False

def valid_wav(data):
    if len(data)<12 or data[:4]!=b'RIFF' or data[8:12]!=b'WAVE': return False
    if struct.unpack_from('<I',data,4)[0]+8!=len(data): return False
    with wave.open(io.BytesIO(data),'rb') as audio:
        if (audio.getnchannels(),audio.getsampwidth(),audio.getframerate(),audio.getcomptype())!=(1,2,22050,'NONE'):
            return False
        frames=audio.getnframes()
        return frames>0 and len(audio.readframes(frames))==frames*2

link_count=0
for name in paths:
    p=root/name
    if p.is_symlink(): errors.append('Symlink: '+name); continue
    if not p.is_file(): errors.append('Absent indexed file: '+name); continue
    if '.github/workflows/' in name or p.suffix.lower() in {'.class','.log','.tmp','.zip','.pdf','.jar','.pyc'}:
        errors.append('Forbidden/residue path: '+name)
    data=p.read_bytes()
    if len(data)>10*1024*1024:
        errors.append('Large file: '+name); continue
    if p.suffix.lower() in {'.png','.wav'}:
        if not asset_pattern.fullmatch(name):
            errors.append('Unauthorized binary asset: '+name); continue
        if name not in manifest: errors.append('Unregistered asset: '+name)
        elif hashlib.sha256(data).hexdigest()!=manifest[name]: errors.append('Asset hash mismatch: '+name)
        try: valid=valid_png(data) if p.suffix=='.png' else valid_wav(data)
        except (ValueError, struct.error, zlib.error, wave.Error, EOFError): valid=False
        if not valid: errors.append('Invalid asset format: '+name)
        continue
    text_suffixes={'.md','.puml','.json','.csv','.py','.ps1','.sh','.java','.txt',
                   '.xml','.properties','.yml','.yaml','.toml','.ini','.cfg','.html','.css','.js','.ts','.svg'}
    if p.suffix.lower() not in text_suffixes and p.name not in {'.gitkeep','.gitignore','.gitattributes','.editorconfig','LICENSE'}:
        errors.append('Unexpected file type: '+name)
    try: text=data.decode('utf-8')
    except UnicodeDecodeError: errors.append('Non UTF-8/binary: '+name); continue
    if any(ord(char)<32 and char not in '\t\r\n' for char in text):
        errors.append('Binary/control character in text: '+name)
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
        uml=(root/'docs/architecture/uml/sokoban-candidate.puml').read_text(encoding='utf-8')
        if uml.count('@startuml')!=1 or uml.count('@enduml')!=1: errors.append('Invalid UML boundaries')
        capsule=(root/historical).read_text(encoding='utf-8')
        embedded=re.search(r'```plantuml\n(.*?)\n```',capsule,re.S)
        if not embedded or embedded.group(1).strip()!=uml.strip(): errors.append('UML/capsule mismatch')
    oq=(root/'docs/requirements/OPEN_QUESTIONS.md').read_text(encoding='utf-8')
    for number in range(1,12):
        if f'OQ-{number:02}' not in oq: errors.append('Missing open question')
    if 'REPRODUCIBILITY_IN_CURRENT_REPOSITORY = NOT_VERIFIED' not in (root/'docs/testing/HISTORICAL_EVIDENCE.md').read_text(encoding='utf-8'):
        errors.append('Historical reproducibility label missing')
if errors:
    for error in errors: print('FAIL:',error)
    sys.exit(1)
print(f'PASS: {len(paths)} files; {link_count} relative links; required docs, paths and secret-pattern scan')
if manifest: print(f'PASS: {len(manifest)} registered PNG/WAV assets; format and SHA-256')
if bootstrap: print('PASS: bootstrap allowlist, immutable source hashes, UML boundaries/source and open questions')
print('NOTE: no Java tests, UML compiler, remote permission audit or manual authority review performed by this script')
PY
printf 'HEAD=%s\nBRANCH=%s\nRESULT=PASS\n' "$(git rev-parse HEAD)" "$branch"
