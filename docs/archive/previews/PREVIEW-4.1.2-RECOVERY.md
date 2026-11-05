# Preview 4.1.2 recovery — 20 September 2026

Continuation workspace: `/workspace/scratch/ef53017d9e0d/nova-412`.
Branch: `codex/preview-412`. Recovered HEAD: `a8af6f17180fe7bf93e06935c31261d2be81bcb7`.
Parent: `5ebf95dd17d2d9ad478fc4eb0ec9be9db623911a` (4.1.1 delivery).
No staged, unstaged, untracked or ignored build outputs were present. All recovered 4.1.2 source changes were committed locally; remote build branch still pointed to 4.1.1. No 4.1.2 build had run.

Read every supplied text/document, inspected all six images and verified all twelve manifest hashes. Older attached handover is not merged. Search is explicitly in scope under section K of the authoritative 4.1.2 handover.

## COMPLETED (source implementation; acceptance still requires verification)
- Exact supplied utility background asset and renderer.
- Revised top-navigation grouping, cog and boxless underline.
- Visible-alpha-bound cropping for official title artwork.
- Sole central Play/Pause transport, HUD Down recovery independent of lost focus.
- Information uses a centred dimmed dialog rather than fullscreen.
- Nested navigation discovery by scan/clock overlay.
- Version name/code advanced to 4.1.2/6040078.

## PARTIALLY COMPLETED
- File browser reuses listing engine inside a new three-column surface; route, commands, source selection and focus need checking.
- Search compact keyboard and cached parent-show matches; hierarchy, cold entry and focus need completing.
- Settings has three columns and background; contextual help, first-setting entry, provider icons/grouping remain.
- Global focus: cards dim artwork, but several owned controls still use rectangular focus.
- Featured narrower action and focusable indicators; transition/readiness need checking.
- Details provider labels and overlay; synopsis width, cast density, semantic actions and technical badges remain.
- Player logging/checkpoint changes present; decoder A/V root cause is not established.

## NOT YET IMPLEMENTED
- Library count/local/network storage summary, selected-provider filter and vertical separators.
- Content-sized Home editor and remaining compact-dialog corrections.
- Selected-first/alphabetical provider selector with genuine artwork.
- Netflix-specific handoff investigation.
- Current release notes, return handover, UI audit, compiled APK and new screenshots.

## NEEDS VERIFICATION
All recovered code is unbuilt. Compile, signed APK, enabled-Preview startup/reinstall, Home/Movies/TV/Details, strict-screen visual inspection and browser/HUD focus checks. Physical playback, streaming title handoffs and Shield performance remain NOT TESTED — USER QA REQUIRED.

## POTENTIAL REGRESSION / INCOMPLETE WORK
- Startup gate uses backdrop timeout and does not track all first-frame artwork.
- Browser swaps root and detaches legacy title: inherited lifecycle/focus assumptions require inspection.
- Main Details provider labels now take more width inside existing action row.
- Settings category entry still initially focuses the rail.
- Cached-only parent series search cannot guarantee cold-entry results.
- Completion/error guards preserve checkpoints but do not establish cause of abnormal stops.

Preserve all passed baseline behaviours and all valid recovered implementations. Continue from this commit; no reset or older-hand-over merge.
