#!/usr/bin/env python3
"""Build pinned LGPL FFmpeg in isolation; install only after all four ABI guards pass."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import shlex
import shutil
import struct
import subprocess
import tarfile
import zipfile

ABIS = ['arm64-v8a', 'armeabi-v7a', 'x86', 'x86_64']
# Source-traced host-only documentation/test-transfer probes; no runtime references.
HOST_TOOLS = {'HAVE_MAKEINFO', 'HAVE_MAKEINFO_HTML', 'HAVE_RSYNC_CONTIMEOUT'}
CONFIG_METADATA = {'FFMPEG_CONFIGURATION', 'FFMPEG_DATADIR', 'AVCONV_DATADIR'}
IGNORED_CONFIG_PREFIXES = tuple('#define ' + name + ' ' for name in HOST_TOOLS | CONFIG_METADATA)

def digest(path):
    with Path(path).open('rb') as stream:
        return hashlib.file_digest(stream, 'sha256').hexdigest()

def download(input_spec, path):
    if not path.exists():
        subprocess.run(['curl', '--fail', '--location', '--silent', '--show-error', input_spec['url'], '--output', str(path)], check=True)
    if digest(path) != input_spec['sha256']:
        raise ValueError('Pinned native source/toolchain download hash differs')

def configuration(path):
    return next(value.decode() for value in Path(path).read_bytes().split(b'\0') if value.startswith(b'--cross-prefix='))

def symbols(nm, path):
    result = subprocess.run([str(nm), '-D', '--defined-only', str(path)], check=True, capture_output=True, text=True).stdout
    return sorted(line.split()[-1] for line in result.splitlines())

def needed(readelf, path):
    result = subprocess.run([str(readelf), '-d', str(path)], check=True, capture_output=True, text=True).stdout
    return sorted(re.findall(r'\(NEEDED\).*?\[(.*?)\]', result))

def config_digest(path):
    # Only build-location metadata and source-traced host doc/test tools may vary.
    text = '\n'.join(line for line in Path(path).read_text().splitlines() if not line.startswith(IGNORED_CONFIG_PREFIXES))
    return hashlib.sha256(text.encode()).hexdigest()


def elf_identity(readelf, path):
    with Path(path).open('rb') as stream:
        header = stream.read(20)
    if header[:4] != b'\x7fELF' or header[5] != 1:
        raise ValueError('Expected little-endian Android ELF library')
    output = subprocess.run([str(readelf), '--wide', '-l', '-d', str(path)], check=True, capture_output=True, text=True).stdout
    soname = re.search(r'\(SONAME\).*?\[(.*?)\]', output)
    loads = [int(line.split()[-1], 16) for line in output.splitlines() if line.strip().startswith('LOAD ')]
    if not soname or not loads or min(loads) < 16384:
        raise ValueError('Native SONAME or 16 KiB ELF alignment is incorrect')
    return (header[4], struct.unpack('<H', header[18:20])[0], soname.group(1))

def check_configuration(directory, reviewed, abi):
    config = (directory / 'config.h').read_text()
    for prohibited in ['GPL', 'GPLV3', 'LGPLV3', 'NONFREE']:
        if '#define CONFIG_' + prohibited + ' 0' not in config:
            raise ValueError('Native licence configuration changed')
    if config_digest(directory / 'config.h') != reviewed[abi]['config_h_sha256']:
        actual_macros = {line.split(None, 2)[1]: line.split(None, 2)[2] if len(line.split(None, 2)) > 2 else '' for line in config.splitlines() if line.startswith('#define ') and not line.startswith(IGNORED_CONFIG_PREFIXES)}
        expected_macros = reviewed[abi].get('configuration_macros', {})
        for name in sorted(set(actual_macros) | set(expected_macros)):
            if actual_macros.get(name) != expected_macros.get(name):
                print('Native configuration mismatch:', abi, name, 'reviewed=', expected_macros.get(name), 'actual=', actual_macros.get(name), flush=True)
        raise ValueError('Compiled native feature configuration differs from reviewed candidate')
    components = digest(directory / 'config_components.h')
    if components != reviewed[abi]['config_components_sha256']:
        raise ValueError('Native component availability differs from reviewed candidate')
    return components

def check_candidate(root, workspace, reviewed):
    """Compare public ABI, headers, dependencies and all compiled configuration macros."""
    tc = workspace / 'toolchain/android-ndk-r30-beta1/toolchains/llvm/prebuilt/linux-x86_64/bin'
    lock = json.loads((root / 'Video/.github/build/ffmpeg-foundation.lock.json').read_text())
    archive = workspace / 'downloads/ffmpeg.tar.gz'
    if digest(archive) != lock['inputs']['ffmpeg']['sha256']:
        raise ValueError('Corresponding FFmpeg source archive hash differs')
    original_files = {}
    with tarfile.open(archive) as packed:
        for member in packed.getmembers():
            if member.isfile():
                name = str(Path(*Path(member.name).parts[1:]))
                original_files[name] = hashlib.sha256(packed.extractfile(member).read()).hexdigest()
    original_files.update(lock['patched_source_sha256'])
    report = {'schema': 1, 'abis': {}, 'source_commit': '894da5ca7d742e4429ffb2af534fcda0103ef593'}
    for abi in ABIS:
        old = root / 'native/prebuilt/ffmpeg' / ('dist-full-' + abi)
        new = workspace / 'candidate' / ('dist-full-' + abi)
        for name, expected in original_files.items():
            if digest(workspace / 'build' / abi / name) != expected:
                raise ValueError('FFmpeg corresponding source differs beyond the two reviewed patches')
        components = check_configuration(workspace / 'build' / abi, reviewed, abi)
        entries = []
        for old_file in sorted((old / 'lib').glob('*.so')):
            new_file = new / 'lib' / old_file.name
            baseline = reviewed[abi]['libraries'][old_file.name]
            if digest(old_file) != baseline['baseline_sha256']:
                raise ValueError('Pinned baseline native bytes changed before comparison')
            exports = symbols(tc / 'llvm-nm', new_file)
            dependencies = needed(tc / 'llvm-readelf', new_file)
            if elf_identity(tc / 'llvm-readelf', old_file) != elf_identity(tc / 'llvm-readelf', new_file):
                raise ValueError('Native architecture, ELF class or SONAME changed')
            if exports != baseline['exports'] or dependencies != baseline['needed']:
                raise ValueError('Native public symbols or dynamic dependencies changed')
            entries.append({'file': old_file.name, 'sha256': digest(new_file), 'exports': len(exports), 'needed': dependencies})
        for old_header in (old / 'include').rglob('*.h'):
            header = old_header.relative_to(old / 'include')
            new_header = new / 'include' / header
            if str(header) == 'libavutil/ffversion.h':
                if '#define FFMPEG_VERSION "8.0.1"' not in new_header.read_text():
                    raise ValueError('FFmpeg release-source version is incorrect')
            elif old_header.read_bytes() != new_header.read_bytes():
                raise ValueError('Native public ABI header changed')
        report['abis'][abi] = {'libraries': entries, 'config_h_sha256': config_digest(workspace / 'build' / abi / 'config.h'), 'config_components_sha256': components}
    return report

def build(root, workspace, video, jobs):
    lock = json.loads((video / '.github/build/ffmpeg-foundation.lock.json').read_text())
    workspace.mkdir(parents=True, exist_ok=True)
    cache = workspace / 'downloads'; cache.mkdir(exist_ok=True)
    source_roots = {}
    for name, spec in lock['inputs'].items():
        archive = cache / (name + ('.zip' if name == 'ndk' else '.tar.gz'))
        download(spec, archive)
        dest = workspace / ('toolchain' if name == 'ndk' else 'sources/' + name)
        if not dest.exists():
            dest.mkdir(parents=True)
            if name == 'ndk':
                with zipfile.ZipFile(archive) as zipped:
                    if any(Path(item).is_absolute() or '..' in Path(item).parts for item in zipped.namelist()):
                        raise ValueError('Unexpected toolchain archive path')
                # unzip preserves the upstream executable permission bits.
                subprocess.run(['unzip', '-q', str(archive), '-d', str(dest)], check=True)
            else:
                with tarfile.open(archive) as packed:
                    packed.extractall(dest, filter='data')
        source_roots[name] = next(p for p in dest.iterdir() if p.is_dir())
    tc = source_roots['ndk'] / 'toolchains/llvm/prebuilt/linux-x86_64'
    if 'Pkg.BaseRevision = 30.0.14904198' not in (source_roots['ndk'] / 'source.properties').read_text():
        raise ValueError('Wrong native toolchain')
    patches = [(video / '.github/build/ffmpeg-opus-n8.0.1.patch', lock['patches']['.github/build/ffmpeg-opus-n8.0.1.patch']), (root / 'native/ffmpeg-android-builder/atempo.patch', lock['patches']['native/ffmpeg-android-builder/atempo.patch'])]
    for patch, expected in patches:
        if digest(patch) != expected:
            raise ValueError('Reviewed native patch differs')
    pre = root / 'native/prebuilt'; builder = root / 'native/ffmpeg-android-builder'
    old_root = '/Users/marc/Documents/git/nova-github'
    old_tc = '/opt/android-sdk/ndk/30.0.14904198/toolchains/llvm/prebuilt/darwin-x86_64'
    for abi in ABIS:
        target = workspace / 'build' / abi
        if target.exists():
            raise ValueError('Build workspace already exists; use a fresh isolated workspace')
        shutil.copytree(source_roots['ffmpeg'], target)
        for patch, _ in patches:
            subprocess.run(['patch', '--batch', '--fuzz=0', '-p1', '-i', str(patch)], cwd=target, check=True, stdout=subprocess.DEVNULL)
        original = configuration(pre / 'ffmpeg' / ('dist-full-' + abi) / 'lib/libavcodec.so')
        options = [value.replace(old_tc, str(tc)).replace(old_root, str(root)) for value in shlex.split(original)]
        output = workspace / 'candidate' / ('dist-full-' + abi)
        options = [value.replace(str(pre / 'ffmpeg' / ('dist-full-' + abi)), str(output)).replace(str(root / 'native/dav1d-android-builder/dav1d/include'), str(source_roots['dav1d_headers'] / 'include')).replace(str(root / 'native/opus-android-builder/opus/include'), str(source_roots['opus_headers'] / 'include')) for value in options]
        pcs = workspace / 'pkgconfig' / abi; pcs.mkdir(parents=True)
        for filename in ['opus.pc', 'dav1d.pc']:
            shutil.copyfile(builder / filename, pcs / filename)
        for pc in (pre / 'openssl' / ('dist-' + abi) / 'lib/pkgconfig').glob('*.pc'):
            (pcs / pc.name).write_text(pc.read_text().replace(old_root, str(root)))
        env = os.environ.copy()
        env.update(PKG_CONFIG_LIBDIR=str(pcs), PKG_CONFIG_PATH='', SOURCE_DATE_EPOCH='1763605273', PATH=str(tc / 'bin') + ':' + env['PATH'])
        print('Building pinned FFmpeg n8.0.1 candidate:', abi, flush=True)
        with (workspace / (abi + '.log')).open('w') as log:
            subprocess.run([str(target / 'configure'), *options], cwd=target, env=env, check=True, stdout=log, stderr=subprocess.STDOUT)
            check_configuration(target, json.loads((video / 'docs/foundation/FFMPEG_REBUILD_REVIEWED.json').read_text())['abis'], abi)
            subprocess.run(['make', '-j' + str(jobs), 'install'], cwd=target, env=env, check=True, stdout=log, stderr=subprocess.STDOUT)
        (workspace / ('configure-' + abi + '.json')).write_text(json.dumps(options, indent=2) + '\n')


def source_bundle(root, workspace):
    """Publish corresponding library source and recipes from an explicit allowlist."""
    video = root / 'Video'
    lock = json.loads((video / '.github/build/ffmpeg-foundation.lock.json').read_text())
    staging = workspace / 'corresponding-source'
    staging.mkdir(exist_ok=False)
    source = staging / 'FFmpeg-n8.0.1'
    with tarfile.open(workspace / 'downloads/ffmpeg.tar.gz') as packed:
        packed.extractall(staging / 'upstream', filter='data')
    shutil.move(next((staging / 'upstream').iterdir()), source)
    (staging / 'upstream').rmdir()
    patches = [video / '.github/build/ffmpeg-opus-n8.0.1.patch', root / 'native/ffmpeg-android-builder/atempo.patch']
    for patch in patches:
        subprocess.run(['patch', '--batch', '--fuzz=0', '-p1', '-i', str(patch)], cwd=source, check=True, stdout=subprocess.DEVNULL)
        shutil.copyfile(patch, staging / patch.name)
    for path in ['.github/build/build-foundation-ffmpeg.py', '.github/build/ffmpeg-foundation.lock.json', '.github/build/nova-ci.xml', 'docs/foundation/FFMPEG_REBUILD.md', 'docs/foundation/FFMPEG_SOURCE_PROVENANCE.json']:
        shutil.copyfile(video / path, staging / Path(path).name)
    # Stable public source URL/hash for the unchanged static OpenSSL module.
    (staging / 'external-source-inputs.json').write_text(json.dumps(lock['static_openssl_source'], indent=2) + '\n')
    for abi in ABIS:
        target = staging / ('configuration-' + abi)
        target.mkdir()
        for name in ['config.h', 'config_components.h', 'ffbuild/config.mak']:
            shutil.copyfile(workspace / 'build' / abi / name, target / Path(name).name)
    archive = workspace / 'Supernova-Foundation-FFmpeg-Source.tar.xz'
    with tarfile.open(archive, 'w:xz') as packed:
        for path in sorted(staging.rglob('*')):
            info = packed.gettarinfo(str(path), 'Supernova-Foundation-FFmpeg-Source/' + str(path.relative_to(staging)))
            info.uid = info.gid = 0
            info.uname = info.gname = ''
            info.mtime = 1763605273
            if path.is_file():
                with path.open('rb') as stream:
                    packed.addfile(info, stream)
            else:
                packed.addfile(info)
    return archive


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--root', type=Path, required=True)
    parser.add_argument('--workspace', type=Path, required=True)
    parser.add_argument('--jobs', type=int, default=4)
    parser.add_argument('--verify-existing', action='store_true')
    parser.add_argument('--install', action='store_true')
    parser.add_argument('--source-bundle', action='store_true')
    args = parser.parse_args()
    root = args.root.resolve(); workspace = args.workspace.resolve(); video = root / 'Video'
    if not args.verify_existing:
        build(root, workspace, video, args.jobs)
    reviewed = json.loads((video / 'docs/foundation/FFMPEG_REBUILD_REVIEWED.json').read_text())['abis']
    report = check_candidate(root, workspace, reviewed)
    (workspace / 'verified-native-build.json').write_text(json.dumps(report, indent=2) + '\n')
    if args.install:
        regression = json.loads((workspace / 'native-runtime-regression.json').read_text())
        if (regression.get('all_comparisons_passed') is not True
                or regression.get('total_decode_cases') != 64
                or {item['abi'] for item in regression.get('abis', []) if item.get('all_comparisons_passed') is True} != set(ABIS)
                or regression.get('candidate_libraries') != {abi: report['abis'][abi]['libraries'] for abi in ABIS}):
            raise ValueError('All four ABI runtime regressions must pass for these exact candidate bytes before installation')
        for abi in ABIS:
            target = root / 'native/prebuilt/ffmpeg' / ('dist-full-' + abi)
            shutil.copytree(workspace / 'candidate' / ('dist-full-' + abi), target, dirs_exist_ok=True)
        print('Verified candidates installed into isolated Foundation dependency tree only.')
    else:
        print('All four isolated candidates passed native ABI/configuration/licence checks; baseline untouched.')
    if args.source_bundle:
        archive = source_bundle(root, workspace)
        print('Corresponding FFmpeg source bundle prepared:', archive.name, digest(archive))

if __name__ == '__main__':
    main()
