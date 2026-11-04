# Preserve the pinned 0.146 native engine on a branch

Owner-approved branch archive, 2026-11-04. Codex has not dispatched a workflow,
created a Release, or pushed an archive branch. Claude's four archive runs verified
all ABIs, but Release creation returned HTTP 403 “Resource not accessible by
integration”. The new job uses `native-archive-0146` as an immutable orphan branch.

| ID | File:line | What goes wrong | Evidence | Suggested fix | Severity | Confidence |
|---|---|---|---|---|---|---|
| NA-01 | `.github/workflows/supernova-0154-conformance.yml:32` | Builds depend on expiring Actions storage. | Run 40843685431 artifacts expire 2027-01-27; Release creation refused in Claude runs 41847738060, 41847968898, 41849511956. | Preserve original verified ZIPs on the dedicated branch; use the branch read path. | Build availability | High |

## Normal path (Claude)

Run `supernova-0156-native-archive.yml` with no inputs from the reviewed recipe
branch. Manual dispatch remains available; the sole push trigger is this workflow's
own path on `claude/supernova-github-access-yfhe89`. Pushing the Codex branch does
not start it. No other workflow or protected build wiring was changed.

The script preserves Claude's artifact `Accept: application/json` correction and
failing `gh` command/reason output. It verifies the pinned run/repository/commit,
all four artifact identities and ZIP hashes, the inner tarball hashes, local lock
and recipe hashes, and each receipt's source, ABI, recipes and installed-file
inventory/hashes. It saves exactly seven root files:

- Four original `supernova-0146-native-<abi>.zip` files (containing tarball/logs).
- `native-supernova146.lock.json` and `NATIVE_CI_ARTIFACT_VERIFICATION.json`.
- `archive-supernova156-manifest.json`, schema 2, with sizes and SHA-256 of the six
  payload/receipt files. The manifest's own bytes are compared after fetch and are
  covered by the Git tree/commit; a manifest cannot contain its own SHA-256.

Every file must be strictly below 100,000,000 bytes. The branch has one commit,
no parents, no other paths, links or executable files. A normal non-force push
creates it only when absent. An existing branch must match every byte/hash and the
entire file inventory; it is never rewritten, extended or repaired in place.
A fresh fetch checks the complete tree, the manifest and all hashes after creation
or an identical rerun. Any unexpected branch/history/content fails closed.

## Read side and build migration

`build-supernova156-native-stack.py --third-party-source branch` is the new default;
`supernova-0156-native-stack.yml` exposes `branch`, `actions` (default
`branch`). The reader fetches the orphan branch afresh, validates its complete
inventory and every manifest hash, compares its lock/review byte-for-byte with the
local reviewed files, and anchors all four ZIPs to the **local** review hashes.
It then performs the existing selected-ABI tarball/receipt/library verification.
A mismatch or missing branch stops the build; no automatic fallback exists.
`actions` is an explicit verified fallback while the old run is available.

Claude then builds the new native engine, reviews its actual receipts, and wires
the app build as specified in `HANDOVER.md`. The archived bytes remain the old
0.146 stack, not a relabelled new wrapper build. No placeholder output hash is usable.

## Plan B — owner runs once from a computer, only if job-token push is refused

Use a trusted computer with Git, Python 3.12+ and `gh` authenticated as the owner
with repository contents access. Before 27 Jan 2027, check out the reviewed Codex
recipe commit and run the following from the repository root. These commands are
instructions only; Codex has not run them against GitHub.

```sh
# This downloads the run's extracted artifacts for a separately retained backup.
gh run download 40843685431 --repo heretoecode/Supernova --dir ../native-0146-extracted

# gh run download extracts ZIPs, so also retain the ORIGINAL ZIP bytes. Re-zipping
# would change the reviewed digest. The script checks metadata before downloading.
mkdir ../native-0146-original-zips
python3 - <<'PY'
import importlib.util
import json
from pathlib import Path
spec = importlib.util.spec_from_file_location('archive', '.github/build/native-archive-supernova156.py')
a = importlib.util.module_from_spec(spec)
spec.loader.exec_module(a)
review = json.loads((a.ROOT / 'docs/0.146/NATIVE_CI_ARTIFACT_VERIFICATION.json').read_text())
rows = a.verification_rows(review, a.ROOT / '.github/build/native-supernova146.lock.json')
for row, item in a.select_artifacts(a.api(f'actions/runs/{a.RUN}/artifacts?per_page=100'), rows):
    a.download(f"actions/artifacts/{item['id']}/zip",
               Path('../native-0146-original-zips') / (item['name'] + '.zip'))
PY

# Verify all four ZIPs/tarballs/libraries against the reviewed local lock; build
# the exact seven-file manifest without publishing anything.
python3 .github/build/native-archive-supernova156.py \
  --downloads ../native-0146-original-zips --destination ../native-0146-verified

# This re-verifies those inputs, creates the ONE orphan commit with no other files,
# pushes native-archive-0146 using the owner's gh credential, fetches it anew and
# checks every hash. Existing identical branch => verify only; different => fail.
# Use a new destination, because verification never reuses a nonempty directory.
python3 .github/build/native-archive-supernova156.py \
  --downloads ../native-0146-original-zips --destination ../native-0146-publish --publish
```

The script's `--publish` uses `git hash-object`, `mktree`, and `commit-tree` with
**no parent**, then `git push <commit>:refs/heads/native-archive-0146` without force;
it never checks out the archive over the source tree. It authenticates through
`gh auth git-credential`, so no token goes into a URL, tracked file or log. After
successful verification Claude selects `third_party_source=branch` for the native
workflow; the build no longer needs the expiring Actions artifacts or Release APIs.
If an existing branch differs, stop and investigate; do not delete or force-push it.

Offline validation: `test-native-archive-supernova156.py` and
`test-supernova156-native-stack.py`. Tests use synthetic archives and local bare
Git remotes; no workflow dispatch, GitHub archive push or Release write occurs.
