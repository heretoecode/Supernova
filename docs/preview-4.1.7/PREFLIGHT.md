# 26 September pre-implementation review

Repository: heretoecode/Supernova. New candidate: codex/preview-4.1.7.
Delivered and physically tested baseline: 094d8e80938501222b4b16ed715c03937abde68f
on codex/preview-4.1.6. The supplied Shield build-device.json independently
identifies this commit, package org.courville.nova.markpreview, version
6.4.63-mark.4.1.6-preview, code 6040082, NVIDIA SHIELD Android TV / Android 11.
Main remains 77b2617ad1e48b7a7a85ba28463de0961af5ba4d.

## Workspace preservation

The original Supernova checkout remains on local commit
913babb1f76e4770d43b626e53d3c28813ce6f71. Its unstaged smoke-script changes
hash to 7079479a0a39b45c4850302a8625dc1e7cefc78d, exactly the delivered
remote file. Thirteen pre-existing missing tracked symlinks are unchanged:
two logback asset links and eleven translated internal release-note links.
There are no staged or untracked repository files. APKs, diagnostics and
handover extracts are outside the repository. Original implementation commit
01ba81755a94d2fa0fabeeae2c5b9a9121d11bf6 and its Git bundle remain present.

A separate worktree was created from the exact delivered remote commit.
No reset, merge, application source change, signing change or main update
was performed during preflight.

## Authoritative package

Uploaded filename: Supernova_Codex_Handover_2026-09-26_FINAL(1).zip
SHA-256: 0dcb746dd0408a4358d448e791ba8a92194c876a90ecf6b14089cc6bb30234df

Outer ZIP integrity and all 24 manifest entries passed. Every supplied
specification/documentation file was read: READ_FIRST, CODEX_PROMPT, root
README, branding README, IMPLEMENTATION_SPEC, VISUAL_AUTHORITY,
QA_AND_ACCEPTANCE, DIAGNOSTICS_4.1.6_FINDINGS, PUTIO_ARCHITECTURE,
SCOPE_AND_DEFERRED, HANDOVER_REVIEW_RECORD, QA README and visual README.
The checksum manifest was also read and verified.

All ten images were inspected: approved launcher master and 320x180 banner;
four physical QA stills (4570, 4571, 4575, 4576); Dune Details, physical
Movies baseline, Playback HUD and Movies/TV header references.
The NORMATIVE directory is empty. The source archive mentioned by
VISUAL_AUTHORITY is absent; the visual README explicitly documents its
deliberate omission. Raw walkthrough videos are deliberately omitted and
their findings consolidated into the written specification. Do not invent
missing images or import superseded material to fill these gaps.

The nested diagnostic ZIP passes integrity. All fourteen files were read
or parsed, including all nine JSONL streams. Event-stream counts agree with
the handover: 306 artwork requests, 67 failures, 248 ready; 916 focus
events; two scan requests and two suspected unclean exits. Playback logs
contain 19 new sessions and 16 prepared events, despite summary.txt saying
zero retained playback sessions. This is an export-summary defect to cover
in the diagnostics workstream. Flight snapshots overlap the source streams
and must not be counted as independent events. Manual report markers in
this supplied export are test/noise. Unclean exits are not confirmed crashes.

## Initial code ownership map

| Workstream | Existing implementation to trace/extend |
| --- | --- |
| Shared navigation/focus | TopNavigation, PreviewFocusRecycler, PreviewContentFocus, PreviewFocusGlow, PreviewFocusUnderline, PreviewDialog |
| Home/library/list/filter | PreviewPages, PreviewHomeRows, PreviewLibraryLoader, PreviewLibraryColumns, PreviewGenres, PreviewCardPresenter |
| Enrichment/artwork | PreviewMetadata, PreviewDiscovery, OfficialTitleArtwork, PreviewBackdrop, PreviewDetailsData; pinned MediaLib index/backend |
| Details/unmatched/More/matching | PreviewMoviePage, PreviewEpisodeRow, PreviewLandscapeCard, PreviewPeople, existing matching and action handlers |
| Playback | PlayerController, PlayerActivity, PreviewPlaybackMenus, PreviewPlaybackLoading, PreviewPlaybackInfo, PreviewTrackLabel |
| Search/keyboard | PreviewSearch, PreviewKeyboard, PreviewTextInput |
| Scan/network/browser | PreviewLibraryScan, PreviewNetworkScanning, PreviewScanReceiver, PreviewAutoScanPolicy, PreviewSourceManagement, PreviewBrowserSurface; MediaLib/FileCore backend patches |
| put.io | New provider/API/association layer integrated with existing index and WebDAV resolver; no independent duplicate library |
| Settings foundation | PreviewSettings, PreviewPreferenceDialogs; retain existing preference semantics |
| Diagnostics | Diagnostics, DiagnosticFlightRecorder, DiagnosticExportActivity |
| Branding/coexistence | Android manifests/resources, build.gradle, prepare-preview.py and assembled merged manifest |

This is an ownership map, not a claim that implementation tracing or any
new feature is complete. REQUIREMENTS.md tracks all 115 substantive source
paragraphs, including acceptance and deferred boundaries.

## Constraints and dependencies

- Preserve package org.courville.nova.markpreview and established signing
  certificate 89ac087ed6f989c90482d4a999f80511fe6ceee26ef1b9c37a142a9f00d39a5a.
- Use NOVA_SIGNING_KEY_BASE64; never create another signing identity.
- Preserve proven playback/WebDAV and all library/user state.
- put.io production device linking needs a registered OAuth client. No
  registration/client configuration is supplied in this handover. Determine
  legitimate existing configuration before requesting the public client ID;
  do not borrow another app's client or request tokens in chat.
- Missing caps for Recently Added/Continue Watching are deliberate; select
  bounded presentation defaults without discarding underlying state.
- Deferred: person discovery, trick-play generation, put.io transfers,
  put.io playback-position sync, replacing primary WebDAV playback, broad
  Settings-content redesign, unsupported NFS and unpromoted older features.
- Physical Shield checks remain AWAITING PHYSICAL QA, never inferred from
  compilation/emulator success.

## Validation plan

Implement shared systems first and map each change to the register. Add
targeted coverage for focus, scan lifecycle, state-preserving reconciliation,
put.io incomplete-sync safety and diagnostic redaction/retention. Then run
compilation, existing/new tests, signed build and exact APK certificate
verification, emulator smoke checks, and three distinct completeness,
visual/behavioural, and regression/scope conformance reviews. No completion
claim or merge to main before these gates and the explicit limitations report.
