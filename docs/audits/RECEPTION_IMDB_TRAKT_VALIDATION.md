# Reception source validation — 7 October 2026

Status: **technical research completed; integration NOT implemented or Shield accepted.** This is a planning record, not authorization to alter the fixes candidate.

## Approved product policy
- Real file runtime and technical facts are authoritative where inspectable.
- Format air/release dates in human-readable British ordinal form, e.g. 10th October 2017.
- Preserve fixed Key/Technical/Reception field geometry; show **Unknown** for unavailable fixed fields. Optional editorial excerpts may be omitted.
- Show sourced TMDb rating and votes together on one line, e.g. `TMDb · 8.1/10 from 101 votes`.
- Multiple independent reliable Reception ratings preferred; do not manufacture entries.
- Trakt reviews: ideally one attributed, useful, spoiler-safe excerpt, without padding Reception or disturbing inherited Nova Trakt functionality.

## IMDb — validated routes and constraints
1. IMDb official licensed GraphQL API (AWS Data Exchange) exposes `title(id: "tt..."){ratingsSummary{aggregateRating voteCount}}`. Requires an AWS account, subscribed product/entitlement, API key, and applicable licence; not available simply because TMDb yields an IMDb ID.
2. IMDb official daily `title.ratings.tsv.gz` dataset contains `tconst`, `averageRating`, `numVotes`. It is explicitly **personal/non-commercial**; reuse/redistribution and attribution restrictions apply. Do not embed, mirror or publish this as a general-purpose app database without licence clearance.
3. IMDb forbids website scraping as a workaround.
4. Current Preview Details enrichment fetches TMDb title/credits/classification etc.; there is no verified direct IMDb ratings/votes feed. External IMDb IDs are identifiers, not rating values.
5. Recommendation: TMDb live Reception first; IMDb rating conditional on authorised licensed source, cost/terms verification and per-title ID matching. If unavailable, fixed IMDb slot says Unknown if displayed; never substitute TMDb as IMDb.

Official sources:
- https://www.imdb.com/interfaces/
- https://help.imdb.com/article/imdb/general-information/can-i-use-imdb-data-in-my-software/G5JTRESSHJBBHTGX
- https://data.imdb.com/documentation/api-documentation/getting-access/
- https://data.imdb.com/documentation/api-documentation/calling-the-api/

## Trakt — validation and remaining checks
- Inherited Nova Trakt auth/scraper functionality must be preserved. Existing Trakt login does not establish a new Reception review fetcher.
- Trakt permits proportionate in-app API integrations with appropriate app credentials, branding, caching and rate-limit handling, but forbids bulk harvesting, redistribution, credential sharing and use in apps promoting piracy.
- Trakt public API operations may use app client ID without OAuth; verify the specific comments/reviews endpoint, fields, spoiler flag, attribution and API app entitlement with the **current** OpenAPI reference before implementing. Public-read status of the exact desired endpoint was NOT independently proven in this audit.
- Trakt docs migrated to https://docs.trakt.tv in June 2026. Rate-limit guidance: unauthenticated and authenticated GET buckets typically 500 calls / 5 minutes, with 429 Retry-After; cache/de-duplicate.
- Design: at most one attributed spoiler-safe review excerpt if supported/licensed. Avoid retrieving protected account information just to display public reviews. Never expose or borrow secrets.
- If a spoiler-safe review or permitted API access cannot be established, omit the optional review.
Sources:
- https://developer.trakt.tv/docs/api-use-policy
- https://developer.trakt.tv/docs/authentication-oauth
- https://developer.trakt.tv/docs/rate-limiting
- https://github.com/trakt/trakt-api/discussions/808

## Acceptance gates before coding
- Verify licensing/entitlement and costs for IMDb ratings, and provenance/freshness/attribution.
- Verify Trakt exact endpoint response shape, content rights, spoiler filtering and attribution with authorised credentials.
- Specify timeout, cache, offline/stale-good, source label and rematch invalidation.
- Preserve existing Trakt functionality, Details focus and layout; test Shield with populated/missing/offline/failed data.
- Do not merge, change signing/identity or expand the current fixes candidate on this record alone.

## Final decision — IMDb excluded (7 October 2026)
**SUPERSEDES earlier conditional/approved IMDb Reception language in this document.** Do not display IMDb rating, vote count or Unknown placeholder in Reception. Do not pursue IMDb ratings integrations or further licensing/API investigations. The project requires no subscriptions and practical API quotas; no verified suitable source exists. Internal IMDb title identifiers may remain for matching and cross-references. TMDb and separately validated Trakt reception data remain distinct.

## Trakt endpoint research — 7 October 2026 (superseding earlier endpoint uncertainty)
- Documented official movie ratings: `GET /movies/{id}/ratings` and show ratings: `GET /shows/{id}/ratings`, each returning Trakt score 0–10, vote count and distribution.
- Documented movie comments: `GET /movies/{id}/comments/{sort}`, OAuth optional, paginated, language filter. Official legacy docs also describe show comments `GET /shows/{id}/comments/{sort}`, OAuth optional. Comment records have spoiler and review flags and author metadata; a live authorised response still needs confirmation.
- API GET allowance: 500 per 5 minutes per authenticated user or per client ID + public IP unauthenticated; honour 429 Retry-After, coalesce and cache.
- Policy permits in-app public discovery and proportionate caching, prohibits bulk redistribution, scraping and piracy-promoting integrations. Assess Supernova's actual use against these terms.
- **Critical new access caveat:** July–September 2026 third-party developer reports say new API app creation requires Trakt VIP. Official create-app guide does not clearly establish whether this is still enforced. Existing inherited Nova client credentials do not automatically grant permission for a new Reception use; verify ownership, scope and terms before reuse. Never borrow another app's credentials.
- **Decision gate:** no paid subscription. Trakt Reception is feasible in API design but **NOT approved as operationally validated** until free authorised app access or permitted existing client use is demonstrated. Do not add an unverified Trakt reviews/ratings requirement to the current fixes-only release. If no qualifying access, exclude new Reception integration, preserve inherited Nova behaviour.
- **Important:** Official Trakt rating endpoint documentation also describes `extended=all` returning external IMDb/Rotten Tomatoes/etc ratings. **Do not display or re-enable those sources**: earlier user decision explicitly excludes IMDb and other rejected ratings providers; their appearance in a Trakt response is not permission to use them.
References:
https://docs.trakt.tv/reference/getmoviesratings
https://docs.trakt.tv/reference/getshowsratings
https://docs.trakt.tv/reference/getmoviescomments
https://developer.trakt.tv/docs/rate-limiting
https://developer.trakt.tv/docs/api-use-policy
https://developer.trakt.tv/docs/create-an-app
https://github.com/euzu/tuliprox/issues/853

## Focused inherited-Nova Trakt code audit — 7 October 2026
Read-only inspection of `src/main/java/com/archos/mediacenter/video/utils/TraktDeviceAuthActivity.java`, `TraktSigninDialogPreference.java`, `build.gradle`, `settings.gradle` and the branch file tree:
- Existing UI initiates TV device-code OAuth or phone browser-based OAuth and stores access/refresh tokens after success; this is **personal account sign-in**, not proof of a public ratings/reviews data feed.
- Successful login deliberately sets `KEY_TRAKT_SYNC_COLLECTION=false` to avoid collection-sync limits. Preserve existing behaviour.
- Calls to `com.archos.mediacenter.utils.trakt.Trakt` include device code generation, code/token exchange and token persistence.
- The Trakt client class is **not in the current Supernova repository tree**. `settings.gradle` references sibling modules `FileCoreLibrary` and `MediaLib`; the missing class's actual origin, client ID and usage rights are not established by these files. Do not assert the credentials are reusable.
- No Trakt Reception fetcher was established by this focused inspection; no runtime authenticated API call performed; no credentials exposed.
**Result:** Existing inherited account integration confirmed; authorisation for a new login-free Reception consumer **UNVERIFIED**. Do not require end users to create API apps or sign in for public ratings; do not repurpose unknown credentials without permission. No production changes.

## Registration cost/access determination — 7 October 2026
**Conclusion: new Trakt application credentials cannot currently be verified as obtainable without VIP membership.** Trakt forum reports and developer-maintainer confirmations indicate that new app creation is currently VIP-only, with some free-account registrations revoked. Public endpoints still require an app client ID; OAuth may be optional for the end user, but that does not remove the developer-registration requirement.
- Official app guide: https://developer.trakt.tv/docs/create-an-app (requires client ID and verified GitHub account; does not explicitly describe current VIP gating).
- Trakt support forum (Aug 25, 2026): https://forums.trakt.tv/t/failure-to-register-an-api/116157/30 (VIP feature for now).
- Other developer impact: https://github.com/euzu/tuliprox/issues/853 (Sep 1, 2026).
- Official authentication: https://developer.trakt.tv/docs/authentication-oauth (public endpoints require app key).
- Official policy: https://developer.trakt.tv/docs/api-use-policy (do not reuse other apps' credentials or circumvent restrictions; support contact for clarification).
**Decision:** No subscription, no user-created API apps, no user sign-in for public Reception. Therefore **block new Trakt Reception integration** pending a genuinely free authorised app registration path or explicit permission from Trakt. Preserve inherited Nova Trakt account sign-in and scrobbling. Do not implement Trakt Reception or add an empty placeholder; proceed with TMDb Reception only. Revisit only if Trakt officially changes access policy or authorises a free client for Supernova.

## User decision — 7 October 2026
Trakt is **parked as a proposed new metadata/Reception provider** because free authorised developer credentials are not available on acceptable terms. No Trakt ratings, votes, reviews, UI slots, placeholders, new API integration or further credential/licensing investigation in the current plan. **TMDb alone** supplies Reception ratings/votes. Preserve inherited Nova Trakt account sign-in/scrobbling functionality unchanged; it is outside this provider decision. Revisit only on user's explicit request. This supersedes all provisional Trakt Reception proposals.
