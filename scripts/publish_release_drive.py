#!/usr/bin/env python3
"""Publish an already verified APK and HTML changelog to Google Drive.

Only called by the manually approved release workflow. No Android source changes.
"""
import argparse
import hashlib
import json
import os
from pathlib import Path
from urllib import parse, request, error

API = "https://www.googleapis.com"
FOLDER = "1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM"
HTML_ID = "17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW"
MARKER = "<!-- SUPERNOVA-RELEASE-ENTRIES -->"

def http(url, method="GET", data=None, headers=None):
    req = request.Request(url, data=data, method=method, headers=headers or {})
    with request.urlopen(req, timeout=120) as res:
        return res.read()

def token():
    fields = parse.urlencode({
        "client_id": os.environ["GOOGLE_OAUTH_CLIENT_ID"],
        "client_secret": os.environ["GOOGLE_OAUTH_CLIENT_SECRET"],
        "refresh_token": os.environ["GOOGLE_OAUTH_REFRESH_TOKEN"],
        "grant_type": "refresh_token",
    }).encode()
    data = json.loads(http("https://oauth2.googleapis.com/token", "POST", fields,
                           {"Content-Type": "application/x-www-form-urlencoded"}))
    return data["access_token"]

def auth(t):
    return {"Authorization": "Bearer " + t}

def meta(t, fid):
    return json.loads(http(f"{API}/drive/v3/files/{fid}?fields=id,name,mimeType,size,parents,md5Checksum", headers=auth(t)))

def folder_matches(t, filename):
    query = parse.urlencode({"q": f"'{FOLDER}' in parents and name = '{filename}' and trashed = false",
                             "fields": "files(id,name,size,md5Checksum),nextPageToken", "page_size": 100})
    return json.loads(http(f"{API}/drive/v3/files?{query}", headers=auth(t)))["files"]

def upload_new(t, filename, content):
    metadata = json.dumps({"name": filename, "parents": [FOLDER]}).encode()
    boundary = "supernova-release-upload"
    body = (b"--" + boundary.encode() + b"\r\nContent-Type: application/json; charset=UTF-8\r\n\r\n"
            + metadata + b"\r\n--" + boundary.encode()
            + b"\r\nContent-Type: application/vnd.android.package-archive\r\n\r\n"
            + content + b"\r\n--" + boundary.encode() + b"--\r\n")
    return json.loads(http(f"{API}/upload/drive/v3/files?uploadType=multipart&fields=id,name,size,md5Checksum",
                           "POST", body, {**auth(t), "Content-Type": f"multipart/related; boundary={boundary}"}))

def update_html(t, version, notes):
    m = meta(t, HTML_ID)
    if m["name"] != "Supernova Development.html":
        raise RuntimeError("Unexpected HTML file identity; refusing update")
    old = http(f"{API}/drive/v3/files/{HTML_ID}?alt=media", headers=auth(t)).decode("utf-8")
    from html import escape
    section_id = f"supernova-release-{version}"
    if f'id="{section_id}"' in old:
        print("HTML already includes this version; no duplicate entry")
        return
    if MARKER not in old:
        if "</body>" not in old:
            raise RuntimeError("Changelog missing insertion point; refusing overwrite")
        old = old.replace("</body>", MARKER + "\\n</body>", 1)
    section = f'<section id="{section_id}"><h2>Supernova {escape(version)}</h2><pre style="white-space:pre-wrap;font:inherit">{escape(notes)}</pre></section>\n'
    updated = old.replace(MARKER, MARKER + "\n" + section, 1).encode("utf-8")
    http(f"{API}/upload/drive/v3/files/{HTML_ID}?uploadType=media&fields=id,name,size",
         "PATCH", updated, {**auth(t), "Content-Type": "text/html; charset=utf-8"})
    verify = http(f"{API}/drive/v3/files/{HTML_ID}?alt=media", headers=auth(t)).decode("utf-8")
    if f'id="{section_id}"' not in verify:
        raise RuntimeError("Changelog update verification failed")

def main():
    p = argparse.ArgumentParser()
    p.add_argument("--apk", required=True)
    p.add_argument("--version", required=True)
    p.add_argument("--notes", required=True)
    p.add_argument("--sha256", required=True)
    args = p.parse_args()
    if not args.version or any(c not in "0123456789." for c in args.version):
        raise RuntimeError("Invalid numeric version")
    apk = Path(args.apk)
    expected = f"Supernova_{args.version}.apk"
    if apk.name != expected:
        raise RuntimeError(f"APK filename must be {expected}")
    data = apk.read_bytes()
    digest = hashlib.sha256(data).hexdigest()
    if digest.lower() != args.sha256.lower():
        raise RuntimeError("SHA-256 mismatch; refusing release")
    t = token()
    folder = meta(t, FOLDER)
    if folder["mimeType"] != "application/vnd.google-apps.folder":
        raise RuntimeError("Destination is not a folder")
    existing = folder_matches(t, expected)
    if existing:
        import base64
        import hashlib as h
        md5 = h.md5(data).hexdigest()
        if len(existing) != 1 or existing[0].get("md5Checksum") != md5:
            raise RuntimeError("Existing APK with same name differs; refusing overwrite")
        print("Existing Drive APK matches checksum")
    else:
        result = upload_new(t, expected, data)
        if result.get("md5Checksum") != hashlib.md5(data).hexdigest():
            raise RuntimeError("Uploaded Drive APK MD5 verification failed")
        print("Uploaded Drive APK", result["id"])
    update_html(t, args.version, Path(args.notes).read_text(encoding="utf-8"))
    print("Verified Drive APK and HTML delivery")

if __name__ == "__main__":
    main()
