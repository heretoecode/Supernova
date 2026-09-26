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
account request has been made and no token has been obtained or stored. Secure
credential persistence, association UI/store, scanner ownership hand-off and live
account validation remain implementation/integration work. Do not label the native
put.io feature complete on the strength of policy tests alone.
