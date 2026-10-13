# Export incident: preserved timeline and evidence plan

**Undiagnosed.** No device logs, OS exit records, personal backup or diagnostic ZIP were supplied in this investigation. Source behaviour establishes feasible paths, not the cause of an apparent exit.

## Raw owner observations

| Observation | Evidence class / confidence | What it does and does not establish |
|---|---|---|
| Settings Backup has history/position switches and export/import “plus credentials plus shortcuts” labels; export warns, picker opens | Owner-observed; labels corroborated in signed source | UI is consistent with source labels, but exact installed APK/locale not independently verified |
| Apparent app crash around **12:41 Dublin local time**, after location selection | Owner-observed; date/device timezone/clock unknown | Could be Activity exit, process termination, ANR, relaunch or another lifecycle event; not a proved crash |
| Initially **0 KB** `.zip.in-progress`-style “Nova backup” destination file | Owner-observed; static creation order corroborates a placeholder possibility | Provider-reported length can lag; does not prove generation failure or root cause |
| App relaunched | Owner-observed | Need PID/session/OS exit evidence to distinguish Activity relaunch from process restart |
| Success notification reportedly timestamped **12:33** | Owner-observed, inconsistent with 12:41 incident sequence | Preserve 12:33 separately; do not normalize it to 12:43, reorder it as fact, or call it completion timestamp |
| Eventually an openable **16.87 MB `.zip`** | Owner-observed | ZIP browsing is not complete CRC/schema/privacy/restore validation or proof of which attempt produced it |
| Artwork folders called “Scrapper Backdrops” (~3 MB), “Scrapper Posters” (25 images/~2.3 MB); ~40 images/~5.5 MB | Owner-observed; raw presentation spelling retained | Source entry prefixes are `scraper_backdrops/` / `scraper_posters/`; presentation names need private listing reconciliation, no personal names/images here |

Authority: audit brief and latest consolidated handover at `c7bbc2dd6d9f0b75ad3cb726d0c632914b43277b` linked in README. Incident date was presumed in that record, not proven. Session date moved from October 12 to October 13 UTC; that does not establish incident date. If incident is during Dublin summer time, 12:41 and 12:33 correspond to 11:41 and 11:33 UTC; apply that conversion **only after confirming date/timezone/clock**.

## Source facts relevant to the timestamps

Export has ongoing notification ID 7 built in service `onCreate`, reused for status, no explicit `setWhen` on stage updates, and success is toast + cancel/remove notification. No persistent export-success notification is constructed. An older builder time or prior notification could be a timestamp interpretation hypothesis; **neither explains the observed eight-minute inconsistency without evidence**. Other service/provider notifications are possible. [MediaLibraryBackupService.java:80](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/MediaLibraryBackupService.java#L80), [MediaLibraryBackupService.java:160](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/MediaLibraryBackupService.java#L160).

Export does not invoke `restartApplication`; import does. The service thread may continue after Settings Activity disappears. A final file could reflect that background completion or a different earlier/later export. Both remain hypotheses until attempt/process/provider identity is correlated. “ZIP opens” must not be reported as a successful restore.

## Diagnostic plan without publishing private material

Respect the existing walkthrough plan: obtain **one privacy-reviewed diagnostic ZIP near its end**, covering this incident and the indexed playback/startup issues. No repeated diagnostic requests or new device action is needed to finish these documentation deliverables. Do not export raw backup contents, usernames, tokens, hostnames, file paths, titles, watch records or artwork into GitHub.

On future authorised private review:

1. Confirm installed source/version/signing receipt, incident calendar date, device timezone/clock accuracy, owner's observation wording and notification app/channel/type. Keep private raw evidence local; publish only safe provenance/outcome.
2. Correlate `backup_export_requested`, `backup_export_verified`, `backup_export_failed`, `backup_restore_requested/completed/failed` with UTC and elapsed time, process/session/PID/sequence and surrounding Activity lifecycle. Today these backup events carry no per-backup attempt ID or stage metrics. A “requested” event precedes worker guard; it alone does not prove work began. [MediaLibraryBackupService.java:128](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/MediaLibraryBackupService.java#L128), [Diagnostics.java:102](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java#L102).
3. Review previous-unclean and Android historical exit evidence where available, bounded exception/ANR frames, memory/thread-stall samples and dropped/retained event coverage. Missing clean marker is not crash proof; API30+ historical exit support is absent on older OS. Reuse maintenance report 05 producer/sanitizer findings, including privacy gaps; do not publish raw diagnostic archive merely because app calls it safe.
4. Match destination creation/write/verification/rename to a single attempt using private provider metadata, final URI/name/modified-time/size if available, plus manifest createdUtc/applicationVersion and safe aggregate CRC/hash validation. Manifest creation time is export-generation time, not completion; picker filename minute is another separate time; provider modified time is not necessarily device time. No raw URIs or archive digest need publication.
5. Investigate 12:33 independently: whether notification's displayed time is builder time, earlier attempt, provider notification, delivery time or misread observation. Only signed APK/exact platform notification evidence can adjudicate.
6. Privately validate every ZIP entry CRC/size/allowed path/manifest, actual DB schema/user_version, portable keys and selected history filters, then use a **synthetic equivalent** for restore testing. Never restore the owner's archive onto their current app to “see if it works” under this read-only authorisation.

## Hypotheses and evidence that would distinguish them

| Hypothesis (not diagnosis) | Required evidence / disproof |
|---|---|
| Settings Activity exited while service finished | Same process/session plus verified export event after lifecycle exit; no OS termination |
| Process died/ANR/OOM during export | Matching exit reason/time or bounded exception/ANR and missing completion, not simply absent clean marker |
| Provider slow/size metadata stale/cloud-backed | Stage timing, provider characteristics and readback/rename history; zero displayed bytes alone insufficient |
| Final ZIP/notification belongs to another attempt | Manifest/provider metadata vs events and request counts; current no-attempt-ID gap may prevent certain attribution |
| Old notification timestamp reused | Exact notification `when`, builder/service creation and actual delivery times for same notification instance |
| Low storage/busy WAL/grant loss/Error/provider rename failure | Specific error/exit/stage data; static reachable failure path is not causal proof |

No hypothesis is ranked as the cause here. Future diagnostic absence must be reported as insufficient evidence, not filled with a plausible story. Proposed telemetry: numeric/enum attempt+stage begin/end, monotonic/UTC clock pairs, byte totals, outcome/error class, cleanup/rename result and verification state; no titles/paths/exception messages. Reuse the recorder and approved diagnostics work; no independent logging subsystem.
