# NOVA 6.6.2 – 6.6.4 (27 Oct – 4 Nov 2026) against Supernova — Claude, 4 Nov 2026

Sources: NOVA release notes (aos-AVP releases page) and the upstream repos at aos-avos `5cf3ae9`, aos-Video
`95f233f9`, aos-MediaLib `e5886e04`, aos-FileCoreLibrary `78ca381`. "Already in Supernova" was checked by code search
only; Codex confirms each one commit by commit before anything is taken.

## A. Engine and playback → the engine release (next version)
Already in Codex's engine plan (`docs/codex/findings/engine-0156-plan.md`, upstream avos up to `2540c4f`):
HDR colour signalling and HDR metadata, display HDR support, Dolby Vision profile 9 + SDR fallback, AV1 Dolby Vision,
DV first-frame recovery, decoder ranking and retries (aos-Video `9ec1bdc2`, `5c721514`), MPEG-TS duration/resume,
multi-video-track timestamps, native window / buffer error handling, OMX removal, subtitle box padding, pre-libass
outline/shadow defaults.

**New since the plan was frozen — add to the engine release:**
- avos `f84855b`, `eb5250d` (PR #19): ASS/SSA subtitles and `\pos` stay on screen when the video box overflows the
  display (Fit width / zoom). Java side aos-Video `8bbfb666`, `95f233f9` (the portrait half does not apply to TV).
- aos-Video `d0be0f11`: a failed network playback reopens at the latest seek position (not found in Supernova).
- aos-Video `e1642b6b`: external subtitles are linked even when renaming them fails (verify against Supernova).
- MediaLib `cf75fdcd`: subtitle cache falls back to internal cache when unavailable.

## B. Library, network and storage → the version after (with the scan fix)
- FileCore `57d73f0`: network initialisation at normal thread priority — faster start-up.
- FileCore `868496a`: no MediaStore `_data` insert without legacy write access (fixes errors on newer Android).
- MediaLib `5d09a072`, `28d64633`, `b1bcc081`: UPnP servers found before the listener attached; batched updates.
- MediaLib `4541d3c8`, `0eeb6b4b`, `87f721ff`: subtitle metadata clean-ups (take only with their callers).
- MediaLib `e5886e04`: ignore zero-start credits from TheIntroDB (already planned).

## C. Already in Supernova or not applicable
- Intro/outro skip and the libavos-before-subtitle-engine crash fix are already in Supernova. NOVA now turns
  intro/outro skip **on by default** (`915347c7`) — an owner decision for Supernova.
- NOVA screen changes (skip pill styling, subtitle settings layout, portrait/phone fixes, toast removal) are NOVA's
  own interface; Supernova has its own and takes none of them.
