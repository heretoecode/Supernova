# Global Visual System — Design Authority

Status: **APPROVED/current**

## Typography and colour
Bright white primary text on dark/translucent Supernova surfaces. Supernova blue is an accent/focus colour, not a replacement text colour. Avoid cyan focused text. Current Shield correction requires clock typography/size alignment with the visual system.

## Focus
Persistent navigation/category rails use a compact rounded blue boundary with restrained outward glow. Adjacent persistent controls use one travelling/retargetable boundary, ~160ms (acceptable written range 140–180ms), so rapid D-pad movement remains smooth.
Media cards do **not** use the travelling boundary; each card enlarges independently. Shared card enlargement is approximately 1.08×. Artwork + rounded boundary + outward glow scale together. No artwork protrusion and no square-corner glow clipping.
Focus must never teleport because an edge target is invalid. Consume deterministic terminal directions.

## Dividers
Toolbar/Details selection uses the existing divider stroke. Selected blue segment must be the same thickness as the white divider; glow is optical outside it, never a second/thicker underline.

## Top navigation
SUPERNOVA far left; Home · Movies · TV Shows; spacer; Network & Files; Search; Settings; Clock. 19sp normal/light textual navigation. No Streaming/Library top items, no Settings/clock separator, no line beneath nav.
Home+LEFT stays Home; Settings+RIGHT stays Settings.

## Navigation background
Top navigation is not a solid bar. Scrolling content may continue behind it. Progressive blur + darkening is strongest behind nav and fades seamlessly below; no visible rectangle/separator. Current physical correction: the fade/blur must not extend so deeply that Network & Files, Search or Settings feel buried.

## Utility surfaces
Historical approved blue/black utility background remains evidence for Search/Settings-era surfaces; do not introduce expensive live per-card blur. Translucency/lightweight depth is preferred where hardware constraints matter.

## Language iconography
National flags only for actual locales. Generic languages use neutral language/globe iconography; avoid implying nationality.

## Evidence
`docs/preview-4.1.7/REQUIREMENTS.md` UI-001–004, UI-007, UI-009; 4.1.2 UI audit; current Shield QA.
