# Library Health counting reconciliation — analysis only, 2026-11-04

Item 3 of BRIEF_0155_MORE. Source: this run's app branch through `7a26c7488`; the relevant app files are unchanged from `0efd8e448`. MediaLib references below mean its pinned source plus existing ordered patches, before the new scan draft. No LibraryHealth file or other implementation was edited. This specifies data semantics and proving tests, not screen wording, layout, navigation or a UI implementation.

## Evidence

Paths abbreviated `app:` are under `src/main/java/com/archos/mediacenter/video/`; `M:` are under MediaLib `src/com/archos/`.

| ID | file:line | What goes wrong | Evidence | Suggested fix | Severity | Confidence |
| --- | --- | --- | --- | --- | --- | --- |
| HC-01 | app:`leanback/LibraryHealth.java:33`; app:`leanback/LibraryHealthActivity.java:134` | Summary and categories count different populations. | Report counts every `media:` preference value. Categories join current `snapshot.technical` Videos, which exclude policy-filtered rows (`PreviewLibraryLoader.java:586`) and can omit malformed rows (`:618`). A stored marker need not produce a category member. This refines the existing stuck-scan report's health observation, not a separate newly discovered incident. | Build one immutable inventory; derive each summary count and its category from the same issue set. | user-visible | high |
| HC-02 | M:`mediaprovider/video/NetworkScannerServiceVideo.java:1174` (pre-draft); M:`mediaprovider/video/VideoProvider.java:625` | Markers can survive deletion of their indexed rows. | Scanner onStop writes `media:<public-id>` before complete DAV deletion. Raw provider delete removes database rows; no health-store cleanup occurs. `SupernovaLibraryPolicy.sourceAvailable:76` clears only source markers. | Cleanup through authoritative provider deletion plus a durable, replayable reconciliation path. | user-visible | high |
| HC-03 | app:`leanback/LibraryHealth.java:54`; app:`leanback/PortableReviewNotes.java:139` | Any preference entry can trigger attention even with no actionable issue. | `needsAttention` returns true for a nonempty store, including a false incorrect flag or an empty pending-review payload. Its unmatched input also differs from category iteration over technical videos. | Compute attention from resolved issue-set union; keep pending restore data and malformed-cache diagnostics distinct. | user-visible | high |
| HC-04 | app:`leanback/LibraryHealthPanel.java:60`; app:`leanback/LibraryHealthActivity.java:148` | One source can count twice in the panel and once in the Activity. | Panel enumerates both `offline:<scope>` and `problem:<scope>` as separate rows; Activity uses a TreeSet of suffixes. Summary only counts offline keys. | Deduplicate source entities by canonical scope with a set of reasons; each consumer uses the same inventory. | user-visible | high |

No assertion about the owner's two actual stored markers can be made without their keys and corresponding database identities. Do not infer that two files are lost from a raw marker count.

## One authoritative inventory

Read the complete provider identity population, the complete health preference map, import/scrape flags, and the same library-policy version used by the library snapshot. Join by public video ID **and canonical location**, using one generation. Retry if provider/policy/health generation changes while assembling it. A failed/null provider query is an unavailable inventory, never an empty database and never permission to prune markers. Read summaries/categories/attention from this single result; do not re-read preferences independently for each category.

The full identity map is distinct from the policy-visible, successfully mapped technical-video set `V`. It includes hidden/excluded records for reconciliation, without counting them as actionable visible entries. It also lets a malformed model record be distinguished from an actually deleted row. Do not depend on paging limits, currently selected category, search/filter results, or source connectivity to decide whether an ID exists.

Use public IDs, not title IDs, scraper IDs or raw scanner IDs. For scanned rows the trigger sets public `_id = files_scanned._id + SCANNED_ID_OFFSET` (`M:VideoOpenHelper.java:175–212`); imported IDs use their provider identity. Query/retain the public identity before deletion instead of applying the scanner offset indiscriminately to all tables. Canonical path matching must preserve origin and segment boundaries using existing policy canonicalization; `/TV` must not match `/TV2`.

Classify every stored marker into exactly one disposition: active visible; valid but excluded/hidden; existing but unmapped; verified deleted; malformed; unresolved because inventory failed. Record raw/disposition counts for diagnostics. Only active visible media issues enter the issue sets below. Excluded/hidden markers remain valid until actual recovery/deletion; they must not be silently erased to make the totals agree. Unmapped or unresolved records require a diagnostic/retry, not a claim that the library is healthy or a missing marker is orphaned.

## Exact set and count rules

For `v` in `V`, derive the existing effective availability precedence from the joined inventory: an applicable offline source makes it offline; otherwise a valid `media:<id>` marker matching its current canonical path makes it missing; otherwise it is available. A missing marker can be retained while a source is offline, but the same video is counted once as offline in the effective state. A mismatched path/ID marker is unresolved stale identity evidence, not proof the new row is missing.

| Inventory set | Membership | Required consumers / count invariant |
| --- | --- | --- |
| `U` | Visible Video is not Movie/Episode, and import/scrape suppression is inactive | Unmatched summary/category use `U`; count is distinct public IDs. Do not substitute an unrelated loader's unmatched list. |
| `I` | Visible Video has Boolean true `incorrect:<id>` associated with its current identity | Incorrect-match count and category both use `I`; false flags are not issues. |
| `M` | Visible Movie/Episode lacks poster or nonempty normalized plot, and import/scrape suppression is inactive | Metadata category uses one shared poster/plot predicate (Activity and panel must agree); count distinct IDs, not one per absent field. |
| `O` | Visible Video's effective state is offline | Effective offline-media count; do not multiply by the number of covering offline scopes. |
| `D` | Visible Video's effective state is missing | Confirmed-missing count is `size(D)`, not raw `media:` key count. |
| `A` | `O union D` | Unavailable category count equals `size(A)`; `O` and `D` are disjoint under precedence. |
| `S` | Canonical active in-policy source/problem scopes; each carries its offline/problem reason set | Source category count is distinct scope entities. Offline-source summary is the subset with offline reason, not offline-media count. Same scope with both reasons counts once; parent and child scopes remain distinct diagnostics, not guessed aliases. |

A scope is active if it is a configured allowed root, a represented retained source, or a failure subtree within one of those roots. Keep excluded/retired source-marker dispositions outside `S` until policy includes them again or an explicit source-removal operation retires them. Do not strip descendant partial-failure reasons merely because the parent responds: clear only the scopes actually proven recovered by traversal. Existing broader recovery behavior needs an owner-reviewed change separately.

Categories deliberately overlap: one video can be unmatched and unavailable, or incorrectly matched and missing artwork. Do not sum category sizes to obtain total problematic videos. The video total is `size(U union I union M union A)`; source entities are a different unit. Overall attention is this union nonempty or `S` nonempty, with inventory-read failure/unmapped diagnostics tracked as unresolved, not a healthy result. Import suppression applies only to `U` and `M`; it does not suppress offline, confirmed missing or explicit incorrect-match issues.

## Marker lifecycle and deletion

- On positively verified rediscovery of the **same** identity, remove `media:<id>`; do not clear unrelated source/incorrect-match markers. A failed/partial/cancelled traversal cannot establish that an unseen file was deleted or recovered.
- On a confirmed committed row deletion, retire its ID-addressed `media:` marker and `incorrect:` flag. Do not retain a flag keyed to a reusable old ID. If a durable user review note must survive for restore, preserve its physical/expected-match identity in the existing portable pending-note representation, separately from active ID-keyed health. Do not delete or reinterpret `PortableReviewNotes.PENDING` as automatic health cache.
- Centralize deletion capture in the provider layer so scanner reconciliation, explicit file deletion, unindex/unscan, imported hidden/expired-storage purge, provider applyBatch and trigger cascades participate. Capturing requested IDs in the scanner alone misses other writers. In one DB transaction, capture affected public IDs+paths, apply the exact guarded deletion, and enqueue cleanup records for **rows actually removed**. Zero rows/guard mismatch must not clear live markers.
- SQLite and SharedPreferences are not one atomic transaction. Use a small DB cleanup outbox committed with the deletion. After commit, drain it on a serialized worker, confirm the ID has not been rebound to another identity, clear exact marker keys idempotently, and acknowledge the outbox only after preferences commit succeeds. Replay on process restart and before publishing a health inventory. Rollback must also roll back the outbox. Never report preferences `apply()` as durable completion of this cross-store operation.
- Handle ID remap in the same authoritative identity operation: rebind marker state only when source/destination path and expected identity prove continuity; remove the old key after the new association is durable. Do not let an old deletion tombstone erase a new row's marker. Imported ID remap (`M:VideoStoreImportImpl.java:652`) and restore's physical-reference resolution (`app:PortableReviewNotes.java:129`) are explicit integration points.
- Reconcile old orphan markers on upgrade/resume using a complete successful identity query and stable generation. Verified-deleted cache markers can be retired via the same replay mechanism. Exclusion, mapper rejection, offline storage and provider failure are not proof of deletion. Report malformed keys/types separately and migrate/quarantine them without altering library rows or portable review notes.

This does not authorize changing row-retention policy, deleting media, changing source definitions or editing any LibraryHealth implementation in this run.

## Tests that would prove the correction (not implemented/run)

| Fixture / action | Required assertions |
| --- | --- |
| Two raw media markers: one visible retained ID, one verified-deleted ID | Raw diagnostics show two before reconciliation; `D`/unavailable contain the visible ID only; committed cleanup removes only the deleted marker; summary equals the category membership. |
| Excluded existing ID and malformed/unmapped existing record, each with a missing marker | Neither is treated as deleted; markers preserved; visible issue counts exclude them; diagnostic dispositions remain explicit. Re-inclusion restores the same issue identity. |
| Same video with media marker and overlapping offline source scopes | Exactly one unavailable entry; effective offline state wins; `D` excludes it while offline; recovery exposes the retained missing state unless that file was positively rediscovered. |
| Same canonical source has offline and problem keys; two different child failures | One entity for the identical scope with both reasons; distinct child scopes remain distinct; both panel and Activity consume identical source counts. Segment-boundary and scheme/authority aliases tested. |
| Unmatched+unavailable video, and matched+incorrect+missing-poster+empty-plot video | Each appears once in each applicable category; union total counts each video once. Whitespace-only plots and null posters use the same predicate in all consumers. |
| Import/scrape active; false incorrect flag; empty/nonempty portable pending payload | Only `U`/`M` suppressed; explicit availability issues remain. False flags/pending payload alone do not make an active media issue; pending portable data is preserved. |
| Scanned public ID, imported public ID, equal raw IDs in the two tables | Deleting one physical row clears only its public-ID markers; scanner offset applied exactly once where applicable; the other table's markers survive. |
| Scanner complete deletion, explicit delete, unindex, purge and provider applyBatch/trigger cascade | Every actual deletion produces cleanup for exact removed public identities. No requirement for the scanner to own the call. Guarded zero-row delete clears nothing. |
| DB delete rollback; crash after commit before preferences; preferences commit failure; restart/replay twice | Rollback retains markers; committed outbox survives crash; failure remains replayable; replay is idempotent and does not resurrect markers or delete media. |
| Delete followed by ID reuse/remap/portable restore | Old tombstone never clears a new identity's flag. Proven continuous remap preserves intended state; ambiguous mapping stays pending and cannot mark an unrelated video missing. |
| Null/failed/truncated provider read; concurrent policy/DB/health change | No marker pruning or empty/healthy success. Retry stable generation; repeated consumers receive one consistent inventory. |
| More entries than any UI page/cache size | Counts and membership remain complete and equal; no pagination-based deletion inference. |

All implementation and screen integration are deferred to owner/Claude under the hands-off and no-UI rules. This document supplies the reconciliation contract and validation cases only.
