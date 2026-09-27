# put.io adapter evidence and remaining integration

Authoritative scope: 26 September handover. Existing WebDAV remains the playback
transport. No transfers, download management, playback-position sync or new
playback transport is introduced.

Read-only API contracts checked against the provider's own SDK source at revision
`6af8983114b1415dab0d1ea58ed3349506345b92`:

- https://github.com/putdotio/putio-sdk-typescript/blob/6af8983114b1415dab0d1ea58ed3349506345b92/src/domains/files.ts
- https://github.com/putdotio/putio-sdk-typescript/blob/6af8983114b1415dab0d1ea58ed3349506345b92/src/domains/auth.ts
- https://github.com/putdotio/putio-sdk-typescript/blob/6af8983114b1415dab0d1ea58ed3349506345b92/src/core/http.ts

The official API documentation endpoint could not be retrieved in this environment;
the provider-maintained typed contracts above were used instead of third-party
wrappers. Listing uses GET `/v2/files/list`, an explicit page size and parent ID;
continuation uses POST `/v2/files/list/continue` with form cursor. Authenticated
requests use the provider's `Token` authorisation scheme, not a guessed Bearer
scheme or query-string credential. Redirects are disabled.

Implemented: bounded-response read adapter, strict page-envelope/identity checks,
recursive cursor traversal, total-count checks, cancellation/failure propagation,
conservative association policy and regression fixtures. The adapter returns only
selected file fields and sanitised errors, never raw response/error bodies or
credentials. An incomplete snapshot yields no reconciliation changes.

OOB contracts are confirmed in the provider SDK, but registered Supernova client
configuration and production linking-link validation are still absent. No live
account request has been made and no production token has been obtained or stored.
PutioTokenStore now provides device-local AES-256-GCM storage with an Android
Keystore encryption key and an atomic encrypted envelope in noBackupFilesDir.
This is not an APK signing key and has no relationship to NOVA_SIGNING_KEY_BASE64.
Read never creates a replacement key; authentication failure requires reconnect,
without deleting the encrypted evidence or touching library records. There is no
plaintext fallback or credential logging. Four tests use an injected test-only
symmetric key for round-trip, random IV, tamper, missing-key and invalid-input
checks; production Android Keystore/Shield behaviour remains unverified.
Platform contracts: https://developer.android.com/privacy-and-security/keystore
and https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec

PutioAssociationStore persists stable IDs in a separate SQLite database. The native
bridge imports through files_scanned and relocates existing native rows by media ID,
preserving metadata/history. Per-folder generations reject stale results; incomplete
snapshots cannot reconcile. Missing IDs become review flags, never deletions.
Ambiguous matches require Same File or Keep Separate. Disconnect explicitly chooses
inactive or generic discovery and retains links. Source URIs reject embedded
credentials. The provider discovery gate excludes owned roots and protects their
records during ancestor scans; generic destructive WebDAV operations are guarded.

Folder selection, reconnect, manual/scheduled sync, scanner ownership and resumable
source reassignment are wired. CI 36336639069 passed all source/backend/WebDAV gates,
including real native schema/trigger tests for import, preserved identity, interrupted
reassignment and incomplete listings. These fixtures do not verify a live account.

Search uses GET files/search and POST files/search/continue with a form cursor.
File information uses GET files/{id}?media_info=1. Only selected metadata and media
format/stream fields enter the display model; transport URLs and arbitrary response
fields are excluded. Search pagination is read-only and cannot feed library removal.
These additions still require their new CI run.

Production OAuth configuration, live account validation and physical Shield QA remain
outstanding. Connected-surface conformance and the complete requirements review also
remain open; source tests alone do not establish feature completion.
