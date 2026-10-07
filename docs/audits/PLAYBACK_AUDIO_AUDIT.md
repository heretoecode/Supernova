# Playback Audio Audit

Status: **COMPLETED**
Preserved: 7 October 2026

## Scope inspected

The audit reviewed playback Audio controls and separated current-session choices from persistent/global defaults.

Observed capability groups were:
- available audio tracks
- audio timing/synchronisation
- audio enhancements/options

## Findings

Track selection and synchronisation clearly belong in playback. Audio Boost and Night Mode also belong here because they are contextual listening controls: the user may want them for a particular title, volume situation or time of day without leaving playback for full Settings.

The HUD must remain compact and must not become a second Settings screen.

## Approved final structure

Audio:
- **available audio tracks**
- **Audio Sync**
- **Audio Boost**
- **Night Mode**

Persistent/global defaults remain in full Settings.

No route from this HUD panel to full application Settings.

## Validation

- multiple/no alternate-track cases;
- selected-track state and focus;
- Audio Sync adjustment and Back behaviour;
- Audio Boost/Night Mode changes during active playback;
- repeated entry/exit without losing the physically accepted HUD styling or playback state.

## Authority

Final behaviour is incorporated into `docs/design/PLAYBACK.md` and `docs/qa/next-version-authority.md`.
