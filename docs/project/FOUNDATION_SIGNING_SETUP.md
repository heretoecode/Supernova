# Foundation signing setup

**Status: signing implementation prepared; user secrets not provisioned or tested; no APK built or signed.** Work is on `codex/foundation-release` only. The approved permanent application ID is **`app.supernova.player`**. The existing user-owned `.p12` must be used; no replacement key was created. This document covers signing preparation, not completion of the Foundation Release.

## Authority and investigation

Read [Foundation handover](FOUNDATION_CODEX_HANDOVER_2026-10-09.md), [release sequence](RELEASE_SEQUENCE_2026-10-09.md), [technical readiness](TECHNICAL_EXECUTION_READINESS_2026-10-09.md), [project status](../../PROJECT_STATUS.md), [About requirements](../design/ABOUT_SETTINGS.md), and the branding asset inventory. The latest Foundation-first instruction overrides older fixes-first passages. Starting Foundation source: `cd1944554cd7f724b4d5cf045033069de379d968`.

Reviewed root Gradle configuration, manifest/provider contracts, both existing APK workflows, signing verification/recovery workflows, and pinned sibling-source preparation. Existing local signing uses `keystore.properties`; Preview uses its established debug identity and `NOVA_SIGNING_KEY_BASE64`. None of those workflows, secrets, or Preview certificate pins were changed. No signing secret values were fetched.

The previous application IDs, hardcoded community/search provider authorities, legacy version/branding and sibling provider constants still need the separate Foundation identity work. Merely changing `applicationId` would not resolve provider collisions. They explicitly block the future signed-build path. About, assets, packaged licences and Shield smoke tests remain release gates.

Read-only GitHub inspection found a **public** repository, default branch `main`, no configured Actions environments, and no registered Foundation workflow. GitHub requires a `workflow_dispatch` workflow to exist on the default branch before it can be launched. Pushing this branch does **not** satisfy that registration requirement. No main change, merge, default-branch change or dispatch was made. See remaining actions below.

## Implementation

- [Dedicated manual workflow](../../.github/workflows/foundation-signing.yml), limited to `codex/foundation-release`, uses environment **`supernova-foundation-signing`**, a read-only token, pinned official Actions, no signing/Gradle cache, and separate artifact naming. No push/PR trigger.
- Three explicit operations: **`inspect`** reads only public aliases/certificate hashes using the first two secrets; **`validate`** (default) checks the selected private-key entry, key password, certificate validity and key/certificate match, without an APK build; **`prepare`** is the future signed release build. Validation performs only an in-memory cryptographic challenge, never an APK signature or key export.
- [Python secret preparation](../../.github/signing/foundation_signing.py) fails before decoding if required secrets are missing, rejects malformed input and debug logging, decodes standard Base64 directly to `$RUNNER_TEMP/supernova-foundation-signing/foundation.p12`, uses directory mode `0700` and file mode `0600`, refuses stale/symlink directories, and passes passwords through environment variables, never command arguments or properties files. Encoded keystore input is removed from the verifier subprocess environment. Inspect/validate always remove temporary material; build uses both a shell exit trap and an always-run cleanup step.
- [Java PKCS#12 verifier](../../.github/signing/FoundationKeystoreCheck.java) rejects legacy JKS auto-detection, absent private-key entries, wrong credentials, unsupported signing algorithms, invalid certificates or mismatched key/certificate. Raw Java errors are captured and suppressed. Supported key algorithms: RSA, EC, DSA. The actual user key's algorithm, Java 17 compatibility, passwords and validity remain unverified.
- [Gradle](../../build.gradle) uses `-PfoundationRelease` to select the permanent application ID and a dedicated `storeType = 'pkcs12'` signing configuration. It requires the prepared file and all signing environment values, rejects Preview flags/local `keystore.properties`, and rejects explicitly requested debug tasks. Without that flag, existing signing/package selection remains in place. The Java namespace is unchanged.
- Future signed builds additionally require environment variable `FOUNDATION_RELEASE_READY=true`, an independently checked public certificate pin, and source/provider/version gates. **Leave readiness unset/false now.** The workflow does not perform full branding/licence QA automatically; readiness is an explicit future approval after those checks. Current legacy source fails the provider/version gate even if readiness is mistakenly set.
- [APK publication verifier](../../.github/signing/verify_foundation_apk.py) checks APK signature, one approved certificate, application ID and legacy manifest identities; rejects signing-store filenames inside the APK; publishes only the APK and public checksum/certificate evidence. Preview's certificate is rejected. No raw build log, signing config, private key or password is an upload target. Raw Gradle output is redirected to runner-temporary storage and removed without publication.
- `.gitignore` excludes common keystore extensions. This is a preventive measure, not permission to put the key in a checkout.

Only public aliases and certificate SHA-256 values appear in inspection summaries. Validation summaries contain the public fingerprint only. GitHub automatically masks supplied secret values; the implementation also avoids emitting them, including raw third-party errors. Do not enable Actions debug logging. Temporary file deletion is runner cleanup, not a claim of forensic secure erasure; GitHub-hosted ephemeral runners are used.

## Exact protected secret names

Create these **environment secrets** in `supernova-foundation-signing`, rather than reusing Preview secrets:

| Secret | Value |
| --- | --- |
| `SUPERNOVA_P12_BASE64` | Standard Base64 encoding of the **whole original binary `.p12` file**; no quotes, filename, JSON, data-URL prefix, or Markdown. |
| `SUPERNOVA_STORE_PASSWORD` | Existing `.p12` store password, exactly as created. |
| `SUPERNOVA_KEY_ALIAS` | Exact alias of the intended `PrivateKeyEntry`, securely verified below. |
| `SUPERNOVA_KEY_PASSWORD` | Password that unlocks that private-key entry. Often the store password for PKCS#12, but do not assume verification has succeeded. |

These are separate from `NOVA_SIGNING_KEY_BASE64` and all Preview identity recovery mechanisms. A roughly 3 KB file becomes roughly 4 KB of Base64, below GitHub's 48 KB secret limit. Base64 is encoding, **not encryption**: treat it as the private key itself.

Also create these **environment variables** (public configuration, not passwords):

| Variable | Value/status |
| --- | --- |
| `SUPERNOVA_CERT_SHA256` | Independently verified public certificate SHA-256, 64 hexadecimal characters, optionally colon separated. Required for a future build; optional for initial inspection/validation. Do not copy Preview's fingerprint. |
| `FOUNDATION_RELEASE_READY` | Leave absent or `false` now. Set to `true` only after the separate Foundation identity/branding/version/licence readiness review. |

`SUPERNOVA_SIGNING_STORE_FILE` is generated internally on the runner; do not create a secret for it.

## Entering secrets using an iPhone

### Protect the environment first

1. In Safari, sign into your own GitHub account and open [repository Settings](https://github.com/heretoecode/Supernova/settings). Use Safari **Request Desktop Website** if Settings or environment controls are hidden.
2. Open **Environments → New environment**, name it exactly **`supernova-foundation-signing`**, then configure it.
3. Set **Deployment branches and tags → Selected branches and tags**, add a **branch** rule for **`codex/foundation-release`** only. Do not allow arbitrary branches/tags.
4. Add a required reviewer you trust and restrict administrator bypass where available. For a single maintainer who must approve their own manual run, leave **Prevent self-review** off; otherwise the only maintainer cannot approve it. Save the rules before adding secrets. The repository is public, so GitHub's public-repository environment protection features apply; confirm the settings actually saved.
5. In this environment's **Environment secrets → Add secret**, enter names from the table. Use **Environment variables** for the two public configuration values. GitHub will not show a saved secret again; an incorrect value must be replaced.

### Encode the original file locally with Apple Shortcuts

Use only a shortcut you create yourself in Apple's **Shortcuts** app. No website converter, downloaded shortcut, upload service, chat attachment, repository file or Actions artifact is needed.

1. Make the securely backed-up original `.p12` available in **Files** on the iPhone. Download it locally if its trusted backup location currently shows a cloud-only placeholder. Do not rename a text export to `.p12` or convert it to another keystore format.
2. Temporarily turn off **Settings → General → AirPlay & Continuity → Handoff** (older iOS wording may differ) to avoid Universal Clipboard copying the encoded key to other devices. Keep the phone private while handling its clipboard.
3. In **Shortcuts**, create a new shortcut and add these three built-in actions, in order:
   - **Select File**: select a single file using Files. On older versions, use **Get File** with the document picker enabled.
   - **Base64 Encode**: action set to **Encode**, input the **File** output from the previous action. If **Line Breaks** is offered, choose **None**. Do not add Get Text, filename extraction, or any network/share action.
   - **Copy to Clipboard**: input the Base64 output. Enable **Local Only** and a short expiration if those controls are available.
4. First test the shortcut on a harmless plain-text file containing exactly `Supernova` without a trailing newline. Its clipboard output must be exactly **`U3VwZXJub3Zh`**. This checks file-content encoding rather than filename encoding. Do not test by displaying the private `.p12` output.
5. Run the same shortcut again, selecting the **original `.p12`**. In Safari, immediately paste the clipboard into the environment secret value for **`SUPERNOVA_P12_BASE64`**, and save it. A nonempty value of about 4 KB is expected for a 3 KB original. Do not paste it into an issue, commit, comment, message or this conversation.
6. Clear the clipboard by copying harmless text. Delete any accidental temporary text export of the encoded key; keep the original secure key backup. Restore your preferred Handoff setting.
7. Add **`SUPERNOVA_STORE_PASSWORD`** directly using your password manager or private entry. Add the alias and key password after verifying them below. Never send these values to Codex or chat.

**Verification boundary:** Apple's supported file-content/clipboard actions and GitHub's text secret-entry mechanism were checked against their documentation; standard Base64 handling and wrapped input were tested locally. No physical iPhone was available for a device-level walkthrough, and no real keystore was encoded during this work. The harmless known-answer step above is deliberately required before using the key. If your iOS version cannot provide these local actions or produces a filename instead of encoded file bytes, stop there and use a trusted local computer, not an online converter.

## Determine or verify the alias securely

**If the alias is known from the original key creation:** enter that alias directly as `SUPERNOVA_KEY_ALIAS`. Alias alone is not private key material, but the workflow still stores it as a protected secret. Validation must confirm it identifies the intended private-key entry.

**If the alias is unknown and only an iPhone is available:** after the workflow-registration blocker below is resolved, supply the first two secrets, open the repository's **Actions → Foundation signing setup and release → Run workflow**, select **`codex/foundation-release`** and operation **`inspect`**, and approve the protected environment if prompted. Read the job **Summary**, which shows public aliases and their certificate SHA-256 hashes only. No APK build or artifact upload occurs. If more than one private-key alias exists, choose the identity intended when the key was created; do not guess. Inspection does not prove the private-key password works.

**Trusted local computer alternative:** with an existing JDK installed, run this locally (replace only the file path):

```sh
keytool -list -v -storetype PKCS12 -keystore /private/path/original.p12
```

Enter the password at the tool's private prompt; do not use `-storepass actual-password` on the command line. Read **Alias name**, **Entry type: PrivateKeyEntry**, and **SHA256** certificate fingerprint. Do not export the key, post the full output, or save it in this repository. If no `PrivateKeyEntry` exists, the file is unsuitable for Android signing; investigate the original export rather than generating another key.

After choosing the alias, enter the remaining two secrets. Once registered, run operation **`validate`** on the Foundation branch, approve the environment, and check the public success summary. Wrong alias/password/format produces a fixed error and cleanup, not raw diagnostics. This does not sign an APK. Verify the public certificate fingerprint against the original creation record or trusted local inspection, then set `SUPERNOVA_CERT_SHA256`. For an iPhone-only inspection, the user must confirm the selected file is the securely backed-up original; runner output alone is not independent proof of provenance.

## Testing and current limits

Safe automated tests: `python3 -m unittest discover -s tools -p 'test_*.py'`. The added [Foundation tests](../../tools/test_foundation_signing.py) cover each missing secret before file creation, strict/wrapped Base64, readiness and certificate guards, file permissions, special-character password preservation, no credential arguments, raw-error suppression, success/failure cleanup, temporary path export, symlink refusal, escaped public aliases, and APK publication gates.

Real Java verifier negative tests load corrupt bytes and **empty PKCS#12/JKS containers**, with correct/incorrect dummy passwords. Empty containers contain no keys or certificates; no signing key was generated. Positive plumbing/APK-tool responses are mocked. **The user's real PKCS#12, a successful private-key check, Android Gradle build, actual APK signature, iPhone execution, and Shield installation have not been tested.** There was no workflow dispatch or release publication.

Workflow syntax/expression checks use checksum-verified official **actionlint v1.7.12** with `-shellcheck=''`; shellcheck was not available, so no shellcheck result is claimed. YAML/security invariants and `git diff --check` were also checked. The future dependency/build path is adapted from existing pinned build preparation but has not been executed; its package/tool availability and Android Gradle behavior require the later release build review.

## Remaining actions and problems

1. **User:** configure the protected environment and enter the existing key's secrets directly using the instructions above. No file or password should be supplied to this conversation.
2. **Workflow registration blocker:** the new workflow is absent from `main`, and therefore cannot yet be manually dispatched through GitHub's supported workflow-dispatch mechanism. An explicitly authorised later change must register the workflow on the default branch (or provide a separately reviewed existing registered dispatch entry). **Do not change the default branch or launch a Preview workflow as a workaround.** This task does not authorise a main merge; none was attempted. Secrets can be entered before registration. If the alias is unknown and no trusted computer is available, defer its entry until safe inspection is available.
3. **User/later authorised validation:** inspect if needed, validate the real existing key without an APK build, establish and pin the correct public fingerprint. Password equality, selected alias, certificate dates/algorithm and Android signing compatibility remain actual-key questions.
4. **Foundation implementation:** complete provider/sibling identity isolation, permanent versioning, label/assets/About, native-library licence obligations and readiness review. Do not set `FOUNDATION_RELEASE_READY=true` before those gates. The signing setup deliberately leaves these release tasks open.
5. Only after secrets, key verification and Foundation readiness: explicitly select `prepare`, verify the actual APK/package/certificate evidence, and perform the clean Shield smoke tests. No signed build is authorised or attempted in this preparation session.

## Commit references

Starting Foundation revision: [`cd194455`](https://github.com/heretoecode/Supernova/commit/cd1944554cd7f724b4d5cf045033069de379d968). Implementation and final verification commits are recorded below once committed. The fixes-only branch and main are not targets of any push in this task.

## References checked

- [GitHub encrypted Actions secrets and Base64 file storage](https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-secrets)
- [GitHub secret limits/security](https://docs.github.com/en/actions/reference/security/secrets)
- [GitHub protected environments](https://docs.github.com/en/actions/reference/workflows-and-actions/deployments-and-environments)
- [GitHub manual workflow/default-branch requirement](https://docs.github.com/en/actions/how-tos/manage-workflow-runs/manually-run-a-workflow)
- [Apple Shortcuts file/content types](https://support.apple.com/en-mide/guide/shortcuts/apd7644168e1/ios)
- [Apple Shortcuts share actions and clipboard](https://support.apple.com/guide/shortcuts/share-actions-apdaf74d75a5/ios)
- [Oracle JDK 17 keytool/PKCS12 and password handling](https://docs.oracle.com/en/java/javase/17/docs/specs/man/keytool.html)
