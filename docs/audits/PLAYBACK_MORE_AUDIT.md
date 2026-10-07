# Playback More Audit

Status: **COMPLETED**
Preserved: 7 October 2026

## Scope inspected

The audit reviewed the playback **More** menu to identify duplication, inappropriate actions and the minimum useful set of secondary playback controls.

The physical candidate showed:
- PLAYBACK heading
- Playback Speed
- Play Mode
- VIDEO heading
- Format
- Report a Problem

Earlier concepts also risked duplicating Audio, Subtitles, Info or Settings in More.

## Findings

Audio and Subtitles already have primary HUD entries and must not be duplicated in More. Info was deliberately removed from the primary HUD because Details already provides title information. Full application Settings must not be linked from playback.

**Format is not technical metadata.** It is picture/aspect presentation: Original, Full Screen, Stretch, 4:3, 16:9 or equivalent supported presentation modes.

Report a Problem is unnecessary in the normal playback menu and should be removed. The PLAYBACK/VIDEO headings add hierarchy without enough benefit for a three-item menu.

## Approved final structure

A flat compact More menu:
- **Playback Speed**
- **Play Mode**
- **Format**

Remove:
- Report a Problem
- unnecessary PLAYBACK / VIDEO section headings
- Audio duplication
- Subtitles duplication
- Info
- full Settings links

Playback Speed and Play Mode may open their existing value pickers. Format opens the existing supported aspect/picture-presentation choices.

## Validation

- correct focus/Back restoration through each child picker;
- no duplicate primary actions;
- Format changes picture presentation rather than opening technical metadata;
- no Report a Problem;
- no full Settings route;
- preserve playback state and accepted HUD accent/focus styling.

## Authority

Final behaviour is incorporated into `docs/design/PLAYBACK.md` and `docs/qa/next-version-authority.md`.
