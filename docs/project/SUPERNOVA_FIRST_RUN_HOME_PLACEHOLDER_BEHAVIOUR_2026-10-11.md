# Supernova — Approved first-run Home placeholder interactions

**Recorded:** 2026-10-11
**Status:** USER-APPROVED DESIGN RULES — parked for consolidated first-run redesign handover. This is not authorisation to modify the active Codex implementation branch or issue a new build.

## Intent
On a fresh installation, show the normal Supernova Home layout immediately, rather than a compressed three-column folder-browser onboarding page. Show realistic dark-blue placeholders until the user explicitly adds and confirms library folders via Network & Files. Preserve existing app-wide typography, hero/poster geometry, layout, focus glow, enlargement and D-pad animations. Never fabricate film/TV metadata.

## Confirmed placeholder behaviour
- **Hero cards:** deep/dark blue artwork surfaces, identical dimensions, focus enlargement and animations to real hero media. Show **subtle neutral skeleton/placeholder lines** where title/metadata would ordinarily appear; no invented titles, dates, genres, episode descriptions, ratings or art.
- **Hero More Info:** preserve its existing placement and focus behaviour. On a placeholder, activate a compact **information dialog** explaining that media details will appear after the user adds their library. Include a prominent **Network & Files** shortcut and a secondary dismiss/continue-browsing action. Do not navigate to a non-existent details page.
- **Recently Added:** show dark-blue poster placeholders using normal row/card geometry, spacing and focus behaviour. Selecting any placeholder shows the **same shared explanation dialog**; no playback, resume or details lookup.
- **Other placeholder selection:** never call a player, media details, metadata fetch, watch history or resume operation. No fake media IDs. Placeholder entries should be typed as UI-only, distinct from persisted media models.
- **D-pad:** the focus system must treat placeholders as genuine focusable surfaces for navigation, but interaction is intercepted before any media action. Dialog takes focus, Back/dismiss restores focus to the originating card/button. Avoid flashing, focus traps or scroll jumps.
- **Network & Files shortcut:** opens the existing complete Network & Files page for local folders, drives and network shares. Merely opening that page **must not start scanning**. Scanning begins only after the user explicitly selects and confirms folders.
- **During scan:** preserve the Home layout and show real media progressively as valid items are discovered, replacing placeholders without an abrupt empty page or focus jump. Never claim scan completion until confirmed.
- **Continue Watching:** no fabricated Continue Watching row; show it only with real playback history.

## Suggested shared dialog copy (wording adjustable)
Title: **No media details yet**
Body: **Your movies and TV shows will appear here once you've added folders to your library.**
Primary: **Network & Files**
Secondary: **Continue Browsing**

The shared information dialog uses the global Supernova dialog typography, spacing, borders, backdrop and focus tokens. It may omit the large application icon used by the first-launch welcome variant; do not make every information dialog overly tall.

## Other approved first-run design decisions
- Welcome dialog over subtle blurred/dimmed Home: actual Supernova app icon, uppercase brand, `Welcome to Supernova`, concise setup explanation, primary **Network & Files**, secondary **Explore Supernova**.
- Dismissing welcome allows exploration of placeholders; first-run dismissal persisted and reset on app-data clearing.
- Existing shared Network & Files foundation requires independent focus/D-pad fixes based on user's observed 0.136 Shield testing.
- Installed 0.136 QA was observed not to initiate a scan before folder confirmation; preserve that invariant.

## Engineering acceptance checks
- Verify 1080p/4K Shield UI screenshot alignment with the **real populated Home** geometry and approved welcome/dialog concept.
- Test Up/Down/Left/Right and focus return on all placeholder types and More Info, including opening/dismissing shared dialog.
- Confirm no media playback, details screen, metadata lookup, persistence, scan or background indexing is triggered by placeholders.
- Confirm first real media arrival does not cause blank-screen flash or lost focus.
- Test navigation to Network & Files, back to Home, and no scan before explicit confirmation.
- Ensure accessible labels distinguish placeholder cards from playable media.

## Related documents
- [Global dialog design standard](SUPERNOVA_GLOBAL_DIALOG_DESIGN_STANDARD_2026-10-11.md)
- [Parked discussions register](PARKED_OUTSTANDING_PRODUCT_DISCUSSIONS_2026-10-11.md)

This document captures **approved behaviour** for later consolidated onboarding implementation, not a claim that it is already shipped.
