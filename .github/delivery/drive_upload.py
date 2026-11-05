#!/usr/bin/env python3
"""Upload a verified signed APK to the owner's Google Drive and prove the upload byte-for-byte.

Owner decision (2 Nov 2026): signed QA builds are delivered to Google Drive automatically so the
APK can be installed on the Shield. Uses the owner's own OAuth client and refresh token with the
narrow ``drive.file`` scope: the workflow can only see files and folders it created itself, never
the rest of the owner's Drive.

Credentials come from environment variables (GitHub secrets) and are never printed:
  GDRIVE_CLIENT_ID, GDRIVE_CLIENT_SECRET, GDRIVE_REFRESH_TOKEN
If any is missing the step reports that delivery is not configured and exits successfully, so a
missing Drive setup never blocks the signed build itself.

Standard library only.
"""
import argparse
import hashlib
import json
import os
import sys
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path

FOLDER_NAME = 'Supernova APKs'
TOKEN_URL = 'https://oauth2.googleapis.com/token'
FILES_URL = 'https://www.googleapis.com/drive/v3/files'
UPLOAD_URL = 'https://www.googleapis.com/upload/drive/v3/files'
CHUNK = 8 * 1024 * 1024


class DeliveryError(Exception):
    pass


class _NoRedirect(urllib.request.HTTPRedirectHandler):
    """Resumable uploads answer 308 'Resume Incomplete' for accepted chunks; it is not a redirect."""
    def redirect_request(self, *args, **kwargs):
        return None


_CHUNK_OPENER = urllib.request.build_opener(_NoRedirect)


def request(method, url, token=None, data=None, headers=None, raw=False):
    headers = dict(headers or {})
    if token:
        headers['Authorization'] = 'Bearer ' + token
    req = urllib.request.Request(url, data=data, method=method, headers=headers)
    try:
        with urllib.request.urlopen(req, timeout=300) as response:
            body = response.read()
            return (response, body) if raw else json.loads(body or b'{}')
    except urllib.error.HTTPError as error:
        # Never echo request headers or bodies: they may contain credentials.
        raise DeliveryError('Google Drive request failed: HTTP %d on %s %s' % (error.code, method, urllib.parse.urlsplit(url).path)) from None


def access_token(env):
    form = urllib.parse.urlencode({
        'client_id': env['GDRIVE_CLIENT_ID'], 'client_secret': env['GDRIVE_CLIENT_SECRET'],
        'refresh_token': env['GDRIVE_REFRESH_TOKEN'], 'grant_type': 'refresh_token'}).encode()
    token = request('POST', TOKEN_URL, data=form, headers={'Content-Type': 'application/x-www-form-urlencoded'}).get('access_token')
    if not token:
        raise DeliveryError('Google did not issue an access token; the refresh token may have been revoked.')
    return token


def folder(token):
    query = "name = '%s' and mimeType = 'application/vnd.google-apps.folder' and trashed = false" % FOLDER_NAME
    found = request('GET', FILES_URL + '?' + urllib.parse.urlencode({'q': query, 'fields': 'files(id)', 'spaces': 'drive'}), token).get('files', [])
    if found:
        return found[0]['id']
    body = json.dumps({'name': FOLDER_NAME, 'mimeType': 'application/vnd.google-apps.folder'}).encode()
    return request('POST', FILES_URL + '?fields=id', token, body, {'Content-Type': 'application/json'})['id']


def upload(token, parent, apk):
    size = apk.stat().st_size
    metadata = json.dumps({'name': apk.name, 'parents': [parent]}).encode()
    response, _ = request('POST', UPLOAD_URL + '?uploadType=resumable&fields=id', token, metadata,
                          {'Content-Type': 'application/json; charset=UTF-8', 'X-Upload-Content-Type': 'application/vnd.android.package-archive',
                           'X-Upload-Content-Length': str(size)}, raw=True)
    session = response.headers['Location']
    result = None
    with apk.open('rb') as stream:
        offset = 0
        while offset < size:
            chunk = stream.read(CHUNK)
            end = offset + len(chunk) - 1
            req = urllib.request.Request(session, data=chunk, method='PUT', headers={
                'Content-Length': str(len(chunk)), 'Content-Range': 'bytes %d-%d/%d' % (offset, end, size)})
            try:
                with _CHUNK_OPENER.open(req, timeout=300) as done:
                    result = json.loads(done.read() or b'{}')
            except urllib.error.HTTPError as error:
                if error.code != 308:  # 308 = chunk accepted, continue
                    raise DeliveryError('Google Drive upload failed: HTTP %d' % error.code) from None
            offset = end + 1
    if not result or 'id' not in result:
        raise DeliveryError('Google Drive did not confirm the uploaded file.')
    return result['id']


def readback_sha256(token, file_id):
    digest = hashlib.sha256()
    req = urllib.request.Request(FILES_URL + '/' + file_id + '?alt=media', headers={'Authorization': 'Bearer ' + token})
    with urllib.request.urlopen(req, timeout=600) as response:
        for block in iter(lambda: response.read(1 << 20), b''):
            digest.update(block)
    return digest.hexdigest()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--apk', type=Path, required=True)
    parser.add_argument('--receipt', type=Path, required=True)
    args = parser.parse_args()
    env = os.environ
    if not all(env.get(name) for name in ('GDRIVE_CLIENT_ID', 'GDRIVE_CLIENT_SECRET', 'GDRIVE_REFRESH_TOKEN')):
        print('Google Drive delivery is not configured (secrets absent); the signed APK remains a workflow artifact.')
        args.receipt.write_text(json.dumps({'delivered': False, 'reason': 'not configured'}, indent=2) + '\n')
        return 0
    expected = hashlib.sha256(args.apk.read_bytes()).hexdigest()
    try:
        token = access_token(env)
        parent = folder(token)
        file_id = upload(token, parent, args.apk)
        actual = readback_sha256(token, file_id)
    except DeliveryError as error:
        print(str(error), file=sys.stderr)
        return 1
    if actual != expected:
        print('Drive readback differs from the signed APK; do not install it.', file=sys.stderr)
        return 1
    receipt = {'delivered': True, 'folder': FOLDER_NAME, 'file_id': file_id, 'filename': args.apk.name,
               'url': 'https://drive.google.com/file/d/%s/view?usp=drivesdk' % file_id,
               'apk_bytes': args.apk.stat().st_size, 'apk_sha256': expected, 'readback_sha256': actual,
               'complete_drive_readback_byte_equal': True}
    args.receipt.write_text(json.dumps(receipt, indent=2) + '\n')
    summary = env.get('GITHUB_STEP_SUMMARY')
    if summary:
        with open(summary, 'a') as out:
            out.write('### %s delivered to Google Drive\n\nFolder: **%s**  \n[Open in Drive](%s)  \nSHA-256 `%s` (download verified byte-for-byte)\n'
                      % (args.apk.name, FOLDER_NAME, receipt['url'], expected))
    print('Delivered %s to Google Drive folder "%s" and verified the complete download.' % (args.apk.name, FOLDER_NAME))
    return 0


if __name__ == '__main__':
    sys.exit(main())
