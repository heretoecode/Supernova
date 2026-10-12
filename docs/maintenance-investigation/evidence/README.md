# Evidence index

All source inventories refer to signed QA source `b3504c2c...`; upstream SHAs are frozen in component-comparisons.json. These are static records, not runtime observations. No private logs, user recordings, credentials or APKs are included.

- [Repository state at start](repository-state-start.json)
- [Registered fork relationship](github-fork-relationship.json)
- [Frozen component comparisons](component-comparisons.json)
- [Complete upstream commit metadata, files and hunk symbols](upstream-commit-inventory.tsv)
- [Three-way changed-file ownership](three-way-file-ownership.tsv)
- [Effective workflow overlays and hashes](effective-component-overlays.tsv)
- [Video tracked-tree inventory and byte sizes](video-tree-inventory.tsv)
- [Package counts](package-counts.json)
- [Manifest components and exported boundaries](manifest-components.tsv)
- [Static XML preferences](xml-preference-inventory.tsv)
- [Literal diagnostic producers](diagnostic-producers.tsv)
- [Upstream releases](upstream-releases.json)
- [Inventory totals and method](inventory-summary.json)
- Net diff statistics: [Video](video-net-diff-stat.txt), [MediaLib](medialib-net-diff-stat.txt), [FileCore](filecorelibrary-net-diff-stat.txt), [AVOS](avos-net-diff-stat.txt)
- Reconstructed effective symbols: [MediaLib](medialib-effective-overlay-symbols.txt), [FileCore](filecorelibrary-effective-overlay-symbols.txt), [AVOS](avos-effective-overlay-symbols.txt)

Ownership indicates Git differences, not reachability or removal safety. The upstream commit inventory classifies metadata separately from unresolved portability. The workflow has 12 dependency patches; 56 file applications are validated by matching original hunk context in memory. Added files are read as empty source before reconstruction. Source line numbers in effective symbol files refer to reconstructed files; use both the pinned component and ordered overlay list to reproduce them.
