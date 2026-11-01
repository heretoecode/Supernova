# Shield 0.149 consolidated correction report

**Status: confirmed source corrections implemented and tested; physical Shield acceptance OPEN. Exact Reception restoration and raw diagnostics investigation BLOCKED by missing inputs.**

Continues baseline `02ba4b02` on `codex/agreed-fixes-2026-11-01`, existing PR #30. Tested application/test/resource/workflow source: `f4bdffa2a26fcd57cba3e64da7d41d19416c12bc`. Later report-only commits do not change those trees. No new version, APK, signing, release, tag or merge performed.

## Available evidence and limits

Read both existing Shield handovers. The original `Supernova Diagnostics - 2026-11-01 21-02-06-018.zip`, `IMG_5090.jpeg` and Gemini toggle demonstration are **absent from this workspace/repository**. No Shield, Android SDK/adb or emulator is available locally. CI uses Robolectric native graphics; its software captures are not device/emulator screenshots. Device metadata values and installed APK bytes are not independently verified.

## Custom Library: initial statistics

**Root cause:** Custom Library enters before its first RecyclerView layout. `focusFromNavigation` requests adapter position zero (statistics), but `restoreFocus` replaces that pending request with `scrollToPosition(1)` when Filters has not been laid out. This can put statistics above the initial viewport. Movies/TV generally enter using an already-mounted shared page.

**Correction:** retain the top offset in that pre-layout case, gated specifically to Custom Library. Supply its snapshot before attachment and use the shared navigation-entry method. Movies/TV paths and their page design remain unchanged.

A second confirmed lifecycle defect was corrected: asynchronous loader completion rebuilt the custom hierarchy, detaching and reattaching the same shared page. Detach cancels its discovery worker and focus/enrichment callbacks while `requestedDiscovery` remains true, preventing retry of an interrupted discovery request. Refresh now calls `setSnapshot` on the attached page, as Movies/TV do.

**Regression:** pending scroll must remain at row zero before layout; saved custom-page activity must show the entire statistics panel on first entry without scroll input; asynchronous snapshot delivery must not detach the shared page. Tests pass. Physical first-entry/re-entry/relaunch/refresh acceptance remains open.

## Global language icons

Owner clarified that all language-icon locations must use the global implementation. Inspected Settings choices, Audio/Subtitle track binding, download results and inline details; these use `PreviewLanguageIcon`. Track metadata comes from `.language`, carried through `preview_track_language`; settings/provider choices supply their stored locale/provider code.

**Confirmed defect:** the shared `inline` path returned plain text without any icon for null, empty, undetermined and unrecognized language metadata. It now consistently provides a neutral globe. Regional locales keep their genuine country flag; generic `en`/`eng` never invent a nationality. OpenSubtitles `zh-ca` remains a neutral Cantonese alias. Code interpretation is centralized in the constructor; region parsing tolerates surrounding whitespace.

**Regression:** real ImageView drawing without manual drawable bounds, inline ImageSpan fallback for unknown inputs, country resource decoding and full-colour rendering despite monochrome tint. The older assertion requiring no icon for unknown metadata was updated to require one neutral icon while preserving its label. Tests pass.

**Still open:** the root cause of the owner's **all-screen Shield** failure is not proven by the inline omission. Actual Shield values, affected-view visibility and hardware rendering remain unverified. Android supports fallback bounds for dimensionless drawables; speculative intrinsic-size changes were removed rather than represented as a fix.

**Existing delivered binary inspection:** retrieved the existing signed artifact from run 41331247034 / artifact 13093937009. APK SHA-256 `cf589e8b78a28267b76892325d628244afe932a3fc140f6f4443054f78cbaa6c` matches the previously delivered receipt. Parsed `resources.arsc`: package `app.supernova.player`, ID `0x7f`, matches global context lookup; tested region names remain present. This rules out a resource-package-name mismatch in that delivered artifact. Existing `region_*` shrink retention is unchanged. Resource presence is **not** offered as rendering acceptance, nor is that artifact assumed to match the owner's installed bytes. No new APK was built.

## Conventional switches

**Confirmed causes and corrections:**

- A supported custom white accent produced a white checked track beside a white thumb, merging them into a white pill. Shared `PreviewAccent.switchTrack` now uses blue for very light/dark accents while retaining normal accent colours. Settings and standard playback switches use this shared rule. Native SwitchCompat raster tests cover white, black and normal accents.
- Legacy preference tinting recoloured the entire multicolour CheckBox layer-list, erasing the track/thumb distinction. Clear that tint and use the checked/unchecked artwork directly. The OFF thumb is white; explicit disabled checked/unchecked entries retain position and hue with dimmed colours.
- SettingsWorkspace and standard playback switch rows explicitly appended redundant On/Off text. Removed visual status text and retained spoken accessibility state.
- Disabled standard playback switches were noninteractive but fully opaque. They now dim; modern Settings already preserves disabled track/opacity and availability help. Approved Subtitles controls already dim correctly and were left unchanged.

Focus remains a separate row outline; checked-state artwork has no focus-dependent fill. Existing parent-row click ownership and persisted preference model are retained, with no second toggle listener. Remote navigation/persistence regressions and raster checks pass. Real remote OK, restart persistence and Shield appearance remain unverified. Exact Gemini comparison is unavailable.

## Reception: partial correction, not exact restoration

**Root cause:** the panel explicitly added either “Reviews unavailable” or a Read reviews button, and the details page supplied its reader action. Removed those affordances. No new review cards, articles or reader interface introduced.

IMDb votes now sit beneath the score. When IMDb is available, suppress the extra TMDb/indexed score row, preserving the four-source hierarchy. When IMDb is absent, retain the genuine existing rating/votes as a fallback. Awards, dividers and the complete Technical Information composition remain.

The full suite caught a real introduced large-text regression: a wrap-content vote caption consumed the height before a weighted score, leaving 14px for a minimum 28px line. Both captions now receive proportional height with autosizing, and the redundant extra rating no longer consumes space. The existing 150% text no-clipping assertion was retained unchanged and now passes, along with the other reference viewport tests.

**Blocked:** exact comparison with IMG_5090.jpeg; original RT/Metacritic logos and review-count presentation. Only IMDb has existing logo artwork; RT/Metacritic are still text marks. `OmdbSupplement.PRODUCTION` deliberately denies access pending entitlement, and its model only contains IMDb votes, not RT/Metacritic review counts. No fabricated counts, screenshot numbers as production data, unapproved provider access or guessed replacement design added. A reference attachment and a genuine approved data source for missing counts are needed to finish parity. The software fixture below is not a claim of that parity.

## Latest Shield diagnostics

**Only the existing handover summary was analysed; the original ZIP was not available.** Counts are events, not distinct bugs or confirmed crash totals.

| Class | Disposition |
| --- | --- |
| Focus anomalies: 864 | Need session/operation context to distinguish expected transitions from genuine focus loss. Custom lifecycle/focus corrections are not claimed to resolve these archive events. |
| Scan failed: 166; batches: 8 | Need unique source/operation IDs and exception chains. No confirmed scanner root cause from counts. |
| Metadata network/package: 671/672; semantic: 12 | Could include cascades from the same operation. Need endpoint/status/cause context; neither external failure nor app defect is established by totals. |
| Playback error: 1; uncaught exception: 1 | Actual player cause chain and stack traces required. No crash diagnosis claimed. |
| Suspected unclean exits: 101 | Not confirmed crashes. Correlate sessions/processes and Android exit evidence. Partial archive coverage and clean-shutdown counts do not establish crash totals. |

No speculative scanning, network, playback or exception-handling changes made. Raw private diagnostics were not published.

## Validation results

| Run/source | Result |
| --- | --- |
| [Final affected regressions 41354599694](https://github.com/heretoecode/Supernova/actions/runs/41354599694), **f4bdffa2** | **73 application tests passed**, zero failures/errors/skips; **300 library passes / 4 existing skips**; release Java compilation and lint pass, **0 lint errors / 1,251 warnings**; **147 tooling tests passed**. Includes both previously failing classes and all final switch corrections. Artifact 13103395424, retrieved XML inspected. |
| [Broader suite 41351832319](https://github.com/heretoecode/Supernova/actions/runs/41351832319), 50f76e93 | 1,282 application tests: **1,280 passed / 2 failed**, zero errors/skips. Libraries 300 passes / 4 existing skips; compilation/lint completed. Failures were the superseded unknown-language assertion and the real Reception 150% clipping defect described above. Both full classes pass in the final targeted run. **No clean full-suite result is claimed after the corrections.** |
| [Subtitles regressions 41351832324](https://github.com/heretoecode/Supernova/actions/runs/41351832324), 50f76e93 | 33 passes, zero failures/errors/skips; Java compilation and tooling pass. Approved Subtitles panel source remains unchanged through final source; global icon implementation in this run is identical to final source. |
| [Earlier affected suite 41351239143](https://github.com/heretoecode/Supernova/actions/runs/41351239143), b161ae7b | 170 application passes; 300 library passes / 4 existing skips. Predates the final global fallback, switch contrast and large-text corrections; not substituted for their final validation. |

Final affected classes: CustomLibraryPageTest (8), NextQaWizardTest (10), Supernova139LanguageFlagsTest (4), Supernova141UiTest (8), DetailsReferenceRenderTest (4), ReceptionSupplementTest (4), SettingsWorkspaceTest (25), NextHudTest (10).

Local JDK parser: 1,108 Java files, zero syntax errors. `git diff --check` passes. CI is tests/lint/compilation only. The old PR workflow's automatic APK assembly runs were cancelled before their Gradle step, and assembly/delivery steps replaced with compilation/evidence collection. Its obsolete frozen-source guard was replaced with committed-input preservation checks for the newly authorised correction scope; the original native/signing tools remain untouched. No signing/release workflow dispatched.

## Software visual evidence

**Robolectric native-graphics fixtures only; not Android emulator/Shield screenshots. Data is synthetic, not owner metadata or production hardcoding.**

- [Custom first entry without scrolling — final source](shield-continuation-software-evidence/custom-first-entry-robolectric-f4bdffa2.png): complete statistics visible for an empty fixture library.
- [ON/OFF switches — final source](shield-continuation-software-evidence/switch-states-robolectric-f4bdffa2.png): separate white thumbs, coloured/dark tracks, no adjacent state text. White/black-accent and disabled variants additionally pass raster assertions.
- [Reception at 150% text — final source](shield-continuation-software-evidence/reception-large-text-robolectric-f4bdffa2.png): score/count no longer clipped; four source slots and Technical Information visible. Not an exact original-reference match.
- [Subtitle Track regional flags and neutral fallback](shield-continuation-software-evidence/subtitle-flags-robolectric-50f76e93.png): source 50f76e93; that popup/global icon implementation is unchanged in final source.

## Preservation and remaining acceptance

Committed diff checks confirm no change to version/build identity, release history/legal assets, native/signing inputs, resource retention, playback HUD/controller or approved PlaybackSubtitlePanel. Movies/TV paths remain unchanged by the Custom-only shared focus condition. Their existing regressions were included in the earlier broader/affected validation; no physical functionality is inferred from those passes.

Remaining work: obtain original ZIP, IMG_5090.jpeg and Gemini reference; finish genuine provider counts/logos and exact Reception parity; trace the actual all-screen Shield icon failure; inspect raw diagnostic stacks/operations; perform physical first-entry/re-entry/relaunch/refresh statistics checks, language rendering, both toggle states/focus/disabled/one-OK/persistence, and Reception acceptance. **Implemented portions await Shield confirmation; the overall handover is not fully closed.** PR #30 stays draft and unmerged. No new APK/release without owner approval.

## Exact changed files

- `.github/workflows/agreed-fixes-validation.yml`
- `.github/workflows/supernova-pr30-conformance.yml`
- `docs/0.149/CONSOLIDATED_FIX_INVESTIGATION_2026-11-01.md`
- `docs/0.149/SHIELD_QA.md`
- `docs/0.149/shield-continuation-software-evidence/custom-first-entry-robolectric-f4bdffa2.png`
- `docs/0.149/shield-continuation-software-evidence/reception-large-text-robolectric-f4bdffa2.png`
- `docs/0.149/shield-continuation-software-evidence/subtitle-flags-robolectric-50f76e93.png`
- `docs/0.149/shield-continuation-software-evidence/switch-states-robolectric-f4bdffa2.png`
- `res/drawable/preview_preference_toggle.xml`
- `src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java`
- `src/main/java/com/archos/mediacenter/video/leanback/PreviewAccent.java`
- `src/main/java/com/archos/mediacenter/video/leanback/PreviewLanguageIcon.java`
- `src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java`
- `src/main/java/com/archos/mediacenter/video/leanback/details/DetailsBlueprintPanel.java`
- `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java`
- `src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java`
- `src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsWorkspace.java`
- `src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java`
- `src/test/java/com/archos/mediacenter/video/leanback/CustomLibraryPageTest.java`
- `src/test/java/com/archos/mediacenter/video/leanback/NextQaWizardTest.java`
- `src/test/java/com/archos/mediacenter/video/leanback/PreviewMoviePageTest.java`
- `src/test/java/com/archos/mediacenter/video/leanback/Supernova139LanguageFlagsTest.java`
- `src/test/java/com/archos/mediacenter/video/leanback/Supernova141UiTest.java`
- `src/test/java/com/archos/mediacenter/video/leanback/details/DetailsImprovementsTest.java`
- `src/test/java/com/archos/mediacenter/video/leanback/details/ReceptionSupplementTest.java`
- `src/test/java/com/archos/mediacenter/video/leanback/settings/SettingsWorkspaceTest.java`
- `src/test/java/com/archos/mediacenter/video/player/NextHudTest.java`
