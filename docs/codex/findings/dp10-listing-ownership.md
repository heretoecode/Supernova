# DP-10 continuation: listing ownership audit

2026-11-04. This is implementation evidence for the existing DP-10, not a new
finding or a completed fix. No player/dependency production source is changed by
this audit. NEXT item 3 is still the next code item; do not advance to item 4.

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
