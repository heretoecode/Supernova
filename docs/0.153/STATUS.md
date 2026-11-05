# Supernova 0.153 — one global style for buttons, menus and panels (approved 2 November 2026)

**Status:** implemented on `claude/supernova-github-access-yfhe89`, built on the delivered 0.152 source (`d81c0027`). Automated build, tests and signing run in GitHub Actions; the owner approves each signing. **Physical Shield verification pending — nothing here is accepted until the owner confirms it on the Shield.**

Spec: [BUTTONS_GLASS_AND_MENUS_APPROVED_2026-11-02.md](../design/BUTTONS_GLASS_AND_MENUS_APPROVED_2026-11-02.md). The rules are **global**: they come from one shared style (`leanback/SupernovaStyle.java`), and `SupernovaStyleConformanceTest` fails the build if any screen defines its own focus look. Screens the owner never showed change too.

## Changes

| # | Area | Change |
|---|---|---|
| 1 | Every button | Black box at rest; white box with **black** text and icon on focus, soft shadow; sized to its content; one icon size. Includes the HUD (play/pause, Settings), details actions and tabs, keyboards, toolbars, Up Next skip, side-panel tabs and Save, source cards, genre picker, text inputs. |
| 2 | Menus and panels | One 97% navy glass for pop-up menus, dialogs, Playback Settings, Edit Page, subtitle panels and pickers. |
| 3 | Rows | White outline focus (no fill change, no cyan glow) with breathing room; the travelling outline in Settings and the file browser is the same white outline. |
| 4 | Pop-up menus | No title ("Actions" etc. removed — kept for TalkBack only). Groups get an inline heading with a line (`Library ———`). Width fits the longest item (360px–40% of screen). Details **More** regrouped: Playback · Library (Remove From Library last) · Streaming Services · Files & Media. |
| 5 | Streaming button | Provider name + one generic "opens externally" icon, same size as Resume/More. |
| 6 | Provider logos | Official single-colour logos in a uniform tile; Prime Video and Disney+ use their symbol only. Providers without a logo get a uniform initials tile (e.g. "JW", "Ha"). |
| 7 | Never white on white | Button text and icons follow the button's focus even when a screen builds or recolours them afterwards (the shared button restores the readable colours). Destructive actions such as Delete are soft red at rest and deep red on the white box. |
| 8 | Settings | No page title; eight larger tiles fill the page; icon beside a single-line title; renamed: Playback & Video, Audio & Subtitles, Library & Home, Streaming, Appearance, Connections, System, About Supernova (internal keys unchanged, so saved settings carry over). Old "Mark's personal Nova build" box removed. |

## Exceptions (reported to the owner as the spec requires)
- **Poster / artwork card focus** (`PreviewFocusGlow`) — not a button; unchanged by the spec.
- **Top navigation bar** — the owner asked for it to stay exactly as is (accent underline).
- **Seek bar thumb** — not a button.
- **Original NOVA phone screens and upstream NOVA 6.6.2 dialogs** (`BrowserCategory`, `square_button`, `big_button`, `button_selector_holo_dark`, `subtitle_swatch`, `subtitle_row_bg`) — not part of the TV interface.
- **Legacy native player menu cards** (`mark_hud_focus`) — only hosted inside the playback panel.
- **Skip button XML** — an XML copy of the shared style; the test checks its colours match.
- **Original NOVA system dialogs** that never pass through Supernova's dialog styling (e.g. the old grid screens' sort dialogs, rescan, private mode) keep the system frame. Supernova's own dialogs, credentials dialogs and delete confirmations do follow the style.
- **Information dialogs** (report reader, confirmations) keep their heading: they are messages, not menus.

## For the owner to decide
- At 97% glass, very bright text directly behind a menu can still show as a faint ghost (see the grouped-menu render with yellow text behind it). 98% would hide it further; 97% is the approved value and is kept until the owner says otherwise.

## Owner Shield checklist
1. Details page: Resume / More / tabs / streaming button — black at rest, white with black text on focus, nothing white-on-white.
2. More menu: no "Actions" title; Playback / Library / Streaming Services / Files & Media headings with lines; Remove From Library last; menu no wider than needed.
3. Playback: play/pause and Settings black at rest, white with black icon and label on focus. Playback Settings panel and its tabs.
4. Any long-press / context menu, the subtitle chooser, versions, artwork picker, settings choices: no title, glass, white outline rows.
5. Settings: no title, eight big tiles, single-line titles with icons; left rail inside a category has one white outline.
6. My Providers: logos uniform; providers without a logo show initials.
7. Up Next skip button and the on-screen keyboard.
