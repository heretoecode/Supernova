# Supernova Playback HUD — complete functionality audit

**Date:** 16 October 2026. **Status:** investigation/documentation only. **Related work:** FD-002, under discussion. No redesign, implementation, APK, release, signing, merge or 0.140 scope change is authorised by this report.

## 1. Snapshot, authority and verification language

The audited application is the final delivered **Supernova 0.139 corrective candidate**, package `app.supernova.player`, versionCode 139, source **`a4841c1dbaac5e1037fb19f9f11882848b435e0b`**. The implementation branch was `codex/supernova-0.139-implementation-2026-10-13`, observed at **`d173e8dd9ea41834118a32e240518d91e15e11d1`**. Comparing `src/` and `res/` at those two commits produced no differences. The signed artifact recorded by the repository has SHA-256 `9421a982296816f1ef53aa062ea608ed213dadc8475069fa9039202e5ee7898b`; no APK was built, installed or downloaded for this audit. This is a delivered candidate, **not evidence of owner acceptance on Shield**. [E01]

`main` observed at `75d9f012fa4f65869eef5d972ad4725df504bd9c` still points readers to the older 4.1.7 accepted baseline. It is not a sufficient description of the latest delivered playback code. The later planning branch `docs/project-decisions-2026-10-12`, observed at **`71be45aace7c2f71b010848a2249c148595c9286`**, carries the current Future Development catalogue and 0.140 handover. This report is delivered on a new documentation-only branch based on that planning snapshot. **Application code evidence links point to the audited application SHA, not the documentation branch's older application files; later delivery/runtime receipts are separately pinned to the observed implementation-branch documentation SHA.** No 0.140 implementation branch appeared in the inspected remote heads; this does not establish that no work exists in another checkout or unpublished branch.

The active player is the inherited `PlayerActivity` → `PlayerController` / `PlayerService` → `Player` architecture. On TV with `try_new_ui=true`, `PlayerController` inflates `player_controller_experimental.xml`. Foundation UI policy enables that preference. `PreviewPlaybackMenus` renders Supernova dialogs using the real NOVA `TVMenuItem` callbacks, rather than implementing a second set of playback operations. Split/stereo and classic/non-TV paths remain conditional variants. [E02] [E03] [E04]

Evidence labels used throughout:

- **SC — Source-confirmed:** a reachable construction path and implementation were traced at the frozen SHA. It does not mean the decoder, service or hardware accepted the action in a live session.
- **RV — Runtime-verified:** actually exercised in an Android application. This audit performed **zero** new Android UI/runtime checks. Earlier repository runtime evidence is identified separately as **historical RV**, with its source and limits.
- **CU — Conditional/unverified:** SC functionality whose media/device/provider/state condition was not exercised in this audit. All SC inventory rows also have this runtime limitation.
- **UO — Unreachable/obsolete:** code/resources exist, but are hidden, filtered, commented out, or belong to a different interface. Do not add them to the shipped TV HUD inventory.

Displayed strings below are the **English resource/hard-coded labels**. Other locales translate resource-backed labels; dynamic track, language and file names are described by the exact generating rule. Headings beginning `—` are section headings, not settings. Off/on labels in tables describe Boolean states; most toggles display a check, not literal “Off” and “On” choices.

## 2. Completeness and counting rules

The stable count is of **control/setting/action families**, not a claim that every file has the same number of tracks or search results.

| Register | Count | Boundary |
| --- | ---: | --- |
| Primary HUD buttons | **4** | Play/Pause, Subtitles, Audio, More |
| Top-level interactive HUD controls | **5** | Four buttons plus seek bar; C01–C05 |
| Configurable fields | **29** | S01–S29: 21 TV playback fields, 1 non-Leanback notification field, 5 linked app Subtitles fields, 2 conditional touch adjustments |
| Other action families | **26** | A01–A26; includes workflow/confirmation/dismissal actions, without counting every dynamic file/result row separately |
| Total controls, settings and action families | **60** | 5 + 29 + 26; all source-confirmed, all live execution unverified in this audit |
| Informational indicator families | **7** | I01–I07, excluded from 60 |
| Total inventory records | **67** | C + S + A + I |
| Navigation/presentation states | **28** | M01–M28 below: 3 root menus, 19 other popup/workflow states, 1 app Settings workspace, 2 inline choice surfaces, 2 alternative/transient dialogs, 1 shared operation-state family |

M21 (3D) and M22 (Notifications) are conditional; M26 (logging-off notice) substitutes for M24/M25; M27 is a startup resume dialog. M28 groups progress/error/empty notices by shared implementation instead of pretending every message is another submenu. M12–M14 leave playback for app Settings. Thus **25 of the 28 states are popup/workflow families and 3 are the app Settings workspace/inline editors**; there are no 28 always-visible nested menus.

For independently checkable choices, the 29-field register contains **91 fixed selectable value entries across 18 fields**, including 31 speed values and 16 colour swatches. Four fields additionally expose **634 integer values** (91 subtitle size + 256 position + 256 opacity + 31 touch brightness levels); two delay ranges depend on duration; five fields depend on tracks/localized languages/system volume range. These counts exclude dynamic result/file lists, repeated representations of a track, and unrelated global Settings. Boolean fields contribute two values each. The 3D and Notifications values are included even though a standard Shield session may never show them. The 8 Format labels are counted as labels; one has a capability mismatch documented in B02.

Supporting global configuration is listed separately in section 8. Merely navigating out into full app Settings does not make every setting in the application a playback-HUD setting. This boundary prevents counting backup, library scanning, account management, etc. as HUD functionality while explicitly recording the escape route.

## 3. Complete current navigation tree

IDs identify presentation states; S/A IDs refer to the master registers. “Settings” under appearance is the **actual nested dialog title** despite its “Subtitle Appearance” opener.

```text
Playback HUD (normal Foundation TV / try_new_ui)
├─ C01 Play / Pause
├─ C02 Seek bar → target-time bubble; commit remote seek on key release
├─ C03 Subtitles → M01 "Subtitle tracks"
│  ├─ TRACK
│  │  ├─ S01 "None" [only when >=1 actual subtitle track exists]
│  │  └─ preferred-language / English / unknown-language track rows
│  ├─ TIMING & APPEARANCE
│  │  └─ "Delay" → M03 "Delay"
│  │     ├─ S02 signed time adjustment
│  │     └─ "Speed Adjustment" → S03 none / Subtitles too fast / Subtitles too slow
│  ├─ ADD SUBTITLES
│  │  ├─ A01 "Download Subtitles" → M05 "Download Subtitles" results
│  │  │  └─ A02 <language> · <filename> → M06 "Subtitle Download"
│  │  │     ├─ filename; Language; Source; optional hash match and Release
│  │  │     └─ A04 "Back" / A03 "Download" → download → re-enumerate tracks
│  │  └─ A06 "Choose Subtitles" [file/smb*/sshj/sftp only]
│  │     └─ M07 "Choose Subtitles"
│  │        ├─ Associated with this title / Other subtitle files headings
│  │        └─ A07 <filename> · Media folder / Saved subtitle cache [· Active]
│  │           └─ M08 "Subtitle Actions"
│  │              ├─ A08 "Use Subtitle" [associated, playback selection enabled]
│  │              ├─ A09 "Associate with This Title" [unassociated]
│  │              │  └─ M09 "Associate Subtitle" → Back / A10 "Associate"
│  │              ├─ A11 "File Information" → M10 "Subtitle File" → A19 "Close"
│  │              └─ A12 "Delete Subtitle File"
│  │                 └─ M11 "Delete Subtitle File" → A14 "Cancel" / A13 "Delete"
│  ├─ TRACK → "Other languages" → M02 "Subtitles · Other languages"
│  │  └─ S01 remaining track rows [if any; same selection field]
│  ├─ TIMING & APPEARANCE → "Subtitle Appearance" → M04 "Settings"
│  │  ├─ "Style:" → S04 size sample + S05 16 colour swatches
│  │  ├─ "Vertical Position:" → S06 numeric adjustment
│  │  ├─ S07 "Outline subtitles"
│  │  ├─ S08 "Subtitle background"
│  │  └─ "Background opacity" → S09 numeric adjustment
│  └─ A15 "Subtitle Settings" → M12 app Settings, Subtitles category
│     ├─ S23 "Subtitles reading language" → M13 inline single-language list
│     ├─ S24 "Subtitles download languages" → M14 inline multi-language list
│     ├─ S25 "Prepare Subtitle Searches"
│     ├─ S26 "Prepare Current Season"
│     └─ S27 "Subtitles by Default"
├─ C04 Audio → M15 "Audio tracks"
│  ├─ TRACK → S10 available audio tracks
│  ├─ ENHANCEMENTS
│  │  ├─ S11 "Audio Boost"
│  │  ├─ S12 "Night Mode"
│  │  └─ S13 "Platform spatialization"
│  └─ SYNC → "Audio delay" → M16 "Audio delay"
│     ├─ S14 signed time adjustment
│     └─ S15 "Keep this setting globally"
└─ C05 More → M17 "More"
   ├─ PLAYBACK
   │  ├─ "Playback Speed" [conditional] → M18 "Playback speed"
   │  │  ├─ S16 0.50×…2.00×, step 0.05×
   │  │  └─ S17 "Keep this setting globally"
   │  └─ "Play Mode" → M19 "Play mode"
   │     ├─ S18 Single / Folder / Repeat single / Repeat folder / Binge watching
   │     └─ S19 "Segment Skipping (this session)"
   └─ VIDEO
      ├─ "3D mode" [conditional] → M21 Off / Side by side / Top and bottom
      ├─ "Format" → M20
      │  ├─ S20 Original / Fullscreen / Stretched / 4:3 / 16:9 / 1.85:1 / 2.39:1 / Optimized
      │  └─ A21 "Picture in Picture" [TV, Android >=24; OS capability still matters]
      ├─ "Notifications" [non-Leanback only] → M22 → S22 3 notification policies
      ├─ A16 "Technical Information" → M23 read-only Video / Audio / File / Source columns
      └─ A17 "Report a Problem"
         ├─ logging enabled → M24 "Report a Problem"
         │  └─ A18 Playback / UI / Network / Other → M25 "Problem recorded"
         │     └─ A19 "Close" / A20 "Copy"; reference/category/time/QR information
         └─ logging disabled → M26 "Diagnostic logging is off" → Close / Copy

Conditional playback overlays (not menu items)
├─ A22 "Skip Intro" / "Skip Recap" / "Skip Outro" / "Skip Credits" / "Skip Preview"
├─ I06 "UP NEXT · Starting in <n>s", next-episode context → A23 "Play Now"
│  └─ A26 Back while panel focused: cancel this automatic next transition (no Cancel button)
└─ M27 startup message "Use network resume position?" → A24 Yes / A25 No

Conditional touch input (no new menu)
├─ S28 left vertical swipe: window brightness0…30
└─ S29 right vertical swipe: system MUSIC volume0…device max

Shared M28 operation/error/empty states
├─ Finding local and saved subtitle files… → A05 Cancel
├─ Searching OpenSubtitles… / Downloading selected subtitle… → A05 Cancel
├─ Associating subtitle file… / Deleting subtitle file… [not cancelable]
└─ offline / no results / unavailable source / failed mutation notices → A19 Close
```

The subtitle sections can repeat: the wrapper preserves native item order, then appends Other languages, Subtitle Appearance and Subtitle Settings. Do not infer that “TIMING & APPEARANCE” is one expandable submenu. `— TRACK`, `— SYNC`, `— ENHANCEMENTS`, `— ADD SUBTITLES`, `— PLAYBACK` and `— VIDEO` are non-focusable headings. A disabled row receives the exact suffix **` — unavailable`**. Audio speed is explicitly removed from Audio and promoted once into More; Audio/Subtitles/Info/Preferences cards are filtered out of More. [E04] [E05] [E06]

## 4. Master control and settings register

Persistence terminology: **session** means live player/service state; **global** means shared preference across titles; **per file** means indexed VideoDbInfo/MediaLib persistence, not a universal guarantee for arbitrary external streams. Writes can be asynchronous and depend on valid indexed identity and subsequent validation.

### 4.1 Top-level controls

| ID | Exact displayed name / path | Function, values and default | Availability, persistence and limitations | Evidence |
| --- | --- | --- | --- | --- |
| C01 | HUD `Play` or `Pause`; accessibility `Play/Pause` | Toggle transport. A prepared active session normally plays; user pause is respected. During pending seek toggles intended post-seek state. | SC/CU. Normal playback controls; no configuration default. Not a “persist play/pause forever” preference. | [E02] [E07] |
| C02 | HUD seek track, no text label; floating elapsed/target bubble | Remote target seeking; pointer drag also supported. Clamp to 0…duration−2000 ms for Preview key seeking; see section 6. | SC/CU. Requires corresponding forward/backward seek capability. Unknown-duration/relative-position paths use legacy behavior. Resume persistence is separate from seeking itself. | [E08] [E09] |
| C03 | HUD `Subtitles` → `Subtitle tracks` | Open M01; selected track has tick. | SC/CU. Always an opener even with no subtitles, permitting download/settings; None/timing/appearance require tracks. | [E02] [E04] [E05] |
| C04 | HUD `Audio` → `Audio tracks` | Open M15. | SC/CU. Opener remains enabled but half-alpha when no audio; native audio card is hidden when track count=0. Empty-menu behavior is a limitation, not proof of working controls without audio. | [E06] [E10] |
| C05 | HUD `More` → `More` | Open M17. | SC/CU. Menu key is another route. No direct Info, Speed, Format, Previous, Next, rewind or forward primary button. | [E02] [E04] [E10] |

### 4.2 Subtitles — track, timing and appearance (S01–S09)

| ID | Exact displayed name / navigation | Every choice or adjustment | Default and persistence | Availability / defects / evidence |
| --- | --- | --- | --- | --- |
| S01 | Subtitles → `None` / dynamic track labels; or `Other languages` → labels | None plus every enumerated embedded/external subtitle track, partitioned rather than discarded. Embedded label uses track name, language, subtitle-format label and dispositions; external label comes from filename-language association, including HI where recognized. There is no fixed list of languages/tracks for a file. | Saved per-file track plus language validation; otherwise selection policy in section 7. None is represented internally separately from UI position 0. No universal “English” default. | SC/CU. `None` exists only if actual track count >0. Unknown or preferred/English tracks remain in root; other tracks go into M02. Appearance disabled for None/bitmap. [E05] [E11] [E12] |
| S02 | Subtitles → `Delay` → signed picker | Milliseconds, initial step **1 ms**, held-key acceleration; if duration>0 bounds −duration…+duration. Zero shown `0 ms`, other values signed ms or seconds. OK reverses sign when within bounds. | 0 ms if no saved value; per-file signed delay saved immediately through `persistVideoInfo()`. | SC/CU. Disabled with None; valid graphical subtitle can still have timing. Unknown duration leaves picker without duration bounds. [E13] [E14] [E15] |
| S03 | Delay → `Speed Adjustment` | `none` = ratio 0, 24000/24000; `Subtitles too fast` = 1, 25025/24000; `Subtitles too slow` = 2, 24000/25025. This scales subtitle timing; it is not playback speed. | `none` / 0; per-file, persisted with delay. | SC/CU. Same availability as Delay. Nested native picker uses child positions minus 3 to map ratios. [E13] [E14] [E16] |
| S04 | Subtitles → `Subtitle Appearance` → dialog `Settings` → `Style:` sample | Integer size **10…100** (91 possible values); preview text `ABC` if <20, `AB` if <50, `A` otherwise. Picker raw range 1000…10000, step1, callback divides by100. Actual subtitle text uses `calcTextSize`, not the sample's raw sp; size is a native0–100 scale, not a font-size point value. | **30** in TV-sized no-touch landscape / sw600 resources; base-resource fallback **12**. Global `pref_play_subtitle_size_key`, saved on native card Back/dismiss callback. | SC/CU. Text subtitle active. B03: tiny raw steps cause repeated keys with no effective size change. No font-family selector. [E17] [E18] [E19] |
| S05 | Same `Style:` area, unlabeled colour swatches | 16 swatches: `#ffffff`, `#dddddd`, `#bbbbbb`, `#999999`, `#777777`, `#555555`, `#333333`, `#cc33ff`, `#aa66cc`, `#33b5e5`, `#0099cc`, `#99cc22`, `#77b300`, `#ffff66`, `#ffa31a`, `#ff3333`. No displayed colour names/hex text. | White `#ffffff`; global `pref_play_subtitle_color_key`, saved with appearance. | SC/CU. 8 swatches per row; D-pad moves highlight, OK applies. Initial palette cursor=0, not initialized from saved colour (B07). [E17] [E20] |
| S06 | Appearance → `Vertical Position:` | Integer **0…255**. Raw picker 0…25500, step10, callback divides by100; held-key acceleration. Maps to bottom spacer `(screenHeight × value / 765)+1`, about one-third screen height at maximum. | 0; global `pref_play_subtitle_vpos_key`, saved with appearance. | SC/CU. Graphical tracks force position0 and disable appearance. This is not a top/middle/bottom radio list. B03 applies. [E17] [E19] [E21] |
| S07 | Appearance → `Outline subtitles` | Unchecked/checked. | false; global `pref_play_subtitle_outline_key`, saved with appearance. | SC/CU. Text subtitle active. [E17] [E18] |
| S08 | Appearance → `Subtitle background` | Unchecked/checked. | false; global `subtitle_background`, saved with appearance. | SC/CU. Text subtitle active; no background-colour selector. [E17] [E18] |
| S09 | Appearance → `Background opacity` | Integer **0…255**. Raw range 0…25500, step1, callback integer division by100; displayed numeric value. | 128; global `subtitle_bg_opacity`, saved with appearance. | SC/CU. Control is still present when background unchecked; opacity has no visible effect then. B03 applies. [E17] [E18] |

Appearance edits apply live and are saved when `TVCardDialog.handleBackPressed()` invokes `onResult`; Back is **not a rollback**. Programmatic `PreviewPlaybackMenus.close()` dismisses without explicitly calling that native result callback, so save-on-close must not be assumed for every lifecycle/stop path (B08). [E22]

### 4.3 Audio and playback (S10–S22)

| ID | Exact displayed name / navigation | Every choice or adjustment | Default and persistence | Availability / limitations / evidence |
| --- | --- | --- | --- | --- |
| S10 | Audio → dynamic track row | Every enumerated audio track. Label combines generated name/language/format/dispositions and channel string; fallback `Track <n>` when generated name empty. No “audio off”, device picker or arbitrary codec conversion list. | Saved per-file track when valid; otherwise supported original-language preference, favourite-language/default/variant matching, first supported/native fallback. | SC/CU. Native menu built only with >0 audio tracks. Unsupported selection may fail/fall back; a row is not a codec support guarantee. [E06] [E11] [E23] |
| S11 | Audio → ENHANCEMENTS → `Audio Boost` | Boolean: off maps filter0; on maps filter3. Not a 0–200% gain slider. | Off/filter0; global `pref_audio_filt_int_key`, immediate apply/write. | SC/CU. Row disabled for configured passthrough1 or2; mode0 or3 permits filters. [E06] [E24] |
| S12 | Audio → `Night Mode` | Boolean; native audio filter's night-mode flag. No independently exposed DRC-strength choices. | false; global `pref_audio_filt_night_int_key`, immediate apply/write. | SC/CU. Same passthrough disable rule as Audio Boost. Actual audio effect requires output testing. [E06] [E24] |
| S13 | Audio → `Platform spatialization` | Boolean; applies spatializer/downmix policy and refreshes audio output. | Saved preference false; global `player_spatialization_enabled`. Effective checked state is capability AND preference. | SC/CU. Enabled only Android≥32, supported+available platform spatializer and 5.1/7.1 spatialization capability, configured passthrough=0. Disabled row remains visible with audio. Do not promise this on Shield. [E25] |
| S14 | Audio → `Audio delay` → signed picker | Fixed **10 ms** step; applies after750ms quiet and on detach; ±duration bounds if known; OK reverses sign if valid. If passthrough supported and configured mode>0, **positive delay prohibited**, maximum0; existing positive global preset reset. Display signed ms/s. | 0; session unless S15 keeps a global value. Service negates sign for native AV delay. | SC/CU. Audio track required. Mode3 is also delay-limited although filters permit it. [E26] [E24] [E25] |
| S15 | Audio delay → `Keep this setting globally` | Unchecked/checked. On stores current delay; edits while checked store new delay; Off writes0 for next start. | UI derives checked from saved delay !=0; default unchecked. Global saved-value sentinel, **not a separate Boolean**. | SC/CU. Cannot retain a visibly checked “keep zero” intent across reopen (B04). Current session delay is not reset merely by unchecking. [E26] |
| S16 | More → `Playback Speed` → `Playback speed` picker | **0.50× to 2.00× inclusive, step0.05× (31)**: 0.50, 0.55, 0.60, 0.65, 0.70, 0.75, 0.80, 0.85, 0.90, 0.95, 1.00, 1.05, 1.10, 1.15, 1.20, 1.25, 1.30, 1.35, 1.40, 1.45, 1.50, 1.55, 1.60, 1.65, 1.70, 1.75, 1.80, 1.85, 1.90, 1.95, 2.00. | 1.00×; session unless S17. Picker applies after750ms without further changes, also on detach; native applied-speed callback can reconcile requested/applied speed. | SC/CU. Requires audio tracks and `isAudioSpeedEnabled`: global playback-speed flag true, passthrough<=0, audio decoder choice !=`2`. Predicate has no direct SDK check despite older comment mentioning API<23. Enabling passthrough also resets the saved speed preset to1.0 via resetPassthroughPref. No reverse playback. [E27] [E28] [E29] |
| S17 | Playback speed → `Keep this setting globally` | Unchecked/checked; on stores current float, off writes1.0. | UI derives checked from saved speed !=1.0; default unchecked. | SC/CU. Same sentinel limitation for “keep 1.00×” as S15. No cancel/revert semantics. [E27] |
| S18 | More → `Play Mode` → dialog `Play mode` | **Single** stop after current; **Folder** next-file traversal; **Repeat single** looping current; **Repeat folder** wrapped folder traversal; **Binge watching** episode-aware next traversal. | Single=0; global `pref_play_mode_int_key`, immediate. | SC/CU. All five choices shown; usefulness depends on next-file/episode resolution. Binge choice is not hidden for movies. Up Next has additional TV-episode conditions. [E30] [E31] |
| S19 | Play mode → `Segment Skipping (this session)` | Boolean override over persistent IntroDB master/policies. On can fetch available segment boundaries; Off clears pending prompt. | Initial master `supernova_segment_enabled`, default true; override session-only, reset on a fresh start, retained by automatic next-file transition. | SC/CU. Toggle shown even if no provider segment data. Requires scraped usable identity, matching URI, valid safe interval, per-type policy to have effect. Not an unconditional Skip Intro/Credits action. [E30] [E32] [E33] |
| S20 | More → Format | **Original, Fullscreen, Stretched, 4:3, 16:9, 1.85:1, 2.39:1, Optimized**. Original fits source aspect; Fullscreen maps index1/STRETCH_XY fill/crop logic with cutout caveats; Stretched maps index2/FULL_SCREEN and fills screen dimensions; fixed ratios override aspect; Optimized is compromise crop/fit. | Original/index0 when unset/invalid; global ordinary/auto-format preference strings via surface listener. | SC/CU. 8 labels created; effective max7 or8. Optimized index7 is not supported by ordinary max7 and resets to Original although row can tick (B02). Menu construction only hides index1/2 for lower maxima; those branches are not reached by current constructors. [E34] [E35] |
| S21 | More → `3D mode` | **Off, Side by side, Top and bottom**. No Anaglyph entry in TV array, although resource/enum exists. | Current renderer mode / default2D; activity/static forced-mode state, reset on PlayerActivity creation; no preference/database write in this action. | SC/CU. Card created only if `VideoEffect.isStereoEffectOn(current effect type)` or a forced mode is already active. Not a general enable-3D-from-normal-video button. Split-view menu can use legacy adapter. [E36] |
| S22 | More → `Notifications` [non-Shield conditional variant] | **All notifications, Notification alerts only, No notifications**. Applies app player notification presentation. | All notifications; global `notifications_mode`, immediate. | SC/CU. Constructed only without `FEATURE_LEANBACK`. It is not a normal Shield option. Distinguish from system CEC/volume notifications. [E37] |

### 4.4 Linked app Subtitles settings (S23–S27)

These are reachable through **Subtitles → Subtitle Settings**, not an additional current in-HUD inline panel. The activity intent preselects `Subtitles`; the app Settings rail remains available. Source construction contradicts the earlier design rule removing links to full Settings (B01).

| ID | Exact name | Choices/default | Persistence and availability | Evidence |
| --- | --- | --- | --- | --- |
| S23 | `Subtitles reading language` | Single localized language from Appendix A; default locale language; initialized using saved `favSubLang` or locale. | Global `favSubLang`. Enabled state tied to original hide-default preference when Settings is constructed. Drives selection/root-vs-other grouping on subsequent reads; not a per-file track replacement in itself. | [E38] [E39] |
| S24 | `Subtitles download languages` | Multiple choices from same language set. Initially locale; empty saved list is refilled with locale on Settings/downloader initialization. Current inline editor applies each toggle immediately; no Save button. | Global `languages_list`. Downloader normalizes generic zh to zh-cn, generic pt to pt-pt. No language filter/search box inside the Download Subtitles result dialog. | [E38] [E39] [E40] |
| S25 | `Prepare Subtitle Searches` | Boolean, default false. Prepare/cache availability for chosen languages when viewing titles or starting playback; no automatic download. | Global `supernova_subtitle_discovery`; additionally OpenSubtitles integration enabled and not Private Mode for speculative work. | [E38] [E41] |
| S26 | `Prepare Current Season` | Boolean, default true. Pace remaining local episodes in current season; next episode only at season boundary. | Global `supernova_subtitle_season_prefetch`. Visible even if preparation is off; useful only when discovery/episode context permits. Not a download-all-season action. | [E38] [E41] |
| S27 | `Subtitles by Default` | Boolean positive presentation, default true (inverse of underlying hide-default=false). | Global underlying `subtitles_hide_default`; wrapper itself nonpersistent. Turning off does not guarantee suppressing forced subtitles: selection policy can still pick a forced track. Positive wrapper directly calls `setChecked` on hidden preference; immediate reading-language enabled-state refresh is not established by this path. | [E38] [E39] [E12] |

### 4.5 Conditional touch/pointer adjustments (S28–S29)

These are **not popup menu entries or standard Shield D-pad controls**. The controller installs its gesture listener on the outer playback view without a TV exclusion. They must be retained in a complete functionality audit, but actual touch dispatch and effect on a TV/HDMI display remain CU. They do not create new menu states.

| ID | Exact displayed name / path | Choices/default and persistence | Availability / limitations / evidence |
| --- | --- | --- | --- |
| S28 | No text setting label: brightness icon/numeric OSD, vertical swipe on left half of playback | Integer gesture levels0…30, gamma2.2 conversion to window brightness0…1, ±1 per accepted scroll threshold. When window override=-1, getter presents level10 before adjustment. Saves converted0…255 value to `brightness_saved`, restored on player resume; app launch can clear saved value to−1 depending on reset preference. | SC/CU touch-input path. Window brightness effect on Shield HDMI is unverified; it is not HDR/contrast/tone mapping or a Video-tab slider. Border/angle/threshold/lock gates apply. [E64] [E65] |
| S29 | No text setting label: volume/mute icon/numeric OSD, vertical swipe on right half | MUSIC stream integer0…`getStreamMaxVolume`, ±1, clamped; starts at system current volume. System AudioManager owns value/persistence, no app-specific preset. | SC/CU touch-input path, not an output-device chooser. Hardware Shield volume keys separately pass to framework; fixed-volume/CEC sinks may ignore local stream edits. [E64] |

Other touch aliases do not add settings: central single-tap toggles C01, outer single-tap toggles HUD visibility; top75% left/right double-tap seeks−/+10s (C02), bottom25% left/right changes S16 by0.05 when enabled. `onLongPress` is empty. Double-tap right incorrectly tests backward-seek capability (B14). [E64]

## 5. Other actions and informational indicators

### 5.1 Action register

All rows are SC/CU. Actions have no configuration default unless specified. Persistent side effects are explicit. Shared Back/Close/Cancel entries count once as action families, not once for every window.

| ID | Exact label / route | Behavior, choices, persistence and condition | Evidence |
| --- | --- | --- | --- |
| A01 | `Download Subtitles` in M01 | Launch dedicated downloader with streaming URI and optional title. Network/provider/account availability controls success, not visibility. | [E05] [E42] |
| A02 | `<language> · <filename>` result | Open review, even for exactly one result. No automatic one-result download in Preview UI. Empty values fall back `Unknown language` / `Subtitle file`. Dynamic choices are provider results. | [E43] |
| A03 | Review `Download` | Request download link, stage payload, save sidecar/cache, update index, RESULT_OK and return for subtitle re-enumeration. Does **not** return SELECTED_PATH or guarantee selected downloaded track; see section7/B06. | [E43] [E44] |
| A04 | Review `Back` | Close review, keep parent results/file-actions surface and restore focus where possible. No download/association performed. | [E45] |
| A05 | Operation `Cancel` | During finding/search/download: cancel operation/finish host. During mutation, waiting dialog is noncancelable with Cancel hidden. Cancel is distinct from programmatic dismiss. | [E46] |
| A06 | `Choose Subtitles` in M01 | List associated/unassociated subtitle files from media folder and saved cache. No arbitrary directory picker or user-selectable storage destination. Scheme restriction in section7. | [E05] [E47] |
| A07 | `<filename> · <source> [· Active]` in M07 | Open Subtitle Actions; check identifies active external file. Two groups, associated first. Source strings `Media folder` / `Saved subtitle cache`. | [E47] |
| A08 | `Use Subtitle` | Return selected associated external path; player applies through normal user track-selection/persistence or waits for re-enumeration. Only playback-selection intents. No file write. | [E47] [E42] |
| A09 | `Associate with This Title` | Review rename of an unassociated file; suffix ` — unavailable` if `canWrite` false. | [E47] |
| A10 | Review `Associate` | Rename subtitle to title-associated filename; maintain companion cache rename; then return/apply path in playback context. Persistent filesystem change, not merely a session label. | [E47] [E48] |
| A11 | `File Information` → `Subtitle File` | Read name/source/size/association and Active status; Close. No codec/language editor. | [E47] |
| A12 | `Delete Subtitle File` | Show destructive confirmation, unavailable if not writable. | [E47] |
| A13 | Confirmation `Delete` | Delete actual subtitle file and companion cache where applicable; reload list. Does not delete video. The audit did not execute it. | [E47] [E48] |
| A14 | Delete confirmation `Cancel` | Dismiss without mutation; initial focused button. | [E45] |
| A15 | `Subtitle Settings` | Start full VideoSettingsActivity with `preview_settings_category=Subtitles`. No `pauseForSettings()` call on this wrapper path, unlike filtered legacy Preferences action. Activity lifecycle still governs pause/resume. | [E04] [E38] |
| A16 | More → `Technical Information` | Show read-only snapshot of **active** metadata/backend, no new network metadata probe. M23 has no title text/button row; scroll and Back dismiss. See I05. | [E49] |
| A17 | More → `Report a Problem` | Logging on: category chooser. Logging off: explanatory reader; it does not enable logging automatically. | [E50] |
| A18 | `Playback`, `UI`, `Network`, `Other` | Choose category; write local diagnostic marker and latest reference. No server submission/email/issue creation. Four choices; latest reference persists. | [E50] |
| A19 | Reader/notice `Close` | Dismiss. Shared notice cases: listing/search failure, no subtitle files/results, offline etc. | [E45] [E46] |
| A20 | Reader `Copy` | Copy displayed reference/message body to Android clipboard; show `Copied`. Not present in the custom technical-information or Subtitle File notice window. | [E45] [E50] |
| A21 | Format → `Picture in Picture` | Enter Android PiP (API26 params / API24 legacy call). Added for TV API≥24; no check of OS PiP feature entitlement at row construction. Afterward generic format handler also runs (B05). | [E34] |
| A22 | `Skip <type>` | Five possible English labels: Skip Intro, Skip Recap, Skip Outro, Skip Credits, Skip Preview. Seek to safe provider end only while 5-second prompt exists; one action family. Not chapter navigation. | [E32] [E33] [E51] |
| A23 | Up Next → `Play Now` | Start resolved next local episode. Shown only Binge/next episode/last5s condition. No separate Cancel button. | [E51] |
| A24 | Startup `Yes` | Use network resume candidate when local/network positions conflict. Noncancelable alert; Yes initially focused. No persistent “always choose network” checkbox here. | [E52] |
| A25 | Startup `No` | Use local resume candidate in same dialog. | [E52] |
| A26 | No on-screen label: `Back` with Up Next focused | Cancel current auto-next transition; clear next URI, restore focus. Does not globally change Binge watching. | [E51] [E31] |

### 5.2 Indicator register

| ID | Exact information / location | Behavior and availability | Evidence |
| --- | --- | --- | --- |
| I01 | Title or official title artwork, upper left; episode context below | Uses physical title context/series episode text where available; episode label hidden when empty. Not a focusable metadata/settings control. | [E02] [E10] |
| I02 | Upper-right current clock | System 24h `HH:mm` or 12h `h:mm` format (no AM/PM in pattern). Not always 24h. | [E53] |
| I03 | Floating elapsed/seek-target pill | Always visible with HUD; current or preview target. **`MM:SS` below1h; `H:MM:SS` at/above1h**, not universally HH:MM:SS. Clamp pill inside track. No thumbnail or chapter title. | [E08] [E10] [E53] |
| I04 | `<total duration> · Movie Ends <clock>` / `Episode Ends <clock>` beside track | Total **duration**, not remaining duration. Episode wording based on nonempty episode context. Prediction now + max(0,duration−clamped position)/speed; preview target used during seek. Paused semantics: ends if resumed now, so predicted clock can advance while paused. Unknown duration shows em dash and no prediction. | [E53] [E54] |
| I05 | Technical Information: `Video`, `Audio`, `File`, `Source` columns | Video format, raw decoder value, resolution, positive bitrate, fps when scale>0; audio track numbers/language/format/channels; file size bytes; safe source type and numeric backend. Null data → Unknown/Not available. `describe()` generates subtitles but `show()` explicitly excludes that part: subtitle list is **not displayed in this panel**. No HDR mastering/full codec-info or chapter promise. | [E49] |
| I06 | `UP NEXT · Starting in <n>s`; next `S<n> E<n> · <title>` and cached still | Five-second countdown near end in Binge; deadline extended when paused. No movie equivalent or unconditional Next Episode HUD button. | [E51] |
| I07 | State/status feedback | Track ticks, unavailable suffix, Active file marker, subtitle operation progress/empty/error text, seek-state spinner, nonlocal buffered-progress fill, auto-skip toast, temporary brightness/volume/speed/seek-icon OSD. These are feedback, not separate settings. Group headings are noninteractive. | [E04] [E08] [E46] [E47] [E51] |

## 6. Remote, focus, long-press and dismissal map

This section is **source-confirmed routing**, not physical Shield verification. Android's actual dispatch/focus/window timing can differ; tests in section10 identify what has and has not been exercised.

| Context / input | Actual route and effect |
| --- | --- |
| Hidden HUD, D-pad Up/Down/Left/Right | On DOWN reveal HUD and explicitly focus seek bar, **then return true**. Initiating Left/Right is consumed; a short first press does not seek. A held key can reach later repeat handling after HUD appears. This differs from later preferred “same initiating input seeks” design. |
| Ordinary `showControlBar()` | Requests Play/Pause focus. Hidden-HUD directional handler subsequently moves it to seek. There is not one universal seek-first default for every show path. |
| Seek or Play/Pause → Up | Focus Subtitles. No current Up→Settings route. Upper row Left/Right: Subtitles ↔ Audio ↔ More; edge buttons self-loop. |
| Upper row → Down | Normally seek bar. If a Skip pill exists and focus is More, activity intercepts Down first to focus Skip instead. |
| Seek → Down; Play/Pause → Right | Seek Down → Play/Pause; Play/Pause Right → seek. Pause Left self-loops. Down from other controls moves to seek; Up at upper row is consumed. |
| Left/Right on a normal button | Returned to view focus/click handling, rather than seeking. Seek handling requires seek focus or a seek already in progress. |
| Left/Right on seek; dedicated Rewind/FF | Check direction capability; pause playing state for preview, generate target immediately; first repeat tick after400ms, subsequent ticks300ms. Steps: first3 advances10s, next3 30s, next3 60s, tenth+120s. Direction change or ≥1250ms gap resets escalation. This changes timeline target, not playback speed. |
| Release seek key | Commit one seek to final preview target; ignore opposite-direction release. Resume handled through seek completion/resume logic, respecting paused-before-seek state. No mandatory OK commit or dedicated Back rollback. Back during an active Preview seek only hides the HUD through the ordinary handler; key-up can still commit the pending target, so cancellation must not be promised. Unknown duration/relative seeking follows inherited incremental native-seek path instead. |
| OK/Enter on focused button | Normal click opens menu or toggles transport. No dedicated long-OK pop-up is installed on the four primary buttons. Repeated activation should be tested. |
| OK/Enter on seek / no focused button route | Controller key-UP toggles play/pause; not a chapter or “confirm seek” action. User play resumes and can hide HUD; button click path can show it with timeout instead. |
| Menu key | Build native menu if needed, open More via Preview wrapper. Up normally hits early focus routing; it is not an Up→More shortcut in the normal Preview HUD path. |
| Track/toggle menu | Up/Down choose rows; OK calls native action. Menu remains open and ticks update. Current chosen track is initial focus where possible; first enabled row otherwise. Selected tick and remote focus are separate. |
| Back from Other languages | Rebuild root Subtitle tracks and restore opening row. | 
| Nested adjustment Back/Escape/gamepad B | `TVCardDialog.handleBackPressed()` runs native result callback (including appearance/preset saves), then reconstructs exact parent. Back is apply-and-return, not cancel edits. Other-language parent restoration follows wrapper callbacks. |
| Back from direct Subtitles/Audio | Close to original HUD opener; should not introduce More as an extra parent. Back from More child restores More; Back from More closes to opener. Root remembers selected index. |
| Back with HUD visible and no pop-up | Hide HUD and consume; next Back with no overlays/HUD exits via activity dispatcher. Focused Up Next gets cancellation priority. A separate native dialog such as technical/readers consumes its own Back first. |
| Timeout | Playing HUD inactivity default **3000ms**. Pause timeout defaults0 (indefinite) but `hide_controls_on_pause=true` sets **5000ms** at activity creation. Subsequent visible-HUD key-down still schedules the normal3000ms timeout. Key-down in visible Preview HUD resets timer. Seek cancels fade while targeting. Preview menus are dialogs and do not themselves schedule a timeout; underlying HUD can fade. Exact opener restoration after fading needs runtime verification. |
| Subtitle delay / appearance picker | Left/Right adjust immediately; OK sign reversal if permitted. Held keys increase step at2s intervals to3×, then10× after3s,100× after6s; release restores initial step. Positive-only appearance fields cannot be negated. |
| Audio delay picker | Left/Right10ms fixed-step, no subtitle-style held-step acceleration. Applies after750ms without further change and on detach. OK reverses sign when within bounds. |
| Speed picker | Left/Right ±0.05 with limits; delayed750ms application and detach flush. OK tries sign reversal, but negative speed violates0.50 minimum, so it has no reverse-play effect. |
| Colour picker | Left/Right one swatch, Up/Down ±8 within palette; OK applies, moving highlight alone does not. |
| Skip pill | From More, Down enters. OK skips. Left/Right/Down returns prior focus. Up/Back are consumed while pill focused; prompt expires after5s and restores origin if still focused. |
| Up Next | Up enters Play Now without automatically stealing focus when panel first appears. OK starts next. Down returns HUD; Left/Right clamp to sole action. Back while focused cancels auto-next. |
| Volume keys on Android TV | Returned to framework when volume bar handling active, to let HDMI/CEC/ARC control sink. Not an in-HUD output-device or volume-boost slider. |

Evidence: [E07] [E08] [E09] [E10] [E22] [E28] [E55] [E56] [E51].

Additional source-supported keyboard/gamepad aliases, not extra HUD buttons: digits0–9 seek0–90% if both directions supported; I opens information; F/Green cycles Format; J/S/Yellow cycles subtitles; A/#/Blue cycles audio; Z/X adjust subtitle delay −/+100ms; O/Red toggles OSD; P/Space/headset/media Play-Pause toggles transport; media Play/Pause act explicitly on key-up; Stop pauses/finishes when playing; G/ChannelDown/MediaPrevious decreases playback speed; H/ChannelUp/MediaNext increases it when speed enabled. **Media Next/Previous do not select episodes in this key map.** D/U and */slash affect local volume where enabled. Joystick seeking exists as an inherited alternative. These are SC/CU, especially outside a standard Shield remote. [E55] [E56]

## 7. Conditional availability and state matrix

| Condition | Visible/enabled differences and behavior | Evidence |
| --- | --- | --- |
| Movie vs episode | Same four buttons and core menus. Episode context changes title/end-time wording; provider query includes season/episode. Binge Up Next only when a resolved adjacent episode exists. No chapter selector in either case. | [E10] [E31] [E42] [E51] [E53] |
| Local / USB (`file`) | Choose Subtitles available. Actual filesystem permissions/USB behavior determine writable association/delete/download. Download may save beside video; otherwise cache. Decoder/seek support still media-dependent. | [E05] [E44] [E47] |
| SMB (`smb*`), SSHJ, SFTP | Choose Subtitles available. File operations require source availability/write permission. Associated cache also maintained. | [E05] [E48] |
| HTTP/HTTPS/WebDAV / UPnP / other schemes | Same basic player; **Choose Subtitles row absent** unless normalized URI resolves to one of the listed schemes. WebDAV is not in the explicit chooser whitelist. Download Subtitles remains visible; search/download success depends on URI metadata/network/provider and save fallback. Do not infer a different HUD solely because source is network. | [E05] [E42] [E44] |
| No subtitle tracks | No None row, Delay or Appearance. Download remains; Choose if scheme eligible; Subtitle Settings remains. | [E05] [E11] |
| Text subtitle selected | Delay and six appearance fields available. Embedded ASS/SSA native rendering can retain format-specific styling; absence of a disabled flag is not evidence every native subtitle renderer obeys every text-style edit. | [E17] [E21] |
| Bitmap/graphical subtitle selected | Appearance disabled; vertical forced0. Delay remains possible. No GUI bitmap-format conversion, OCR or font controls. | [E21] |
| Subtitle None selected | Delay/Appearance displayed with unavailable suffix; cannot activate. Track choices still available. | [E05] [E04] |
| Preferred/English/unknown vs other language | Root partition uses favourite language and English match; unknown stays root. Rest under Other languages; selected track can therefore be in a child. This is presentation, not automatic track-selection policy. | [E05] |
| No audio | HUD Audio still enabled/half-alpha; native card hidden, menu empty; no filters/delay/speed construction. See B09. | [E06] [E10] |
| One audio track | Track still selectable; enhancement/sync rows still built. Not suppressed solely because only one track exists. | [E06] |
| Passthrough preference1/2 | Boost/Night disabled, speed omitted, spatialization disabled. If passthrough supported, delay max0. | [E06] [E25] [E29] |
| Passthrough preference3 (recoding) | Boost/Night permitted; speed omitted; spatialization disabled; delay max0 when passthrough supported. Rules are not identical across controls. | [E06] [E25] [E29] |
| Playback-speed flag false / MediaCodec audio decoder (`2`) | Speed action absent, getters return1.0. Speed action is discovered from audio card, so audio track count also matters. XML preference defaults **true**; predicate fallback for a genuinely absent/uninitialized key is false. | [E06] [E29] |
| Android/platform spatializer | API≥32 plus required caps and passthrough0 to enable toggle. A usual older Shield Android release must not be assumed to meet this. | [E25] |
| Standard vs unusual display aspect | Standard surface mode max7; auto mode max8 when no HDMI and `(videoW/videoH − screenW/screenH)>0.7`. Optimized label still created in both. Format arrays independent of movie/TV. | [E35] |
| TV API≥24 | PiP row constructed. OS support, manifest/platform restrictions and actual transition unverified. | [E34] |
| Stereo/split renderer | 3D card only if existing stereo/forced mode; split-view containers may bypass Preview modal route and use retained TV card system. Not an everyday Shield 3D switch. | [E03] [E36] |
| Non-Leanback TV-style interface | Notifications menu added. Classic non-Preview interfaces have additional legacy controls; not current Foundation Shield HUD. | [E37] [E02] |
| Playing/paused/seeking | Pause label/timer and preview target change; predicted finish uses speed. Seeking pauses decoder then commits on release. Back hides before exit. | [E07] [E08] [E53] |
| Resume/new start/re-enumeration | Saved per-file choices validated against available tracks; language mismatch can reset subtitle selection. Fresh start resets session segment override and reloads global speed/delay. Re-enumeration refreshes displayed callbacks/ticks, rebuilding list when shape changes. | [E04] [E12] [E24] |
| Network resume conflicts | Startup alert only when eligible network candidate differs and both positions>0; network-only candidate can be used without asking; explicit launch position has precedence, user pause suppresses conflict prompt. No new HUD menu setting. | [E52] [E57] |
| Segment/provider data | Only scraped usable identity can fetch; prompt/auto behavior requires safe concrete end before EOF−5s, URI match and selected per-type policy. Prompt lasts5s. Session toggle can show while no segments exist. | [E32] [E33] |
| Last5s, Binge, next episode | Up Next available; countdown freezes effectively while paused; focus never automatically stolen. At zero, navigate next. Back-focused cancellation clears next transition. | [E31] [E51] |
| OpenSubtitles disabled/offline/credentials/quota | Download entry remains. Disabled integration rejects manual discovery; offline/error/quota leads notice. Saved credentials/API configuration required for authenticated download behavior; actual live service not tested. | [E40] [E41] [E44] |
| Touch/pointer input | Gesture callbacks are attached in current outer controller even on TV; left vertical brightness/right volume and double-tap seek/speed have no popup opener. Input dispatch/effect on Shield is unverified. | [E64] [E65] |
| Private Mode | Speculative subtitle preparation disabled; explicit manual search bypasses private persistent cache and uses provider directly. Do not assume every global appearance/preset edit becomes ephemeral in Private Mode. | [E41] |

### Subtitle defaults and per-file persistence

Audio selection is not simply “track1”: service consults stored valid choice, optional original-language preference, favourite-language default/variant matches, supported tracks and backend outcome. Subtitle defaults consider saved index/language validation, hide-default, audio-vs-locale, forced tracks, untagged external text, favourite/default/Chinese variant and foreign-audio English fallback. “Subtitles by Default off” can still select forced subtitles. UI grouping into Other languages is an independent rule. [E12] [E23]

The frontend writes audio/subtitle selections and timing through `PlayerService.persistVideoInfoFromFrontend` and pinned MediaLib `IndexHelper.writeVideoInfo`. The latter snapshots the object and writes signed subtitle delay/ratio and packed tracks asynchronously; compatible network XML export additionally requires its preference, nonlocal URI and positive duration. **Do not misdiagnose negative subtitle delay as never persisted by citing the older `VideoDbInfo.update()` guard:** that is not this write path. [E14] [E58]

Native `VideoDbInfo.fromCursor` only reloads packed audio/subtitle choices when `lastTimePlayed>0`; unindexed URI id−1 does not receive a native row update. 0.139 also has explicit restored-title overrides, validates stream choices and allows later manual choice to supersede restoration. Thus persistence is **source-confirmed intent for indexed playback**, not proof that every no-history, new external URL, changed-track-order or restored-backup case behaves identically. These remain QA cases. [E58] [E59]

### Online subtitle workflow and boundaries

The provider is OpenSubtitles REST via existing API helper, not a new integration. Player passes streaming URI and optional title. Downloader reads saved credentials, attempts login and uses saved download languages; warns on missing/invalid credentials/service failures. Query can include file identity/hash, friendly filename, online IDs and episode information. `SubtitleDiscovery.manual` can use private persistent cache/backoff, except Private Mode. Search results show language+filename; review additionally shows OpenSubtitles source, optional file-hash match and release. No visible ratings, vote counts, hearing-impaired filter, format filter, manual query editor, manual season/episode controls, result sort menu, account-login form or download-destination selector exists in this workflow. These must not be invented for a unified panel. [E40] [E41] [E42] [E43]

Download always needs explicit result review and confirmation in Preview, even with one result. It obtains link/quota status, stages network response before opening destination, writes `<video basename>.<language>.srt` beside writable supported non-slow-remote media or in `MediaUtils.getSubsDir`; writable remote copy is also cached locally. Storage is automatic. Explicit online download can replace the existing same-name subtitle; there is no overwrite prompt in this path. No download was performed in this audit. On success RESULT_OK requests track re-enumeration, **not explicit selection of that downloaded file**; only the local chooser returns selected-path intent. [E44] [E42]

### Local subtitle workflow and boundaries

The current chooser is a title-relative listing over existing subtitle discovery/association, not a general “browse any file anywhere” picker. Existing associated files and other subtitle files are separated; saved cache and media folder are identified. File Information works read-only. Use applies an already-associated file; Associate reviews and then renames an unassociated file to associate it, potentially returning it for playback. Delete requires confirmation and writes to real storage. Unwritable operations remain visibly unavailable. Finding can cancel; backend rename/delete uses a noncancelable waiting dialog because it cannot safely advertise cancellation. Failure and empty lists have explicit notices. [E47] [E48] [E46]

## 8. Related global controls, duplicates and unreachable code

The following are dependencies or neighboring app configuration, **not extra controls in the counted playback pop-ups**. The full Settings activity can be navigated beyond its preselected Subtitles category; this report does not recursively reclassify the entire app as HUD.

| Related app setting | Choices/default or effect | Relationship |
| --- | --- | --- |
| Playback speed enable flag (`playback_speed`) | Boolean **XML default true**; predicate fallback false if key absent. | Controls whether More has speed. Distinct from S16 value. [E29] |
| Favourite audio language / prefer original audio | Same language catalogue as subtitle languages; original-audio Boolean default false. | Default-selection policy, not Audio popup choices. [E23] [E39] |
| Audio passthrough (`force_audio_passthrough_multiple`) | Saved numeric modes0–3, default0 in player reads. | 0 disabled,1/2 passthrough,3 recoding; restrictions in matrix. Configured mode gates UI even when a sink is not actually carrying passthrough. [E06] [E25] |
| Audio decoder choice | Native/default `1` vs MediaCodec `2`; additional preference presentation determined in full Settings. | `2` removes speed. No decoder chooser in HUD. [E29] |
| Advanced → Subtitle Compatibility → `Subtitles encoding` | 16 codepage values: Default(0), Eastern European1250, Cyrillic1251, Western European1252, Eastern European28606, Greek1253, Turkish1254, Hebrew1255, Arabic1256, Baltic1257, Vietnamese1258, Thai874, Simplified Chinese936, Traditional Chinese950, Japanese932, Korean949. Global, default0. Exact English resource labels enumerated in Appendix B. | `codepage` moved out of Subtitles category into Advanced; **not** an Appearance control or direct nested HUD entry. [E38] [E60] |
| Integrations → IntroDB | Master true; five per-type modes NORMAL/PROMPT/AUTO/SMART. Intro default PROMPT; Recap/Preview SMART; Outro/Credits AUTO. Display choices Play Normally / Show Skip (5 seconds) / Auto Skip (Safe Auto Skip for Credits) / Smart. | Persistent per-type policies live outside HUD; S19 only overrides session enabled state. SMART auto-skips recap on Binge arrival; other SMART cases play normally in present policy. [E32] [E38] |
| Integrations → OpenSubtitles | Enable Boolean true; account/credential/status management outside player. | Manual search respects integration flag; credential input is not embedded in result review. [E38] [E41] |
| Track Watch History / Remember Playback Position; network bookmarks | Independent global choices. | Affect history/resume storage; not per-track checkbox controls. [E38] [E57] |
| Hide controls on pause | Boolean controlling `pauseTimeout`. | Current transport auto-hide behavior, not a More setting. [E07] |
| Accent & Colour | Existing global focus/style configuration. | Wrapper uses shared focus surfaces; old colour-restoration QA remains a regression case. Not subtitle text colour S05. [E38] [E45] |

**UO register — do not count as current Shield HUD controls:**

| Code/resource lead | Actual reachability |
| --- | --- |
| Hidden `preview_info`, `preview_speed`, Previous/Next, backward/forward, Format and Fullscreen/cutout image buttons in experimental XML | GONE/nonfocusable; some listeners remain installed. Speed/info live via secondary menu; hidden buttons do not add primary controls. [E02] [E10] |
| Legacy TV `Info`, `Preferences`, Audio/Subtitles cards in More | Explicitly filtered by Preview root. `Technical Information` is the replacement information action. The Subtitle Settings escape remains separately reachable. [E04] |
| Sleep timer method and picker0…24h,1-minute increments | TV menu construction is inside a comment; no current More path. Non-TV legacy options must not be promoted into Shield menu. [E61] |
| Anaglyph resource/enum | Not in TV 3D array, which contains only3 choices. [E36] |
| SubtitleSettingsDialog / non-TV action-bar menu, brightness dialog, floating player, touch volume/brightness gestures, rotation lock/projector/cutout controls | Retained alternative interfaces and/or app configuration. No normal Foundation TV HUD **menu opener** to these was found; touch brightness/volume gesture paths are separately source-confirmed as S28/S29, not obsolete. Hardware/keyboard volume routing is mapped separately. [E02] [E37] [E55] |
| `PreviewPlaybackInfo` vs `PreviewTechnicalInfo` | Active `showVideoInfos()` uses `PreviewTechnicalInfo`. Other helper content is not evidence of displayed settings. [E49] |
| IntroDB summary line in Play mode | `refreshPlayModeIntroSummary()` returns immediately for Foundation. Do not promise interval summary in current Play mode. [E30] |
| Chapters / thumbnail preview | No reachable chapter enumeration/selector, next/previous chapter action, chapter markers/titles or trick-play thumbnail UI found in player source/resources. Chapter icon keyword and “The next chapter begins” sample text are not implementations. This is bounded negative source evidence, not a claim no container/native decoder could contain chapter metadata. |
| HDR tone mapping, motion smoothing, sharpness, contrast, rotation, video-track picker, deinterlace, colour-space chooser, A–B Repeat, Skip Silence, output-device picker | No current popup path/implementation found for those listed features. A conditional window-brightness gesture does exist as S28; that does not establish a video-processing brightness setting. Audio Boost, Night Mode and spatialization **are** source-supported, despite earlier mock-up caution lists. No mock-up is evidence of functionality. |
| Bookmark/watchlist/Trakt controls | Bookmark method and external/legacy paths exist, but no bookmark/watchlist/Trakt action in current Preview playback root. Trakt service integration can run without an in-HUD Trakt button. [E37] [E57] |

## 9. Confirmed bugs, duplication and limitations

“Confirmed” here means code establishes the mismatch or behavior; it is **not physical Shield reproduction**. Findings are documented only, without fixes or scope expansion.

| ID | Finding | Confidence / impact / next verification |
| --- | --- | --- |
| B01 | **Full Settings escape remains:** Subtitle Settings starts VideoSettingsActivity. Earlier QA/design said no HUD links to full app Settings. More correctly filters Preferences, so filtering one path did not remove this one. | SC design/implementation mismatch. Verify pause/resume and focus on return. [E04] [E38] |
| B02 | **Optimized Format can lie about selection:** ordinary format controller max7 accepts indices0–6. Menu still contains index7 Optimized; choosing7 resets actual format0 and callback then ticks selected menu row. | SC reproducible by control-flow inspection. Check standard Shield wide-video/screen case and auto-mode threshold. [E34] [E35] |
| B03 | **Appearance raw-step quantization:** size and opacity initial1/100 increments; position10/100; integer callbacks often apply/display unchanged value across many presses. From opacity128, one Right gives12801/100=128; one Left gives12799/100=127. This also creates asymmetric first effective step. | SC arithmetic/usability defect. Held acceleration masks it; test short/repeated taps, long hold and end limits. No claim of UI crash. [E17] [E15] |
| B04 | **Keep sentinel limitations:** audio delay0 and speed1.0 imply unchecked on reopen, so explicit “keep neutral value” is not representable as a retained checked flag. Unchecking changes saved next-start default, not necessarily current playback value. | SC behavior inherited from NOVA; explain rather than silently change. [E26] [E27] |
| B05 | **PiP falls through to Format selection:** after PiP branch, generic `if(v instanceof TVMenuItem)` still calls setVideoFormat on the PiP row index and ticks it. Index is beyond format values, so resets Original. | SC side-effect defect; successful PiP entry and display support still CU. [E34] [E35] |
| B06 | **Download success is not explicit apply:** downloader returns only RESULT_OK; player calls `checkSubtitles()`. A still-valid selected track may remain selected. Local Use/Associate has explicit SELECTED_PATH apply logic. | SC workflow limitation; test None, existing embedded/external choice, one/multiple downloads. Do not describe download as guaranteed “download and activate”. [E42] [E44] |
| B07 | **Palette cursor ignores saved colour:** `mCurrentlySelectedColor=0`; dialog does not initialize cursor from current colour. Reopening and pressing OK on initial palette selection can apply white. | SC limitation; selected-vs-focused clarity needs runtime check. [E20] |
| B08 | **Appearance persistence depends on exit path:** Back native card invokes result and saves6 values. `close()`/stop directly dismiss current dialog without result callback. Live renderer changes and saved prefs may diverge on programmatic stop/lifecycle close. | SC missing callback path; actual recreation/exit effect CU. Save semantics must be tested, not assumed from “Back saves.” [E17] [E22] |
| B09 | **No-audio opener remains enabled:** native card hidden/empty; HUD Audio still clickable at half-alpha; wrapper selects matching card without visibility filtering and empty selection invokes card previewClick. No explicit “No audio tracks” notice in that path. | SC affordance limitation; exact empty-click visual outcome CU. [E04] [E06] [E10] |
| B10 | **Finish/elapsed text differs from future design:** current displays total duration, Movie/Episode Ends and variable-width clock/time formatting. Future FD-002 requested remaining duration, generic Ends at, HH:MM:SS and same-input seeking. | SC baseline/design distinction, not authority to change it. [E53] [D01] |
| B11 | **Conditional controls not fully capability-gated:** PiP checks TV/API not system feature; Format includes Optimized regardless max; speed gate is preference/decoder-based, not proof backend applies speed; audio track row does not guarantee supported codec. | SC limits; exact devices/output behavior CU. [E06] [E23] [E29] [E34] |
| B12 | **Technical panel omits subtitle data:** describe builds SUBTITLES block but show skips it. No copying/export action in this custom panel. | SC display limitation, not assumption that decoder lacks subtitle metadata. [E49] |
| B13 | **Existing same-name subtitle can be overwritten by explicit download**, without overwrite confirmation. Staging protects failure before destination opening but is not an overwrite-choice UI. | SC limitation; do not test against owner's files without a disposable fixture. [E44] |
| B14 | **Right double-tap checks backward capability:** +10s gesture branch uses `canSeekBackward()` rather than `canSeekForward()`. | SC conditional-input defect; relevant to asymmetric seek capability, not proof of a D-pad seeking defect. [E64] |

Duplicates resolved/preserved: audio speed exists in native Audio menu but wrapper suppresses that row and promotes it to More, so it is **one field**. Other languages shares S01, not a second subtitle setting. “Subtitle Appearance” opens native title “Settings”, while “Subtitle Settings” launches full app Settings: **two different destinations**, not duplicates. Same text “Keep this setting globally” represents two independent saved values. Same track appears in information and selection contexts without creating an extra setting.

Historical launch defect: original 0.139 could throw formatter NPE when the HUD initialized known duration before `mFormatBuilder`; corrective `82b4862f` initializes formatter first. Frozen final candidate includes this correction. Earlier software-TV playback passed that crash point, but long copied-loop fixture stalled and physical Shield attribution/retest remains open. Do not report the original formatter crash as still confirmed in final source, or the software test as Shield acceptance. [E01] [E62]

## 10. Verification performed and outstanding Shield QA

### This audit

- Fresh isolated repository clone and detached inspection; no edits to application `src/`, `res/`, configuration, CI, signing or artifacts. No merge, cherry-pick, APK build, install, emulator provisioning, release or external provider action.
- Verified application source/resources identical at delivered `a4841c1d…` and observed implementation-branch `d173e8dd…`.
- Read every TV menu construction branch, wrapper filtering/renaming, native nested pickers, callback/persistence routing, track defaults, subtitle downloader/results/chooser, information/report flows, remote dispatch/Back/seek and overlay conditions.
- Compared against pinned NOVA Video commit `0651a3e0b60ada996a9774ab527d7c1ef34b7503`, not latest upstream as a substitute for actual functionality.
- Read pinned MediaLib `5758074049bc153ac4967b4ecd21886469270076` IndexHelper/VideoDbInfo and repository patch composition for persistence. No supporting dependency was changed.
- Ran existing `python -m unittest discover -s tools -p 'test_*.py'`: **81 passed, 0 failures, 18.780s**. These are repository/packaging/source guards and helper tests; they are not 81 live HUD interaction tests.
- Workspace inspection found **no adb, Android emulator or javac command**. Android/Robolectric tests were read, not rerun; no physical Shield was attached or used. No new runtime-verified HUD control is claimed.

### Existing evidence reviewed, not re-executed

| Evidence | What it supports | Limit |
| --- | --- | --- |
| `PreviewPlaybackMenusTest` | Existing tests for real dialog callbacks, direct-speed/native dismissal, compact bounds, track refresh/ticks/focus, new-track focus, subtitle icons/download-label action and direct Audio Back. | Source tests with mocked cards/native player dependencies; not hardware tests. [T01] |
| `Supernova138PlaybackTest`, seek/adjustment/track/UpNext tests | Expected geometry/pill/timing/focus/seek/value/selection policies. | Read-only test review; no rerun here. Formatter-injecting old fixture did not catch cold-init ordering. [T02] |
| Final corrective signed CI record | Historical 1,014 tests/211 classes, 81 Python guards, lint0 errors,1772 warnings at final candidate. | Repository receipt, not rerun; not a completed whole-source/device acceptance matrix. [E01] |
| Final corrective runtime receipt | **Historical RV**, API28/x86 software Android TV: owned H264/AAC20s fixture, advancing output, inspected frame16.917/frame406, normal completion19458ms, no formatter exception. | No physical Shield, no audible-output proof, long60s copied-loop stalled at0.083s; cause unresolved. [E62] |
| Earlier 4.1.7 physical QA / 0.137 HUD notes | Owner-recorded design/QA history, focus rectangle and timing refinement leads. | Older versions/screens; not current-candidate acceptance and no claim this audit used Shield. [D02] [D03] |

**Verification totals:** 60 source-confirmed control/setting/action families, 7 source-confirmed indicator families; **0 newly runtime-verified**, all67 require live condition-specific acceptance. UO and related global dependencies are separate registers and are excluded from that total. Historical RV establishes controlled playback output, not every menu/action in this inventory. No missing runtime result has been filled in from an expectation or screenshot mock-up.

### Practical physical NVIDIA Shield checklist

Use disposable owned test media, a temporary subtitle directory and recorded app/source/version. Record Shield model, Android version, display resolution/refresh, AVR/soundbar, HDMI/CEC/passthrough, subtitle/audio format and actual source URI scheme (redact credentials). Mark each case PASS/FAIL/NOT RUN, attach screenshot/video/current log and selected-versus-applied values. Never use owner media for delete/overwrite testing.

- [ ] **Q01 Baseline:** confirm installed candidate source/version and cold launch from Movie and Episode Details; no formatter crash; first output advances; Back returns originating title/focus. Repeat after force-stop and activity recreation.
- [ ] **Q02 Layout:** four buttons plus seek, no hidden legacy buttons exposed; test720p/1080p/4K, long localized labels, long title/logo, 4:3 and wide video; pill boundaries, total/end-time/clock and safe-area visibility.
- [ ] **Q03 Hidden input:** tap each D-pad direction with HUD hidden; record first Left/Right consumption; hold direction; verify exact focus after ordinary show, menu return and pause. Compare to intended future behavior without changing baseline.
- [ ] **Q04 Seeking:** tap/hold/reverse/release, minimum/EOF clamps, rapid direction changes, lost key-up, paused-before-seek, unknown/zero duration and nonseekable stream. Confirm native seeks on release and playback speed unchanged. Test network buffering without ANR.
- [ ] **Q05 Back/timeouts:** nested→parent→HUD→hide→exit; repeat after >3s idle while each pop-up remains open. Check opener restoration and no accidental play/pause on dismissed-dialog key-up. Test repeated OK/held OK, Menu, dedicated media keys.
- [ ] **Q06 Subtitle list:** zero/one/many tracks, preferred+English+other+unknown, selected Other-language track, embedded/external, HI/SDH/forced/default, long names. New sidecar re-enumeration must preserve callback, tick and useful focus.
- [ ] **Q07 Track defaults:** fresh start/resume, locale-vs-foreign audio, hide-default/forced fallback, original audio preference, reordered/removed tracks, changed external language, history disabled, restored-title overrides, unindexed external URL. Verify selected and actually rendered track separately.
- [ ] **Q08 Subtitle timing:** ±1ms tap/hold limits, OK sign reversal, all3 ratios, negative delay save/restart and network XML policy. Verify text and bitmap behavior; unknown-duration bounds.
- [ ] **Q09 Appearance:** all16 swatches, size10/30/100, position0/255, outline/background combinations, opacity0/128/255. Record raw-step no-ops and palette reopen cursor. Test bitmap disabled, ASS/SSA native style precedence, Back saves vs stop/recreate/programmatic exit.
- [ ] **Q10 Online:** valid/empty/wrong credentials, integration disabled, offline, no/one/many results, quota/rate limit, review Back, Cancel search/download, restart after error. No download before confirmation; test actual result fields, basename association, auto cache/sidecar storage and selected track after return.
- [ ] **Q11 Storage:** USB exFAT/NTFS at root/deeper folder, read-only source, local folder, SMB/SFTP/SSHJ and WebDAV. Verify chooser whitelist, listing failure/empty message, cache deduplication and source icons. Test association/delete/overwrite only disposable subtitle fixtures; video bytes unchanged.
- [ ] **Q12 Audio:** zero/one/many tracks, unsupported codec, successful/failing switch, channel labels and saved selection across sessions. Observe no-audio affordance and focus.
- [ ] **Q13 Filters/output:** verify Boost/Night effect on PCM, configured passthrough0/1/2/3 and actual sink behavior; positive/negative audio delay restrictions, preset sentinel0; spatializer unavailable/available API32+ where practical. Do not tick unavailable modes as hardware passes.
- [ ] **Q14 Speed:** flag off/on, MediaCodec audio vs native, passthrough modes, all31 values/endpoints,750ms apply, Keep1.0/nonneutral persistence, exit before delay expires and real AV sync/end-clock effect. Test media Next/Previous aliases changing speed rather than episode.
- [ ] **Q15 Format/PiP:** all8 labels and actual applied format/ticks, ordinary vs auto-format threshold, persistent restore. Confirm Optimized fallback and PiP format-reset findings, OS-supported/unsupported PiP and return focus/playback.
- [ ] **Q16 Play modes:** Single, Folder, Repeat single, Repeat folder and Binge across local/network folder/episode order, missing next and season boundary. Check repeat vs completion/history accounting; movie Binge without fabricated episode controls.
- [ ] **Q17 Segments:** all5 segment types and NORMAL/PROMPT/AUTO/SMART, provider absent/offline, invalid/end-of-file boundaries,5s prompt expiry, More→Down→Skip route, Back while Skip focused, session override reset/new title/auto next. Preserve post-credit content.
- [ ] **Q18 Up Next:** last5s countdown, pause/resume, Play Now, focus entry Up/exit Down, focused Back cancellation, natural completion after cancellation, no next episode, repeat modes and cached still unavailable.
- [ ] **Q19 Information/report:** snapshot reflects active file/backend without network re-probe; empty metadata, scroll and Back; report4 categories, logging off, reference copy/QR, no unintended upload. Test return after HUD faded.
- [ ] **Q20 Settings escape/resume:** Subtitle Settings full-activity route and Back, five global fields, reading-language enabled state after default switch, locale/download-language normalization, persistent preparation off/season toggle; network-vs-local Yes/No, explicit0/positive caller position and user-paused conflict suppression.
- [ ] **Q21 Conditional legacy:** only on suitable device/fixture, stereo/split3D three values and non-Leanback Notifications; label unavailable hardware conditions NOT RUN rather than importing them into Shield baseline.

- [ ] **Q22 Conditional touch:** with suitable pointer/touch input, verify central/outer taps,10s double-tap zones, bottom speed zones and vertical brightness/volume. Check real HDMI effect, current-value OSD vs applied value, bounds, persisted/reset brightness, fixed-volume sink, and B14 asymmetric capability. Do not add touch-only controls to the remote popup by assumption.

## 11. NOVA inheritance and Supernova changes

Pinned build manifest records NOVA Video `0651a3e0…`, customized MediaLib `57580740…`, FileCore `c4b760c…` and native AVOS `2cf21c48…`, with repository patches. Comparing the actual pinned Video source confirms the following attribution; no inference from branding or a latest-NOVA feature list is required. [E63]

| Area | Inherited from pinned NOVA | Supernova modification / boundary |
| --- | --- | --- |
| Player engine/service, native TV cards, tracks, signed timing and subtitle ratio | Existing Player/Activity/Controller, TV menu callbacks, metadata and output path. | Wrapper preserves underlying actions; title restoration/diagnostics and backup-intent ownership additions alter lifecycle/persistence around them. |
| Audio Boost/Night/spatialization, audio delay/speed with Keep | Present in pinned upstream PlayerActivity, including capability/passthrough checks and saved-value sentinels. | Compact presentation and value formatting, audio grouping, speed moved to More in Preview. They are not newly invented Supernova features. |
| Subtitle size/colour/position/outline/background/opacity | Six settings and native picker logic already upstream, including raw×100 scheme. | “Subtitle Appearance” label, section organization, Other languages partition, persistent subtitle preparation/search result/local chooser presentation, explicit chooser apply routing. |
| Format/3D/PiP/Play mode/Notifications | Underlying menus/arrays/actions inherited. | More root filters cards, adds headings, promotes speed; new session segment policy/Skip/Up Next behavior. Existing Format/PiP control-flow limitations remain. |
| HUD layout | Classic TV/phone controllers and remote mechanics inherited. | Experimental layout, four visible buttons, persistent bubble, lower inline total/contextual end time, top clock, title artwork, custom focus graph and accelerated preview-key seek. Cold formatter-init correction included in final139. |
| Technical info and diagnostics | Legacy Info activity available upstream. | In-player snapshot panel, safe source type, local Report a Problem marker; no top-level Info button. |
| Online/local subtitle backend | OpenSubtitles, association/cache/backend discovery inherited. | Preview result review/explicit confirm, staged transfer safety, shared chooser actions and retained-focus surfaces; private paced SubtitleDiscovery cache. |

## 12. Previous decisions and preliminary regrouping recommendation

This audit is not final arrangement approval. History is chronological, and older wording conflicts:

1. 4.1.7 QA/design specified Subtitles · Audio · centred Play/Pause · More, removal of Up→More and full Settings links, submenu simplification still undecided. [D02]
2. 10 October0.135 approved visual spec specified Play/Pause/Subtitles/Audio/More in one centred row, resolving extra primary Info/Settings buttons. It did not approve dropping functionality. [D04]
3. 12 October HUD refinements moved Subtitles/Audio/More above the seek line at left and aligned Play/Pause/track/total+contextual end-time, persistent pill and top-only clock. Current XML/logic follows that lineage. [D03]
4. Latest FD-002 records14 October provisional four **Audio | Subtitles | Video | More** tabs, two-column cinematic panel with inline controls, single-focus treatment. Illustrative controls are not approved inventory. It also records a preferred no-transport-button HUD and later source-informed seeking decisions; these are **future design direction, not current implementation facts or authority to alter0.140**. Owner declined future playback technical panel, while **current code still has it**, so do not silently omit it from audit/migration review. Online subtitle-search design remains deferred pending source audit. [D01]
5. The0.140 handover explicitly excludes Playback HUD/Settings redesign. This documentation does not change that exclusion. [D05]

A single popup can logically preserve the current fields as follows. Recommendations use confirmed functionality; they do not populate Video with speculative filters.

| Proposed group | Existing fields/actions that fit | Decisions/conditions to preserve |
| --- | --- | --- |
| **Audio** | S10 tracks; S14 delay; S15 Keep; S11 Boost; S12 Night; S13 spatialization. | Keep capability/unavailable feedback and separate saved preset intent. No output-device selector. Speed can fit More consistently with present wrapper; final tab assignment belongs to owner. |
| **Subtitles** | S01 tracks incl None/other languages; S02 delay; S03 timing conversion; S04–09 inline appearance; A01–14 download/local workflows. | Keep six appearance fields, bitmap/None restrictions, all language tracks, destructive file confirmation, actual sidecar/cache behavior. S23–27 are global configuration and need explicit distinction before embedding them. Online search layout is deferred. |
| **Video** | S20 Format, conditional S21 3D; A21 PiP if retained here. | This is a genuine but small Video category. Do not add HDR/contrast/sharpness/chapter/video-track controls to fill space. S28 is a conditional touch window adjustment; any new Shield popup exposure needs explicit review. Disabled/conditional/empty behavior needs owner review after QA. |
| **More / Playback** | S16 speed+S17 Keep; S18 Play mode; S19 segment override; A17–20 local report workflow; conditional S22 Notifications outside Shield. | Technical Information A16 is current functionality but future owner preference is removal; require explicit reviewed disposition rather than silently losing it. Reporting is local marking, not a network support submission. |

**Keep immediately accessible:** seeking/target feedback and direct transport through remote Play/Pause; temporary Skip and Up Next/Play Now belong to their timed playback context. A visible Play/Pause button is an engineering recommendation for discoverability/accessibility, **not a reversal of the owner's later preferred no-button future design**. Owner must resolve that visible-button choice separately. Preserve remote transport even if future visual design omits its button. Conditional touch brightness/volume and their aliases should retain their input-specific behavior; adding them to a Shield popup requires a separate decision based on actual effect/capability. Clock, position, duration/finish feedback are information, not popup settings. Embedded chapter navigation and thumbnails need separate feasibility/approval work because they are absent from the current HUD.

Before final arrangement, review every S/A ID against the proposed page, including conditional/unavailable rows and exits into app Settings. Do not combine subtitle timing speed with playback speed; do not merge the two Keep presets; do not turn automatic subtitle destination into an invented setting; do not discard file association/delete or off/forced subtitle behavior. A unified popup should reduce nesting without changing these operations until separately approved.

## Appendix A. Complete subtitle language-choice rule

For S23/S24, `VideoPreferencesCommon.buildLanguageList` inserts current system language first, English second if different, then localized labels from the code catalogue sorted case-insensitively by display name. It skips names equal to locale/English; TreeMap collapses identical display names. The exact displayed count/order therefore depends on locale/resources. Labels use `ISO639codes.getLanguageNameFor2LetterCode(context,code)` and `Locale` display names; do not substitute English names for a device using another language. The complete **102-code** hard-coded provider catalogue is below; system locale can add an otherwise absent code. [E39]

```text
ab af an ar as at az be bg bn br bs ca cs cy da de ea el en eo es et eu ex fa fi fr
 ga gd gl he hi hr hu hy ia id ig is it ja ka kk km kn ko ku lb lt lv ma me mk ml
 mn mr ms my ne nl no nv oc or pl pm pr ps pt-br pt-pt ro ru sd se si sk sl so sp
 sq sr sv sw sx sy ta te th tk tl tp tr tt uk ur uz vi ze zh-cn zh-tw zh-ca
```

This is not the list of actual playback tracks; S01/S10 use media metadata. S24 can represent combinations of these language values, not only one choice. The inline editor may temporarily allow an empty selection, but initialization refills locale when empty.

## Appendix B. Subtitle-encoding neighboring setting

This is outside the counted direct playback fields because `codepage` is moved to **Advanced → Subtitle Compatibility**. English labels/values come from arrays/strings, not invented choices: [E60]

| Displayed English label | Stored value |
| --- | --- |
| Device language %s | `0` |
| Eastern European (CP1250) | `1250` |
| Cyrillic (CP1251) | `1251` |
| Western European (CP1252) | `1252` |
| Southern European (CP28606) | `28606` |
| Greek (CP1253) | `1253` |
| Turkish (CP1254) | `1254` |
| Hebrew (CP1255) | `1255` |
| Arabic (CP1256) | `1256` |
| Baltic (CP1257) | `1257` |
| Vietnamese (CP1258) | `1258` |
| Thai (CP874) | `874` |
| Simplified Chinese (CP936, GB2312) | `936` |
| Traditional Chinese (Big5) | `950` |
| Japanese (Shift-JIS, EUC-JP) | `932` |
| Korean (CP949, EUC-KR) | `949` |

## Appendix C. Source evidence index

All E/T references below are immutable GitHub code/evidence links. Ranges identify the relevant construction/handler, not a claim a method's comments alone prove behavior. D references point to the separately pinned planning records.

| Evidence | File and lines (immutable snapshot) |
| --- | --- |
| E01 | [docs/0.139/CONTINUATION.md][E01] |
| E02 | [res/layout/player_controller_experimental.xml:1–51][E02] |
| E03 | [src/main/java/com/archos/mediacenter/video/player/PlayerController.java:666–768][E03] |
| E04 | [src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java:19–135][E04] |
| E05 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:2031–2111][E05] |
| E06 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:2114–2189][E06] |
| E07 | [src/main/java/com/archos/mediacenter/video/player/PlayerController.java:1458–1477][E07] |
| E08 | [src/main/java/com/archos/mediacenter/video/player/PlayerController.java:1623–1798][E08] |
| E09 | [src/main/java/com/archos/mediacenter/video/player/PreviewSeekPolicy.java:1–17][E09] |
| E10 | [src/main/java/com/archos/mediacenter/video/player/PlayerController.java:1497–1515][E10] |
| E11 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:3898–3957][E11] |
| E12 | [src/main/java/com/archos/mediacenter/video/player/PlayerService.java:2173–2416][E12] |
| E13 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:1678–1732][E13] |
| E14 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:3643–3670][E14] |
| E15 | [src/main/java/com/archos/mediacenter/video/player/tvmenu/SubtitleDelayTVPicker.java:58–252][E15] |
| E16 | [src/main/java/com/archos/mediacenter/video/player/Player.java:922–945][E16] |
| E17 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:1734–1875][E17] |
| E18 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:4411–4468][E18] |
| E19 | [res/values-sw500dp-land-notouch/config.xml:1–4][E19] |
| E20 | [src/main/java/com/archos/mediacenter/video/player/SubtitleColorPicker.java:43–164][E20] |
| E21 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:3841–3890][E21] |
| E22 | [src/main/java/com/archos/mediacenter/video/player/tvmenu/TVCardDialog.java:131–181][E22] |
| E23 | [src/main/java/com/archos/mediacenter/video/player/PlayerService.java:2027–2169][E23] |
| E24 | [src/main/java/com/archos/mediacenter/video/player/PlayerService.java:2767–2871][E24] |
| E25 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:487–576][E25] |
| E26 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:1877–1957][E26] |
| E27 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:1959–2020][E27] |
| E28 | [src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioSpeedTVPicker.java:138–230][E28] |
| E29 | [src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java:559–571][E29] |
| E30 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:2370–2406][E30] |
| E31 | [src/main/java/com/archos/mediacenter/video/player/PlayerService.java:418–456][E31] |
| E32 | [src/main/java/com/archos/mediacenter/video/player/SegmentSkippingPolicy.java:1–18][E32] |
| E33 | [src/main/java/com/archos/mediacenter/video/player/PlayerService.java:1783–1810][E33] |
| E34 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:2317–2368][E34] |
| E35 | [src/main/java/com/archos/mediacenter/video/player/SurfaceController.java:45–249][E35] |
| E36 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:2269–2289][E36] |
| E37 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:2408–2454][E37] |
| E38 | [src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java:31–101][E38] |
| E39 | [src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java:1125–1172][E39] |
| E40 | [src/main/java/com/archos/mediacenter/video/utils/SubtitlesDownloaderActivity2.java:204–331][E40] |
| E41 | [src/main/java/com/archos/mediacenter/video/utils/SubtitleDiscovery.java:12–79][E41] |
| E42 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:3981–4030][E42] |
| E43 | [src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleResults.java:1–27][E43] |
| E44 | [src/main/java/com/archos/mediacenter/video/utils/SubtitlesDownloaderActivity2.java:405–805][E44] |
| E45 | [src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java:14–160][E45] |
| E46 | [src/main/java/com/archos/mediacenter/video/leanback/PreviewOperationDialog.java:1–43][E46] |
| E47 | [src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleChooser.java:31–115][E47] |
| E48 | [src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardCommon.java:146–375][E48] |
| E49 | [src/main/java/com/archos/mediacenter/video/player/PreviewTechnicalInfo.java:1–56][E49] |
| E50 | [src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java:440–467][E50] |
| E51 | [src/main/java/com/archos/mediacenter/video/player/PreviewUpNext.java:1–49][E51] |
| E52 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:4515–4586][E52] |
| E53 | [src/main/java/com/archos/mediacenter/video/player/PlayerController.java:2901–2918][E53] |
| E54 | [src/main/java/com/archos/mediacenter/video/player/PreviewHudTiming.java:1–11][E54] |
| E55 | [src/main/java/com/archos/mediacenter/video/player/PlayerController.java:2318–2597][E55] |
| E56 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:1490–1586][E56] |
| E57 | [src/main/java/com/archos/mediacenter/video/player/PlayerService.java:720–813][E57] |
| E58 | [src/com/archos/mediacenter/utils/videodb/IndexHelper.java:402–439][E58] |
| E59 | [src/com/archos/mediacenter/utils/videodb/VideoDbInfo.java:217–232][E59] |
| E60 | [res/values/arrays.xml:150–203][E60] |
| E61 | [src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:1640–1675][E61] |
| E62 | [docs/0.139/CORRECTIVE_RUNTIME_PLAYBACK_2026-10-16.json][E62] |
| E64 | [PlayerController.java:1963–2309][E64] |
| E65 | [PlayerBrightnessManager.java:52–145][E65] |
| E63 | [.github/build/nova-ci.xml:1–20][E63] |
| T01 | [src/test/java/com/archos/mediacenter/video/player/PreviewPlaybackMenusTest.java][T01] |
| T02 | [src/test/java/com/archos/mediacenter/video/player/Supernova138PlaybackTest.java][T02] |
| D01 | [docs/Future Development/01 Under Discussion/FD-002-playback-hud-redesign.md][D01] |
| D02 | [docs/qa/4.1.7-final-shield-qa.md][D02] |
| D03 | [docs/project/QA_0137_PLAYBACK_HUD_SEEKBAR_FOCUS_AND_LAYOUT_2026-10-12.md][D03] |
| D04 | [docs/design/PLAYBACK_HUD_0.135_APPROVED_2026-10-10.md][D04] |
| D05 | [docs/Releases/0.140-release-scope-and-codex-handover.md][D05] |

[E01]: https://github.com/heretoecode/Supernova/blob/d173e8dd9ea41834118a32e240518d91e15e11d1/docs/0.139/CONTINUATION.md "docs/0.139/CONTINUATION.md"
[E02]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/res/layout/player_controller_experimental.xml#L1-L51 "res/layout/player_controller_experimental.xml:1–51"
[E03]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L666-L768 "src/main/java/com/archos/mediacenter/video/player/PlayerController.java:666–768"
[E04]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java#L19-L135 "src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java:19–135"
[E05]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2031-L2111 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:2031–2111"
[E06]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2114-L2189 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:2114–2189"
[E07]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1458-L1477 "src/main/java/com/archos/mediacenter/video/player/PlayerController.java:1458–1477"
[E08]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1623-L1798 "src/main/java/com/archos/mediacenter/video/player/PlayerController.java:1623–1798"
[E09]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PreviewSeekPolicy.java#L1-L17 "src/main/java/com/archos/mediacenter/video/player/PreviewSeekPolicy.java:1–17"
[E10]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1497-L1515 "src/main/java/com/archos/mediacenter/video/player/PlayerController.java:1497–1515"
[E11]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L3898-L3957 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:3898–3957"
[E12]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L2173-L2416 "src/main/java/com/archos/mediacenter/video/player/PlayerService.java:2173–2416"
[E13]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1678-L1732 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:1678–1732"
[E14]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L3643-L3670 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:3643–3670"
[E15]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/tvmenu/SubtitleDelayTVPicker.java#L58-L252 "src/main/java/com/archos/mediacenter/video/player/tvmenu/SubtitleDelayTVPicker.java:58–252"
[E16]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/Player.java#L922-L945 "src/main/java/com/archos/mediacenter/video/player/Player.java:922–945"
[E17]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1734-L1875 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:1734–1875"
[E18]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L4411-L4468 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:4411–4468"
[E19]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/res/values-sw500dp-land-notouch/config.xml#L1-L4 "res/values-sw500dp-land-notouch/config.xml:1–4"
[E20]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/SubtitleColorPicker.java#L43-L164 "src/main/java/com/archos/mediacenter/video/player/SubtitleColorPicker.java:43–164"
[E21]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L3841-L3890 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:3841–3890"
[E22]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/tvmenu/TVCardDialog.java#L131-L181 "src/main/java/com/archos/mediacenter/video/player/tvmenu/TVCardDialog.java:131–181"
[E23]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L2027-L2169 "src/main/java/com/archos/mediacenter/video/player/PlayerService.java:2027–2169"
[E24]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L2767-L2871 "src/main/java/com/archos/mediacenter/video/player/PlayerService.java:2767–2871"
[E25]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L487-L576 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:487–576"
[E26]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1877-L1957 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:1877–1957"
[E27]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1959-L2020 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:1959–2020"
[E28]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioSpeedTVPicker.java#L138-L230 "src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioSpeedTVPicker.java:138–230"
[E29]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java#L559-L571 "src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java:559–571"
[E30]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2370-L2406 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:2370–2406"
[E31]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L418-L456 "src/main/java/com/archos/mediacenter/video/player/PlayerService.java:418–456"
[E32]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/SegmentSkippingPolicy.java#L1-L18 "src/main/java/com/archos/mediacenter/video/player/SegmentSkippingPolicy.java:1–18"
[E33]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L1783-L1810 "src/main/java/com/archos/mediacenter/video/player/PlayerService.java:1783–1810"
[E34]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2317-L2368 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:2317–2368"
[E35]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/SurfaceController.java#L45-L249 "src/main/java/com/archos/mediacenter/video/player/SurfaceController.java:45–249"
[E36]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2269-L2289 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:2269–2289"
[E37]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2408-L2454 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:2408–2454"
[E38]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L31-L101 "src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java:31–101"
[E39]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java#L1125-L1172 "src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java:1125–1172"
[E40]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/utils/SubtitlesDownloaderActivity2.java#L204-L331 "src/main/java/com/archos/mediacenter/video/utils/SubtitlesDownloaderActivity2.java:204–331"
[E41]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/utils/SubtitleDiscovery.java#L12-L79 "src/main/java/com/archos/mediacenter/video/utils/SubtitleDiscovery.java:12–79"
[E42]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L3981-L4030 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:3981–4030"
[E43]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleResults.java#L1-L27 "src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleResults.java:1–27"
[E44]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/utils/SubtitlesDownloaderActivity2.java#L405-L805 "src/main/java/com/archos/mediacenter/video/utils/SubtitlesDownloaderActivity2.java:405–805"
[E45]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L14-L160 "src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java:14–160"
[E46]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/leanback/PreviewOperationDialog.java#L1-L43 "src/main/java/com/archos/mediacenter/video/leanback/PreviewOperationDialog.java:1–43"
[E47]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleChooser.java#L31-L115 "src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleChooser.java:31–115"
[E48]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardCommon.java#L146-L375 "src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardCommon.java:146–375"
[E49]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PreviewTechnicalInfo.java#L1-L56 "src/main/java/com/archos/mediacenter/video/player/PreviewTechnicalInfo.java:1–56"
[E50]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java#L440-L467 "src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java:440–467"
[E51]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PreviewUpNext.java#L1-L49 "src/main/java/com/archos/mediacenter/video/player/PreviewUpNext.java:1–49"
[E52]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L4515-L4586 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:4515–4586"
[E53]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L2901-L2918 "src/main/java/com/archos/mediacenter/video/player/PlayerController.java:2901–2918"
[E54]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PreviewHudTiming.java#L1-L11 "src/main/java/com/archos/mediacenter/video/player/PreviewHudTiming.java:1–11"
[E55]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L2318-L2597 "src/main/java/com/archos/mediacenter/video/player/PlayerController.java:2318–2597"
[E56]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1490-L1586 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:1490–1586"
[E57]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L720-L813 "src/main/java/com/archos/mediacenter/video/player/PlayerService.java:720–813"
[E58]: https://github.com/heretoecode/aos-MediaLib/blob/5758074049bc153ac4967b4ecd21886469270076/src/com/archos/mediacenter/utils/videodb/IndexHelper.java#L402-L439 "src/com/archos/mediacenter/utils/videodb/IndexHelper.java:402–439"
[E59]: https://github.com/heretoecode/aos-MediaLib/blob/5758074049bc153ac4967b4ecd21886469270076/src/com/archos/mediacenter/utils/videodb/VideoDbInfo.java#L217-L232 "src/com/archos/mediacenter/utils/videodb/VideoDbInfo.java:217–232"
[E60]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/res/values/arrays.xml#L150-L203 "res/values/arrays.xml:150–203"
[E61]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1640-L1675 "src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java:1640–1675"
[E62]: https://github.com/heretoecode/Supernova/blob/d173e8dd9ea41834118a32e240518d91e15e11d1/docs/0.139/CORRECTIVE_RUNTIME_PLAYBACK_2026-10-16.json "docs/0.139/CORRECTIVE_RUNTIME_PLAYBACK_2026-10-16.json"
[E63]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/.github/build/nova-ci.xml#L1-L20 ".github/build/nova-ci.xml:1–20"
[T01]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/test/java/com/archos/mediacenter/video/player/PreviewPlaybackMenusTest.java "src/test/java/com/archos/mediacenter/video/player/PreviewPlaybackMenusTest.java"
[T02]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/test/java/com/archos/mediacenter/video/player/Supernova138PlaybackTest.java "src/test/java/com/archos/mediacenter/video/player/Supernova138PlaybackTest.java"
[D01]: https://github.com/heretoecode/Supernova/blob/71be45aace7c2f71b010848a2249c148595c9286/docs/Future%20Development/01%20Under%20Discussion/FD-002-playback-hud-redesign.md "docs/Future Development/01 Under Discussion/FD-002-playback-hud-redesign.md"
[D02]: https://github.com/heretoecode/Supernova/blob/71be45aace7c2f71b010848a2249c148595c9286/docs/qa/4.1.7-final-shield-qa.md "docs/qa/4.1.7-final-shield-qa.md"
[D03]: https://github.com/heretoecode/Supernova/blob/71be45aace7c2f71b010848a2249c148595c9286/docs/project/QA_0137_PLAYBACK_HUD_SEEKBAR_FOCUS_AND_LAYOUT_2026-10-12.md "docs/project/QA_0137_PLAYBACK_HUD_SEEKBAR_FOCUS_AND_LAYOUT_2026-10-12.md"
[D04]: https://github.com/heretoecode/Supernova/blob/71be45aace7c2f71b010848a2249c148595c9286/docs/design/PLAYBACK_HUD_0.135_APPROVED_2026-10-10.md "docs/design/PLAYBACK_HUD_0.135_APPROVED_2026-10-10.md"
[D05]: https://github.com/heretoecode/Supernova/blob/71be45aace7c2f71b010848a2249c148595c9286/docs/Releases/0.140-release-scope-and-codex-handover.md "docs/Releases/0.140-release-scope-and-codex-handover.md"

Additional pinpoint evidence:

- [Subtitle default resources (base)](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/res/values/config.xml#L2-L5)
- [Subtitle default colour](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/res/values/donottranslate.xml)
- [Array choices,3D,ratios](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/res/values/arrays.xml#L61-L104)
- [Palette swatches](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/res/values/arrays.xml#L168-L185)
- [English labels](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/res/values/strings.xml#L293-L448)
- [Subtitle settings XML](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/res/xml/preferences_video.xml#L400-L442)
- [Speed XML default](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/res/xml/preferences_video.xml#L90-L96)
- [Pause timeout5000ms](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L695-L702)
- [Per-file frontend save](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L1094-L1103)
- [Format persistence](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L4633-L4642)
- [3D session state](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L872-L908)
- [Bitmap positioning / size scale](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/SubtitleManager.java#L780-L864)
- [Audio delay750ms/departure](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioDelayTVPicker.java#L157-L220)
- [More Back and wrapper route](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L2831-L2889)
- [Time string MM:SS/H:MM:SS](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1285-L1300)
- [Language catalogue](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java#L104-L106)
- [Language ordering](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java#L1518-L1549)
- [Inline language editor](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/leanback/settings/InlinePreferenceChoices.java#L10-L39)
- [Startup Foundation UI policy](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/foundation/FoundationUiPolicy.java#L7-L16)
- [Passthrough resets speed preset](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java#L487-L497)
- [Episode/next cancellation](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L1908-L1915)
- [Session segment master/prompt](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L1425-L1440)
- [Segment fetch gates](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L1656-L1689)
- [Surface format operations](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/SurfaceController.java#L348-L423)

[E64]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1963-L2309 "Conditional gestures and numeric OSD"
[E65]: https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerBrightnessManager.java#L52-L145 "Window brightness conversion and persistence"

- [Brightness restore at player resume](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1121-L1129)
- [App-start brightness reset policy](https://github.com/heretoecode/Supernova/blob/a4841c1dbaac5e1037fb19f9f11882848b435e0b/src/main/java/com/archos/mediacenter/video/browser/MainActivity.java#L336-L346)
