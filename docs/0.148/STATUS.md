# 0.148 signed QA — delivered and verified

Delivered on **31 October 2026**. No delivery access blocker remains. The owner-added branch policy was read back before signing; the existing environment protections and all 15 branch policies remain intact after signing.

## Exact delivery

- File: **[Supernova_0.148.apk](https://drive.google.com/file/d/1Fz8PJYvYpbwKJv_W0wg3frpRLlhLYMuw/view?usp=drivesdk)**, **95,585,975 bytes**.
- SHA-256: **44c156c55798799cca8de8b3388e9d2ba23c7e328238193b8b4a50a4bd0f2898**.
- Version: **0.148 / code 148**; package: **app.supernova.player**.
- Existing signing certificate SHA-256: **79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e**.
- Exact validated build source: **30d4961e4e5b0e412ec35fe8d27cd6197cea8b26**. Later branch commits contain documentation only.
- [Signing run 41191359800](https://github.com/heretoecode/Supernova/actions/runs/41191359800) **PASS**, completed **10:06:51 UTC**, reused **conformance_run_id=41185088587**. Workflow head a5be2856d40d2b4c3305a184a9ec90447314056e detached the exact validated source before signing the existing conformance APK. No application rebuild or local signing.

Application/test source, resources, manifest and original native/build/signing inputs match validated PR29 checkpoint **b0bce99a41b8321606059f4bfe5b5c16da741c6b**. Authorised version/history metadata and new version-specific QA orchestration are the only non-documentation changes from that checkpoint. No user-data schema or reset/migration change.

## Completed checks

- [Full conformance 41185088587](https://github.com/heretoecode/Supernova/actions/runs/41185088587) **PASS**, completed **08:59:16 UTC**: **1,258 application + 300 library tests pass**, four existing library skips. Release lint: **zero errors / 1,496 warnings**.
- [Focused 41185088529](https://github.com/heretoecode/Supernova/actions/runs/41185088529): **183 pass**; [isolated library 41185088530](https://github.com/heretoecode/Supernova/actions/runs/41185088530): **six pass**. Subsets are not added to full totals. Downloaded SHA/CRC and JUnit readbacks pass.
- **140 local tooling checks pass**, including source/version/certificate/branch and pre-key-access rejection checks.
- Independent signed APK verification: valid **v1/v2/v3 signatures**, pinned certificate, SDK23–36, binary manifest package/version, exact embedded source, ZIP CRC, all **2,445 non-signature payload entries** identical to the validated unsigned APK, all **84 native hashes** matching CI and validated PR29, and minimum **16KB ELF LOAD alignment**. No shared UID; private internal services retained. Ordinary apksigner v1 META-INF warnings are recorded in the receipt; v2/v3 verification passes.
- Unsigned provenance: **139 runtime inventory/POM records / 136 distinct archives** match the reviewed SBOM. Existing Android23 transport proof is reused for unchanged inputs; no physical rerun.
- Complete APK Drive download is **byte-identical** to the verified signed artifact. Signature, manifest, source, payload and native checks pass again on the downloaded file. APK folder **1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM** has 16 entries; all previous 15 IDs/names/sizes are preserved. Existing files and sharing were not changed.

Receipts: [signed CI evidence](SIGNED_QA_EVIDENCE.json), [independent signed verification](INDEPENDENT_SIGNED_APK_VERIFICATION.json), [Drive download verification](DRIVE_APK_READBACK_VERIFICATION.json), [delivery](DRIVE_APK_DELIVERY.json), [signing authorisation](SIGNING_AUTHORIZATION.json), [signing run/artifact](SIGNING_RUN.json), [software validation](FINAL_VALIDATION.json), [checksum](Supernova_0.148.apk.sha256).

## Published HTML

The established **[Supernova Development.html](https://drive.google.com/file/d/17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW/view?usp=drivesdk)** was updated in place. All **16 historical articles remain byte-for-byte unchanged and ordered**; the new 0.148 article brings the total to 17. Name, parent and sharing are preserved. Full downloaded bytes match [RELEASE_NOTES.html](RELEASE_NOTES.html) and the canonical repository release notes.

HTML: **55,628 bytes**, SHA-256 **0c20e247ffc1d1384b293d25eabb9205d9a1bfcdc3a429f701c1c044b4acfa12**. Proof: [HTML_RELEASE_NOTES.json](HTML_RELEASE_NOTES.json). The earlier RELEASE_NOTES_DRAFT.html and resolved SIGNING_ACCESS_BLOCKER.json are historical records, superseded by this verified delivery.

## Preserved state and remaining acceptance

Branch: **codex/supernova-next-implementation-2026-10-30**. PR #29 remains **draft**, unmerged, with its existing base. Main remains **75d9f012fa4f65869eef5d972ad4725df504bd9c**; other development branches are untouched. No public release or tag.

No Shield access, installation, uninstall, reset or app-data modification. Physical signed upgrade/data retention, real AV/provider/layout performance and original Concept A4 comparison: **NOT RUN**. Static identity/certificate/schema preservation does not constitute an installed upgrade test. Unverified ChaptersDB sets remain hidden under the owner's strict-matching decision. No personal decision is required to complete this delivery.
