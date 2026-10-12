# SUPERNOVA — CURRENT ROADMAP AND DECISION HUB
Updated: 2026-10-12. Owner-approved planning and QA source of truth; not code authorisation.

> Version naming: this repository's established release notation is **0.138** (owner sometimes says 1.3.8) and **0.139** (owner sometimes says 1.3.9). Confirm actual versionCode/versionName in signed artifact before publishing; do not silently rename.

## The agreed sequence — do not reorder or combine stages
### Stage A — complete current installed 0.138 walkthrough (IN PROGRESS)
- Continue hands-on Shield QA with user, covering Home, Movies/TV/custom Libraries, Network & Files, Search, Settings, playback and relevant navigation.
- Record each owner-observed defect and approved design decision; distinguish source proof from device observation.
- Finish shared Library statistics/count/storage design; one shared calculation/presentation contract for Movies, TV and user-created Libraries.
- Obtain **one diagnostic ZIP near the end** of walkthrough, not repeated requests during testing. Correlate known playback startup failures and other incidents; protect sensitive information.
- Preserve 0.138 install, library data and historical baseline.

### Stage B — approve and implement 0.139 (NOT STARTED)
- Compile one explicit owner-reviewed 0.139 handover: selected 0.138 QA fixes, diagnostics expansion, Library/navigation/hero/UI fixes, and **BitTorrent functionality retirement requested by owner**.
- Diagnostics: build on existing recorder, add precise playback/seek/startup/native/transport evidence, correct privacy/sanitisation and bounded retention as approved; do not rewrite functioning diagnostics.
- BitTorrent: remove torrent/magnet/libtorrent/torrentd entrypoints and exclusive native/build/UI components only after full impact graph, shared-dependency checks, APK inventory and safe data/backup compatibility. If removal cannot be proven safe, report exception to owner rather than break unrelated functions.
- Do not silently bundle broad Jetty/OpenSSL/FFmpeg/Android target migrations into 0.139 unless a separately approved urgent security exception is warranted.
- Codex starts implementation only on explicit owner go-ahead; existing signed app identity/signing/upgrade data must be preserved.

### Stage C — deliver signed 0.139 APK and owner smoke test
- Build/install **over existing 0.138** without wiping data, preserve source SHA and signed receipt.
- User tests that it installs/launches, basic playback/navigation work, libraries and media data import/scan/load, network sources connect, settings and existing user data remain accessible. This is a **basic acceptance smoke test**, not full maintenance certification.
- Address any release-blocking regression before requesting maintenance approval.

### Stage D — owner authorises comprehensive maintenance (NOT AUTHORISED YET)
- After owner explicitly approves following 0.139 smoke test, establish a new immutable stable source baseline; preserve 0.138 historical investigation as dated evidence and incrementally update master docs.
- Selectively adapt NOVA upstream nonvisual fixes using three-way comparisons and cross-module patches. **NOVA-first** is the default for shared third-party projects; independent updates are documented exceptions for security/support or incompatibility, with separate validation.
- Third-party lifecycle/security audit: actual packaged binary/source, current upstream version, NOVA integration, advisories and reachability, API23/Shield compatibility; priority Jetty, OpenSSL, FFmpeg, network forks and native decoders. Preserve UI.
- Database v62 conflict, safe migrations, backup/restore and no destructive downgrade; diagnostics privacy and security; network exposure, TLS verification and UPnP.
- Architecture and shared UI foundations, semantic design roles, UI Atlas/focus; measured performance and memory, test matrices, reliable build/signing.
- **Codebase cleanup and optimisation explicitly IN SCOPE**: remove genuinely redundant/dead code, legacy resources and graphics, unused libraries and manifest/build references **only after proving non-use** via source/resource/JNI/manifest/reflection/backup graph, release R8, APK comparison and runtime tests. Avoid deleting compatibility readers or data.
- **Canonical Supernova background image**: see policy below.
- Work in small reversible independently validated changes; do not indiscriminately merge upstream UI or promise performance gains without measurement.

## Canonical background asset — owner-approved maintenance requirement
- The currently approved **Supernova default background** already displayed on **Network & Files, Search and Settings** is the canonical source of truth.
- Investigate exact resource file, drawable qualifiers, layout/theme/programmatic references, asset variants, fallbacks, error/default states, overlay/gradient treatments and older legacy background resources.
- Centralise generic app-background selection in one named resource/semantic role/helper. All screens requiring the *default app background*, and generic background-failure fallback paths, must reference that same approved asset. No competing legacy default backgrounds.
- **Do not overwrite intentional content imagery**: movie/TV artwork, hero backdrops, poster thumbnails, user-provided backgrounds, artwork-specific placeholders, or intentionally distinct overlays without owner review. Decide case-by-case whether a missing artwork placeholder is truly a generic background or a distinct media placeholder.
- Preserve approved crop, scaling, opacity, brightness, colour, aspect ratio and text legibility across Shield resolutions; do not introduce unwanted dimming or blur.
- Remove obsolete alternative generic background assets only after exhaustive resource usage checks including XML styles/themes, Java/Kotlin, drawables, density/night variants, fallbacks, reflection and packaging.
- Verify Network & Files, Search, Settings and every other generic background consumer visually on device, including error/empty/loading states, and compare to approved screenshots. A shared resource does not guarantee identical crop if view rendering differs.
- No asset/code change is performed by this decision record. The actual resource identifier/file and current full consumer map remain **UNVERIFIED** and must be located before implementation.

## Source of truth and evidence hierarchy
1. Owner decisions and Shield QA observations documented here and in dated project-decisions notes (owner acceptance not equivalent to source or device proof).
2. Signed 0.138 source SHA `b3504c2c4b1725f3070416d14b63e53ae1db3d74`, build QA receipts, and code.
3. Codex comprehensive **read-only** 0.138 maintenance investigation (reports 00–15, evidence), isolated branch `docs/maintenance-investigation-2026-10`, draft PR #15; no code changes.
4. Approved mockups/Google Docs historical material as relevant, with exact visual conformance confirmed before handover.
5. New release source, binary and device evidence supersede only the applicable current-state assertions, not historical observations.

## Central references
- [0.138 walkthrough and Library UI contracts](2026-10-12-supernova-0138-shield-qa-and-library-foundation.md)
- [Release sequencing and baseline documentation policy](2026-10-12-owner-decisions-and-baseline-policy.md)
- [NOVA-first dependency policy and Android 17](2026-10-12-nova-first-dependency-policy-and-android-17.md)
- [Jetty security follow-up](2026-10-12-jetty-security-follow-up.md)
- [Third-party lifecycle and BitTorrent retirement decision](2026-10-12-dependency-lifecycle-and-bittorrent-retirement.md)
- [Dependency triage and BitTorrent impact map](2026-10-12-dependency-triage-and-bittorrent-impact-map.md)
- [Codex maintenance investigation README](https://github.com/heretoecode/Supernova/blob/docs/maintenance-investigation-2026-10/docs/maintenance-investigation/README.md)
- [Draft PR #14 — owner decisions](https://github.com/heretoecode/Supernova/pull/14)
- [Draft PR #15 — read-only maintenance audit](https://github.com/heretoecode/Supernova/pull/15)

## Release gates and boundaries
- **Now:** discuss, investigate, update documentation, continue 0.138 QA.
- **Not yet:** start 0.139 coding, change release signing or identity, merge PRs, launch broad maintenance, clean resources, upgrade Jetty/FFmpeg/Android targets.
- **0.139 implementation:** only after owner approves consolidated handover.
- **Maintenance:** only after delivered 0.139 passes owner's smoke test and owner explicitly authorises.
- Never mark a Codex-reported fix as owner-tested without owner evidence; don't assume removed UI implies removed code; don't silently alter approved visuals.
