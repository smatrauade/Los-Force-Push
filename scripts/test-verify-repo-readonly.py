#!/usr/bin/env python3
"""Exercise the real verifier in disposable Git copies; never alter the source repo."""
import argparse
import csv
import hashlib
import io
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import zipfile


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

        cases = [
            ('valid repository', None, None),
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

        # Exercise the immutable bootstrap layout with the current verifier transplanted.
        bootstrap = parent / 'bootstrap'
        bootstrap.mkdir()
        archive = git(root, 'archive', '--format=zip', 'ff6fbf170bd9782fb703e1ab4fa1e811ec0782ed')
        with zipfile.ZipFile(io.BytesIO(archive)) as bundle:
            if any(not (bootstrap / name).resolve().is_relative_to(bootstrap.resolve()) for name in bundle.namelist()):
                raise RuntimeError('Unsafe archive path')
            bundle.extractall(bootstrap)
        shutil.copyfile(root / verifier, bootstrap / verifier)
        initialize(bootstrap, 'docs/bootstrap-sokoban-autocontenido-v1')
        check(bootstrap, 'historical bootstrap', None, '--bootstrap')
        write(bootstrap, 'outside-bootstrap.txt', b'untracked fixture\n')
        check(bootstrap, 'bootstrap scope rejection', 'Outside bootstrap scope:', '--bootstrap')
        print(f'VERIFIER TEST RESULT: {passed} PASS, 0 FAIL')


if __name__ == '__main__':
    main()
