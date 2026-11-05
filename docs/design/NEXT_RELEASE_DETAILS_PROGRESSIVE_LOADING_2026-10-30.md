# Next release — details page progressive loading and transition (Shield QA, 2026-10-30)

**Status:** Owner-reported physical Shield issue, investigation and implementation pending. Do not alter signed 0.147, PR #27, main or signing protections.

## Evidence
Owner supplied 4.83-second video IMG_5068.mp4 showing opening the TV programme **Marshals** from the Home hero into its details page. Hero artwork, metadata/text, action elements and tabs/sections below become visible in separate stages after the page is opened. The result feels slow and visibly assembles in front of the viewer. Video reviewed in chat; the original clip is not claimed to be uploaded to GitHub. Root cause and exact latency are not yet established.

## Required next-release behaviour
- Prepare details data, image and page layout before or during focus on the selected media when reasonably available. Prefer prefetch/caching for currently focused items and reuse existing data from Home/library cards; avoid duplicate expensive fetches.
- Enter details as a coherent, stable composition with hero, title/metadata, actions, tabs and lower layout positioned consistently. Avoid late tabs popping in, hero flicker, text shifting and sequential unrelated fades.
- Do NOT block opening indefinitely waiting for slow/offline metadata/artwork. Use bounded preloading, a stable intentional placeholder/skeleton when needed and one coordinated transition when essential content is ready; preserve responsive Back and D-pad.
- Measure where time is spent: navigation, database lookup, metadata provider, image decode/cache, main-thread/layout, tab creation and async callbacks. Distinguish network wait from UI rendering and avoid guessing a cause.
- Ensure stale requests from previous focused items cannot overwrite current details, preserve correct film/TV/custom page variants and existing approved layout. Do not change Home hero or remove details artwork.
- Test warm and cold cache, online/offline, missing art, rapid switching, entry from Home and libraries, 720p/1080p/4K, focus, Back and Shield actual timing. Include regression tests and before/after recordings. No claim of fixed until physical Shield verification.

Related next-release QA: NEXT_RELEASE_ADDITIONAL_SHIELD_QA_2026-10-30.md and NEXT_RELEASE_0_147_SHIELD_FINDINGS_AND_LIBRARY_BACKGROUND_2026-10-30.md.
