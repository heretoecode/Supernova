# Codex brief — everything for 0.157 (6 Nov 2026)

One brief for all of Codex's 0.157 work. Work down the list in order, then hand back once. 0.156 is closed: nothing
here goes into 0.156.

**Rules** (as before): a new branch `codex/0157-2026-11-06` from `claude/supernova-github-access-yfhe89`; merge that
branch first and again before you hand back; no PR, no merge into Claude's branch, no workflow dispatch, no `.github/`
workflow or Gradle edits (new `.github/build/*.patch` files are fine), nothing in `docs/codex/HANDS_OFF.txt`. No
screens, layouts, `res/` or on-screen wording: Claude builds every screen from what you deliver. One statement per line,
lines ≤ 120, `python3 tools/test_code_layout.py` passes. A test for every change; never weaken, skip or delete a test.
Compile every changed file (app and modules) before you hand back and list anything you could not compile. Every new
API goes in `docs/codex/findings/0157-ui-hooks.md` (main-thread safe or not, threading, cancellation, failure values).

Designs this serves: `docs/0.157/OWNER_NOTES.md`, the Files redesign (`docs/design/proposals/network-files-tabs/`
01–31, decisions in `docs/REVISIT_LATER.md` under "Network & Files as one page with tabs", principles in
`docs/design/FLAT_PAGE_PRINCIPLES.md`).

## The list

**Files page (was Network & Files) — data and engine**
1. **Tab summaries** in one cheap snapshot per tab: This Shield (devices, videos, free space), inside a folder (videos
   here, in library, not added), Connections (connections, reachable, videos), Library Care (videos, to check, sources).
   "To check" must equal `LibraryAttentionCount`.
2. **Reachability for every saved connection:** Reachable / Not reachable with "last seen", refreshed in the
   background with a bounded timeout; Put.io keeps using `PutioSyncScheduler.status()`.
3. **Scan one source:** start a scan of a single chosen source (same stop/progress/history rules as Scan now).
4. **A scan-running signal** for the top-bar ring on every page: observable, main-thread safe, no polling by screens,
   on while any library scan or Put.io sync runs, off when it ends or fails.
5. **Automatic scan choices** Off · Hourly · 2 hours · 6 hours · Daily ("Twice a day" goes); migrate existing users
   to the nearest choice and say which in the report. "When Supernova opens" stays a single on/off.
6. **Recent scans:** last 10 with source, when, how long, what changed, and Finished / Stopped / Failed with a plain
   reason (reuse the 0.156 store).
7. **Find it again, everywhere:** extend 0.156 Find it again with "Search everywhere" (every connection), with
   progress, Stop, and the same re-link rules (keep watched state and resume point).
8. **"Not a film" (personal video):** mark a file so it stays in your files but out of Movies, TV Shows and matching;
   undoable; persisted; carried by backup under an existing category; never re-matched by scans.
9. **Library Health lists per kind** with a plain reason for each row: Couldn't be read (Damaged, Timed out,
   Unsupported) with Try again; Not matched with No match / Possible (and how many candidates).
10. **Forgiving Fix match search** (as forgiving as Search, but against TMDB): ignore capitals, punctuation and a
    leading "The"; try dotted-initials and with/without "The" variants; one-letter typo retry; original titles; IMDb
    (tt…), TMDB, TVDB and **Trakt** numbers and Trakt slugs; rank and mark the best match. Bounded time, cancellable,
    offline returns none. Reuse the existing Fix match transport and allow-lists.
11. **Connections, add and remove:** one form per type with an encryption switch (WebDAV/WebDAVS, FTP/FTPS) and the
    right default port; remove with **Undo** (soft delete for a short window, then really gone; nothing on the
    server touched); Find on my network results marked New / Added with the device kind.
12. **Put.io sign-in by code** (enter a code at put.io/link on another device) if the current flow does not already
    do this: confirm in the report, and expose state (waiting, signed in, expired, failed) without exposing tokens.
13. **Artwork for folders and loose files:** for a folder, up to four artworks of library items inside it; for a file
    not in the library, a still frame where one can be made cheaply (local files), otherwise none. Cached, bounded.

**Details, Home and library pages — data**
14. **More Like This** from three sources (TMDB recommendations, TMDB similar, Trakt related), merged and de-duplicated,
    split into In your library / On your services.
15. **Details key information fixes** (0.157 notes §1 item 4): Also Known As without foreign-language noise; series
    runtime from episodes rather than Unknown; no Budget/Revenue for series; check the wrong Production Company.
16. **Details tabs without pop-in:** tab availability known with the page (cheap, synchronous or pre-fetched), so all
    tab titles can be drawn at once.
17. **Smooth Home hero changes:** pre-fetch and cache the next hero's backdrop and logo so the change never waits.
18. **Library statistics:** the eight stats (Titles, Unwatched, In progress, Total size, Added this month, On this
    Shield + size, On the network + size, Last updated) and the status line with its reason.
19. **The stuck "Scanning local library…" pill** (0.157 notes §1 item 3): find why it stayed 10+ minutes; fix the cause.

**Player — engine**
20. **Seek loading signal:** a reliable "loading at this position" state (and the chapter) so the ring beside the time
    shows only while the picture is really waiting.

**Engine stack and clean-up**
21. **The newer NOVA engine stack** (excluded from 0.156): bring it in, with the native build, packaging recipe and
    a reviewed archive, as for 0.155/0.156.
22. **Everything you deliberately left for 0.157** in `reports/2026-11-05-0156-final.md`: native counters and
    protocol instrumentation, DP-10 listing cancellation, TV stage 5 classic route deletion, the security/privacy
    audit, scan cadence report, three full player reads, upgrade safety matrix, version-neutral pipeline draft, MORE
    groups 2–4 deep reads, post-hunt fixes, lint inventory, dead-code pass, performance, APK size and dependency
    audits, repeat hunt and tests.
23. **Legacy NOVA clean-out** (0.157 notes §10): write `docs/codex/findings/legacy-cleanup-candidates.md` first (SAFE /
    UNSURE / KEEP with evidence and size, in small batches; biggest candidate the `try_new_ui` / classic-UI switch).
    **Remove nothing until the owner approves the report**; then remove approved batches, each with a full build and
    tests. Keep engine, playback and network code close to NOVA upstream.

## Hand back
One line in `docs/codex/HANDOVER.md`, one report in `docs/codex/reports/` (each item: done with commit, or not done with
the reason), the new APIs in `docs/codex/findings/0157-ui-hooks.md`, and any patch registrations (add / replace /
unregister) with SHA-256s. Then stop.
