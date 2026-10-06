# Preview 4.1.2 — Shield QA

Install `NOVA_Preview_4.1.2.apk` over the existing Preview. Do not uninstall or clear data. The package and signing certificate are retained; the version code increases to 6040078. Keep a verified backup as usual.

## First checks

1. With New UI already enabled, install the update, open it, close it and reopen it. Check that Home is composed before the splash fades and that saved rows/providers remain.
2. Visit Home, Movies, TV Shows, Network & Files, Search and Settings. Check navigation/clock position and that D-pad focus stays visible.
3. On Featured, check the full Resume label, Left/Right on the indicator strip, title/logo footprint and transition.
4. Search for a known series (including Shogun for Shōgun). Confirm the parent show appears above episodes, opens series Details and the compact keyboard leaves results visible.
5. Browse a real Local/USB/WebDAV/SMB folder. Confirm the primary source/path/list/context screen has no legacy blue underlay. Check folder entry, Back, Options and left/right focus.
6. In Settings, check first-setting focus, matching contextual help, all nine categories and provider tick persistence without flashing. Reopen the provider chooser to see the selected-first grouping.
7. Check Details synopsis width, cast density, grouped Actions, provider icon/name pairs and the centred Information overlay. Back should return focus to Information.

## Playback priority

- Test both a movie and a TV episode. The HUD must show only one centred Play/Pause, with Audio/Subtitles bottom-left and More retained.
- Seek, then press Down: focus should return to Play/Pause. Check timeline/control/More navigation and first-Back dismissal, then Back-to-exit.
- Check official movie and TV logo size, concise track labels and episode end wording.
- If A/V sync drifts or playback exits unexpectedly, note the title, source/protocol, approximate timestamp, audio/subtitle track, decoder/passthrough settings and the exact preceding action. Test Resume immediately afterwards. Do not deliberately kill the process during a library/backup write.
- Confirm Continue Watching advancement/final-episode retention, Up Next/autoplay, binge/recap and non-destructive Watch Next hiding still work.

## Known limitations to keep separate from regressions

- A/V desynchronisation and spontaneous native exits are not proven fixed. The changes trace lifecycle events and improve progress checkpoints.
- Netflix title-page navigation remains unresolved; compare with the previously working Prime example.
- Service filtering includes only local titles with fresh availability already learned through Details for the selected country/provider. Unknown or stale availability is excluded; this is not a full-library remote catalogue scan.
- Real network scans, credential-backed sources, provider handoffs, populated artwork timing and Shield performance cannot be certified by the emulator.

Report failures with the screen/action, expected versus actual behaviour and a screenshot or short recording where useful. No need for an exhaustive test sweep before reporting a clear regression.
