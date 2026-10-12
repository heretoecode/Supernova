# 0.137 Search page — keyboard density and right-side information

**Status:** User-reported and photo-observed refinement requirements for upcoming 0.138 scope; right-panel layout proposals await user confirmation. No implementation performed.

## User-requested keyboard changes
- Search field currently appears visually as a two-line-height area despite being a single-line input, with text/placeholder near top. Make a **true single-line** field with text vertically centred, appropriately compact height, preserved cursor/placeholder/focus and accessibility.
- Reduce heights and excessive vertical spacing of digit, letter and six action keys without making D-pad targets uncomfortably small. Preserve existing keyboard layout and typing behaviour.
- Convert **Caps** and **Backspace** buttons to recognisable **icon-only** controls with accessible descriptions.
- Reallocate recovered width to **Space**, noticeably wider than its neighbours. Retain labelled **Clear, Done, Cancel** with compact height.
- Preserve consistent focus states and correct D-pad traversal; use shared applicable global button tokens without confusing keyboard-specific controls with generic action buttons.

## Right-hand information (proposal pending user confirmation)
- User sees **Your Library**, **Flexible Formatting**, **Original Titles**, **Minor Typos**, **People & Studios** explanations and finds current placement/alignment loose. User suggests separators and possibly centring.
- Design recommendation: **left-align** headings/descriptions for scanning, use a consistent icon column and text start, regular heading/description spacing, **subtle horizontal separators**, no heavy nested cards. Align right panel's content block sensibly with left keyboard area. Informational items should not become focus targets.
- Exact layout, divider weight and vertical placement remain to be confirmed from actual Shield render and user feedback. Preserve existing search semantics and approved Supernova background.

## Evidence and validation
- User photo IMG_4899.jpeg attached in conversation; **do not upload original image to GitHub/Codex without explicit permission**.
- Validate 16:9 TV, overscan, font scaling, long placeholders, focus/typing, caps/backspace/space and action keys, text clipping, readability.
- User has approved complete prior 0.138 work scope; this is an additional design observation, not evidence of an implemented fix.

## User-approved right panel refinement

User approves left-aligned headings/descriptions, aligned icons and subtle separators. **Each of the five headings must have its own distinct, semantically appropriate monochrome icon**, consistently sized, aligned and stroked: Your Library (library/media icon), Flexible Formatting (formatting/list icon), Original Titles (globe/language icon), Minor Typos (spelling/search icon), People & Studios (**two-person icon**, not generic information symbol). No colourful mismatched icons or repeated generic info icons. This is approved for the next-version handover.

## Copy and capitalisation approval

User explicitly authorises editorial improvements to the five right-hand explanatory headings/descriptions **where wording better communicates actual implemented search behaviour**, with consistent British English spelling, capitalisation, punctuation and terminology. Audit search indexing/features before promising capabilities; avoid changing semantics or inventing search functions. Apply consistent title-case or sentence-case policy for headings and sentence-case descriptions. Preserve distinct monochrome icons and approved left-aligned, subtle-divider layout.
