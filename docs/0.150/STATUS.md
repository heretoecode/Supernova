# Supernova 0.150 Shield QA — delivered; physical verification pending

[Download Supernova_0.150.apk](https://drive.google.com/file/d/1wKejCBYoHvBft1HsuMXeOyIsoEwveVFi/view?usp=drivesdk)

| Item | Verified value |
| --- | --- |
| Version / code | 0.150 / 150 |
| Application ID | app.supernova.player |
| Exact tested/APK source | `8d3c46abd1baa25e912ca9b29a59e617f34beeea` |
| Corrections baseline | `538cb869a4bd7e3b5a84c7997437dbc45c37a321` |
| Filename / bytes | Supernova_0.150.apk / 95,606,623 |
| SHA-256 | `26f23e7ff0203469f41dc49d24121a71be52227b78edda5a423af24b3848822e` |
| Existing certificate SHA-256 | `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e` |
| Complete conformance | [41402999860](https://github.com/heretoecode/Supernova/actions/runs/41402999860), success |
| Protected signing | [41405684145](https://github.com/heretoecode/Supernova/actions/runs/41405684145), success; cleanup passed |
| Drive readback | All bytes identical; signature, manifest, embedded source and payload verified again |
| Physical Shield / installed upgrade | **PENDING — owner testing required** |

The complete exact-source run passed **1,295 application tests**, **300 library tests plus four existing reviewed skips**, and **157 tooling tests**. No test failures/errors; no application/tooling skips. Release lint: **zero errors / 1,251 warnings**. APK assembly, native/runtime/branding/history gates and embedded source-ID verification passed. All six retained UI captures (Settings, regional icons, initial/refreshed Custom, normal/enlarged Reception) are byte-identical to the preceding software-reference captures. The conformance artifact did not retain the separate Subtitles/switch-face capture directories; corresponding tests passed, and no visual comparison of missing files is claimed.

Application code, Java tests, resources, manifest, native inputs and approved designs remain byte-for-byte unchanged from 538cb869. The version/history asset advances to 0.150/code150 and preserves the previously delivered 0.149 binary record. The historical mapping test now expects the authorised version; seven new tooling tests exercise the version-specific signing gates. No application regression required a new code change.

Signing used the existing protected environment, existing certificate and secrets, preserving dispatch-only signing, source/tree pins, full-suite gates, APK hashes, secret isolation and cleanup. It signed the exact tested unsigned payload without rebuilding it. Independent checks confirm v1/v2/v3 signature validity, 16KB ZIP alignment, unique/valid ZIP entries, all 84 native hashes, retained regional/Reception resources, matching source/history and complete signed/unsigned payload parity. APK resource presence is not claimed as physical rendering evidence.

One new APK was added to the existing APK folder; all 17 older entries remain. No existing file or sharing setting changed. No production release/tag or merge was created. Existing PR #30 remains draft/unmerged.

## QA acceptance and limits

The owner still needs to test Custom Library entry without scrolling, actual icons across all language surfaces, OFF/ON/focus/disabled switches, Reception with genuinely available metadata, playback and installed upgrade on Shield. Software captures and package/certificate compatibility checks do not establish device functionality or a successful installed upgrade.

The existing metadata access gate does not supply all Reception provider scores/counts/award details. Missing values are never filled with screenshot examples. Residual legacy focus symptoms, scan/source failures, DNS/time-out infrastructure causes and the native file-error cause remain limited by the partial raw archive, as documented in the [correction investigation](../0.149/OWNER_REFERENCE_AND_RAW_DIAGNOSTICS_REVIEW_2026-11-01.md).

There is no application Android instrumentation source set. Existing API23 transport/native-security proof is reused only for unchanged pinned dependency/native inputs; no new emulator or Shield runtime test is claimed.

[Full automated results](FINAL_VALIDATION.json) · [Unsigned conformance](UNSIGNED_CONFORMANCE.json) · [Protected signed receipt](SIGNED_QA_EVIDENCE.json) · [Independent signed verification](INDEPENDENT_SIGNED_APK_VERIFICATION.json) · [Drive delivery](DRIVE_APK_DELIVERY.json) · [Independent Drive readback](DRIVE_APK_READBACK_VERIFICATION.json)
