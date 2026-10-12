# 04 — Search, matching and app keyboard

Source baseline: signed 0.138 `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. Evidence below is **VERIFIED static source** unless expressly attributed to native CI evidence or **UNKNOWN**. No device-navigation test was run. Stable IDs describe surfaces; generated SITE/FOCUS IDs identify exact source lines.

| ID | Exact surface/text/options | Effect/persistence/focus |
|---|---|---|
| SRCH-001 | Search tab → heading `Search`, hint `Search Movies and TV Shows` | Local indexed universe; query generation cancels stale work. Search-mode entry can restrict movie/TV/unmatched. Restore query/selected ID/scroll/key in instance state, not a new saved account preference |
| SRCH-002 | Empty help rows order: `Your Library` → `Search movies, TV shows, episodes and filenames.`; `Flexible Formatting` → `Punctuation and opening articles are optional.`; `Original Titles` → `Find a title using its indexed original name.`; `Minor Typos` → `Small spelling differences can still match.`; `People & Studios` → `Search cast, crew and studios where indexed. Select a result to open its details.` | Five aligned source explanations; descriptions are capability text, not proof of dataset accuracy. Help shown for empty/no results; results pane hidden |
| SRCH-003 | `No matching library titles`; `<n> results · In your library`; `Library search is temporarily unavailable` | Title/episode/filename/indexed metadata ranking, grouped show results; data source ContentResolver. Hardware Unicode/delete forwarded to field. Result select opens Details |
| KEY-001 | Rows `1234567890`, `QWERTYUIOP`, `ASDFGHJKL`, `ZXCVBNM`; icon Caps Lock, `Space`, icon Backspace; `Clear`, `Done`, `Cancel` | Caps default On; content description `Caps Lock On/Off`, Backspace. Caps/Backspace visible text empty; do not call them literal text buttons. Space weight3; row staggering12/36 scaled units, 32-unit row plus3 gap |
| KEY-002 | Shared keyboard in Search/name/numeric/server/account input | First T focus, remembered key; Left/Right no wrap, nearest horizontal centre across rows; top Up handed to host, bottom Down optional exit. Search right/Done can enter nonempty results; results Left or first result Up returns last key |
| KEY-003 | Clear/backspace/field input | Clear empties, Backspace removes selection or previous code point; replaces selected text, moves cursor. Cancel calls host, not universal form rollback. Secret input uses password transform and keeps spaces, plain input trimmed on Done; system IME suppressed |
| MATCH-001 | `Find a Match` / manual video/show scraper | Existing PreviewMatchSearch and ManualVideoScrappingActivity/TvshowScrappingActivity. Candidate query/provider names conditional; production source labels/resources included. Exact network search results/latency/account errors UNKNOWN |

Existing Search native images include compact empty, populated return-focus and enlarged keyboard fixtures. Shared numeric Maximum items uses this same alphanumeric keyboard, **not** an invented numeric keypad. No physical text-scale/focus certification claimed.

## Source owners and exact option appendix

- [PreviewSearch.public PreviewSearch](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L48)
- [PreviewKeyboard.public PreviewKeyboard](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewKeyboard.java#L24)
- [KeyboardInput.class](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/KeyboardInput.java#L8)
- [PreviewTextInput.private static void showInput](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewTextInput.java#L18)
- [PreviewMatchSearch.class](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewMatchSearch.java#L12)

[Every matched display/choice expression in this area](../evidence/04-search-keyboard-source-options.md) · [Full display sites](../evidence/display-sites.tsv) · [Focus branches](../evidence/focus-branches.tsv). Supplementary cross-area owners, including inherited utilities, appear in the full catalogue. Literal extraction does not establish visibility.
