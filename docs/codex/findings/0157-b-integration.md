# 0.157 Part B — evidence and screen-owner integration

Scope: BRIEF_0157_B.md, source merge 63d9a40ae (source 8bd76e114), 7 November 2026.
No Activity, controller, view, menu, resource, Gradle, workflow, dependency fork or 0.156 file is edited.
PlayerStripSource is a data adapter: its changes select numbered timeline data and a deferred frame source;
its screen/navigation/focus/layout contracts are unchanged. No newer NOVA stack is introduced.

## Findings and concrete fixes

| Item | Evidence | Non-visual result / boundary |
| --- | --- | --- |
| B1 UK certificates | MediaLib ShowIdParser selects iso_3166_1=US for the legacy field. StreamingRepository.country defaults to IE (service availability country). SharedMetadata.entry excludes episodes from its classification block. PreviewMoviePage also excludes episode/season ratings. An unqualified legacy certificate therefore cannot supply the requested GB value. | AgeCertificates stores GB-only, exact title/episode keys independently of locale/service country. Production classification gateway populates it; explicit refresh is worker-only. Episode advice wins; series fallback is labelled. Release notes are not content advice. Missing provider descriptors stay empty; no episode-specific BBFC lookup or reasons are fabricated. |
| B2 seek pictures | SeekThumbnails.nearest previously only queued a disk lookup on a miss. The scrub UI calls nearest; it never requested extraction at that target. Background sampling near current playback cannot populate distant scrub positions. This affects both local and Put.io. | A miss queues an owned, coalesced latest-target extraction. Earlier empty preview requests are removed; callbacks for parts are retained. Existing buffering/seeking/background/PiP gates remain. Fake-extractor regressions fail against the previous class for both sources. Actual extractor binding verifies resolved Put.io URL/local URI, microsecond timestamp and owned release. Hardware decoder/real signed-URL operation still needs Shield testing. |
| B3 part pictures | PlayerPartsStrip.loadChapterPictures returns when source.frames is null. PlayerStripSource used the not-yet-created SeekThumbnails directly. Later engine creation did not supply a completion for those abandoned requests. | DeferredPartFrames queues up to 256 requests across engine startup, then attaches to the owned thumbnail engine; source close fences late results. Existing PartPictures requests a position inside every part. Tests cover all parts, startup, bounds and close. Failed/unsupported decoding still yields no bitmap rather than invented artwork. |
| B3 chapter names | IntroDB replaces/splits ordinary chapters, but the old data keeps file labels such as Chapter 03 and Chapter 05. | Rule supplied through PlaybackParts.numbered: count remaining CHAPTER intervals 1…n; rewrite only empty or generic English Chapter N names. Keep authored names and IntroDB labels, positions and kinds. PlayerStripSource applies that data rule. Splits count as separate remaining intervals. No marker or seek position is changed. |
| B4 Episodes row | PlayerService.previewSeasonEpisodes and adjacent navigation depended exclusively on PreviewLibraryLoader.memoryCache. PlaybackEpisodes also requires the current physical ID in that snapshot. Empty/replaced Home snapshots therefore give an empty row even when the database has several episodes. The view hides the tab for fewer than two results. | A player-owned worker queries the exact TMDB series independently, maps public physical IDs, filters personal/excluded roots and reuses hidden/version/season policies. CancellationSignal, source generation, series identity, 30-second refresh/retry and destruction fence the result. Authoritative empty results cannot revive an old snapshot. Real SQLite/provider/worker tests reproduce the missing-cache route and fence an old publish. This proves a source defect; the original owner's file/ID trace was not supplied, so it is not a claim that every Marshals/MobLand mismatch had this cause. |
| B5 track fields | PreviewTrackLabel flattens language/codec/channels into one label and abbreviates codecs. | TrackDetails supplies separate language, original disposition, plain codec, channel layout, SDH, forced and audio-description fields; TEXT is empty. No unsupported original/SDH flags are inferred from arbitrary track names. Screens must adopt the new record. |
| B6 separate choices | SeriesTrackChoices used one :enabled bit to gate both stored descriptions. | Separate :audio_enabled/:subtitle_enabled bits fall back to the legacy bit until set. Existing description JSON stays intact, including subtitle None. Disabling one retains its saved choice and leaves the other active. Legacy combined setter still changes both. |
| B8 inactivity | Existing limits count only automatic episode boundaries; a plain CONTINUE event calls PlayerService.onCompletion. Reusing it for a mid-episode idle question would advance the episode incorrectly. | Elapsed-time inactivity is optional, defaults off, resets on real input, and has the existing 60-second question timeout. IDLE_CONTINUE keeps the current video; a real episode boundary held during the question can still advance after confirmation. Clock option epochs are recomputed per call; selected timers retain monotonic deadlines. Owner forwards actual input through playbackInteraction, never progress ticks. |

## B7 — complete PiP removal needs protected screen changes

Part B explicitly says “non-visual work only, no screen/resource/Gradle/.github/ edits”. That prevents complete
removal here: the entry/menu and platform lifecycle are in the protected player screen. The owner's removal
decision is already recorded in docs/REVISIT_LATER.md; this is an ownership gate, not a request to approve the
feature decision again. No misleading partial manifest disable or dead button is introduced.

The source owner can make one atomic removal using this map (line numbers are discovery anchors):

| Location | Remove / retain for restoration |
| --- | --- |
| AndroidManifest.xml PlayerActivity declaration, supportsPictureInPicture=true | Remove capability with the entry/menu change; leaving the entry after disabling capability can fail at runtime. |
| PlayerActivity imports/fields and onPictureInPictureModeChanged / pipActions (about 1396–1437) | Remove PiP params/actions, mWasInPictureInPicture, subtitle-suppression and callback ownership. Retain ordinary multi-window and normal subtitle behaviour. |
| PlayerActivity resume/pause/destroy/new-intent/back handling (about 1444–2050) | Remove PiP-only branches/RETURN intent handling; keep normal playback resume, source generation and screen lifecycle. |
| PlayerActivity adjustSkipButtonForPiP and callers (about 3239–3283) | Remove PiP-specific size/visibility paths; owner retains normal skip-button layout. |
| PlayerActivity floating-player entry (about 3538–3566), PreviewPlaybackMenus and resource menu/string references | Remove the actionable entry and its platform calls together. Search all enterPictureInPictureMode references. |
| PipPlaybackActions.java and PipPlaybackActionsTest.java | Helper can be removed only after all screen callers are removed. Preserve its pre-removal source in git history for restoration. Do not silently skip/remove an existing test to get this branch green. |
| SeekThumbnailPolicy / service visibility pip flag | Remove/deprecate only after the protected visibility caller changes; it currently remains a valid tested pause gate. |

After that source-owned change: compile the complete app, run player/intent/subtitle/lifecycle tests and prove on
Shield that no PiP menu/entry/platform capability remains while Home/full-screen return and ordinary background
playback still work. The before-removal recovery reference is 63d9a40ae and the existing 0.156 PiP helper/tests.

## B9 — pause-test evidence has not arrived

No 6 November owner diagnostics archive is attached or present on the fetched source branch. Do not substitute the
4 November/older reports. On 6 November 2026 UK time is GMT, so the requested 17:51–17:55 interval is UTC too.
When supplied, correlate actual session/source IDs, player_state paused/playing reason codes, seek/seek_complete,
checkpoint/resume selection, focus events, reconnect/backend errors and buffering/transport windows. Distinguish
requests made while paused from actual reads. Old 0.156 coverage does not include the new 0.157 native observations;
absence of those counters cannot prove zero activity. No explanation of that specific pause is asserted here.

## B10 / original item 15 — company provenance

The confirmed display path in PreviewMoviePage (about 1109–1118) prefers any non-empty indexed Tags studios value.
Only an empty value reaches enrichment; for TV that fallback reads networks, whereas movies read
production_companies. MediaLib ShowIdParser also fills its studios field from networks, while MovieIdParser2 uses
production_companies. Consequently “Studio / Network” is not a trustworthy production-company field for a series.
A fresh verified production-company payload cannot replace the existing wrong non-empty tag on this path.

The original Marshals row and provider payload were not provided. It would be false to claim proof that WingNut
came from TMDB, a wrong match, an old NFO, a backup, or overlapping movie/episode rows. A supported additional
candidate is the video view's coalesce(m_studios,s_studios), which prefers a stale movie value if both metadata
rows survive; that is a candidate, not a demonstrated history for the owner's file.

The already delivered DetailsFacts.read(kind, expectedId, details, …).companies uses only the identity-checked
production_companies envelope. New CompanySourceBTest verifies separation from networks and a legacy studios
field and rejects a movie envelope even when its numeric ID matches the requested series. Fixtures are synthetic,
not purported real Marshals data. Screen owner should use this field for Production Company and expose networks
separately. Complete the original diagnosis with the specific file's type/public ID, movie/show/episode IDs,
m_online_id/s_online_id, m_studios/s_studios/studios and the exact kind/ID provider response; no credential or raw
network path is needed for that comparison. No screen wording or metadata-destructive repair was made here.

## B10 / original item 20 — Shield acceptance packet

The existing Player.seekLoading API and source/native patches remain unchanged. This is a same-engine rebuild
with seek hooks, not the excluded newer NOVA stack. Apply the eight existing add-only registrations from the
report; rebuild/review the avos archive including seek-and-frame-evidence followed by atomic-alignment. The old
reviewed archive deliberately yields UNAVAILABLE. No new archive is published or old hash relabelled here.

1. Build exact-source unsigned conformance, app plus both module suites and lint, with the reviewed rebuilt native
   archive and its source/recipe/library/ABI/capability receipt. Use the pipeline draft rather than a 0.156 signing
   workflow for new JNI. Actual signing remains owner-approved.
2. Spinner owner subscribes to Player.seekLoading().observe(mainExecutor, …), draws only snapshot.loading(),
   reads cached chapter/title, and closes the subscription on detach. Do not infer readiness from seek_complete.
3. On Shield/API 30 test local and Put.io clips, hardware and software paths, playing and paused seeks, forward and
   backward, chapter boundaries, and rapid repeated seeks. Each supported request must stay WAITING until its own
   successful output submission; obsolete tokens cannot make the current seek READY.
4. Delay network reads and decoder output. A timeout, seek acknowledgement or failed/dropped output must not report
   READY. Stop/source change/release/error must end the obsolete waiting state. Unsupported/old backends show
   UNAVAILABLE with loading false and still perform the existing fallback seek.
5. Verify every part picture/seek preview with the same clips, including opening the row before decoder startup.
   Change video during extraction; no old picture may populate the new source. Exercise disabled previews and
   buffering/background gates. Compare decoder/transport workload with playback stability.
6. Repeat source change, completion and teardown while observing the signal. Export the exact build/capability
   receipt and diagnostic session alongside the result. Successful Android output submission is the signal's
   defined boundary; physical SurfaceFlinger presentation is not measured or asserted.

SeekLoadingSignalTest and player ownership regressions pass in the Part B cohort. Previous native checks remain
32 production object compiles/four ABI assertions and the UBSan/pthread unit; those are not a linked archive or
executed Shield acceptance. Full device/archive acceptance is not claimed by drawing or testing a host spinner.

Final follow-ups: PartPictures admits only four concurrent requests so a 300-part timeline does not overrun the
256-entry startup/engine queues. Episode queries warm the existing version compatibility/health policy on their
worker before main projection. Inactivity supports time-only, episodes-only or both; the legacy master still
controls both. checkDeadlines reconciles elapsed stop/question deadlines after sleep, when Handler uptime has not
advanced. These follow-ups are covered by the Part B tests and introduce no screen or resource edits.
