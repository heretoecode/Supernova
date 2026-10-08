# Functional audit — three review passes

Baseline `2a9380abf97a89ec35d91f7c7a9b03327364634f`. Reviews are source/document reviews, not physical test passes. All implementation files remained at baseline.

## Pass 1 — inventory completeness

Checked manifest root/variants/dependency declarations, 731 Java app files (no app Kotlin), Settings main/More XML and dynamic constructor/loop sites, Preview settings organisation, source/sidebar/action population and entrypoint/native callbacks. Closure index: 118 manifest declarations, 1,109 callback symbols, 159 XML control declarations, 110 existing test files. Every major route assigned to workflow/priority inventory; lower-level runtime reachability retained conservatively. Index occurrence alone is not a manually verified effect or visible-control count.

Corrections: added all 13 Licences ACTION_VIEW rows and XML list-value appendix; added widget, subtitle-file wizard, legacy rescan period/time/source/foreground, credential editor, shortcut fallback/alternate, file picker, automatic-description progress, classic information, bulk-selection and system/platform routes. Checked 101 XML leaves +29 dynamic =130 main/More records; normal TV84 leaves, Playback5/Library17 with source-based conditional deltas. Indexed comments excluded from XML counts; Trakt collection sync commented row excluded. Existing partial audits not marked complete.

Disposition: completeness review satisfied at declared source/functional-domain scope; installed localized order/count/advanced states Q01/Q02 and low-level legacy runtime reachability Q09 remain explicitly conditional. No source index is used to certify dead code.

## Pass 2 — handlers, dependencies and side effects

Traced Hide Watched through LoaderUtils/settings finish/complete snapshot/PreviewPages post-filter; collection rescrape explicit intent through pinned MediaLib collection/movie table/job/Tags consumers; sort-name recreation transaction through current Preview stored sort-name readers. Checked DB/source writes vs file deletion, NFO sidecars/network bookmarks, unencrypted archive/restore staging/replace/journal, Trakt/OpenSubtitles/put.io state, SFTP identity overlays, scheduler period/foreground/per-source flags, and native player/JNI dependencies.

Corrections: Featured exposes only More Info; play callback is not rendered. Current normal NetworkShortcutDetailsActivity routes a valid Shortcut to PreviewSourceManagement; native fragment has conditional fallback/direct-fragment semantics. Removed nonexistent draft test names; app test presence grounded in test register. Resolved NFO bool item defaults and resource string escaping; distinguished native immediate remove, alternate close-on-failure and workspace confirmed-row-retention. Opening Settings migration/audio state mutation, subtitle inverse proxy listener and legacy sub-hour scan summary retained as source concerns.

Disposition: evidence review satisfied; no universal history-retention, runtime-correctness, entitlement, first-use security or native-output guarantee. Q03–Q09 name concrete limits. No live scan/rescrape/restore/export/delete/account action executed.

## Pass 3 — design/findings consistency and docs-only safety

Compared actual five-section Network rail with approved future seven; current put.io/native browser/scanner dependencies vs designed Downloads/Library Health; current custom genre-based Home rows vs separate parked smart collections. Preserved latest Home sole More Info and H6 acceptance, approved Details assets/missing-information fallback, four primary runtime HUD controls vs fifth hidden resource, parked identity/profiles/product work, and conditional IntroDB endpoints rather than claiming a separately named SkipDB integration. Historical source/build tests remain distinct from Shield acceptance.

Cross-checked 15 findings/10 questions and 22 cleanup candidates against priority inventories. No class/service/schema wholesale removal is certified safe. Every hide/move/rename is future-only; no approved decision reopened. Audit changes only docs/audits Markdown; old audit/handover/design/QA files were not edited by this audit; audit index updated. Concurrent remote documentation-only changes to PARKED_FUTURE/SEARCH_MATCHING (`7762cef6`, `becd754a`) were read, preserved and reconciled separately.

## Validation and commits

- Safe static checks and nine Python tests: PASS, detailed in [test register](FUNCTIONAL_AUDIT_TEST_REGISTER.md). No Android/Gradle/JUnit/native/physical test run here.
- Document source-path/line bounds, relative links, XML/dynamic key completeness and existing app test-name references: PASS: 3,060 baseline/pinned-library code links (path + line bounds), 43 relative documentation links, all 101 XML and 130 XML/dynamic modeled records; no missing app test-name references.
- `git diff --check` and changed-path restriction: PASS on staged payload: 11 changed files, all Markdown under docs/audits; no tracked-file difference outside docs/audits; no whitespace errors. Post-commit and remote verification recorded below.
- Baseline/source authority SHA: `2a9380abf97a89ec35d91f7c7a9b03327364634f`.
- Audit payload commit: [`533dc533bdf5d14c030cd03251a2e51228a7b5e4`](https://github.com/heretoecode/Supernova/commit/533dc533bdf5d14c030cd03251a2e51228a7b5e4); parent preserved `becd754a24c708d24044476ee78e31050cf6eb2c`. `git push origin HEAD:refs/heads/codex/post-4.1.7-shield-fixes` succeeded; remote refs query returned the exact payload SHA.

Additional pass-3 reconciliation: normal local Search has normalized title/episode/path matching, no semantic TMDb/IMDb-column or cast/crew/studio predicate; Find a Match ID lookup is distinct. Future Search guidance/credit search and combined custom-page/Discovery concepts remain discussion only.

Final publication only on codex/post-4.1.7-shield-fixes; no main merge/release/APK. Commit proof is added after payload commit; final certification commit appears immediately after it in branch history and is linked in the final user report. The temporary pre-rebase local payload SHA was not published; only the rebased payload reference above is authoritative.

## Post-commit proof

- Payload diff against parent: **11 files, all docs/audits Markdown**, 3,112 insertions / 1 changed index line; source/application state unchanged.
- Baseline-to-payload diff outside docs: **empty**. Preserved concurrent remote design notes account for two other documentation changes relative to initial baseline; those are not audit-authored edits.
- Post-rebase/payload validation: **PASS**, same 3,060 code links, 43 relative document links, 101 XML keys and 130 XML/dynamic modeled rows; no missing source path/line/test-name claims.
- `git diff --check` and clean payload working tree: **PASS**. No source/tests/resources/build/workflow/dependency/signing/config/version changes; no APK/release/main merge.
- Final certification record updates only status/review Markdown. Source audit complete; Q01–Q10 remain unresolved runtime/installed/design-disposition conclusions, not untested claims reported as passes.
