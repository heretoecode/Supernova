# Supernova 0.135 — User checklist scope confirmation

Date: 2026-10-09
Status: **CONFIRMED PLANNING SELECTIONS ONLY — NO CODEX HANDOVER**
Source: User-provided checkbox selections in main Supernova planning conversation.

**Counts: 34 checked / confirmed for inclusion; 10 unticked / not yet approved by this checklist.**

## Checked — confirmed for inclusion in 0.135

### Home & First Launch
- [x] Empty-library welcome screen with Add Media
- [x] Show Building Your Library while scanning
- [x] Hide Customise Home until media is imported
- [x] Fix hero carousel dimming/flashing transitions
- [x] Improve Home Move/Hide controls and row customisation

### Search
- [x] Implement approved Search page mock-up
- [x] Retain approved on-screen keyboard and D-pad navigation
- [x] Match titles without exact punctuation or leading articles
- [x] Support alternate titles and minor spelling mistakes
- [x] Search cast, crew, studios and networks where metadata exists

### Movies & TV Shows
- [x] Boxed Filter, Sort, Order, View and Columns controls
- [x] Centre the selected poster row vertically
- [x] Preserve working artwork zoom and horizontal row continuation

### Playback & Continue Watching
- [x] Simplify HUD to Subtitles, Audio, Play/Pause and More
- [x] Improve subtitle track labels and appearance controls
- [x] Add Restart Movie/Episode
- [x] Clarify Remove from Continue Watching Row action

### Settings & Navigation
- [x] Restore coloured streaming-provider icons
- [x] Improve language flags
- [x] Fix Open Source Licenses freezing/crashing

### Details Page
- [x] Improve Details, Extras and More Like This navigation
- [x] Reduce delays loading related information
- [x] Improve streaming provider buttons and logos
- [x] Refine Cast & Crew layout
- [x] Use approved expanded-information mock-up

### Custom Library Page
- [x] Create a New Page landing screen
- [x] One custom page after Home, Movies and TV Shows
- [x] Five-step creation wizard
- [x] Choose page name, icon and content type
- [x] Configure dynamic filters and display options
- [x] Provide live preview and Review step
- [x] Edit and rename existing page
- [x] Delete page with confirmation
- [x] Protect unsaved changes and support D-pad navigation

## Unticked — awaiting user decision; NOT authorised for inclusion by this checklist

### Network & Files
- [ ] Three-panel page redesign
- [ ] Simplify adding network sources and media folders
- [ ] Fix unreliable or incomplete library importing
- [ ] Make Scan Library start immediately
- [ ] Show clear scan progress, results and history

### Playback & Continue Watching
- [ ] Remove Info and unnecessary Settings links from HUD

### Settings & Navigation
- [ ] Correct top-navigation clock font and size
- [ ] Implement three-panel Settings navigation
- [ ] Correct Advanced Diagnostics navigation

### Details Page
- [ ] Correct metadata alignment

## Interpretation and guardrails

- Checked means **approved planning scope**, not implemented, tested or handed to Codex.
- Unticked means **not selected in this confirmation round**, not automatically rejected forever, and not permission to remove or regress existing functionality.
- The five Network & Files items were all unticked. Earlier project planning had marked reliable import as a high priority; this latest explicit checklist does not approve those items. Seek a separate user decision before placing them in the final 0.135 handover.
- The four-button HUD is checked, but the separate item “Remove Info and unnecessary Settings links from HUD” is unticked. Do not treat that independent removal item as newly approved; reconcile the design dependency with the user before implementation.
- “Preserve working artwork zoom and horizontal row continuation” is a **regression-protection requirement**, not an assertion those behaviours are broken.
- Custom Library Page full creation/editing was separately approved and is confirmed here. Its authoritative specifications and two original approved images are linked below.
- No changes to application code, APK, protected branches, signing or release state are authorised by this documentation update. Do not initiate a Codex handover until the user explicitly requests it.

## Design references

- [Custom Library Page approval](SUPERNOVA_0.135_CUSTOM_LIBRARY_PAGE_APPROVAL_2026-10-09.md)
- [Approved Search mock-up](../design/approved-mockups/search-page-approved.png)
- [Approved Details expanded-information mock-up](../design/approved-mockups/details/details-page-expanded-information-approved.png)
- [Custom Library Page creation-options mock-up](../design/approved-mockups/custom-library-page/library-creation-options-approved.png)
- [Custom Library Page default landing-page mock-up](../design/approved-mockups/custom-library-page/default-landing-page-approved.png)
- [Recent product decisions](../design/RECENT_PRODUCT_DECISIONS_2026-10-08.md)
