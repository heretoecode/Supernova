# Preview 4.1.6 — source preservation and CI

## Verified preservation

- Repository: `heretoecode/Supernova`.
- Dedicated branch: `codex/preview-4.1.6`; canonical remote `main` remains at `77b2617ad1e48b7a7a85ba28463de0961af5ba4d`.
- Original local implementation commit: `01ba81755a94d2fa0fabeeae2c5b9a9121d11bf6`.
- Exact-source remote preservation commit: `b71d3bc8274ff135c4fbeda1df57b7a08f9cbcea`.
- Both commits have root tree `16c5a3628363a8eb811f260232cd7c020250e673`. This verifies the complete tracked tree, including paths, file modes and blob contents, not merely selected files.
- All 40 changed files against Preview 4.1.5 were additionally checked by path and blob hash through the remote compare API (960 insertions, 281 deletions).
- The original implementation report is preserved at `docs/preview-next/IMPLEMENTATION.md`; its pre-preservation status statements are historical, superseded by this report.
- The original local commit and branch have not been discarded or reset. Unrelated working-tree deletions were not staged or uploaded.
- `Supernova_01ba8175.bundle` preserves the original commit and passes `git bundle verify`. This incremental bundle requires baseline commit `77b2617ad1e48b7a7a85ba28463de0961af5ba4d`, which already exists in the canonical repository. A durable downloadable copy was saved separately.

## Validation progression

1. Remote commit `e839d61c54f6de52a6627916534d1dccb468ad72` enables this dedicated branch in the existing CI push filter. Its tree matches local commit `5785f824` (`4a2163dc25ca567136f815c41360d4b99a835eb5`).
2. [CI run 36074086464](https://github.com/heretoecode/Supernova/actions/runs/36074086464) reached Android Java compilation, independently of signing. It failed on one source error: `PreviewPages.Holder.presenter` was typed as generic `Presenter`, which does not declare `bindEntry`.
3. Local correction `f6dda40361e2c15428ee5e48fe276b2ea0e5b6bd`, preserved remotely as `6238061b0ba2a5c33b5c76804fc41f13bf0d2d40`, narrows that field to its actual `PreviewCardPresenter` type. Both trees are `a1f55dd69d5f843f16ad784d234b1bb9c6b63f6d`.
4. The same correction moves existing non-signing stability, metadata and WebDAV checks before the signing gate, and includes the source-validation log in always-uploaded diagnostics. Signing requirements are unchanged.
5. [CI run 36074524237](https://github.com/heretoecode/Supernova/actions/runs/36074524237), testing remote commit `6238061b0ba2a5c33b5c76804fc41f13bf0d2d40`, completed source compilation and every selected test successfully before failing solely at the original-key delivery gate.

| CI stage | Verified outcome |
| --- | --- |
| Android Java/resource compilation and first targeted unit-test group | PASS; `BUILD SUCCESSFUL`, 19 tests passed |
| Targeted stability and library metadata checks | PASS; 74 tests, zero failures or skipped tests |
| FileCore WebDAV security/range checks | PASS; 17 tests, zero failures or skipped tests |
| Optional full-mode personal-build test group | Not run: this push used Preview validation mode |
| Restore original signing-key cache | Cache miss for `nova-mark-debug-signing-v1` |
| Require original signing identity | Blocked: `NOVA_SIGNING_KEY_BASE64` was empty and the existing keystore was unavailable |
| Signed APK, emulator process startup, release optimisation and upgrade checks | Not run after the signing gate stopped delivery |

The three executed groups contain 110 passing test executions, covering 94 unique class/method names because some regression tests are deliberately repeated. These include Robolectric startup/recreation, page rendering/navigation, Movie/Episode Details, keyboard, HUD structure, sorting, diagnostics and existing stability regressions. They are not physical-device tests.

The [diagnostics artifact](https://github.com/heretoecode/Supernova/actions/runs/36074524237/artifacts/10840345106) contains source-build logs, test reports and fixture screenshots. Its ZIP SHA-256 is `5e54d45d41556d9343f8bc7a68183a238167521481c20de7a45fecbcb5a81429`. Report totals and the successful Gradle stages were inspected directly. Selected Robolectric fixture screenshots were inspected; they do not establish real artwork, animation, remote-control or Shield behaviour.

## Locally verified checks

- 30,010 dependency-free Java assertions pass for menu geometry and the diagnostic flight recorder.
- All 710 Java source files parse without syntax errors. This is not Android symbol/type compilation.
- 430 XML files parse; HUD structure, non-exported remote Details activity and the original signing gate pass the offline structural checks.
- `git diff --check` passes.

## Delivery and runtime boundaries

No replacement signing identity was generated or substituted. Signed APK delivery requires the original certificate SHA-256 `89ac087ed6f989c90482d4a999f80511fe6ceee26ef1b9c37a142a9f00d39a5a`.

No APK, emulator smoke-test pass or physical Nvidia Shield runtime validation is claimed. Compilation and selected unit tests succeeded; the workflow's overall failure represents the separate signing blocker, not compilation failure. Multiple versions / Resume, Up Next, Backup / Restore and extended physical soak testing remain parked as requested. Existing implementation limitations are documented in `IMPLEMENTATION.md`.
