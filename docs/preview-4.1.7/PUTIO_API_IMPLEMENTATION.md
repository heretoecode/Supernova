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

PutioAssociationStore now persists stable IDs in a separate SQLite database,
without updating Video rows or their metadata, resume, artwork, versions or rows.
Per-folder generations reject stale results; partial snapshots cannot attach IDs.
Conflicting identities abort the entire transaction. Missing IDs become review
flags, never deletions. New or ambiguous files prevent activation. Disconnect
requires an explicit inactive/generic discovery choice and retains links. Source
URIs reject user-info/query/fragment credentials. Six database tests cover these
boundaries; Android CI validation is pending for this checkpoint.

This store is not yet wired to live source selection or a scanner ownership gate.
Its activation method must not be used until generic-discovery exclusion is ready.
Credential lifecycle integration, association UI, scanner ownership hand-off and live
account validation remain implementation/integration work. Do not label the native
put.io feature complete on the strength of policy tests alone.
