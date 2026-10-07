# Settings audit — 7 October 2026

## Scope
Static architecture/product audit of current Preview settings, legacy preference surfaces and current corrective authority. This is a development foundation, not Shield acceptance.

## Findings
- Preview currently consolidates a large inherited preference tree into modern categories: Playback, Video, Audio, Subtitles, Library & Metadata, Home, Appearance, Streaming, Network, Integrations, Advanced and About. There is still substantial legacy preference ancestry beneath that presentation.
- `PreviewSettings` dynamically creates/re-homes controls. This is useful for migration but makes ownership harder to reason about than a declarative settings model.
- A disabled Legacy bucket deliberately preserves old values. Keep it during stabilisation, but it should not become a permanent dumping ground.
- Network/library scanning appears in settings while operational scan controls also exist in Network & Files. Operational source/scan management belongs primarily in Network & Files; Settings should retain defaults/policy.
- Playback HUD/session controls and persistent defaults must remain explicitly separated. The recent Audio/Subtitles audits already establish this rule.
- Diagnostics are correctly development-oriented but currently add substantial surface area to Advanced.
- Integrations need one consistent hierarchy: parent in left rail, children indented beneath it, child configuration in centre.
- Inert/attribution-only rows should never masquerade as actionable settings.

## Recommendations
1. Define one owner for every setting: global default, library/source policy, session control, integration account, appearance, diagnostic, or legacy.
2. Move operational source actions (scan now, source management, download management) to Network & Files; leave scheduling/default policy in Settings where appropriate.
3. Create a migration/removal register for Legacy rather than adding new entries indefinitely.
4. Replace runtime re-parenting gradually with a declarative Preview settings schema once behaviour is stable.
5. Add per-category D-pad/focus/Back tests and a duplicate-setting test.
6. Keep Advanced/Diagnostics visually separated from normal user preferences.
7. Future Downloads preferences should be limited to defaults (destination, Wi-Fi/network policy if added); active transfers belong in Network & Files.

## Future-development questions
- Which legacy saved values still need migration compatibility after the later fresh-install identity transition?
- Which Network controls are policy versus immediate actions?
- Should About/diagnostics expose build evidence only in preview/development builds?
