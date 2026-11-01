# Supernova 0.148 Shield QA — Reception panel missing reviews / empty space

**Owner evidence:** screenshot `IMG_6C3933CB-3985-4873-B108-6C58B1E7D8D2.jpeg` from installed 0.148 on Nvidia Shield. Screenshot is in conversation, **not verified uploaded to GitHub**.

## Observed
The movie Details Reception column displays `TMDb 7.7 / 10` near the top and `527 votes` at the bottom, with a large otherwise blank area. Owner reports the panel previously had more information and expects reviews to appear. This is a **suspected regression** in content loading/rendering, not proof from one image that the provider has review records for this specific title.

## Required investigation
- Compare 0.148 Reception view/model, metadata retrieval, review providers, cache and loading lifecycle against earlier working versions and approved design. Determine whether reviews exist for this exact item, are fetched but dropped, hidden, or never requested.
- If reviews are available, render review text/attribution according to the approved Reception design; preserve rating/vote information. If no reviews are available, use a compact and honest empty state rather than a tall blank panel. Do not fabricate review data.
- Check content measurement, focus/scroll clipping, error/empty/loading state and asynchronous update on Shield; verify for both movies and TV where applicable.
- Add tests for review available, loading, empty, failed request and returning to Details; validate physically on a signed QA build and obtain owner acceptance before marking fixed.
- Preserve PR29 draft, existing user data and main. Documentation only; no application changes here.
