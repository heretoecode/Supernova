#!/usr/bin/env python3
"""Build the pinned OpenSSL4.1 static runtime for the preserved Android API floor."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import subprocess

TARGETS = {
    'armeabi-v7a': ('android-arm', 'armv7a-linux-androideabi'),
    'arm64-v8a': ('android-arm64', 'aarch64-linux-android'),
    'x86': ('android-x86', 'i686-linux-android'),
    'x86_64': ('android-x86_64', 'x86_64-linux-android'),
}

def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument('--source', type=Path, required=True)
    p.add_argument('--ndk', type=Path, required=True)
    p.add_argument('--workspace', type=Path, required=True)
    p.add_argument('--prefix', type=Path, required=True)
    p.add_argument('--abi', choices=TARGETS, required=True)
    p.add_argument('--jobs', type=int, default=4)
    a = p.parse_args()
    target, triple = TARGETS[a.abi]
    source, ndk, work, prefix = [x.resolve() for x in (a.source, a.ndk, a.workspace, a.prefix)]
    compiler = ndk / 'toolchains/llvm/prebuilt/linux-x86_64/bin'
    if not (compiler / (triple + '21-clang')).is_file():
        raise ValueError('Pinned Android API21 compiler is required')
    work.mkdir(parents=True, exist_ok=True)
    env = dict(os.environ, ANDROID_NDK_ROOT=str(ndk), ANDROID_NDK_HOME=str(ndk),
               PATH=str(compiler) + os.pathsep + os.environ['PATH'],
               CC=str(compiler / (triple + '21-clang')), AR=str(compiler / 'llvm-ar'),
               RANLIB=str(compiler / 'llvm-ranlib'), CFLAGS='-Os -fPIC',
               LDFLAGS='-Wl,-Bsymbolic,-z,max-page-size=16384')
    # Keep file-backed CA loading available. No certificate/TLS validation bypass
    # patch is imported; no-shared binds these exact archives into rebuilt FFmpeg.
    command = ['perl', str(source / 'Configure'), target, '-D__ANDROID_API__=21',
               'no-shared', 'no-tests', 'no-ui-console', '--prefix=' + str(prefix),
               '--openssldir=/system/etc/security']
    with (work / 'build.log').open('w') as log:
        for cmd in (command, ['make', '-j' + str(a.jobs)], ['make', 'install_sw']):
            subprocess.run(cmd, cwd=work, env=env, check=True, stdout=log, stderr=subprocess.STDOUT)
    libraries = []
    for name in ('libcrypto.a', 'libssl.a'):
        matches = list(prefix.glob('lib*/' + name))
        if len(matches) != 1: raise ValueError('Missing/ambiguous installed OpenSSL archive: ' + name)
        f = matches[0]
        libraries.append({'file': str(f.relative_to(prefix)), 'sha256': hashlib.sha256(f.read_bytes()).hexdigest(), 'bytes': f.stat().st_size})
    (work / 'evidence.json').write_text(json.dumps({'abi': a.abi, 'android_api': 21,
        'configure': command, 'libraries': libraries, 'stdio_ca_loading_enabled': True}, indent=2) + '\n')
    print('Pinned OpenSSL4.1 static archives built for', a.abi)

if __name__ == '__main__': main()
