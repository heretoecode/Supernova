# Settings — Design and Behaviour Authority

Status: **APPROVED foundation; category-by-category redesign is not implicitly authorised**

## Shell
Three panels:
**Left Categories → Middle Settings/options → Right contextual explanation/current value**.
Dark/translucent Supernova styling, white type, monochrome icons, compact blue focus boundary/glow.

Permanent non-scrolling left rail with all top-level categories visible and About at bottom:
Playback · Video · Audio · Subtitles · Library & Metadata · Home · Appearance · Streaming · Network · Integrations · Advanced · About.

Entry focuses **Playback in the left rail**, not the first middle setting. Focusing a category never expands it. Indented children do not appear merely on hover. OK/Select or RIGHT explicitly enters category/subcategory. Nested subcategories stay in the middle workspace, never the permanent left rail.
Deterministic routing: rail UP/DOWN one category; RIGHT first appropriate middle; LEFT returns origin; middle UP/DOWN; RIGHT enters meaningful value/control; Back reverses hierarchy; closing a choice panel restores the exact setting.

Preserve existing preference keys, values and semantics unless an explicit requirement changes them. Compatibility controls remain Advanced; normal source scheduling belongs Network & Files. Trakt, OpenSubtitles and the real IntroDB switch belong Integrations.

## Providers
My Providers uses genuine provider catalogue artwork. Selected and alphabetical alternatives are distinct sections. Selection updates tick state in place without flashing/focus movement; regroup when reopened rather than reordering underneath the user.
Current Shield corrections: provider logos should display in colour in My Providers; **Done** must be visually separated appropriately.

## Language
Use a national flag only for a true locale such as en-GB. Generic language uses neutral language iconography. This rule applies consistently to UI language, subtitle reading language and playback subtitle-track selection.

## Current correction
Shield QA requires the intended three-panel structure and nested/indented navigation to be corrected where physical presentation diverges.

## Evidence
`docs/preview-4.1.7/REQUIREMENTS.md` UI-058–060 and UI-009; 4.1.2 audit/handover; 4.1.4 release notes; current Shield QA.


## Superseded historical category structure
An older 13-category foundation (General; Home & Discovery; Playback; Video & Audio; Subtitles; Library; Sources & Storage; Appearance; Trakt; Streaming; Integrations; Advanced; About) is **SUPERSEDED** by the current 12-category rail above. Do not restore it by accident. Historical “Remember library view preferences” semantics remain valid: Grid/List, Filters, Sort and Order are remembered separately for Movies and TV Shows.

See `docs/project/HISTORY_RECONCILIATION.md` for the historical lineage.

## 9 October 2026 — observed Advanced / Diagnostics focus defect (video evidence)
**Physical user video:** `IMG_4760.mp4` (20.7 s, portrait recording of Shield TV). Video reviewed in chat; **not committed to GitHub**, so retain this written observation rather than claiming the video is in repo. Hovering Advanced shows Diagnostic Logging, Diagnostic detail, Issues Digest, Report a Problem, Show Latest Reference and Export Diagnostic Report in the middle workspace. OK/RIGHT on Advanced expands Video Compatibility, Audio Compatibility, Network Compatibility, Subtitle Compatibility in an indented rail and moves focus there. There is no obvious usable path to the displayed diagnostic controls; Export Diagnostic Report is effectively inaccessible from this route.

**User-approved correction, targeted rather than wider Settings redesign:** merely highlighting Advanced shows a short **Advanced settings** introduction/caution in centre and supporting explanation on right; **do not** show diagnostics as the default Advanced landing page. OK/RIGHT on Advanced explicitly expands the nested rail; include **Diagnostics** as a peer of Video Compatibility, Audio Compatibility, Network Compatibility and Subtitle Compatibility. OK/RIGHT on Diagnostics opens the existing diagnostic controls in the centre, with contextual explanation on right. Other subcategories show their own controls in centre/right. LEFT/Back returns to the originating child or parent without lost focus; returning from export restores Diagnostics focus. Keep Diagnostic Logging and **Export Diagnostic Report** fully functional, accessible by D-pad; do not remove or rename existing preference keys/actions. Advanced entry must not silently jump into the first compatibility child. This is a targeted newly observed defect and future corrective specification, **not permission to modify the active fixes-only branch**.

**Read-only source corroboration on main:** `src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java` lines 49–52 append diagnostic preferences directly to Advanced; lines 72–78 create nested compatibility groups; lines 191–194 initially make direct child preferences visible but hide nested categories; lines 203–224 on entry build a child list only from `PreferenceCategory` items, and focus its first child. This structure explains the observed mismatch: diagnostics appear in the parent workspace but are bypassed by the child-navigation path. Verify against the exact installed APK/branch before claiming a fix. **Acceptance:** Advanced landing intro; all five indented sections accessible; Diagnostics selectable; all six diagnostic actions reachable; export launches system picker; reliable focus/Back paths and no regressions to compatibility preferences.
