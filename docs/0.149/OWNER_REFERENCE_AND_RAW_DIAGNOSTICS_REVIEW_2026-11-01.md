# Original references and raw Shield diagnostics continuation

Continues `75c17bed` on the existing PR #30 branch. **Candidate implementation; CI pending. No physical Shield verification or new APK.** This supersedes the earlier report's missing-reference limitation. The production metadata feed limitation described below remains.

## Original evidence

All three owner files were extracted from `Supernova_0.149_Codex_QA_References.zip` and inspected. [Provenance and original-file SHA-256 values](owner-references/provenance.json). [Reception original](owner-references/IMG_5090.jpeg) and [Gemini original video](owner-references/gemini_generated_video_A1E9E8FB.mp4) are retained unchanged; the private diagnostics ZIP/logs remain outside the repository.

The video is 10 seconds, 910×512, 30fps. Reviewed OFF, ON, transition and return-to-OFF frames. The switch is approximately 2.7:1, with a dark OFF face, cyan-to-blue ON gradient, white circular thumb and separate outline/glow. Demo mouse-pointer/click animation and “Feature Status” labels are not product requirements; the owner's previous no-redundant-On/Off-text instruction still applies.

The JPEG shows square IMDb, red/green tomato, gold-ring tilted Metacritic mark, and gold trophy. Score captions remain directly below their scores: IMDb votes, Tomatometer review count, Metascore review count. Internal dividers begin after the icon; awards have a summary and separate detail lines. Technical Information remains immediately below. No visible OMDb attribution row or written-review button appears in this reference.

## Raw archive coverage and version attribution

Processed every retained JSONL stream, deduplicating by `(process, sequence)`: **18,557 decoded lines → 16,000 distinct events**, 2,557 duplicate copies, zero conflicting identities. One partial/malformed line in a retained important stream was excluded. Archive coverage explicitly says PARTIAL; dropped=0/write errors=0 does not restore pruned context.

The export describes installed 0.149 / Android 11 / SDK30 / NVIDIA SHIELD, but retains history across four versions: 22 startup records for 0.146, 44 for 0.147, 50 for 0.148, 15 for 0.149. Do not attribute all retained failures to 0.149.

### Confirmed application defects corrected

1. **Playback-menu uncaught exception:** one main-thread `IllegalArgumentException` in a retained 0.148 process. Stack: `ViewGroup.offsetRectBetweenParentAndChild` → `FocusFinder.findNextFocus` → `PreviewPlaybackMenus` dialog D-pad UP. The focused tab/outer control is outside `nested`, but the current source still passes it into `FocusFinder(nested, focused, UP)`. All three menu focus searches now verify subtree membership before invoking FocusFinder. SDK30 regression dispatches real Dialog UP with outer-tab focus and a nested adjustment panel; detached targets are also checked. No broad exception swallowing.
2. **Episode external-ID validation:** `tv/{series}/season/{s}/episode/{e}/external_ids` returns an **episode** ID, but the validator compared it with the **series** ID in the URL. Valid chapter metadata was rejected. Exact nested-episode routes now validate positive episode identity and external-ID fields; series/movie identity and malformed-payload checks remain. Twelve retained semantic-failure events are consistent with this path, but missing operation payload/endpoint context prevents proving that all twelve share this cause.
3. **False unclean-exit markers across processes:** all roles wrote one shared session marker. **35 of 101 warnings name a previous process that continued logging more than one second after the warning**, proving those warnings did not identify that process's death. Session markers now use stable per-process-role storage. Manual diagnostic-reference storage remains unchanged. The remaining 66 warnings are still not confirmed crashes.
4. **Corrupted diagnostic monotonic clock:** event payload fields named `elapsed_ms` overwrote the recorder's monotonic timestamp with operation durations (e.g. a few milliseconds during a process with billion-millisecond uptime). Such payload values now use `duration_ms`; recorder identity/time fields cannot be overwritten by payloads. The OS exit record now preserves the exited process as `exit_pid` instead of overwriting the recorder’s own `pid`. Raw analysis used UTC ordering to avoid that collision.
5. **Misclassified focus-holder geometry:** 742 of the 743 `navigation_overlap` events target the deliberately focused `PreviewPages` content holder, whose viewport extends behind navigation. Geometry inspection no longer treats that holder as a leaf control. It does not suppress actual leaf/control warnings.

### Disposition of all other actionable classes

| Evidence | Finding / remaining limitation |
| --- | --- |
| 864 focus warnings | 743 overlap (742 holder, one FocusRecycler), 80 offscreen, 27 fallback, 12 repeated unhandled navigation, two clipped. Of the 27 fallbacks, 26 succeeded; one failed 0.147 restoration correlates with a legacy VerticalGridView offscreen/unhandled-navigation burst. Retained data lacks enough hierarchy/transition context to safely change the working Movies/TV pages. That legacy concern remains unresolved rather than suppressed or counted as many independent bugs. |
| 166 source-scan failures, 8 batch-failure records | Two anonymized source identities; 158 source failures concern one of them. Several records mirror the same source/batch across app/scanner processes. The eight batch records form four near-simultaneous cross-process pairs. Twenty source failures and two batch records are tagged 0.149. No underlying listing/storage/permission exception is retained, so an app-vs-source/network/storage root cause cannot be established. Existing scanner code protects traversal-error locations against deletions. No speculative scanner changes. |
| 671 metadata network failures, 672 package failures | One process during 31 October 12:16–13:25 UTC; its startup/version is not retained. Network categories: 666 UnknownHostException, five SocketTimeoutException. Package categories: 667 UnknownHostException, five timeout (mostly provider availability; three season packages). These are upstream DNS/transport failures and propagated enrichment-package failures, **not missing Android APK packages/resources** or 1,343 independent bugs. The exact DNS/server/connectivity cause is not retained. No credential/key/host guessing or arbitrary retry changes. |
| 12 semantic failures | Ten tagged 0.148, two tagged 0.149; all `invalid_payload`. Episode-validator defect above corrected, but individual attribution remains limited by missing request/payload context. Invalid/mismatched root-title responses remain rejected. |
| One player error | Tagged 0.147: code1001 = VE_FILE_ERROR, qualifier1000 = VEQ_NONE, state5 = PLAYING (mapped against pinned MediaLib interface). A reopen follows ~0.3s later, then prepared/playing callbacks and WebDAV reads. No native decoder/file/network cause chain is retained. This is not the Java crash. No speculative decoder/network changes. |
| 714 playback-attempt records | 238 distinct attempt IDs; 474 seek-boundary records and 240 open-boundary records. These are transitions, not 714 failed plays. One OPEN terminal failure matches the file error; cancellations/supersessions/unattributed callbacks are not independently diagnosed as user-visible playback faults. |
| 101 suspected unclean exits / one OS exit record | Cross-process false markers corrected as above. The retained OS reason10 is USER_REQUESTED, not a crash/ANR classification. One Java exception is independently evidenced; no total of 101 crashes is inferred. |
| Custom Library and language evidence | 321 CustomLibraryActivity-scoped events include entry, toolbar focus, D-pad transitions and lifecycle, but no statistics rectangles/initial scroll offset. Audio/subtitle metadata events contain selected track/count only: **no retained language-code fields**. Logs cannot prove initial visibility or explain all-screen flag absence. |

## Reception restoration

Replaced textual RT/Metacritic provider names with reference-derived vector marks, restored the square IMDb tile and trophy, positioned icon marks at the start of a quarter-width icon column, restored smaller unit typography, caption colouring, inset dividers and separate award summary/detail. Removed the extra visible attribution row. Technical Information and other details regions remain unchanged.

All three numeric captions now bind genuine metadata when supplied. The data model retains OMDb's actual legacy `tomatoReviews`; a validated typed `withReviewCounts` adapter accepts independently verified RT/Metacritic counts. Counts are never derived from percentages, IMDb votes or screenshot values. Missing counts render an explicit unavailable caption. Count-only updates participate in details/enrichment signatures; an available count can be shown without fabricating a missing score. Reference example numbers exist only in tests.

**Remaining production-data limitation:** the existing production OMDb access gate has no credential/entitlement binding, and standard OMDb provides no Metacritic review count. This continuation does not invent credentials, silently enable an unapproved service, scrape guessed title matches or claim live provider data. Layout/count rendering is implemented; real-world completeness depends on genuine approved metadata access. Original-reference visual comparison and software captures are required below before finalizing this candidate.

## Switch comparison and correction

The previous short flat pills differed from the video. `StandardSwitchDrawable` now supplies the approximately 70×26 reference proportions, cyan/blue ON gradient, dark OFF face, subtle border and white circular thumb. It is shared by native preference faces, native SwitchCompat tracks and playback switches; no theme tint can turn the entire face white. Settings allocate enough width to prevent clipping. Focus remains in the existing row outline/glow, never in the switch-state fill. Disabled controls dim without changing thumb-side state; redundant On/Off text stays removed.

Only the switch drawable in the approved Subtitles panel changes: its row layout, dimensions, backend, labels, track/download popups and preferences remain intact. A committed-source scope check enforces that exact replacement; the existing panel tests/render tests must pass. The video does not justify redesigning Audio/Video/More or other controls.

## Custom Library and global language recheck

The source implementation from 75c17bed is preserved: Custom-only pending-position-zero protection and attached-page snapshot refresh. A new SDK30 native-graphics regression uses a populated 45-title custom viewport, first focus before layout, full initial statistics visibility and refresh without scrolling. The earlier actual CustomLibraryActivity first-entry/no-detach regression remains.

Global language rendering remains `PreviewLanguageIcon`, including real regional PNGs and neutral fallback for generic/undetermined codes. Existing text/ImageView/inline/popup rendering regressions are rerun. Archive track counts cannot be converted into language or region values; actual Shield icon binding, codes and visibility remain unverified. No arbitrary country flags are assigned to generic languages.

## Validation and delivery

Pending candidate CI. No APK-producing tasks are requested; existing PR validation uses tests, lint and Java compilation only. Original raw logs remain private. Keep PR #30 draft/unmerged and do not start a release. Physical Shield acceptance stays open even if software/native-graphics regressions pass.
