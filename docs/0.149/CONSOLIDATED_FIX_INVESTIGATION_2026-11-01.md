# Shield continuation — implementation and open evidence gaps

Continues `02ba4b02` on `codex/agreed-fixes-2026-11-01`, PR #30 draft/unmerged. No version, APK, signing, release or merge requested or performed.

## Evidence available

Read `SHIELD_DIAGNOSTICS_AND_TOGGLE_HANDOVER_2026-11-01.md` and `SHIELD_QA_FINDINGS_2026-11-01.md`. Original diagnostics ZIP, IMG_5090.jpeg and Gemini demonstration are **not present in this workspace/repository**. No Android SDK, adb, emulator or physical Shield is available locally. Existing software fixture images are not device evidence. No claim of physical resolution is made.

## Custom Library

Confirmed lifecycle defect: its initial cached render attaches `PreviewPages`, then asynchronous loader completion calls `Workspace.render`, removing its entire hierarchy and attaching the same `PreviewPages` again. `PreviewPages.onDetachedFromWindow` shuts down its worker and cancels enrichment/focus-related callbacks; this is not a safe refresh path. Movies/TV use `setSnapshot` on the attached page. Updated Custom Library to do the same. Initial mount now supplies its snapshot before attachment and enters via the shared `focusFromNavigation` path, which pins adapter position zero while selecting Filters. Movies/TV implementation paths remain unchanged; the shared focus correction is gated to Custom Library.

Additional initial-focus defect traced: `focusFromNavigation` requests position zero, but `restoreFocus` then replaces that pending scroll with `scrollToPosition(1)` when Filters has not been laid out yet. That targets the Filters row below the statistics and can hide row zero. Custom-only focus restoration now retains the pending top offset in that case. Movies/TV retain their existing behaviour. The source path explains the report; physical causal confirmation remains pending. Added first-entry activity visibility and snapshot-refresh/no-detach assertions, without issuing scroll input. **Implemented candidate correction, awaiting Shield confirmation.**

## Language icons

Traced metadata from player audio/subtitle track `.language` through `preview_track_language`, compound TextViews and subtitle popup ImageViews; settings use stored locale values; download results use provider language codes. Regional codes resolve bundled `region_*` resources. Generic `en`/`eng` deliberately resolve to the neutral globe, not an invented country. OpenSubtitles `zh-ca` denotes Cantonese and must remain neutral. Resource shrink keep rules already retain `region_*`; no new APK inspection is claimed.

Confirmed fallback omission: `inline` returned plain text without any icon for null, empty, undetermined, bibliographic aliases and unrecognized language metadata. Removed that suppression so every inline track receives either its explicit regional flag or the neutral globe, and added regression cases for these inputs. This is a confirmed application defect; the raw Shield metadata is still unavailable to establish whether it caused the owner’s particular failure.

Added intrinsic drawable dimensions for predictable ImageView scaling and centralized the provider-code guard in construction. Added native-graphics software tests that render actual ImageViews for regional, ISO3, generic, provider-special and null metadata without manually assigning drawable bounds. Existing tests only drew icons onto a Canvas with explicit bounds or checked packaged resources. **Root cause of missing Shield icons remains unconfirmed**: Android supports fallback bounds for dimensionless drawables, so intrinsic sizing alone cannot be called the fix. Actual Shield language values and affected UI captures remain needed.

## Standard switches

Confirmed legacy preference defect: `tintSwitches` tints an entire CheckBox's multicolour track/thumb layer-list with one colour, destroying their distinction. Clear the tint and bind the existing two-state switch artwork; OFF thumb is now white. Existing SwitchCompat controls retain their white circular thumb and separate checked-state track tint. SettingsWorkspace explicitly emitted redundant On/Off text; removed that visual text while retaining spoken accessibility state. Applied the same text correction to standard switches in PreviewPlaybackMenus without changing Audio/Video/More layout. Disabled switch state remains disabled/dimmed and its row explains availability. The standard row still delegates one click to the persisted preference model; no second click listener was introduced. Approved playback Subtitles geometry and controls remain unchanged.

Added multicolour raster assertions for legacy switches and updated settings visibility/accessibility assertions. Existing persistence, focus and remote-navigation tests remain required. Gemini exact-reference comparison and physical remote verification are unavailable.

## Reception

Confirmed requirement mismatch: DetailsBlueprintPanel always added either “Reviews unavailable” or a Read reviews button, and PreviewMoviePage supplied the reader action. Removed that affordance and unavailable-review text; retained rating availability, genuine supplied scores/awards, dividers and the complete Technical Information composition. IMDb vote count now sits beneath its score rather than alongside it. Existing tests explicitly required review navigation; updated those tests to require its absence.

**Exact original restoration is blocked**, not complete. IMG_5090.jpeg is absent; only IMDb has existing logo artwork, while Rotten Tomatoes/Metacritic are currently text marks. `OmdbSupplement.PRODUCTION` deliberately denies access pending entitlement, its data model contains only IMDb votes, and standard supplied data contains no RT/Metacritic review counts. No fabricated counts, copied screenshot numbers, unauthorized provider access or guessed replacement design introduced. Owner reference and an approved genuine metadata source for missing provider counts are needed to finish exact parity.

## Diagnostics disposition

Only the handover summary was analysed, **not the original ZIP**. Counts cannot reveal distinct incident causes.

| Class | Evidence and disposition |
| --- | --- |
| Focus anomalies (864 events) | Cannot distinguish expected transitions from focus loss without session/operation context. Custom lifecycle correction may remove one source of churn; not claimed as resolution of these events. |
| Scan failures (166 + 8 batch events) | Need unique source/operation IDs and exception chains; no confirmed scanner cause from counts. |
| Metadata network/package (671/672), semantic (12) | May describe cascading failures in one operation. Need endpoints/statuses/exception causes; neither external outage nor application defect can be inferred from totals. |
| Playback error (1), uncaught exception (1) | Need actual stacks and player cause chain; no crash root cause established. |
| Suspected unclean exits (101) | Not confirmed crashes. Correlate process/session and Android exit evidence first. Partial archive coverage and clean-shutdown counts do not establish a crash count. |

No speculative changes to scanning, network, playback or exception handling.

## Validation

Local tooling suite: 147 tests passed. `git diff --check` passed. New/updated Android tests and relevant existing regressions are selected by `agreed-fixes-validation.yml` (tests/lint/compilation only, no APK). CI result recorded below when available. Software fixture output must be labelled Robolectric software evidence, never Shield/emulator screenshots.

Remaining mandatory acceptance: first entry/re-entry/relaunch/refresh custom statistics, actual metadata-to-icon rendering, switch off/on/focus/disabled with one remote-OK change and restart persistence, exact original Reception and counts, raw diagnostic investigation, physical Shield confirmation. No unrelated approved feature is declared device-verified.

The existing PR-triggered conformance workflow included `assembleNoamazonRelease`. Its two automatically triggered continuation runs (41351147911 and 41351186104) were cancelled before their Gradle step. Replaced assembly with Java compilation so subsequent PR validation respects the owner's no-new-APK constraint. Tests, lint and compile remain enabled. A superseded manual validation run was also cancelled.
