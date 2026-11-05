# DP-10 continuation: listing ownership audit

2026-11-04. This records existing DP-10, not a new finding or a completed fix.
The original audit below is retained; the follow-up implementation checkpoint
supersedes its "create a new patch" instruction. NEXT item 3 remains open; do
not advance to item 4. Production app/build wiring remains unchanged.

## Reproduced source baseline

- App: pulled `codex/0155-code-2026-11-04` at `9fd8bc94c`; `UpdateNextTask.java`
  was read completely (444 lines). `PlayerService` replacement/teardown sites
  were inspected, not counted as the full NEXT item 6 read.
- FileCore: clean upstream `c4b760c55102c72d45f68b0d16999fc1f4f013a7`, all eight
  `native-supernova156.lock.json.filecore_operations` applied with `--index`,
  then existing `filecore-supernova155-scan.patch` applied with `--index`.
  All applied cleanly. Paths/lines below refer to this prepared dependency.
- MediaLib: fork `5758074049bc153ac4967b4ecd21886469270076`, all its patch
  operations in `supernova146-dependency-patches.json` applied in manifest order
  in scratch. Foundation identity transformation was not run; these transport
  sources do not depend on it. This was source inspection, not a build receipt.
  The existing upstream patch reports one historical trailing-whitespace warning.
- No Gradle, Android build, new DP-10 test execution or device test was run.

## Transport boundaries the public hook must respect

The FileCore paths below are relative to `src/com/archos/filecorelibrary/`;
MediaLib paths are under `src/com/archos/mediacenter/filecoreextension/upnp2/`.
All rows refer to **existing DP-10** (user-visible retention, high confidence in
ownership; termination of a future implementation must be measured, not inferred).

| ID | File:line | What goes wrong / ownership boundary | Evidence | Suggested fix | Severity | Confidence |
|---|---|---|---|---|---|---|
| DP-10 (DAV) | `webdav/ScanIoScope.java:29`, `:39`, `:66`; `webdav/WebdavRawLister.java:39` | Existing scanner scope owns calls, but is created on the worker and isn't registered with UpdateNextTask. It cannot receive task cancellation during scope creation. | Thread-local scope plus callStart registration; deadline interrupts owner and cancels only its tracked calls. `RawLister` has only the no-argument synchronous method. | Add a public task-owned handle in a **new companion patch**; cancellation state must exist before factory construction. Register scope atomically against cancel-before-register, keep callStart fencing, close on worker in finally. | user-visible | high |
| DP-10 (FTP/FTPS) | `ftp/FTPRawLister.java:45`; `ftp/Session.java:110`, `:131`, `:205`, `:226` | Listing has its own client, but the client is not returned until after blocking connect/login. | New FTPClient/FTPSClient per attempt; lister finally closes only after acquisition returned. Connection retries create another client. | Register each owned socket/client **before** connect, including retries and data sockets. Cancellation must not send blocking logout on the caller/deadline thread. Preserve TLS policy, credentials and retries for live tasks. | user-visible | high |
| DP-10 (JSch) | `sftp/SFTPRawLister.java:51`; `sftp/SFTPSession.java:56`, `:151`, `:200` | A listing channel rides a cached SSH session; getSFTPChannel includes blocking session/channel connect. | `currentSessions`/`usedSessions` and per-endpoint monitors; releaseSession intentionally preserves the cached connection while in use. | A listing-owned channel can be closed; a cached/shared session cannot. Handle cancellation during session/channel acquisition without closing playback's session or leaking a late channel. Isolate connection setup if it cannot be safely interrupted. | user-visible | high |
| DP-10 (SSHJ) | `sshj/SshjRawLister.java:63`; `sshj/SshjUtils.java:45`, `:110`, `:169` | Both SSH and SFTP clients are cached by credential and reused. | Static sshClients/sftpClients; getSFTPClient returns the cached object. closeSFTPClient/disconnectSshClient are endpoint-wide. | Do not register either shared object as a task-owned closeable. Supply a dedicated listing channel/connection before blocking I/O, retaining host-key verification/auth behavior. | user-visible | high |
| DP-10 (SMBJ) | `smbj/SmbjRawLister.java:58`; `smbj/SmbjUtils.java:56`, `:142`, `:283` | DiskShare/session/transport are shared. Retry/read locks protect other consumers. | Static maps; withReadRetry holds a read lock across share.list, reset takes the write lock and tears down the endpoint. | Task-owned request/handle or isolated transport; never resetConnection or close the cached DiskShare/Connection on task cancellation. Account for resolution/connect/auth/share acquisition and retries, not just list iteration. | user-visible | high |
| DP-10 (JCIFS) | `jcifs/JcifsRawLister.java:64`, `:84`; `jcifs/JcifsUtils.java:67`, `:427`, `:480` | A fresh SmbFile does not establish ownership of its pooled context/transport or shared protocol probe. | Four static base contexts; shared FutureTask protocol probes; getSmbFile resolves/negotiates before listFiles and retries with another handle. | Preserve protocol selection and shared probes; use operation-owned transport/context or library request cancellation. Closing a global context/pool is prohibited. A wrapper around listFiles alone misses setup/probe stalls. | user-visible | high |
| DP-10 (UPnP) | MediaLib `UpnpRawLister.java:84`, `:112`, `:172`; `UpnpServiceManager.java:514` | Worker wait and actual network request are separate. Interrupting the waiter does not cancel the submitted Browse. | Factory constructor does blocking device lookup. Manager discards ControlPoint.execute's handle and returns an int. Callback populates mFiles on another thread. | New MediaLib companion patch must expose an owned browse handle/request cancellation and fence late callback writes as well as the wait. Never shut the shared UPnP service/router down. Preserve result order. | user-visible | high |

Local/SAF/ZIP also have synchronous filesystem/provider calls. An interrupt and
loop checkpoints provide cooperative cancellation, not a claim that Android
DocumentFile or a kernel filesystem call can always be forcibly stopped. ZIP
traversal must own/close its ZipFile. MIME/metadata access during app filtering
can itself perform transport work and must remain within the ownership lifetime.

## Exact next implementation step

Create **new** `.github/build/filecore-supernova155-listing-cancellation.patch`
after `filecore-supernova155-scan.patch`; never edit the existing scan patch or
protected dependency-preparation files. Start at `RawLister.java:38` (public
listing contract) and `webdav/ScanIoScope.java:29–105` (existing call lifecycle),
then implement each transport ownership boundary above. UPnP requires a **new**
MediaLib companion patch, not a private client access from the app.

The handle needs explicit transitions, testable without starting I/O:

1. Task exists / cancelled before execute: no factory or blocking acquisition may
   begin. Publish the task's cancellation token before submitting the worker.
2. A resource registers before blocking connect/list. Under the same state lock,
   either it becomes owned, or an already-cancelled/closed handle closes it and
   rejects work. Recheck at the operation start so cancel-before-connect cannot
   be undone by reconnecting a closed object.
3. Cancellation and the whole-operation deadline both detach only owned work and
   fence its callbacks. Closing must not hold a task lock while invoking a client
   that can block or reenter. cancel(false) still cancels owned network I/O; the
   flag cannot mean "leave an obsolete listing alive".
4. Worker finally cancels the timer, closes resources, unregisters ownership and
   shuts down its executor. A late deadline cannot interrupt a reused worker.
   Do not use Future.isDone/isCancelled as proof the worker actually ended.
5. Cancellation remains active through filtering, comparator calls and traversal;
   add checkpoints without changing comparator ordering, MIME eligibility,
   current-file matching, modulo wrap or repeat/binge/database preference.
6. In `UpdateNextTask.java:400–443`, keep checks both before posting and **inside**
   the main-queue runnable. Fence registration and cancel/execute races too;
   cancel-before-execute must not submit into a shut-down executor. Preserve DP-09.

Deliver the complete DP-10 app change, new companion patch(es), and tests together
in the requested single DP-10 fix commit. No partial public API or app call site
is committed here: doing only DAV would leave the factory-wide requirement open.

## Acceptance tests required in that commit

- Actual WebdavRawLister PROPFIND against MockWebServer: no response headers,
  then a valid DAV multistatus body trickling continually. Cancel explicitly and
  by whole-operation deadline; join/await the actual worker and drain main queue.
- Repeated create/replace/cancel, including cancel(false), cancel before submit,
  cancel between resource creation/registration and before connect/list, and
  timer/close races. Assert worker count returns to baseline and no late listener.
- Cancel while filtering, in comparator calls, and while locating current/next.
  Use controlled latches; assert no further traversal and no result delivery.
- Run an unrelated playback call concurrently on the same base client; it must
  complete with the expected bytes after the listing is cancelled. Test the
  transport-specific sharing cases from the table, not just unrelated hosts.
- Verify exact unchanged next URI/ID for database, remote list, normal folder,
  repeat wrap and binge fixtures. This is a cancellation fix, not DP-09 selection.
- Register fake owned resources before blocking acquisition to deterministically
  exercise close-before-register and late-register races; use real socket-backed
  transport tests for termination. Fake flags alone do not prove a network stop.

Existing ScanIoScope tests from the earlier draft are useful supporting tests,
not evidence that UpdateNextTask or non-DAV transport cancellation is fixed.

## Follow-up implementation checkpoint — task lifetime and DAV draft

New file: `.github/build/filecore-supernova155-listing-cancellation.patch`.
SHA-256: `f60e493b5deeaa4411bfcacc5781e0ccd32c0da77d3880c3b86e77c29910f5da`.
This is an **unintegrated prerequisite draft**, not the requested DP-10 fix.
Never apply it to a release independently of the remaining transport/app work.
The eight reviewed FileCore operations and the unchanged scan draft
(`28ec346fe7fb974b0aed3bf1ceca3d8b9c7ae629ee6bd0747c0f14353496c662`)
apply before it at `c4b760c55102c72d45f68b0d16999fc1f4f013a7`.

| Component | Implemented in draft | Limits / next change |
|---|---|---|
| `ListingCancellation` | Single-use token exists before worker submission. `enter()` binds the owner and starts the whole-operation timer before factory/setup. Atomic owned abort registration rejects cancelled/closed lifetimes; cancellation and close detach resources before invoking callbacks, tolerate a broken abort, and serialize the interrupt with owner detachment. Registration close unregisters completed resources. Scope close is owner-checked and idempotent. | Public API supplied only in patch; no production app caller. Abort callbacks must be nonblocking and operation-owned; this is not permission to register cached transport clients. |
| DAV `ScanIoScope.client` | Existing scanner scope still works. A listing-bound client registers each call on callStart against its captured token, even when execute is late. callEnd/callFailed release registration. Base client/pool/dispatcher remain untouched. | Uses existing scan patch's client wrapper; full Android lister tests await Claude. No other protocol is hooked yet. |
| `WebdavRawLister` | Checkpoints before client setup, after redirect resolution, during result conversion and before returning. | App filtering/sorting/traversal must remain inside the future app binding; not implemented in UpdateNextTask. |

Prepared the real FileCore baseline in scratch and applied all eight locked
operations plus the old scan draft. Exported the new patch, then checked and
applied it to a second fresh detached worktree of that baseline. The applied
sources compile at Java 17 using ECJ 3.40.0 on JRE21 with JUnit 4.13.2 and the
prepared dependency's **OkHttp/mockwebserver 5.5.1**, mockwebserver3 5.5.1,
Okio 3.18.1 and Kotlin stdlib 2.1.21. Ran these actual test classes:

- `ListingCancellationTest`: **12 passed**. Cancel before entry/registration,
  unregister completed resources, late registration after close, exactly-once
  abort, abort outside state lock, broken abort isolation, cancellation checkpoint
  after interrupt clearing, nested/wrong-owner/repeated close, late deadline on
  a reused worker, and 100 iterations each of register/cancel and close/cancel.
- `ListingCancellationSocketTest`: **8 passed**. Silent/trickling PROPFIND
  sockets with explicit abort and deadline; eight repeated worker replacements;
  call created before cancel but executed after; callStart after normal close;
  same-base-client concurrent playback finishes with expected bytes. Tests await
  actual runnables and executor termination and never cancel their Futures.
- Existing `ScanIoScopeTest`: **5 passed**, providing scanner regression coverage
  after the wrapper change. Earlier tests were not edited or weakened.

Four new `WebdavListingCancellationTest` cases use the real factory and lister,
real OPTIONS and valid DAV multistatus bodies; explicit/deadline × silent/trickle.
They were syntax-parsed, **not run or fully Android type-checked**. No Gradle was
invoked under MORE rule 4. All six changed/added Java files passed syntax parsing;
patch and worktree whitespace checks passed. The standalone socket tests consume
HTTP bodies directly, so they do not substitute for those real lister tests.

Continue by completing the missing transport rows from the audit above, using
only operation-owned resources and preserving protocol/auth/trust/retry behavior.
No MediaLib UPnP cancellation patch exists yet. Then integrate the public token
with UpdateNextTask and add the previously specified app/selection/main-queue
acceptance coverage. Cancel(false), comparator/traversal cancellation, actual
obsolete app worker exit and no callback after main-queue drain remain unproved.
Keep the final production DP-10 fix atomic; keep item 4 pending. The new draft
moves beyond the earlier source audit but deliberately does not expose a partial
API to the current application or shut down any shared transport.


## Backup-brief continuation — FTP/FTPS socket draft (2026-11-04)

NEXT item 3 remains **open**. New companion:
`.github/build/filecore-supernova155-ftp-listing-cancellation.patch`.
Apply after the eight locked FileCore operations, the unchanged scan draft,
and the unchanged lifetime/DAV draft described above, at upstream `c4b760c`.
Do not install any of these partial prerequisites as the completed DP-10 fix.
No production application caller or protected build wiring changed in this run.

| ID | File:line in this companion's prepared result | What changed / evidence | Suggested next action | Severity | Confidence |
|---|---|---|---|---|---|
| DP-10 continuation | `src/com/archos/filecorelibrary/ftp/ListingSocketFactory.java:32–45` | Allocate an unconnected socket, register its close against the task, then check cancellation before returning it. A late register closes/rejects it; a closed socket cannot be reconnected. Only operation-owned sockets are registered. | Android socket-close/handshake acceptance, especially Android 11 Conscrypt, still required | user-visible, existing finding | high ownership; device unproved |
| DP-10 continuation | `ftp/Session.java:111–143,177–180,191–223,263–266` | Install ownership before connect for each independent FTP/FTPS attempt; cancellation checkpoint before a retry stops reopening an obsolete connection. Live credentials/mode/retry policy unchanged. | Run actual Session/factory tests supplied below; add non-cancelled retry/authentication fixtures | user-visible, existing finding | high in source; Android tests unrun |
| DP-10 continuation | `ftp/FtpsTlsPolicy.java:26–31` | Capture the task lifetime and wrap each replacement SocketFactory, retaining the actual delegate installed by Commons Net `execPROT`. Trust store and control/data hostname verification remain. | Verify platform TLS behavior and rejected CA/hostname cases under ownership | user-visible, existing finding | high |
| DP-10 continuation | `ftp/Session.java:65–80`; `FTPRawLister.java:48–84` | Cancellation closes sockets without ABOR/QUIT/global session reset; cancelled cleanup skips protocol logout. Checkpoints surround cwd/list and result conversion; no result filtering/order logic changed. | Finish app callback fencing/selection tests before claiming no late app result | user-visible, existing finding | high |

### Validation completed here

- Fresh pinned checkout, each of the eleven patches passed ordered
  `git apply --check` and `git apply --index`; final tree and companion digest
  are recorded in the backup report. No existing patch was edited.
- Java 17 standalone ECJ compilation of real `ListingCancellation`,
  `ListingSocketFactory`, `FtpsTlsPolicy` and standalone tests; **32 JUnit tests
  pass**: 20 new FTP/FTPS socket tests and 12 existing lifetime/race tests.
  These execute real Commons Net 3.13.0 sockets, LIST parsing and the actual
  TLS policy. They do not stub sockets or cancel Futures to fake worker exit.
- New coverage: silent greeting, silent login, silent and continuously
  trickling LIST, explicit cancel/deadline, stalled control and data TLS
  handshake, encrypted trickling LIST, eight repeated replacements,
  cancel-before-allocation, cancel-between-allocation-and-registration,
  closed-before-connect, live FTP and verified FTPS file selection, and an
  independent live FTP client surviving cancellation of an actually blocked
  listing. The independent client is on the same loopback host at a different
  port; same-endpoint playback and other transport-sharing acceptance remains.
- Each socket test awaits its actual runnable and executor termination and
  checks that no result is delivered after the cancellation checkpoint. This
  is **not** UpdateNextTask main-queue/listener acceptance: app integration is
  still absent. Test-only TLS certificates are generated with current validity;
  no expired private fixture or relaxed production trust is used.
- Six new `FtpRawListingCancellationTest` Robolectric cases exercise the actual
  `RawListerFactory -> FTPRawLister -> Session` route on API 30: greeting/login
  cancel, silent/trickle explicit/deadline, no queued reconnect and no QUIT.
  **Written and Java-17 syntax parsed, not executed or full Android type-checked**.
  All six changed/added Java files parse. No Gradle or device build was run,
  as required by MORE rule 4.

Standalone compiler/runtime inputs: ECJ 3.40.0 on OpenJDK 21 targeting Java 17;
JUnit 4.13.2, Hamcrest 1.3, Commons Net 3.13.0 + Commons IO 2.21.0,
OkHttp/okhttp-tls 5.5.1, Okio 3.18.1, Kotlin stdlib 2.1.21 and SLF4J API 2.0.17.
Resolve the prepared dependency's normal test classpath; compile the three
production files above, `test/java/com/archos/filecorelibrary/ListingCancellationTest.java`
and `test/java/com/archos/filecorelibrary/ftp/FtpListingCancellationSocketTest.java`
with ECJ `-17`; run both classes with `org.junit.runner.JUnitCore`. The separate
Robolectric class requires the full Android workspace and is not part of 32.

### Exact next checkpoint (before NEXT item 4)

1. Run/review the six actual factory/Session tests with Claude's full workspace.
   Finish FTP setup ownership: synchronous `InetSocketAddress`/Commons Net DNS
   resolution and credential-store loading happen before a socket exists and
   are **not forcibly cancelled by socket ownership**. This draft makes no
   unbounded-DNS termination claim. Add a controlled connect-in-progress race,
   cancellation during retry setup, live retry/auth failure and same-endpoint
   unrelated playback tests. Measure Android 11 close latency for SSL sockets;
   only host-JVM close latency is established here. Failed attempt sockets stay
   owned until task-scope cleanup; validate bounded retries and cleanup there.
2. Complete the existing audit's isolated JSch, SSHJ, SMBJ and JCIFS resources
   and setup/retry/probe paths. Never cancel a shared cached session, pool or
   connection. The FTP companion does not implement these transports.
3. Create a new MediaLib UPnP companion retaining the submitted Browse handle,
   with cancellable device lookup/wait and late callback-write fencing.
4. Pair the completed public hook with an allowed new app draft for
   `UpdateNextTask` (the NEXT brief permits new patch files, not arbitrary
   production-file edits): token before execute, cancel(false), cancelled-before-
   submit/execute races, scope through filter/comparator/traversal, checks before
   posting and inside main-queue runnable. Keep DP-09 selection/order/repeat/binge
   logic byte-for-byte except cooperative checkpoints. Run the required real
   WebDAV silent/trickle app-worker/main-queue tests, repeated replacement,
   per-transport shared-client survival and exact unchanged URI/ID fixtures.
5. Only when those prerequisites and acceptance tests are complete, deliver the
   requested single atomic **DP-10 fix** commit. Then start NEXT item 4 (security
   and privacy audit), followed by items 5 onward. No later NEXT item was marked
   complete or reordered in this run.
