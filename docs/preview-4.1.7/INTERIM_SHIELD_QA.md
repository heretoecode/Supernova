# Preview 4.1.7 — INTERIM SHIELD QA BUILD

This is an unfinished development snapshot for Mark's physical Nvidia Shield testing, not the final Preview 4.1.7 APK. The complete requirements audit and three-pass conformance review remain in progress. No Preview 4.1.8 work is authorised.

## Exact source

- Branch: `codex/preview-4.1.7`
- Build commit: `ff955d90d7a76d157776b07cfb116fd7c67ea924`
- Build run: <https://github.com/heretoecode/Supernova/actions/runs/37192141331>
- Frozen application source: `07aef20b29b85aebb6cf2364f98af4bd9fbb5dd1`; subsequent interim commits change validation scripts/workflow/documentation only. GitHub comparison confirmed the final delivery-trigger commit changes only `.github/workflows/build-preview-apk.yml` relative to `bf8af15cd58733439b3da3a8e7e4d57726bd4210`.
- Package: `org.courville.nova.markpreview`
- Version: `6.4.63-mark.4.1.7-preview`
- Required signing certificate SHA-256: `89ac087ed6f989c90482d4a999f80511fe6ceee26ef1b9c37a142a9f00d39a5a`

The APK's delivered filename will explicitly identify it as INTERIM. Its internal application identity, version strategy and branding are unchanged.

## Known defect explicitly accepted for this snapshot

During playback, opening Info and pressing Back can leave the HUD hidden instead of restoring focus to Info. The technical-only overlay itself opens; the captured runtime did not show a fatal exception. This was reproduced with actual remote-key input in emulator run 37156506482. Mark explicitly chose to deliver the unchanged snapshot with this defect.

The unchanged assertion is run after APK upload for this explicitly marked interim only. A red playback check is a real failure, not a passing or waived final-release test. Full/final validation continues to require it before packaging. The subsequent local correction is deliberately excluded from this APK.

## Validation and delivery

Run 37192141331 built the optimised signed universal release APK. Preview launch/reinstall/restart, Settings/Movies/TV/browser/Search smoke, signed development-to-release upgrade, release restart and packaged certificate verification passed. The post-upload playback check reproduced exactly `Back did not restore HUD Info focus`; the job is correctly red, with failure evidence retained in artifact 11300210341. This is not a fully passing build.

- APK: `Supernova-4.1.7-INTERIM-SHIELD-QA-ff955d90.apk`
- Size: 82,736,779 bytes
- APK SHA-256: `6636e08f48406df22be046241f560f95ff8e9fd084632afebf31a7f47cd9e079`
- APK artifact: 11300170330; its downloaded archive SHA-256 matched GitHub's digest `8294765f4fc80e93aa006cc86d64e325efadb07c527e6b966d96fd52d15bf4c3`.
- `apksigner verify --verbose --print-certs` passed with the required certificate above.
- Local extracted APK SHA-256 matched the CI output. Its packaged `res/vy.png` matches the approved 320×180 Shield banner pixel-for-pixel. Native libraries include arm64-v8a, armeabi-v7a, x86 and x86_64.
- Classic clean startup was not run in this preview-mode build. Actual installed-4.1.6 data preservation/coexistence remain physical tests, not established by the emulator development-to-release upgrade.
- Remote `main` remains `77b2617ad1e48b7a7a85ba28463de0961af5ba4d`.

Fresh source validation run 37192141281 passed compilation and identity/log-safety checks, 137 targeted tests, all 391 Video unit tests, 92 selected regression tests and 17 WebDAV tests. Counts were read from the successful logs and XML reports in artifact 11299472402. These suites overlap and must not be added into a unique-test total. The merged manifest reports Supernova, the expected Preview package/version, and the approved banner reference; its inherited shared-user warning remains an external coexistence investigation, not an established failure cause.

## Physical testing limitations

Emulator and unit tests do not establish physical Nvidia Shield behaviour. Mark's checks include D-pad repeats/focus, actual layout/glow/crops, hardware playback and seeking, audio/subtitles, WebDAV, real-library/data preservation, launcher/banner, diagnostics soak, installed 4.1.6 upgrade and upstream coexistence. Do not uninstall or clear app data to work around an installation failure; retain the exact installer error for investigation.

Production put.io OAuth/link configuration is still unavailable. Live production authentication is not verified. Existing authorised signing material is available; no substitute key was generated. Upstream APK/certificate and installed shared-user/installer evidence remain separate external requirements.

Continue implementation and validation towards a separately built FINAL APK; this interim is not to be renamed into the final release.
