# Codex brief — 0.157 part B (from the owner's 0.156 review, 6 Nov 2026)

Same rules as `BRIEF_0157.md`: branch `codex/0157-2026-11-06`, non-visual work only, no screen/resource/Gradle/
`.github/` edits, API 23, Robolectric for org.json, readable code (`tools/test_code_layout.py`). Source of each item:
`docs/0.157/REVIEW_0156.md` (P-numbers). Report in the same 0.157 report file; list APIs for Claude in
`findings/0157-ui-hooks.md`.

1. **Age rating:** UK certificate (BBFC, e.g. 15, 12A) and its reasons where known, per film and per episode/series,
   stored with the title and available offline. Find out why the owner's earlier attempts never showed a rating
   (wrong country? not saved?). Expose: rating, reasons, or none.
2. **Seek preview pictures (P10):** none appear on Put.io or local files in 0.156. Find the cause and fix it. (The
   missing Settings switch is Claude's: it was never moved into Advanced.)
3. **Chapter list (P2):** after merging IntroDB parts into the file's chapters, names must make sense (no "Chapter
   03" beside "Chapter 05" because 1, 2 and 4 were replaced). Propose the rule (e.g. renumber the remaining chapters
   1…n). Also supply a picture for every part (none appear today).
4. **Episodes row missing (P7):** MobLand shows Episodes in the player; Marshals and Mayor of Kingstown (several
   episodes in the season) do not; on Marshals it seemed to appear and vanish. Find the cause and fix it.
5. **Track details:** for each audio and subtitle track supply the parts separately: language (plain name), Original
   yes/no, sound format in plain words (EAC3 → Dolby Digital Plus, AC3 → Dolby Digital, etc.), channels (5.1,
   Stereo), SDH / Forced / Audio description. Drop "TEXT". Mock-ups `docs/design/proposals/track-names/`.
6. **Per-series choices split:** keep the audio choice and the subtitle choice per series separately (today one switch
   covers both). Migrate existing choices.
7. **Remove picture in picture** completely (owner decision). Record what was removed and where, so it can return
   (`docs/REVISIT_LATER.md` has the note).
8. **Sleep timer and Still watching? logic:** stop-at clock times that move with the clock (Off · now+30 · now+60 ·
   now+90 · end of episode); Still watching? able to count time untouched as well as episodes. Claude draws the rows.
9. **Put.io pause test:** when the owner's 0.156 diagnostics report arrives, read around 17:51–17:55 UK time on
   6 Nov (MobLand on Put.io paused a few minutes, then resumed) and report what happened.
10. **Leftovers:** item 15 (Marshals production company: find the real cause) and item 20 (seek loading: make it
    ready for Shield acceptance once Claude draws the spinner).

Not in this brief: item 21 (newer NOVA engine) — suggested as its own version.
