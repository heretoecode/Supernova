# 0.137 Shield QA — Settings Open Source Licenses and Network & Files

**Status:** User observations, 2026-10-12; video attachments IMG_4895.mp4 and IMG_4897.mp4 received in conversation, **not yet visually analysed**. Do not upload original recordings or raw logs to GitHub/Codex without explicit permission. **No fixes implemented.**

## Diagnostics and Settings
- Diagnostics now accessible at Settings > Advanced > Diagnostics; diagnostic logging is **ON** and user reports it working. Previously unwanted diagnostic level selector is gone as agreed. Preserve both.
- User plans a later detailed setting-by-setting review. Existing left Settings list is navigable but bottom About category clipped; when About expanded, bottom indented **Technical Information** entry also clipped. Fix bottom clipping/scroll-to-visible for both parent and nested items at relevant viewport sizes.
- Technical Information loads centre panel and D-pad focus behaves correctly, no crash observed. Credits & Acknowledgements works; formatting needs refinement. Release Notes no longer incorrectly appears as indented item in centre panel; notes display on right as centre panel scrolls. Preserve improvements; polish formatting.
- **Open Source Licenses reproducible crash/freeze:** focusing left-panel Open Source Licenses alone is safe. Right panel displays title and instruction to press OK for locally bundled notices. User reports event at **02:36 local time, 2026-10-12 (Ireland)**: pressing OK opens notices, first entry **Android X Runtime Software** receives focus, focus cannot move, app then crashes. Audit stack trace/diagnostics around 02:36 and focus/dialog/list lifecycle; reproduce exact sequence. Do not claim root cause before logs/source investigation.

## Network & Files
- **Severe D-pad focus regression:** while navigating local storage devices vertically in left panel, pressing Down repeatedly sends focus unexpectedly to **centre-panel breadcrumb/address row**. User must repeatedly press Left to return and cannot traverse list normally. Fix focus traversal and restoration so Up/Down stay within intended left-panel device list, and Left/Right intentionally change panels.
- Grid View, Sort, All Files toolbar buttons should use approved global Supernova button visual design including clear unfocused/resting state and focused treatment; preserve actual actions.
- **All Files control appears nonfunctional:** user cannot toggle ON/OFF; no distinct selected state or visible content/filter change. Audit actual filtering/visibility logic and button state, ensure functional toggle, persisted state if applicable, and visual feedback. Do not assert a specific file should appear without inspecting current folder contents and policy.
- **Overview centre panel** appears to focus a block of informational scan text; text above extends outside box and text below is cut off. Audit layout, clipping, scrolling/overflow, and focus. Overview scan information is read-only: **centre information content should not be focusable**; preserve meaningful navigation on controls elsewhere.

## QA/evidence requirements
- Inspect both user-supplied videos locally when available; document timestamps and observations without sharing media externally. Ask for screenshots/video only where current evidence insufficient.
- Preserve confirmed improvements, audit 0.137 diagnostics and reproduction, validate actual Shield D-pad behaviour and crash-free license navigation.
- Prior Settings density/inlay requirements in QA_0137_SETTINGS_CATEGORY_DENSITY_CLIPPING_AND_CENTRE_PANEL_SURFACE_2026-10-12.md still apply.
- These are candidate additions to 0.138 approved work, with high-priority crash and severe navigation regression. User has not requested starting coding in this message.
