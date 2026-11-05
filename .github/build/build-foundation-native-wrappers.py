#!/usr/bin/env python3
"""Build pinned Foundation wrappers without invoking dependency-upgrading bootstraps."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import shutil
import subprocess
import xml.etree.ElementTree as ET

ABIS = ('armeabi-v7a', 'arm64-v8a', 'x86', 'x86_64')

def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--root', type=Path, required=True)
    parser.add_argument('--native-evidence', type=Path, required=True)
    parser.add_argument('--output', type=Path, required=True)
    args = parser.parse_args(); root = args.root.resolve()
    manifest = ET.parse(root / 'Video/.github/build/nova-ci.xml').getroot()
    paths = {'AVP', 'MediaLib', 'FileCoreLibrary', 'native/avos', 'native/libyuv',
             'native/libnativehelper', 'native/torrentd', 'native/prebuilt/torrentd',
             'native/prebuilt/dav1d', 'native/prebuilt/opus', 'native/prebuilt/libmysofa'}
    revisions = {}
    for project in manifest.findall('project'):
        path = project.get('path', project.get('name'))
        if path not in paths: continue
        revision = subprocess.check_output(['git', '-C', str(root / path), 'rev-parse', 'HEAD'], text=True).strip()
        if revision != project.get('revision'): raise ValueError('Native dependency pin differs: ' + path)
        revisions[path] = revision
    if set(revisions) != paths: raise ValueError('Native dependency manifest is incomplete')
    evidence = json.loads(args.native_evidence.read_text())
    for abi in ABIS:
        for library in evidence['abis'][abi]['libraries']:
            if digest(root / 'native/prebuilt/ffmpeg' / ('dist-full-' + abi) / 'lib' / library['file']) != library['sha256']:
                raise ValueError('Installed rebuilt FFmpeg differs from verification')
    if not (root / 'Makefile').exists(): shutil.copyfile(root / 'AVP/Makefile', root / 'Makefile')
    # DEBUG is the existing native wrapper configuration used by the 0.137 release
    # pipeline. The Android application remains an unsigned noamazonRelease build.
    sdk = os.environ.get('ANDROID_SDK_ROOT') or os.environ.get('ANDROID_HOME')
    if not sdk: raise ValueError('Android SDK path is required')
    ndk = Path(sdk) / 'ndk/29.0.14206865'
    if not (ndk / 'ndk-build').is_file(): raise ValueError('Install pinned wrapper NDK 29.0.14206865 first')
    environment = dict(os.environ, LIBAV_CONFIG='full', BUILD='DEBUG')
    command = ['make', 'BUILD=DEBUG', 'LIBAV_CONFIG=full', 'MAKE_JOBS=4', 'android_ndk=' + str(ndk)]
    # AVOS links these static archives. Separate phases also preserve ordering if
    # a caller supplies parallel MAKEFLAGS; an existing local obj/ masked this in CI.
    subprocess.run(command + ['native_libyuv', 'native_libnativehelper', 'native_build_FileCoreLibrary'], cwd=root, env=environment, check=True)
    subprocess.run(command + ['native_build_native/avos', 'native_torrentd'], cwd=root, env=environment, check=True)
    libraries = []
    for abi in ABIS:
        destination = root / 'MediaLib/libs' / abi; destination.mkdir(parents=True, exist_ok=True)
        sources = [root / path / 'libs' / abi for path in ('native/avos', 'native/libyuv', 'native/libnativehelper')]
        sources.append(root / 'native/prebuilt/ffmpeg' / ('dist-full-' + abi) / 'lib')
        for source in sources:
            for library in source.glob('*.so'): shutil.copyfile(library, destination / library.name)
        for dependency in ('dav1d', 'opus', 'libmysofa'):
            library = root / 'native/prebuilt' / dependency / 'lib' / abi / ('lib' + dependency.removeprefix('lib') + '.so')
            shutil.copyfile(library, destination / library.name)
        for module in ('MediaLib', 'FileCoreLibrary'):
            for library in sorted((root / module / 'libs' / abi).glob('*.so')):
                libraries.append({'path': str(library.relative_to(root)), 'sha256': digest(library)})
    args.output.write_text(json.dumps({'schema': 1, 'native_build': 'DEBUG', 'libav_config': 'full', 'wrapper_ndk': ndk.name,
        'dependency_revisions': revisions, 'ffmpeg_source_commit': evidence['source_commit'],
        'native_evidence_sha256': digest(args.native_evidence), 'libraries': libraries}, indent=2) + '\n')
    print('Pinned native wrappers and packaging inputs recorded; dependency bootstraps were not invoked.')

if __name__ == '__main__': main()
