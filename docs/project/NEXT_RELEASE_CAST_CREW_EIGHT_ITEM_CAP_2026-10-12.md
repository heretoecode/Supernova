# Next release requirement — Cast and Crew capped at eight (2026-10-12)

**Status:** Explicit user request to include in next version, even if the broader lower-details-page redesign is deferred. Documentation only; implementation and validation pending. Do not modify active development branch without approved consolidated handover.

## Required behaviour

- Details-page **Cast** list: show at most **8** people.
- Details-page **Crew** list: show at most **8** people.
- No Show More, See All, expandable list, hidden overflow navigation or alternate control exposing the remaining names in these sections.
- If fewer than eight are available, show only the actual entries, no blank placeholders.
- Apply consistently to shared/global details-page implementations where Cast/Crew sections exist, across applicable content types.
- Preserve stable, meaningful ordering (e.g. existing provider/billing order for cast; review current crew ordering and ensure it is deterministic and useful); do not arbitrarily reorder or invent people.
- Where upstream API permits, avoid unnecessary fetch of excess people; where credits arrive in one complete response, it is acceptable to fetch the response and cap the UI/model results to eight. Do not claim that the API itself supports limits without verification.
- Ensure no residual focusable hidden cards/overflow affordances after the eighth entry.
- Add automated checks where feasible and Shield QA checks for 0, 1, 8, and >8 entries, including Cast and Crew separately.

## Release coordination

User explicitly requests this in the **next version**, alongside other already-discussed requirements. It is **not conditional** on redesigning the lower half of the details pages. Include in the next consolidated release handover and acceptance checklist; do not silently omit. Existing 0.137 QA and provisional 0.138 scope should be reconciled transparently before implementation. No claim that it is already implemented or verified.
