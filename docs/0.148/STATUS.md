# 0.148 signed QA — preparation in progress

Owner authorised the next unique signed QA APK and verified upload on 31 October 2026. Version **0.148 / 148**; package **app.supernova.player**; existing certificate SHA-256 **79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e**.

Build source: **30d4961e4e5b0e412ec35fe8d27cd6197cea8b26**. Application/test source, resources, manifest and original native/build/signing inputs match validated PR29 checkpoint **b0bce99a41b8321606059f4bfe5b5c16da741c6b**. Authorised version/history metadata and version-specific QA orchestration are the only non-documentation changes. The source gate runs before build and before signing; later changes must be documentation only.

- Full conformance [41185088587](https://github.com/heretoecode/Supernova/actions/runs/41185088587): running; signing is gated on success.
- Focused [41185088529](https://github.com/heretoecode/Supernova/actions/runs/41185088529): **183 pass**, zero skips/failures. Archive SHA/CRC and JUnit readback verified.
- Isolated library [41185088530](https://github.com/heretoecode/Supernova/actions/runs/41185088530): **six pass**. Archive SHA/CRC and JUnit readback verified.
- Local tooling: **140 pass**, including source/version/certificate/branch and pre-key-access rejection checks.
- Existing protected signing process is prepared; dispatch and actual signing remain pending full conformance. No signed 0.148 file or upload is claimed yet.

Target APK folder: **1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM**, metadata name **APKs**; 15 existing entries recorded and preserved. Existing HTML file: **17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW**, text/html, 16 historical release articles recorded before updating. Upload and complete Drive byte readback will precede delivery claims and final HTML publication.

PR29 remains draft; main and existing release branches remain unchanged. No Shield access, installation, uninstall, reset or data modification. Physical signed upgrade/retention and hardware acceptance: **NOT RUN**. Unverified ChaptersDB sets remain hidden under the owner's strict policy.
