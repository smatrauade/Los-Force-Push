#!/usr/bin/env python3
"""Validate public documentation paths and Markdown links in the current Git tree."""
import argparse
from pathlib import Path
import re
import subprocess
import sys
from urllib.parse import unquote, urlsplit


def git(root, *args):
    return subprocess.check_output(['git', *args], cwd=root, timeout=25).decode('utf-8')


def anchors(text):
    result=set(re.findall(r'<(?:a|h[1-6])\s+(?:name|id)=["\']([^"\']+)', text))
    counts={}
    fenced=False
    for line in text.splitlines():
        if re.match(r'^\s*(```|~~~)', line):
            fenced=not fenced
            continue
        if fenced:
            continue
        heading=re.match(r'^#{1,6}\s+(.+?)(?:\s+#+)?$', line)
        if heading:
            title=re.sub(r'<[^>]*>', '', heading.group(1)).lower()
            title=re.sub(r'[^\w\- ]', '', title).replace(' ', '-')
            number=counts.get(title, 0)
            counts[title]=number+1
            result.add(title if number==0 else title+'-'+str(number))
    return result


def validate(root):
    errors=[]
    names=sorted(set(git(root,'ls-files','-z','--cached','--others','--exclude-standard').split('\0'))-{''})
    documents=[name for name in names if name.endswith('.md') and (root/name).is_file()]
    checked=0
    for name in documents:
        src=root/name
        if src.is_symlink():
            errors.append('SYMLINK '+name)
            continue
        text=src.read_text(encoding='utf-8')
        fenced=False
        for lineno,line in enumerate(text.splitlines(),1):
            if re.match(r'^\s*(```|~~~)',line):
                fenced=not fenced
                continue
            if fenced:
                continue
            for raw in re.findall(r'!?\[[^\]]*\]\(([^)]+)\)',line):
                target=raw.strip().strip('<>')
                parts=urlsplit(target)
                if parts.scheme or parts.netloc:
                    continue
                local=unquote(parts.path)
                dest=(src.parent/local).resolve() if local else src.resolve()
                if not dest.is_relative_to(root) or not dest.exists():
                    errors.append(f'BROKEN_LINK {name}:{lineno}')
                    continue
                # Reject links through symlinked directories as well as leaves.
                relative=(src.parent/local if local else src).relative_to(root)
                if any((root/Path(*relative.parts[:i])).is_symlink() for i in range(1,len(relative.parts)+1)):
                    errors.append(f'UNSAFE_LINK {name}:{lineno}')
                if parts.fragment and dest.is_file() and dest.suffix=='.md':
                    if unquote(parts.fragment) not in anchors(dest.read_text(encoding='utf-8')):
                        errors.append(f'BROKEN_ANCHOR {name}:{lineno}')
                checked+=1
    print(f'DOCUMENTS={len(documents)}; LOCAL_LINKS_AND_ANCHORS={checked}')
    return errors


def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--local',action='store_true',help='validate local public documents (default)')
    parser.parse_args()
    root=Path(git(None,'rev-parse','--show-toplevel').strip()).resolve()
    required=['README.md','docs/README.md','docs/requirements/REQUIREMENTS.md',
        'docs/requirements/TXT_FORMAT.md','docs/architecture/ARCHITECTURE.md',
        'docs/architecture/RESPONSIBILITIES.md','docs/decisions/DECISION_LOG.md',
        'docs/traceability/TRACEABILITY_MATRIX.md','docs/testing/TEST_STRATEGY.md',
        'docs/assets/ASSET_POLICY.md','entregables/r10-20261113/README.md']
    errors=[]
    for name in required:
        path=root/name
        if path.is_symlink() or not path.is_file() or path.stat().st_size>512_000:
            errors.append('MISSING_OR_UNSAFE '+name)
    errors.extend(validate(root))
    for error in errors:
        print('FAIL: '+error,file=sys.stderr)
    if errors:
        return 1
    print('RESULT=PASS_PUBLIC_DOCUMENTATION')
    return 0


if __name__=='__main__':
    try:
        sys.exit(main())
    except (OSError,UnicodeError,ValueError,subprocess.SubprocessError) as error:
        sys.exit('FAIL: documentation inspection '+type(error).__name__)
