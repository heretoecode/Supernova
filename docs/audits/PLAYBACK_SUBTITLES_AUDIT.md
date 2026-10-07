# Playback Subtitles Audit

Status: **COMPLETED**
Preserved: 7 October 2026

## Scope inspected

The audit reviewed the playback Subtitles entry point and the functions reachable from it, with the goal of separating useful per-session controls from global application Settings.

Observed/current capabilities included:
- Off / available subtitle tracks
- alternate/other-language track selection
- Download/Add subtitles
- file-based subtitle selection where supported
- subtitle timing/synchronisation
- appearance controls
- routes that could expose broader subtitle Settings

## Findings

The HUD should contain controls that make sense while watching the current video. It should not become a duplicate of the full Settings hierarchy.

Subtitle appearance is an exception worth keeping in playback because the result can be judged immediately against the actual picture/subtitles.

Physical QA also uncovered a functional defect not explained by menu structure: changing subtitle tracks visibly advances the video by a frame and then snaps back. A subtitle-track change must not visibly disturb the video frame or playback position.

## Approved final structure

First level:
- **Off / available subtitle tracks**
- **Download Subtitles**
- **Sync**
- **Appearance**

Appearance retains:
- Style, including style selector and colour choices
- Vertical Position
- Outline subtitles
- Subtitle background
- Background opacity

Global defaults, provider configuration and preferred language remain in full application Settings. There must be no link from the HUD back to full Settings.

## Functional requirement

Fix the subtitle-track frame-jump at its root. Track changes must not seek, restart playback, unnecessarily rebind/recreate the player or surface, or otherwise produce a visible frame/position disturbance.

## Validation

- switch repeatedly among Off and multiple tracks while video is moving;
- verify no forward/back frame flash;
- verify Sync and Appearance changes stay in the playback context;
- verify Back returns predictably;
- verify no full-Settings route remains;
- protect the physically accepted four-control HUD and Supernova-blue focus/accent styling.

## Authority

Final behaviour is incorporated into `docs/design/PLAYBACK.md` and `docs/qa/next-version-authority.md`.
