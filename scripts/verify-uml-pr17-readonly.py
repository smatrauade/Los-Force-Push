#!/usr/bin/env python3
"""Verifica (read-only) UML PlantUML/PNG congelados contra código HEAD original."""
from pathlib import Path
import argparse, hashlib, json, struct, subprocess, sys

def stop(msg): raise SystemExit('BLOCKED_UML_'+msg)
def sha(p):
    if not p.is_file() or p.is_symlink(): stop('FILE_'+p.name)
    return hashlib.sha256(p.read_bytes()).hexdigest()
def run(cmd,root):
    r=subprocess.run(cmd,cwd=root,capture_output=True,text=True,timeout=30,check=False)
    if r.returncode:stop('GIT_'+str(r.returncode))
    return r.stdout.strip()
def main():
    a=argparse.ArgumentParser();a.add_argument('--root',required=True);a.add_argument('--verify-java',action='store_true');args=a.parse_args()
    root=Path(args.root).resolve(strict=True)
    folder=root/'docs/architecture/uml/pr17-rc-ffa27f9'
    if not folder.is_dir() or folder.is_symlink():stop('DIRECTORY')
    manifest=json.loads((folder/'RENDER_GATE.json').read_text(encoding='utf-8'))
    if manifest['source_head']!='ffa27f9d8c72bd204db76f887b2ca0cd8e6f454c':stop('SOURCE_HEAD')
    names=sorted(manifest['diagrams'])
    if len(names)!=9 or names!=sorted(p.name for p in (folder/'png').glob('*.png')):stop('PNG_COUNT')
    if names!=sorted(p.name.replace('.puml','.png') for p in (folder/'plantuml').glob('*.puml')):stop('PUML_COUNT')
    for name,x in manifest['diagrams'].items():
        pp=folder/'plantuml'/name.replace('.png','.puml')
        png=folder/'png'/name
        if sha(pp)!=x['puml_sha256'] or sha(png)!=x['sha256']:stop('HASH_'+name)
        with png.open('rb') as h: header=h.read(24)
        if header[:8]!=b'\x89PNG\r\n\x1a\n' or header[12:16]!=b'IHDR' or struct.unpack('>II',header[16:24])!=(x['width'],x['height']):stop('PNG_FORMAT_'+name)
    for name,key in [('SOURCE_INVENTORY_PINNED.tsv','inventory_sha256'),('JAVA_TO_UML_TRACEABILITY.csv','traceability_sha256')]:
        if sha(folder/name)!=manifest[key]:stop('TRACE_SHA')
    if args.verify_java:
        # Verify exactly the frozen Java sources; this mode is not meant for future code revisions.
        inv=[s for s in (folder/'SOURCE_INVENTORY_PINNED.tsv').read_text(encoding='utf-8').splitlines() if s and not s.startswith('#')]
        if len(inv)!=50:stop('JAVA_COUNT')
        for entry in inv:
            p,blob,*_=entry.split('\t')
            file=root/p
            if not file.is_file() or file.is_symlink():stop('JAVA_SOURCE_'+p)
            actual=run(['git','hash-object','--',p],root)
            if actual!=blob:stop('JAVA_BLOB_'+p)
    print('RESULT=PASS_PLANTUML_RC_READONLY')
    print('PNG=9; PUML=9; JAVA_PINNED='+('50/50' if args.verify_java else 'NOT_CHECKED'))
    print('STARUML_FORMAT_REQUIREMENT=OPEN; TEAM_APPROVAL=NOT_RECORDED')
if __name__=='__main__':
    try:main()
    except (KeyError,ValueError,OSError,subprocess.TimeoutExpired) as e:stop(type(e).__name__)
