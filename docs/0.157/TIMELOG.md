# 0.157 time log (owner asked, 15 Nov 2026: measure how long it really takes)

All times local. Coding time only; waiting for the owner or for a blocked build is noted separately.

| When | What |
| --- | --- |
| 15 Nov 01:43 | Coding starts (first commit: diagnostics read, build order). |
| 15 Nov 01:43–01:53 | Codex's 0.157 work merged; seek-preview cause fixed; hidden switch shown. |
| 15 Nov 01:53–02:00 | Blocked: GitHub Actions disabled on the fork (owner re-enabled it at 02:00). |
| 15 Nov 02:00 | First build: failed in 25 s at set-up (the build only accepts the pinned MediaLib plus registered patches). |
| 15 Nov 02:07 | Owner's usage ran out and was reset. Clock noted by the owner. |
| 15 Nov 02:07–02:35 | Recent scans store, no rebuild on unchanged snapshots, NOVA 6.6.5+ carried into MediaLib and FileCore as registered patches; software-decoding notice; Skip button fixes. Second build started 02:13. |
| 15 Nov 02:35–07:37 | **Not working:** the session's worker stopped and restarted several times (about 5 hours lost; no coding). The second build had failed at 02:28 (test compile errors in merged work). |
| 15 Nov 07:37– | Resumed: failure report extended to show compile errors; fixes; Recently Added; the minute-by-minute scan cause (failed scans never counted as done) with the 15-minute rule; third build started 07:39. Scan timer skips quietly in the background; memory by kind and page in diagnostics. |
| 15 Nov 07:42–12:52 | **Not working:** the worker stopped and restarted again (about 5 hours lost). The third build had failed at 07:51. |
| 15 Nov 12:52– | Resumed. |
