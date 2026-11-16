# 0.158 — owner notes

Items the owner has placed in 0.158 (from 12 Nov 2026). Everything else new still goes to 0.157 until the owner
reviews the pending list.

1. **Media Transfers, part 2** (part 1 is in 0.157; see `docs/0.157/OWNER_NOTES.md` §24):
   - Per-series switch in Details › More: "Bring new episodes to this Shield" / "Stop bringing new episodes"; new
     episodes of a flagged series found by a scan on any connection are copied, verified and (if the delete setting is
     on) removed from the network; the Transfers page lists flagged series with a way to switch each off.
   - Schedule: an optional transfer window (e.g. 02:00–07:00) with Start now.

## Owner's notes, 16 Nov 2026 (dictated while Claude was unavailable) — proposals, not approved

Owner's instructions: review the docs, current code and approved mock-ups first; say what exists and what would
change; treat these as proposals; change nothing approved; discuss and get approval before any code. **Before
investigating, ask the owner for their existing Supernova research document and read it.**

2. **Episodes (Seasons & Episodes tab):** OK on an episode plays it straight away; a long press opens a menu: Play
   episode · Show details · Delete episode. **Deleting an episode** offers to delete earlier episodes of the same
   season that are in the library too (e.g. deleting episode 3 offers 1 and 2): the owner picks which, confirms, and
   nothing earlier is ever deleted without approval. Needs investigation, mock-up and discussion.
3. **Network statistics on Files:** check whether storage figures are split by protocol (WebDAV, SFTP, SMB, others)
   or lumped as "Network"; ideally indexed media per protocol, and if possible per source; confirm the figures are
   real indexed media, not estimated capacity.
4. **A Trakt list as a Home row:** pick one of your Trakt lists to show as a Home row, each title showing where it can
   be streamed on the services you have chosen. Investigate the data source and how provider choices are set.
