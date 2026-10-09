#!/usr/bin/env python3
"""Secret handling for Foundation only. Never serialise a password or private key."""
import argparse
import base64
import binascii
import html
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys

PREVIEW_CERT_SHA256 = "89ac087ed6f989c90482d4a999f80511fe6ceee26ef1b9c37a142a9f00d39a5a"
DIRECTORY = "supernova-foundation-signing"
SECRET_NAMES = ("SUPERNOVA_P12_BASE64", "SUPERNOVA_STORE_PASSWORD", "SUPERNOVA_KEY_ALIAS", "SUPERNOVA_KEY_PASSWORD")


class SigningError(Exception):
    """Only fixed, non-secret diagnostic messages belong here."""


def preflight(mode, env):
    if mode not in ("inspect", "validate", "prepare"):
        raise SigningError("Unsupported signing operation.")
    required = SECRET_NAMES[:2] if mode == "inspect" else SECRET_NAMES
    missing = [name for name in required if not env.get(name)]
    if missing:
        raise SigningError("Required Foundation Actions secrets missing: " + ", ".join(missing))
    # Do not accept control characters in alias; Java/Gradle still receive it via env.
    if mode != "inspect" and any(ord(c) < 32 for c in env["SUPERNOVA_KEY_ALIAS"]):
        raise SigningError("Key alias contains unsupported control characters.")
    if mode == "prepare":
        if env.get("FOUNDATION_RELEASE_READY") != "true":
            raise SigningError("Signed build blocked: Foundation identity/version/assets/licence readiness is not approved.")
        normalize_fingerprint(env.get("SUPERNOVA_CERT_SHA256", ""), required=True)
    # GitHub recommends against --debug/--stacktrace and runner debug with secrets.
    if env.get("RUNNER_DEBUG") == "1" or env.get("ACTIONS_STEP_DEBUG", "").lower() == "true":
        raise SigningError("Disable Actions/runner debug logging before using signing secrets.")


def normalize_fingerprint(value, required=False):
    result = value.replace(":", "").strip().lower()
    if not result and not required:
        return ""
    if not re.fullmatch(r"[0-9a-f]{64}", result):
        raise SigningError("Set SUPERNOVA_CERT_SHA256 to the independently verified public certificate SHA-256.")
    if result == PREVIEW_CERT_SHA256:
        raise SigningError("Foundation must not reuse the Preview signing certificate.")
    return result


def decode_keystore(value):
    # Native iOS Shortcuts can wrap standard base64; only ASCII whitespace accepted.
    compact = re.sub(r"[ \t\r\n]", "", value)
    if not compact or len(compact) > 48 * 1024:
        raise SigningError("Encoded keystore is empty or exceeds the Actions secret limit.")
    try:
        data = base64.b64decode(compact, validate=True)
    except (binascii.Error, ValueError):
        raise SigningError("Keystore secret is not standard base64 file contents.") from None
    if not data:
        raise SigningError("Decoded keystore is empty.")
    return data


def store_path(env):
    if not env.get("RUNNER_TEMP"):
        raise SigningError("RUNNER_TEMP is required for temporary signing material.")
    return Path(env["RUNNER_TEMP"]).resolve() / DIRECTORY


def remove_store(directory):
    # Never follow an attacker-created link or delete a caller-selected path.
    if directory.name != DIRECTORY or directory.is_symlink():
        raise SigningError("Unsafe temporary signing directory.")
    if directory.exists():
        shutil.rmtree(directory)


def verify(mode, keystore, env):
    helper = Path(__file__).with_name("FoundationKeystoreCheck.java")
    child_env = {name: value for name, value in env.items() if name != "SUPERNOVA_P12_BASE64"}
    # No secrets on argv; raw stdout/stderr are never forwarded to Actions logs.
    try:
        result = subprocess.run(["java", str(helper), "inspect" if mode == "inspect" else "validate", str(keystore)],
                                env=child_env, capture_output=True, text=True, timeout=60, check=False)
    except (OSError, subprocess.TimeoutExpired):
        raise SigningError("Java keystore verification could not run.") from None
    if result.returncode:
        raise SigningError("Keystore verification failed. Check PKCS#12 format, alias and passwords privately.")
    if mode == "inspect":
        entries = []
        for line in result.stdout.splitlines():
            try:
                encoded_alias, fingerprint = line.split(" ", 1)
                alias = base64.b64decode(encoded_alias, validate=True).decode("utf-8")
            except (ValueError, UnicodeError):
                raise SigningError("Invalid public key metadata returned by verifier.") from None
            fingerprint = normalize_fingerprint(fingerprint, required=True)
            if not alias or any(ord(c) < 32 for c in alias):
                raise SigningError("Alias must be inspected privately with a trusted local keytool.")
            entries.append({"alias": alias, "certificate_sha256": fingerprint})
        if not entries:
            raise SigningError("No private-key entry found in PKCS#12 keystore.")
        return entries
    fingerprint = normalize_fingerprint(result.stdout.strip(), required=True)
    expected = normalize_fingerprint(env.get("SUPERNOVA_CERT_SHA256", ""))
    if expected and fingerprint != expected:
        raise SigningError("Certificate does not match SUPERNOVA_CERT_SHA256. Refusing different identity.")
    return fingerprint


def execute(mode, env=None):
    env = dict(os.environ if env is None else env)
    preflight(mode, env)
    data = decode_keystore(env["SUPERNOVA_P12_BASE64"])
    directory = store_path(env)
    if directory.exists() or directory.is_symlink():
        raise SigningError("Temporary signing directory already exists. Refusing reuse.")
    directory.mkdir(mode=0o700)
    keystore = directory / "foundation.p12"
    keep = False
    try:
        fd = os.open(keystore, os.O_WRONLY | os.O_CREAT | os.O_EXCL | os.O_NOFOLLOW, 0o600)
        with os.fdopen(fd, "wb") as stream:
            stream.write(data)
        metadata = verify(mode, keystore, env)
        if mode == "inspect":
            summary = "## Foundation public alias inspection\n\nPrivate key/passwords are not exported. Key-password validation still required.\n\n"
            for entry in metadata:
                # HTML-escaped code spans prevent Markdown/workflow injection.
                summary += "- Alias: <code>" + html.escape(entry["alias"]) + "</code>; certificate SHA-256: <code>" + entry["certificate_sha256"] + "</code>\n"
        else:
            summary = "## Foundation signing validation\n\nPKCS#12 private-key entry, key password, key/certificate match and certificate validity checked. No APK signed.\n\nPublic certificate SHA-256: `" + metadata + "`\n"
        if env.get("GITHUB_STEP_SUMMARY"):
            with open(env["GITHUB_STEP_SUMMARY"], "a", encoding="utf-8") as stream:
                stream.write(summary)
        if mode == "prepare":
            if not env.get("GITHUB_ENV"):
                raise SigningError("GITHUB_ENV is required to pass the temporary keystore path.")
            path = str(keystore)
            if "\n" in path or "\r" in path:
                raise SigningError("Unsafe runner temporary path.")
            with open(env["GITHUB_ENV"], "a", encoding="utf-8") as stream:
                stream.write("SUPERNOVA_SIGNING_STORE_FILE=" + path + "\n")
            keep = True
        return metadata
    finally:
        if not keep:
            remove_store(directory)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("mode", choices=("preflight", "inspect", "validate", "prepare", "cleanup"))
    parser.add_argument("--operation", choices=("inspect", "validate", "prepare"), default="validate")
    args = parser.parse_args()
    try:
        if args.mode == "cleanup":
            remove_store(store_path(os.environ))
        elif args.mode == "preflight":
            preflight(args.operation, os.environ)
        else:
            execute(args.mode)
        print("Foundation signing operation passed; no secret values logged.")
    except (SigningError, OSError):
        # Print only our fixed SigningError messages, never system paths/secret data.
        failure = sys.exc_info()[1]
        print(str(failure) if isinstance(failure, SigningError) else "Temporary signing operation failed safely.", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
