# 07 — Movies, TV, custom libraries, scanning, metadata and artwork

## Current data-to-screen pipelines

**VERIFIED:** local/network scanners write VideoStore file rows; scraper provider stores movie/show/episode identities and artwork links; library snapshot loader combines these with independent ViewingHistory and saved view/filter settings. PreviewPages renders Movies/TV and custom pages using common Library geometry/components. Home selection/Continue Watching uses physical availability and durable identity progress; custom rows/Watch Next are separately configurable. Streaming-only details and put.io associations must not be treated as verified local codec metadata.

Anchors: [PreviewLibraryLoader.java:12](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryLoader.java#L12), [PreviewPages.java:18](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L18), [CustomLibraryPage.java:9](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryPage.java#L9), [PreviewHomeRows.java:11](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L11), [PreviewSeriesJourney.java:13](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewSeriesJourney.java#L13), [ViewingHistory.java:16](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/ViewingHistory.java#L16), [UserCollectionStore.java:16](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/collections/UserCollectionStore.java#L16).

| Feature | Existing ownership / shared services | Important distinction |
|---|---|---|
| Movies and TV grids | PreviewPages + PreviewLibraryColumns/Header, snapshot/filter/sort state | Shared renderer exists; do not propose creating one from scratch |
| Custom Library page | CustomLibraryPage configuration, common renderer and editor guide | Page configuration is not the same as MediaLib user_collection tables |
| Inherited movie collections | ScraperStore.MovieCollections and collection loaders | Provider movie franchises coexist with user-created membership |
| Continue Watching / next episode | ViewingHistory identity, PreviewSeriesJourney and local availability | Completed season→next season intent differs from resumed file bookmark |
| Watch Next / custom Home rows | PreviewHomeRows preferences and owner limits | Automatic row caps differ from explicit user row limits |
| Search | PreviewSearch/search index / PreviewSearchText + inherited provider path | Search indexing/filtering and people metadata need dataset coverage; explanation text alone is not proof |
| Local metadata badges | PreviewMediaInfo/DetailsFacts + MediaLib native track metadata | Provider values cannot certify file codec/SDH/HDR or storage presence |
| Enrichment | PreviewEnrichmentQueue SQLite jobs and PreviewMetadataCache AtomicFile envelopes | Caches may return stale usable data; fresh() distinguishes that from successful refresh |
| Artwork | Picasso/custom handlers, ArtworkRequest diagnostics, PreviewBackdrop/Frost, scraper artwork | Fetch/decode/cancel/blur cost must be measured separately; no visual replacement authorized |
| Subtitles | Local track availability vs SubtitleDiscovery cache/search/download | Cached provider result is not a local subtitle; search readiness does not consume/download automatically |
| NFO/export | MediaLib XmlDb, NfoExportService and associated files | Filename/path canonicalization and credential-safe logging affect network identity |
| Trakt | Integration service and durable local history reconciliation | Local tracking preferences, private mode and remote watched state have distinct semantics |

## Background scan/import evidence

**VERIFIED:** custom MediaLib source serializes network scans and coalesces bursts into one follow-up; explicit requests bypass stale reachability. Workflow overlays add ProviderDiscoveryGate lock, staged source policy and structured scan/local import events. Final `medialib-next-qa.patch` adds `_id > afterId` traversal to avoid repeatedly revisiting rows when eligibility does not change. These effective sources must be preserved when adopting upstream scanner changes.

Sources: [MediaLib/NetworkScannerServiceVideo.java:86](https://github.com/heretoecode/aos-MediaLib/blob/5758074049bc153ac4967b4ecd21886469270076/src/com/archos/mediaprovider/video/NetworkScannerServiceVideo.java#L86), [medialib-preview417.patch:109](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/.github/build/medialib-preview417.patch#L109), [medialib-next-qa.patch:7](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/.github/build/medialib-next-qa.patch#L7), [medialib-next-refinement.patch:30](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/.github/build/medialib-next-refinement.patch#L30).

**VERIFIED:** metadata cache has 64 read locks and separate 64 fetch locks; per-file read bound 3MiB; 7-day freshness; locale-specific filenames; failed fetch falls back to old usable metadata. Enrichment queue has FOREGROUND/HOME/CURRENT_PAGE/NEXT/BACKGROUND priority, section-by-section execution, viewport priority reset and SQLite schema 2. SubtitleDiscovery has independent serial worker, default-off enabled gate, private-mode/integration check and persistent positive/negative cache. These are different systems with different freshness/rate behavior, not automatic duplicates.

Sources: [PreviewMetadataCache.java:13](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewMetadataCache.java#L13), [PreviewEnrichmentQueue.java:13](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewEnrichmentQueue.java#L13), [SubtitleDiscovery.java:27](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SubtitleDiscovery.java#L27).

## Risks and evidence limits

- **VERIFIED:** Supernova vs upstream DB62 schema collision; genre-ID support cannot simply adopt current upstream MediaLib. See report 08.
- **VERIFIED source characteristic / INFERRED maintenance risk:** metadata cache bounds individual reads but no global cache eviction/size cap appears in this class. Locale/ID/section combinations can increase files over time. Establish storage-growth measurements and inspect external cleanup callers before calling it a leak.
- **INFERRED:** snapshot rebuilding and eager per-section enrichment can increase memory/CPU during large imports; priority queue exists, so first measure queue depth/request count/cache hit rate and repeated viewport churn.
- **UNKNOWN:** current owner data completeness, duplicate DB rows, actual search accuracy, artwork cache hit rates, scan performance and historical incident cause. Synthetic QA fixtures are not the user's library.
- **VERIFIED:** old MovieCollections/classic list code remains in the tree and manifest. Provider collection routes/custom collection compatibility forbid assuming “Collections” is obsolete.

## Upstream changes and proposed adaptation

Upstream MediaLib `8a405189...` adjusts scanner matching of encoded/raw paths and quotes XML basename regex; the technical fix is useful but added URI/name logging must not be copied into safe diagnostics. `7875a0ba...` defers category row refresh during bulk scan/scrape. Supernova already gates discovery and has custom snapshot timing, so reconcile final notification ownership and lock order rather than add a second independent suppressor.

Upstream genre-ID `552d52b8...` and Video `114e979f...` can make filters stable across localization after an approved successor schema. Upstream list fixes `4bc4be83...`/`5d73519c...` affect inherited Lists; verify their actual custom UI entry before adaptation. Post-release grouped-delete `27961c40...` is a current-development candidate, not confirmed 6.5.5 shipped behavior. Upstream layouts, resource density and sorting controls must not replace Supernova's existing interface.

**PROPOSED** regression checkpoints: complete movies/TV/custom collection before/after row and membership counts; stable identity progress across unavailable source and reimport; all sort keys including Air Date ties/pre-1970/missing dates and refresh; matched metadata without artwork; full-show/season rematch; unknown genre localization; no provider request on library import when subtitle discovery is off; private-mode no track/search side effects; large scan refresh counts and 10k-item navigation latency. Pass criteria must include preservation of user collections/history and focus, not only fewer rebuilt rows.
