# Preserve the pinned 0.146 native engine (NEXT item 1)

Draft only, 2026-11-04. The workflow has **not** been dispatched and no Release
has been created or changed. Existing build/signing workflows remain untouched.

| ID | File:line | What goes wrong | Evidence | Suggested fix | Severity | Confidence |
|---|---|---|---|---|---|---|
| NA-01 | `.github/workflows/supernova-0154-conformance.yml:32` | Builds depend on expiring Actions storage. | Read-only API check of run 40843685431 on 2026-11-04: four unexpired ABI artifacts, each matching the existing verification ZIP digest, expire 2027-01-27T08:39:15Z. | Review and manually dispatch the new archival workflow before expiry, then migrate native acquisition to the verified Release assets. | Build availability | High |

The new manual-dispatch-only `supernova-0156-native-archive.yml` calls
`native-archive-supernova156.py`. It checks the run/repository/commit, all four
artifact identities and ZIP SHA-256 values, the inner tarball SHA-256 values,
the existing lock digest and recipe hashes, and every receipt's source, recipes,
ABI and installed-file inventory/hash. The authoritative inputs remain
`.github/build/native-supernova146.lock.json` and
`docs/0.146/NATIVE_CI_ARTIFACT_VERIFICATION.json`; no replacement hashes were invented.

After all four pass, it preserves the original ZIPs (including build logs), the
four `Supernova_0.146_native_<abi>.tar.gz` files, the lock, the verification file
and a measured manifest in Release `native-archive-0146`. The tag must identify
the reviewed source commit `e2ff14b954459dba40328baa5e00fe5b588dcb34`.
A new Release starts as a draft, is downloaded again and checked, then published
with `make_latest=false`. A partial failure leaves the draft resumable. Existing
assets are verified before adding missing ones; differing bytes cause failure,
never replacement/deletion. Re-running verifies assets without rebuilding them.
Tarballs are inspected as streams, never extracted to or executed on the host.

## Build migration for Claude/owner

1. Review this draft and manually dispatch it from a branch containing these new
   files. Confirm the final Release is published and all 11 expected assets exist.
2. Download the four tarballs from the Release by immutable asset identity or tag
   plus exact filename. Compare each SHA-256 directly with the **existing local**
   verification file before handing it to `package-supernova146-native.py`.
   Do not trust a checksum downloaded alongside an unverified binary as its sole anchor.
3. In a later authorized change, replace only the existing workflows' native
   `download-artifact` acquisition step with those downloads. Keep the packager's
   receipt checks, recipes, source/NDK pins and wrapper builds unchanged. `avos`,
   `libnativehelper`, and FileCore wrappers still rebuild against the application.
   Do not relabel the archived stack as a 0.156 engine or change historical run receipts.
4. Verify all four ABIs through packaging/conformance and APK-native checks. On
   the Shield run the existing playback checks. Confirm the pipeline works with
   Actions-artifact access deliberately unavailable, using only Release downloads.
5. Keep the original run/lock/license/source provenance and Release asset IDs in
   the acquisition receipt. Protect the tag/assets from accidental replacement;
   hashes, rather than the mutable Release title/tag alone, are the trust anchor.

Offline validation: `python3 .github/build/test-native-archive-supernova156.py`
tests corruption, duplicate/unsafe archive members, receipt/file tampering,
missing/expired/wrong-run artifacts, existing Release mismatches and draft tag
targets. These synthetic tests do not download the real artifacts or publish
anything. Real archival and build verification remain owner/Claude actions.
