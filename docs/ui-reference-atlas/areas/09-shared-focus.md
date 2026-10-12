# 09 — Shared components, dependencies and focus contracts

Source baseline: signed 0.138 `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. Evidence below is **VERIFIED static source** unless expressly attributed to native CI evidence or **UNKNOWN**. No device-navigation test was run. Stable IDs describe surfaces; generated SITE/FOCUS IDs identify exact source lines.

| ID | Existing component and current facts | Consumers / exceptions / verification boundary |
|---|---|---|
| SHARED-001 | SharedThreePanel | Three panels, generic text/heading/action/control factories; Settings, Network & Files, Health, custom wizard. Generic heading21sp/light is different construction from dynamic text(...,21) context heading. Decorate20-unit padding,16 radius,1 stroke,4 elevation, gap16; widths configurable |
| SHARED-002 | LibraryPageHeader + PreviewLibrarySummary + PreviewLibraryColumns + PreviewToolbar | All Movies/TV/custom. Header title30sp/bold, rows13sp/light #e1e9ef; first four icons17dp+9dp gap; 2 scaled padding above/below. Main Recycler padding28dp; header/title/context geometry described in statistics report |
| SHARED-003 | PreviewDialog | Tracked weak-reference modal stack with owner/lifecycle; create/choose/read/review/message/destructive native confirm. Child dismissal restores opener, semantic tag/id fallback, then first shown enabled focusable; if parent/root detached, no false restoration claim. Native tracked dialogs have weaker attached-opener path |
| SHARED-004 | Choice list | Heading17sp, choice14sp, 37 scaled unit row, icon/check18 units; min240/max520 scaled width or viewport−48. Header fixed, rows clipped/scroll, unavailable items disabled/alpha. `— ` groups11sp/25 row; final `Done` special fixed footer. Focus is perimeter, not check/fill. Default selected row else first enabled |
| SHARED-005 | Read/review/message | Reader24sp heading16sp body, Close/Copy, scroll focus; review Back/confirmation initial Back; message centred content/action stack; page delete message differs from native row delete. Exact window/blur API conditions are source facts, device appearance UNKNOWN |
| SHARED-006 | PreviewTextInput / KeyboardInput / PreviewKeyboard | Shared editable/masked input and app keyboard, common nearest-row navigation; numeric validation uses host callback. Input is not a new per-screen keypad. Search supplies exits, modal names don't |
| SHARED-007 | TvButton / PreviewAccent / FocusGlow / ContentFocus | Button13sp × functional1.12 × TvLayout.scale, min44 baseline, permanent control radius10–12. Row ContentFocus transparent resting + accent perimeter halo; selected/checked separate. No generic web focus substituted as native evidence |
| SHARED-008 | TopNavigation / PreviewFocusRail / PreviewFocusRecycler | Semantic tabs + scroll rail, grid/list logical edges, stable media keys and focus restore. Shared row/card boundaries differ from keyboard/menu/dialog boundaries |
| SHARED-009 | TvLayout / background/artwork | scale clamp .85–1.25 of min(width/960,height/540), dp×density, safe insets; 4/5/6/8 grid columns by logical width. PreviewBackdrop/Frost/navigation shade/artwork presenter shared; physical resolution alone cannot predict crop/type layout |
| SHARED-010 | Inherited XML/AndroidX/Leanback | Programmatic surfaces coexist with XML preferences, native alerts, pickers and classic layouts. English resources and source sites alone do not resolve compiled sibling-module/resource overlays, reflection/JNI or Android generated widgets |

The existing [maintenance shared-component consumers](../../maintenance-investigation/evidence/shared-component-consumers.tsv) map is reused; new source sites and UI-owner hashes supplement it. Change impact is source ownership documentation, **not** permission to consolidate/redesign. Unknown: every device focus fallback after opener destruction, OEM font/IME/theme/animation rendering, physical overscan and semantic accessibility output. On-demand capture protocol must label these rather than assert source branches executed.

## Source owners and exact option appendix

- [SharedThreePanel.public static TextView text](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/SharedThreePanel.java#L62)
- [PreviewDialog.public static Dialog create](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L15)
- [PreviewDialog.public static Dialog choose](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L102)
- [PreviewTextInput.private static void showInput](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewTextInput.java#L18)
- [PreviewKeyboard.private TextView nearest](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewKeyboard.java#L91)
- [TvButton.public static void style](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/TvButton.java#L11)
- [TvLayout.public static float scale](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/TvLayout.java#L12)
- [PreviewFocusGlow.public void draw](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewFocusGlow.java#L20)
- [PreviewFocusRecycler.class](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewFocusRecycler.java#L6)

[Every matched display/choice expression in this area](../evidence/09-shared-focus-source-options.md) · [Full display sites](../evidence/display-sites.tsv) · [Focus branches](../evidence/focus-branches.tsv). Supplementary cross-area owners, including inherited utilities, appear in the full catalogue. Literal extraction does not establish visibility.
