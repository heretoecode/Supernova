# Supernova Preview 4.1.4

Corrective Preview based only on Preview 4.1.3. Physical Nvidia Shield testing is
the acceptance authority; these notes describe implementation, not verified
resolution of every reported physical defect.

- Native demux read errors now use the failure path instead of silently becoming
  completion. Selected upstream A/V clock and HTTP/WebDAV corrections are included.
- Featured changes update the hero rather than rebinding every Home row. First
  backdrop load no longer fades in after the launch readiness gate.
- Content-based focus, compact Home customisation/rename keyboard, aligned card
  captions, smaller See All and consistent Cast & Crew text geometry.
- Network & Files has the six-panel landing composition, fixed scan-status area,
  existing-scheduler controls, source summaries and a Supernova source-management
  screen. Visible protocol choices omit internal SMBJ/SSHJ implementation names.
- Settings uses the twelve required categories. Trakt, OpenSubtitles and the real
  IntroDB switch are grouped under Integrations. Compatibility controls remain in
  Advanced; normal source scheduling lives in Network & Files.
- Search results show real resolution/filename distinctions and preserve selected
  file identity. Details exposes Versions. Ordinary selection ranks measured
  resolution, bytes per duration, then stable file ID, carrying existing resume.
- Grid/list share sort direction and field. Focused list entries can retrieve
  missing technical metadata without opening Details. Unknown HDR is not guessed.
- Playback HUD anchors and paired spacing are corrected; More avoids duplicated
  Audio/Subtitles/Info entries. Technical information is compact and names the
  physical file. Settings entry pauses the existing session.
- Diagnostics add process/unclean-exit evidence, richer checkpoints and a generated
  summary while retaining opt-in logging, redaction and bounded retention.

See BUILD_AND_QA.txt in the delivered return handover for exact build, source,
APK checksum and tests actually completed. See UPSTREAM_PORTS.md for individual
upstream changes, risks and limitations.
