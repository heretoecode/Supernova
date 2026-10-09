# Settings > About — approved future layout and section specifications

Status: **approved design direction / planning documentation only**. Not implemented, not a new fixes-only scope item, and not authority to modify `codex/post-4.1.7-shield-fixes`. Updated 9 October 2026 after user review of About mock-ups.

## Shared Settings layout
- Maintain the familiar left Settings navigation with **About expandable in place**. Its indented children: **App Information**, **Release Notes**, **Open-source Licences**, **Credits & Acknowledgements**, **Technical Information**. The selected child is highlighted; don't repeat an About menu in the middle panel.
- The two content regions to the right are **flexible**: either two independently useful centre/right panels or one merged, spacious area. Do not force an empty or redundant panel simply to preserve a three-panel grid. Keep consistent Supernova colours, fonts, spacing, focus and TV D-pad behaviour.
- **App Information:** merge centre and right into one area. Centre the *overall branding and information group* for symmetry, but align labels/values within that group for legibility. Use the approved Supernova identity/artwork and the exact user-facing attribution **“Based on Nova Video Player 6.4.64”**; show the real installed app version and accurate release information, never mock-up placeholders as factual. Do not invent package IDs or add a Check for Updates button. The generated image with large cosmic explosion/planet, unusual typography and an update button is **illustrative layout only, not approved branding or functionality**.

## Open-source Licences — user approved visual structure
- **Centre:** scrollable, focusable inventory of **verified, actually distributed** software libraries/components; clear selected-row focus, library name and concise role. The illustrative image's dependency names/versions must not be copied blindly.
- **Right:** selected component's identity, verified version where known, licence type, official project details, **full locally readable scrollable licence and required notices**, and a genuine scannable QR for its verified official site. No clickable external URLs, no dependence on a browser, no licence text replaced by QR. TV D-pad navigation must enter, scroll and return predictably.
- Complete release-specific runtime/native dependency and licence compliance review before shipping; see `docs/audits/ABOUT_LICENCES_ATTRIBUTION_AUDIT_2026-10-08.md` and credits ledger. The approved mock-up is a **layout reference**, not a validated bill of materials.

## Release Notes — final design decision for future implementation
- **Left:** About > Release Notes selected.
- **Centre:** vertical chronological release history with **latest/current installed release first**, followed by older genuine shipped releases descending. Each version shows its actual release label/date and an obvious TV focus/selection state. Avoid fabricated history or conflating interim QA APKs, CI snapshots and public releases.
- **Right:** the selected release's notes, readable at TV distance, with concise **app-store-style nontechnical one-line changes** and small monochrome semantic icons (not colourful emoji). Support longer release notes by vertical scrolling with persistent selected-version context.
- **Current/latest release remains expanded and directly visible by default**, with no collapse affordance on that current entry. Older releases start collapsed; OK/D-pad can expand or collapse them, and **several older entries may remain open**. Selection in the centre determines the detailed notes shown on the right; expansion in the history must not unexpectedly collapse other entries or lose focus. If only one real release exists, keep the current release readable and do not invent prior versions.
- Future Supernova pre-1.0 labels follow the approved sequence 0.1, 0.2 … 0.9, 0.10, 0.11, etc., but **display the actual installed release identity**, not a hypothetical 0.12. This does not authorise package/signing/versionCode changes.

## Technical Information — final design decision for future implementation
- **Left:** About > Technical Information selected.
- **Centre:** concise, scrollable technical categories, for example **Application & Build**, **Device & Android TV**, **Playback & Media Capabilities**, **Library & Storage**, **Diagnostics**. Include only categories backed by reliable actual data; do not present speculative capabilities as detected.
- **Right:** clearly aligned read-only values for the selected category, with long strings wrap/scroll/copy only if practical on TV. Show verified app version/build identifiers, Android/API and device identifiers at a useful non-sensitive level, available playback capabilities where detected, and relevant local library/storage/diagnostic state. Mark values **Unavailable/Unknown** where not measurable instead of guessing.
- Never display authentication secrets, API keys, tokens, passwords, secret-bearing network URLs, private account details or unredacted sensitive paths. Avoid device serials and other unnecessary unique identifiers. Diagnostic logging state may be shown; this design **does not authorise a new in-app diagnostics viewer**, data collection, or export changes. Keep deep media-file codec/path data in the existing selected-media Technical Information view, not conflated with app-wide About diagnostics.
- No update checker or external clickable links.

## Credits & Acknowledgements — next visual review
- Use the same three-column framework when useful: **centre** contributor/project/service entries; **right** verified attribution, acknowledgements and applicable QR to official website. Keep open-source software licences separate from external data/service credits; preserve NOVA/Archos lineage and exact mandatory TMDb attribution and logo. **Detailed mock-up remains to be reviewed**, not automatically approved by this document.

## QA / acceptance checkpoints
- Physical Shield D-pad navigation across left/centre/right, return/back, long text scrolling, preserved focus, and no accidental jumps.
- Correct merged App Information alignment and no duplicate menu.
- Release Notes reflects actual shipped history and current build; expands multiple historical entries without losing focus.
- Technical values are verified, readable, privacy-safe and gracefully absent when unsupported.
- Licences and credits match final APK and legally required notices; QR destinations verified and scannable.

No source code, APK, branding asset or active QA branch was modified by this design record.
