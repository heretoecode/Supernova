# Resume signed 0.149 delivery

The owner-authorised delivery was attempted after green conformance. GitHub rejected [run 41331247034](https://github.com/heretoecode/Supernova/actions/runs/41331247034) before any steps: `Branch "codex/agreed-fixes-2026-11-01" is not allowed to deploy to supernova-foundation-signing due to environment protection rules.` No signing key was accessed, no rule was changed and no alternate certificate was used.

Required owner/admin action: in the existing `supernova-foundation-signing` environment, add **only** the exact branch `codex/agreed-fixes-2026-11-01` to the existing permitted deployment branches. Retain all other branches, protections, secrets and signing identity. Secret metadata access through the current integration returned HTTP 403; the existing secret contents/presence have not been inspected or claimed missing.

The ready candidate is [successful conformance 41328625056](https://github.com/heretoecode/Supernova/actions/runs/41328625056), tested source `cb28b1b936891374bc801f0b58b3397926fc3086`. Artifact [13093101711](https://github.com/heretoecode/Supernova/actions/runs/41328625056/artifacts/13093101711) contains `Supernova_0.149_unsigned.apk` and its complete conformance receipt. Artifact retention is 14 days; rerun conformance if it expires. Subsequent source/assets/build changes require another conformance run; documentation-only descendants are explicitly permitted.

After the existing environment permits the branch, dispatch:

```sh
gh workflow run supernova-0149-qa.yml \
  --repo heretoecode/Supernova \
  --ref codex/agreed-fixes-2026-11-01 \
  -f conformance_run_id=41328625056
```

The workflow independently checks the successful exact-source run, payload hash, all test/lint/native/resource gates and existing certificate before private-key access. It signs the tested payload and checks all non-signature APK entries for parity. It publishes `Supernova_0.149.apk` plus a public verification receipt only after success. The required certificate is `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`.

Delivery is still unfinished: download and independently verify that signed artifact, compare package/version/certificate and payload against the tested unsigned build, upload once to existing Drive APK folder `1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM`, then download the complete uploaded bytes and recheck checksum, manifest and signature. Record the signed checksum and actual Drive link only then. Do not treat the unsigned artifact as installable QA. Perform feasible installed/emulator checks and retain [physical Shield acceptance](SHIELD_QA.md) as pending until observed.
