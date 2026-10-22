# Supernova 0.141 implementation and validation ledger

Status: implementation in progress; no acceptance, APK or delivery claimed.

## Continuation baseline

Inspected 22 October 2026: fresh clean clone; no pre-existing local work. Main remains `75d9f012fa4f65869eef5d972ad4725df504bd9c`. Draft/unmerged PR 18 points to `cf257aaa59ad8d59b2ab7252d5beae174f5879c5`; its application, resources and build files equal delivered signed 0.140 source `866f21f11f27dea4846ddd3cac57d98b281eecbb`. Changes after that source are documentation/evidence only. No other open 0.140 fixes or newer implementation heads were found. Created isolated `codex/supernova-0.141-implementation-2026-10-22` at PR 18 tip; no reset, clean, revert, merge, previous-branch edit or maintenance implementation.

Authority: complete handover and scope copied byte-for-byte from planning tip `306e1b35689104c3307cfc06b4d3193ba343be31`; complete FD-002 audit copied from `663646c129ca43882827c2f42dd7e8aa7c936820`. Current user authorises implementation, validation and candidate preparation, overriding those documents' earlier documentation-only scheduling wording. Underlying branding, package and signing identity are retained.

## Requirements → implementation → evidence

Every row requires physical Shield acceptance; automated evidence will be recorded separately. Pending does not mean fixed.

| Requirement | Code / investigation | Required evidence | Status |
|---|---|---|---|
| HUD transport left, slim blue seek, tooltip, remaining + finish right, no duplicate elapsed | PlayerController / experimental XML / PreviewHudTiming | seek/focus/time tests; software renders; physical playback | Pending |
| Accurate conditional chapters; no online fetching | inspect actual decoder metadata boundary | supported metadata proof or documented omission when unavailable | Pending |
| Right translucent popup; exact horizontal Subtitles / Audio / Display / Playback tabs; Subtitles first | PreviewPlaybackMenus | real callbacks, scroll, tab focus, Back, timer and lifecycle tests | Pending |
| Preserve C01–C05, S01–S29, A01–A26 and I01–I07 with approved dispositions | FD-002 inventory; migrate existing handlers | per-ID migration register + regressions | Pending |
| Remove Technical Information / Report a Problem from playback UI; preserve diagnostics | PlayerController / menus / activity | menu and key-path assertions | Pending |
| Audit B01–B14 reconcile against 0.140 | activity/controller/pickers/downloader | finding-by-finding evidence and limits | Pending |
| Hero overlap + smoothness; rapid reversals, repeats, boundaries, focus, art loading | PreviewPages / PreviewFeaturedCard | deterministic interrupted transition tests; hardware frame timing pending | Pending |
| All licence pagination removed; dependency/transitive notices + required texts retained | FoundationAboutWorkspace / legal inventory / legacy entries | complete disclosure reachability; dependency inventory and hashes | Pending |
| Global language icons: reliable region only; neutral generic; unknown/nonlanguage exclusions | PreviewLanguageIcon + all renderers | language/region/unknown/mixed + accessibility tests | Pending |
| Statistics values centred; entire panel matches toolbar opacity | LibraryPageHeader / MenuFoundation | bounds + rendered varying values / scaling | Pending |
| Shared keyboard one-line input; . , / \\ keys; bottom-left Caps Lock and all actions retained | PreviewKeyboard / KeyboardInput / consumers | punctuation, Caps, selection, path and D-pad tests | Pending |
| Put.io connection focus; all controls, Back/re-entry/cancel/error/external return | PutioAccountController / AuthFlow / browser | focus tests; real account flow requires owner | Pending; release-blocking acceptance |
| Edit Library far right, controls left; responsive and reachable | PreviewPages / PreviewToolbar | geometry + remote traversal + scaling | Pending |
| All Library rows centre in content viewport; ends/short/fast/data refresh | PreviewPages / focus recycler | grid/list/custom row centring + repeated input | Pending |
| Movie/TV card → tabs 20–24dp separation; surfaces and scroll retained | PreviewMoviePage (shared movie/TV) | both media types / sizes / text scale / D-pad | Pending |
| Home/Search/Network/Library/Details/playback/backup/scan regressions | existing full test suite | build, lint, full suite + device checklist | Pending |
| Candidate / signing / APK / Drive / notes | existing protected procedures only | exact source, original cert, checksum, full readback | Pending |

## Missing visual evidence

Exact approved 22 October HUD image and IMG_5004/IMG_5007 are absent from the repository. Connected Drive searches for HUD, 0.141 and the filenames returned no matches; Supernova image search returned older Statistics atlas images. The written approved contract is the implementation authority per handover. No substitute is claimed as the original image. Physical Shield screenshots/video and visual acceptance remain owner work.

## Validation boundaries

No physical Shield is attached. Real Put.io account authorisation is unavailable. Successful compilation alone will never close an owner-reported defect. Record test failures and corrections; preserve original 0.140 evidence. Do not merge main or create a public release.
