# Playback Settings source parity

Baseline PlayerActivity.createPlayerTVMenu/updateAudioMenu/updateSubtitleMenu and PreviewPlaybackMenus; native controllers retain all ranges and capability gating. No new preference keys or native options. Tests: PreviewPlaybackMenusTest and existing picker/subtitle/player regressions.

| Tab | Actual existing capability | Availability / exact source owner |
|---|---|---|
| Audio | Current native audio tracks, selected radio, language metadata | mAudioInfoController count/selection; native callback |
| Audio | Audio filters and Night Mode direct switches | Decoder/audio capability gating and current mAudioFilt/mNightModeOn; no invented boost/downmix/output |
| Audio | Spatialisation capability, disabled where unsupported | Existing native menu item only |
| Audio | Audio delay, Keep Setting | createTVAudioDelayDialog: 10 ms step, ±known duration; passthrough maximum 0; native saved-delay preference |
| Subtitles | Native tracks incl Off; other-language tracks | mSubtitleInfoController; existing tag false partitions Other languages |
| Subtitles | Delay and speed ratio | createTVSubtitleDialog: ±duration, configured 1 step; actual subtitle_delay_ratio_array |
| Subtitles | Appearance, colour, outline/background, vertical position, opacity | Existing SubtitleSettingsDialog/native controls; legacy native UI size 10–100 step1, vertical 0–255, opacity 0–255, existing colour palette; inherited richer appearance controls retained unchanged |
| Subtitles | Download / choose subtitle file | Existing provider/filesystem callbacks and availability checks; external chooser ownership preserved |
| Subtitles | Reading / download languages | VideoPreferencesCommon.subtitleLanguageChoices; favSubLang and languages_list; no new codes |
| Subtitles | Prepare Searches / Current Season / Subtitles by Default | Existing preferences, defaults false/true/inverted false; retain dependency on default display |
| Video | Format choices directly visible | Exact pref_format_mode_entries; mSurfaceController.getMax hides/disables unsupported values; no new zoom option |
| Video | PiP | Existing API24+/TV/feature check; native callback |
| Video | 3D | Existing capability-gated card from native menu; no invented video filter or track setting |
| More | Playback speed | Native audio-speed action, 0.50–2.00 step .05, native availability |
| More | Play mode | Exact pref_play_mode_entries, native current mode |
| More | Segment skipping session switch / read-only segment summary | Existing IntroDB/session override; independent of scene chapters |
| More | Notification mode | Existing non-Leanback-only pref_notification_mode_entries |

Sleep timer code is commented out in the native card inventory and is not newly exposed. Technical info remains through the existing supported secondary path, and global Settings is not added to this overlay.

Panel geometry remains existing: Space Black 0xe615171b, radius10, padding16; width min(620 scaled dp,72% safe width), height min(360 scaled dp,safeHeight−48); right inset24, vertically centred. Audio/video two-column hierarchy and direct format choices remain. Inline containers now appear immediately after their invoking row; native adjustment persistence/dismissal callbacks remain owned by TVCardDialog. Multi-choice selection collapses to parent; Back collapses adjustment/choices before closing Settings; tab switch/close flushes native adjustments. Focus/checked/toggle states remain separate. Physical Concept A4 image comparison remains blocked on unavailable reference image bytes and Shield capture.
