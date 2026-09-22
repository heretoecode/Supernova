# Physical acceptance and known limits

NOT TESTED — USER QA REQUIRED

1. Shield upgrade over 4.1.3 with Supernova already enabled; Home, Movies, TV,
   Search, Settings and both Movie/TV Details using the real library.
2. The captured long WebDAV episode and seeks across its earlier ~38:18 failure;
   interrupt network access and verify failure keeps resume/Continue Watching.
   A malformed server/demuxer that reports genuine EOF without an error can still
   require further native evidence. Do not call the original failure solved yet.
3. Resume after interruption; lip-sync over time, pause/resume, PCM/passthrough,
   audio transitions, decoder recovery and subtitle timing on Shield hardware.
4. Keep known-passing WebDAV automatic scanning; all scheduling frequencies,
   opens/returns, per-source inclusion, manual scan and error presentation.
5. Compare against supplied reference images on the real TV: six-panel landing,
   three-pane browser, Settings, logo safe area, focus glow, title spacing and
   keyboard. Emulator/sample images are not proof of visual acceptance.
6. Search each real Lanterns encode; selected file, Versions, best-quality Play,
   resume and logical watched history; no file should become inaccessible.
7. Home -> Details -> Playback -> Details -> Home focus; direct Continue Watching
   -> Playback -> original Home focus. Settings -> Back must remain paused.
8. SMB/SFTP/WebDAV/FTP authentication, browsing, save location, add/remove library
   source and back/focus restoration. Removal must never delete physical files.
9. Diagnostic export after a forced exit and after a normal exit; redact private
   data before sharing. Native A/V offset is not exposed and is not fabricated.

Known limits / continuation work:
- The complete approved visual composition has not been accepted on a Shield.
- Browser directory transitions still reuse Nova's fragment/back-stack machinery;
  an invariant in-place centre-pane shell across every protocol is not verified.
- Technical hydration is focused-item/on-demand, not a whole-library probe.
  The metadata retriever does not expose every HDR transfer characteristic; those
  fields remain unknown until supplied by real metadata. Aggregate show data may
  require a subsequent library refresh after a file is hydrated.
- Per-file database bookmarks remain the underlying Nova model. Presentation
  coalesces variants and transfers resume for automatic choice, but conflict
  resolution with external Trakt/network bookmarks needs physical QA.
- Diagnostics do not yet provide complete per-source added/changed/removed counts
  or native renderer underrun/discontinuity events when those engines expose no
  structured callback. Existing scanner-state instrumentation remains available.
- Broad lint/full regression and hardware capability matrix are deliberately
  outside the fast-preview acceptance evidence.
