# Additional Shield findings — 9 October 2026

**Status:** observed after frozen 35-item fixes-only scope. This document **does not alter** `docs/qa/4.1.7-final-shield-qa.md` or automatically authorise coding on `codex/post-4.1.7-shield-fixes`. Triage whether each finding is a clarification of existing fixes or requires a separately approved scope update.

## QA-NEW-01 — Advanced Diagnostics unreachable via D-pad
- **Evidence:** user-uploaded 20.7-second `IMG_4760.mp4`, reviewed in chat; video bytes not present in GitHub.
- **Observed:** Advanced hover shows diagnostics actions in centre; entering Advanced reveals only Video/Audio/Network/Subtitle Compatibility indented options and shifts focus into those. No usable route to diagnostic actions including Export Diagnostic Report.
- **Source finding (main read-only):** `PreviewSettings.java` appends diagnostics directly to Advanced but generates the expanded child rail exclusively from nested `PreferenceCategory` groups. See `docs/design/SETTINGS.md` for line references and approved corrective UX.
- **Requested outcome:** Advanced introductory centre panel; Diagnostics nested beside four compatibility groups; accessible existing actions in centre, help on right, stable D-pad/Back/focus; preserve export.
- **Relationship:** overlaps frozen QA items 22–23 (Settings three-panel/nested navigation), but **new explicit Diagnostics routing requirement** was **approved by the user on 9 October 2026** for the next fixes-only release; see the appended approval in `docs/qa/4.1.7-final-shield-qa.md`.
- **Status:** approved for next fixes-only release; **not yet implemented or device retested**.

## QA-NEW-02 — Playback unexpectedly re-enters Preparing Playback
- **Evidence:** user observation of MobLand Season 2 Episode 4 at approximately 02:32 Dublin local time (incident timestamp not yet independently verified).
- **Observed:** video was playing, then returned unexpectedly to Preparing Playback.
- **Needs:** user diagnostic ZIP; correlate lifecycle/player recreation, media-source/network interruptions, decoder, memory, retries, and app state. **No root cause established.**
- **Status:** pending diagnostic export and investigation. Remind user conversationally before next Codex handover; **no scheduled reminder**.
