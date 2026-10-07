# Immediate Home hotfix — implementation and conformance

Authority: latest “Immediate Home hotfix turnaround — physical Shield QA (7 October 2026)” in `next-version-authority.md`, and HOME §12. Continued exact live fixes branch `eea74f740fcbe5c4325a0917e0241aa34210418e`, preserving existing work. No main merge, identity/signing transition, unrelated corrections or subsequent release.

This is a validation checkpoint, not a delivered candidate or physical acceptance. H6 is already SHIELD ACCEPTED on the user's tested candidate and is regression-protected; no new physical acceptance is assigned.

| Item | Implementation | Validation / physical follow-up |
|---|---|---|
| H1 | Featured extends downward; neighbours match active height/top/bottom; hero reserves heading + ~60% of 105dp thumbnail below it | Actual viewport/equal-boundary render test; Shield composition pending |
| H2 | Home artwork listener always receives null; Featured art remains inside rounded card; removes background motion dispatch from Featured changes | Listener regression and rounded-card render; real artwork on Shield pending |
| H3 | Home row wrappers do not clip descendants; ordinary rails span physical width with original leading content inset retained as padding; Featured neighbours draw past wrapper bounds | Actual artwork pixel beyond old inset; first/middle/last focus checks and Shield edge QA pending |
| H4 | More Info is the only Featured action; LEFT/RIGHT remains item browsing; existing Details entry is reused | No Play/Resume action assertions, normal Details/focus regressions; Shield pending |
| H5 | Fixed 86dp title/logo zone; 24dp breathing space before metadata and 12dp before synopsis; unchanged typography; action at lower edge | Wide/tall/compact logo layout renders; movie/TV and real logo Shield review pending |
| H6 | Accepted card presenter scaling/mask/foreground is unchanged | Existing focused-card artwork/boundary/glow regressions retained and rerun; protect physical acceptance |
| H7 | Move/Hide are real sibling controls inside the row, with no surface/elevation panel; media lane narrows while controls are entered; close restores full lane and exact opener | Actual row ancestry/alignment/Up/Down/Back/Right tests, existing Move/reorder exact return; Shield pending |

Changed production components: `PreviewFeaturedCard.java`, `PreviewPages.java`. Card artwork/focus presenter, playback, Details, Settings, providers, metadata/native patch and dependency/application/signing configuration are unchanged. Row rail lookup now uses actual child type to support sibling controls; asynchronous updates preserve that hierarchy. The previous fake-row “inline” test was replaced by actual attached row hierarchy/focus tests, rather than treating a semantic command tag as visual proof. Superseded two-action Featured expectation now checks one action, retaining rounded-corner/exposure geometry assertions.

New regression suite: `PreviewHomeHotfixTest`; updated `PreviewCorrectiveAuthorityTest`; retained `PreviewLibraryReturnTest`, `PreviewCardArtworkRetryTest` and complete existing Home/navigation coverage. Local diff checks pass. Complete source/full/build/render/native/lint/upgrade/signature gates and exact replacement APK identity are pending.

Physical Shield remains the authority for visual/interaction corrections. Synthetic artwork/logo renders prove bounds/hierarchy only. Do not mark H1–H5/H7 SHIELD ACCEPTED without the user's follow-up QA.

## First source/render checkpoint

At `b8371528a88ffb91c447e5cbf24c19783d05a544`, source run 37613261692 is SUCCESS: 148 targeted and 444 complete Video tests pass, followed by stability/library and WebDAV gates. Actual render review confirms equal Featured top/bottom boundaries, downward enlargement, only the Continue Watching heading/~60% artwork teaser, one lower More Info action and the larger fixed title-to-metadata spacing. Inline controls are actual row siblings; the edge test proves artwork is visible at x=10 beyond the old x=28 inset.

Final fixture strengthening uses the actual OfficialTitleArtwork Logo foreground drawable for wide/tall/compact shapes, includes TV metadata, paints synthetic images for composition review, verifies first/middle/last focused unit scale/parent alignment and adds actual Hide/library-preservation/valid-focus coverage. These retain the approved bounds/focus assertions. Final source/build validation remains pending; the earlier signed-build run is superseded by this test-only checkpoint before candidate production.
