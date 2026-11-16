# 0.157: questions for the owner

Each has Claude's recommendation. Easy to undo: done as recommended. Hard to undo: skipped until answered.

1. **New mark orientation.** Copied from the approved picture (`brand/03` row 1): outer gap on the right, inner gap
   at the bottom. RULES §21's wording says lower right and upper left. Recommendation: keep the picture. (Done.)
2. **New Files page switched on by default** before every Files piece is in (Check matches, Couldn't be read, Found
   on your network, real reachability pills still to come). Recommendation: on, so the Shield test sees it. (Done;
   preference `supernova_files_page` false brings the old page back.)
3. **NOVA's hidden grid and classic screens (step 6).** Codex's report found nothing provably SAFE to delete (all
   UNSURE). Removing routes is hard to undo safely. Recommendation: leave for 0.158 with per-route proof. (Skipped.)
4. **Memory cuts (step 5).** Need the Shield's per-page numbers from a diagnostics export of a 0.157 build.
   Recommendation: measure on the 0.157 Shield test, cut in 0.158. (Skipped.)
5. **Backup "Choose" in six groups.** The agreed six (Settings · Library and history · Home rows and pages ·
   Connections · Accounts · Subtitle fonts) don't match the stored parts: there is no accounts part (logins are never
   backed up) and no subtitle-fonts part. Recommendation: five groups now (Accounts = service settings), fonts when
   they can be backed up; with the Settings redesign in 0.158. (Skipped for 0.157.)
6. **Check matches for TV shows.** Films are done (looked up again from the file name; Accept saves the new match).
   For a show, Accept means re-matching every episode, which needs more care. Recommendation: films in 0.157, TV
   shows in 0.158; Fix match still works for any show meanwhile. (Skipped for 0.157.)
7. **Experimental "Also translate other languages".** Approved to build and judge, but it merges a second subtitle
   source on screen: a large piece of player work. Recommendation: build it first thing in 0.158 so it gets a proper
   Shield trial. (Skipped for 0.157.)
8. **Chapter row centred between the tab line and the bottom edge** (player v10) was proposed, not approved, so it
   was left as it is.
9. **Signing 0.157 — resolved (owner, 16 Nov 2026: "Allow the 0.157 signing copies").** Copies added:
   `supernova-0157-qa.yml`, `supernova_0157_qa_signing.py`, `verify_supernova_0157_qa_apk.py` — version strings
   only; certificate pin, environment, branch, test thresholds and required classes unchanged. Original note: The signing gate (`.github/signing/supernova_0156_qa_signing.py` and
   `verify_supernova_0156_qa_apk.py`, used by `supernova-0156-qa.yml`) only accepts version 0.156. Signing a 0.157
   APK needs 0.157 copies of the QA workflow and those two gate files, as was done for 0.156 (same certificate pin,
   same protected environment and branch; only the version strings and the list of required test classes change).
   That is a change to the signing setup, which you've ruled out, so it is not done. Recommendation: allow the
   0.157 copies (or make the gate read the version from the history file), then sign the final green build.
   The 0.157 conformance (build) workflow copy is added: `supernova-0157-conformance.yml`.

10. **Captions under the player's Chapters and Episodes cards? (REVIEW_0157 #16) — answered from the approved
    designs:** no. The approved player mock-ups (`player/20`, `27`, `chapters/04`) put the name and second line inside
    the card, on its shade; captions under cards are for Home rows. Kept as approved; say if you want them changed.
11. **Centred chapter row (REVIEW_0157 #17):** QUESTIONS #8 wrongly called it unapproved; Player v11 approved it
    (13 Nov). Now built: equal space above and below the card row.
12. **Track pills on the main Playback Settings page (REVIEW_0157 #11).** You asked for this "to confirm in the
    mock-ups"; the approved mock-ups (`player/25`, `28`) show the track row as name only. Not built. Recommendation:
    show the chosen track's pills on the Subtitle Track and Audio Track rows (e.g. "English · Original · SDH"), as in
    the lists; say yes and it goes in.
13. **Fix match opens as a page in Files' style, over where you were.** The decision was "Fix match from anywhere
    takes you to Files › Library Care's Fix match page; Back returns". Built as one full page (path "Files › Library
    Care › Fix match", keyboard left, matches right); Back returns to where you came from. It is not yet reached by
    switching to the Files tab first. Easy to change if you want the tab to switch underneath.
14. **Folder grid (REVIEW_0157 #25):** browsing a storage now stays in Files as the approved grid (path, cards,
    In library / Not added, Sort, Videos only). Not yet: the List view button and folder artwork mosaics.
