# Product decisions — overnight discussion, 7–8 October 2026

Status: durable **approved design directions**, not automatic implementation scope. Existing 4.1.7 fixes-only scope remains frozen.

## Smart Home Rows — APPROVED
- **Show/Hide** controls whole-row visibility, separately from row membership.
- **Dynamic Genre Rule ON** automatically adds matching future library items.
- **Dynamic Genre Rule OFF** freezes automatic membership changes; manually added items remain and manual additions stay possible.
- Sort direction is independently reversible.
- Do not equate hidden row with disabled dynamic rule.

## Up Next / watched / reset — APPROVED
- Up Next overlays/integrates with inherited **Binge Watching** rather than silently replacing its working behaviour.
- Watched threshold is **90%** of playback; manually seeking near the end alone does not mark an item watched. Continue playback/post-credits behaviour must not be broken.
- **Reset Progress** is confirmed and applies to the selected item only: clears playback position and watched state, not the media entry or whole series.

## Multiple Versions — APPROVED
- Automatic default chooses highest quality in this priority order: **4K Dolby Vision/HDR10+ → 4K HDR10 → 4K SDR → 1080p HDR → 1080p SDR → 720p → 480p** (within actually supported/playable variants).
- Dedicated **Versions (n)** action beside Play on Details.
- Selected version's compact resolution/HDR label updates on Details; technical panel follows selected version.
- User's manual version choice persists. Do not silently override it with automatic quality ranking.

## Advanced Subtitles — KEEP EXISTING
- Retain inherited NOVA subtitle appearance, positioning, synchronisation, language selection, downloads and format handling as closely as possible.
- No new presets or sliders; do not redesign merely for modernisation. Revisit only if real testing identifies a defect.

## Release/version history presentation — DESIGN DIRECTION
- Pre-1.0 numbering proceeds sequentially (0.1, 0.2, etc.).
- Latest release details expanded by default; older releases collapsed and expandable by TV D-pad.
- Do not confuse this future UI direction with current application versioning or authorise a branding/package change.

## Boundaries
- Seek thumbnails, Profiles, AI, Anime, Trakt reviews, wider Library Health and other exploratory work remain parked unless explicitly activated.
- Network & Files, Settings and APK walkthrough were intentionally reserved for separate detailed review; do not infer final approvals from their mention here.
- This file records decisions, not a new Codex handover or permission to modify application code.

## Later detailed decisions recovered — 7–8 October (APPROVED design, not fixes-only implementation)
### Movies/TV/custom-page toolbar — SUPERSEDES older underline-only toolbar design
Below library stats and above the retained full-width divider, each control (Filters, Sort, Order, List/Grid, Columns) uses an **always-visible subtle rounded outlined box**, icon plus text label, and selected Supernova accent focus. No floating labels straddling the divider, no underline-only focus. Controls form a left cluster. On the one custom page only, **Edit** is far right after a generous gap, same box/icon/label/focus treatment; built-in Movies/TV do not have Edit. The older MOVIES_TV.md statement prescribing divider-only focus is **SUPERSEDED as future design**. The current fixes-only scope is unchanged.

### One custom Library Page — APPROVED design, NOT authorised implementation
Top-nav + opens Create a New Page, then Library Page (local media) or Discovery Page (PARKED). Exactly **one** custom Library Page slot immediately after fixed Home, Movies, TV Shows; do not offer creation of a second when occupied. No page reordering or multi-page expansion plan. Five-step three-panel creation/edit wizard: **Page Details** (name/icon), **Content Type** (Movies, TV Shows, Both), **Filters** (genre, year, watched status, minimum rating, country, language, studio/network, collection only where metadata reliable; multi-genre Any/All), **Display Options**, **Review**. Right panel is a live preview, and Back preserves entered choices. **Exclusion Options** sixth step was explicitly **REMOVED/SUPERSEDED**. Edit from custom toolbar reopens same wizard prefilled; rename in Page Details. **Delete Page** appears only in edit wizard, separated/destructive with confirmation explaining media is not deleted. Unsaved exit: **Save Page**, **Discard Changes**, **Continue Editing**; Save validates, Discard in edit preserves last saved state, creation discards draft, no prompt when unchanged. Works with Shield Back/D-pad.

### Search and alternate titles — APPROVED future design
Search is **local library only**, not remote discovery. Match indexed title, filenames, episode names and indexed cast/crew/studios/networks, explaining match role when relevant. Right guidance mock-up text: “Search Supernova”, “Just type what you're looking for”, “No exact formatting”, “Articles optional” (The/A/An), “Alternate titles”, “Minor typos OK”. These must be **real functional abilities**, not unimplemented marketing copy. Normalise punctuation, optional leading articles, small typos with sensible ranking, original/international titles; display original/alternate titles on movie/show Details when actually available and index them. The keyboard depicted on the left of the reference mock-up is not an approved keyboard redesign. Read-only source investigation found TMDb original_title/original_name already stored in MediaLib; full alternate-title lists not established. Fetch/store/locale/deduplicate real alternate titles only if supported and validated. Existing SEARCH_MATCHING.md keyboard rules remain otherwise in force.

### Multiple Versions — expanded APPROVED design
Film/individual episode Details has dedicated **Versions (N)** button beside Play only when N>1; uses existing chooser, with clear current checkmark and reliable file distinctions. Show-level Details keeps aggregated per-quality counts, not an arbitrary one-file badge or Versions action. Rank best **compatible** default: 4K Dolby Vision/HDR10+ > 4K HDR10 > 4K SDR > 1080p HDR > 1080p SDR > 720p > 480p/lower. Use reliable actual HDR/codec/bitrate/audio/source metadata and device compatibility, not filename-only heuristics. Explicit per-item manual choice persists and overrides auto ranking while file available. **All entry routes** (Home, Continue Watching, Recently Added, Movies, TV, episode lists, Details, playback) must resolve consistently; switching versions must preserve appropriate watched/resume state. Main film/episode hero metadata line gains a compact selected-file label (e.g. 720p, 1080p, 4K HDR10, 4K Dolby Vision) **even for a single version**, immediately updated on selection. Keep full codec/audio/size/path in Technical Information; don't invent quality when unknown.

### Playback, Home and focus — APPROVED behavioural refinements
Inherited Binge Watching is authoritative for episode progression; Up Next has **five-second countdown**, Play Now shortcut, no visible Cancel, auto-advance on expiry when safe; never double-advance, protect post-credit scenes. Single Play Mode returns after one episode; Folder/Repeat semantics preserved. Segment **Skip pill lasts five seconds** and is not manually dismissible: press to skip, ignore to continue. This is distinct from Up Next. Watched threshold **90% actual playback** or reliable end; merely seeking near end must not mark watched, and post-credits remain protected. Continue Watching long-press gains **Reset Progress** with confirmation, clearing only selected item position and watched state; **Dismiss** only removes from that row, not watch state. Home Smart Rows Show/Hide affects visibility only; Dynamic Genre Rule On automatically includes new matches; Off freezes auto membership while retaining existing/manual items. Sort Date Added/Title/Year with ascending/descending. Featured artwork colour/blur reduction was discussed but **set aside**, not implementation approval.

### Segment Skipping — APPROVED design, not implemented
Settings > Integrations master On by default, independent Intro/Recap/Outro/Credits/Preview controls. Intro default Show Skip (5s); Recap Smart; Outro Auto; Credits Auto only if post-credit-safe; Preview Smart, falling back to normal playback when classification uncertain. Modes where appropriate: Auto Skip, Show Skip button, Play normally; smart handling must not skip meaningful material. IntroDB.app and TheIntroDB are candidate sources subject to actual support. Existing Play Mode “Skip intro/outro” becomes session **Segment Skipping override**, persists uninterrupted binge but resets at separately started playback; must not rewrite global setting. Remove raw marker timestamps from ordinary Play Mode UI (diagnostics only). Preserve four-control HUD Subtitles, Audio, centred Play/Pause, More; no Info or full Settings links.

### Version naming / Release Notes — APPROVED design, NOT current build identity
First genuine Supernova modified release **0.1**, then 0.2 … 0.9, 0.10, 0.11, 0.12 (twelfth). This is a sequential release label, **not semantic version ordering**; audit actual delivered historical releases vs QA snapshots/CI before mapping. 1.0 reserved until user approves. Android versionCode separately must remain valid; signing/package transition deferred; user reports creating a new signing key, but no key material/location verified or to be committed. Settings Release Notes: current/latest release **permanently expanded at top without collapse affordance**; all older releases below collapsed by default, D-pad/OK toggles; several older releases may remain open. Each version uses concise app-store-style nontechnical one-line changes and monochrome semantic icons, not emoji; 0.1 bottom. Final About/Release Notes page geometry **deferred to Settings walkthrough**. Illustration 0.12 is not verified current version.

### Subtitle policy — APPROVED keep existing
No reported subtitle problems: preserve existing NOVA-derived appearance, positioning, synchronisation, language preferences, downloads, formats and HUD subtitle paths. No new presets, sliders or redesign; revisit if physical testing exposes a concrete defect.

### Backup — APPROVED and audit findings
See BACKUP_RESTORE.md for the more specific later authoritative specification. **Comprehensive backup** (no category selection) and **full restore of everything actually included**. Absolutely no network passwords, credentials, API/OAuth/access/refresh tokens, session secrets or secret-bearing URIs, including indirect copies. Archive must be verified before replacement; keep staging, integrity, journal and rollback. Include readable `RESTORE_INSTRUCTIONS.txt` **inside ZIP** so it travels with backup. Restore requires reauthentication. User explicitly asked **feature-by-feature audit** of watch history/resume across version changes, new Settings preferences, new Home rows/UI, custom page definitions, network/source identities, manual metadata associations and obsolete NOVA UI prefs. Do not assume legacy export captures current Supernova state. Read-only source audit established MediaLibraryBackupService currently exports media.db, credentials_db, shortcuts DBs, settings.json and named_preferences.json (SettingsBackup serialises all keys), plus personal art; this currently **leaks recoverable secrets**. VerifiedBackup, SafeBackup, RestoreJournal already perform partial verification, staging, integrity, rollback. Must inspect exact keys and migration; no source changes approved.

### Ratings investigation — NEEDS DECISION, not approved integration
PutFlix (putflix.app) screenshot showed IMDb, TMDb and Rotten Tomatoes critic/audience ratings; chill.institute also cited. Their exact backend sources **unverified**. MDBList, OMDb, RPDB are candidate aggregation providers; official IMDb access/licensing is distinct. GitHub default-branch search found no MDBList reference, but not proof against all branches. Check API coverage, legal reuse/attribution, cost/quotas, cache and real-title accuracy before deciding. Do not imply PutFlix's method is confirmed or ratings integration implemented.

### Discussion order / boundaries
Advanced Subtitles closed. Remaining planning: Backup & Restore (ongoing compatibility audit), legacy NOVA cleanup, Performance & Diagnostics (source/diagnostic QA direction: crash/ANR/leaks/jank/focus/codec/seek/subtitles/network/indexing, privacy-safe redacted opt-in export, low overhead; no in-app diagnostic viewer approved), then **Settings, Network & Files and full installed APK walkthrough last**. Profiles, Anime, AI, Trakt reviews, seek thumbnails, Discovery, broad Library Health remain PARKED. One custom Library Page design approved but **not implementation scope**. No code changes or fixes-only expansion.

## 8 October later supersession — Continue Watching
The earlier proposed **Reset Progress** long-press action in this document is **SUPERSEDED**. Approved replacement: **Restart Episode / Restart Movie**, which immediately starts playback from 00:00. Existing Dismiss is renamed **Remove from Continue Watching Row**, preserving watch state and saved position. Details > More also gains Restart. See `docs/project/CURRENT_CHAT_DECISIONS_2026-10-08.md` for full context. These are future product decisions, not additions to the frozen fixes-only release.

## 8 October approved visual branding — authoritative cross-reference
For complete branding decisions and the 4K PNG handover, see `docs/project/CURRENT_CHAT_DECISIONS_2026-10-08.md`, section **8 October final background asset / 4K handover**. Original restrained Space Black Blend composition/colour is approved; the brighter nebula and strong light-streak experiments are rejected. The chat-generated high-quality **3840×2160** upscale `Supernova_Space_Black_Blend_4K_UHD.png` must be uploaded and verified in GitHub before Codex can treat it as a source asset. Use one background design on Home, Network & Files, Search, Settings, banner and icon; preserve dynamic media artwork backgrounds on Movies, TV and custom library pages. Keep header dark enough for white nav/clock. Banner = ring plus Roboto Light-like SUPERNOVA; square icon = ring only; splash = static ring over SUPERNOVA, no startup delay; trailing-glow animation applies to real loading indicators only. **Documentation is not permission to implement or change fixes-only branch.**

## 9 October 2026 — Shield visual evidence and audit reconciliation
- Home Featured: user photograph of *MobLand* confirms a broad inherited left-side dark/blue fade; user requests **clean, crisp artwork without the heavy inherited gradient**, with text readability addressed separately. This is a new explicit future request and supersedes only the earlier *parked discussion* about general artwork colour/blur reduction, not the currently implemented 4.1.7 baseline.
- Home Featured animation: user video demonstrates **brief darkening/translucency/flashing during carousel transitions**. Record as an outstanding, distinct visual defect; cause and fix not verified. Do not confuse with playback HUD diagnostic flash.
- Movies/TV boxed toolbar: user reaffirmed LEFT/RIGHT between controls, DOWN to library, UP towards top nav, with physical focus-graph validation still needed. See MOVIES_TV.md.
- The conversation-supplied photograph/video were examined but **not committed as binary assets**; original evidence remains in conversation. No implementation or fixes-only scope change.
