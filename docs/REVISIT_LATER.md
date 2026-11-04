# To revisit later

Ideas and decisions the owner wants to come back to. Check this list when planning each release.

## Ratings without a personal OMDb key — added 4 Nov 2026
- **Decision (owner):** do not build an OMDb key into the app for now. Reception's IMDb, Rotten Tomatoes and
  Metacritic scores keep using the key typed into Settings (`omdb_api_key`, `metadata/OmdbSupplement.java`,
  900 lookups/day budget).
- TMDB "just works" because Supernova uses NOVA's own TMDB key shipped in the app
  (`aos-MediaLib/res/values/donottranslate.xml`).
- Agreed for 0.156: OMDb ratings are saved per title and refreshed at most weekly
  (`leanback/details/PreviewDetailsData.java`).
- **Options:** (1) one key built in at build time from a GitHub secret, so ratings need no user action (free tier
  1,000/day, paid tier more; a key inside an APK can be extracted); (2) MDBList (adds Letterboxd/Trakt; also a key);
  (3) keyless scores only (TMDB, Trakt) without IMDb/RT/Metacritic.
- **Revisit when:** Supernova is shared with anyone else, or the daily limit is reached.
- Notes: `docs/0.155/OWNER_NOTES.md` item 34, `docs/0.155/DATA_PROVIDERS.md`.

## Diagnostics privacy — added 4 Nov 2026
- Diagnostics record everything in full detail because the owner is the only user. Cut back (or make opt-in) before
  Supernova is ever shared. Notes: `docs/0.155/OWNER_NOTES.md` item 29, `docs/codex/BRIEF_0156_THUMBS_DIAGNOSTICS.md`.
