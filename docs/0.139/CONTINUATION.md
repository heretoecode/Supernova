# Supernova 0.139 continuation checkpoint

Implementation is ongoing. No 0.139 APK has been built, signed or delivered.

Branch: `codex/supernova-0.139-implementation-2026-10-13`.
Recovered committed source on 14 October: `e89c211b1d467dd3b4879a56f06c3d4cafb1f76e`, plus preserved MediaStateRecovery and CombinedPlaylistMediaTest changes. Those changes are now committed at `074db4bcf1112a3ec89922d70c6e8519a6393a1a`.
Current application source: `d18a80f83fc417f68d3a8ee9afabbb6aa8001d94`. Draft review PR: https://github.com/heretoecode/Supernova/pull/17, based on the existing0.138 development branch, with no merge authorization.
Read INTAKE.md, WORKSTREAMS.md, UI_CONFORMANCE.md, REPRODUCE.md and the source-pinned JSON receipts. The 43-row ledger still describes whole-workstream acceptance; staged test passes do not complete the release.

## Preserved and verified work

Earlier receipts include full regression PASS732/171 at1f8897e7 (before subsequent changes), artwork34/8 and7/2, source exporter30/4, browser favourites and five-store recovery90/8, operations20/3, connected Settings24/5, and logical-only production export24/6. Main Settings retains the permanent left rail, all26 categories without scrolling and flat Details/Instructions. Whole-library1.0 export is test-only; the dedicated legacy reader remains, with its established authentication replacement semantics. New logical restoration preserves receiving authentication. Full VideoPreferencesCommon/provider lifecycle, non-preview restore entry and hardware acceptance remain open.

Playlist receipts:

* `e89c211b`: PASS16/3 — PLAYLISTS_STATE_VERIFICATION.json, native private replacement/source/policy.
* `074db4bc`: PASS68/7 — PLAYLISTS_MEDIA_VERIFICATION.json, lists/pins/hidden in one protected SQLite participant plus earlier journal regressions.
* `29cfda5a`: PASS76/8 — PLAYLISTS_JOURNAL_VERIFICATION.json, combined default/health/viewing/media/bookmarks recovery, stable references, seven pre-decision rollback boundaries, post-decision replay, manual-change preservation, corruption, stale previews and interruption.
* `ddcbb6a6`: FAIL compilation18s — PLAYLISTS_INTEGRATION_FIRST_VERIFICATION.json. The loader hook called nonexistent Diagnostics.exception; corrected to the existing Diagnostics.error at8664c4dd. No tests executed in that failed attempt.

Current code registers24 categories including Saved playlists. Matches and Title playback remain unregistered. New playlists have local-only status4; receiving Trakt definitions/members and unowned rows stay exact. Both Trakt definition queries exclude4 while preserving NULL; native list-member query uses list_id rather than member _id. Public playlists carry names and TMDB membership only. Private restore aliases keep receiving identities stable; a source-device restore allocates a distinct identity for a local copy that would collide with a preserved account-owned definition. Pending definitions reconcile during the normal Library worker using the combined journal, preserving existing local/account definitions and never creating media.db solely for restoration/export.

## Exact verification environment and next action

Isolated detached app build: `/workspace/supernova139-lists-build/Video`, currently at source d18a80f8, build-only AndroidManifest.xml/searchable_video.xml modifications preserved.
Isolated MediaLib: `/workspace/supernova139-lists-source/MediaLib`, clean8424b8af3f54f498d8e1c1cac322487e15b9ab54. Its parentebac4f4 snapshots the existing20-file0.138 preparation. Original shared dirty MediaLib at5758074 is untouched. Apply `.github/build/medialib-supernova139-playlists.patch` AFTER the established0.138 patch stack for reproduction. FileCoreLibrary remains the existing prepared sibling.
JDK `/workspace/toolchains/jdk-21.0.12.1+1`; SDK `/workspace/android-sdk`. Gradle command/flags in REPRODUCE.md; native build tasks excluded from these staged checks.

At this checkpoint the title None/schema2 test is running in exec session75462, log `/workspace/scratch/supernova139-title-none-first.log`, isolated app source d18a80f8. It selects TitlePlaybackCompatibilityTest and PortableTitlePlaybackTest. FIRST save exact XML counts/classes and log hash before advancing the detached build; investigate failures and preserve their receipts. The previous track-parser run at98cf33bc passed9/2 (TITLE_TRACKS_VERIFICATION.json).

Completed subsequent playlist integration: PASS56/8 at3eaced03, PLAYLISTS_INTEGRATION_VERIFICATION.json. Earlier compilation failure and55/8/1-failure run remain preserved. Playlist preview no longer allocates origin preferences; source-device restore and receiving alias collisions have distinct stable identities. Source/library/controller integration now passes its stated staged checks.

Media matches: strict public source/schema/pure preparation PASS7/1 at e8faad18; receiving metadata ownership plus native dependency investigation PASS10/2 at df238703. MATCHES_SOURCE_VERIFICATION.json and MATCHES_DEPENDENCY_VERIFICATION.json preserve exact scope. Native Library joins metadata by video_id; merely rewriting files scraper pointers deletes the old metadata/pin/facts and cannot borrow another file's metadata. Changing only provider ID leaves old scraped facts visible. Implement a canonical metadata/join/cache-aware participant with coordinated originals/redo; do not register this category or drop cleanup triggers as a shortcut. It remains unregistered.

Indexed folders: private native participant and public source codec PASS10/1 at55103ee0 (INDEXED_SOURCES_VERIFICATION.json). Sources schema2/archive plus six-store private journal schema7 PASS122/12 at40e0be99 (INDEXED_SOURCES_JOURNAL_VERIFICATION.json); schemas1–6 remain readable, source-schema1 leaves indexed folders untouched. Includes eight rollback and four acknowledgement boundaries, corruption/staleness, exact native source caches/originals and the actual ShortcutDbAdapter rescan reader. Pending-source reconciliation PASS68/8 at98116b91 (INDEXED_SOURCES_RECONCILIATION_VERIFICATION.json): only receiving configured locations resolve references; no archive-location opt-in or new unknown native store; later native name/rescan choice wins; malformed pending input fails before writes. Full scanner scheduling/provider/device behavior remains open.

Title playback remains unregistered. State/source primitives, actual VideoMetadata parser compatibility and source-schema2 subtitle None distinction now exist. None must be a separate intent, remapped to the receiving subtitle count; a legacy index without language cannot prove it means a subtitle or None. d18a80f8 is not yet verified for those changes. Next implement durable explicit choices without invented lastTimePlayed/watch timestamps, combined-journal/persistence/export integration and real PlayerService callback validation/manual supersession. Avoid implementing an index-only restore that turns None into a subtitle on a different file.

After this narrow run, finish the two unregistered domains (Matches and Title playback), source/controller/full VideoPreferencesCommon/legacy/non-preview/SAF/provider/actual-force-stop matrices, and remaining43-row acceptance work. Full current-source regression, native codecs/R8/4ABI/upgrade, legal/native retirement and final release gates are still open. Do not rerun completed stages without a relevant source change or concern.

## Scope and external limitations

Current handover and roadmap were reread from decisions0163a347853b2b84ff15946c922a3305883733af; prior authoritative source/audit/visual decisions remain in INTAKE.md. Owner explicitly excludes the newly documented1.4.0 Details redesign. Do not implement it or future/parked work. Existing independently approved0.139 Details changes are preserved; no new content-density policy has been invented. The original finite/passive/full-unbounded-information conflict remains recorded rather than solved by dropping content or unreadable text.

Remaining broad matrices include connected Customise Home Genres/font/held-key/persistence, shared hero/artwork/Continue Watching/lifecycle, direct transport diagnostics, original Details/Library visual/navigation checks, complete shipped legal notices and isolated shared-module torrent retirement, full current-source regression/lint/native codecs/R8/4ABI/upgrade.

ADB was rechecked14 October: no device. No configured VPN/KVM/device access is established; Shield and actual install/upgrade/output acceptance remain external blockers. Owner incident data and original historical backup are unavailable. Signing environment previously excluded this branch; integration403 while reading secret metadata is not proof that signing secrets are absent. Recheck final signing access against a distinct0.139 recipe once implementation gates are satisfied; preserve existing certificate/package and historical signing workflows.

Correct delivery destination, verified by listing prior releases14 October: `Supernova/APKs`, id `1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM`, parent `1c3G--zRc7aAt_SF6KQVniyD8xv6usMmZ`. It contains actual Supernova_0.138.apk (103518717bytes),0.137 and0.136. The older “APKs to Test” folder1EOs61… contains NOVA previews and is not the current Supernova delivery folder. No upload occurred; metadata does not prove publication. Final standalone filename is Supernova_0.139.apk, version0.139/code139, established certificate79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e. Source is still0.138/code138 during development; do not label a partial APK0.139 complete.

HTML release-note source is docs/release-notes/Supernova Development.html; same published Drive file17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW. Final bundled release-history.json,0.139 changelog and HTML must describe verified final behavior, preserve prior history, distinguish candidate/delivery/physical acceptance and pass readback after delivery. They have not yet been updated to claim0.139 delivery.

No main merge, release publication, signing dispatch, branch reset or app identity change.
