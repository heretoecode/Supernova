# Supernova 0.139 signing access — exact administrator action

Final implementation `23f75b38b18f6b34f5ceb94c001260f08bafac09` passed [CI38746280548](https://github.com/heretoecode/Supernova/actions/runs/38746280548). The downloaded unsigned artifact independently passes package, source assets, native evidence, CRC and 16 KiB alignment checks. See CI_APPROVED_VERIFICATION_2026-10-15.json and CI_APPROVED_PACKAGING_2026-10-15.json. Signing and delivery are already owner-authorised.

The remaining signing access block is confirmed: the exact branch-policy POST returns HTTP403 `Resource not accessible by integration`. The integration reports account `admin:true`, but lacks the endpoint permission. Before/after readback preserves all five existing selected branches. See SIGNING_CONTINUATION_RECHECK_2026-10-15.json. No signing dispatch or key access occurred.

## Administrator UI action

1. Open [heretoecode/Supernova repository Settings → Environments](https://github.com/heretoecode/Supernova/settings/environments).
2. Open **supernova-foundation-signing**.
3. Under **Deployment branches and tags**, retain **Selected branches and tags** and all existing entries.
4. Add a deployment rule with type **Branch** and the exact name **codex/supernova-0.139-implementation-2026-10-13**. Save it.
5. Preserve the existing protection rules and signing secrets. Do not select all branches, permit a wildcard, move the source onto an older allowed branch, replace the certificate or export the key.

Alternatively, an administrator credential with repository **Environments: write** permission can perform the same narrow operation:

```sh
gh api --method POST \
  repos/heretoecode/Supernova/environments/supernova-foundation-signing/deployment-branch-policies \
  -f name=codex/supernova-0.139-implementation-2026-10-13 \
  -f type=branch
```

An approved GitHub App configuration granting Environments write permission is another administrative route; repository permission changes may require installation-owner approval. Merely confirming repository account ownership does not alter the current integration token's permissions.

## Continue after policy readback confirms the exact branch

The prepared workflow is registered as **Supernova 0.139 signed QA**, `.github/workflows/supernova-0139-qa.yml`. It repeats complete conformance before its single secret-bearing step, uses the existing protected environment and verifies the certificate and tested unsigned payload before publishing its signed QA artifact. Dispatch on the existing branch only:

```sh
gh workflow run supernova-0139-qa.yml --repo heretoecode/Supernova \
  --ref codex/supernova-0.139-implementation-2026-10-13 \
  -f approved_qa_distribution=true
```

Record the actual dispatched HEAD and run ID. Current release-preparation descendants change only documentation/evidence; compare all application/test/build/workflow/dependency/signing paths with validated23f75b38 before dispatch. The signing run will rebuild and validate its own exact SHA. Do not relabel the earlier unsigned run as a signed run.

Required certificate SHA-256: `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`.

After success, download `supernova-0139-signed-qa`; independently verify signature/certificate, `app.supernova.player`, version0.139/code139, SDK23/36/non-debuggable, CRC/16KiB alignment and unsigned payload parity. Upload only the verified `Supernova_0.139.apk` to existing [Supernova/APKs](https://drive.google.com/drive/folders/1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM), folder ID `1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM`. Read metadata, download the uploaded copy and match its SHA-256 before returning its observed direct link. Preserve the user's existing sharing and files.

No signed APK, signed checksum or 0.139 Drive file link exists in this continuation. The CI unsigned APK SHA-256 `a2681451a9b57c13e67d0e6a48f979d96d594e8a2d8614f007bd5050b01879c7` identifies evidence only and must not be presented as the requested signed deliverable.

Physical Shield focus/output/passthrough/HDR, actual OS-force-stop recovery, owner incident reproduction and138→139 signed upgrade remain **NOT RUN**. Producing the signed QA APK is needed to perform that upgrade test; hardware acceptance is not a prerequisite to signing this conformance-passing QA candidate. No main merge, tag, public GitHub release or0.140 work is authorised.
