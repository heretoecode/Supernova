# Foundation signing delivery and correction status

First signed Foundation 0.133 / 133 succeeded in [37942503242](https://github.com/heretoecode/Supernova/actions/runs/37942503242) using workflow 06014d7d and application 11766e59. APK `Supernova-Foundation.apk`, SHA-256 `0f20d7db872abeeb2edbcb65d4e24ea0454ec7fbe948397ebaee54bd60d14e9f`, expected existing certificate verified independently. User reports successful installation and two Home regressions. See [first binary evidence](FIRST_SIGNED_APK_VERIFICATION.json) and [correction status](FOUNDATION_UI_CORRECTION.md).

The following retry notes are historical, superseded by that successful delivery. The existing key, secrets, protected environment and signing safeguards are preserved. The user authorises corrected signed delivery; after final checks, the corrected source pin must replace 11766e59. A fresh dispatch is required because rerunning the previous workflow snapshot would rebuild old application source.

## Historical first-signing retry

User explicitly approved signed APK source **11766e59dd143798e0c76b6eb8d4b0f6c9e1dfb8** and confirmed enabling protected FOUNDATION_RELEASE_READY=true. Fresh existing-key validation [37935348904](https://github.com/heretoecode/Supernova/actions/runs/37935348904) succeeded on that commit with public certificate SHA-256 **79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e**.

Protected prepare [37935418033](https://github.com/heretoecode/Supernova/actions/runs/37935418033), attempt 1, was cancelled during unusually slow Ubuntu mirror downloads before any APK build/signing. Attempt 2 passed source-built FFmpeg/native regressions and repeated application tests, lint and unsigned conformance; the existing key validated and signed Gradle assembly succeeded. Final verification failed closed and no artifact was published. Temporary signing material and private Gradle logs were cleaned up. The Foundation APK has **not been delivered**; release completion remains open.

## Reproduced tool-format cause and isolated correction

The final step selected the highest installed build-tools version, 37.0.0, although reviewed unsigned conformance explicitly uses installed 36.0.0. With the same retained, publicly signed Preview APK, version 36 prints `Signer #1 certificate SHA-256 digest:` while version 37 prints `V3.0 Signer: certificate SHA-256 digest:`. The strict parser accepted no fingerprint from version 37 and refused publication. This reproduced format difference explains the empty match; it is not evidence that the actual Foundation APK used a wrong certificate. No unverified APK is accepted or published.

Final verification now explicitly selects installed/tested 36.0.0. The pin, valid-signature requirement, single signer, Preview exclusion, package/version/resources/native bytes and allowlisted uploads remain unchanged. The workflow also explicitly fetches/checks the approved **11766e59** app commit for Video, so this workflow-only correction and later delivery documentation cannot change the signed application source. No application source/assets, signing identity, environment policy, main or fixes-only branch are changed.

All 88 local unsigned native inventory hashes already matched verified source 70f62c28; one stale top-level APK checksum field was corrected to the independently recorded **816585f0b442c371cbfdedfea9a65c79a4e21d936f6fc73f413b53819422c31a**. Canonical CI hashes/evidence and readiness remain unchanged.

## Remaining execution action

The executor's short-lived GitHub CLI credential expired with HTTP 401. Connected GitHub tools remain available for reading, artifact download and committing, but expose no workflow-dispatch operation. A rerun of the previous run reuses its old workflow snapshot and cannot apply this correction. After the correction is committed, a fresh manual prepare dispatch on codex/foundation-release is needed. The APK source remains the user-approved 11766e59; no new signing approval, key, password or certificate change is required. Continue through actual signed artifact verification and delivery after dispatch. Physical Shield acceptance remains pending after delivery.
