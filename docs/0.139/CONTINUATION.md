# Supernova 0.139 continuation checkpoint

Implementation is ongoing. No 0.139 APK has been built, signed or delivered.

Branch: `codex/supernova-0.139-implementation-2026-10-13`.
Recovered committed source on 14 October: `e89c211b1d467dd3b4879a56f06c3d4cafb1f76e`, plus preserved MediaStateRecovery and CombinedPlaylistMediaTest changes. Those changes are now committed at `074db4bcf1112a3ec89922d70c6e8519a6393a1a`.
Current application source: `22df26ceb6ecd8a2b15dfff6113e97f7ab460fe8`; latest receipt-only commit `a90051dc067283412ef097a674157a5fc5d411c1`. Draft review PR: https://github.com/heretoecode/Supernova/pull/17, based on the existing0.138 development branch, with no merge authorization.
Read INTAKE.md, WORKSTREAMS.md, UI_CONFORMANCE.md, REPRODUCE.md and the source-pinned JSON receipts. The 43-row ledger still describes whole-workstream acceptance; staged test passes do not complete the release.

## Preserved and verified work

Earlier receipts include full regression PASS732/171 at1f8897e7 (before subsequent changes), artwork34/8 and7/2, source exporter30/4, browser favourites and five-store recovery90/8, operations20/3, connected Settings24/5, and logical-only production export24/6. Main Settings retains the permanent left rail, all26 categories without scrolling and flat Details/Instructions. Whole-library1.0 export is test-only; the dedicated legacy reader remains, with its established authentication replacement semantics. New logical restoration preserves receiving authentication. Full VideoPreferencesCommon/provider lifecycle, non-preview restore entry and hardware acceptance remain open.

Playlist receipts:

* `e89c211b`: PASS16/3 — PLAYLISTS_STATE_VERIFICATION.json, native private replacement/source/policy.
* `074db4bc`: PASS68/7 — PLAYLISTS_MEDIA_VERIFICATION.json, lists/pins/hidden in one protected SQLite participant plus earlier journal regressions.
* `29cfda5a`: PASS76/8 — PLAYLISTS_JOURNAL_VERIFICATION.json, combined default/health/viewing/media/bookmarks recovery, stable references, seven pre-decision rollback boundaries, post-decision replay, manual-change preservation, corruption, stale previews and interruption.
* `ddcbb6a6`: FAIL compilation18s — PLAYLISTS_INTEGRATION_FIRST_VERIFICATION.json. The loader hook called nonexistent Diagnostics.exception; corrected to the existing Diagnostics.error at8664c4dd. No tests executed in that failed attempt.

Current code registers25 categories including Saved playlists and Title playback. Matches remains unregistered. New playlists have local-only status4; receiving Trakt definitions/members and unowned rows stay exact. Both Trakt definition queries exclude4 while preserving NULL; native list-member query uses list_id rather than member _id. Public playlists carry names and TMDB membership only. Private restore aliases keep receiving identities stable; a source-device restore allocates a distinct identity for a local copy that would collide with a preserved account-owned definition. Pending definitions reconcile during the normal Library worker using the combined journal, preserving existing local/account definitions and never creating media.db solely for restoration/export.

## Exact verification environment and next action

Isolated detached app build: `/workspace/supernova139-lists-build/Video`, currently at source22df26ce, build-only AndroidManifest.xml/searchable_video.xml modifications preserved.
Isolated MediaLib: `/workspace/supernova139-lists-source/MediaLib`, clean8424b8af3f54f498d8e1c1cac322487e15b9ab54. Its parentebac4f4 snapshots the existing20-file0.138 preparation. Original shared dirty MediaLib at5758074 is untouched. Apply `.github/build/medialib-supernova139-playlists.patch` AFTER the established0.138 patch stack for reproduction. FileCoreLibrary remains the existing prepared sibling.
JDK `/workspace/toolchains/jdk-21.0.12.1+1`; SDK `/workspace/android-sdk`. Gradle command/flags in REPRODUCE.md; native build tasks excluded from these staged checks.

There are no remaining live verification jobs at this checkpoint. Latest integration at22df26ce is PASS71/10 (TITLE_INTEGRATION_VERIFICATION.json),2m53s, log `/workspace/scratch/supernova139-title-integration-second.log`. Preserve the existing build-only manifest/searchable XML modifications when advancing the detached worktree. XML and native preview files are in the existing build report directories. Exact stages and prior failures are in their receipts.

Completed subsequent playlist integration: PASS56/8 at3eaced03, PLAYLISTS_INTEGRATION_VERIFICATION.json. Earlier compilation failure and55/8/1-failure run remain preserved. Playlist preview no longer allocates origin preferences; source-device restore and receiving alias collisions have distinct stable identities. Source/library/controller integration now passes its stated staged checks.

Media matches: strict public source/schema/pure preparation PASS7/1 at e8faad18; receiving metadata ownership plus native dependency investigation PASS10/2 at df238703. MATCHES_SOURCE_VERIFICATION.json and MATCHES_DEPENDENCY_VERIFICATION.json preserve exact scope. Native Library joins metadata by video_id; merely rewriting files scraper pointers deletes the old metadata/pin/facts and cannot borrow another file's metadata. Changing only provider ID leaves old scraped facts visible. Implement a canonical metadata/join/cache-aware participant with coordinated originals/redo; do not register this category or drop cleanup triggers as a shortcut. It remains unregistered.

Indexed folders: private native participant and public source codec PASS10/1 at55103ee0 (INDEXED_SOURCES_VERIFICATION.json). Sources schema2/archive plus six-store private journal schema7 PASS122/12 at40e0be99 (INDEXED_SOURCES_JOURNAL_VERIFICATION.json); schemas1–6 remain readable, source-schema1 leaves indexed folders untouched. Includes eight rollback and four acknowledgement boundaries, corruption/staleness, exact native source caches/originals and the actual ShortcutDbAdapter rescan reader. Pending-source reconciliation PASS68/8 at98116b91 (INDEXED_SOURCES_RECONCILIATION_VERIFICATION.json): only receiving configured locations resolve references; no archive-location opt-in or new unknown native store; later native name/rescan choice wins; malformed pending input fails before writes. Full scanner scheduling/provider/device behavior remains open.

Title playback now has source-schema2, receiving metadata compatibility, guarded explicit preference overlays and player/manual callback integration. None is separate intent and remaps to the receiving count; legacy ambiguous/no-language indices never guess. Private overlays avoid fake lastTimePlayed and leave native watch/resume columns unchanged. PENDING unbound references reconcile only after exact indexing; existing receiving choices win. Callback acknowledgement requires both explicit backend selections, handles synchronous and failed callbacks, and respects manual supersession. Full subtitle callback now sends raw Off(-1), not the UI None index. Overlay/URI guard/source signature protects reused/moved file identities. These are synthetic backend and SQLite checks, not actual decoder output.

Title receipts: source None schema/receiving remap PASS11/2 atd18a80f8 (TITLE_NONE_VERIFICATION.json); overrides PASS18/3 atfc7da1e0 (TITLE_OVERRIDES_VERIFICATION.json); combined journal/stale guards PASS56/7 at21e9d814 (TITLE_JOURNAL_VERIFICATION.json); player hook/callback/manual stage PASS45/8 atdef2bca7 (TITLE_PLAYER_VERIFICATION.json); registered archive/pending/full callback PASS71/10 at22df26ce. Fixture failures and missed registry edit run remain preserved in FIRST receipts. No registered codec pass implies full Backup43/Shield or release acceptance.

Next finish Media matches, source/controller/full VideoPreferencesCommon/legacy/non-preview/SAF/provider/actual-force-stop matrices, and remaining43-row acceptance work. Full current-source regression, native codecs/R8/4ABI/upgrade, legal/native retirement and final release gates are still open. Do not rerun completed stages without a relevant source change or concern.

## Scope and external limitations

Current handover and roadmap were reread from decisions0163a347853b2b84ff15946c922a3305883733af; prior authoritative source/audit/visual decisions remain in INTAKE.md. Owner explicitly excludes the newly documented1.4.0 Details redesign. Do not implement it or future/parked work. Existing independently approved0.139 Details changes are preserved; no new content-density policy has been invented. The original finite/passive/full-unbounded-information conflict remains recorded rather than solved by dropping content or unreadable text.

Remaining broad matrices include connected Customise Home Genres/font/held-key/persistence, shared hero/artwork/Continue Watching/lifecycle, direct transport diagnostics, original Details/Library visual/navigation checks, complete shipped legal notices and isolated shared-module torrent retirement, full current-source regression/lint/native codecs/R8/4ABI/upgrade.

ADB was rechecked14 October: no device. No configured VPN/KVM/device access is established; Shield and actual install/upgrade/output acceptance remain external blockers. Owner incident data and original historical backup are unavailable. Signing environment previously excluded this branch; integration403 while reading secret metadata is not proof that signing secrets are absent. Recheck final signing access against a distinct0.139 recipe once implementation gates are satisfied; preserve existing certificate/package and historical signing workflows.

Correct delivery destination, verified by listing prior releases14 October: `Supernova/APKs`, id `1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM`, parent `1c3G--zRc7aAt_SF6KQVniyD8xv6usMmZ`. It contains actual Supernova_0.138.apk (103518717bytes),0.137 and0.136. The older “APKs to Test” folder1EOs61… contains NOVA previews and is not the current Supernova delivery folder. No upload occurred; metadata does not prove publication. Final standalone filename is Supernova_0.139.apk, version0.139/code139, established certificate79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e. Source is still0.138/code138 during development; do not label a partial APK0.139 complete.

HTML release-note source is docs/release-notes/Supernova Development.html; same published Drive file17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW. Final bundled release-history.json,0.139 changelog and HTML must describe verified final behavior, preserve prior history, distinguish candidate/delivery/physical acceptance and pass readback after delivery. They have not yet been updated to claim0.139 delivery.

No main merge, release publication, signing dispatch, branch reset or app identity change.

## Next independent investigation

Media identification still needs canonical metadata/join/cache-aware application and durable originals/redo, with user pins/legacy collection dependencies preserved. Native proof shows simple pointer or provider-ID rewriting is unsafe; do not register a placeholder or drop cleanup triggers. Continue other independently testable gaps while resolving this.

Legacy viewing correctness is another concrete open item: ViewingHistory.reconcile may migrate native watched state when no structured record exists; selected history/positions restoration currently owns only the structured store. Investigate and test export coverage of native legacy markers and prevention of reintroduction after selected empty restore. Preserve independent offsets/timestamps and receiving Trakt/account state, with no invented times or external writes. Actual owner incident/historical archive remains unavailable.

Also continue full production VideoPreferencesCommon/SAF/recreation/non-preview1.0 reader integration, connected Customise Home geometry/state/visual matrices, original0.139 shared UI/hero/artwork/Library/Details, diagnostics breadth, complete legal/native retirement and final candidate native codecs/R8/4ABI/lint/regression/upgrade. New1.4.0 Details redesign stays excluded.
