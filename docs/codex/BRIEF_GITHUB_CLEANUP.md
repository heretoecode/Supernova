# Codex brief — make GitHub the single, current home of Supernova (owner approved 2026-11-05)

Rules: `BRIEF_0155_MORE.md` rules 1–5 and `docs/codex/HANDS_OFF.txt` (read it first). Branch
`codex/0155-code-2026-11-04` only. No PR, merge, rebase, force-push or workflow dispatch. **Documentation only:** no
app code, no `.github/`, no Gradle, no `tools/`. Claude keeps working on 0.156 in parallel, so stay out of
`docs/0.155/`, `docs/0.156/`, `docs/design/` (read them, never edit them).

## Goal (the owner's words)
"If tomorrow I delete Claude, open a new account and connect it to GitHub, it should simply know where we left off."
GitHub must be the one up-to-date place for everything about Supernova, readable by the owner (plain English) and by
any AI (exact paths).

## The list
1. **Inventory first** → `docs/codex/findings/docs-inventory.md`: every file under `docs/` and the repo root `.md`
   files, one line each: what it is, date, still current / superseded / history, and **every reference to it** from
   code, workflows, `tools/`, tests, `assets/` or other docs (grep). Files referenced by anything except other docs
   are **never moved** (e.g. `docs/0.154/SIGNED_QA_EVIDENCE.json` is read by release-history checks).
2. **`CLAUDE.md` (repo root, new)** — read automatically by Claude Code. ≤ 250 lines, plain English, sections:
   - What Supernova is (TV-only NOVA fork for the owner's NVIDIA Shield), current version and where the signed APK
     comes from.
   - **Start here, in order:** this file → `PROJECT_STATUS.md` → `docs/0.156/RELEASE_LIST.md` →
     `docs/0.155/OWNER_NOTES.md` → open GitHub Issues → `docs/codex/HANDOVER.md`.
   - **How the owner works** (from `docs/0.155/OWNER_NOTES.md` header rules and this conversation's practice): British
     English; simple replies when asked; photos → notes → final mock-ups only (one numbered group, captions only, no
     owner notes inside designs, panels sized to content, ≤ ~5 inline options else a side panel) → owner approval →
     code only after the owner's go-ahead; the owner approves every signing; never paste keys or secrets; no model
     names in commits or code.
   - **Who does what:** Claude = anything the user sees, integration, builds, signing; Codex = non-visual work on its
     own branch, never main, never existing `.github/`/Gradle/hands-off files.
   - **Branches and repositories:** main, Claude's working branch, Codex's branch, `native-archive-0146`, the
     `heretoecode/aos-MediaLib` fork (used by every build — keep), `heretoecode/aos-AVP` (unused; to be archived).
   - **Build, test, sign:** which workflows, in which order, the conformance → signing gate, where artifacts land,
     Drive delivery not configured.
   - **Golden rule:** every decision is written into the repo (owner notes, issues) before the session ends —
     conversations are not stored anywhere else.
3. **`AGENTS.md` (repo root, new)** — the same "start here" for Codex/other agents, shorter, pointing at `CLAUDE.md`
   and `docs/codex/`.
4. **`README.md` rewrite** — the GitHub front page for a person: what Supernova is, current version (0.155, NOVA
   6.6.4 engine), what's coming (link to the 0.156 milestone), how to install the APK, where things live, credits
   to NOVA/Archos and licence note. No internal jargon.
5. **`PROJECT_STATUS.md` rewrite** — today's state: released 0.155 (date, signing run, APK), next 0.156 (link to the
   list and milestone), work in progress (Claude, Codex), decisions waiting for the owner, known problems. Remove the
   "Preview 4.1.7 Final" baseline text. Keep a short "History" section linking to the archive.
6. **Archive** — move superseded docs (per the inventory, and only unreferenced ones) into `docs/archive/<topic>/`
   with `docs/archive/README.md` (index: what each folder holds, why it's history). Old `docs/Future Development/`
   register: archive it once its open items are in Issues (step 7). Fix relative links you break.
7. **Issues content** → `docs/issues/ISSUES.json` (Claude creates them on GitHub from this file; do not create them
   yourself). Each entry: title, body (plain English, then "Details" with file paths), labels, milestone.
   - **Labels:** `bug`, `design`, `player`, `home-details-search`, `network-files`, `settings`, `diagnostics`,
     `engine`, `docs`, `parked`, `decision-needed`, `codex`, `claude`.
   - **Milestone `0.156`:** one issue per item in `docs/0.156/RELEASE_LIST.md` (all 45), with its owner-notes item
     number and mock-up folder.
   - **No milestone, label `parked`:** every entry in `docs/REVISIT_LATER.md`.
   - **`decision-needed`:** anything waiting for the owner (e.g. Smart Collections, Put.io improvements, streaming
     launch from Where to Watch).
   - Also `docs/issues/LABELS.json` and `MILESTONES.json` (name, description, colour).
8. **Keep it current** — add to `CLAUDE.md` and `AGENTS.md`: "At the end of every session update `PROJECT_STATUS.md`,
   the owner notes and the affected Issues."

## Not yours (Claude/owner)
Bringing main up to date (a PR the owner approves), creating Issues/labels/milestone from your JSON, archiving
`aos-AVP` (owner, repository settings), the repository description.

## Before you finish
Report `docs/codex/reports/YYYY-MM-DD-github-cleanup.md`: what moved where, every link fixed, anything you kept in
place and why; update `docs/codex/HANDOVER.md`; confirm with
`git diff --name-only claude/supernova-github-access-yfhe89...HEAD` that no code, `.github/`, Gradle, `tools/` or
hands-off file changed. Commit and push.
