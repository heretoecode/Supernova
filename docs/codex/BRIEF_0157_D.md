# Codex brief — 0.157 part D: Settings audit and data fixes (owner voice memo, 8 Nov 2026)

Do this after part C (or alongside, if C is waiting on the owner). Same rules as `BRIEF_0157.md`; non-visual only.
Source: `docs/0.157/OWNER_NOTES.md` §20.

1. **Settings inventory** — `docs/codex/findings/0157-settings-inventory.md`: every setting in Settings and Files
   (including Advanced and hidden ones). For each: key; current tile/tab; what it really does (read the code); who reads
   it; still used or dead; current title and description; a plain-English title and description proposal; the kind of
   control it should be (switch / up to five inline choices / long list); and tags (playback, picture, audio,
   subtitles, library, details, home, history, streaming, trakt, ratings, backup, diagnostics, engine, network,
   appearance, about). Flag anything confusing, duplicated or obsolete. Claude uses this to arrange the new tabs.
2. **Release notes data:** every version in `assets/foundation/release-history.json` gets proper notes (one change
   per line, more detail than today, from each version's `docs/0.1xx/` records); no status text in titles ("In
   development…", "Shield QA candidate…"); dates kept so Claude can show them as day month year. 0.156's notes rebuilt
   from `docs/0.156/RELEASE_LIST.md`. Keep `docs/0.1xx/` folders where they are.
3. **Licences data:** one version per component (AndroidX/Kotlin/JetBrains show long version lists; Sentry shows two);
   keep the full licence texts available (legal), plus each project's URL for a QR code.
4. **Diagnostics:** a clear-after-export API, and a retention setting (keep 1 / 2 / 4 weeks) that trims old entries
   automatically.
5. **Remove Issue digest** everywhere (data, producers, settings keys, backup), keeping diagnostics automatic.
6. **Recreate sort titles:** make the rebuild automatic when "ignore The and A" changes, so the manual row can go;
   remove the collections wording.
7. **JustWatch link:** it fires an intent the Shield cannot open. Replace with data for an attribution line and QR
   (no app intent).
8. **Backup categories:** propose a smaller, clearer set (today ~30 custom items), with what each contains, for a
   from-scratch redesign; keep private items off by default.
9. **Lag:** check the 0.156 diagnostics report around 01:05 on 8 Nov (after leaving System settings) when the owner
   sends it.
