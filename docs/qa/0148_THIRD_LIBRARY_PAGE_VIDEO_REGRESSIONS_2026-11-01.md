# 0.148 Shield QA — third library page regressions (1 November 2026)

**Evidence:** Owner-supplied 29.9-second portrait phone recording `IMG_5074.mp4` of Nvidia Shield running installed Supernova 0.148. Video visually inspected at multiple points (0–27s). Original video is attached to ChatGPT, **not verified stored in GitHub**. Do not claim this evidence is uploaded to repo.

## QA-0148-LIB-01 — Statistics not correctly visible on initial load (REOPEN)
Owner reports third library page statistics remain hidden until scrolling down, then scrolling back up. Video shows top statistics/header and poster grid changing relative visibility during scrolling. This behaviour was previously reported and claimed fixed for 0.148; treat as a **failed regression fix** until independently reproduced. Investigate initial scroll offset, layout measurement/insets, header visibility, focus-triggered scroll and delayed data binding. Do not hide or fake stats to pass test.

## QA-0148-FOCUS-02 — Transient focus flicker Home ↔ Filters on third library entry (REOPEN)
Owner observes highlight oscillating between Home and Filters before settling on Filters. Video shows unstable initial navigation/focus state near the top of the page. Root cause **not proven** by recording. Trace initial focus request order, nav selection vs focus distinction, delayed asynchronous library load, state restoration and competing focus requests. Distinguish actual focus from selected-tab indicator. Focus must settle deterministically with no flash and remote D-pad must work immediately.

## Codex acceptance and investigation
- Preserve exact source/version 0.148 baseline and PR29; no merge into main.
- Reproduce on third/custom library page on physical Shield; instrument focus transitions and layout/scroll positions at first entry and when returning from other pages.
- Add deterministic automated tests for cold/warm library entry, initial stats visibility without scroll, scroll down/up, Home/Filters focus stability, return navigation, and slow asynchronous content load.
- Compare with previous reported 0.147 symptoms and existing 0.148 changes. Do not mark fixed solely from unit tests or source edits; request owner physical retest with a new signed QA build after validated changes.
- Do not modify other branches or clear library data. Record exact code commits, screenshots/video evidence and validation.
