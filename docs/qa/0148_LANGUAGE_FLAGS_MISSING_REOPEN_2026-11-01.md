# Supernova 0.148 Shield QA — missing language flags (REOPEN)

Owner reports this issue has been raised repeatedly and is still not fixed in installed 0.148. **Evidence:** owner-supplied photograph `IMG_08CDFB3B-2D86-45A6-BA81-22272CC38F24.jpeg`, showing Settings → Audio & Subtitles → Audio output & tracks → Audio preferred language. The selector displays generic globe icons for English, Abkhazian, Afrikaans, Albanian, Arabic and Aragonese, with no language flags. Photo is available in conversation, **not verified uploaded to GitHub**.

## Expected behaviour / acceptance
- Restore meaningful flag icons where an unambiguous, approved country/locale association exists; don't assign arbitrary national flags to languages spoken across multiple countries. Use an appropriate neutral language/globe icon for languages without a valid country association.
- Investigate the original approved language-icon design and existing shared language selection/locale mapping, asset loading and rendering rather than treating this as a new request. Apply consistently to Audio preferred language, subtitle language and any other shared language selection surfaces.
- Confirm real flag assets render on Nvidia Shield (not merely emoji fallback dependent on missing TV fonts). Preserve the selection radio state, D-pad focus, text, sorting and language codes.
- Add automated tests for mappings, fallback, asset resolution and UI; verify visually on a physical Shield in a signed QA build. Mark fixed only after owner retest. Treat as a **repeated failed regression**, document why prior fixes did not reach runtime.

Documentation only; no application code or main branch changed. Keep PR #29 draft.
