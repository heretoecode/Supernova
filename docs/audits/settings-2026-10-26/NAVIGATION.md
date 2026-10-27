# Settings screen and navigation-state map

[Audit](AUDIT.md) · [Inventory](INVENTORY.md) · [QA](SHIELD_QA.md)

## Counting and hierarchy

52 screen/state templates; 71 auxiliary control families below. A family can render multiple values (RGB channels are separately enumerated; repeated provider/keyboard character rows are templates). The 165 inventory rows overlap these families; do not sum them. System pickers belong to Android, not a bespoke Supernova screen.

```text
Settings (global navigation retained)
  Rail: Playback · Video · Audio · Subtitles · Library & Metadata · Home
        Appearance · Streaming · Network · Integrations · Advanced · About
  Integrations → Trakt / OpenSubtitles / IntroDB (enabled children)
  Advanced → Video / Audio / Network / Subtitle Compatibility
             Backup & Restore / Diagnostics
  About → app information at parent; Release Notes / Open-source Licences
          Credits & Acknowledgements / Technical Information
  Middle: preferences or special workspace → Right: context / inline values
```

## State catalogue

| ID | Surface | Contents | Route / condition |
| --- | --- | --- | --- |
| N01 | Entry | Playback rail focus; optional requested Settings/Streaming deep link | Left category list; focus previews, OK/Right enters; top navigation retained |
| N02 | Playback | Playback middle preferences | Up/Down rows, Left rail, Right information/value panel |
| N03 | Video | Video middle preferences | Hardware-conditioned rows; selection in right panel |
| N04 | Audio | Audio middle preferences | Decoder/output dependencies; disabled rows skipped |
| N05 | Subtitles | Subtitle middle preferences | Positive inverse default proxy, reading/download language selectors |
| N06 | Library & Metadata | Library middle preferences/actions | Long service-action list; confirmations and running jobs |
| N07 | Home | Home preferences | Featured switches, editor, queue clear |
| N08 | Appearance | Appearance preferences | Language restart/accent recreation |
| N09 | Streaming | Streaming preferences | Country/query/filter/provider/preferred/refresh/attribution |
| N10 | Network | Network preferences | VPN and SFTP trust; transport compatibility under Advanced |
| N11 | Integrations landing | Three integration switches | Right/OK expands children; explicit switch changes save |
| N12 | Advanced introduction | Caution introduction | No diagnostics landing list; merged workspace; Right/OK expands only |
| N13 | Video Compatibility | Nested Advanced child | Some rows require inherited unlock and device capability |
| N14 | Audio Compatibility | Nested Advanced child | Output/decoder constraints |
| N15 | Network Compatibility | Nested Advanced child | Backend/discovery/buffering constraints |
| N16 | Subtitle Compatibility | Nested Advanced child | Encoding list |
| N17 | Diagnostics | Nested Advanced child | Logging/level/digest/report/reference/export |
| N18 | Backup landing | Nested Advanced child | Export/Restore,26 direct category toggles,details/instructions |
| N19 | Trakt | Nested integration child | Sign-in/authenticated/disabled/sync status |
| N20 | OpenSubtitles | Inline account form | Draft credentials, reveal, saving/success/failure |
| N21 | IntroDB | Nested integration child | Five segment policy selectors |
| N22 | List choices | Right inline single-choice editor | Immediate save; rebuild and request selected value focus; Left centre |
| N23 | Multi choices | Right inline multi-choice editor | Immediate set persistence; rebuild path differs from modal |
| N24 | Provider choices | Selected/Other provider groups | Selected scroll max140dp; Other flexible scroll; dynamic catalogue |
| N25 | Modal preference fallback | List/multi/provider legacy modal | Done for multi/provider; in-place checks; not normal inline activation |
| N26 | Text keyboard | Validated text/secret input | Remote keyboard + hardware input,Clear/Done/Cancel; validation before acceptance |
| N27 | Accent palette | Seven choices | Six preset colours and Custom |
| N28 | Custom RGB | Three 0–255 channels, Apply | Left/Right sliders; Apply recreates Settings |
| N29 | Home editor | Rows and context editor | OK move/place,Right fields,eye visibility,Back cancel move |
| N30 | Home genre picker | Genre multi-selection | Any/All appears for multiple genres; restore editor anchor |
| N31 | Home confirmation | Delete row/discard unnamed draft | Safe initial focus; explicit destructive action |
| N32 | Clear queue/history | Confirmations | Queue and history actions have distinct retained state |
| N33 | SFTP identities | Empty/recorded host list | Host choice then Cancel/Reset confirmation |
| N34 | Diagnostic disable | Cancel/Keep logs/Delete logs | Only final noncancel choice disables |
| N35 | Diagnostic digest/reference | Async reader or no-reference/QR | Local data; return focus; no upload |
| N36 | Problem category | Playback/UI/Network/Other | Local marker then reference reader; logging-off explanation |
| N37 | Diagnostic export | Activity + system destination picker | Prepared report/success/failure/cancel and Settings return |
| N38 | Backup information | Details/instructions modal pages | Scroll,Back to landing without replacing state |
| N39 | Backup privacy | Locations inclusion/history exclusion notice | Safe choice or explicit consent; no restore yet |
| N40 | Backup busy | Create/inspect/preview/restore/save phases | Cancel where supported; held focus; owned modal |
| N41 | Backup destination | Android ACTION_CREATE_DOCUMENT | Success/cancel/unavailable/retry retained staging |
| N42 | Backup source | Android ACTION_OPEN_DOCUMENT | Cancellation changes no settings; chosen archive inspection |
| N43 | Backup review | Present categories and temporary selection | Next preview/Cancel; only supported categories |
| N44 | Backup confirmation | Selected replacement or legacy whole-library | Cancel or explicit restore; receiving-device warnings |
| N45 | Backup result | Verified export/restored/error/recovery-required | Back; Save verified backup again only if permitted |
| N46 | About app information | Foundation parent surface | App summary; child links initially collapsed |
| N47 | About Release Notes | Bundled version rows + notes | Current expanded; independent older expansion |
| N48 | About Open-source Licences | Async local catalogue + notice pager | Pending entry remembered; component selection/details scrolling |
| N49 | About Credits | Grouped acknowledgements + details/QR/logo | No provider enable actions |
| N50 | About Technical | Five technical categories | Read-only runtime facts with unavailable fallbacks |
| N51 | Classic More options | Legacy optional activity | Remaining time/trailer/rating legacy controls; organised foundation does not expose as ordinary rail child |
| N52 | Trakt authentication/sign-out | Inherited custom dialogs/OAuth | Account state refresh and network/denial recovery |

## Input and focus contract

Category focus runs `show` and repopulates preview content without expanding. OK toggles expansion; ordinary categories enter middle controls. Right on Advanced/Integrations/About expands but deliberately leaves parent focus; Down enters the first child. Child focus selects its workspace, OK/Right enters its controls. This is actual code behaviour, including differences from approved permanent-rail design.

Middle Up/Down is handled within the middle panel. Left restores remembered rail target; Right requests the help ScrollView, while OK on a list/multi row explicitly focuses its first inline choice. Right alone may land on the help container rather than a selectable choice (V03). Right-pane Left attempts the remembered middle view, with a first-row fallback if it is detached. Single-value selection rebuilds choices and focuses the selected value. Multi/provider selection rebuilds and focuses a computed neighbour (F01). Two-state controls use native preference persistence.

The fragment Back callback is enabled only for middle/help/About/Backup focus. It delegates to special workspaces or restores rememberedLeft. On rail focus it is disabled; the activity can move to global top navigation and then exit. Left from a child requests its parent; Back on an expanded rail child is not separately handled as hierarchy reversal (V01). Opening a category resets scrollToPosition(0); there is no general process-death restoration for every settings row. Backup saves its view state explicitly; About retains per-section row selection in its workspace instance.

FocusFirst waits at most16 posted animation frames for adapter stability and stops if detached. Child reveal checks current selection and runs after layout/transition. Async About licences use request generations and pending-entry state. Streaming generation guards protect stale catalogue UI but do not automatically refresh existing right-panel choices. OpenSubtitles checks attachment/visibility before publishing. Export/system-picker return restoration is a device check.

D-pad terminal directions are consumed in several panels; rail Up at its first entry can return toward top navigation. Enter/Select follows native View click, hardware typing goes through PreviewTextInput/PreviewKeyboard; not every list implements a Tab-specific graph. Remote-only punctuation, long text, held-key repeats, Tab/Shift-Tab/Escape and keyboard Back must be verified on Shield.

## Auxiliary controls and actions

| ID | Control family | Values/action | Persistence, effect, validation |
| --- | --- | --- | --- |
| A01 | Home row visibility | Show/Hide eye per row | preview_home_rows41.visible; immediate; built-ins retained |
| A02 | Home row move | OK starts/places; Up/Down changes order; Back cancels | preview_home_rows41 ordering committed on place |
| A03 | Home create row | Create draft | No persistent row until nonempty name accepted |
| A04 | Home row name | Text max40 | Saved JSON name; validation and draft discard |
| A05 | Home row icon | Automatic or known genre icon | JSON icon; empty means automatic |
| A06 | Home Movies | Select Movies | JSON movies=true,tv=false |
| A07 | Home TV Shows | Select TV Shows | JSON movies=false,tv=true |
| A08 | Home Both | Select Both | JSON both=true |
| A09 | Home Genres | Multi-select genres | JSON genres; empty means all |
| A10 | Home Match Any | Any selected genre | JSON matchAll=false; conditional on >1 genres |
| A11 | Home Match All | All selected genres | JSON matchAll=true; conditional on >1 genres |
| A12 | Home Sort | Date Added/Title/Year | JSON sort added/title/year |
| A13 | Home Order | Ascending/Descending | JSON descending; old values preserved |
| A14 | Home Maximum | 5/10/15/20/25/30 | JSON maximum; custom default15 |
| A15 | Home automatic inclusion | On/Off | JSON dynamic; switching Off freezes calculated members |
| A16 | Home Delete | Delete Row confirmation | Removes custom row only,not library titles |
| A17 | Home Keep draft | Continue Editing | No persistent deletion |
| A18 | Home Discard draft | Discard Row | Unfinished unnamed draft only |
| A19 | Queue Cancel | Cancel | No queue change |
| A20 | Queue Clear | Clear | Watch Next memberships only |
| A21 | History Cancel | Cancel | No history change |
| A22 | History Clear | Confirm delete | ViewingHistory.clear; positions retained |
| A23 | Accent preset | Six named colours | preview_accent41 int; apply immediately + recreate |
| A24 | Accent Custom | Open RGB | Draft channels from saved colour |
| A25 | Accent Red | 0–255 slider | Draft red; no persistence before Apply |
| A26 | Accent Green | 0–255 slider | Draft green |
| A27 | Accent Blue | 0–255 slider | Draft blue |
| A28 | Accent Apply | Apply | Save opaque RGB int,recreate |
| A29 | OpenSubtitles Username | Text max128; not empty | Draft until login success |
| A30 | OpenSubtitles Password | Secret text | Draft; mask unless reveal |
| A31 | OpenSubtitles Reveal | Show Password On/Off | Transient; does not sign in |
| A32 | OpenSubtitles Save | Save & Sign In | Async login; only success commits credentials |
| A33 | SFTP host | Select recorded identity | No reset until confirmation |
| A34 | SFTP reset Cancel | Cancel | Retains trusted identity |
| A35 | SFTP reset | Reset identity for host | SftpHostTrust.forget; next connection retrust |
| A36 | Diagnostics Cancel disable | Cancel | Logging and saved reports retained |
| A37 | Diagnostics Keep logs | Keep saved logs | Disable; retain local reports |
| A38 | Diagnostics Delete logs | Delete saved logs | Disable and remove saved logs |
| A39 | Problem category | Playback/UI/Network/Other | Marker/reference in supernova_diagnostic_session |
| A40 | Reference reader | Readable reference and optional QR | No upload; no new preference |
| A41 | Backup Export | Export Backup | Stage selected categories; fail unsupported selection |
| A42 | Backup Restore | Restore Backup | Open archive picker before any state mutation |
| A43 | Backup Details | See full details | Read-only policy page |
| A44 | Backup Instructions | Restore instructions | Read-only archive instructions |
| A45 | Backup privacy Cancel | Cancel/keep prior choice | Does not consent to locations |
| A46 | Backup include locations | Include locations | supernova_backup_include_locations; consent required |
| A47 | Backup category choice | 26 exported category definitions S140–S165 | Export commits; review selection transient |
| A48 | Backup Review Next | Preview selected categories | No replacement before final confirmation |
| A49 | Backup review Cancel | Cancel/close review | No application preference replacement |
| A50 | Backup logical confirm | Restore selected categories | Replacement transaction for selected owned state |
| A51 | Backup legacy confirm | Restore legacy backup | Separate whole-library replacement |
| A52 | Backup busy Cancel | Cancel | Model cancellation boundary,not guaranteed rollback after commit |
| A53 | Backup result Back | Back to Backup & Restore | Closes review/result and returns landing |
| A54 | Backup retry save | Save verified backup again | Only retryExport && !recoveryRequired |
| A55 | Backup create document | Android Save/Cancel | Provider-owned picker; verify destination readback |
| A56 | Backup open document | Android Choose/Cancel | Provider-owned picker; inspect untrusted archive |
| A57 | Diagnostic create document | Android destination/Cancel | Save safe ZIP; return to diagnostic opener |
| A58 | Keyboard characters | Printable character template | Per-key repeat does not constitute a separate Setting |
| A59 | Keyboard Caps | Caps Lock | Transient keyboard case |
| A60 | Keyboard Space | Space | Draft field content; secret whitespace preserved |
| A61 | Keyboard Backspace | Backspace | Draft deletion |
| A62 | Keyboard Clear | Clear | Draft only until Done |
| A63 | Keyboard Done | Done | Validated acceptance; caller controls persistence |
| A64 | Keyboard Cancel | Cancel/Back | Draft discarded |
| A65 | About release expansion | Expand/collapse older release | Workspace expanded set; no app settings |
| A66 | About component | Select bundled legal component | Async reader with generation guard |
| A67 | About notice pager | Scroll/page local legal text | Reader state; no upload |
| A68 | About technical selection | Five read-only categories | Runtime facts,unavailable fallbacks |
| A69 | About QR | Official HTTPS website QR | Not an integration sign-in or arbitrary URL input |
| A70 | Trakt sign-in | Custom dialog/OAuth | Inherited account flow; denial/cancel/error must recover |
| A71 | Trakt sign-out | Custom confirmation | Clear Trakt grant according to existing account handler |

## Source owners

`PreviewSettings.java` (all rail/input/model routes); `InlinePreferenceChoices.java`; `PreviewPreferenceDialogs.java`; `VideoSettingsActivity.java`; `BackupSettingsController.java` and `BackupWorkspace.java`; `CustomiseHomeEditor.java`; `PreviewHomeRows.java`; `PreviewTextInput.java` / `PreviewKeyboard.java`; `FoundationAboutWorkspace.java` / `BundledNoticeReader.java`; `Diagnostics.java` / `DiagnosticExportActivity.java`; `InlineOpenSubtitles.java`; `VideoPreferencesCommon.java`. Immutable links are indexed in VERIFICATION.md and every inventory definition.
