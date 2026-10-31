# Next version — Details hero card spacing and metadata alignment

**Status:** User-reported visual regression; implementation pending. **Target:** next version after 0.143.

## Evidence
User supplied two Nvidia Shield photographs (Slow Horses TV series and Michael movie, 2026-10-28). The hero information block has inconsistent vertical spacing: metadata appears pushed too close to title/logo, and the user reports metadata can wrap onto two lines. Movie and TV show layouts differ depending on optional technical badges. These photographs support the visible alignment/spacing issue; exact pixel offsets and all wrap conditions require reproduction.

## Required behaviour
1. Establish a coherent, consistent vertical rhythm among title artwork/logo, single-line metadata, optional video/audio badges, synopsis and Play/More buttons on movie and TV Details hero cards.
2. **Metadata must stay on one line:** age certification, release year, genres, duration or season/episode counts, and TMDB rating where available. Use sensible priorities, ellipsis/truncation for overly long variable text, and adaptive widths; do not wrap to a second line or clip into backdrop/artwork.
3. Provide enough separation from title artwork without pushing the synopsis/actions outside the hero. When optional badges are absent, collapse their space cleanly; when present, maintain predictable spacing.
4. Preserve approved artwork/backdrop, existing hero style and action layout. Do not reintroduce written reviews; preserve ratings.
5. Test long/short movie and series titles, title images of different dimensions, missing fields, long genres, multiple seasons, technical badges present/absent, 720p/1080p/4K and enlarged text. Verify physical Shield screenshots against supplied examples and confirm no clipping or unwanted wrapping.

## Workflow
Add to next-version Codex handover. Documentation only; no application code or merge to main.
