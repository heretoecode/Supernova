# 0.141 Playback HUD audit reconciliation

Source: full FD-002 audit at `663646c129ca43882827c2f42dd7e8aa7c936820`, reconciled with actual delivered 0.140 `866f21f1` / branch tip `cf257aaa`. This register records implementation intent; test results and device acceptance are separate. No legacy controls are imported merely to fill a tab. The TV dialog route also covers conditional split/stereo layouts; these retain the native playback callbacks and still require suitable hardware QA.

## Complete control/settings migration

| Audit ID | 0.141 disposition |
|---|---|
| C01 | Far-left Play/Pause plus existing hardware/media transport. |
| C02 | Slim seek line, focused/active target tooltip; remaining/finish right. Existing accelerated key seek and paused-state semantics retained. Hidden Left/Right now reaches seeking with the initiating input. |
| C03 | Replaced primary Subtitles opener with first, default Subtitles tab in Settings. |
| C04 | Replaced primary Audio opener with Audio tab. Empty audio explicitly reports no tracks. |
| C05 | More replaced with Settings; exact horizontal Subtitles, Audio, Display, Playback tabs. |
| S01 | Subtitles tracks/None and Other languages; native callbacks and per-file persistence retained. |
| S02 | Subtitles Delay: signed native timing picker. |
| S03 | Native subtitle speed adjustment within Delay, distinct from playback speed. |
| S04 | Subtitle Appearance size; raw step100 gives one effective integer size per tap. |
| S05 | Subtitle Appearance sixteen swatches; cursor initialized to actual saved colour. |
| S06 | Subtitle Appearance position; step100 gives one effective position per tap. |
| S07 | Subtitle Appearance outline toggle retained. |
| S08 | Subtitle Appearance background toggle retained. |
| S09 | Subtitle Appearance opacity; step100 gives one effective opacity per tap. |
| S10 | Audio tracks, including language icon; native selection/persistence retained. |
| S11 | Audio Boost; existing passthrough gating retained. |
| S12 | Audio Night Mode; existing passthrough gating retained. |
| S13 | Audio Platform spatialization; existing device/API capability checks retained. |
| S14 | Audio delay, signed bounds and passthrough restrictions retained. |
| S15 | Audio delay global persistence retained with existing zero sentinel (see B04). |
| S16 | Playback Speed in Playback tab; existing audio/backend/passthrough predicate retained. |
| S17 | Playback speed global persistence retained with existing1.0 sentinel (B04). |
| S18 | Playback → Play mode, all five native modes retained. |
| S19 | Playback → Play mode session segment toggle; provider safety policy retained. |
| S20 | Display → Format; all actual supported ratios, unavailable Optimized clearly disabled. |
| S21 | Display conditional 3D using native renderer condition and three existing modes. |
| S22 | Playback conditional Notifications on non-Leanback only. |
| S23 | Reading language inside Subtitles, same global key/catalogue and hide-default gating. |
| S24 | Download languages inside Subtitles; same catalogue/global set, at least one selection retained. |
| S25 | Prepare Subtitle Searches inside Subtitles; same global false default. |
| S26 | Prepare Current Season inside Subtitles; same global true default. |
| S27 | Subtitles by Default inside Subtitles; same inverse hide-default key. Reading-language enabled state updates immediately. Forced-track selection policy unchanged. |
| S28 | Conditional touch window brightness remains input-specific; no fabricated Display slider. |
| S29 | Conditional touch/system volume retained; Shield hardware volume still routes to framework. |

## Complete action/indicator migration

| Audit IDs | 0.141 disposition |
|---|---|
| A01–A04 | Subtitles Download workflow: explicit result review, Download and review Back retained. Successful download returns explicit saved path for normal apply/re-enumeration. |
| A05 | Existing operation Cancel; in-flight noncancelable file mutation semantics retained. |
| A06–A08 | Choose Subtitles, associated/unassociated listing, explicit Use retained with existing scheme/writability conditions. |
| A09–A10 | Associate with This Title review and confirmed association retained. |
| A11 | Subtitle File Information remains available in local-file workflow. |
| A12–A14 | Delete Subtitle File review, destructive Delete and default Cancel retained. |
| A15 | Full Settings activity escape replaced by inline S23–S27 in playback panel. |
| A16 | Technical Information removed from TV playback UI, including native card/options visibility and keyboard shortcut; underlying information/diagnostic implementation retained for other contexts. |
| A17–A18 | Report a Problem and its category chooser removed from playback UI. Underlying diagnostic/report infrastructure retained. |
| A19–A20 | Close/Copy retained in applicable subtitle/notice workflows and diagnostic infrastructure outside removed playback entry. |
| A21 | Display → Format conditional PiP now checks actual OS feature and returns before format selection. |
| A22 | Contextual segment Skip remains outside Settings; current safe provider interval rules retained. |
| A23 | Up Next Play Now remains outside Settings. |
| A24–A25 | Existing network-resume Yes/No flow retained. |
| A26 | Focused Up Next Back cancellation retained. |
| I01 | Existing cached title/logo and episode context retained. |
| I02 | Target position is tooltip only while seek-focused/active; duplicate elapsed is absent. |
| I03 | Total/contextual end wording replaced with remaining HH:MM:SS and generic Ends at, speed/preview aware. |
| I04 | Top clock retained. |
| I05 | Active technical information removed from playback presentation only. |
| I06 | Up Next context/countdown retained. |
| I07 | Checks, unavailable states, operation/errors/buffering and input-specific feedback retained. |

## All fourteen findings

| Finding | Reconciliation / engineering outcome | Acceptance boundary |
|---|---|---|
| B01 | Full Settings escape removed; five global subtitle fields embedded. | Test page, preference persistence and D-pad return; physical provider/preparation behaviour pending. |
| B02 | Format entries outside actual SurfaceController maximum disabled; tick uses applied format. | Native menu control flow plus software regression; actual video framing pending Shield. |
| B03 | Appearance size/position/opacity now step100 in existing scaled picker, correcting short-tap quantization. | Integer limits/held-input and visual renderer checks required. |
| B04 | Intentional inherited sentinel representation retained: delay0 / speed1.0 reopen unchecked; unchecking changes next-start preset without resetting session. No new global Boolean or migration introduced. | Documented limitation; neutral/nonneutral reopen and real backend QA. |
| B05 | PiP feature checked; explicit return prevents format fall-through. | Actual supported PiP transition remains hardware/platform QA. |
| B06 | Successful download returns saved external path through existing explicit chooser apply route. | Provider + writable/read-only sidecar/cache + actual rendered-track acceptance pending. |
| B07 | Colour cursor initialized from manager colour. | Reopen/OK and all swatches need device rendering. |
| B08 | Nested card result callback flushes on Back, tab change and programmatic close; renderer values saved. | Regression callback checks; actual recreation/stop timing pending. |
| B09 | Audio tab explicitly shows No audio tracks, with no misleading enabled track action. | Silent-video fixture/Shield QA pending. |
| B10 | Remaining/finish/seek-tooltip contract implemented; hidden Left/Right no longer needs a second tap. | Real seek/key-up/unknown-duration/paused playback QA pending. |
| B11 | Optimized and PiP capability mismatches corrected. Existing speed/codec gates retained; visible track still does not promise decoder support. | Hardware output/backend acceptance pending. |
| B12 | Technical playback panel removed by approved scope; underlying data/diagnostic infrastructure retained. No unsupported subtitle info panel invented. | No playback entry remains; outside-playback diagnostic regression required. |
| B13 | Explicit confirmation before replacing existing subtitle destinations, including remote cache, after safe staging. | Disposable-file/provider cancel/error/write tests and physical storage QA required. |
| B14 | Right touch double-tap now checks forward capability. | Asymmetric-capability software regression; physical pointer input pending. |

## Chapters

The baseline Java bridge had no chapter enumeration. The pinned FFmpeg parser has embedded chapter tables, while AVOS also has a debug-only synthetic generator. A new isolated native patch exports only parser-marked embedded starts/titles through the existing metadata parcel (count 9003; base 40000; stride 2). It converts container origin to the playback-relative millisecond timeline and resets source provenance when chapters are freed. Synthetic/debug chapters never receive the embedded provenance flag. No network source is called.

Java rejects unknown duration, missing keys, more than256 entries, negative/EOF/out-of-order/duplicate starts, and invalid metadata types; a bad table yields no markers. Valid starts are read-only marker lines and the current chapter title augments the seek tooltip. No chapter selector/thumbnail/online fetching is invented. Unit/native build/actual fixture evidence must be recorded separately; physical Shield acceptance remains open.

## Physical acceptance

Use all 22 original audit Q cases with updated visible controls. Q02 uses Settings/tabs rather than four legacy buttons; Q19 verifies removed playback entries and tests diagnostics elsewhere; Q20 tests inline preferences rather than activity escape. All unchanged history, resume, segment, next, storage, codec, backend and hardware conditions remain relevant. No Shield pass is claimed from a software test.
