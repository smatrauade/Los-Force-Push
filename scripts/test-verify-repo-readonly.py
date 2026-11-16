#!/usr/bin/env python3
"""Exercise the real verifier in disposable Git copies; never alter the source repo."""
import argparse
import csv
import hashlib
import io
import zipfile
import os
from pathlib import Path
import shutil
import subprocess
import tempfile


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--bash', default='bash', help='Existing Bash executable (Git Bash on Windows)')
    args = parser.parse_args()
    root = Path(__file__).resolve().parent.parent
    manifest_name = 'docs/punto5/ASSETS_REGISTRO_SHA256.csv'
    verifier = 'scripts/verify-repo-readonly.sh'
    env = dict(os.environ, GIT_OPTIONAL_LOCKS='0', PYTHONDONTWRITEBYTECODE='1')

    def git(directory, *arguments):
        return subprocess.check_output(
            ['git', '-c', 'core.autocrlf=false', '-C', str(directory), *arguments], env=env)

    def initialize(directory, branch='verifier-regression'):
        git(directory, 'init', '-q', '-b', branch)
        git(directory, 'config', 'core.autocrlf', 'false')  # Only the disposable fixture repo.
        git(directory, 'remote', 'add', 'origin', 'https://github.com/smatrauade/Los-Force-Push.git')
        git(directory, 'add', '--all')
        git(directory, '-c', 'user.name=Verifier regression', '-c', 'user.email=verifier@example.invalid',
            '-c', 'commit.gpgsign=false', 'commit', '-qm', 'Disposable verifier fixture')

    def update_hash(directory, asset):
        path = directory / manifest_name
        with path.open(encoding='utf-8', newline='') as stream:
            reader = csv.DictReader(stream)
            fields = reader.fieldnames
            rows = list(reader)
        for row in rows:
            if row['path'] == asset:
                row['sha256'] = hashlib.sha256((directory / asset).read_bytes()).hexdigest()
        with path.open('w', encoding='utf-8', newline='') as stream:
            writer = csv.DictWriter(stream, fieldnames=fields, lineterminator='\n')
            writer.writeheader()
            writer.writerows(rows)

    with tempfile.TemporaryDirectory(prefix='sokoban-verifier-tests-') as owned:
        parent = Path(owned)
        baseline = parent / 'baseline'
        baseline.mkdir()
        names = git(root, 'ls-files', '-z', '--cached', '--others', '--exclude-standard').decode().split('\0')
        for name in sorted(set(names) - {''}):
            source = root / name
            if source.is_symlink() or not source.is_file():
                raise RuntimeError('Unsafe/missing source: ' + name)
            target = baseline / name
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(source, target)
        initialize(baseline)
        png = 'src/desktop/resources/assets/sprites/box.png'
        wav = 'src/desktop/resources/assets/sfx/step.wav'

        def write(directory, name, data):
            target = directory / name
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_bytes(data)

        def corrupt(directory, name, data):
            write(directory, name, data)
            update_hash(directory, name)  # Prove the format guard independently of SHA-256.

        def duplicate(directory):
            path = directory / manifest_name
            lines = path.read_text(encoding='utf-8').splitlines()
            with path.open('w', encoding='utf-8', newline='') as stream:
                stream.write('\n'.join(lines + [lines[1]]) + '\n')

        def corrupt_delivery(directory, name, data):
            write(directory, 'entregables/'+name, data)
            manifest=directory/'entregables/SHA256SUMS.txt'
            lines=manifest.read_text(encoding='utf-8').splitlines()
            digest=hashlib.sha256(data).hexdigest()
            with manifest.open('w',encoding='utf-8',newline='\n') as stream:
                stream.write('\n'.join(digest+'  '+name if x.endswith('  '+name) else x for x in lines)+'\n')

        def missing_slide(directory):
            name='Los_Force_Push_Sokoban_Defensa_18-11-2026.pptx'
            data=io.BytesIO()
            with zipfile.ZipFile(directory/'entregables'/name) as source, zipfile.ZipFile(data,'w') as target:
                for info in source.infolist():
                    if info.filename!='ppt/slides/slide9.xml':
                        target.writestr(info,source.read(info))
            corrupt_delivery(directory,name,data.getvalue())

        cases = [
            ('valid repository', None, None),
            ('invalid PDF with updated hash', lambda d: corrupt_delivery(d, 'INFORME_TECNICO.pdf', b'not PDF'), 'Invalid delivery PDF:'),
            ('invalid DOCX with updated hash', lambda d: corrupt_delivery(d, 'INFORME_TECNICO.docx', b'not DOCX'), 'Invalid delivery DOCX:'),
            ('invalid PPTX with updated hash', lambda d: corrupt_delivery(d, 'Los_Force_Push_Sokoban_Defensa_18-11-2026.pptx', b'not PPTX'), 'Invalid delivery PPTX:'),
            ('invalid MDJ with updated hash', lambda d: corrupt_delivery(d, 'Los_Force_Push_Sokoban_ENTREGA_FINAL.mdj', b'{}'), 'Invalid delivery MDJ:'),
            ('PPTX missing slide with updated hash', missing_slide, 'Invalid delivery PPTX:'),
            ('MDJ missing diagrams with updated hash', lambda d: corrupt_delivery(d, 'Los_Force_Push_Sokoban_ENTREGA_FINAL.mdj', b'{"_type":"Project","ownedElements":[]}'), 'Invalid delivery MDJ:'),
            ('truncated PNG', lambda d: corrupt(d, png, (d / png).read_bytes()[:40]), 'Invalid asset format:'),
            ('PNG CRC corruption', lambda d: corrupt(d, png, (d / png).read_bytes()[:-1] + b'X'), 'Invalid asset format:'),
            ('invalid WAV', lambda d: corrupt(d, wav, b'RIFF\x04\x00\x00\x00WAVE'), 'Invalid asset format:'),
            ('hash mismatch', lambda d: write(d, png, (d / png).read_bytes() + b'X'), 'Asset hash mismatch:'),
            ('duplicate manifest', duplicate, 'Duplicate asset manifest entry:'),
            ('missing asset', lambda d: (d / png).unlink(), 'Missing asset:'),
            ('unregistered asset', lambda d: write(d, 'src/desktop/resources/assets/sprites/unregistered.png', (d / png).read_bytes()), 'Unregistered asset:'),
            ('binary outside asset paths', lambda d: write(d, 'src/test/resources/rogue.png', (d / png).read_bytes()), 'Unauthorized binary asset:'),
            ('unexpected binary', lambda d: write(d, 'unexpected.bin', b'\x00\x01\xff'), 'Unexpected file type:'),
            ('invalid UTF-8 text', lambda d: write(d, 'invalid.txt', b'\xff'), 'Non UTF-8/binary:'),
            ('broken relative link', lambda d: write(d, 'broken.md', b'[missing](no-such-document.md)\n'), 'Broken/escaping link:'),
            ('prohibited residue', lambda d: write(d, 'residue.class', b'test fixture'), 'Forbidden/residue path:'),
            ('oversized file', lambda d: write(d, 'oversized.txt', b'x' * (10*1024*1024+1)), 'Large file:'),
            ('missing public document', lambda d: (d / 'docs/architecture/ARCHITECTURE.md').unlink(), 'Missing: docs/architecture/ARCHITECTURE.md'),
            ('broken anchor', lambda d: write(d, 'anchor.md', b'[readme](README.md#does-not-exist)\n'), 'BROKEN_ANCHOR'),
            ('escaping link', lambda d: write(d, 'escape.md', b'[outside](../outside.md)\n'), 'Broken/escaping link:'),
            ('copied UML altered', lambda d: write(d, 'entregables/fuentes_plantuml/00_Arquitectura_MVC.puml', b'altered'), 'Delivery hash mismatch:'),
            ('pinned UML source altered', lambda d: write(d, 'docs/architecture/uml/pr17-rc-ffa27f9/plantuml/00_Arquitectura_MVC.puml', b'altered'), 'Delivery UML PUML mismatch:'),
            ('pinned Java altered', lambda d: write(d, 'src/main/java/ar/edu/uade/sokoban/replay/MoveCommand.java', b'altered'), 'BLOCKED_UML_JAVA_BLOB_'),
            ('delivery PDF altered', lambda d: write(d, 'entregables/INFORME_TECNICO.pdf', b'altered'), 'Delivery hash mismatch:'),
            ('video altered', lambda d: write(d, 'entregables/VIDEO_GAMEPLAY.mp4', b'altered'), 'Video hash mismatch'),
            ('unlisted delivery binary', lambda d: write(d, 'entregables/unlisted.pdf', b'%PDF-fixture'), 'Invalid delivery manifest:'),
            ('academic ZIP rejected', lambda d: write(d, 'entregables/academic.zip', b'fixture'), 'Forbidden/residue path:'),
            ('synthetic secret', lambda d: write(d, 'synthetic.txt', (('gh' + 'p_') + 'A' * 30).encode()), 'Possible secret:'),
        ]
        passed = 0

        def check(directory, name, expected, *options):
            nonlocal passed
            result = subprocess.run([args.bash, verifier, *options], cwd=directory,
                                    env=env, capture_output=True, text=True, encoding='utf-8', errors='replace')
            output = result.stdout + result.stderr
            ok = result.returncode == 0 if expected is None else result.returncode != 0 and expected in output
            if not ok:
                raise AssertionError(f'{name}: exit={result.returncode}\n{output}')
            passed += 1
            print(f'PASS {name} (exit={result.returncode})')

        for index, (name, mutate, expected) in enumerate(cases):
            directory = parent / f'case-{index}'
            shutil.copytree(baseline, directory)
            if mutate:
                mutate(directory)
            check(directory, name, expected)

        print(f'VERIFIER TEST RESULT: {passed} PASS, 0 FAIL')


if __name__ == '__main__':
    main()
