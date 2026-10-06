# SUPERNOVA Preview 4.1.2 — return handover

## Continuation and source authority

Recovered the existing `codex/preview-412` working tree at `a8af6f1`; it was clean and contained committed, unfinished 4.1.2 implementation. Continued it as local commit `7e2eefb`. No reset, revert, old-handover merge or deferred 4.2 additions.

The full authoritative 4.1.2 handover was read, all six supplied visual/QA images inspected and all twelve manifest checksums verified. Search is explicitly authorised by section K. The original 4.1.1 baseline was retained except for listed corrections.

The continuing source tree was published as `1e590e0249a63063514d8be9b30aeaff4a4b12e9`, tree `f1d67b3ff9fa2e7075bc4abac1000cffb4ed6d1c`. This preserves the recovered and continuation changes. Source is on `codex/apk-build-fixes`; `codex/apk-build-412` also retains the initial publication.

## Delivery identity

Version name: `6.4.63-mark.4.1.2-preview`. Version code: `6040078`.
Package: `org.courville.nova.markpreview`.
Required certificate SHA-256: `89ac087ed6f989c90482d4a999f80511fe6ceee26ef1b9c37a142a9f00d39a5a`.
Intended installation: update over the existing Preview 4.1.1; preserve application data.

Final build source: `53cf382800ca0fbc3261456aaebdf4b99611241d`, tree `034a8d4203be142c16ad3ebe527fe30794434aba`. Local continuation commit `87dfd84` has the identical tree.

Delivered APK: `NOVA_Preview_4.1.2.apk`, 82,411,264 bytes.
APK SHA-256: `ac8755892ba52530acd2602084b56fb002b41544d4fa59460bc1af206bcb2a9c`.
Build: https://github.com/heretoecode/aos-Video/actions/runs/35506060249 — SUCCESS.

## Root causes established from the runtime paths

- HUD seeking recovery required `mControlBar.hasFocus()`, even when seeking had lost that focus. Recovery now responds to Up/Down while the HUD is visible and the nested TV menu is absent.
- A later UI-mode switch re-exposed the hidden backward/forward buttons. Both initial setup and subsequent mode changes now keep them hidden in Preview. Remote seeking remains.
- Settings disabled its entrance animation before a later unconditional call restored the slide. Preview now bypasses that later slide call.
- The classic browser's title/grid root and presenters were retained underneath changed menus. Preview now attaches the listing dock to its own source/path/list/context composition, retaining protocol engines and detached command handlers. Legacy title focus helpers do not control this root.
- The Home root is wrapped by the startup composition; the scanner/clock overlay previously recognised only a direct TopNavigation root. Recursive navigation discovery reconnects the existing shared status container.
- Logo sizing used transparent PNG canvas dimensions. Decode-worker visible-alpha cropping preserves aspect and enlarges sparse artwork inside the appropriate surface bounds.
- Search parent results depended on an already-populated memory snapshot. Parent series now query the local show index, with existing diacritic normalisation, independently of whether Home has populated that cache.
- Native player error handling calls `stopPlayback()` before the service's `onError()`. The latest live position can disappear before the normal error-save path. The additional pre-stop checkpoint samples the existing service state first; if native state is unavailable it retains the periodic checkpoint.

A/V desynchronisation and spontaneous native exits have NOT been reproduced or assigned a proven decoder/network root cause. No A/V delay hack or engine replacement was added.

## Implemented corrections

- Shared boxless focus/underline, icon emphasis, artwork-only dimming with readable captions, revised media/utility navigation groups and Settings cog.
- Exact approved blue/black utility background on utility surfaces; translucent navigation. No expensive live per-card blur.
- Home first-frame gate observes visible card and backdrop loading completion, with a short fade; narrower Featured action, focusable carousel strip and retained local candidates/transition.
- Compact Home editor with unchanged reorder/visibility/membership handlers; clearing Watch Next remains separate and confirmed.
- Indexed Movies/TV count, total/local/network storage hierarchy and subtle vertical/horizontal list separators. Mixed-source TV shows are counted in each applicable source and labelled accordingly; unknown indexed sizes are disclosed.
- Streaming Service filter lists selected providers only. It filters local titles with fresh, successful availability learned through Details, scoped to country and title type. Unknown/stale availability is not a match; it does not fetch a remote catalogue or perform a full-library availability sweep.
- Narrower Details synopsis, denser rectangular cast cards, separated provider row, grouped non-selectable action headers, distinct removal/delete/Add to Row/synopsis icons and title-style labels.
- Centred Information dialog, dimmed Details behind it, no X, Back focus restoration, compact technical badges and existing legitimate metadata. No invented ratings or reviews.
- Compact Search keyboard and field beside results; larger parent-series result above episode rows; cold-entry show lookup and explicit keyboard/field/results directions.
- Primary browser source/path/list/context layout, compact filename rows/separators and focus return to source rail. Source/protocol actions remain backed by existing listing handlers; unavailable protocol details are not fabricated.
- Responsive nine-category Settings rail with real settings list and focused-setting help/options; immediate first configurable setting on entry, no slide-in. Existing keys/values/actions retained. Provider selector has selected and alphabetical other sections, genuine cached catalogue icons and immediate in-place ticks. Groups reorder when reopened, avoiding flashing/focus movement during selection.
- Ten-second periodic playback checkpoint; service completion-after-error guard, lifecycle/seek diagnostics, visible logo footprint, concise tracks and sole centre Play/Pause.

## Netflix investigation

The current handoff enriches the real TMDb/JustWatch offer, resolves known redirectors, and dispatches its returned HTTPS URL to the provider package. Android accepting ACTION_VIEW proves only that an activity launched; it cannot confirm which screen the provider app displayed. No validated Netflix-specific alternate URI contract or physical title URL/log was available. Host/path-presence diagnostics were added without logging URL tokens. Netflix title-page navigation remains unresolved; the known-good Prime route is preserved. No guessed provider ID or unsupported deep link was introduced.

## Protected behaviour

Automatic WebDAV trigger/import; backup/export/migration; provider persistence/no flashing; Prime title-page example; diacritic matching; first-Back HUD dismissal/second-Back exit; anchored playback menus; Continue Watching/final episode handling; Up Next/autoplay; binge/recap; Watch Next membership and non-destructive hiding. Physical preservation is not asserted solely from source review.

## Validation and build history

- Recovery: clean Git status, branch/history/diff inspected; exact asset and handover manifest verified.
- Initial build on the new branch, run `35502390870`, stopped before compilation because its branch-scoped signing cache was absent. No alternate signing key was generated.
- The same source was fast-forwarded to the established build branch. Run `35502487976` recovered and passed the pinned certificate check and completed successfully.
- Initial run `35502487976`: signed debug/release builds, all 37 targeted tests, full-process Preview cold/restart/warm startup, Settings/Movies/TV/browser/Search routes, release install-over/restart and pinned signature verification passed.
- Visual inspection identified additional issues despite successful route checks: automatic system keyboard obscured Search, the phone emulator status bar overlapped browser navigation, Settings help could retain an old item after category updates, and the Featured Resume label was clipped. Corrected those and removed the duplicate utility background layer. HUD XML now also hides the removed transport controls before controller setup.
- The final error checkpoint retains a prior positive position if the failed native player returns zero or throws; normal service persistence remains unchanged.
- Four focused 4.1.2 checks cover sparse/empty logo bounds, country/type-scoped fresh provider evidence and empty library summary structure, alongside retained targeted regression checks. Search smoke also scans its captured accessibility hierarchy for IME nodes. This alone is not sufficient: Android can omit a separate IME window from that dump, so final screenshots are the visual acceptance evidence.
- Final run `35506060249`: signed development and optimised release builds PASS; 37 targeted tests, zero failures/skips; Preview-enabled cold/restart/warm startup and changed-route checks PASS; release install-over/restart PASS; required signing certificate and APK signature PASS. Downloaded APK checksum matches the CI record.
- Final screenshots visually confirm unobscured compact Search, Settings first-item focus with matching context, browser navigation without system-bar overlap, complete Featured Resume text and only one central HUD transport control. Empty runtime screens and synthetic fixtures are distinguished in the screenshot evidence notes. Populated physical acceptance remains user QA.
- The intermediate run `35505928278` was superseded when the final measured Resume-width correction was added. It is not the delivery artifact.

## NOT TESTED — USER QA REQUIRED

Physical 4.1.1 install-over, populated Shield startup and logo coverage, remote/controller focus, actual Local/USB/WebDAV/SMB browsing, playback sync/abnormal exits/resume, audio/subtitle changes, provider title pages, scan/import, backup restore and Shield scrolling performance. No physical device or credentials were available.

Exact visible Codex usage/allowance: unavailable. No estimate supplied.
