# Outside services Supernova uses (from the code, 4 Nov 2026)

| Service | What for | Key / account | Backup if it fails |
|---|---|---|---|
| TMDB (`api.themoviedb.org`, `image.tmdb.org`) | Film/TV details, artwork, cast, trailer list, user reviews, TMDB score | App key | None (the library falls back to file names and local .nfo files) |
| JustWatch via TMDB | Where to Watch | Through TMDB | None; unknown is shown as unknown, never as unavailable |
| OMDb (`omdbapi.com`) | IMDb, Rotten Tomatoes, Metacritic scores and awards on Reception | Owner's own key in Settings; 900 requests/day budget | Without a key, Reception shows the TMDB score only |
| IntroDB (`api.introdb.app`) | Intro, recap and outro times for Skip | — | **Yes:** used together with TheIntroDB (`IntroDbManager`) |
| TheIntroDB (`api.theintrodb.org`) | Intro/recap when IntroDB has none; the only source of credits and preview times | — | Backup for IntroDB |
| ChaptersDB (`chaptersdb.com`) | Chapters for files with none of their own | — | **It is the backup:** the file's own chapters come first (`PlayerActivity.previewChapters`) |
| OpenSubtitles.com (`api.opensubtitles.com`) | Subtitle search and download | Owner's account | None (local and embedded subtitles still work) |
| Trakt (`api.trakt.tv`) | Optional watch history sync; Trending and Popular Home rows | Owner's account | None |
| YouTube (`youtube.com`) | Trailers and extras play in YouTube's embedded player; extra durations read from the public page | — | **Yes:** if the embedded player can't run, the trailer opens in YouTube instead (`PreviewTrailer`) |
| Put.io (`api.put.io`) | The owner's own files (a source, not a data provider) | Owner's account | — |
| Sentry | Crash reporting inherited from NOVA | Off: no `sentry.properties`, empty DSN | — |

Credits today (`assets/foundation/credits.json`) name only NOVA, Archos, TMDB, JustWatch, Trakt and OpenSubtitles.
Missing: **OMDb, IntroDB, TheIntroDB, ChaptersDB, YouTube** — added in mock-up `26`; to add to `credits.json` in 0.156
(check each service's attribution terms when coding; TMDB's required sentence stays word for word).
