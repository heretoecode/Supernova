# Unsigned development APK

**Continuation candidate:** source `83aede983501c0ad728576a144d2a0ef51998190` passes 182 focused tests and six isolated library cases. Full conformance/build [41133244918](https://github.com/heretoecode/Supernova/actions/runs/41133244918) is still running. No new APK has yet been accepted. The download below is the **previous** checkpoint and does not include the continuation changes.

## Previous completed candidate — historical

[Successful build and download](https://github.com/heretoecode/Supernova/actions/runs/41118855808): choose artifact **supernova-next-unsigned-candidate**, then extract `Supernova_next_development_unsigned.apk` alongside `unsigned-conformance.json`.

- Tested source: `7011e877d026e745a079846827ababe03c2af798`.
- Package: `app.supernova.player`; development identity **0.147 / code 147**.
- APK: **95,373,272 bytes**.
- APK SHA-256: `20c1ef4bab4091b5f817f0d06665d712bc0fbe602149f66b6ea4f2cc0aacae7c`.
- Candidate ZIP artifact ID: `13008374956`; SHA-256 `9d845669f60c67e31ccacdadb7e11e79c123074230d6a9a05b4c85ba91607f82`.
- Candidate artifact retention: **14 days** from 30 October 2026; evidence retention uses the repository default. Receipts and selected captures remain committed after artifact expiry.

This APK is **unsigned** and cannot serve as an installed upgrade over the signed 0.147 app. CI apksigner verified that it is unsigned; local independent checks verified complete ZIP/CRC/digests and all 84 packaged native libraries. No signing, Drive upload, release, tag or main merge occurred. The original signed 0.147 delivery is preserved. A signed next-version QA build remains pending feature/design completion and the protected signing step.

Physical Nvidia Shield, real playback/AV/provider testing and installed upgrade/retention: **NOT RUN**. See SHIELD_ACCEPTANCE.md. Full receipts: UNSIGNED_CONFORMANCE.json, FINAL_VALIDATION.json and INDEPENDENT_UNSIGNED_APK_VERIFICATION.json.
