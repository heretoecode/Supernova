# 24 September 2026 implementation pass

Authority: Supernova_Codex_Handover_2026-09-24_NEXT.zip only. All supplied text and four images inspected; manifest verified. Its optional broader SOURCE_ARCHIVE ZIP is absent and QA_EVIDENCE is empty; written QA_STATUS is supplied. No historical ZIP supplies requirements.

Baseline: heretoecode/Supernova, main, 77b2617ad1e48b7a7a85ba28463de0961af5ba4d; clean checkout; build.gradle declares Preview 4.1.5 (6040081). Changes since recorded 4.1.5 release are workflow/documentation changes.

## Source map

| Requirement | Existing implementation and integration point |
| --- | --- |
| Shared focus, typography, iconography | PreviewContentFocus, PreviewFocusGlow, PreviewIcon, PreviewCardPresenter; native preference and transport presentation |
| Context menus and placement | PreviewDialog, PreviewPlaybackMenus; preserve action callbacks |
| Navigation | TopNavigation, MainFragment, PreviewFocusRecycler |
| Home flashing/performance | PreviewPages render/rail binding; PreviewCardPresenter image requests; PreviewLibraryLoader emissions |
| Library header, Sort/List metadata | PreviewPages, PreviewLibraryColumns, PreviewLibrarySummary, PreviewMetadata |
| Search and naming keyboard | PreviewSearch and PreviewTextInput |
| Multi-genres and dynamic rows | PreviewHomeRows, PreviewPages filters |
| Movie/TV Details, episodes, related, Extras, facts | PreviewMoviePage, existing fragment actions, PreviewTrailer, PreviewPeople, StreamingRepository |
| Settings hierarchy | PreviewSettings native preference adapter/sidebar |
| Network three panels | PreviewPages source panels and existing NetworkRootActivity/source handlers |
| Playback HUD | PlayerController, player_controller_experimental.xml; existing playback actions retained |
| Diagnostic/soak infrastructure | Diagnostics, DiagnosticExportActivity, existing player/library/lifecycle event hooks |

## Delivery blockers (not implementation blockers)

- Local Gradle bootstrap failed fetching gradle-9.5.0-all.zip: network unreachable. No Android SDK/emulator was found locally.
- Canonical main CI run 35939551632 failed at Require the existing personal signing identity: Existing signing key unavailable; refusing to generate a different identity.
- Preserve expected certificate SHA-256 89ac087ed6f989c90482d4a999f80511fe6ceee26ef1b9c37a142a9f00d39a5a. Do not create/substitute a keystore. Signed APK delivery remains pending.
- Physical Multiple Versions/Resume, Up Next, Backup/Restore and extended soak testing remain parked. No historical WebDAV EOF fix is claimed.

## Source candidate and implementation status

Preview 4.1.6 source candidate: versionCode 6040082. **Not an APK release, not compilation-verified, and not physically tested.** The handover has been worked through; source changes and remaining gaps are distinguished below. No historical branch or older handover has been merged.

| Scope | Source change | Verification / remaining limitation |
| --- | --- | --- |
| Global focus | Shared boundary-only thin outline/outward halo; neutral contents; full-row focus; card scale | Java syntax checked; clipping, overscan and visual balance need runtime/Shield review |
| Top navigation | Locked groups/order, uniform 19sp sans-serif-light text, no divider | Source inspected; 1080p fit not rendered locally |
| Global iconography | Shared monochrome functional/genre drawables; distinct Grid, Sort, Columns, source/statistics controls | No four-square generic fallback; visual audit still needs rendered pages |
| Context menus | Shared translucent surface, local row focus, separate ticks; adjacent placement with gap and safe bounds | Geometry tested over 10,000 seeded anchor cases; actual Android window placement unverified |
| Home flashing | Diff-based page/rail updates; one artwork request per card; unchanged requests retained; generation-safe callbacks; no refresh delay workaround | High-confidence source causes addressed, not a confirmed physical-device fix |
| Home navigation/performance | Featured focus retained, explicit vertical route, constrained synopsis, recycled generous rails, emission/render/rebind/cache/request timing | No new arbitrary Home row cap; real library frame-time/flash correlation awaits Shield logs |
| Movies/TV | Compact semantic statistics, toolbar divider, existing card metadata retained | Source inspected; no invented quality badges |
| Sort | Menu choices generated from available table metadata; Audio and selected direction reflected | New Robolectric regression added but cannot run locally |
| List metadata | Bounded worker enrichment when entering/scrolling List, persisted through native metadata save; no focus-triggered retrieval; series summaries refreshed | Opportunistic batches, not a launch rescan; full legacy-library population timing unmeasured |
| Search/naming | One QWERTY + permanent numbers keyboard; live debounce retained; whole-code-point delete; focus and search latency instrumentation | Keyboard regression added; query/runtime checks pending |
| Genres/custom rows | Shared semantic checklist, multi-select OR rule, Movies/TV/both validation, sort/optional maximum, persisted dynamic rows | Manual rows/Watch Next retained; dynamic rows excluded from manual Add-to-Row choices |
| Network & Files | Three panels with real source/library/saved-location/add-source routes and workspace-to-category return | No invented recent-source history or unsupported cloud integration |
| Settings | Three panels, expandable category children, existing Preference handlers/integrations, explanatory pane | Runtime focus and preference-change regression checks pending |
| Movie/TV Details | One-viewport hero; Play, one provider, More; left-aligned section tabs; continuous vertical navigation; tab selection retained across enrichment | Native playback/edit/delete/version handlers retained; runtime layout not verified |
| Episodes | Recycled horizontal season rows, logical episode grouping, runtime/progress, journey-selected initial card, per-season focus memory | No synopsis or fabricated episode facts; physical alternate-version/Resume QA remains parked |
| More Like This | Local-priority landscape grid, configured-provider availability filter, focus-only plain availability mark, internal Details route for remote titles | Provider availability is not proof of subscription entitlement; live service/device launch checks pending |
| Extras | Real supported YouTube videos, accurate supplied type, trailer ordering, landscape grid, empty-tab hiding and focus restoration | **Partial:** existing supported embedded playback retained. No authorised native playable media URL or reliable duration is supplied by current integration; native-player playback/duration badges not fabricated |
| Information panels | Full Cast then Crew, Key/Reception/Technical columns, native file facts, known HDR, TMDb budget/rating, attribution | **Partial:** no validated awards, quotations or filming-location feed. Crew retains existing director/writer metadata; missing portraits/fields not invented |
| Playback HUD | Exactly five symmetrical controls, no transport skip buttons/status sublines, neutral contents, focus-only scrub timestamp | XML structure checked; upper title/logo/clock routes preserved, native playback smoke test unavailable |
| Diagnostics | Opt-in Normal/QA, 30s heartbeat, bounded 3-minute flight ring/freeze, session/operation IDs, issue marker/reference, digest, dropped/write-error/coverage reporting | **Partial coverage:** native/direct transports are not fully observable. Reports explicitly say PARTIAL; no app-quality score or claimed WebDAV cure |
| Stability follow-through | Existing startup containment, resume/variant policy, Up Next policy, source/security/backup code kept | Source diff inspected, but unchanged code is not proof of runtime non-regression |

### Diagnostic limits and privacy

The in-memory flight recorder is bounded to 252 KiB / 180 seconds. Event files retain the existing 256 KiB rotation limits. Normal mode retains detail in memory; QA mode also writes detail. Anomalies freeze the recent ring and capture a short post-event tail. Manual markers provide a screenshot/video cross-reference rather than silently taking screenshots. Existing redaction excludes raw media paths, credentials, headers and exception messages. Library, artwork, search, metadata networking and physical playback selection have additional events; existing player/checkpoint/backend hooks remain.

Native process death can prevent final writes; an unclean exit is not proof of a crash. No logger can reconstruct unavailable native network ranges/retries/EOF evidence. Every report keeps that limitation explicit.

## Validation actually performed

| Check | Result |
| --- | --- |
| JDK compilation of production MenuPlacement/DiagnosticFlightRecorder plus independent harness | PASS |
| `PreviewNextChecks` | PASS: 30,010 assertions; geometry, no overlap/gap, screen bounds, fallback, UTF-8 byte/time bounds, eviction and invalid input |
| `java tools/ParseJava.java src` | PASS: 710 Java files parsed, zero syntax errors; **not Android type checking** |
| `python tools/check_preview_next.py` | PASS: 430 XML files parsed; unique HUD IDs, five groups/order, hidden legacy controls, internal activity, signing gate |
| `git diff --check` | PASS |
| Source diff review | Performed: fixed async tab resets, stale artwork callbacks, rejected enrichment scheduling, remote-only file actions, episode/series rating leakage, series metadata refresh, and focus routes |
| Android Java compilation + Robolectric | NOT RUN: wrapper fails before tasks start while downloading Gradle 9.5.0 (`Network is unreachable`) |
| APK build/sign/verification | NOT RUN / NOT PRODUCED: build toolchain unavailable and original signing identity unresolved |
| Startup, major navigation, native playback, menu open/close, update-install smoke tests | NOT RUN: no APK, Android SDK/adb/emulator or physical device here |
| Shield visual/performance/soak | NOT RUN; no physical result claimed |

Attempted command:

```sh
./gradlew compileNoamazonDebugJavaWithJavac testNoamazonDebugUnitTest -PmarkPreview -Puniversal --no-daemon
```

The CI workflow now runs source compilation and targeted next/Details/startup/diagnostic tests **before** the existing signing gate. This does not bypass signing or generate a substitute key. The workflow change has not been executed remotely. Added `PreviewNextTest` covers keyboard editing, Audio sort persistence and five-control HUD structure. Existing Details expectations were updated for explicitly superseded tabs/full cast layout, retaining native playback delegation and episode-rating checks.

### Re-run offline checks

```sh
java tools/ParseJava.java src
python tools/check_preview_next.py
# Use a scratch output directory outside the source tree.
java com.sun.tools.javac.Main -d /tmp/supernova-check/classes tools/PreviewNextChecks.java src/main/java/com/archos/mediacenter/video/leanback/PreviewMenuPlacement.java src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticFlightRecorder.java
java -ea -cp /tmp/supernova-check/classes PreviewNextChecks
```

## Metadata-source review

The enrichment gateway uses the app's existing TMDb credentials/client, bounded responses and documented movie/TV detail/video/recommendation endpoints. Existing country/provider selection restricts remote results; no IMDb/OMDb scraping, new API key or unsupported awards/quotation feed was introduced. Missing values remain absent. About now states TMDb non-endorsement and JustWatch attribution. Source references reviewed:

- https://developer.themoviedb.org/docs/faq
- https://developer.themoviedb.org/reference/movie-details
- https://developer.themoviedb.org/reference/movie-videos
- https://developer.themoviedb.org/reference/movie-watch-providers

These references do not establish a commercial redistribution licence or guarantee catalogue accuracy. No such claim is made.

## Required next validation / delivery

1. Run Android compilation and all targeted tests with the pinned sibling repositories/toolchain. Resolve any resulting type/resource/runtime failures.
2. Smoke new-UI startup and Home/Movies/TV/Search/Details/Settings/Network; D-pad across tabs/rows/menus; playback and Back to the exact Extras card; retained library state.
3. Restore access to the **existing** signing identity; verify the certificate above. No replacement identity.
4. Build and inspect the signed Preview 4.1.6 APK, test update-install continuity, then deliver for Shield QA.
5. Measure Home flashing/frame-time/cache behaviour on the real library. Parked tests remain Multiple Versions/Resume, Up Next, Backup/Restore and extended soak.

The source candidate remains local; no unverified change was pushed to remote main. Unrelated absent symlinks in flavour logging/release-note paths appeared during workspace recovery and are excluded from this implementation change set.
