#!/usr/bin/env python3
"""Read-only integration-documentation integrity and branch drift reporter (stdlib)."""
import argparse
import re
import subprocess
import sys
from pathlib import Path
from urllib.parse import unquote

p = argparse.ArgumentParser()
p.add_argument('--local', action='store_true', help='check documentation and local checkout only (default)')
p.add_argument('--remote', action='store_true', help='also compare live remote refs to 2026-11-03 snapshot')
args = p.parse_args()

def git(*argv):
    x = subprocess.run(['git', *argv], text=True, encoding='utf-8', errors='replace', capture_output=True, timeout=25)
    if x.returncode: raise RuntimeError(f'git {argv[0]} failed ({x.returncode}): {x.stderr.strip()[:240]}')
    return x.stdout.strip()
try:
    root = Path(git('rev-parse', '--show-toplevel')).resolve()
except Exception as e:
    sys.exit(f'FAIL_CLOSED NOT_A_GIT_REPO: {e}')
expected = [
 'AGENTS.md','README.md','TODO.md','docs/README.md',
 'docs/continuity/REHYDRATION_INDEX.md','docs/continuity/CURRENT_STATE.md',
 'docs/continuity/FRESHNESS_POLICY.md','docs/continuity/SOURCE_REGISTRY.md',
 'docs/continuity/BRANCH_AWARE_FRESHNESS.md',
 'docs/requirements/REQUIREMENTS.md','docs/requirements/OPEN_QUESTIONS.md',
 'docs/decisions/DECISION_LOG.md','docs/traceability/TRACEABILITY_MATRIX.md',
 'docs/testing/TEST_STRATEGY.md','docs/architecture/ARCHITECTURE.md',
 'docs/architecture/RESPONSIBILITIES.md','docs/architecture/uml/STARUML_STATUS.md',
 'docs/integration/README.md','docs/integration/TEAM_INTEGRATION_PLAN.md',
 'docs/integration/BRANCH_SNAPSHOT_2026-11-03.md',
 'docs/integration/INTEGRATION_ACCEPTANCE.md'
]
errors=[]
for rel in expected:
    t=root/rel
    if not t.is_file() or t.is_symlink(): errors.append('MISSING_OR_SYMLINK '+rel)
    elif t.stat().st_size > 512_000: errors.append('OVERSIZED '+rel)
if errors:
    print('FAIL_CLOSED: missing/unsafe docs:',*errors,sep='\n',file=sys.stderr)
    sys.exit(2)

# Validate local links only in *new* documents and appended I0 sections without mass-parsing legacy Markdown.
newdocs=[x for x in expected if 'docs/integration/' in x or x.endswith('/BRANCH_AWARE_FRESHNESS.md')]
mdlink=re.compile(r'\[[^\]]*\]\(([^)]+)\)')
for rel in newdocs:
    src=root/rel
    s=src.read_text(encoding='utf-8')
    for target in mdlink.findall(s):
        target=unquote(target.split('#')[0].strip('<>'))
        if not target or re.match(r'^[a-z][a-z0-9+.-]*:',target,re.I): continue
        dest=(src.parent/target).resolve()
        if not dest.is_relative_to(root) or not dest.exists():errors.append(f'BROKEN_LINK {rel} -> {target}')

marker='<!-- I0_BRANCH_AWARE_CONTEXT_2026_11_03 -->'
for rel in ('AGENTS.md','README.md','docs/README.md','docs/continuity/REHYDRATION_INDEX.md','docs/continuity/CURRENT_STATE.md','TODO.md','docs/continuity/FRESHNESS_POLICY.md'):
    cnt=(root/rel).read_text(encoding='utf-8').count(marker)
    if cnt!=1: errors.append(f'I0_POINTER_MARKER_COUNT {rel}={cnt}')
try:
    branch=git('branch','--show-current') or '(DETACHED)'
    sha=git('rev-parse','HEAD')
except RuntimeError as e:
    errors.append(str(e));branch='UNKNOWN';sha='UNKNOWN'
print('PROJECT=smatrauade/Los-Force-Push')
print('CHECKOUT_BRANCH='+branch)
print('CHECKOUT_SHA='+sha)
print('DOCS_PRESENT='+str(len(expected)))
print('NEW_MARKDOWN_LINKS='+('PASS' if not [x for x in errors if x.startswith('BROKEN_LINK')] else 'FAIL'))
print('NOTICE=Historical branch SHAs in BRANCH_SNAPSHOT are snapshots, not current authorities')

if args.remote:
    snapshot={
        'main':'f4a28fd6c1d869fcd6f6189e5cbc2da49bb2f7a2',
        'victoria':'a7df64172dbb857e7a10709ec9ea85a5f80aacf7',
        'Objetivo2':'ac728a2241d2fae8c4c32b78e29fd4c9bc6228eb',
        'feature/undo-restart-score':'efd0fcd2a1423bd1d5b064458671ec9b149fd054',
        'impaciencia':'b2c63327384a9e72333284d935aed181f0c66565',
    }
    try:
        for b,old in snapshot.items():
            got=git('ls-remote','--heads','origin','refs/heads/'+b).split('\t')
            if len(got)!=2: errors.append('REMOTE_MISSING '+b);continue
            print('REMOTE '+b+' '+got[0]+ (' UNCHANGED_FROM_CUT' if got[0]==old else ' DRIFT_FROM_CUT'))
            if got[0]!=old: errors.append('REMOTE_DRIFT '+b+' old='+old+' now='+got[0])
    except RuntimeError as e: errors.append('REMOTE_UNKNOWN '+str(e))
if errors:
    for x in errors: print('ISSUE='+x,file=sys.stderr)
    sys.exit(2)
print('RESULT=PASS_LOCAL_STRUCTURE'+('_AND_REMOTE_CUT' if args.remote else ''))
