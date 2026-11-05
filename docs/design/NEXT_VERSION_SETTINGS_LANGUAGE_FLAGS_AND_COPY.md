# Next version — Settings locale flags and wording corrections

**Status:** User-confirmed UI defects; pending implementation. **Priority:** High for recurring missing flags. **Target:** next version after 0.143.

## Evidence and context
User screenshot of Audio preferred language picker shows globe icon repeated for English, Abkhazian, Afrikaans, Albanian, Arabic and Aragonese. User reports country/locale flags were approved in previous cycles but remain absent. Audio Output & Tracks subsection currently uses generic instruction text: `Review the current values and choose a setting to change it.` Picker includes `Choose one option. Changes are saved when selected.`

## Required corrections
1. Audit actual locale data, language-picker row binding, icon resolver and rendering path. Implement the previously approved **locale-specific flag treatment** where region is explicit (e.g. English UK 🇬🇧 and English US 🇺🇸); do not assign an arbitrary national flag to a language-only code. Retain neutral language/globe icon for languages without an unambiguous region.
2. Ensure the icons actually render on Nvidia Shield/Android TV; use supported assets/fallbacks rather than relying on emoji glyphs that may render as monochrome or missing. Check picker scrolling, selected/focused state and all language pickers using the shared component.
3. Replace generic Audio Output & Tracks description `Review the current values and choose a setting to change it.` with **`Configure audio output, playback compatibility and preferred audio tracks.`** (proposed user-facing wording; verify technical accuracy with the underlying settings).
4. Remove/rewrite boilerplate picker guidance such as `Choose one option. Changes are saved when selected.` when it adds no value. Audit similar generic copy throughout Settings, replacing it with short purpose-specific text; do not alter underlying preference keys, values, behaviour or dependencies.
5. Add tests for en-GB/en-US, generic en, language-only locales (e.g. Arabic), unsupported/unknown locale, picker focus/selection, and hardware rendering. Explicitly report screenshot/device evidence to prevent recurring 'implemented' claims without visual confirmation.

## Related design
Preserve existing Settings category layout, focus/navigation conventions, and the separate planned row/toggle visual refinements. Do not modify 0.143 or merge to main as part of documentation.
