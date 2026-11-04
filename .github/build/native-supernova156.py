#!/usr/bin/env python3
"""Shared validation for the engine recipe; no network or build on import."""
import hashlib
import importlib.util
import json
import subprocess
import tarfile
from pathlib import Path, PurePosixPath

BUILD = Path(__file__).resolve().parent
VIDEO = BUILD.parents[1]
ABI = "arm64-v8a"


def sha(path):
    with Path(path).open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def require(condition, message):
    if not condition:
        raise ValueError(message)


def run(arguments, cwd=None, **kwargs):
    return subprocess.run(list(map(str, arguments)), cwd=cwd, check=True, **kwargs)


def git(directory, *arguments):
    return subprocess.check_output(["git", "-C", str(directory), *arguments], text=True).strip()


def load_module(name):
    spec = importlib.util.spec_from_file_location(name.replace("-", "_"), BUILD / (name + ".py"))
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


def load_lock():
    lock = json.loads((BUILD / "native-supernova156.lock.json").read_text())
    stack = lock["sources"]["third_party_native_stack"]
    require(sha(VIDEO / stack["lock"]) == stack["sha256"], "Old native lock differs")
    old = json.loads((VIDEO / stack["lock"]).read_text())
    require(stack["sources"] == old["sources"] and stack["ndk"] == old["ndk"],
            "Third-party sources/NDK differ")
    for name, expected in old["recipes"].items():
        require(sha(BUILD / name) == expected, "Old native recipe differs: " + name)
    for path, expected in lock["recipe_hashes"].items():
        require(sha(VIDEO / path) == expected, "Engine recipe differs: " + path)
    patches = lock["patches"]
    require([item["order"] for item in patches] == list(range(1, 6)), "Invalid patch order")
    for item in patches:
        require(sha(VIDEO / item["path"]) == item["sha256"], "Engine patch differs")
    return lock


def checkout(source, destination):
    require(not destination.exists(), "Fresh source destination required")
    destination.parent.mkdir(parents=True, exist_ok=True)
    run(["git", "clone", "--filter=blob:none", "--no-checkout", source["repository"], destination])
    run(["git", "checkout", "--detach", source["commit"]], cwd=destination)
    require(git(destination, "rev-parse", "HEAD") == source["commit"], "Source commit differs")
    if "tree" in source:
        require(git(destination, "rev-parse", "HEAD^{tree}") == source["tree"], "Source tree differs")


def apply_avos(avos, lock):
    require(git(avos, "rev-parse", "HEAD") == lock["sources"]["avos"]["commit"], "Wrong avos tip")
    require(not git(avos, "status", "--porcelain"), "Fresh avos checkout required")
    for item in lock["patches"]:
        patch = VIDEO / item["path"]
        require(sha(patch) == item["sha256"], "Engine patch differs")
        run(["git", "apply", "--check", patch], cwd=avos)
        run(["git", "apply", "--index", patch], cwd=avos)
    require(git(avos, "write-tree") == lock["expected_patched_avos_tree"], "Patched avos tree differs")


def safe_name(value):
    path = PurePosixPath(value)
    require(bool(value) and not path.is_absolute() and ".." not in path.parts
            and str(path) == value, "Unsafe archive path")
    return path


def unpack(archive, destination):
    # Reject links and special files before extraction, including escaping links.
    with tarfile.open(archive, "r:gz") as packed:
        members = packed.getmembers()
        require(len({item.name for item in members}) == len(members), "Duplicate archive member")
        for item in members:
            safe_name(item.name)
            require(item.isfile() or item.isdir(), "Archive links/special files forbidden")
        packed.extractall(destination, filter="data")


def inventory(root):
    return [{"path": str(path.relative_to(root)), "bytes": path.stat().st_size, "sha256": sha(path)}
            for path in sorted(root.rglob("*")) if path.is_file()]


def verify_payload(root, receipt, lock, source_commit):
    require(receipt["abi"] == ABI and receipt["source_commit"] == source_commit,
            "Unexpected ABI or recipe source commit")
    require(receipt["native_lock_sha256"] == sha(BUILD / "native-supernova156.lock.json")
            and receipt["avos_commit"] == lock["sources"]["avos"]["commit"]
            and receipt["avos_tree"] == lock["expected_patched_avos_tree"]
            and receipt["patches"] == lock["patches"]
            and receipt["recipe_hashes"] == lock["recipe_hashes"], "Engine provenance differs")
    actual = inventory(root / "libraries")
    require(actual and actual == receipt["libraries"], "Native payload inventory differs")
    required = {"libavos.so", "libavosjni.so", "libnvpnativehelper.so"}
    require(required <= {Path(item["path"]).name for item in actual}, "Missing engine DSOs")
    for item in actual:
        parts = safe_name(item["path"]).parts
        require(len(parts) == 4 and parts[0] in {"MediaLib", "FileCoreLibrary"}
                and parts[1:3] == ("libs", ABI) and parts[3].endswith(".so"),
                "Unexpected native library path")
