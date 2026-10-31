**31 October: 0.148 software conformance passes; signed delivery is blocked by GitHub environment administration access (HTTP403).** See [0.148 status](../0.148/STATUS.md) for the exact allowlist action. No signed 0.148 APK or upload is claimed. The completed unsigned 0.147 inspection artifact below is historical.

# Current unsigned inspection APK

[Download exact candidate artifact](https://github.com/heretoecode/Supernova/actions/runs/41137713737/artifacts/13019305914) from successful [full run 41137713737, attempt 2](https://github.com/heretoecode/Supernova/actions/runs/41137713737). Choose **supernova-next-unsigned-candidate**, then extract `Supernova_next_development_unsigned.apk` and `unsigned-conformance.json`.

- Tested source: `f5e2924cc8f7302032933f8aec8793030b6e1b0f`. Later branch commits are documentation only; source trees are recorded in continuation/PRESERVATION.json.
- Package: `app.supernova.player`; retained development identity **0.147 / code 147**.
- APK: **95,382,168 bytes**; SHA-256 `ab335395cf25743e5506662feb187e77a99c9ceb8066d41d9f17df9b49d3ff5f`.
- Candidate ZIP: artifact **13019305914**, 90,820,817 bytes; SHA-256 `3f7cdf7e4258f9f32054aadb454e6d19aab3bc10c3f8f2e25342d7849cdcd8d5`.
- Full evidence: artifact **13018929794**, 122,883,656 bytes; SHA-256 `a0836e6bb454e2d50a5017f29dc568b17c35facb90f66348ce34a687ec5d7cf1`. Attempt 1's small failure archive is a separate artifact and is not accepted evidence.
- Candidate expiry: **13 November 2026, 23:02:38 UTC** (14 days). Evidence expiry: **28 January 2027, 22:30:37 UTC**. Committed receipts/captures remain available after artifact expiry.

This is an **unsigned inspection artifact**. Android requires a valid signature before installation; it is not an installable signed QA candidate or an upgrade over signed 0.147. CI apksigner verified the unsigned state. Independent local verification checked ZIP/CRC/digests, all 84 native libraries (76 distinct files against staged inputs, eight against the pinned Sentry AAR), native evidence identity and all 139 runtime inventory/POM records against the reviewed SBOM (136 distinct archives). Local apksigner was not rerun because this workspace has no Android SDK.

Full validation: **1,258 app + 300 library tests pass**, four existing library skips; **zero lint errors / 1,496 warnings**. Receipts: [FINAL_VALIDATION](continuation/FINAL_VALIDATION.json), [UNSIGNED_CONFORMANCE](continuation/UNSIGNED_CONFORMANCE.json), [independent payload verification](continuation/INDEPENDENT_UNSIGNED_APK_VERIFICATION.json).

Physical Nvidia Shield, real playback/AV/provider acceptance, original Concept A4 comparison and signed installed upgrade/retention: **NOT RUN**. Procedures: SHIELD_ACCEPTANCE.md. No signing, Drive upload, release, tag or main merge occurred. Original signed 0.147 delivery and protected branches remain preserved. Signed installable QA delivery requires the separate protected signing step.

## Historical candidate

The earlier [7011e87 run 41118855808](https://github.com/heretoecode/Supernova/actions/runs/41118855808) predates continuation changes. Its APK is 95,373,272 bytes, SHA-256 `20c1ef4bab4091b5f817f0d06665d712bc0fbe602149f66b6ea4f2cc0aacae7c`; candidate artifact 13008374956, ZIP SHA-256 `9d845669f60c67e31ccacdadb7e11e79c123074230d6a9a05b4c85ba91607f82`. Original root receipts and software-captures/ provenance remain historical and unmodified.
