# Supernova 0.142 implementation evidence

## Authority and exact state

Owner's 2026-10-23 instruction authorises A/B implementation and HUD proposal only; it supersedes planning-only text in imported documents. Isolated branch `codex/supernova-0.142-implementation-2026-10-23` starts exactly at delivered application `4f0937f1fe8826f50d2c0e844b0837e8a2360a46`. Main `75d9f012`, prior release tip `0f2dfe4d`, blueprint `7a2a906a`, audit `1343fd6e` are preserved. No tags/public GitHub Releases exist. PR19 is draft/open/unmerged against 0.140; earlier open PRs are historical work, not replacement baselines. Imported authority paths are added selectively, without cherry-picking unrelated branch changes.

`PARITY_LEDGER.md` and JSON map all 202 required items before application edits. Labels and baseline citations come from the complete audit. Mapping is not a test result.

## Source-to-layout token and component mapping

| Blueprint / component | Existing source authority | Resolution |
| --- | --- | --- |
| Typeface, white/grey text | SharedThreePanel.text, TopNavigation.text | Existing sans-serif-light; white primary, #b7bdc6 secondary; no new font |
| 1920×1080 logical design | TvLayout, Android TV 960×540dp at density 2 | Explicit blueprint pixel dimensions /2 in baseline dp; scale with TvLayout and preserve system insets |
| Rail x32 / width352 / gap20 | PreviewPages content inset; SharedThreePanel.dp | Network-specific 16dp inset, 176dp rail, 10dp gap at baseline; no global panel default change |
| Rail row76 / icon30 / text25 nominal | SharedThreePanel.accentAction, PreviewIcon | 38dp minimum rows with wrap-content for font scale; icon15dp, 12.5sp at baseline density mapping; retain readable action18sp where interaction requires it |
| Main title40 / subtitle21 | SharedThreePanel.text | 20sp heading / 11sp subtitle baseline; inherited font scaling |
| Card radius18 / gap14 / padding22 | SharedThreePanel.decorate | Reuse dark gradient/border family, network-specific 9dp radius /7dp gap /11dp padding |
| Focus2px, blue | PreviewAccent.color, SharedThreePanel.accentFocus | Existing PreviewFocusRail with its 160ms travelling PreviewFocusGlow; existing accent resource/preference, no hardcoded replacement blue; monochrome icons |
| S01 O10/O20/O30/O40 | Box inventory, canonical shares, Put.io enabled/token presence, PreviewLibraryScan/LibraryHealth | Four vertical cards; no fake reachability/quota/capacity. Account state without a successful probe is labelled saved connection, not connected |
| S02 L01–L06 | Box/ExtStorageManager, UniversalFileBrowser.open | Actual volume rows in workspace, explicit OK browse; no auto inclusion or storage removal |
| S03 W01–W13 | networkShares/discoverDevices/scanControls | Source list + supported Add/Discover/Scanning controls within workspace; no seventh rail item |
| S04 C01–C11 | connectionForm/connect/launchCredentials, PreviewTextInput/Keyboard | Six actual protocols, all original fields and secure keyboard. Connect/Browse semantics; no fictional Test Connection success, display-name persistence or cancel phases |
| S05 F01–F11 | BrowserSelection, FileAdapter, breadcrumbs, sort/filter prefs | Same RecyclerView, three toolbar controls, six sorts, membership/bookmark confirmations; optional detail split INSIDE single workspace only while browsing |
| S06 P01–P23 | PutioAccountController/AuthFlow/BrowserActivity/ReadClient | Retain all eight actions and associations. Extract reusable API browser controller for embedding, keep Activity adapter. No new provider/token/playback operation |
| S07 H01–H07 | LibraryHealthPanel.NAMES and existing actions | Five category rows inside workspace; same issue/action handlers, first real focusable entry |
| Forms / account / health normal pages | Browser centre/items/right/actions | Reparent existing content into one vertical scroll workspace; fixed rail remains visible. Browser alone uses internal list/detail split |
| Editor five steps | CustomLibraryActivity.Workspace.render | Add 97% opaque dark scrim above artwork; 94% panels; keep layout/controls/Live Preview and avoid live blur |
| Host variants | onboarding, directoryPicker, PreviewBrowserSurface | Main redesign explicitly enabled; local picker and Home onboarding contracts retained |

## Engineering decisions resolved from source

Connection records are library roots plus saved locations, deduplicated by canonical URI, not a new source database. Save Location remains immediate and independent; library changes remain staged until reviewed. No Scan This Source or Movies/TV classification is invented for the generic browser. Existing global scan and explicit Put.io associations remain.

Put.io browser adaptation: move its existing API pagination/ancestry/metadata logic into a reusable lifecycle-owned controller and inject the browser surface callbacks. Main Network & Files embeds it in the existing workspace; standalone activity delegates to the same controller. Leaving section cancels tasks and invalidates callbacks. This preserves API read-only capability, account checks, cursor cycles and WebDAV playback separation.

Audit L-01 is stale relative to delivered source: PreviewKeyboard already includes the 0.141 punctuation rows. Retain and rerun those tests. L-02–L-12 remain capability boundaries, not invitations to add storage management, migrate credentials or redesign scan ownership. Inherited exclusions must not be silently widened; expose the excluded ancestor with explicit confirmation rather than pretend a child Add worked.

## Verification

Passing exact-source CI [40058921765](https://github.com/heretoecode/Supernova/actions/runs/40058921765) validates `b347707defdbf2ab3ae83d920ba9fa7af3936220`: 1,094 Android cases / 232 classes, zero failures/errors/skips; 102 tooling tests; lint zero errors /1,788 warnings; 64 native decode comparisons, 24 rebuilt FFmpeg libraries, 88 native and 138 runtime hashes. Complete ZIP download/hash/CRC and independent unsigned APK identity, assets, native bytes and pre-key evidence gates pass. See RELEASE_REPORT.md and the conformance/readback receipts. Application inputs remain unchanged from `08a896b1`.

The signing workflow freezes that validated SHA with explicit checkout/ancestry checks, as in 0.141. Local tooling repeats all 102 checks after the freeze assertions. No signing key is accessed and no signed APK distributed at this checkpoint. Owner-enabled branch access is verified; physical Shield, live providers, USB and upgrade acceptance remain separate gates.

Final eight full-shell captures and five editor steps come from the passing CI. The capture harness introduced in `51e1ac59` invalidates descendants and asserts saved brand/rail pixels to prevent intermittent cached-text omission in software screenshots. Original editor-source measurements are intentionally failing baseline evidence (25 channel levels); corrected steps measure 1. Software fixtures do not certify physical TV fidelity.

Inline Put.io cancellation returns to Cloud Services (or a neutral cancelled reader in a host without a return callback). It must never bypass a different-account ownership decision or implicitly resume a suspended association. No account/source mutation occurs on Cancel or Finish Later.

Credential limitation: the non-SMB correction preserves email-style usernames for new or re-entered credentials. It cannot safely reconstruct an already truncated username from an older stored record; affected connections need explicit credential re-entry. Existing storage schema, encryption keys, scope boundaries, path case and protocol identity remain unchanged. Save Password still follows the existing persistence semantics; Connect is not a claim of successful authentication or a durable saved browsing location.


Form scroll verification uses the semantic Connect action container, not partial text matching against its explanatory paragraph. The initial new assertion matched that paragraph and was corrected; its failure is not evidence of a product focus defect. Existing geometric traversal is retained unless the corrected regression establishes a failure.

Final form contract: choosing a protocol focuses Server / Address; keyboard Done returns to the edited field; Save/Show toggles retain their own focus. Posted focus work is fenced by the listing generation so leaving the form cannot retarget a later section. WebDAV HTTP and plain FTP explicitly disclose that the connection is unencrypted; saved-credential storage copy does not imply credentials are never sent to the selected server. These presentation/focus details preserve existing transport and persistence handlers.


Full CI 40023022572 completed 1,094 tests with one failure in an unchanged backup confirmation test. The helper could observe worker completion before LiveData published the completed phase to the UI. A test-only correction waits for both; all 12 BackupSettingsControllerTest cases pass locally with original restore assertions retained. Native verification (64 decode comparisons / four ABIs), lint and unsigned assembly succeeded in that failed run; it is retained failed evidence; the fresh full CI 40058921765 passes. Owner subsequently enabled the exact branch in the signing environment; all eight prior rules remain. No signing secrets have been exercised or APK distributed.
