# Supernova 0.139 backup/restore — owner decisions on portable personalisation
Date: 2026-10-13. Status: owner-approved direction; unresolved technical design questions noted. Docs only, no implementation authorisation.

## Goal
Backup/restore must reconstruct the owner's personalised Supernova experience on a completely new Android TV device: Home row ordering, show/hide, custom row definitions/membership, custom Library pages (including a third page), streaming preferences, language, subtitle styling, accent, playback and all other deliberately configured settings. Restore first, reconnect storage and services manually, scan/index available media, fetch fresh metadata/artwork from providers, and reconcile durable identities/progress where matching is possible. Preserve unresolved logical references rather than silently discard them. No promise of universal cross-device file matching, permissions, hardware parity or offline metadata availability.

## Settings completeness
Preserve **every meaningful user-configurable setting the user explicitly changed or saved**, including currently omitted subtitle appearance/preparation, scan interval, NFO policies, supported player options and diagnostic logging enable/disable. Distinguish explicit value (even if equal to default) from follow-current-default; build a typed/versioned settings registry and end-to-end coverage tests. Exclude credentials, secrets, Android grants, ephemeral status, device-only state and unsafe hardware-specific settings; retain portable intent and validate/clamp unsupported capabilities on restore with clear notice. Do not claim copying all raw SharedPreferences equals preserving user intent.

## Privacy and absolute exclusions
- **No actual media content/files in new backups, ever, and no UI option to enable it**: no video, audio, subtitle content, trailers, posters, backdrops, stills, thumbnails or downloaded artwork. Metadata/IDs are distinct from media bytes. New archive export schema must reject forbidden payloads and test this invariant.
- **No credentials of any form in new backups, and no toggle**: passwords, tokens, keys, cookies, sessions, authentication material, secret-bearing URLs. Enforce with positive allowlisted schemas, sanitisation and prepublication validation/tests. No absolute guarantee that arbitrary user-entered free text cannot contain a secret; identify and mitigate this risk explicitly.
- **Filenames, local file paths, folder/network locations and physical locators must be user-optional** with a prominent privacy warning. Proposed default Off, awaiting owner's explicit confirmation. Off must apply across all categories and embedded fields, not just media DB. Some physical-file matches/bookmarks/sources cannot restore without locations; explain dependencies without silently enabling disclosure.
- Title IDs, history and row/list membership may still disclose interests; category descriptions must be truthful.

## Watch history
Owner approves **Watch history On by default** (supersedes audit report 06 recommendation Off). Off must show warning that watched states and related personalised behaviour may not restore. Aim for generic title/episode provider identity plus watched/completed state, reliable watched count if actually tracked, and positive in-progress playback offset. Source audit has not established reliable lifetime play counts; do not fabricate. Keep resume/positions logically separable for clarity/privacy, and avoid physical file paths unless location opt-in. Design missing-provider-ID and unresolved media behaviour.

## User collections / saved lists
Owner states **Supernova has no exposed Collections feature** and asks what list definitions/memberships mean. Source audit lists `user_collection`, `user_collection_item`, `list_table`, `video_list_table`, but tables alone do not prove a supported visible feature. Do **not** market or present separate Collections/Saved Lists switches until current UI/use paths verified. Preserve meaningful legacy compatibility only after investigation; avoid falsely claiming feature support.

## Library database / metadata
Current 0.138 archive sanitises but retains potentially identifying paths, server names, account IDs, user labels and watch interests; sanitised does **not** mean anonymised or PII-free. Owner asks whether backing up entire metadata DB is worthwhile. Preferred design direction: purpose-built export of durable user intent, configurations, manual corrections and stable IDs; rescan/re-fetch regenerable metadata on new device, not blind full media.db restore. Exact offline/provider availability tradeoffs and manual match retention require review.

## Put.io
**Explicit owner decision:** exclude Put.io association database, file/folder/account mapping and source ownership records from the new backup design for now; revisit if/when Put.io integration is further discussed. Do not silently include these through generic library, network or streaming categories. Distinguish generic streaming preferences from Put.io-specific associations. Existing 0.138 archive behaviour unchanged until authorised implementation.

## Release scope correction
The Codex backup investigation `de3e72fcfa59562f3a14ba196985c8574ce524ce` report `08_PRIORITIES_AND_OWNER_DECISIONS.md` calls broad backup redesign post-0.139/parked. **Superseded:** owner has already INCLUDED full backup/restore redesign in Supernova **0.139**. Findings remain source evidence; release scheduling in that report is not authoritative.

## Pending owner/engineering decisions
- Confirm filenames/locations default Off.
- Decide whether full metadata cache is ever a selectable category (never media bytes); recommend not by default.
- Define watch history versus resume UI and count semantics; clarify whether Off impacts recommendations/Continue Watching.
- Decide category defaults and dependencies, explicit replace versus merge, legacy artwork-bearing 1.0 restore compatibility, versioned new format, restoration of unmatched identifiers, old source definitions and bookmarks with path privacy Off.
- Clarify what settings are user-configurable versus internal, and what UI features actually use legacy collection/list tables.
- Audit user-input free-text secret handling; do not claim zero risk from sanitisation.
No source code, tests, APK, merge or Codex interruption authorised by this record.

## Restore instructions document — owner requirement
The archive's `RESTORE_INSTRUCTIONS.txt` must be reviewed, corrected and maintained as part of the 0.139 backup/restore redesign. It must accurately describe **actual implemented** export and restore behaviour for the archive format/version, not aspirational features; be sufficiently detailed and accessible for nontechnical Android TV users; and include all applicable warnings and limitations. In particular cover: which data is and is not included (no media bytes/artwork/subtitles, no credentials); optional filenames/paths and their privacy implications; potentially revealing titles/history; new-device setup and reconnecting storage/network/services and Android permissions; matching/reindexing/fetching metadata and handling unmatched media; category selection and consequences of omitted categories; watch-history/resume exclusions; compatibility/version checks; replace/merge semantics and any deletion/overwrite risk; pre-restore backup recommendation; cancellation, interrupted export/import, incomplete files and verification/success criteria; limits of portability and offline use. Describe legacy archives separately where behaviour differs. Never imply that sanitisation guarantees anonymity or that all archives will restore on all versions. Ensure instructions are bundled in the generated archive, match the UI warnings, are updated alongside format changes, and are tested/checked against the actual code paths. This is a documentation/design requirement only, not permission to implement yet.
