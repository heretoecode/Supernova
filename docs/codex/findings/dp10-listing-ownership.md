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
