# PR #29 continuation

Owner instruction: continue the existing implementation. HUD visual design, all-four-tab inline Settings and full ChaptersDB integration are approved. Resolve engineering choices using existing conventions. Do not reopen those approvals. Keep draft PR #29 and existing branch.

## Implemented continuation

Current source: `f5e2924cc8f7302032933f8aec8793030b6e1b0f`. The page registers its actual global focus listener while attached and removes it on detach, so leaving for global navigation clears pending preparation and returning to the same card can schedule it again. The unchanged real-event regression found that the earlier `clearChildFocus` attempt did not receive this transfer; that hook has been replaced. HUD indentation now matches surrounding tabs and release lint remains enabled.

- HUD: one row Play/Pause → flexible seek → total duration → Ends at → Settings. Existing 28dp safe edge, 44dp Play/Pause target, 24/4dp seek target/track, 116×40dp Settings pill and existing gear/font/colour assets. Title/logo middle-left; top-right clock preserved. Timestamp stays visible; actual chapters only. Seek initial focus; existing Up to Settings / Down to Play-Pause; opposite direction returns to seek. Left/Right continue existing seek policy; media keys unchanged. Ends at hides while paused/unknown and recomputes on resume/seek/speed; timezone refresh uses current timezone.
- Edit Library: four source-parity sections; direct rail selection; no Previous/Next/Cancel footer. Save in Review retains the existing in-panel action position; Delete stays in Page Details; Both remains Both. Retained collection is shown honestly in Review. Form/preview shell survives changes; semantic choice parent and section scroll restore. Rail scrolls for large text; fixed preview title and stationary frame surround scrolling statistics and a list of up to 20 actual indexed posters/items. Existing SharedThreePanel proportions/typography are retained pending the specific visual revision below; no generated-image geometry or new filters.
- Details: after 250ms settled focus, raise only that item to temporary priority between active Details and Home, restoring previous page priorities and retaining work progress. Existing queue/cache and identity mapping retained. Publish cached core before optional season/recommendation/provider disk work; keep same-frame merging and title-generation cancellation. Record first paint and core-cache timing alongside existing artwork/information/section diagnostics. Fingerprint caller already handled null; helper guards are defensive, not a claimed crash fix.
- Global Settings: one immediate travelling-rail boundary, neutral child surfaces and an explicit selected check; remove the permanent selected-outline overlay and in-flight Settings rail interpolation so only the active target draws a focus boundary. Other surfaces' animation remains unchanged. Physical ghost report still requires device verification.
- ChaptersDB: published IMDb movie and IMDb/TVDB series lookups verified working. Episode response must match the requested TVDB identity, parent relation and explicit season/episode; never infer from title alone. Normalise/sort/de-duplicate actual chapter times; reject malformed, conflicting and out-of-file starts; no synthetic zero. GET-only fixed HTTPS routes, response/cache bounds, 24-hour private cache, one-hour negative cache, seven-day offline fallback, 429 backoff and throttling; existing trusted HTTP configuration, no credentials/local paths sent. Embedded chapters take precedence and skip provider requests. Activity-scoped acquisition is cancelled on teardown; generation guards reject obsolete completion.

## ChaptersDB decision and observed limitation

Owner explicitly selected **Keep unverified sets hidden; preserve strict matching** during this continuation. Current v1 responses expose chapter starts, optional note, public IDs and votes; they do not provide total cut runtime/edition proof. `verifiedChapters()` therefore exposes no markers from these unverified v1 sets. Acquisition is wired, but this is not a claim of usable production chapter coverage or full edition alignment. A last chapter start is not duration, and the highest-voted set is not matching evidence. The Voyager DVD fixture labels Parallax S01E02 while aired ordering is S01E03; its episode TVDB identifiers are empty. Strong episode joins fail closed.

The official API page explicitly states: “All endpoints are free to use and require no authentication. The API returns JSON responses for easy integration with your applications.” This is an affirmative API-use statement. No separate data redistribution licence, published numerical rate allowance or author identity agreement was found; no bulk provider catalogue is packaged in the application or republished, and no redistribution right is asserted. Small sanitised response contracts are retained only as test fixtures. Conservative private cache/request limits are application engineering policy, not invented provider terms. No owner legal approval is being requested merely because a separate licence page was not discovered.

Observed contract fixtures in src/test/resources/chaptersdb are sanitised real GET responses; synthetic episode-ID mutations in tests are explicitly labelled. This technical limitation can only be removed with provider-supplied alignment evidence or a reliable independently verified per-edition contract, not by another design approval. The owner has already rejected an unverified override.

## Only remaining optional visual choices

The four-section structure and source parity are implemented using existing shared tokens. The proposed **new** 22/35/43 region proportions, different font/spacing/card treatment and alternative poster-grid geometry are not approved. They remain a visual revision choice; current source-backed shared geometry is retained. Moving Delete from Page Details to Review would be a separate owner choice; it has not been moved. Both naming and existing pinned in-panel Review Save are preserved, so no confirmation is needed to retain them.

Physical Shield, Concept A4 image parity and installed signed upgrade: NOT RUN. New automated results will be recorded after actual completion; prior receipts must not be read as testing this checkpoint. No signing, Drive write, main merge, version identity change or replacement branch.

## Validation progress

Current source `f5e2924cc8f7302032933f8aec8793030b6e1b0f`: focused [41137713734](https://github.com/heretoecode/Supernova/actions/runs/41137713734), full conformance [41137713737](https://github.com/heretoecode/Supernova/actions/runs/41137713737), pending. Isolated library [41137713712](https://github.com/heretoecode/Supernova/actions/runs/41137713712) passes all six cases; downloaded SHA/CRC and JUnit results independently checked (continuation/LIBRARY_FIXTURE_VALIDATION.json). Local 134 tooling tests and parsing of 1,103 Java files pass. No current-source APK acceptance yet.

| Preceding checkpoint | Actual outcome / correction |
|---|---|
| `83aede9` | Focused 41133244888: 182 passed, zero failures/skips, independently read back. Library 41133245160: six passed. Full 41133244918: all 1,257 application + 300 library tests pass, four retained skips; release lint fails mixed tab/space indentation in the new HUD block (corrected). Lint has 1,496 warnings, one extra SetTextI18n warning versus 7011e87. Archive digest/CRC, JUnit and lint independently checked: continuation/DIAGNOSTIC_83AEDE9.json. No APK accepted. |
| `7584247` | Real card → global nav → same card event regression exposes that clearChildFocus does not observe this escape. Focused 41136565935: failure; assertions unchanged. The attached global observer in f5e2924 addresses the actual event path. |
| `11bf740` | Fixes HUD indentation only; its full run was superseded/cancelled after the focus event failure. Cancellation is not success. |

Earlier diagnostic history: `acccec6` focused passed 174; screenshot review then found tooltip text needed explicit layout before positioning. Strengthened painted-text/enlarged-font tests followed. Run 41128669132 passed 178/179 and exposed default button focus policy; explicit focusable targets fixed it, with 41130047853 subsequently passing. Runs 41131538478 and 41131885502 exposed an initial-library fixture race and missing image-loader initialization; fixtures were corrected without weakening assertions. Prior completed 7011e87 receipts and APK remain historical only. No lint suppression or baseline was added.

Preceding 83aede9 captures and exact provenance: [software fixtures](continuation/software-captures/README.md). They predate the focus-lifecycle correction and do not establish current-source APK or physical acceptance.

## Current measured implementation tokens

HUD XML dimensions below are logical Android dp converted once by resource density. Editor/shared panel dimensions retain the existing `TvLayout.scale` clamp (.85–1.25) once before density conversion through `SharedThreePanel.dp`/`PreviewDialog.dp`. Text uses Android sp/fontScale; no second manual font scaling is applied. They are engineering measurements of retained shared/source controls, not pixels copied from a generated image.

| Surface | Actual source tokens / resource IDs |
|---|---|
| HUD safe/control area | `player_controller_experimental`: sides 28dp, control strip 104dp (8 + 88 + 8), safe bottom max(24dp, 4% viewport height); no secondary action row |
| HUD groups | `preview_pause_group` 44×44, `pause` target 44×44 with centred 22dp artwork; seek frame flexible / 88dp, slider target 24dp / track 4dp; labels 14sp; Settings 116×40, existing gear and 20dp pill radius |
| HUD title/clock | Official title binding retained; fallback bounds 380×64, AppCompat autosizing 16–23sp; middle-left 28dp, episode metadata 13sp with 4dp gap; constrained layout moves the block only enough to clear the chapter bubble. Clock remains 32dp right / 24dp top, 14sp |
| HUD bubble | 13sp, min 22dp, 7dp side padding and existing 3/7 top/bottom; 4dp pointer, 5dp radius, 2dp bar gap; max two lines, actual text layout measured within seek bounds before pointer placement |
| Editor shell | Existing SharedThreePanel .24/.38/.38 weights, 16dp gaps, 20dp panel padding, 16dp radii; outer 24/14/24/16; source viewport keeps 900dp focus-revealed content below 780dp logical width |
| Editor content | 21sp headings, 18sp actions, at least existing 50dp target with wrap-content rows; independent rail/form/preview scrolling; only Review has the retained in-panel Save; Delete remains Page Details |
| Preview | Existing statistics model; real indexed items, 20-item cap; 50×75dp aspect-preserving poster thumbnail and 15sp label with existing neutral fallback; fixed heading, separately scrolling statistics/items, stationary outer focus frame |

No final visual approval of the optional new editor proportions/font/card/grid proposal is asserted. Actual current-source captures must come from a passing run and retain source/artifact hashes; earlier proposal diagrams remain labelled historical proposals.

The final editor-state audit also marks/focuses the actual current choice in existing Year/watched/rating/View/Sort/indexed-facet pickers. Labels/options and filter semantics are unchanged; unsupported saved values remain visible in the invoking field/context rather than being silently replaced or invented as new options. A retained-year regression covers selection, focus and parent restoration.

Settled preparation also resolves nested Home rail cards (not just the hero/library cells), observes global focus while attached, clears pending work when focus leaves media and rechecks the actual focused identity before offering priority. Existing queue/cache remains authoritative. A real nested Recently Added view regression covers identity and obsolete-focus cancellation.

Unmodified current-source HUD/editor/global Settings captures and exact provenance: [continuation/software-captures/](continuation/software-captures/README.md). They are software fixtures, not physical acceptance or approved reference images.
