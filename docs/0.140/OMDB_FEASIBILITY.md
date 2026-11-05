# OMDb supplementary-data audit — 16 October 2026

Owner decision: no authorised account or credential binding has been confirmed. Prepare the integration, but do not enable requests using an unverified key. This is a documented external dependency, not a claim of live integration.

## Verified API and rights evidence

- [Official API](https://www.omdbapi.com/): HTTPS JSON by exact IMDb `i` identifier, `type=movie|series|episode`, API key required. Supported supplementary fields include `Ratings` source/value pairs, `imdbRating`, `imdbVotes`, `Metascore` and `Awards`; absence and `Response=False` are possible. This API does not establish episode-specific awards, country content descriptors or licensing of provider artwork. Never infer award counts/categories from prose or use title-search matches as verified identity.
- [Key registration](https://www.omdbapi.com/apikey.aspx): free account advertises 1,000 daily requests; paid account capacity must be confirmed for the actual account. No account was registered and no key was requested or used in this task.
- [Legal terms](https://www.omdbapi.com/legal.htm): personal/non-commercial restrictions and retention of intellectual-property notices. The API site footer states CC BY-NC 4.0. These do not establish Supernova's right to redistribute third-party IMDb/Rotten Tomatoes/Metacritic data or logos in every distribution context. Obtain applicable provider confirmation/entitlement for the intended use and cache/redistribution policy. No scraping or assumed logo licence.

## Prepared implementation

`OmdbSupplement.PRODUCTION` has a hard denied access binding. `enabled()` is false; Details skips even the additional external-ID lookup. The adapter accepts an injected entitlement/key/budget interface for future provisioning and fixture tests. There is no embedded key, preference switch, runtime setter, new settings flow or secret in the build. Merely adding an environment variable does not enable this version.

The prepared transport accepts only exact `tt` identifiers and movie/series/episode types, uses HTTPS with redirects disabled, bounded timeouts and a 64 KiB response ceiling. JSON must return the exact requested IMDb ID and content type. It whitelists the three approved rating sources, validates numeric ranges, treats N/A/absent/invalid values as missing, preserves awards prose, and does not consume `Rated`, posters or descriptors. Data is not persisted. Exceptions and credential-bearing request URLs are not logged or passed to diagnostics.

The existing background Details enrichment seam can supply series-wide/movie ratings to the four approved Reception slots with visible “via OMDb” attribution. Season/episode pages never borrow the series supplement. Missing ratings keep stable placeholders. Live rendering and provider-brand attribution approval remain outstanding.

## Exact activation requirements

1. Owner supplies an authorised account and applicable use/attribution/cache entitlement for the actual distribution, with provider-artwork rights handled separately.
2. Choose a credential model: authorised per-user keys in Android Keystore-backed no-backup storage, or an authorised backend that retains the shared API key server-side. An APK/build secret is extractable and is not a secure shared-key distribution model. No credential is needed in this conversation.
3. Implement the selected secure binding behind `Access`, opt-in/revocation and privacy wording, with an atomic daily request budget matching that account, backoff for exhausted/invalid credentials, and only entitlement-permitted caching. This release deliberately leaves the binding denied.
4. Verify movie and series TMDb→IMDb external IDs, country/episode semantics, real successful/missing/quota/error responses, attribution and approved visuals using the authorised account. Review provider terms again when enabling.

The fixture suite covers denied access without reading credentials or opening transport, exhausted budget, invalid IDs/types, exact request identity, mismatched JSON identity/type, missing/out-of-range values, source whitelist and transport failure. Fixture values are test data, not retrieved ratings.
