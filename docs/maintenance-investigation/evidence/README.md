# Evidence index and reproduction

Frozen Supernova source `b3504c2c4b1725f3070416d14b63e53ae1db3d74`; upstream primary heads/bases in component-comparisons.json, auxiliary refs in native-manifest-and-history.json. Files below are static source/Git/API inventories or explicitly marked synthetic/proposed evidence. No private logs/recordings/credentials/APKs included.

Ownership/duplicate bytes is not reachability/removal proof. Commit metadata/hunk symbols are not portability certification. All12 ordered workflow patches, including two plain unified-diff files, were reconstructed in memory with matching old contexts (58 file applications); no application checkout was edited. Effective symbol line numbers refer to reconstructed files plus pin/overlay list. Production declaration/literal/reference regexes can miss multiline/indirect/dynamic relationships.

## Every evidence deliverable

- [avos-effective-overlay-symbols.txt](avos-effective-overlay-symbols.txt)
- [avos-net-diff-stat.txt](avos-net-diff-stat.txt)
- [component-comparisons.json](component-comparisons.json)
- [dependency-advisory-details.json](dependency-advisory-details.json)
- [dependency-advisory-query.json](dependency-advisory-query.json)
- [diagnostic-producers.tsv](diagnostic-producers.tsv)
- [diagnostic-sanitizer-probe.json](diagnostic-sanitizer-probe.json)
- [effective-component-overlays.tsv](effective-component-overlays.tsv)
- [effective-versus-upstream.tsv](effective-versus-upstream.tsv)
- [ffmpeg-tls-default-source.json](ffmpeg-tls-default-source.json)
- [filecorelibrary-effective-overlay-symbols.txt](filecorelibrary-effective-overlay-symbols.txt)
- [filecorelibrary-net-diff-stat.txt](filecorelibrary-net-diff-stat.txt)
- [github-fork-relationship.json](github-fork-relationship.json)
- [inventory-summary.json](inventory-summary.json)
- [manifest-components.tsv](manifest-components.tsv)
- [manifest-screen-atlas.tsv](manifest-screen-atlas.tsv)
- [medialib-effective-overlay-symbols.txt](medialib-effective-overlay-symbols.txt)
- [medialib-net-diff-stat.txt](medialib-net-diff-stat.txt)
- [module-source-counts.json](module-source-counts.json)
- [native-manifest-and-history.json](native-manifest-and-history.json)
- [navigation-static-branches.tsv](navigation-static-branches.tsv)
- [package-counts.json](package-counts.json)
- [patch-identity-checks.tsv](patch-identity-checks.tsv)
- [production-symbol-inventory.tsv](production-symbol-inventory.tsv)
- [proposed-media-corpus.json](proposed-media-corpus.json)
- [repository-state-start.json](repository-state-start.json)
- [resource-byte-duplicates.json](resource-byte-duplicates.json)
- [shared-component-consumers.tsv](shared-component-consumers.tsv)
- [source-references.json](source-references.json)
- [source-resource-footprint.json](source-resource-footprint.json)
- [three-way-file-ownership.tsv](three-way-file-ownership.tsv)
- [ui-static-literals.tsv](ui-static-literals.tsv)
- [upstream-commit-inventory.tsv](upstream-commit-inventory.tsv)
- [upstream-releases.json](upstream-releases.json)
- [upstream-selected-ledger.json](upstream-selected-ledger.json)
- [video-net-diff-stat.txt](video-net-diff-stat.txt)
- [video-tree-inventory.tsv](video-tree-inventory.tsv)
- [xml-preference-inventory.tsv](xml-preference-inventory.tsv)

- [completion-verification.json](completion-verification.json)
- [preference-consumer-trace.tsv](preference-consumer-trace.tsv)
- [earlier-audit-lead-reconciliation.json](earlier-audit-lead-reconciliation.json)

## Reproduce primary source comparisons

Use independent full clones; no protected checkout mutation is required. Example read-only commands after fetching the frozen objects:

```sh
git merge-base b3504c2c4b1725f3070416d14b63e53ae1db3d74 e394eda85058d076c1b85adf7b64a788068d2c8a
git log --reverse --format='%H %s' f53a1fc055d1d550a2edad4992bf82f9d12a7872..e394eda85058d076c1b85adf7b64a788068d2c8a
git diff --name-status f53a1fc055d1d550a2edad4992bf82f9d12a7872 b3504c2c4b1725f3070416d14b63e53ae1db3d74
git diff --name-status f53a1fc055d1d550a2edad4992bf82f9d12a7872 e394eda85058d076c1b85adf7b64a788068d2c8a
git ls-tree -rl b3504c2c4b1725f3070416d14b63e53ae1db3d74
```

Repeat using each component's common base/pin/head. Read every ordered patch from .github/workflows/supernova-0138-qa.yml and .github/build; effective-content comparison must preserve Supernova overlays. The Foundation preparation script changes build/module configuration and rebuild lock selects FFmpeg8.0.1 independently of generic upstream builder branches. Direct snapshot comparison is documented for prebuilt-opus's unrelated histories. GitHub compare has250-commit/300-file caps; prebuilt-ffmpeg hits file cap and is not exhaustive.

The Maven advisory query is POST https://api.osv.dev/v1/querybatch with public SBOM Maven names/versions only; full details fetched from /v1/vulns/{id}. It is not native/fork security clearance. Synthetic sanitizer probe translates the source's three regex replacements to Python, then truncates4096; exact method SHA and canary outputs are recorded. Java/Android execution is explicitly unverified because javac is absent. No actual authentication data was used.

## Handover verification

completion-verification.json records documentation path scope, source links/relative links, meaningful report presence, clean detached application checkout and protected refs. GitHub PR/delivery URLs are added after publication. These checks do not turn outstanding runtime/portability work into complete results.
