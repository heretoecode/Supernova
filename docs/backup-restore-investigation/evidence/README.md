# Evidence and reproduction

All material is static source/schema/key evidence. No user backup/diagnostic archive, private database, watch records, image contents, actual network locations, credentials or device dumps were used. Developer-owned schema names and source code strings are not runtime data.

| Artifact | Coverage and interpretation |
|---|---|
| [media-db-fields.tsv](media-db-fields.tsv) | 39 known media tables / 306 fields, storage defaults/types and inclusion/replacement rules, pinned code links |
| [media-db-table-definitions.json](media-db-table-definitions.json) | Complete CREATE constraints with final artwork table names and additive field migrations; source definitions, not Android migration execution |
| [sql-source-definitions.json](sql-source-definitions.json) | 199 table/view/index/trigger definitions and inline table migrations, including historical alternatives. Not every historical object is active; no unresolved extracted SQL definition |
| [xml-settings-matrix.tsv](xml-settings-matrix.tsv) | 111 preference-key instances with resource-resolved keys/defaults/labels, action versus stored option and portable-rule mapping |
| [setting-key-matrix.tsv](setting-key-matrix.tsv) | 364 resolved key/prefix candidates plus read/XML default expressions and complete source references; broader than user-facing settings |
| [preference-call-sites.json](preference-call-sites.json) | 1,055 resolved production preference-shaped calls and 396 dynamic/unresolved expressions, default allowlist/prefixes and XML records. Includes Bundle/model calls: store context must be read with Report 02 |
| [named-store-call-sites.json](named-store-call-sites.json) | 45 named SharedPreferences call sites. SupernovaLibraryPolicy.HEALTH/provider ownership from build overlays are resolved in Report 01, not guessed by pin-only extraction |
| [reproduce-inventories.txt](reproduce-inventories.txt) | Python source-analysis recipe stored as a documentation text artifact; reads checkouts and writes evidence output only |
| [completion-verification.json](completion-verification.json) | Static path/line/relative-link, JSON/count, docs-only scope and unchanged source-checkout checks; no device/runtime-test claim |

## Method and limits

Reuse the existing maintenance XML/consumer/source/overlay/diagnostic maps at commit eac3e504490a92dd9f43252649264657e485982c. Inspect signed Video b3504c2c4b1725f3070416d14b63e53ae1db3d74, MediaLib 5758074049bc153ac4967b4ecd21886469270076, FileCore c4b760c55102c72d45f68b0d16999fc1f4f013a7 in clean separate checkouts. Build overlays are source evidence, not executed or applied to application checkouts in this investigation. They do not change the backup-related schema definitions tabulated here; source retention and provider ownership additionally cite the actual patches.

The lexical extractor is not a Java compiler. It resolves literal/constant concatenations, records defaults as source expressions and retains unresolved dynamic calls for manual review. A key present in a Bundle or named store is not included simply because the default-preference allowlist admits its spelling. Getter default expression is not always the initialized runtime default. Reviewed active dynamic categories, named stores, resource values, player/global settings and missing sidecars are documented in Reports 01–03. Runtime-created keys/files/historic DB objects cannot be exhaustively counted without private inspection; writer-wide copy/allowlist rules are specified instead.

The media field catalog evaluates source CREATE and additive-column SQL in an isolated Python in-memory SQLite connection to inspect declarations. Final v56 artwork table definitions replace earlier variants. This is **not** execution of Android onCreate/onUpgrade, migration data transformation, triggers, JNI functions, runtime restore or a user DB. Original SQL definitions retain constraints; snapshot exports carry whole live SQLite objects, including engine or extra historic objects if present. The catalog should not be used as migration certification.

## Reproduce without building or changing app source

Check out the exact three refs above in independent clean directories. From this evidence directory run:

```sh
python reproduce-inventories.txt /path/to/Video /path/to/MediaLib /path/to/FileCore /path/to/separate-evidence-output
```

This writes inventories only to the specified evidence output. It does not fetch/build/compile APKs, open a private DB, apply source patches or modify application files. Compare output counts and pinned references; inspect manual semantic maps in the reports for dynamic keys/overlays. Documentation validation checked each referenced code file/line locally at its SHA, every relative report link, JSON parsing and counts, then confirmed all added files are under docs/backup-restore-investigation and all inspected source checkouts are clean.

## Evidence still unavailable

The owner's exact APK/provider/date/timezone, personal archive's actual entries/byte totals/schema, diagnostic incident/exit records, every-setting roundtrip, Android/provider interruption and power-loss tests, performance and signed 0.138-to-0.139 upgrade proof. These are clearly proposed future gates, not fabricated passes. Private future review may publish only privacy-reviewed aggregate outcomes.
