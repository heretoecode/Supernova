#!/usr/bin/env python3
"""Offline apply/contract checks in a disposable clone; no NDK/Gradle or CI writes."""
import argparse
import hashlib
import json
import subprocess
import sys
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
BUILD = ROOT / ".github/build"

CONTRACT = r'''
#include <assert.h>
#include <stdint.h>
#include <string.h>
#include <sys/types.h>
#include "avos_mp_metadata.h"
#include "avos_mr_metadata.h"
#include "stream_hls_compat.h"
_Static_assert(AVOS_MP_METADATA_VIDEO_TRACK_DOVI_PROFILE == 15, "MP DV key changed");
_Static_assert(AVOS_MP_METADATA_VIDEO_TRACK_HDR10_PLUS == 16, "MP HDR10+ key changed");
_Static_assert(AVOS_MP_METADATA_VIDEO_TRACK_DOVI_COMPAT == 17, "MP DV compatibility changed");
_Static_assert(AVOS_MP_METADATA_VIDEO_TRACK_MAX == 18, "MP track stride changed");
_Static_assert(AVOS_MR_METADATA_VIDEO_TRACK_DOVI_PROFILE == 14, "MR DV key changed");
_Static_assert(AVOS_MR_METADATA_VIDEO_TRACK_HDR10_PLUS == 15, "MR HDR10+ key changed");
_Static_assert(AVOS_MR_METADATA_VIDEO_TRACK_DOVI_COMPAT == 16, "MR DV compatibility changed");
_Static_assert(AVOS_MR_METADATA_VIDEO_TRACK_MAX == 17, "MR track stride changed");
_Static_assert(AVOS_MP_METADATA_NB_CHAPTER == 9003, "chapter count key changed");
_Static_assert(AVOS_MP_METADATA_CHAPTER == 40000, "chapter base key changed");
_Static_assert(AVOS_MP_METADATA_CHAPTER_START == 0, "chapter time key changed");
_Static_assert(AVOS_MP_METADATA_CHAPTER_TITLE == 1, "chapter title key changed");
_Static_assert(AVOS_MP_METADATA_CHAPTER_MAX == 2, "chapter stride changed");
int main(void) {
    const char source[] = "hls+https://media.invalid/playlist.m3u8?token=unchanged";
    const char *backend = source;
    assert(stream_hls_compat_url(&backend) == 1);
    assert(backend == source + 4);
    assert(strcmp(backend, "https://media.invalid/playlist.m3u8?token=unchanged") == 0);
    assert(strncmp(source, "hls+", 4) == 0); /* caller's URI was not rewritten */
    const char *ordinary = "https://media.invalid/video.mkv";
    const char *original = ordinary;
    assert(stream_hls_compat_url(&ordinary) == 0 && ordinary == original);
    const char *empty = "";
    const char *missing = 0;
    assert(stream_hls_compat_url(&empty) == 0);
    assert(stream_hls_compat_url(&missing) == 0);
    assert(stream_hls_compat_url(0) == 0);
    return 0;
}
'''

HOST_TESTS = (
    "test_video_hdr.py", "test_video_decoder_candidates.py", "test_dovi_avc.py",
    "test_dovi_recovery.py", "test_android_surface_validation.py",
    "test_pause_resume_policy.py", "test_atempo_missing_state.py",
)


def run(arguments, cwd=None):
    result = subprocess.run(arguments, cwd=cwd, capture_output=True, text=True, timeout=180)
    if result.returncode:
        raise RuntimeError("Command failed: " + " ".join(map(str, arguments))
                           + "\n" + result.stdout + result.stderr)
    return result.stdout.strip(), result.stderr.strip()


def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def require(condition, message):
    if not condition:
        raise ValueError(message)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--avos", required=True, type=Path,
                        help="local upstream repository containing the frozen tip and blobs")
    parser.add_argument("--run-host-tests", action="store_true")
    args = parser.parse_args()
    lock = json.loads((BUILD / "native-supernova156.lock.draft.json").read_text())
    require(sha(ROOT / lock["sources"]["third_party_native_stack"]["lock"]) ==
            lock["sources"]["third_party_native_stack"]["sha256"], "Old stack lock differs")
    with tempfile.TemporaryDirectory(prefix="supernova156-native-") as temporary:
        directory = Path(temporary)
        work = directory / "avos"
        run(["git", "clone", "--quiet", "--shared", "--no-checkout", str(args.avos.resolve()), str(work)])
        run(["git", "checkout", "--quiet", "--detach", lock["sources"]["avos"]["commit"]], work)
        tree, _ = run(["git", "rev-parse", "HEAD^{tree}"], work)
        require(tree == lock["sources"]["avos"]["tree"], "Unexpected upstream tree")
        for operation in lock["patches"]:
            patch = ROOT / operation["path"]
            require(sha(patch) == operation["sha256"], "Draft patch changed: " + patch.name)
            run(["git", "apply", "--check", str(patch)], work)
            run(["git", "apply", "--index", str(patch)], work)
            print("PASS ordered git apply --check:", patch.name, flush=True)
        tree, _ = run(["git", "write-tree"], work)
        require(tree == lock["expected_patched_avos_tree"], "Patched tree differs from measured draft")
        probe = directory / "contract.c"
        probe.write_text(CONTRACT)
        executable = directory / "contract"
        run(["cc", "-std=c11", "-Wall", "-Wextra", "-Werror", "-I" + str(work / "public"),
             "-I" + str(work / "Include"), str(probe), "-o", str(executable)])
        run([str(executable)])
        print("PASS native metadata/chapter key ABI and actual HLS helper", flush=True)
        executable = directory / "buffer-limits"
        run(["cc", "-std=c11", "-Wall", "-Wextra", "-Werror", "-I" + str(work / "Include"),
             str(work / "test/stream_buffer_limits.c"), "-o", str(executable)])
        run([str(executable)])
        print("PASS retained upstream buffer boundary test", flush=True)
        if args.run_host_tests:
            for test in HOST_TESTS:
                stdout, stderr = run([sys.executable, "-m", "unittest", "discover", "-s", "test", "-p", test], work)
                print("PASS", test, stdout, stderr, flush=True)
        print("Verified draft tree:", tree, flush=True)
    print("Original repository unchanged; NDK, real JNI/parcel, full libass and device checks remain separate.")


if __name__ == "__main__":
    main()
