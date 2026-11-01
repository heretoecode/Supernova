# Original references and raw Shield diagnostics continuation

Continues `75c17bed` on the existing PR #30 branch. **Implemented and affected regressions verified; awaiting physical Shield confirmation. No new APK.** This supersedes the earlier report's missing-reference limitation. The production metadata feed limitation described below remains.

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
4. **Corrupted diagnostic monotonic clock:** event payload fields named `elapsed_ms` overwrote the recorder's monotonic timestamp with operation durations (e.g. a few milliseconds during a process with billion-millisecond uptime). Such payload values now use `duration_ms`; recorder identity/time fields cannot be overwritten by payloads. The OS exit record now preserves the exited process as `exit_pid` instead of overwriting the recorder’s own `pid`. Deferred incident records use an explicit trusted snapshot-context path so captured playback/app sessions and operations remain correct even when writers are delayed; public event payloads cannot spoof them. Raw analysis used UTC ordering to avoid the clock collision.
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

Replaced textual RT/Metacritic provider names with reference-derived vector marks, restored the square IMDb tile and trophy, positioned icon marks at the start of a quarter-width icon column, restored smaller unit typography, caption colouring, inset dividers and separate award summary/detail. Awards receive the reference’s larger vertical share; genuine provider line breaks survive sanitizing, and detail text uses single line spacing. The final-reference assertion exposed 150%-text truncation of the category at the previous two-line limit: enlarged text now receives additional award-row space and up to three detail lines, keeping the normal-size reference layout unchanged. The no-ellipsis assertion remains intact. No title/category wording is inserted into production metadata. Removed the extra visible attribution row. Technical Information and other details regions remain unchanged.

All three numeric captions now bind genuine metadata when supplied. The data model retains OMDb's actual legacy `tomatoReviews`; a validated typed `withReviewCounts` adapter accepts independently verified RT/Metacritic counts. Counts are never derived from percentages, IMDb votes or screenshot values. Missing counts render an explicit unavailable caption. Count-only updates participate in details/enrichment signatures; an available count can be shown without fabricating a missing score. Reference example numbers exist only in tests.

**Remaining production-data limitation:** the existing production OMDb access gate has no credential/entitlement binding, and standard OMDb provides no Metacritic review count. This continuation does not invent credentials, silently enable an unapproved service, scrape guessed title matches or claim live provider data. Layout/count rendering is implemented; real-world completeness depends on genuine approved metadata access. Unmodified software-rendered reference fixtures are linked below; their illustrative values are not a live metadata fetch.

## Switch comparison and correction

The original white ON-pill cause was whole-drawable/theme tint merging the track and thumb; that suppression remains in place. The previous short flat pills also differed from the video. `StandardSwitchDrawable` now supplies the approximately 70×26 reference proportions, cyan/blue ON gradient, dark OFF face, subtle border and white circular thumb. It is shared by native preference faces, native SwitchCompat tracks and playback switches; no theme tint can turn the entire face white. A newly attached native track explicitly receives current checked/enabled state, including restyling an already-checked control. Settings allocate enough width to prevent clipping. Focus remains in the existing row outline/glow, never in the switch-state fill. Disabled controls dim without changing thumb-side state; redundant On/Off text stays removed.

Only switch-face rendering and the compound-icon sizing correction below change in the approved Subtitles panel: its row layout, dimensions, backend, labels, track/download popups and preferences remain intact. A committed-source scope check enforces that exact replacement; the existing panel tests/render tests must pass. The video does not justify redesigning Audio/Video/More or other controls.

## Custom Library and global language recheck

The source implementation from 75c17bed is preserved: Custom-only pending-position-zero protection and attached-page snapshot refresh. Its reproduced source root cause was initial shared focus restoration queuing row one (Filters) before a holder existed, which scrolled row-zero statistics away; Custom entry now retains row zero. Loader refresh previously detached/remounted the page; the preserved fix updates its attached snapshot. Movies/TV branches remain unchanged. A new SDK30 native-graphics regression uses a populated 45-title custom viewport, first focus before layout, full initial statistics visibility and refresh without scrolling. Its capture uses the configured density/physical viewport (1920×1080), rather than the older unrelated 960×540 helper; no production geometry was changed to accommodate the test. The earlier actual CustomLibraryActivity first-entry/no-detach regression remains.

Global language rendering remains `PreviewLanguageIcon`, including real regional PNGs and neutral fallback for generic/undetermined codes. The preserved inline fallback correction ensures unknown/null/undetermined track metadata no longer drops the icon entirely. Existing signed-APK inspection already found retained regional drawables in the correct resource package; that proves packaging only, not Shield rendering. A further confirmed binding defect was found: the selected Subtitle Track label bound its icon at 20dp and then changed its bounds to the approved 46 reference pixels, leaving TextView’s cached compound width stale. A shared size-aware bind now sets final bounds before assignment; the existing 46px design token is retained. An SDK30 regression checks actual cached padding against final drawable bounds before/after track refresh. This is not assumed to explain every reported missing flag. Existing text/ImageView/inline/popup rendering regressions are rerun. Archive track counts cannot be converted into language or region values; actual Shield icon binding, codes and visibility remain unverified. No arbitrary country flags are assigned to generic languages.

## Validation and delivery

Final application source: `f5efd624448298ce7e01443e39ef2b6686c46473`. Subsequent delivery documentation/capture changes do not modify application or test sources. All three original files were analysed; original-reference availability is resolved, while the production-data and physical-device limitations below remain.

Candidate checks are recorded by source/run below. No APK-producing tasks were requested; existing PR validation uses tests, lint and Java compilation only. Original raw logs remain private. Physical Shield acceptance stays open even when software regressions pass.

| Scope / source | Evidence | Result |
| --- | --- | --- |
| Full app suite, `34017913` | [41359107706](https://github.com/heretoecode/Supernova/actions/runs/41359107706), artifact 13105148073 | 1,293 cases: 1,291 passed, two diagnostic failures below. Libraries: 300 passed/four existing skips. Compilation/lint completed; zero lint errors/1,251 warnings. This was before the diagnostic/session corrections and final reference-spacing changes. |
| Languages, Subtitles, playback focus and switch face, `a75997f4` | [41359647840](https://github.com/heretoecode/Supernova/actions/runs/41359647840), artifact 13105415402 | 31 app passes; 300 library passes/four existing skips; compile/lint passed, zero errors/1,251 warnings. Includes new SDK30 actual Dialog focus-crash and selected-icon cached-width regressions. |
| Settings and shared switch face, `eca8f287` | [41359758407](https://github.com/heretoecode/Supernova/actions/runs/41359758407), artifact 13104908311 | 26 app passes; 300 library passes/four existing skips; compile/lint passed, zero errors/1,251 warnings. Checked/disabled restyle, white thumb, OFF fill, ON gradient and unchanged focus-state pixels verified. |
| Diagnostics correction, `54615e24` | [41360768312](https://github.com/heretoecode/Supernova/actions/runs/41360768312), artifact 13105172395 | 62 app passes, including both formerly failing methods; 300 library passes/four existing skips; 150 tooling passes; compile/lint passed, zero errors/1,251 warnings. |
| Reception/metadata/Custom regressions, `5b0e4464` | [41361495483](https://github.com/heretoecode/Supernova/actions/runs/41361495483), artifact 13105357543 | 66 app cases: 65 passed/one newly asserted award-detail truncation at 150% text. 300 library passes/four existing skips. Normal/720p/4K fixtures and metadata/Custom regressions passed. Enlarged-text correction follows below. |
| Final Reception enlargement, `f5efd624` | [41362433013](https://github.com/heretoecode/Supernova/actions/runs/41362433013), artifact 13105014445 | 47 app passes: complete reference text at 720p/1080p/4K/150%, Reception/metadata/details regressions. 300 library passes/four existing skips; 150 tooling passes; compile/lint passed, zero errors/1,251 warnings. Normal and enlarged captures inspected; normal PNG byte-identical to preceding run. |
| Initial Custom capture, `82e7646c` | [41361733457](https://github.com/heretoecode/Supernova/actions/runs/41361733457), artifact 13105477600 | 19 app passes, including populated initial/no-scroll/refresh and actual CustomLibraryActivity entry/no-detach; 300 library passes/four existing skips; 150 tooling passes; compile/lint passed, zero errors/1,251 warnings. First-layout and refreshed PNGs inspected. |
| Local parser, scoped preservation guard and tooling | `java tools/ParseJava.java src`; preservation check; unittest discovery | 1,110 Java sources parse without syntax errors; approved-scope guard passes; all 150 tooling tests pass, including private-archive aggregate fixtures. |

The full run includes 18/18 `StreamingRepositoryTest` and 16/16 `PreviewPlaybackMenusTest` passes, covering episode identity and guarded focus respectively.

No all-green full suite at final HEAD is claimed. Full-suite scope plus corrected affected-class runs are reported separately. The library log’s “HTTP length mismatch” is the deliberately failing truncation fixture within a passing test, not a separate suite failure.

[Unmodified software evidence and provenance](owner-reference-software-evidence/README.md).

Initial affected run 41358847531: 177 application tests, 175 passed / two diagnostic failures; same two failures occurred in diagnostic-only follow-up 41359345706. They exposed an overly broad new payload guard blocking legitimate captured session context, and missing cleanup in the new holder test contaminating the existing post-failure-window regression. Both are corrected; original delayed-session/window assertions are retained and the complete diagnostic-class follow-up passes all 62 cases.

## Exact changed files since 75c17bed

These are the complete continuation paths relative to the repository root. Original raw diagnostic ZIP/logs are excluded. `STATUS.md` separates this unshipped source from the historic delivered APK.

### Application and resources

- [res/drawable/preview_preference_toggle.xml](../../res/drawable/preview_preference_toggle.xml)
- [res/drawable/reception_awards.xml](../../res/drawable/reception_awards.xml)
- [res/drawable/reception_metacritic.xml](../../res/drawable/reception_metacritic.xml)
- [res/drawable/reception_rotten_tomatoes.xml](../../res/drawable/reception_rotten_tomatoes.xml)
- [res/layout/preview_preference_checkbox.xml](../../res/layout/preview_preference_checkbox.xml)
- [src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java](../../src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java)
- [src/main/java/com/archos/mediacenter/video/leanback/PreviewLanguageIcon.java](../../src/main/java/com/archos/mediacenter/video/leanback/PreviewLanguageIcon.java)
- [src/main/java/com/archos/mediacenter/video/leanback/StandardSwitchDrawable.java](../../src/main/java/com/archos/mediacenter/video/leanback/StandardSwitchDrawable.java)
- [src/main/java/com/archos/mediacenter/video/leanback/details/DetailsBlueprintPanel.java](../../src/main/java/com/archos/mediacenter/video/leanback/details/DetailsBlueprintPanel.java)
- [src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java](../../src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java)
- [src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java](../../src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java)
- [src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsWorkspace.java](../../src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsWorkspace.java)
- [src/main/java/com/archos/mediacenter/video/metadata/OmdbSupplement.java](../../src/main/java/com/archos/mediacenter/video/metadata/OmdbSupplement.java)
- [src/main/java/com/archos/mediacenter/video/player/PlaybackSubtitlePanel.java](../../src/main/java/com/archos/mediacenter/video/player/PlaybackSubtitlePanel.java)
- [src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java](../../src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java)
- [src/main/java/com/archos/mediacenter/video/streaming/StreamingRepository.java](../../src/main/java/com/archos/mediacenter/video/streaming/StreamingRepository.java)

### Regression tests and archive analysis

- [src/test/java/com/archos/mediacenter/video/diagnostics/DiagnosticsTest.java](../../src/test/java/com/archos/mediacenter/video/diagnostics/DiagnosticsTest.java)
- [src/test/java/com/archos/mediacenter/video/leanback/CustomLibraryPageTest.java](../../src/test/java/com/archos/mediacenter/video/leanback/CustomLibraryPageTest.java)
- [src/test/java/com/archos/mediacenter/video/leanback/StandardSwitchReferenceTest.java](../../src/test/java/com/archos/mediacenter/video/leanback/StandardSwitchReferenceTest.java)
- [src/test/java/com/archos/mediacenter/video/leanback/details/DetailsReferenceRenderTest.java](../../src/test/java/com/archos/mediacenter/video/leanback/details/DetailsReferenceRenderTest.java)
- [src/test/java/com/archos/mediacenter/video/leanback/details/ReceptionSupplementTest.java](../../src/test/java/com/archos/mediacenter/video/leanback/details/ReceptionSupplementTest.java)
- [src/test/java/com/archos/mediacenter/video/leanback/settings/SettingsWorkspaceTest.java](../../src/test/java/com/archos/mediacenter/video/leanback/settings/SettingsWorkspaceTest.java)
- [src/test/java/com/archos/mediacenter/video/metadata/OmdbSupplementTest.java](../../src/test/java/com/archos/mediacenter/video/metadata/OmdbSupplementTest.java)
- [src/test/java/com/archos/mediacenter/video/player/PlaybackSubtitlePanelTest.java](../../src/test/java/com/archos/mediacenter/video/player/PlaybackSubtitlePanelTest.java)
- [src/test/java/com/archos/mediacenter/video/player/PreviewPlaybackMenusTest.java](../../src/test/java/com/archos/mediacenter/video/player/PreviewPlaybackMenusTest.java)
- [src/test/java/com/archos/mediacenter/video/streaming/StreamingRepositoryTest.java](../../src/test/java/com/archos/mediacenter/video/streaming/StreamingRepositoryTest.java)
- [tools/analyse_shield_archive.py](../../tools/analyse_shield_archive.py)
- [tools/test_analyse_shield_archive.py](../../tools/test_analyse_shield_archive.py)

### Preservation workflow

- [.github/build/verify-owner-reference-preservation.py](../../.github/build/verify-owner-reference-preservation.py)
- [.github/workflows/supernova-pr30-conformance.yml](../../.github/workflows/supernova-pr30-conformance.yml)

### Reports, original references and unmodified software evidence

- [docs/0.149/owner-reference-software-evidence/reception-reference-150-percent.png](../../docs/0.149/owner-reference-software-evidence/reception-reference-150-percent.png)
- [docs/0.149/owner-reference-software-evidence/reception-reference-1080p.png](../../docs/0.149/owner-reference-software-evidence/reception-reference-1080p.png)
- [docs/0.149/CONSOLIDATED_FIX_INVESTIGATION_2026-11-01.md](../../docs/0.149/CONSOLIDATED_FIX_INVESTIGATION_2026-11-01.md)
- [docs/0.149/OWNER_REFERENCE_AND_RAW_DIAGNOSTICS_REVIEW_2026-11-01.md](../../docs/0.149/OWNER_REFERENCE_AND_RAW_DIAGNOSTICS_REVIEW_2026-11-01.md)
- [docs/0.149/STATUS.md](../../docs/0.149/STATUS.md)
- [docs/0.149/owner-reference-software-evidence/README.md](../../docs/0.149/owner-reference-software-evidence/README.md)
- [docs/0.149/owner-reference-software-evidence/custom-populated-after-refresh-api30.png](../../docs/0.149/owner-reference-software-evidence/custom-populated-after-refresh-api30.png)
- [docs/0.149/owner-reference-software-evidence/custom-populated-first-frame-api30.png](../../docs/0.149/owner-reference-software-evidence/custom-populated-first-frame-api30.png)
- [docs/0.149/owner-reference-software-evidence/gemini-switch-states-api30.png](../../docs/0.149/owner-reference-software-evidence/gemini-switch-states-api30.png)
- [docs/0.149/owner-reference-software-evidence/provenance.json](../../docs/0.149/owner-reference-software-evidence/provenance.json)
- [docs/0.149/owner-reference-software-evidence/regional-language-icons.png](../../docs/0.149/owner-reference-software-evidence/regional-language-icons.png)
- [docs/0.149/owner-reference-software-evidence/settings-switches-1080p.png](../../docs/0.149/owner-reference-software-evidence/settings-switches-1080p.png)
- [docs/0.149/owner-reference-software-evidence/subtitles-main.png](../../docs/0.149/owner-reference-software-evidence/subtitles-main.png)
- [docs/0.149/owner-reference-software-evidence/subtitles-track.png](../../docs/0.149/owner-reference-software-evidence/subtitles-track.png)
- [docs/0.149/owner-references/IMG_5090.jpeg](../../docs/0.149/owner-references/IMG_5090.jpeg)
- [docs/0.149/owner-references/diagnostic-aggregate.json](../../docs/0.149/owner-references/diagnostic-aggregate.json)
- [docs/0.149/owner-references/gemini_generated_video_A1E9E8FB.mp4](../../docs/0.149/owner-references/gemini_generated_video_A1E9E8FB.mp4)
- [docs/0.149/owner-references/provenance.json](../../docs/0.149/owner-references/provenance.json)

## Remaining acceptance limits

- No attached Shield, Android emulator or adb runtime is available in this workspace. All supplied PNGs are Robolectric/software fixture output. Native graphics refers to the Robolectric graphics backend, not a physical Nvidia/GPU run. No physical fix or signed-APK runtime claim is made.
- Shield first-entry statistics, real metadata on every language surface, remote hardware input, provider counts, actual playback and cold-entry rendering remain awaiting device confirmation. The original partial logs cannot replace those checks.
- Production Reception needs a genuine authorised metadata feed to fill every original provider value/count/award detail. Existing safe access gating remains; missing values are explicit rather than fabricated.
- Scanner source/listing exceptions, residual legacy focus symptoms and the native file-error cause remain unproven in the retained archive. DNS/timeout observations do not establish a precise external infrastructure cause.
- No APK was assembled, signed or delivered; no version/release/tag was started and PR #30 remains draft/unmerged. Existing production native libraries and Movies/TV page sources were preserved.
