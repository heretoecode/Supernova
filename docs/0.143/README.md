# Supernova 0.143 — Settings and Details test build

Owner authorisation: 27 October 2026, full Settings implementation and next test APK. No main merge or public release is authorised. The earlier design documents' investigation-only statements describe their historical authorisation state.

## Source and preservation

Development branch: `codex/supernova-0.143-implementation-2026-10-27`, created from clean PR #22 head `0cf393c50c6da316985c036c9be5b61667564e97`. PR #22 remains an ancestor, including Details implementation `bd36ca73`. The established 0.142 development tip `48e9cd6fbff47c120c013d5017f30cb3f3219030` and its tested source `b347707defdbf2ab3ae83d920ba9fa7af3936220` remain ancestors. PR #21's approved blueprint, mapping, audit and technical specification were copied selectively; its older app/release files were not merged. Existing branches, PRs and main remain intact. Checkout was clean before this work.

Version `0.143`, code `143`, follows `assets/foundation/release-history.json`, the build's version authority. The previous current entry was moved into history with its existing signed-delivery proof attached. Application ID remains `app.supernova.player`; the existing certificate pin remains `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`.

## Implementation

A separate Settings workspace replaces the visible inherited hierarchy. Its eight-card, weighted 4×2 landing page opens category-specific subsection rails and right-hand content. The original preference hierarchy remains the live model: original listeners, typed storage, runtime option suppliers, capability/account gates and action handlers still own behaviour. The new view holds navigation anchors, never duplicate saved settings. Controls and observers reconcile in place and detach with the view lifecycle.

`assets/foundation/settings-map.json` accounts for all 165 audit IDs and records each disposition. Twenty-three review/non-exposed definitions stay hidden; inverse subtitle state, legacy sponsor and backup compatibility definitions are model-only; Network & Files owns credentials/scheduling. All 26 backup categories use the existing archive engine and consent rules. About routes into the existing Foundation disclosures instead of duplicating old legal preferences. No preference migration or reset is introduced.

Shared presentation includes native Boolean toggles, compact single-choice radio selectors with a stationary Cancel footer, stable multi-select lists, existing text/account forms and retained action/confirmation flows. Service masters retain credentials/policies and reconcile child availability after re-enabling. Subtitles by Default now invokes its inverse native listener before acceptance; language availability updates immediately. Provider ticks retain row identity/order/focus. Advanced-unlock reparenting safely removes an existing parent before native attachment. Phone-only controls honour the existing TV capability gate.

Backup occupies the right content column; its existing nested operation dialogs and Android document pickers remain. About catalogues open their readers within that same column. Back unwinds modal/nested state before returning to the original category card. Grid arrows do not wrap. Subsections and content scroll independently; selected subsection and focused row anchors survive normal navigation and saved-instance restoration.

## Assets and design

The [verified asset manifest](../investigations/handover-2026-10-27/ASSET_MANIFEST.md) remains applicable: unchanged official Space Black Blend PNG and packaged background, existing platform sans-serif-light family with Android fallback and existing saved accent. No font/background was generated or downloaded. Two missing node/gear glyphs and radio indicators use the existing procedural vector stroke system. Grid geometry uses weighted columns and dp/sp, without focus zoom. At 150% accessibility text scale the grid may scroll to keep every description readable; normal 16:9 views retain all eight visible cards. Physical overscan/font/focus calibration is pending.

## Details retained

PR #22 is preserved: Language globe removed, accessible Cast/Crew spacing, incremental/stale-safe metadata and artwork loading, bounded on-demand TMDB written reviews using the existing public supported source. No credentials or paid source were added. See [Details implementation and QA](../details-2026-10-27/).

## Validation and delivery

Signed test build **0.143 / 143** is delivered: [Supernova_0.143.apk](https://drive.google.com/file/d/1b02zx7zlseN5eJQNqol1OlTp0CejPAU0/view). Frozen APK source: `7b826443363af00b236e5b6af309046456d719df`. SHA-256: `7885f505eba0d6d317e9d10178d7cd2853e8ba0b00d9dabfbe8648f14692ec55`. The existing certificate, identity and full Drive APK readback are independently verified. All nine older APKs and all ten historical HTML release articles are preserved. [Release notes](https://drive.google.com/file/d/17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW/view) were updated in place.

[Delivery receipt](SIGNED_QA_DELIVERY.json), [signed conformance proof](SIGNED_PUBLIC_QA_EVIDENCE.json), [independent final-source conformance](CONFORMANCE_40575357794.json) and [test coverage](TESTING.md) record exact sources/artifacts. Both complete final-source GitHub jobs passed. See [Shield QA requirements](SHIELD_QA.md); physical acceptance and signed upgrade are **NOT RUN**. Software renders are Robolectric native renders, never physical Shield screenshots. The existing manual protected signing/conformance process is adapted to this version; postponed GitHub-to-Drive publisher automation is not enabled. No alternative signing key is permitted.
