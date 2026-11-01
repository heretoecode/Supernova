# Supernova 0.148 Shield QA — streaming provider icons

**Evidence:** owner-supplied photo `IMG_39C6DBBC-74E2-4868-BFBC-F56A2D68BD17.jpeg`, showing Settings → Streaming services → My providers. The photograph is available in conversation, **not verified uploaded to GitHub**.

## Observed
Amazon Prime Video, Disney Plus and Netflix have distinct recognisable icons. Other visible provider entries (Apple TV Store, Hayu, JustWatch TV Free, JustWatch TV Plus, JustWatch TV Store) show the same generic broadcast/wireless-looking icon. The **Done** action also shows this icon, suggesting an inappropriate fallback applied to a non-provider action. The screenshot alone cannot prove whether provider-specific assets exist for all entries or the root cause.

## Required investigation and acceptance
- Audit provider ID → icon/logo mapping, available assets, image loading, fallbacks and row types in the My providers selection panel. Compare with existing approved design and prior reports.
- Where a verified provider logo asset exists, render the correct recognisable logo at consistent size and legible contrast. If absent, use a deliberate neutral provider fallback; do not invent or misattribute logos.
- Give **Done** a distinct action icon (or no icon if consistent with approved UI), never the provider fallback.
- Preserve provider selections, checked-state markers, D-pad focus and Done behaviour; do not mistake selection ticks for navigation-focus ticks.
- Add mapping/fallback and visual regression tests and verify on physical Shield in a signed QA build before closing. Investigate possible common icon resolution issue with missing language flags but track separately until proven.

Documentation only; no app code changes. Keep PR29 draft and main unchanged.
