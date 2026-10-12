# Reusable visual-reference request and update protocol

This is documentation tooling, not a UI implementation/build workflow. Prefer unchanged native evidence from an existing exact-source CI artifact; otherwise a labelled source-derived reconstruction is permitted. Do not edit app source, tests, resources or build settings, build/sign an APK or dispatch CI under this assignment.

## Request template

```text
Area and stable surface IDs: e.g. WIZ-001..WIZ-007 / DET-005 / NET-004
Source branch and exact commit; target APK/certificate if using an existing device:
Entrypoint and complete navigation/key sequence:
Required states: initial/focused/selected/checked/scroll/empty/loading/error/cancel/return
Options that must appear, in exact source order:
Controlled fixture: snapshot/preferences/URI schemes/capabilities/provider offline policy
Logical viewport dp, pixel size, density, fontScale, locale, system bars/overscan:
Evidence class: A Shield / B Android native-test / C approximation
Priority: prefer existing native artifacts; missing native may use labelled C
Existing artifact/image IDs to preserve and reuse:
Destination folder and atlas version:
Verification required: labels/order/bounds/icons/focus callbacks/fixture values
Known device-only unknowns and requested scope of new capture:
```

For Library stats specify movie/show/episode identities, getFileUri schemes, indexed bytes/knownSizes and selected custom entries. For Customise specify saved-order rows, visibility/dynamic/types/genres/sort/direction/maximum/members/calculated, cached genres and focus/opener. Use synthetic data without account credentials, real media filenames or private databases.

## Production steps

1. Read assignment and area inventory; resolve stable ID and current owner/class/method/resources. Pin APK source separately from receipt/branch head. Diff app/resources/tests against the candidate baseline before reusing renders.
2. Check existing image manifest and CI artifact first. Read the exact capture test: fixture-generated menu options or substitute validator text can differ from production. Preserve original bytes/filenames/provenance; add a new record for another commit.
3. Prefer existing genuine Android native/Robolectric captures. Record run/artifact/archive entry/test method, viewport/fixtures/digest/date and every verified deviation. A native View fixture is not the full running app or Shield.
4. If fresh native capture is unavailable within authorised read-only scope, document the precise reason. C reconstruction may transcribe real source layout/text/icon geometry with controlled fixtures; add a visible C banner. Do not invent UI/data/assets, use AI concept images or generic internet imagery. Source is evidence; reconstruction is an illustration of it.
5. Validate every required state against an option/state coverage matrix. Source assertions don't prove D-pad execution. Native rendering without invoking the actual production callback must say so. Record missing alternatives instead of filling them speculatively.
6. Inspect all delivered images; check expected text/options/clipping and compare source/test provenance. Save unmodified PNGs, per-image manifest, fixture inputs, source links, coverage and deviations. Keep historical entries unchanged.
7. Publish additive Drive files in correct area, verify IDs/metadata/parents/size/readable contents, then update Drive master/index and docs-only GitHub index. Never alter sharing silently. Only a draft docs PR; never merge.
8. Reassess acceptance; carry gaps forward with IDs/owners/methods and a precise capture recipe. Handover to ChatGPT/Claude references actual artifacts and does not claim their independent review occurred.

## Tooling supplied

- `tooling/extract_inventory.py --source <pinned Video checkout> --output <evidence> [--medialib <pinned checkout> --filecore <pinned checkout>]`: catalogue declared screens, XML defaults/ordered arrays, source display/focus branches and sibling resources. Read-only source; output only under documentation. Syntactic coverage is not runtime certification.
- `tooling/render_reconstructions.py --source <same Video checkout> --output <visuals>`: reproduce current35 category C illustrations using source assertions and Chromium. Requires Python Pillow/Playwright, system Chromium and DejaVu Sans. Selected Android geometry/icons are deliberately transcribed; this is not a Java/Android renderer. Current source SHA is pinned in tooling and must be explicitly reviewed/revised when updating the baseline.
- `tooling/write_reports.py`: authored area map/effective preference trace, current source path stored at its top. Review content on any new SHA; changing a constant alone is insufficient.
- `tooling/package_evidence.py`: collects the ten exact0.138 artifact PNGs and historical metadata from downloaded/extracted CI archives at documented scratch paths. Obtain artifacts11728856790 and11691549941 through authenticated GitHub downloader; extract complete ZIP with CRC checks. It does not run tests or build an APK.
- `tooling/build_master.py`: regenerate the standalone searchable HTML report and review galleries after manifests/Drive receipts are updated.

A new native harness/test/CI workflow or device install would require authority beyond the current guardrail against app/tests/build changes. Existing captures and docs-only C tooling can proceed under this assignment. Native upgrade requests should explicitly define a permitted environment/harness; do not silently start0.139 or maintenance work to get a screenshot.

## Manifest contract

Each image has: stable image ID, surface/state, A/B/C/D class, real source branch+SHA, method/fixture/test references, run/artifact if relevant, date, logical viewport and actual pixel dimensions, synthetic/runtime data provenance, SHA-256, original file path/Drive link, known deviations and unknowns. For A add Shield model/OS/output chain and installed APK identity; for B exact native renderer/test; for C source-to-layout method/font/geometry limits; D cannot satisfy current-interface evidence.

Surface IDs (NAV/HOME/CH/LIB/WIZ/DET/SRCH/KEY/NET/HEALTH/CLOUD/SET/ABOUT/DIA/BAK/PLAY/SHARED/OTHER) persist across versions. SITE/FOCUS IDs identify a frozen source location and may change on line shifts; map them back to persistent surface IDs when rebaselining. Add versions rather than silently overwrite historical evidence.
