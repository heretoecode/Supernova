# Network scan ownership draft — item 2, 2026-11-04

This extends `stuck-network-scan.md`; it is a dependency draft, not an installed fix or a diagnosis of the Shield. No app Java, resources, manifests, visible text, navigation or hands-off files were edited in this item.

## Reproduction baseline and application order

Use MediaLib `5758074049bc153ac4967b4ecd21886469270076` and FileCoreLibrary `c4b760c55102c72d45f68b0d16999fc1f4f013a7`. Apply each module's existing operations from `.github/build/supernova146-dependency-patches.json` in their recorded order, including the MediaLib Foundation authority replacements at the `foundation_identity` position. Then append:

1. FileCoreLibrary: `.github/build/filecore-supernova155-scan.patch`.
2. MediaLib: `.github/build/medialib-supernova155-scan.patch`.

The companion FileCore patch is necessary: MediaLib alone cannot cancel the raw Sardine calls or redirect probes behind `WebdavRawLister`. Both files are new and satisfy rule 3's filename exception. Neither existing preparation scripts nor the locked patch inventory were changed; Claude must explicitly apply both drafts in an isolated build workspace. Do not update existing patch hashes or silently add these to production builds.

Both patches passed `git apply --check` on separate, clean worktrees at the fully prepared dependency baselines. All ten changed/new dependency Java files passed a Java syntax parse. This does not establish Android compilation.

## Ownership and compensation

`NetworkScanLedger` registers all roots before dispatch, assigns an unpredictable member ticket in addition to batch/URI, and retains exactly one terminal transition per ticket. Intent redelivery cannot claim a ticket twice. A bare batch ID cannot consume a ledger-owned member. Old tickets cannot start a new worker or decrement a newer batch. Existing standalone and direct legacy batch test contracts remain separate.

| Handoff / termination | Draft behavior | Test evidence added (not run unless stated below) |
| --- | --- | --- |
| Scheduler posting false/throw, broadcast send throw, accepted callback/broadcast subsequently lost | Explicit rejection, plus independent scheduled acknowledgement expiry | `NetworkScanLedgerTest.lostBroadcastExpiresAndLateDeliveryCannotStartAServiceOrAffectRetry`; timer-rejection test; source inspection of scheduler guards. The actual scheduler post/send failure injection still needs an Android integration run. |
| Receiver background gate / same-URI throttle | Reject exact ticket before returning; repeated rejection is inert | `stoppedBetweenQueuedAndDeliveryReleasesBothAndAllowsRetry`, `throttleRejectsNewMemberButCannotFinishOneTwice` |
| Unsupported URI, service start throw/null, setup gate closed | Reject exact ticket; no attempt to bypass Android background restrictions | `serviceStartThrowAndNullAndUnsupportedUriTerminate`; setup gate remains for Claude to exercise |
| Handler message rejection / executor rejection | Remove request-map entry, terminate ticket, request idle service stop | `rejectedMessageReleasesMemberAndRequestMapEntry`, `executorRejectionReleasesMemberAndRequestMapEntry` |
| Different counted member for an already queued URI / repeated delivery of same member | Different member ends not_started; repeated ticket cannot claim again; counted duplicate does not create unowned follow-up work | `duplicateUriHasDistinctMembersAndRejectsOnlyNewSlot`, `duplicateIntentCannotClaimMemberTwice`, `bareBatchIdCannotConsumeLedgerMember` |
| Service cleanup / shutdownNow drops queued tasks | Closing fence precedes shutdown; all owned never-started tickets end; running ticket is cancelled and completes in its worker finally | `ownerTeardownReleasesQueuedButKeepsCancelledWorkerUntilUnwind`; full Android service-destruction wiring still requires Claude's runtime verification |
| Never acknowledged start | 15 seconds after the planned broadcast dispatch; no app/main-looper dependence | Lost-delivery and separate-deadlines tests |
| Accepted but executor never starts | Five-minute source deadline includes queue time | `acknowledgementAndSourceDeadlinesHaveSeparateOwnership` |
| Running source reaches deadline / teardown | Cancel only that source's registered DAV/redirect calls and interrupt its owner; **retain its count until it unwinds** | `runningDeadlineCancelsWithoutFalseSuccessOrDoubleCompletion`, `cancelledWorkerWaitingForProviderGateCannotPerformLateScan`, `NetworkScanTransportTest` |
| Normal/failed/unchecked scan exit | Existing scanner finally publishes terminal result through the ledger; outer task finally compensates failures outside the scan body | Existing unchecked-scan regression retained; new ledger tests assert one batch terminal event and no success handoff on cancellation |

Queued telemetry is emitted before any broadcast can run. Terminal worker events retain the real checked/added/updated counters. Failed/not-started/cancelled members mark aggregate error so a different successful member cannot turn a cancelled batch into metadata success. This deliberately errs toward no metadata handoff after incomplete work.

The 15-second acknowledgement and five-minute accepted-source budget are proposed constants, **not device-tuned policy**. Large roots and long serial queues may exceed five minutes; Claude/owner should validate these limits before integration. The scope also bounds standalone executor scans, while counted work additionally has the queue-inclusive deadline.

## Cancellation safety and limits

`ScanIoScope` is a thread-local ownership scope. Scoped Sardine/redirect clients retain the base transport/authentication/TLS configuration and share its connection pool; cancellation enumerates only calls registered to that scope, never `dispatcher.cancelAll()`. Calls created before cancellation but executed afterward are cancelled at callStart. Thread-local state is removed at close; scanner executor finally clears its interrupt before another member runs.

Checkpoints fence a cancelled worker after provider-lock acquisition, after root resolution, around traversal callbacks, before reconciliation/deletion/bulk flushes and subsequent writes. The old worker cannot start a scan after finally acquiring a lock it waited for while cancelled. Already committed work is not rolled back, and an already-entered provider transaction may finish. Its batch slot stays owned until the worker exits, so a replacement counted batch cannot overlap those writes. Offline/partial listing protection remains in place; cancellation does not flush a pending delete pass.

A JVM monitor, platform DNS implementation, blocked provider transaction or non-DAV transport may not honor interruption. The draft **does not promise immediate release** for such a worker: preserving ownership is safer than resetting the shared count while it could still write. HTTP call cancellation specifically covers DAV listing, root lookup and redirect probes. FileCore's base clients currently have no custom event listeners; the scoped listener forwards callStart/end/failure, not every optional diagnostic callback. Preserve any future additional instrumentation when integrating this wrapper.

## Verification and remaining gates

Five `ScanIoScopeTest` JUnit tests compiled at Java 17 language level with Eclipse ECJ 3.40.0 and passed using JUnit 4.13.2, OkHttp/MockWebServer 5.5.1 and their Maven dependencies in a scratch directory. Silent server, slow trickle, disconnect/recovery, unrelated-call isolation and cancel-before-execute were covered. No Gradle was invoked. An initial javac attempt could not compile because the environment has a JRE without javac; ECJ was used instead.

The MediaLib patch contains its own `NetworkScanLedgerTest` and `NetworkScanTransportTest` (silent/trickle/disconnect through the actual raw DAV lister and scanner finally), with a test-only MockWebServer dependency. The FileCore patch additionally contains Robolectric DAV/redirect integration tests. These Android/Robolectric tests were **not executed or type-checked against the complete app workspace**. Existing tests were not weakened, skipped or deleted. Claude must compile both dependencies plus the app and run all existing and new tests; test method names in the table describe assertions, not passing results.

| ID | file:line | What goes wrong / remaining work | Evidence | Suggested fix | Severity | Confidence |
| --- | --- | --- | --- | --- | --- | --- |
| SCAN-UI-01 | app `leanback/PreviewLibraryScan.java:66`, `:257` | Native-only queued state and terminal/out-of-order totals can still misrepresent an idle batch independently of corrected native ownership. | Original investigation's lifecycle and guard trace; this draft never edits that class. | Owner/Claude: consume one batch identity with monotonic transitions and install a guard for every operation. Preserve native ownership when considering retry. | user-visible | high |
| SCAN-DRAFT-01 | MediaLib `NetworkScannerServiceVideo.java:cleanup` | Ancillary cleanup/listener failure can still leave the old alive flag stale even though ledger compensation runs first. | Existing cleanup performs Upnp/listener work before/around flag notification; no exception-safe rewrite of those unrelated callbacks in this draft. | Claude: test injected cleanup failures and make flag/resource cleanup exception-safe, with regression tests. | user-visible | high |
| SCAN-DRAFT-02 | new `NetworkScanLedger.java:accept`, new `ScanIoScope.java:cancel` | Source timeout is cooperative for non-DAV/platform/DB/monitor work; queue-inclusive timeout needs Shield validation. | Cancellation owns DAV calls, not all protocol sessions or JVM monitors; count intentionally stays busy while a running worker has not unwound. | Validate limits; provide transport-specific cancellation if needed. Never force-reset count as a substitute. | user-visible | high |

These remaining concerns prevent a production-ready claim. The deliverable is the requested reviewable draft and tests; the app build's patch list remains unchanged.
