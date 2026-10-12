# Supernova UI Reference Atlas — master index

**Read-only investigation/documentation delivery, 2026-10-12. Full atlas acceptance remains INCOMPLETE.** Ten area inventories and both priority visual packages are delivered; every conditional runtime option/reachability and physical Shield verification remain explicit gaps. Do not describe the source catalogue as a fully executed UI graph.

[Draft GitHub PR #16](https://github.com/heretoecode/Supernova/pull/16) · [Downloadable full package](https://drive.google.com/file/d/1UIlz-XYZvRHs82GuvI5VhkwWSzuXEC2o/view?usp=drivesdk)

[Drive atlas folder](https://drive.google.com/drive/folders/1kzYXFIvuxs5eGrvGD-c-2cgHBKpPkLGc) · [Standalone searchable master report](MASTER_INDEX.html) · [Drive master report](https://drive.google.com/file/d/1iQvaP8w2C8Oe31Bb_tAUsR8dC8uOoRFS/view?usp=drivesdk) · [All published Drive links](https://drive.google.com/file/d/1qpIgpicpX3OwGkTPR9KAM6qq6oqTZ7Ob/view?usp=drivesdk) · [Acceptance and gaps](ACCEPTANCE_AND_GAPS.md) · [Reusable on-demand protocol](ON_DEMAND_REFERENCES.md).

## Baseline and evidence

Signed0.138 application source: `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. QA receipt head `16a60cb11a5dd20ebe374873db360e32b617d7b0` differs only in documentation. Newly reused native captures come from unsigned CI `074d7c4a90bafdf6ce487ecf0b2f8b4f773c092f`; its application/resources/tests match signed source. The historical0.137 audit/render pins remain separate. [Source/provenance receipt](evidence/source-provenance.json).

Owner reports installed0.138 on Shield; install/walkthrough/runtime fidelity is not inferred from Git source or CI. No Shield screenshot supplied. Classification **A** physical Shield (none), **B** genuine Android/native test (six historical +ten recovered current), **C** labelled code-derived approximation (35), **D** concept artwork (none used). Source facts, synthetic fixtures, render results and device unknowns are separate.

## Reviewed area inventories

| Area | Stable ID families | Inventory |
|---|---|---|
| Launch, navigation, Home and Customise Home | NAV/HOME/CH | [01](areas/01-home-navigation.md) |
| Movies, TV, custom Library, five-step editor, filters/sort/columns/stats | LIB/WIZ | [02](areas/02-libraries.md) |
| Details, seasons, actions, extras, people, streaming | DET | [03](areas/03-details.md) |
| Search, app keyboard, matching | SRCH/KEY/MATCH | [04](areas/04-search-keyboard.md) |
| Network & Files, source forms, onboarding, Health, cloud | NET/HEALTH/CLOUD | [05](areas/05-network-health.md) |
| Settings, integrations, Appearance, About | SET/ABOUT | [06](areas/06-settings-about.md) |
| Diagnostics, references, backup/restore/export | DIA/BAK | [07](areas/07-diagnostics-backup.md) |
| Preparing Playback, HUD, audio/subtitles/More, external/PiP | PLAY | [08](areas/08-playback-subtitles.md) |
| Shared component ownership and focus contracts | SHARED | [09](areas/09-shared-focus.md) |
| Classic/legacy/external-intent/utility/conditional surfaces | OTHER/SCREEN | [10](areas/10-inherited-conditional.md) |

Each area links its exact-source-expression appendix, source owners, ordered controls/defaults/actions/persistence, focus/Back flows and unavailable/conditional states. Shared source sites crossing areas remain searchable in the full catalogue.

## Priority visual packages

- [Customise Home package + state coverage](CUSTOMISE_HOME_VISUAL_PACKAGE.md):29 C illustrations +six newly recovered native captures, links to unchanged historical audit/six PNGs; [Drive report](https://drive.google.com/file/d/1bG6DuY97ZNiV5VEIfzQT1Phi_Y_GrgpE/view?usp=drivesdk), [original destination](https://drive.google.com/drive/folders/1AJJ4_S5FS0u87Hk8mh6SathtWiGHMLKN).
- [Library Statistics package](LIBRARY_STATISTICS_VISUAL_PACKAGE.md):four native current captures +six C variants, real shared owners, exact typography/geometry/count/local/network/partial-size/mixed semantics; [Drive report](https://drive.google.com/file/d/1sWsZJhJm9DcTgXNKNwfzE4dwW5ua-5Ib/view?usp=drivesdk), [Drive destination](https://drive.google.com/drive/folders/1eejvJNZeFgaOXcwceZhbZtR3-BveNFhB).
- [Every image's provenance/file link](evidence/image-manifest.json) · [Home coverage TSV](evidence/customise-coverage.tsv) · [Statistics inputs/calculation](evidence/statistics-fixtures.json).

No internet imagery, AI artwork or speculative redesign is used as current-interface evidence. Synthetic native artwork is labelled as the original test fixture. C font/geometry/Android error/native alert deviations remain recorded, even after visual/layout checks.

## Searchable source and option evidence

All769 production Java files scanned;316 display-related owners;1,782 display source sites;437 focus branches;78 manifest activities/aliases;130 preference declarations;178 layout text sites;973 primary English resource records. Pinned sibling supplements add364 resources and410 display sites. These are exact syntactic records, not claims of every runtime widget or reachability.

- [Manifest/screen entries](evidence/manifest-screens.tsv)
- [Exact display/option expressions, resources and callback context](evidence/display-sites.tsv)
- [Focus/key/Back/restoration source branches](evidence/focus-branches.tsv)
- [Declared preference choices/defaults/dependencies](evidence/preferences-declared.tsv) and [effective category/conditional consumer traces](evidence/preferences-effective.tsv)
- [Layout texts/accessibility/visibility](evidence/layout-text.tsv), [English primary resources](evidence/english-resources.tsv), [sibling resources](evidence/sibling-resources.tsv), [sibling display sites](evidence/sibling-display-sites.tsv)
- [UI owner hashes/paths](evidence/ui-owners.tsv), [counts and extraction limits](evidence/inventory-counts.json)
- [Drive upload/readback receipts](evidence/drive-upload-receipts.json)

[Inventories and Source Evidence Drive folder](https://drive.google.com/drive/folders/1DE7F-TbiUquzXvR2RlDVord2-BlOByf8). The standalone HTML embeds reviewed reports,45 current images and2,847 source/focus/preference/screen/sibling records for offline search; historical images link their original Drive files. Full appendices/tooling remain in the downloadable package and GitHub.

## Prior records and handover

Read [owner-approved assignment](https://github.com/heretoecode/Supernova/blob/docs/project-decisions-2026-10-12/docs/project-decisions/2026-10-12-ui-reference-atlas-codex-assignment.md), [roadmap](../project/DECISIONS_AND_ROADMAP.md), [previous maintenance investigation](../maintenance-investigation/README.md), [prior source atlas](../maintenance-investigation/09_UI_ATLAS_AND_NAVIGATION.md), [shared components](../maintenance-investigation/10_DESIGN_SYSTEM_AND_SHARED_COMPONENTS.md), and [historical Customise audit](https://drive.google.com/file/d/1eR29w23z29BcFFoUCh5ttur_LGMX4Q4A/view). The newer user instruction/current0.138 record supersedes stale4.1.7 or pending-install status in older planning files.

ChatGPT/Claude: start with this index, use stable surface/image IDs, cite source commit and evidence class, then read area report and coverage/gaps. Do not call C a screenshot, confuse fixture labels with production, mark a source concern as a reproduced Shield defect, or infer implementation approval. No independent Claude review was performed. Use the supplied request template for additional detailed references without remapping all app areas.

All changes belong to `docs/ui-reference-atlas-2026-10-12` and documentation paths; no app/tests/resources/build/settings/signing changes, APK build, CI dispatch,0.139 work, maintenance implementation or branch/PR merge. Publication/integrity outcome is recorded in DELIVERY.json.
