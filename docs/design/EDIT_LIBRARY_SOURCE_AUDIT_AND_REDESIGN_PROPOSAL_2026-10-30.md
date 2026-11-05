## Status and evidence boundary

**Investigation and proposed information architecture only. Design approval pending; no redesign implemented.** This report follows [the requested brief at its inspected SHA](https://github.com/heretoecode/Supernova/blob/9f8982433fad47cf0e4c8ad2c0ee5002da588973/docs/design/NEXT_RELEASE_EDIT_LIBRARY_FULL_AUDIT_AND_REDESIGN_2026-10-30.md).

Application baseline: delivered 0.147 frozen source `f4abf7cb880243ba00481f82beee19d20bc72e33`, inspected through draft PR #27 head `b4a6ee7a010c9b5b6af8731108f3973477bfc7f5`. A local Git diff found no differences in `src/main`, `res`, or `AndroidManifest.xml` between those two revisions. Main (`75d9f012fa4f65869eef5d972ad4725df504bd9c`) is older and is **not** substituted for the delivered application. PR #27 remains open/draft, stacked on 0.146.

Evidence labels below:
- **Source verified** means traced in this frozen source, not executed on hardware.
- **Reported Shield** means the existing owner evidence documented in GitHub; original clips were not available in this workspace and were not re-reviewed or uploaded here.
- **Hypothesis / pending** means runtime evidence is still needed.

No application changes, updates to existing remote branches, merges, tags, builds, signing, release changes, or user-data operations were performed. Inspection used a fresh clone and a detached local worktree. Publication adds only this report on a new documentation branch and a draft PR targeting the supplied documentation branch (GitHub Issues are disabled). Signed 0.147 and existing work remain untouched.

## 1. Reused maintenance mapping and concrete gaps

This extends the existing map rather than claiming a new whole-app inventory.

| Existing record | Reused coverage | Gap filled here |
|---|---|---|
| [02_CODEBASE_ARCHITECTURE.md](https://github.com/heretoecode/Supernova/blob/eac3e504490a92dd9f43252649264657e485982c/docs/maintenance-investigation/02_CODEBASE_ARCHITECTURE.md), “Movies / TV / mixed libraries” | Loader → shared renderer → CustomLibraryPage/Activity; provider data and per-page preferences; custom configuration differs from user collections | Current editor entry routes, exact controls, draft/exit and render ownership |
| [07_LIBRARIES_AND_METADATA.md](https://github.com/heretoecode/Supernova/blob/eac3e504490a92dd9f43252649264657e485982c/docs/maintenance-investigation/07_LIBRARIES_AND_METADATA.md), “Custom Library page” | Existing configuration/editor and shared library renderer; snapshot, history, artwork/scan dependencies | Field constraints, hidden retained fields, preview and save behavior |
| [09_UI_ATLAS_AND_NAVIGATION.md](https://github.com/heretoecode/Supernova/blob/eac3e504490a92dd9f43252649264657e485982c/docs/maintenance-investigation/09_UI_ATLAS_AND_NAVIGATION.md), “Custom library/page guide” | Create/edit/filter, Save/Cancel and opener restoration; explicitly incomplete runtime atlas | Actual focus graph, stale references, dirty-exit destination loss |
| [10_DESIGN_SYSTEM_AND_SHARED_COMPONENTS.md](https://github.com/heretoecode/Supernova/blob/eac3e504490a92dd9f43252649264657e485982c/docs/maintenance-investigation/10_DESIGN_SYSTEM_AND_SHARED_COMPONENTS.md) | SharedThreePanel, TopNavigation, PreviewDialog/Keyboard, shared library components and responsive viewport | Exact editor overrides that create competing backgrounds and scroll/focus ownership |
| [12_CLEANUP_AND_OPTIMISATION_REGISTER.md](https://github.com/heretoecode/Supernova/blob/eac3e504490a92dd9f43252649264657e485982c/docs/maintenance-investigation/12_CLEANUP_AND_OPTIMISATION_REGISTER.md), C11/C13/C14 | Lifecycle ownership, semantic components and backdrop cost need evidence | Editor-specific rebuild, layering and measurement checkpoints |
| Maintenance evidence: [navigation-static-branches.tsv](https://github.com/heretoecode/Supernova/blob/eac3e504490a92dd9f43252649264657e485982c/docs/maintenance-investigation/evidence/navigation-static-branches.tsv#L102) | Already records the problematic dirty-exit branch | Confirmed still present at 0.147; not a newly introduced regression |
| [refinement register §§2, 16–20](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/docs/project/SUPERNOVA_POST_0.135_REFINEMENT_REGISTER_2026-10-10.md#L176) | First-use vs edit, five steps, deletion, preview, no icon/Collections picker, Back/dirty-exit/opener requirements | Requirements-to-current-source delta below |

The maintenance report’s underlying frozen source was `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. Comparison with current source shows **CustomLibraryPage unchanged**. CustomLibraryActivity changed background/scrim/panel treatment and language display/icon handling; the dirty-exit callback and vertical footer persist. The 0.145 maintenance branch also has no changes to these two files relative to current head. This is a focused gap audit, not a repeat of completed maintenance mapping.

Historical requirements contain superseded design-only wording. Read the refinement register and October 30 instructions as later authority. In particular: icon selection and Collections picker were removed; the stored compatibility fields remain. The earlier dimmed library overlay and latest single Space Black background direction need reconciliation in the proposal, not silent interpretation as already approved geometry.

**0.147 FD-003 is Network & Files traceability, not an Edit Library feature map.** Its test totals/physical checklist do not establish editor acceptance.

## 2. Actual routes, state and data ownership

Source: [CustomLibraryActivity.java:14](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L14), [TopNavigation.java:74](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/src/main/java/com/archos/mediacenter/video/leanback/TopNavigation.java#L74), [PreviewPages.java:507](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L507).

1. Shared top navigation custom slot (index 6, visually after TV) opens CustomLibraryActivity. Empty slot is “+”; occupied slot uses saved text name. Pressing the current custom tab within that activity is a no-op.
2. No valid saved page → first-use introduction and five-step wizard immediately. Saved page → shared PreviewPages library. Its custom-only **Edit Library** toolbar action opens step 0 prefilled. There is one persisted slot, not multiple custom libraries.
3. CustomLibraryActivity also has a launch route from PreviewSeasonActivity’s custom navigation. Its manifest declaration is non-exported. The shared shell supplies the ordinary custom entry from other screens; there is no independent built-in Movies/TV editor.
4. Draft is a decoded copy of saved JSON; original is its encoded baseline. Steps, editing flag, draft, original, focus tag and a legacy scroll parcel are saved in Activity instance state. No ViewModel owns the editor: Activity + inner Workspace own state and views.
5. Default SharedPreferences key `supernova_custom_library_page` holds one JSON object. Validation precedes synchronous `commit()`; save trims name, reports failure, and keeps editor open on validation/write failure. Dirty detection compares encoded strings.
6. Filtering reads existing Snapshot movies/shows, episodes and continuing-show identities. It does not create media, rescan sources, change metadata matches, reorder Home rows, alter watched state or modify file inclusion.
7. The Activity seeds from memory cache then runs one PreviewLibraryLoader load on a single-thread executor. On success it updates snapshot and preview, rendering the library only if not editing. On failure it records diagnostics only: **no editor loading/error/retry status**. On destroy it sets closed and shuts down the worker. The success callback checks closed. Preview is synchronous local filtering/summary text, not a poster-generation worker.
8. [PortableCustomPage](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/src/main/java/com/archos/mediacenter/video/utils/backup/PortableCustomPage.java) exports this definition in schema 1, translating stored `collection` to portable `franchise`; restore reverses it. Preserve this compatibility, plus separate toolbar/column preferences. Do not confuse this with legacy user_collection tables.

## 3. Complete current editor parity and proposed destinations

All rows are source verified. “Preserve” means required parity for a future approved implementation, not a hardware pass.

| Current location / function | Actual choices, rules and dependencies | Proposed destination / acceptance |
|---|---|---|
| Left rail | Page Details, Content Type, Filters, Display Options, Review; focus changes the active step; click also selects | Same five named sections. Keep direct access, explicit Previous/Next and stable selected-step identity |
| Page Details: Name | Shared text input, 40-character input limit; validation requires trimmed 1–40 characters | Label/value name field; preserve keyboard Done/Cancel and draft until Save |
| Page Details: Delete Page | Existing pages only; confirmation says media/history/progress remain; preference removal on confirmed success; failure dialog; activity finishes after success | Separated destructive area only in Page Details; Cancel initial focus; deletion must never delete media or related stores |
| Content Type | Direct Movies / TV Shows / Both choices | Visible mutually exclusive choices with current selection distinct from D-pad focus |
| Filters: Genres | Actual genres from both snapshot lists; multi-select; All Genres reset; immediate draft callback, no Done | Same data-backed picker and draft semantics; keep selections even if metadata temporarily absent |
| Genre matching | Any / All toggle; empty set means no genre restriction | Adjacent to genres; picker heading must reflect actual Any/All (current shared chooser always says “Match Any Selected”) |
| Year | All (0), or one year from current snapshot, descending year choices | Preserve single-year rule; do not silently turn it into toolbar multi-year filtering |
| Watched state | All / Watched / Unwatched / In Progress | Preserve rules: TV Watched requires nonzero indexed episode count and complete-series watched status; progress uses resume or continuing-show identity |
| Minimum rating | Any / 5 / 6 / 7 / 8 / 9; model accepts 0–10; unknown/nonpositive ratings fail a positive threshold | Same available choices and honest unknown handling; preserve imported valid values outside picker list |
| Original Language | Conditional row if values exist or saved selection exists; All + metadata tags excluding “und”; global language icons; exact tag match in model | Same conditional facet; retain selected value on empty/unavailable snapshot; consistent locale/icon handling |
| Studio / Network | Conditional All + parsed metadata values; retains selected nonempty value | Same facet, no invented provider/account controls |
| Display Options: View | Grid / List | Same saved page default, shared renderer |
| Sort | Title / Year / Date Added | Preserve comparator behavior: year is integer year, added timestamp, title case-insensitive; descending reverses primary order, stable key breaks ties |
| Direction | Ascending / Descending | Adjacent sort control |
| Review | Labelled text for name, type, genres/matching, year, watched, rating, language, studio, display, sort/direction | Group into Page / Content & Filters / Display. Human-readable values; keep long content scrollable |
| Live Preview | Draft name, indexed count/size/local/network summary, up to first 20 display names in draft order; mixed pages combine movie/show summaries | Same truthful count and textual list; explicit “showing up to 20” cue; pinned heading and stationary all-side focus boundary |
| Footer | Step 0: Next + Cancel/Close; steps 1–3: Previous + Next + Cancel/Close; Review: Previous + Save Page + Cancel/Close | Persistent horizontal action row where it fits; order and edge navigation defined; accessible wrap/overflow at large text |
| Save Page | Review action; also dirty-exit option; validates/commits, updates nav name, closes editor, recreates shared library, tries Edit focus | Preserve data semantics; validation/write errors keep draft and restore meaningful focus |
| Back / Cancel / Close | Picker Back returns to section; preview Back → remembered centre; centre Back → selected rail; rail Back requests exit | Same hierarchy with valid attached fallback and coherent current section |
| Dirty-exit modal | Continue Editing initial focus, Save Page, Discard Changes; no prompt when encoded draft unchanged | Preserve all three choices; correct destination handling below |
| Saved compatibility fields | `icon` retained/validated but no icon picker; `collection` retained and actively filters matches but no Collections picker or review row | Preserve round-trip/filter semantics; no new editable fields without approval. Hidden collection can explain unexpectedly restricted results; surface read-only context if approved |

No country filter, arbitrary item membership editor, page visibility/order control, source association editor, scan/exclusion control, artwork picker, second page, or Discovery page exists in this wizard. Home row ordering/visibility and library list-column controls belong to separate existing features; do not invent them here.

### Functions outside the wizard that must survive

PreviewPages is already the shared library renderer. It supplies title/statistics, cards/list, Details opening, toolbar Filters/Sort/Order/View, list Columns and far-right Edit Library. Page rules first constrain its source; toolbar genres/years/providers further filter that source. Toolbar state is separate (`preview_custom_genres/years/providers` and `custom_` column namespace). It must not be merged into the wizard’s definition or reset by redesign.

Toolbar view/sort/direction writes back to the custom definition. Toolbar exposes additional shared sorting choices (including date/rank/column sorts) beyond the wizard’s three stored sort enums; persistence maps to title/year/added. This asymmetry is existing behavior requiring explicit parity decisions, not license to drop choices.

Artwork still comes from existing media and updates TopNavigation. Scan/source links and underlying media identities are indirect read dependencies, not editable associations. Preserve them unchanged. Backup and restore must retain both definition and separately owned preferences.

## 4. Findings and root causes

### EL-01 — competing background layers / safe area (source verified; visual impact Reported Shield)

The shared shell selects dynamic PreviewBackdrop for custom tab 6; utility background switching applies only to indices 3–5. Editor rendering then mounts the saved library, blocks descendants, overlays a near-opaque `0xf70b121d` scrim, adds an “Edit Library” title, and replaces each SharedThreePanel background with another nearly opaque gradient (`0xf0212c39 → 0xf00b121d`, permanent stroke). The shell still owns its scene-sampled navigation shade.

Important execution-order detail: render initially sets Workspace padding/background, but the saved-page `library()` call clears that background and resets padding to zero. First-use retains the Workspace treatment. Thus create/edit have different effective insets and layer stacks despite sharing most construction code. The scrim covers the stage, not the global navigation. It is dimming, **not** the former API-31-only blur.

Compared with maintenance baseline, the previous alpha/API31 blur was replaced with this scrim and denser panel backgrounds. Multiple independently owned treatments are a concrete structural cause of discontinuity; changing one color cannot establish one coherent scene. The exact visual contribution of the navigation sampler still needs capture.

Sources: [CustomLibraryActivity.java:39](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L39), [CustomLibraryActivity.java:77](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L77), [SharedThreePanel.java:48](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/src/main/java/com/archos/mediacenter/video/leanback/SharedThreePanel.java#L48), [TopNavigation.java:174](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/src/main/java/com/archos/mediacenter/video/leanback/TopNavigation.java#L174).

### EL-02 — incomplete Live Preview focus outline (reported defect; source mechanism identified, exact clipping cause pending)

The focus StateListDrawable is the **foreground of the ScrollView itself**. Its heading and summary/list are children of the scrolling content. Shared panel geometry uses 16dp radius, while focus factory uses 10dp; the same surface also has a permanent 1dp border. No dedicated stationary outer focus frame/inset is defined. Up/Down explicitly scrolls by 72dp and consumes terminal keys.

This couples focus decoration to scrolling/foreground drawing and makes the heading scroll too. A scroll-coordinate/bounds or ancestor-clipping interaction is a plausible cause of the stronger top/missing bottom boundary. Source alone does not prove which pixels were clipped on Shield. Capture foreground bounds, scrollY, parent viewport/clip rectangles, panel measured bounds and all four corners at top/middle/end. Do not mark resolved by a thicker stroke or software-only screenshot.

Source: [CustomLibraryActivity.java:42](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L42), [CustomLibraryActivity.java:96](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L96), [SharedThreePanel.java:127](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/src/main/java/com/archos/mediacenter/video/leanback/SharedThreePanel.java#L127).

### EL-03 — vertically stacked actions and dense controls (source verified)

`footer=column()` explicitly creates a VERTICAL LinearLayout; footerAction adds wrap-width, fixed 40dp-height buttons with top margins. This is why paired navigation/close controls stack. The footer is already outside the weighted options ScrollView (a good existing property), but three stacked actions consume centre height. Options and rail rows are fixed 42dp; review creates a divider after every line; fixed intro/title offsets and near-full-height panels compound pressure. Large text/long labels can outgrow these fixed heights. Default widths .24/.38/.38 and 20dp per-panel padding are inherited rather than fitted to actual content.

At logical widths below 780, SharedThreePanel wraps a 900dp-wide panel strip in a horizontal viewport. That deliberate focus-reveal fallback must be retained or replaced by an explicitly approved responsive behavior, not mistaken for a bug or simply deleted.

### EL-04 — dirty-exit destination is lost (source verified; also present in maintenance evidence)

`requestExit(Runnable leave)` invokes leave only for clean drafts. Dirty Save calls `save()` without continuation; Discard calls `navigate(0)` (Home), ignoring the requested leave action. Consequently:
- Dirty Close/Back → Discard takes user to Home instead of returning to the same custom library.
- Dirty top-tab exit → Save stays on custom library; Discard goes Home regardless of requested tab.
- First-use cancellation after edits also goes Home rather than the original leave callback.
Validation/save failure correctly keeps editing, and Continue Editing safely does nothing beyond closing the modal.

Define explicit exit intent: close editor → saved custom page; cancel creation → opener; explicit tab navigation → requested tab. Successful save/discard resolves that intent; failure/Continue retains editor. This reconciles prior same-page Close/Back requirements with actual global tab navigation, and needs review before implementation.

Source: [CustomLibraryActivity.java:69](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L69); [refinement register §§2, 16–20](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/docs/project/SUPERNOVA_POST_0.135_REFINEMENT_REGISTER_2026-10-10.md#L176) §20.

### EL-05 — stale focus and incomplete restoration (source verified risks)

`render()` removes/rebuilds the entire editor after ordinary changes. wizardCentreAnchor stores a raw View; it is neither cleared nor rebound when render removes it. Preview Left/Back blindly requests that reference, with no attached/visible/focusable check or fallback. Footer buttons have no semantic tags, and preview has no stable tag; rememberFocus therefore misses these targets. Option tags are positional, so the same tag may identify a different row after conditional facets/inline choice replacement.

Centre/preview scroll offsets are not saved. Workspace’s legacy grid field is set null in the shared library path; its restoreScroll parcel does not restore the actual PreviewPages list. Save/close nulls sharedLibrary and recreates it, so exact pre-editor list scroll/item return is not established. Preserve opener identity independently of Views and renderer recreation.

Preview updates on successful load only call setText while editing, which avoids an explicit requestFocus there; filtering/selection edits nevertheless rebuild all three regions and post focus restoration. No source basis for claiming async updates always preserve focus or scroll.

### EL-06 — global navigation boundary needs an editor contract (source verified risk; Shield tab mismatch not yet reproduced)

TopNavigation is constructed while Workspace has no child content: one-time prepareScrollingContent finds no PreviewPages/ScrollView. Later-added library/editor children do not participate in that constructor-time inset setup. Workspace does not implement TopNavigation.ContentFocus. Nav Down recursively searches children; in edit mode it can encounter the deliberately blocked underlying library instead of a defined wizard entry target. Up to top nav is explicit only at the first rail step.

The global shell remains reachable rather than an absolute modal focus trap; underlying library descendants are blocked, but a visual scrim does not define focus ownership. Specify whether top navigation stays reachable through guarded exits, and provide a direct Down route into the active editor.

Existing MainActivity.onNewIntent and MainFragment.consumePreviewNavigation do process preview_tab and select the requested tab, so **the reported TV content / Documentaries highlight mismatch is not proven to be a missing-intent handler**. Instrument selected tab, focused semantic ID, visible activity and content identity across custom → TV and rapid switches; distinguish selected vs merely focused tab.

### EL-07 — “missing statistics” is initial visibility, not absent aggregation

Current shared header explicitly calls LibraryPageHeader with customLibrary.entries(snapshot); PreviewLibrarySummary supports filtered movies/shows and indexed size/source summaries. The later [October 30 clarification](https://github.com/heretoecode/Supernova/blob/9f8982433fad47cf0e4c8ad2c0ee5002da588973/docs/design/NEXT_RELEASE_ADDITIONAL_SHIELD_QA_2026-10-30.md) says stats appear after scrolling down/up. Investigate first measure/bind, missing navigation inset, initial focus-driven scrolling and saved scroll, not a new stats implementation. Root cause of the physical initial-visibility failure remains pending.

### EL-08 — data/error transparency gaps (source verified)

Initial Snapshot is empty without a cache; a failed load logs only diagnostics. Zero preview results cannot distinguish loading, failed load, disconnected-source indexed state or genuine zero matches. Genre chooser always labels Any even when allGenres=true. Review omits the retained collection filter. Year choices do not reinsert a saved year absent from the snapshot (language/studio do preserve selected values). Do not clear hidden/temporarily unavailable selections.

## 5. Recommended redesign structure — proposal for review

Retain the existing five-step model, persistence format, shared library renderer, keyboard, genre picker and dialog infrastructure. Separate durable draft/focus/exit state from View construction. This is a fresh composition proposal, not a second wizard or permission to rewrite unrelated shared components.

**Scene ownership:** one approved existing static Supernova Space Black scene at the global shell, shared TopNavigation and one safe-area owner. No second application header. “Edit Library” is a contextual content title. Three regions use consistent restrained surfaces/insets, not independent background scenes. If retaining the earlier floating-edit context cue, use one controlled dim treatment over the same static scene; final overlay/full-workspace geometry and any blur remain owner-review decisions. Home/details/poster artwork remain governed by their own requirements.

**Three regions are justified by actual functions:** five-section selector; editing workspace; read-only preview with independently scrollable content. Keep .24/.38/.38 only as an existing baseline, not a chosen final ratio. Size the selector to labels, give form adequate width, and keep preview usable at large text.

```text
Shared global TopNavigation / approved Space Black scene
Context: Create Library or Edit Library
┌ Section selector ┬ Editing workspace          ┬ Live Preview ┐
│ Page Details     │ Pinned section heading     │ Pinned title │
│ Content Type     │ Scrollable grouped fields  │ Match summary│
│ Filters          │                            │ Scroll list  │
│ Display Options  │                            │              │
│ Review           │ Previous | Next/Save | Close│              │
└──────────────────┴────────────────────────────┴──────────────┘
```

This is an information-layout schematic, not a pixel mockup or approved final geometry.

- Keep headings and actions outside their content scrollers. Put preview focus on a stationary outer container with a deliberate inset; content clips inside it. Separate permanent panel boundary, selected step/value and current focus.
- Use stable semantic IDs for fields/actions/preview, resolve them after mutation, and validate attachment before restoring. Preserve preview/options scroll and the exact library opener item/scroll.
- Rail Right enters first actionable field (Review enters Save); centre Right enters preview; preview Left/Back restores remembered field or a valid fallback; centre Back returns to same rail step. Terminal directions stay deterministic.
- Horizontal footer needs horizontal Left/Right routing: current centre handler treats every Left as rail and every Right as preview. Merely changing LinearLayout orientation would break button traversal. Define footer edges explicitly; Down from form reaches nearest footer action; Up returns to remembered field.
- First-use and editing share the form, with different introduction/deletion/exit context. Explicit tab exit is guarded; Back/Close returns to the editor’s originating library. Continue Editing restores the precise invoking control.
- Prefer incremental updates: change field value/review/preview without rebuilding rail, footer and scroll containers. Snapshot completion updates results/status without changing focus.
- Show honest loading/failure/empty status and preserve cached usable data. A retry must use existing loader ownership; no new scanner or source polling.
- Preserve every parity row above. No schema changes, new field types, removed capabilities, new background asset or richer poster preview is proposed.

Before implementation, review this structure and resolve the edit-overlay geometry/global-nav boundary. Then provide precise normal/large-text/narrow-viewport mockups and interaction flows grounded in the parity table for approval. This report does not claim that final mockups have been approved.

## 6. Validation / acceptance matrix and remaining evidence

**Performed here:** read-only Git/source comparison and route tracing; maintenance mapping/register reconciliation; inspection of existing tests. No new tests, builds, software renders, emulator sessions or physical Shield runs were performed. No Shield/ADB device or supplied clip is attached. Existing tests are evidence of intended cases, not new passing results.

Existing [NextQaWizardTest](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/src/test/java/com/archos/mediacenter/video/leanback/NextQaWizardTest.java) covers first-step Up → custom nav, Review Right → Save, initial dirty-dialog focus and Save → custom toolbar. Its dirty-save test explicitly expects the leave callback NOT to run, so it does not prove correct navigation after a requested tab exit. [CustomLibraryPageTest](https://github.com/heretoecode/Supernova/blob/b4a6ee7a010c9b5b6af8731108f3973477bfc7f5/src/test/java/com/archos/mediacenter/video/leanback/CustomLibraryPageTest.java) covers persistence/delete, genre/year/watched rules, TV completion/progress, draft serialization and initial artwork. PortableCustomPageTest covers the portable definition. They do not certify focus outline clipping, every dirty-exit route or physical appearance.

| Required scenario | Current evidence / future acceptance |
|---|---|
| Every inventory row, all five steps, edit vs first-use, unchanged draft | Source traced; execute parity cases after approval, ensuring mere browsing does not dirty draft |
| Name blank/40/41/whitespace/long Unicode, keyboard Done/Cancel | Validation traced; verify readable field/caret and exact opener focus; no invalid commit |
| Content types; all filter combinations; no metadata, unavailable saved facets, hidden collection | Model traced; fixture-backed expected result identities/count/order and faithful round trip |
| Save / commit failure / validation failure | Source dialogs exist; failure must retain draft/focus; success returns to intended destination |
| Cancel/Back/Close + clean/dirty × Save/Discard/Continue | Destination defect source verified; test each route, including explicit top-tab target and creation cancellation |
| Delete confirm/cancel/write failure | Source traced; inspect before/after page key and media/history/source state; Cancel default |
| Rail ↔ form ↔ preview; inline chooser and genre modal; all footer actions | Source risks recorded; capture each transition and fallback after rebuild/recreation |
| Preview at top/middle/end and rapid scroll; 0/1/>20 matches | Bottom outline reported; all sides stationary/visible, content correctly clipped, count/list cap truthful |
| Async completion/failure while focused in each region/picker | Callback traced; no focus steal, stale detached targets, hidden state loss or fictitious zero-result success |
| Rotation/process recreation, long labels/large text, 720p/1080p/4K and logical width <780 | Partial instance state traced; actual bounds, action visibility and focus reveal NOT RUN |
| Custom library stats immediately on entry; edit return after scroll | Header exists; physical initial visibility reported failed; no scroll gesture should be required |
| Home → Movies → TV → Documentaries → TV; rapid switches/Back | Physical mismatch reported; log content/tab/focus identity and lifecycle, no assumed single cause |
| Source disconnect / offline cache / failed load / empty custom library | Read dependencies traced; preserve indexed records/source associations and drafts, no destructive “cleanup” |
| Data preservation / portable backup | Compare saved JSON incl icon/collection, custom toolbar/columns, media counts/IDs, matches, watched/progress, source/scan links before/after |
| Space Black/background/nav seam and 4-side preview focus | Requires approved mockups, software bounds evidence and real Shield screenshots; software alone cannot close physical findings |

All physical cases above remain **NOT RUN in this investigation**. These evidence gaps block a claim of complete runtime acceptance, not publication of this source audit. After design approval, use a dedicated next-release implementation branch from verified release ancestry and require regression plus real Shield QA. Preserve main, draft PR #27, all development branches and signed releases throughout.
