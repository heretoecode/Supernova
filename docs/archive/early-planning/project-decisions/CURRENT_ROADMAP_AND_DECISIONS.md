> **CURRENT IMPLEMENTATION AUTHORITY — 2026-10-13:** Owner has now authorised Codex to begin work on the complete approved **Supernova 0.139** scope (original 41 workstreams plus hero rotation and comprehensive Backup & Restore). Read [authoritative 0.139 Codex implementation handover](2026-10-13-supernova-0139-codex-implementation-handover.md) FIRST. Older statements below about full handover still pending, backup/hero parked, or coding not authorised are superseded. This is NOT approval to merge, publish or change signing/identity.\n\n# SUPERNOVA — CURRENT ROADMAP AND DECISION HUB
Updated: 2026-10-13. Owner-approved planning and QA source of truth; **Customise Home first implementation approved in principle**, but complete 0.139 release scope and other candidates still require explicit handover approval.

> Version naming: this repository's established release notation is **0.138** (owner sometimes says 1.3.8) and **0.139** (owner sometimes says 1.3.9). Confirm actual versionCode/versionName in signed artifact before publishing; do not silently rename.

## The agreed sequence — do not reorder or combine stages
### Stage A — complete current installed 0.138 walkthrough (IN PROGRESS)
- Continue hands-on Shield QA with user, covering Home, Movies/TV/custom Libraries, Network & Files, Search, Settings, playback and relevant navigation.
- Record each owner-observed defect and approved design decision; distinguish source proof from device observation.
- Finish shared Library statistics/count/storage design; one shared calculation/presentation contract for Movies, TV and user-created Libraries.
- Obtain **one diagnostic ZIP near the end** of walkthrough, not repeated requests during testing. Correlate known playback startup failures and other incidents; protect sensitive information.
- Preserve 0.138 install, library data and historical baseline.

### Stage B — consolidate 0.139 and implement approved scope (Customise Home phase approved; full handover pending)
- Compile one explicit owner-reviewed 0.139 handover: selected 0.138 QA fixes, diagnostics expansion, Library/navigation/hero/UI fixes, and **BitTorrent functionality retirement requested by owner**.
- **Customise Home first implementation is owner-approved in principle**, alongside Home heading monochrome icons, default pure-white heading text and 20% reduced top-navigation blur vertical reach; consult [consolidated 0.139 scope](2026-10-13-supernova-0139-consolidated-handover-scope.md) for exact contract.
- **PARKED:** Shield Home / Android TV launcher channel integration, including proposed **Show on Shield Home** toggle; exclude from 0.139 until owner revisits it. Preserve existing functionality.
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

## App-wide shared UI architecture — owner-approved maintenance principle
**The background and Search keyboard are examples, not the limits of this requirement.** Supernova should have a single authoritative implementation/semantic contract for each intentionally shared UI component, style, asset, and behaviour. Updating a shared component once must update all intended consumers, rather than requiring page-by-page copies and fixes.

- **Official keyboard:** the keyboard design refined on the Search page is the canonical Supernova in-app keyboard. Inventory every text-entry path (Search, Network & Files, Settings, dialogs, credentials, filters, library creation and hidden/error flows) and route all applicable **app-controlled** on-screen keyboards to the same reusable component. Preserve purpose-specific modes (password masking, numeric/symbol input, IME actions, focus/remote accessibility, credential privacy); do not intercept external/system IMEs, accessibility keyboards or third-party activities blindly.
- **Official generic background:** the existing approved asset and rendering rules must be the single generic-background reference and fallback for app-controlled generic backgrounds; intentional content imagery is separate (see detailed policy below).
- **Other common UI:** inventory and consolidate navigation/top bar, focus visuals and D-pad routing, dialog/context menus, controls/buttons, shared Library header/stats, media rows/cards, loading/empty/error states, typography/spacing/accent tokens and common animation rules where the owner-approved design is shared. Specialised screens can use explicit documented variants rather than silent duplicates.
- **Audit before refactor:** Codex's existing static UI atlas/shared-component findings are a starting point, **not proof every consumer already uses them**. Map each UI request/entrypoint to implementation, identify parallel legacy components, XML themes/styles, Java views, manifest activities, keyboard launch paths, accessibility/system integration and actual Shield runtime usage.
- **Centralisation is not indiscriminate replacement:** preserve established user-visible design and semantics; distinguish canonical app-owned components from Android system surfaces and intentionally unique content. Avoid blanket resource renames or rewriting working screens just for abstraction.
- **Acceptance tests:** change one shared style/behaviour in a test branch and demonstrate it propagates to all mapped intended consumers; test each screen and dialog with D-pad, Back, focus restoration, keyboard privacy, different field types, empty/error states and screenshot comparisons. Delete old implementations/assets only after no direct/indirect/reflective/resource/backup consumers remain. Compare APK/R8 results and rollback cleanly.
- This is **Stage D comprehensive maintenance after 0.139 smoke test and explicit owner authorisation**, not a new 0.139 requirement or permission to begin code edits.

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
- **[Consolidated 0.139 owner scope, approved work and parked items](2026-10-13-supernova-0139-consolidated-handover-scope.md)**
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
- **Now:** Customise Home first implementation authorised in principle; complete consolidated 0.139 handover and 0.138 QA, source audit, and documentation. No actual app source change has been made by these docs commits.
- **Not yet:** start unapproved 0.139 work beyond Customise Home/approved Home-heading and blur scope, change release signing or identity, merge PRs, launch broad maintenance, clean resources, upgrade Jetty/FFmpeg/Android targets.
- **0.139 implementation:** only after owner approves consolidated handover.
- **Maintenance:** only after delivered 0.139 passes owner's smoke test and owner explicitly authorises.
- Never mark a Codex-reported fix as owner-tested without owner evidence; don't assume removed UI implies removed code; don't silently alter approved visuals.


## 2026-10-13 owner selection and backup investigation — latest override
- Owner has now **approved the 41-item INCLUDE and 7-item EXCLUDE/PARK 0.139 feature/fix scope** documented in the [consolidated handover](2026-10-13-supernova-0139-consolidated-handover-scope.md). Earlier headings that describe all non-Customise Home fixes as merely candidates are historical; the **coding start, complete implementation handover and build remain separately gated**. No app code or merge is authorised by this documentation.
- The owner additionally requested a **read-only comprehensive backup/restore Codex audit**, anchored in the existing maintenance report. See [canonical backup/restore audit brief](2026-10-13-backup-restore-audit-brief.md). The broader backup UI/format redesign remains **parked for 0.139 implementation** pending findings and explicit owner decision. New export reliability incident and no-artwork/granular/privacy decisions are documented, not implemented.
- Hero 5 Movies/5 TV rotation policy is a newly refined **design proposal**, but 10-title policy implementation remains parked pending explicit re-inclusion.


## LATEST OWNER OVERRIDE — 2026-10-13
**0.139 approved implementation scope is now the original 41 owner-selected items PLUS (1) 10-title hero rotation policy and (2) complete backup/restore improvements.** This supersedes earlier roadmap language parking either hero rotation or broader backup/restore implementation. Other parked items remain parked. Owner explicitly approved both workstreams for 0.139; this does **not** mean coding has started or that the currently running read-only Codex backup audit should be interrupted or converted into an implementation task.

Hero: 5 Movies + 5 TV from indexed library, freely mixed order; Recently Added, in-library Trakt Trending/Popular and Because You Watched blend; exclude watched/Continue Watching; successful playback moves eligibility to Continue Watching; manual D-pad preserved; idle-triggered auto-advance pauses on input. Confirm exact timings and fallback with owner before final implementation lock.

Backup/restore: full redesign/reliability/correctness/UX/format compatibility subject to findings of [canonical backup audit brief](2026-10-13-backup-restore-audit-brief.md) and existing maintenance investigation. No artwork in new backups/no toggle; granular safe independent categories; optional media matches including paths with privacy warning; full settings/defaults inventory; SAF temporary-file and crash analysis; truthful progress; preserve old restore compatibility and protect secrets. Read-only audit proceeds first; subsequent coding handover, tests and Shield validation remain required. **Do not merge, build or edit app source merely because this documentation changed.**


## 2026-10-13 — Existing branding is settled
Owner confirms an earlier branding release **already implemented** the new Android TV launcher app banner, Supernova branding, backdrops, app icon, progress/loading icons, animated icon and new signing key. This is historical owner-reported completion, not a new 0.139 implementation item. Preserve current visuals and signing identity. The parked Shield Home **channel integration** is separate from the already implemented **launcher banner**. Do not reopen banner design or rotate signing keys as part of 0.139.
