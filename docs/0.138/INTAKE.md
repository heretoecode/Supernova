# Supernova 0.138 continuation audit

Intake 2026-10-12. Clean fresh checkout; no AGENTS.md or local active coding work found.
PR #12 remains open, draft, unmerged, head `45521c370563c8c176b0c3d9435fbf08fcd115e0`, base `91a502a7355c637b2630990e15a2582aa96a0329`.
The change after tested `68caa2c0ac3c42073b8ed2168995c2316625b29a` is release-note documentation only.
Candidate branch `codex/supernova-0.138-qa-2026-10-12` starts at the complete PR #12 head. Main and 0.137 history are preserved.
Approved documentation is inspected independently at `a591034c` in `docs/nova-upstream-deep-audit-2026-10-11`; provisional approval wording in older notes is superseded by the full handover and current user instruction.

Package `app.supernova.player`; certificate SHA-256 `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`. Pinned module/runtime/native revisions and protected signing gates are retained. No replacement key, debug Foundation build, merge, APK distribution or private evidence upload is authorised.

The workspace has no attached Shield, Android emulator, user diagnostic export or original QA media. The 23:22 event and 02:36 Ireland licence failure cannot be correlated with private diagnostics here. Automated reproduction and local synthetic renders can establish source regressions, not physical Shield acceptance. Record these distinctions in conformance.

## Implementation checkpoints

1. Home membership/progression, full-collection sorting, shared metadata, hero focus/readability.
2. Shared Details Card and compact non-focusable information; shared Library implementation and Filters routing/return.
3. Search, Settings/licence reader, Network & Files focus/filter/overview regressions.
4. Preparing Playback, HUD layout/timing/font, persistent quota-aware subtitle discovery and season policy.
5. Exact-source tests, lint, unsigned APK/native conformance, local renders and full per-requirement Shield checklist.

## Approved inputs (SHA-256)

- `docs/project/APPROVED_SUPERNOVA_0138_CONSOLIDATED_CODEX_IMPLEMENTATION_SCOPE_2026-10-12.md`: `38def4ffef558b0537d6a808d306bad9af3cd36c3a323c6fdc79b94e4014d3bc`
- `docs/project/GLOBAL_DETAILS_CARD_UPPER_PAGE_DESIGN_2026-10-12.md`: `5a1cd64f56fe30134df0b92ef6a308ee1fc3944636d49621ba0a37631c384e4b`
- `docs/project/NEXT_RELEASE_LOWER_DETAILS_COMPACT_NONSCROLLABLE_AND_GLOBAL_BUTTONS_2026-10-12.md`: `9de55c01badc86fcf58b4c6fa73d909772622e9f462205f268c27a7468dab1f4`
- `docs/project/NEXT_RELEASE_CAST_CREW_EIGHT_ITEM_CAP_2026-10-12.md`: `5a929b72230005622e592c040c6569154e653398fb20fa4a1d2f163e200f6042`
- `docs/project/GLOBAL_HERO_METADATA_STANDARD_AND_SUBTITLE_DISCOVERY_DISCUSSION_2026-10-12.md`: `2960e7cc4b4cd45676500802ecabbbde81e65f264f6ea80b05eeb18f579f4813`
- `docs/project/QA_CONTINUE_WATCHING_NEXT_EPISODE_AND_SEASON_SUBTITLE_PREFETCH_2026-10-12.md`: `dce084d8df1c676544dcdbc6c5c1969ed5676bef05da0c9f52d3b3c8109accca`
- `docs/project/QA_0137_HOME_CONTINUE_WATCHING_LIBRARY_HEALTH_BANNER_2026-10-11.md`: `ff098ca7e99d5580d58edb07134ccb1086fb8233d13759f9868d42475184f3d0`
- `docs/project/QA_0137_HERO_HOME_ROWS_CUSTOM_MIXED_CONTENT_2026-10-11.md`: `e2fc57466d7b1c6e94cee9744b2d4a6ad980ca2f57b53f7d0c9d620a58e20e9c`
- `docs/project/QA_0137_HERO_ANIMATION_METADATA_AND_CONTENT_MIX_DISCUSSION_2026-10-12.md`: `fae3c9dd1d6ccc9888b082232411f25fbacbacb1a4199e9a47e581723dc6c64b`
- `docs/project/QA_0137_GLOBAL_BUTTON_DESIGN_STANDARD_2026-10-11.md`: `541ce2a528acd3e436eb94ad58f43ef3724c244b8628b2747b0ec0b7bfdafeeb`
- `docs/project/GLOBAL_LIBRARY_PAGE_SHARED_DESIGN_RULE_2026-10-12.md`: `d88cffd036db245b69a364f5b408dff38aa6414168fb50a550da786e453a91e1`
- `docs/project/QA_0137_SEARCH_KEYBOARD_DENSITY_AND_INFORMATION_LAYOUT_2026-10-12.md`: `c49eaf2af997875ab468d3173d228bca41f6990e811905e190ee8e0a4871d9dd`
- `docs/project/QA_0137_SETTINGS_CATEGORY_DENSITY_CLIPPING_AND_CENTRE_PANEL_SURFACE_2026-10-12.md`: `7284b35791d8aa6b89eec62a20f47e7f26b83f85b1cad23fac810d05877c5697`
- `docs/project/QA_0137_SETTINGS_LICENSES_AND_NETWORK_FILES_FOCUS_2026-10-12.md`: `dbfbfa24c241221f0768ba7f0e6a6dfbe18be85a5d85bc38e6aa9f148dc65141`
- `docs/project/QA_0137_TV_AIR_DATE_SORT_AND_PREPARING_PLAYBACK_TAGLINE_2026-10-12.md`: `b9ff0fb0afb854cb1b6040e18eb5048eb63ab5161267ac16acb7b74a7d377c2f`
- `docs/project/QA_0137_PLAYBACK_HUD_SEEKBAR_FOCUS_AND_LAYOUT_2026-10-12.md`: `e4f8643fc09e52b14e7bb36ad5376f8681536aefae87d2edaa28b75bf3555fcc`
- `docs/project/QA_0137_DIAGNOSTICS_1122_PRE_HANDOVER_2026-10-11.md`: `924ab4cef803d38bb99fd80809615587552621e157675a124ce3e321952c9ecb`
