# Supernova 0.135 — development QA notes

Development branch: `codex/supernova-0.135`. Package remains `app.supernova.player`; version 0.135, code 135. This is a QA candidate, not a published final release.

- Shared three-panel Settings and Network & Files, with consistent geometry, typography, row focus and context. One file browser also serves compact empty-library Home and compatible TV browsing/picker routes; native transport, credentials and scanning remain in use.
- Complete scrolling breadcrumbs, persisted list/grid, sorting and All Files controls. Stage recursive library roots and exclusions, review Added/Excluded/Removed, then Discard, Keep Editing or Apply & Scan. Source changes commit atomically.
- Library Health groups unmatched media, incorrect matches, incomplete metadata/artwork, unavailable files and source problems. Unavailable storage retains library IDs, metadata and playback progress; scans no longer automatically purge missing Foundation records.
- Home welcome/import states, restrained artwork treatment, contained focus, available Featured choices and independent visibility/dynamic membership/manual additions/sorting. Movies and TV keep symmetric boxed controls and existing return focus.
- Local Search improves article, punctuation and minor-typo matching and searches supported indexed original titles/people/studios. Details uses compact information and Cast/Crew text rows, permanent primary actions and existing asynchronous metadata/artwork paths.
- Four-action playback HUD: Subtitles, Audio, centred Play/Pause and More. Restart starts from zero; dismissing Continue Watching changes row membership only. Up Next uses five seconds. Watched state uses real played coverage so seeking cannot manufacture it.
- Persisted manual versions and actual native technical file badges; available display/decoder compatibility informs automatic ranking. Corrected native retriever transfer metadata and technical-cache consistency. Approved safe segment policies include verified movie outro/post-credit protection.
- One custom Library Page after TV Shows, with a five-step editor, reliable indexed filters, independent columns, preview, prefilled edit/delete and protected drafts.
- Format 1.0 nonsecret backup with instructions, private staging, revalidation and durable rollback. Credential databases/private OAuth envelopes are excluded; database copies and preferences are sanitized and services require sign-in after restore. Export preserves the live library and authentication.
- Diagnostics retains bounded redacted local capture/export; turning logging off offers Keep/Delete/Cancel. My Providers retains catalogue colours and language marks use locale-aware fallback.

Full native unsigned conformance passed checkpoint `1039278`; later hardening requires its own complete run. Physical NVIDIA Shield navigation, network/removable storage, playback/subtitles/HDR/audio, animations and signed 0.134 upgrade remain acceptance gates. Credentialed provider coverage/quotas and frame-only HDR10+ metadata are not claimed verified. See REQUIREMENTS.md, TEST_RESULTS.json and QA_READINESS.json for the exact candidate evidence and outstanding gates.
