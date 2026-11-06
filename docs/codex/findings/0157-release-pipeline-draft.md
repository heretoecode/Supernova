# Version-neutral release pipeline draft — 0.157

Draft only. No active workflow, Gradle, signing helper, archive, release history or 0.156 file is changed. Do not
dispatch an existing 0.156 workflow for these new JNI capabilities. Integration belongs on the source branch after
review of a newly built native archive and its receipt. The following is the proposed complete gate sequence.

## Inputs and authority

Commit one reviewed `release-candidate.json` beside the future generic workflow. Read it from the checked-out source,
never from a dispatch text field or an unsigned artifact. Fields: schema, application ID, version name/code, minimum
API, supported ABI list, MediaLib/FileCore pins and ordered patch hashes, engine source commit, packaging-recipe
commit, archive URL/SHA-256, native capability list, expected signing certificate SHA-256, and required test classes.
The archive hash must describe actual reviewed bytes. There is deliberately no invented 0.157 archive hash here.
For this delivery require `seek_picture_v1` and `frame_observation_v1`; an old archive must fail this admission gate.
The receipt must identify every native source/patch and every packaged library hash, not just the archive's name.

Use generic workflow/artifact names: `supernova-conformance`, `supernova-unsigned-candidate`,
`supernova-conformance-evidence`, `supernova-qa`, `supernova-signed-qa`. Versioned filenames inside an artifact come
from the checked-in manifest. Every receipt carries the full source commit and candidate-manifest SHA-256.
Retain old versioned workflows as historical entry points; never rewrite a completed 0.156 run's authority.

## Unsigned conformance

1. Manual dispatch only, read-only contents/actions permissions, no signing environment or secrets. Concurrency is
   workflow/ref scoped and may cancel an obsolete unsigned run. Checkout the event's exact full SHA with persisted
   credentials disabled. Use the currently pinned checkout/setup-java/download/upload action revisions, Java 21,
   Java 17 target, API-23 minimum and pinned Android tools.
2. Validate the candidate manifest before downloading anything. Verify archive SHA-256, reviewed engine receipt,
   source and recipe identities, required capabilities and ABI set. Obtain the packaging recipe from its pinned
   commit in a detached worktree, rather than executing a recipe supplied by the downloaded archive. Reject path
   traversal, symlinks, duplicate libraries, missing ABIs and any hash mismatch using the existing packager checks.
3. Prepare pinned dependency sources with every registered operation and hash checked, including the original four
   0.157 patches and the two continuation Java patches before test-fixture patches. Build the existing Jetty security
   backports; preserve their embedded receipt. Never silently fall back to an unpatched repository JAR.
4. Run the existing full Gradle command: dependency inventory, app unit tests, both module unit suites, release
   lint and unsigned release APK assembly, with verification/QA/universal/arm64 properties and native tasks excluded
   only because the reviewed prebuilt archive was installed. Run the complete tooling suite. Preserve thread-dump
   watchdog, explicit suite counts, failures/errors/skips, class inventory and all raw reports. Require non-empty
   app, MediaLib, FileCore and tooling results, zero failures/errors/skips, and zero lint errors.
5. Require exactly one APK. Independently inspect application ID, version name/code and minimum API against the
   checked-in manifest. Run native APK verification against the packaging receipt: actual entries, per-library
   hashes, ABI, ELF/API/alignment/security requirements and all required capabilities. A log saying "passed" is
   insufficient input to a verifier; store its structured result and payload hashes.
6. Emit unsigned-conformance.json with the exact source and manifest hashes, unsigned APK hash/bytes, verified APK
   identity, full suite counts/classes, lint/tooling results, preparation receipt, archive/recipe/native receipt and
   APK-native verification result. Upload the exact APK and receipt only on success. Upload raw validation evidence
   even on failure. Missing evidence fails conformance; it never defaults to zero failures.

## Protected QA signing

1. A separate manual dispatch accepts only the numeric conformance run ID. Restrict to the authorised source branch,
   established `supernova-foundation-signing` environment, read-only contents/actions permissions and non-cancelling
   signing concurrency. The owner still approves each signing. No automatic signing after conformance.
2. Fetch GitHub's run metadata separately. Require success, the exact generic conformance workflow path, authorised
   repository/branch and a head SHA equal to the receipt source. Require the selected candidate manifest's hash to
   match the receipt. Prove that source is an ancestor of the dispatch checkout and that subsequent differences are
   `docs/` only, as today; then detach to the tested SHA. Code/assets/build/manifest changes require a fresh run.
3. Rehash the unsigned APK; independently validate all non-empty suites, lint/tooling, required tests, native
   capabilities, identities and manifest/receipt agreement before accessing a signing key. Parameterise the existing
   signing validators with the checked-in candidate; preserve all their negative checks. Passing `0.157` as an
   argument to the hard-coded 0.156 helpers is not a migration and must fail rather than bypass their checks.
4. Keep the established PKCS12 identity, certificate fingerprint and protected secret scope. Disable shell tracing,
   refuse secret debug logging, use temporary private files, validate the key/certificate before signing, retain
   zipalign's 16-KiB check and apksigner verification, and always destroy private material and secret-scope logs.
5. Independently verify signed application/version/minimum API, expected certificate and APK signature, exact
   unsigned payload parity after normalising signing metadata, packaged native hashes/capabilities and source
   receipt. Publish only the verified APK and a public receipt containing no key material. Delivery is a later step
   after key destruction; optional Drive credentials remain separate and missing delivery cannot invent success.

## Gate equivalence and rejection tests for integration

| Existing 0.156 gate | Generic replacement | Required negative control |
| --- | --- | --- |
| Fixed version/package assertions | Checked-in candidate plus APK inspection | Wrong name, code, package or min API |
| Fixed engine source/hash | Reviewed candidate and native receipt | Old archive, missing capability, changed library |
| Three real unit suites, lint, tooling | Same tasks and raw reports, non-empty evidence | Empty/missing suite; failure/error/skip; lint error |
| Run path/branch/success/head SHA | GitHub metadata plus candidate/source equality | Foreign workflow, branch, repository, failed run |
| Documentation-only continuation | Same ancestor and path comparison | Source divergence or a changed asset/Gradle file |
| Tested unsigned bytes | Receipt SHA plus signature-independent parity | One changed ZIP payload entry |
| Existing upgrade certificate | Same fingerprint and independent verifier | Correct package signed with another certificate |
| Protected environment and cleanup | Same environment/manual approval/final cleanup | Failure at decode, signing and independent verification |

Before activating the draft, run each negative control in the helper test suite and a full unsigned conformance on
the exact integration source. The current local javac/native object probes are not substitutes for that run. No
pipeline, signing, package or native archive is represented as released by this document.
