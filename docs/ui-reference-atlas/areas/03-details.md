# 03 — Media Details, seasons, actions, metadata, extras and streaming

Source baseline: signed 0.138 `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. Evidence below is **VERIFIED static source** unless expressly attributed to native CI evidence or **UNKNOWN**. No device-navigation test was run. Stable IDs describe surfaces; generated SITE/FOCUS IDs identify exact source lines.

## Existing page families and controls

| ID | Surface / entry / conditional text | Existing behaviour and focus |
|---|---|---|
| DET-001 | Movie/Episode Details from Home/Library/Search/browser | PreviewMoviePage over VideoDetailsFragment data/actions. Cinematic title/metadata/synopsis and action row; local technical badges require real local metadata; episode context differs from movie. `Play`/resume caption from action adapter, `Streaming Services` when applicable, `More` |
| DET-002 | TV show / season / remote provider Details | TV native adapter/TvshowFragment, PreviewSeasonActivity and PreviewRemoteDetailsActivity use shared page. Show/remote-TV can expose `Seasons & Episodes`. Season More menu `Series Details`, `Episodes`; no fabricated local facts on remote-only page |
| DET-003 | Section/tab order | `Seasons & Episodes` if show/remote tv, then `Details`; `Extras` only playable extras, `More Like This` only related cards. Existing CI covers lower information density and read-only eight-person cap |
| DET-004 | Details facts | Source-driven identity, overview, Cast/Crew, genre/studio/language/rating, technical information, File/Source; missing metadata sections omitted or pending. `Budget (TMDb)` and `Box office (TMDb)` only positive enrichment. Actual API metadata values/names are runtime UNKNOWN; entire exact fact-label expression appendix provided |
| DET-005 | `Actions` menu | Restart Episode/Movie first if local movie+Activity; `Report Incorrect Match` if movie.hasScraperData. Group order `Playback & Navigation`, `Library`, `Streaming Services`, `Files & Media`, only populated eligible groups. Groups are nonfocusable. Native action adapter order retained within each group; not alphabetized |
| DET-006 | Action membership and mutations | Group0 Next Episode. Group1 watched/unwatched, index/unindex, scrape/change info, hide/unhide; adds `Add to Row` for local. Group3 delete/confirm delete, plus local `Subtitles and Artwork` or show `Artwork`. Streaming logo actions excluded from generic groups. Watched labels refresh from current adapter; root remains open |
| DET-007 | Incorrect match / rematch / artwork | Report flag `incorrect:<videoId>` → Library Health. Find a Match/manual scraper has shared keyboard/search candidate UI. Native retained subtitle/artwork pickers styled without replacing their handlers. Poster/backdrop/current metadata values conditional |
| DET-008 | Extras taxonomy | `Trailers`, `Teasers`, `Behind the Scenes`, `Featurettes`, `Interviews`, `Clips`, `Other Videos`; playable/link availability gates tabs/actions. Trailer uses actual provider key/type, not generic web imagery |
| DET-009 | People/recommendation/version/episode choice | Real indexed/enriched actors/crew/recommendations and available files/episodes; episode grouping/source selects actual media. Names, number of seasons, missing episodes, provider launch errors UNKNOWN without fixtures |

Focus: opener token captured before Details; return resolves media key/tag/position. Tabs/action controls and landscape/episode cards own focus boundaries; action menus use PreviewDialog restoration and live adapter refresh. Lower information paragraphs are deliberately read-only in current source and are not generic action targets. Source branches for navigation, cancellations and backdrop updates are indexed; every device-key sequence and unexpected content boundary remains UNKNOWN.

Native 0.138 existing [image index](https://github.com/heretoecode/Supernova/blob/16a60cb11a5dd20ebe374873db360e32b617d7b0/docs/0.138/ui/README.md) supplies five-sections, 150% text, local-track and small-viewport captures with their original fixture provenance. They are synthetic Android renders, not Shield screenshots. Full action ordering depends on VideoActionAdapter/TvshowActionAdapter and source/media state; these adapters' exact source/resource expressions are catalogued, with dynamic media effects explicitly pending runtime verification.

## Source owners and exact option appendix

- [PreviewMoviePage.private void more](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L561)
- [PreviewMoreActions.public static int group](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoreActions.java#L20)
- [VideoActionAdapter.ACTION_](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L39)
- [TvshowActionAdapter.ACTION_](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowActionAdapter.java#L34)
- [PreviewDetailsFacts.class](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewDetailsFacts.java#L7)
- [PreviewSeasonActivity.onCreate](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewSeasonActivity.java#L17)
- [VideoDetailsFragment.class](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsFragment.java#L167)
- [PreviewRemoteDetailsActivity.class](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/streaming/PreviewRemoteDetailsActivity.java#L12)

[Every matched display/choice expression in this area](../evidence/03-details-source-options.md) · [Full display sites](../evidence/display-sites.tsv) · [Focus branches](../evidence/focus-branches.tsv). Supplementary cross-area owners, including inherited utilities, appear in the full catalogue. Literal extraction does not establish visibility.
