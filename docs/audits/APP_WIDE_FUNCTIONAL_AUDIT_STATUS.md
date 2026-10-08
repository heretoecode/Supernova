# App-wide functional audit status

Date: 8 October 2026. Repository `heretoecode/Supernova`; branch `codex/post-4.1.7-shield-fixes`. **COMPLETE — source functional audit and three review passes; documented runtime/installed-state questions remain unresolved.** Do not equate this source audit with physical QA acceptance or runtime correctness.

## Baseline and authority

- Baseline branch HEAD: **`2a9380abf97a89ec35d91f7c7a9b03327364634f`** (handover-link documentation commit). All app source permalinks in this audit pin this SHA; later audit commits are documentation commits, not APK source.
- [Authoritative handover](APP_WIDE_FUNCTIONAL_AUDIT_HANDOVER.md) was read completely. [PROJECT_STATUS.md](../../PROJECT_STATUS.md), docs/README, existing audits, design/QA/reference records and actual entrypoints were inspected. Historical partial audits remain preserved, not relabelled app-wide complete.
- Accepted historical 4.1.7 APK source `7efb4195639205a6526281491af40c0dab1776b7`; previous tested fixes `61a1ae5a21be90d185d448362c9729eb6c33cc42`; consolidated corrective candidate `b021c51dcbbe2d5679fb9015c43830713f02aa0b`; current Home-hotfix replacement APK source **`f97f62942f6b4f1a90e2e15258db452384d6b39c`**. Exact build/physical status stays in docs/qa. Baseline branch HEAD is not invented as installed Shield APK identity.
- Pinned MediaLib `5758074049bc153ac4967b4ecd21886469270076`, FileCoreLibrary `c4b760c55102c72d45f68b0d16999fc1f4f013a7`, AVOS `2cf21c486c7bf244c49b78d2220197a6d75396dd`; repository workflow overlays traced, no dependency/app/build source altered.

Before publication, remote target advanced to `becd754a24c708d24044476ee78e31050cf6eb2c` through `7762cef6` and `becd754a`: concurrent **documentation-only** Search/parked-discussion updates. Both were read and reconciled; application source unchanged. Audit commits will be appended above them. These are inherited user discussion changes, not audit-authored design edits.

## Method and evidence classes

Trace from entrypoint to Preview gate, declaration/visibility, listener/action generation, native/shared service, persistence and consumer. Enumerate XML outside comments, dynamically inserted rows/loops, manifest variants/aliases/intents, Java callback registrations and XML control declarations. Cross-check lexical references with actual handler source; references alone do not prove runtime execution or dead code. Group dynamic menus by shared domain only when each child and conditional state is explicitly named. Index lower-level helper/platform reachability conservatively; do not claim every source line was manually audited or native implementation formally verified.

**Code-established** means handler/persisted consumer traced; **test-supported** names only actual existing assertions and executed checks where indicated; **conditional/unresolved** identifies code/API/account/legacy/merged-binary boundaries; **device-confirmation** names physical/installed output questions. Designs/mockups express approved direction, never implementation proof. Prior QA evidence keeps its exact source SHA and acceptance limits.

Normal Settings TV model: FEATURE_LEANBACK + TV mode + try_new_ui=true + advanced=false + no cutout + sponsor/adult disabled. Counts include visible disabled/nonselectable leaves, exclude structural headers/hidden leaves. Dynamic-source/account names and installed current values are not invented. Source order and default-English model separate from installed localized AndroidX order.

## Coverage matrix

|Subsystem|Coverage checkpoint|Inventory|Evidence / remaining bounded uncertainty|
|---|---|---|---|
|Startup/UI gating/navigation|EntryActivity/UiChoiceDialog/MainActivityLeanback/MainFragment/TopNavigation; try_new_ui/classic/TV/phone; Back/focus/return/loading|App workflows + manifest/control index|Code-established; installed feature/permission/locale/merged binary conditional|
|Settings|98 main XML + 3 More TV + 29 dynamic + 13 licence links; all paths/gates/action/consumer/default/list/summary states; 12-category rail|Settings inventory|Normal model 84 visible leaves; advanced/account/device/locale order/Shield count isolated Q01–Q03|
|Network & Files|Five actual sidebar sections; local/index/saved/manual six-protocol/discovery/account/browser/file/source/scan/errors/focus; inherited source manager/credential/rescan routes|Network inventory + App supplement|48 grouped action/state records; real sources/accounts/storage not activated|
|Home|Featured readiness/rotation/promotion and More Info only; default/custom rules/membership/CW/Watch Next/Move/Hide/loading/fallback|App workflows; PreviewHomeRows/Pages/FeaturedCard|Code-established; latest Home physical QA pending; H6 historical acceptance preserved|
|Movies/TV|Grid/list/genre/year/provider/sort/order/columns/unmatched/snapshot/technical enrichment/persisted per-view state|App workflows|Code-established; large library/performance/provider freshness device questions|
|Search/matching|Local search/keyboard/system providers; title/TMDb/IMDb matching/review/show-episode correction/errors/accepted identity|App workflows + public intents|Source chain to scraper/provider and Home identity reconciliation; live catalogue correctness conditional|
|Details|Local/remote/show/episode/versions/native More actions/watch/index/delete/subtitle/artwork/cast/crew/facts/extras/recommendations/provider offers|App workflows + action/control index|Capability/context-dependent action generation; no deletion or provider actions executed|
|Playback|Native PlayerActivity/Service/LibAvos/HUD/seek/track/timing/appearance/speed/play-mode/format/up-next/history/exit/info/floating/external/privacy|App workflows + Settings rows + native composition index|Source/test-presence and prior QA separated; native/runtime/output precision untested here|
|Library/provider/index|Local MediaStore/network scanner/import/provider/DB/schema/notifications/watch/collections/metadata/art/thumbnail/retriever/remote-state|App workflows + pinned MediaLib + overlays|Library mutations/sidecar/record-vs-file distinctions mapped; legacy service retain|
|Accounts/providers|TMDb/JustWatch/Trakt/OpenSubtitles/IntroDB/put.io login/auth/cache/failure/disconnect/reassign/review/entitlement|Settings + Network + App|Actual IntroDB endpoints; no independently established named SkipDB integration; no production credential invention|
|Import/export/backup|NFO/DB/full verified backup/staging/hash/RestoreJournal/migrations/private archive/settings/grant compatibility|Settings + App|Real source/state/security effects established; no restore/export triggered|
|Downloads/Library Health/collections|Legacy torrent/download/file-copy + unmatched/error/scan supporting features + smart Home rule rows vs future workspaces/full product|App + Network future comparison|Designed/implemented/inherited/parked explicitly distinct|
|Diagnostics/About|Local logging/detail/digest/problem category/reference/archive/build/notes/licences and build-gated Sentry distinction|Settings + App + Test register|Opt-in local logging separate Sentry; Report saves local reference, not sends message|
|Platform/permissions/onboarding|Root+variant manifests/permissions/aliases/providers; external player/search/picker/widget/plugin/boot/service; UI choice and subtitle wizard|Source/control index + App supplement|118 declarations incl aliases/variant duplicates; final merged flags/native platform reachability conditional|
|Legacy/non-user-facing foundations|Native adapters/fragments/classic phone/categories/collection/list/wizard/credential/editor/file operations/jobs|App supplement + Source register + Cleanup|Manifest/intent/key/schema/reflection/JNI boundaries retained; no obsolete-by-name deletion|
|Cleanup candidates|22 UI hide/keep/move/further-trace/local-code candidates; scope/confidence/risk/removal prerequisites|Cleanup register|No whole class/service/schema deletion proven safe; no code changes|
|Approved design/QA authority|Current HOME/DETAILS/PLAYBACK/MOVIES_TV/SEARCH/SETTINGS/NETWORK and PARKED_FUTURE; references; exact historical candidate SHAs|App comparison + findings + review pass 3|No approved decisions reopened; candidate source/Binary identities separated|
|Review/test/publication|Three passes; code-link/key/test-name closure/static checks/docs-only diff; existing branch push|Review record + Test register|Complete; payload committed/pushed and remote SHA verified; final record in following documentation commit|

## Deliverables and reconciled counts

1. [Settings inventory](SETTINGS_FUNCTIONAL_INVENTORY.md): 101 XML leaves (98 main +3 More TV), 29 dynamic leaves = **130 rows**, plus **13 licence links**. Normal model **84 visible leaves**, Playback **5**, Library & Metadata **17**; advanced Library **18**, adult build additionally **19**. Approximate six/eighteen Shield observation explicitly isolated, not fabricated.
2. [Network & Files](NETWORK_FILES_FUNCTIONAL_INVENTORY.md): **5** actual sections, **48** grouped action/state rows plus inherited-route supplement. Overview2; Local V; Network S+2; Cloud1 actionable+3 inert; Saved B; empty labels excluded. Frequency5/protocol6/connected put.io menu8.
3. [App inventory](APP_WIDE_FUNCTIONAL_INVENTORY.md): **65** workflow rows + **10** inherited/system supplement rows = **75**; all major navigation, playback, provider, data/background, conditional/classic/system domains included.
4. [Cleanup register](LEGACY_NOVA_CLEANUP_CANDIDATES.md): **22** evidence/risk/confidence/prerequisite records; UI hiding separated from code removal.
5. [Findings](FUNCTIONAL_AUDIT_FINDINGS.md): **15** prioritised findings and **10** unresolved questions, recommendations not implementation instructions.
6. [Source register](FUNCTIONAL_AUDIT_SOURCE_REGISTER.md): **731** app Java files (no app Kotlin found), **118** component declarations (including root/variant duplicates/aliases, not unique final installed components), permissions/build/native overlay evidence.
7. [Control register](FUNCTIONAL_AUDIT_CONTROL_REGISTER.md): **1,109** callback/registration symbol occurrences and **159** XML focusable/action declarations. These are discovery counts, not visible controls; per-domain side effects cross-reference functional workflows.
8. [Test register](FUNCTIONAL_AUDIT_TEST_REGISTER.md): **110** existing app/tools test files; test presence and execution separated.
9. [Three-pass review record](FUNCTIONAL_AUDIT_REVIEW_RECORD.md): completeness, dependency/side-effect evidence, design/docs-only consistency; validation and publication commit references.

## Checkpoints, known gaps and completion boundary

Settings and Network priority reconstruction completed before remaining app workflow/cleanup closure. Review pass 1 found licences and platform/legacy routes requiring explicit supplements; pass 2 corrected Featured and source-manager route assumptions/test names; pass 3 reconciled approved future layouts and proved documentation-only scope. No destructive jobs, account flows or source connections activated.

No unassigned major subsystem remains in the coverage matrix. Remaining **Q01–Q10** are concrete installed/device/account/performance/low-level-platform/disposition boundaries in findings; they are unresolved audit conclusions, not implied runtime passes or future authorization. No Shield access expected or needed to finish source discovery. No APK/Gradle/native full verification/real-server/entitlement/penetration review performed. Exact localized tie ordering/advanced live relayout and final merged binary differ from static model; no claim those questions are resolved.

Source/static checks: 431 XML parsed/static gates passed; tools unittest suite **9 passed**. No application tests, build scripts, dependencies, resources, workflows, app configuration/signing/identity/version or functionality changed. No release, implementation PR or main merge. All changes confined to Markdown in docs/audits.

Published audit payload: [`533dc533bdf5d14c030cd03251a2e51228a7b5e4`](https://github.com/heretoecode/Supernova/commit/533dc533bdf5d14c030cd03251a2e51228a7b5e4) above preserved remote authority `becd754a24c708d24044476ee78e31050cf6eb2c`. Push to the requested branch succeeded and `git ls-remote` returned that exact payload SHA. The following documentation-only commit records final certification; its reference is available in this file’s history and final user report.

Final source/coverage gates are satisfied at the declared functional-domain scope: priorities, all major routes/controls/services, conservative conditional/inherited reachability, cleanup register, findings, all documentation and three review passes. 3,060 source links/path-line bounds, 43 relative document links, 101 XML/130 modeled records and existing-test-name checks passed. `git diff --check` passed; all 11 audit-authored changed files are docs/audits Markdown. `git diff baseline HEAD -- . :!docs` is empty, proving no tracked non-documentation file changed even after preserving concurrent remote design notes. Working tree clean after payload commit/push.

No APK/release/workflow dispatch/implementation PR/main merge or live destructive action. Build/validation workflow push filters ignore docs/Markdown. No application code, resource, test, build script, dependency, configuration, signing, identity, version or functionality changed. Completion describes functional source discovery and evidence recording, not runtime acceptance or authorization for UI/code cleanup. Q01–Q10 remain bounded conclusions for later planning/QA; none is silently marked resolved.
