# Third-party dependency lifecycle audit and BitTorrent retirement decision
Date: 2026-10-12. Documentation-only owner direction. No source, native, build or release changes.

## Owner-approved policy
1. Expand Jetty assessment into a comprehensive inventory of all independently maintained third-party projects actually used by Supernova, including Java/Android dependencies, native source and prebuilt binaries, build tools and embedded/indirect transitive dependencies.
2. For each: exact resolved signed-APK version and provenance (distinguish build declaration/source lock from packaged binary), latest maintained upstream version, change/security/support history, whether NOVA adopted newer code and integration patches, actual reachability and need, Android minSdk23/Shield compatibility, and proposed action: retain / NOVA-first adapt / exceptional direct upgrade / replace / retire.
3. Preserve NOVA-first default; independent upgrades are documented exceptions, not blanket pursuit of newest version. Security and maintenance review should not assert exploitability from advisory version matches alone.
4. Track findings as living dependency register with evidence and owner decisions. Existing Codex baseline report 11 identifies resolved 138-artifact SBOM, native pins and Jetty findings; not a complete latest-version audit.

## Owner-approved BitTorrent product retirement
- Remove BitTorrent/torrent functionality and user-facing references from Supernova in a future separately authorised implementation, provided unrelated media playback, file browsing, local/network scanning, streaming and integrations remain intact.
- Candidates include libtorrent, torrentd, native per-ABI binaries, builders/prebuilts, Java/JNI bindings, native symbols, services/manifest receivers/providers/permissions, torrent URI/magnet and intent handlers, screens/preferences/strings/resources, third-party dependencies and license notices where no longer shipped, data stores/migrations/backups, build/CI packaging and feature flags.
- This is **removal approval in principle**, not proof that any binary or code is unused and not authority to delete now.
- Map direct and indirect consumers, reflection/JNI, file-protocol routing, historic preferences and data restoration, external intents, app launch, streaming and fallback behaviour; distinguish torrent-only code from shared networking utilities.
- Establish installed-APK baseline (entries/ABI bytes/DEX/manifest/resource/permissions) and release-build reachability/minification results. Use isolated reversible changes with build tests, Android23/Shield tests, local/SMB/WebDAV/UPnP/HTTP playback, network discovery, backup/restore and no loss of user media or identity. Measure actual size delta, do not promise gains in advance.
- Do not delete user data or database tables without explicit migration and preservation strategy; avoid unsafe APK downgrade due to known database v62 conflict.
- Check whether any of the Jetty/UPnP stack is actually independent of BitTorrent before considering removal; existing source confirms Jetty is used by jUPnP.
- Do not touch 0.138 installation, pending 0.139 work or protected branches as part of investigation.

## First evidence from Codex 0.138 audit
- report 11: existing legal/native inventory records libtorrent 2.0.14 and separate libtorrent builder, torrentd builder/prebuilt manifest components. Whether actual signed APK includes each library and whether code paths remain reachable must be proven.
- report 12: no component certified unused; removal requires call/resource/JNI/manifest/serialization/backup and release R8 proof.
- report 11: Jetty is an UPnP dependency, not evidence of BitTorrent linkage.

## Proposed delivery
A) project/version/security matrix prioritised by security, unsupported versions and active use;
B) BitTorrent removal impact map, safe deletion sequence and regression tests;
C) owner-reviewed candidates for future maintenance, with no automatic code edits.
