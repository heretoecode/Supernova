# Supernova 0.135 — Custom Library Page scope approval

Date: 2026-10-09
Status: **APPROVED FOR 0.135 IMPLEMENTATION** (planning scope; not evidence of implementation)
Authority: Explicit user confirmation on 9 October 2026.

The complete **one custom Library Page** creation and editing feature is included in the planned **Supernova 0.135** release, building on the installed Foundation 0.134 baseline.

## Authoritative behaviour
- Top navigation **+** opens **Create a New Page**, with **Library Page** (local media) available and **Discovery Page** parked / Coming Soon.
- Exactly **one** user-created Library Page slot after Home, Movies, TV Shows. Do not offer a second page or reorder fixed pages.
- Five-step three-panel wizard: **Page Details** (name and icon); **Content Type** (Movies, TV Shows, Both); **Filters** (genre with Any/All multi-select, year, watched state, minimum rating, country, language, studio/network and collection only when reliable metadata supports them); **Display Options**; **Review**.
- Live preview on right, D-pad/Back support, and retained entries when moving backwards.
- Custom page toolbar includes **Edit** at far right, reopening prefilled wizard. Rename via Page Details.
- **Delete Page** only in edit flow, separate destructive confirmation stating library media is not deleted.
- On unsaved exit, offer **Save Page**, **Discard Changes**, **Continue Editing** when edits exist. Save validates; discard in edit restores last saved state; discard during creation drops draft.
- The previously proposed sixth **Exclusion Options** wizard step is superseded and must not return.
- Preserve the existing Supernova visual language, including canonical top navigation, font, Space Black Blend constraints, dynamic artwork where applicable, and focus/D-pad conventions.

## Approved original visual references
- [Library creation options](../design/approved-mockups/custom-library-page/library-creation-options-approved.png)
- [Default landing page](../design/approved-mockups/custom-library-page/default-landing-page-approved.png)

These two files are preserved original image assets. They do **not** constitute proof that every wizard-step mock-up has been recovered. Do not invent missing mock-ups or mistake incidental generated UI for approved behaviour. Later explicit written approvals supersede conflicts in reference images.

## Related authoritative specification
- [Recent product decisions — custom Library Page](../design/RECENT_PRODUCT_DECISIONS_2026-10-08.md)
- [Movies and TV toolbar](../design/MOVIES_TV.md)

## Execution boundary
This approval adds the feature to **0.135 planning scope only**. It does **not** authorise changes to protected older release branches, a merge into main, or any premature code implementation. No other parked features are activated by this decision. The full 0.135 handover, tests and execution checkpoint plan still need reconciliation.
