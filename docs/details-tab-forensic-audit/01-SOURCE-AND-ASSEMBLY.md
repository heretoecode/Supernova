# 01 — Source ownership and assembly

Evidence: S unless marked. All `P` anchors below mean [PreviewMoviePage at signed source](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java). The [source ledger](evidence/SOURCE_LEDGER.md) supplies exact excerpts, line ranges and file hashes for reproduction.

## Entry and screen families

| Atlas ID / entry | Implementation | Conditions and data |
|---|---|---|
| DET-001 / Home hero More Info, Home row, Movies/custom Library, Search, browser media | `VideoViewClickedListener.showVideoDetails` → `VideoDetailsActivity` → `VideoDetailsFragment` | Intent serializes `EXTRA_VIDEO`, list ID, force selection/backdrop flags and monotonic launch timestamp. `PreviewPages.open` remembers focus anchor before delegating the click. Browse/search click handlers ultimately use the same native video/show entry routes; external-intent/player entry may use fallback below. |
| DET-001 / episode | Same Video Details path | `movie` variable can hold an `Episode`; this is not a TV-show aggregate page. Selected physical episode facts, episode plot and series context are combined deliberately. |
| DET-002 / TV show | `VideoViewClickedListener.showTvshowDetails` → `TvshowActivity` → `TvshowFragment` | Intent `EXTRA_TVSHOW`; `TvshowFragment` retains native action adapter, FullScraperTagsTask, SeasonsLoader and per-season EpisodesLoader; shared preview renderer. |
| DET-002 / season | Focusable season heading in `renderSeasons` → `PreviewSeasonActivity` | Intent show ID + season; memory snapshot then fresh loader; `bindSeason` uses shared page, season-scoped metadata and episodes; missing show/invalid season emits notice and finishes. This separate season page is current, despite future one-master-show requirement. |
| DET-002 / remote provider title | `PreviewRemoteDetailsActivity` → shared page | kind + TMDB ID; StreamingRepository worker loads title/availability; provider actions replace Play/More; failure opens “Details unavailable / Please try again when the metadata service is available.” No fabricated physical file data. |
| OTHER / classic or player-overlay Details | Native Leanback Details fragments/presenters | Video fragment returns native view if `mVideo==null`, launched from player, or `try_new_ui` false (P renderer is then absent). Show preview is gated on preference and non-null show. Do not use native XML to infer current five-column dimensions. |

`VideoDetailsActivity` inflates `res/layout/androidtv_details_activity.xml`: outer FrameLayout; `details_backdrop` ImageView, match-parent, centerCrop, alpha 0, accessibility ignored; `main_browse_fragment` hosts VideoDetailsFragment. `TvshowActivity` uses its own activity layout. Native presenters/resources (`leanback_details_description`, `leanback_tvshow_details_description`, `leanback_details_overview`, text/director/plot groups) remain in the tree but are not the constructors for preview facts. This is **Java Android Views**, not Compose, not a Details ViewModel, not a RecyclerView/adapted list of fact rows.

## Exact preview tree

```text
Activity content / native activity layout
└─ Fragment view: TopNavigation [TvLayout insets, background drawable]
   ├─ bar [brand, horizontal top-nav focus rail, status clock]
   └─ stage [full measured shell height for scrolling content, drawn under bar]
      ├─ fragment container FrameLayout
      │  ├─ retained native Leanback view [GONE]
      │  └─ PreviewMoviePage extends ScrollView [not itself focusable]
      │     └─ body vertical LinearLayout [safe side margins, 24dp bottom]
      │        ├─ heroPage vertical LinearLayout
      │        │  ├─ CinematicCard [weight 1]
      │        │  │  ├─ artwork ImageView [CENTER_CROP]
      │        │  │  ├─ left-right scrim View
      │        │  │  └─ cardCopy vertical LinearLayout
      │        │  │     ├─ title TextView [official-logo foreground or text]
      │        │  │     ├─ meta TextView
      │        │  │     ├─ context TextView [episode / season only]
      │        │  │     ├─ pills LinearLayout [measured technical badges]
      │        │  │     ├─ plot TextView [upper-card synopsis, line limited]
      │        │  │     ├─ Match Metadata TextView [conditional action]
      │        │  │     ├─ buttons [Play/Resume, providerActions, More]
      │        │  │     └─ flexible empty View [AFTER buttons]
      │        │  └─ PreviewToolbar [section headings / underline]
      │        └─ lower FrameLayout [minimum height; one active tab surface]
      │           ├─ information horizontal LinearLayout [Details only]
      │           │  ├─ Cast wrapper, heading, content [weight 1]
      │           │  ├─ Crew wrapper, heading, content [weight 1]
      │           │  └─ Details wrapper, hidden heading, content [weight 3.6]
      │           │     └─ panels horizontal LinearLayout
      │           │        ├─ Key Information [weight 1]
      │           │        ├─ Reception [weight 1; can be GONE]
      │           │        └─ Technical Information / Streaming Availability [weight 1]
      │           ├─ Episodes wrapper [Seasons & Episodes only]
      │           ├─ related wrapper [More Like This only]
      │           └─ trailers wrapper [Extras only]
      └─ scanStatus FrameLayout [overlay, bottom-end, blocks focus]
```

[P52](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L52)–89, 120–139, 181, 222–353 construct and switch this tree. Section containers themselves do not scroll independently. Individual facts are LinearLayout(label TextView + value TextView), populated programmatically in `renderDetails`. All preview lower headings/labels are **hardcoded English literals**, not localization resource IDs. Ordinary facts/people have no assigned `R.id` or click handler. Semantic tags identify panels (`semantic:details.panel.key.information`, `.reception`, `.technical.information`, `.streaming.availability`) and card (`semantic:details.card`); tab tags `section:<label>`; people regular tags `person:<name>:<role>`. `Diagnostics.semantic` also writes a keyed diagnostic tag using `R.id.preview_diagnostic_semantic`; this does not make an item focusable or clickable.

Cast/Crew wrapper visibility collapses absent people entirely. Key Information is retained even when empty. Reception/Technical panel visibility depends on whether it has more than its heading child. Missing individual fields remove rows rather than reserve blank space. Weights redistribute when wrappers/panels are GONE; five fixed equally sized columns is therefore an inaccurate reconstruction. [P69](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L69)–73, 212–213, 326–328, 399.

## Source-to-data trace

| Screen input | Source path / method | Persistent or provider source / updates |
|---|---|---|
| Initial selected Video/Movie/Episode | `VideoViewClickedListener`; `VideoDetailsFragment.onViewCreated` / `onLoadFinished` / `fullyReloadVideo` | Intent object first, authoritative CursorLoader next. `VideoCursorMapper.bindColumns` maps `_id`, DATA, SIZE, DURATION, WIDTH/HEIGHT, calculated audio/video fields, SCRAPER_* fields, bookmark/watch flags into Movie/Episode/Video. Local and network records use the same model. |
| Full movie tags | `Movie.getFullScraperTags` → MediaLib `TagsFactory.buildMovieTags` | ContentResolver → ScraperStore movie + actors/directors/writers/genres/studios/artwork/trailers tables/views. P `setTags` is called from fragment FullScraperTagsTask on main handler. |
| Full show tags | `Tvshow.getFullScraperTags` → `TagsFactory.buildShowTags` | ScraperStore show + relationships; TvshowFragment FullScraperTagsTask binds show and posts `setTags`; per-season episode adapters separately populate interactive rows. Aggregate facts consume page snapshot or global memory snapshot, not those adapters. |
| Episode tags | `Episode.getFullScraperTags` → `TagsFactory.buildEpisodeTags` | Episode table/guest actors/directors/writers then parent ShowTags. Episode cast initially uses episode tags; later normal enrichment is **series credits**, not episode guest-star JSON. |
| Library aggregates | `PreviewLibraryLoader` / `Snapshot` / `Entry` | AllVideosLoader + AllTvshowsLoader; file rows and joined scraper values. `Entry` stores measured category/codec/HDR information, bytes, language/studio/collection and release date. Page `setSnapshot` rerenders facts; show page commonly falls back to `memoryCache()`. |
| On-file tracks | `VideoDetailsFragment.VideoInfoTask` → `VideoInfoCommonClass.retrieveMetadata` → `VideoMetadata.fillFromRetriever` | Existing native `MediaFactory` metadata retriever; local path or remote stream with forwarded headers. UPnP resolves streaming URL; SMB checks existence first. `VideoMetadata.save` writes measured width/height, codec, bitrate/fps-derived field and JSON audio tracks to VideoStore. UI bitrate/fps read **in-memory track object**, not the saved fps field. |
| Enriched descriptive facts | `PreviewMoviePage.metadataIdentity` / `requestEnrichment` → `PreviewDetailsData.cached/load` → `StreamingRepository.metadata` | TMDB v3 title, credits, release_dates/content_ratings, videos, recommendations and tv season/{n}. Locale-specific disk envelope first; fresh/cache-fallback gateway next. Provider availability is separate and country/configuration dependent. |
| Scanner fields | MediaLib `VideoStoreImportService/Impl`, `NetworkScannerServiceVideo`, scraper parsers/NFO | Existing maintenance07 maps effective build overlays. File metadata can precede matching; scraper tags can derive from TMDB or NFO/manual metadata. Indexed rating does not retain a guaranteed provider, hence “Indexed Rating.” |

Pinned MediaLib getters and relations were inspected directly. Its build overlays in Supernova affect scanner/import/API behavior; no overlay changes the inspected BaseTags/VideoTags/MovieTags/ShowTags/EpisodeTags/TagsFactory/ScraperProvider relationship methods. Native metadata enum extensions are in `.github/build/medialib-supernova135.patch`; current VideoMetadata consumes the corresponding HDR/Dolby Vision/track keys. This investigation does not rebuild native code or assert measured codec values for any owner file.

## Supported data not displayed in the lower Details tab

“Supported” here means a named getter/field/parsed input in the inspected models, not every arbitrary property TMDB might return in an untyped JSONObject. No new field is implied to be approved. The field inventory covers every property actually read for lower output; the ledger also preserves the model source so internal IDs and artwork/control fields are reproducible.

| Existing model support | Omitted or only used elsewhere |
|---|---|
| BaseTags identity/title/plot/contentRating/IMDb ID/online ID/runtime/resume/bookmark/lastPlayed/file/videoId, actors/set/directors/writers, posters/backdrops | Title/plot are upper-card only; full synopsis absent below. IMDb/provider/internal IDs, file association IDs, bookmarks/resume/last played, collection `set` map and full people beyond eight are not fact rows. Stored unscoped contentRating is deliberately not used for Age rating. Movie tag runtime is not a direct fallback, unlike episode tag runtime. Artwork lists live in picker/backdrop paths. |
| VideoTags genre/studio lists + cached formatted strings | Formatted lists displayed; individual genre/studio IDs or chips are absent. |
| MovieTags year, releaseDate, originalLanguage, originalTitle, spokenLanguages, titleLanguage; collection ID/name/description and poster/backdrop paths/URLs/cache files | Lower original title/release/collection **only** read enrichment JSON, not these stored getters. Language fields have no lower fact row. Collection description/IDs/images not displayed below. Indexed movie year is displayed. |
| ShowTags premiered Date/year, originalLanguage/title, spokenLanguages, titleLanguage; count/watched/pinned fields in Tvshow | Lower release/original title read JSON instead of stored equivalents; no language row. Counts are header context and technical aggregates. Watched episode count, pinned state, full show runtime are not lower rows. |
| EpisodeTags aired/season/episode/show ID, parent ShowTags; Episode online/local ID, plot/rating/contentRating/picture | Date/number are used; title/plot upper only, image in episode row. Series original title/tagline retained below; episode guest stars from metadata are not separately rendered. Unscoped episode contentRating omitted. |
| Video raw/streaming/friendly path, filename, dimensions, calculated and guessed formats/definition, 3D, external subtitle count, chosen audio index, occurrences, watched/Trakt flags, user hidden state, resume/last played/size/duration, preview HDR/Dolby Vision fields | Guessed filename quality is not exposed as measured resolution. Friendly UPnP path/stream URL are not used by lower Location. 3D/selected audio index/occurrences/external-subtitle count and watch/source-state fields have no dedicated lower fact. File URI host/path is used with sensitive components removed. |
| VideoMetadata overall duration/file size, video width/height | Lower Runtime/File size/Resolution read Video model values, not these in-memory getters directly. |
| VideoTrack format/profile/level/bitRate/fps/fpsRate/fpsScale/s3dMode/decoder/colorTrc/dolbyVisionProfile/dolbyVisionCompatibility/hdr10Plus | Bitrate and rational fps are shown; range resolves a label. Native codec profile/level, integer fps, 3D mode, decoder name, raw color transfer/DV profile/compatibility values omitted; track format is represented through indexed calculated format, not directly this object. |
| AudioTrack name/format/bitRate/sampleRate/channels/vbr/supported/language/disposition | Format/channels/sampleRate included in Audio tracks; language, name/index, bitrate, VBR/support/disposition flags are not included in that row. Header may show AD. Identical formatted tracks collapse, so row is not a count of actual tracks. |
| SubtitleTrack name/path/isExternal/isGfx/format/language/disposition | Lower Subtitles contains language strings only. Names, paths, format, internal/external and graphics/forced/default/SDH/caption flags omitted below. Header measured badges can expose SDH/Captions; sidecars are handled separately, not automatically listed here. |
| Named JSON properties already consumed elsewhere: title/name/overview, images/logo/profile/backdrop/poster paths, credits id, videos site/key/type, recommendations, seasons/season_number/episodes/episode_number/episode name/still, number_of_seasons | Used in card/logo/people/extras/recommendations/episode presentation, not extra lower field labels. `episode_run_time` has a helper/test but **no renderer call**, so a TV episode-runtime row must not be invented. |
| Entry added/modified/runtime/bitrate/language/studio/collection/path/container/sortTitle/releaseDate, active/heroEligible/heroSequential/secondary/homeMetadata/tagline, availability-related state | Some support library filters/sort/hero/technical tables. No Added/Modified/total runtime/aggregate audio/aggregate bitrate/local-vs-network byte split or language facts on show Details. |

The JSON object is not a fixed exhaustive metadata schema: properties such as homepage/popularity cannot be called supported Details fields simply because a remote API may supply them. This report does not fabricate new rows for those properties.

## Source-quality and reachability findings

| Finding | Direct evidence | Consequence / limit |
|---|---|---|
| No lower synopsis or reachable full-reader action | [P222](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L222)–353 contains all facts, no overview; [P561](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L561) `moreInfo` is private and `rg` finds only its declaration | Upper synopsis is not a hidden/conditional lower field. Retained dead reader code does not satisfy future full-synopsis requirement. |
| Detached Versions button | [P67](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L67) creates button; [P145](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L145) sets visibility/count; no `addView(versions)`; [P152](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L152) attempts requestFocus | Multi-file selection policy exists, but this specific preview button cannot be focused/rendered. Right from Play when its visibility is VISIBLE calls a detached target and consumes the key. Do not promise accessible versions on this page from setter presence alone. |
| Unattached divider | [P68](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L68) creates `tabLine`, GONE, no addition | No actual full-width structural divider or shared content-frame outline in this implementation. |
| No current person portrait | [P207](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L207) clears portrait map; [P375](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L375)–377 builds only text; no `portraits.put` in class; PreviewPeople returns on empty targets | Portrait loading helpers/resources are not proof of visible portraits; current people columns are text only. |
| Season credit/video fresh fetch blocked | StreamingRepository metadata allowlist accepts `season/[0-9]{1,4}` but not suffix `/credits` or `/videos`; PreviewDetailsData.loadSeason asks those and catches failure | Cache readers accept those section filenames. Existing test seeds cache directly. Fresh season people/extras may remain missing unless already cached. No live HTTP/device failure claimed. |
| Technical late-update asymmetry | Fragment updateMetadataWhenReady1837 only calls preview.bind for Movie | Episode may wait for later reload/tag/snapshot trigger; full track facts can lag. This is a source risk, not proof every episode is stale. |
| Eight filtered crew, not best-eight role ranking | [P387](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L387)–391 loops provider order with allowlist and cap | Early producers can consume slots before later director/editor; repeated person in distinct roles retained. |
| Helper classification test differs from renderer | PreviewDetailsFacts.certificate appends territory; [P248](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L248) calls SharedMetadata.certificate instead | Actual lower Age rating has no territory suffix. Test helper output must not be substituted into field specification. |
