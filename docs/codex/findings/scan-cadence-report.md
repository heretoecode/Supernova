# Scan cadence and cost — 0.157

Static trigger audit, 6 November 2026. A source timer is distinguished from a one-second message used to debounce
real notifications. No periodic 15-minute local scan timer was found in the reviewed admission paths. Source alone
cannot identify the Android event that caused the owner's historical cycle; capture trigger/batch IDs on the Shield.

| Trigger / owner | Admission and timing | Cost / terminal ownership | Confidence |
| --- | --- | --- | --- |
| VideoStoreImportService startup | Initial local import and observer registration | MediaStore inventory, native reconciliation and metadata for required rows; finally clears busy/deleting/count state | High, source and injected-failure tests |
| MediaStore file/video observers | Delayed one-second handler messages coalesce changes | May chain during provider update bursts; not a periodic alarm | High for mechanism; unknown historical producer |
| Android scanner-finished, mount/permissions/foreground | Existing service messages and mounted-storage checks | Full/incremental inventory depending on message/state | High for trigger paths, device rate unmeasured |
| NetworkAutoRefresh scheduled | Off, 1h, 2h, 6h, daily; enabled migration never to Off | Registered source traversal/NFO/metadata probes, batch/source deadlines and last-ten history | High, cadence/selection tests |
| App-open network admission | Existing single boolean; only after sufficiently old successful completion (one hour), not request timestamp | Reuses network batch; Put.io follows its integration admission | High, policy tests |
| Explicit Scan one local source | Exact registered root, one owned job, bounded inventory, progress watchdog | Only new/changed OS metadata requests; native reconciliation only in chosen root; late system replies fenced | High, scoped/import tests; Android scanner latency unmeasured |
| Explicit Put.io source | Exact API-owned signed-account scope, existing enabled/busy gates | Selected pages only; one queued-to-terminal history owner, existing client Stop | High, scheduler/selection tests |

Migration: old 12 hours → 6 hours; 15/30 minutes → hourly; ties choose the slower positive interval. The app-open
boolean is unchanged. UI must consume the new choices; no protected screen was changed. Recent history retains
last ten source runs, actual completion/duration/counts and safe reasons, rather than a second scheduling journal.

Ranked proof work: (1) collect media-provider observer/scanner/mount/foreground trigger plus batch ID around a
15-minute recurrence; (2) compare checked/changed counts to distinguish repeated enumeration from actual metadata
work; (3) record real start/progress/terminal times and busy-finally cleanup; (4) compare explicit source work to a
full import; (5) trace source failures and callback deadlines while playback continues. Do not disable notifications
or invent a new timer to cure an unobserved cause. An active native worker correctly retains the aggregate scan
signal; Android's independent system scanner cannot be killed by the app's Stop.
