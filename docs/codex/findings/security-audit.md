# Security and privacy audit — 0.157

Static boundary audit plus targeted safe fixes, 6 November 2026. This records confirmed exposure and retained
contracts, not a penetration-test certificate. Component inventory is in 0157-carry-forward-audit.md; the current
manifest adds two non-exported, same-UID listing services. Raw credentials and private server addresses are not
included in evidence. Dependency advisories/backports are in dependency-audit.md.

| ID | Boundary / evidence | Consequence / action | Severity / confidence | Disposition |
| --- | --- | --- | --- | --- |
| SEC157-01 | UpdateNextTask logs original locations and exception objects | Appenders can receive credentials, paths and server names before export redaction | User-visible privacy / high | Fixed: redact before log arguments; captured-appender regression retains original operation URI |
| SEC157-02 | PlayerService source/subtitle/next logs pass Uri objects | Same early-appender disclosure, even when a later report is clean | User-visible privacy / high | Fixed explicit source-URI arguments with shared redactor; real UPnP source-path appender regression |
| SEC157-03 | PlayerActivity start/new-intent/subtitle logging sites | Debug/trace output can contain media paths, intent headers or URLs | User-visible privacy / high | Screen file untouched under current instruction; hand off redaction with captured-appender tests |
| SEC157-04 | PlayerActivity STARTED_VIDEO_INTENT broadcast has no package/permission boundary | Other apps can observe title/thumbnail/duration metadata when broadcast is delivered | User-visible privacy / high | Protected playback screen contract; owner must decide recipients and compatibility before scoping |
| SEC157-05 | FileCore NetworkCredentialsDatabase uses schema-3 static-key Blowfish, username/path stored as metadata | App-private sandbox is the main at-rest boundary; extracting database plus app code can reveal passwords | User-visible security / high | No incompatible credential migration here; design versioned AndroidKeyStore encryption with atomic migration, recovery and downgrade policy |
| SEC157-06 | MediaLib Trakt.setAccessToken/setRefreshToken store strings in default SharedPreferences | Trakt tokens rely on app sandbox, unlike Put.io's device-key envelope | User-visible security / high | Migration needs provider/API-wide ownership and device-key/upgrade/logout tests; no silent destructive conversion |
| SEC157-07 | Manifest has no explicit allowBackup=false; network config permits cleartext base traffic | OS backup policy and intentional FTP/DAV/HTTP support are separate from manual backup sanitisation | User-visible privacy / high | Review platform backup/data-extraction rules and transport choices with source owner; no protected resource/contract edits |
| SEC157-08 | Exported VideoSearchProvider accepts bound search values and returns library titles/artwork references without a component permission | Read-only system-search contract also exposes matching library metadata to another app | User-visible privacy / high | Query is parameter-bound; do not call this SQL injection. Restricting callers changes an external screen/search contract |

| SEC157-09 / DG157-04 | ExternalPlayerResultListener URI and arbitrary result Bundle logs | Leaks occur before export redaction | User-visible privacy / high | Fixed `14675bc02`; DEBUG appender regression checks raw arguments and formatted text; prior source fails |
| SEC157-10 / DG157-03 | SAF font document display name becomes a private File child | A provider can overwrite a sibling app-private file using ../name.ttf | User-visible security / high | Fixed `ed31a873e`; reject non-leaf names and canonical-parent escapes; real file regression and negative control |
| SEC157-11 / DG157-05 | SubtitlesDownloaderActivity2.stageSubtitle account headers sent to returned URL | Credential origin/TLS not validated at this screen call site | User-visible security / high | Screen handoff: exact credential origin, credential-free CDN retrieval, safe redirect handling; no screen edit |
| SEC157-12 / DG157-06 | OAuthDialog host suffix, mixed content and raw URL logging | Non-exact web-origin admission and sensitive code/state log output | User-visible privacy / high for predicates/logs | Screen handoff; no asserted UUID-state bypass or permissive SSL callback |
| SEC157-13 | OpenSubtitlesCredentialsDialog stores username/password in its private preferences | Sandbox-only account storage; a manual settings filter is not device encryption | User-visible security / high | Screen/credential migration requires compatibility, rollback and device-key tests; no plaintext-to-KeyStore conversion claimed |

The source manifest was reparsed after merge 9dd9d0c65: 15 explicit/implicit exported entries remain; both listing
services remain non-exported. The protocol and native observation APIs do not expose raw path/credential fields.
OpenSubtitles helper still logs file names/search objects/server messages at DEBUG/ERROR; UnavailablePosterBroadcastReceiver
logs path/title/failure data. These remain a wider privacy-fix queue; the app is not declared globally log-clean.

## Exported entry points and intent handling

EntryActivity and QueryBrowserActivityVideo explicitly target TV hosts while preserving incoming action/data/type,
extras, ClipData and URI grants. This is deliberate compatibility, not permission escalation proof. The full player
read covers its external intent path; numeric resume parsing rejects invalid types, but screen logging still needs
the fix above. VideoPicker/FolderPicker/details/collection/TV/search hosts remain exported without a component
permission as inventoried; they are not certified safe merely because compilation passes. Media mount broadcasts
are protected Android actions. Explicit import/reset receivers and BIND_JOB_SERVICE must retain their existing
sender/platform gates; do not widen these to make a new scan API work.

Trakt's exported custom-scheme redirect requires ACTION_VIEW, exact nova.trakt/auth scheme/host, code and a matching
persisted UUID state, then removes the pending state before exchange. Missing/mismatched state is rejected without
logging code/token. Manifest's historical localhost filter does not bypass that handler's validation. The device
flow remains its existing bounded poll. Account UI changes are outside this task.

The new listing services are non-exported. Binder validates Binder.getCallingUid against the app UID before reading
arguments; Stop requires the current opaque token. Temporary credentials are marked temporary, never persisted or
logged by the bridge, and the caller supplies no PID. A private process owns DNS, connection caches and UPnP and can
terminate itself; cancellation cannot close playback's transport. Bounds reject malformed/partial wire payloads.

## Credentials, TLS and backup

PutioTokenStore uses AndroidKeyStore AES/GCM, a versioned bounded noBackup envelope and authenticated context, with
cleared byte buffers. Public code-link snapshots contain no token and terminal/expired callbacks cannot save one.
Host crypto/state tests do not prove hardware keystore migration. Network credentials remain the older scheme in
SEC157-05; do not misdescribe them as equivalently device encrypted.

FTPS uses explicit AUTH TLS on 21, the default trust manager and hostname verification on control and data
connections. JSch enables StrictHostKeyChecking=yes; SSHJ uses the existing host-key verifier rather than an
unconditional accept-all verifier. Network security config trusts system plus bundled ISRG roots and permits
cleartext for supported insecure transports; it is not a trust-all certificate policy. Third-party TrustAll helper
classes in a lint report are not proof that these call sites use them. No TLS verification was disabled for tests.

Default portable settings use positive rules and reject token/credential keys. Connection/removal/reachability and
health evidence are not arbitrary preference dumps; personal roles export opaque credential-free fingerprints
under HIDDEN. Logical backup/restore tests cover category isolation, rejected/private fields and retained current
auth. Raw historical database archives have a different explicitly selected scope; never advertise a raw archive
as a sanitised diagnostic export. The new current DB locator preserves existing logical ZIP naming.

Diagnostics clean URLs/paths, secret assignments, headers and complete exception chains; tests exercise hostile
values. New transport traces accept no address/exception text and capture the session before opening. Native
observations are typed numeric/capability data. These facts do not make every original logger in every screen safe;
the open sites above are explicit findings, not silently accepted or labelled fixed.
