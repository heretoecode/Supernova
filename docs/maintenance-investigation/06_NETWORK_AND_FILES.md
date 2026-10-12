# 06 — Network & Files, discovery and transport ownership

## End-to-end current flow

**VERIFIED:** PreviewNetworkWorkspace is a compatibility host containing UniversalFileBrowser, not a parallel active three-panel browser. UniversalFileBrowser builds locations, toolbar/list/grid, selected-file information and actions; BrowserSelection stages root/exclusion edits until Save Changes & Scan. Directory picker/onboarding modes reuse the browser but change allowed actions. Classic protocol listing Activities remain manifest-declared and use shared factories.

FileCore provides MetaFile2, ListingEngine/RawLister and FileEditor factories for local/content, SMB jcifs and SMBJ, SFTP JSch and SSHJ, FTP(S), WebDAV and ZIP. UPnP is supplied by MediaLib filecoreextension/upnp2. Network source form selects SMBJ/SSHJ based on preferences, validates host/port, stores credentials separately, clears the password field after connection, then browses. Save Password can retain credentials; a temporary credential remains process-local. Backup intentionally excludes credential DB. Source: [PreviewNetworkWorkspace.java:17](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewNetworkWorkspace.java#L17), [UniversalFileBrowser.java:235](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/UniversalFileBrowser.java#L235), [FileCoreLibrary/MetaFile2Factory.java:36](https://github.com/nova-video-player/aos-FileCoreLibrary/blob/c4b760c55102c72d45f68b0d16999fc1f4f013a7/src/com/archos/filecorelibrary/MetaFile2Factory.java#L36), [FileCoreLibrary/NetworkCredentialsDatabase.java:65](https://github.com/nova-video-player/aos-FileCoreLibrary/blob/c4b760c55102c72d45f68b0d16999fc1f4f013a7/src/com/archos/filecorelibrary/samba/NetworkCredentialsDatabase.java#L65).

## Protocol and trust boundaries

| Path | Source and authentication boundary | Maintenance concern / verification |
|---|---|---|
| Local/content/USB | Permission/content resolver/URI grant, FileCore editor and scanner | Unmounted storage and revoked grants must preserve indexed history; descriptor offset/length ownership |
| SMB / SMBJ | Shared credentials DB; jcifs or SMBJ contexts/transport; relay for player/retriever | Cold-start crypto/readiness, negotiated dialect, multicredit limits, reconnect and concurrent metadata/playback |
| SFTP / SSHJ | JSch/SSHJ session pools; Supernova SftpHostTrust overlay in both implementations | Trust-on-first-use stores first key; changed key rejected. First connection authenticity still requires independent assurance |
| FTP / FTPS | Credentials and connection pool, explicit TLS mode where selected | Plain FTP is not confidential; FTPS host/certificate behavior and retry/resource cleanup need device fixtures |
| WebDAV HTTP/HTTPS | HTTP clients, credential origin boundaries, redirects/PROPFIND | Preserve existing cross-origin authorization stripping; upstream OPTIONS capability validation should adapt without regressing it |
| UPnP | Discovery/service lifecycle, jUPnP and Jetty client/server | Network-visible HTTP parsers and descriptor fetching; advisory relevance assessed in report 11 |
| Streaming/provider files | put.io controller/API/token store vs arbitrary file transport | Provider IDs/URLs are not permanent local paths; token envelope, cancellation, playback authorization and backup reconnection |

Network security configuration explicitly permits cleartext traffic, with bundled ISRG roots and system anchors. This enables configured HTTP/local protocols and is not proof that authenticated API calls use HTTP. Blanket prohibition would break intended local sources. Future policy needs endpoint/protocol purpose rather than speculative global tightening. [network_security_config.xml:3](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/res/xml/network_security_config.xml#L3).

**VERIFIED high-priority surface:** pinned StreamOverHttp creates `new ServerSocket(0)` without an explicit loopback address, while returned URLs use localhost. Effective overlays do not change those constructor calls. Thus “localhost URL” does not prove loopback-only listening. **INFERRED:** an unintended LAN listener can expose relayed content or resource exhaustion if reachable; actual interface, firewall and admission/auth behavior require on-device checking. PROPOSED: bind the relay to loopback if all legitimate consumers are local, after external-player compatibility checks. No network probe was performed. [FileCoreLibrary/StreamOverHttp.java:87](https://github.com/nova-video-player/aos-FileCoreLibrary/blob/c4b760c55102c72d45f68b0d16999fc1f4f013a7/src/com/archos/filecorelibrary/StreamOverHttp.java#L87).

## Browser navigation and state

**VERIFIED static:** dirty selection is reviewed on exit; selection save failure restores old root/exclusion preferences; library changes do not delete physical media. Listing generation and current-URI guards reject stale callbacks. Empty/removal transitions choose a safe focus target; left rail movement is handled separately from explicit Right/Left panel transitions. onDetached stops discovery/listing/listeners and scan refresh; close additionally shuts metadata executor. D-pad behavior and clipping must be runtime checked.

Routes: Overview → Scan Library / Network Scanning / Library Health; Network Shares → saved share or Add Network Source → protocol/host/path/port/credentials → Connect → list/grid → folder or media → File Information/Details/play; Local Storage → drive → folders; Saved Locations → open/remove; Discover → discovered server → browse; staged Add/Remove/Exclude/Restore Inclusion → Review → Save Changes & Scan. Keyboard cancel/dirty exit must restore opener. [UniversalFileBrowser.java:255](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/UniversalFileBrowser.java#L255), [UniversalFileBrowser.java:147](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/UniversalFileBrowser.java#L147), [UniversalFileBrowser.java:479](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/UniversalFileBrowser.java#L479).

## Defects, duplicate candidates and upstream reconciliation

- **VERIFIED:** protocol reporting sourceType records SMBJ/SSHJ/SFTP as other. Browser protocols exist; add enum coverage only in an approved diagnostics change.
- **VERIFIED:** hidden-directory keep behavior is already corrected by `filecore-next-qa.patch`; do not report it as unimplemented. Local import traversal is likewise changed by the final MediaLib QA patch.
- **INFERRED:** classic per-protocol Activities duplicate some presentation, but external intents and settings paths may reach them. No removal decision is justified by the universal browser alone.
- **VERIFIED static upstream opportunities:** shared SMB readiness, credential @ repair, URI identity reconciliation, OwnedStreams cleanup, purpose-aware backend reads and WebDAV OPTIONS validation. Source ledger U23–U33 supplies exact commits/dependencies/tests.
- **UNKNOWN:** Shield NAS throughput, SMB dialect used, FTPS compatibility, fd leaks, reconnect latency and whether stale entries explain any owner incident. No private logs were read.

## PROPOSED checks and changes

Prioritize listener/privacy boundary and SMB readiness/auth repair, then stream lifecycle/read policies. Reconcile source identity changes with scanner/NFO/history; test raw/encoded/#/?/%/Unicode names and old roots together. Keep SFTP TOFU overlay in any replacement session helper. Benchmark sequential/range/metadata reads separately; throughput and time-to-first-frame can move in opposite directions. Never import upstream frontend navigation or settings visuals to gain transport changes.

Required scenarios: cold/warm NAS, sleep/wake, credentials with @/domain/backslash, wrong credential recovery, two simultaneous reads, throttling/disconnect mid-read and seek, unknown-length stream, metadata cancellation, revoked local grants, 50+ folders, 8+ drives, dirty save/cancel and remove-selected entry. Release-build/minified paths and actual Shield/receiver/network configs are required before implementation acceptance.
