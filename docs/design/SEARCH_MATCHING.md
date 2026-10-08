# Search & Matching — Design and Behaviour Authority

Status: **APPROVED/current; Find a Match corrections active**

## Search
One left heading: **Search**. Remove duplicate upper-right Search/subtitle.
Query display placeholder: **Search Movies and TV Shows**. It is a non-focusable display field with subtle neutral/translucent boundary, no permanent blue state and no X.
Search opens with keyboard focus on **T**.

Keyboard rows exactly:
1 2 3 4 5 6 7 8 9 0
Q W E R T Y U I O P
A S D F G H J K L (inset)
Z X C V B N M (further inset)
Clear | Space | Backspace
No Caps/Shift/123. Traditional stagger, dark/translucent backdrop, subtle unfocused key surfaces; focused key gets compact Supernova-blue outline/glow with white character.

Keyboard is primary. Appropriate right-edge key RIGHT enters first result when results exist; result LEFT returns to remembered last keyboard key. UP from number row enters top nav. No-result edge remains keyboard. Results update only when query changes, not on focus changes.
Details return restores query, results, scroll and exact result focus.

## Results
Preserve successful live search and series/version routing. Parent series may appear prominently above episode rows. Result distinctions should use real filename/resolution/version data rather than fabricated quality.

## Find a Match / metadata correction
Matching is shared between Unmatched and More→Edit/Correct. Search field and review flow must make the selected identity explicit before persistence.
Current Shield corrections:
- keyboard focus/navigation;
- visible input box;
- typing must retain keyboard focus;
- result posters must render correctly.
Manual correction must preserve playback/watched state, row membership, physical file associations and versions where applicable. It must not preserve an incorrect identity merely to preserve its artwork.

## Evidence
`docs/preview-4.1.7/REQUIREMENTS.md` UI-020–021, matching requirements and UI-047–049; 4.1.2 handover/audit; 4.1.7 Shield QA.


## Next-version Search / Find a Match amendments — APPROVED

### Forgiving matching
Normalise case, punctuation/periods, apostrophes, hyphens, whitespace and leading The/A/An where sensible. Exact/near-exact results still rank highest. Examples: `OC` → **The O.C.**; `dark knight` → **The Dark Knight**; `schitts creek` → **Schitt’s Creek**. Apply equivalent UX to local Search and online Find a Match where technically possible.

### Keyboard hard boundaries
Hard edges stay on the same key with no flicker: number row + Up; Q + Left; Clear/Space/Backspace + Down. P/right edge + Right deliberately enters results when results exist. Do not transiently focus the input and then correct it.

### Find a Match result presentation
Use normal Search as visual authority for result row height, artwork slot dimensions/alignment, text start, title hierarchy, secondary metadata, synopsis treatment, vertical spacing and focus treatment. Fields may differ because the source differs.


## Future discussion — landing guidance and local-library metadata search (8 October 2026)
**Not yet approved for implementation; no change to the current Search correction scope.** On Search entry, keep the approved left keyboard and provide concise right-side instructions with appropriate icons explaining title search normalisation (e.g. `OC` for *The O.C.*, `Terminator 2` for *The Terminator 2*) and potential TMDb/IMDb **ID-based** search. Verify identifier handling in code before promising it works. This is local-library Search, not online Discovery.

Investigate and design optional local-library searches by **cast/crew** (partial name or surname; all indexed roles including actor, writer, producer, director) and **studio/production company**. Results should explain the matched credit, e.g. Spielberg — story/writer on *The Goonies* versus director on *Saving Private Ryan* where the local metadata supports those credits. Do not invent credits, assume complete provider data or open person profile pages. Genre search remains undecided; year is a filter, not a requested text query. Preserve keyboard focus and existing Search layout authority. See `docs/design/PARKED_FUTURE.md` for status.

### App-wide audit confirmation (8 October 2026)
The source audit confirms current local Search does **not** search indexed studio/network, cast or crew credit fields, nor display matched person's role. Future approved discussion direction: search locally indexed media by studio and TV network, cast and crew (including director, writer, producer, story credit), allow partial person names, and display the exact matching credit/role beside each matching title. Only show media in the user's library; account for metadata coverage and multiple roles. This is **not implemented** and not part of the fixes-only release without separate authorisation.

### Search result design accepted (8 October 2026)
User likes the illustrated result treatment showing **the exact matched credit/role** beneath the library title (e.g., `Director: Steven Spielberg`, `Story by: Steven Spielberg`). User wants this implemented in a future authorised feature scope. Discuss additional searchable metadata: production studio/company, TV network, genres, franchise/collection; plot and themes optional due relevance; year, country/language, streaming provider better as filters. No implementation authorised by this documentation decision; source audit confirms credit/network search is not currently implemented.

### Recovered search empty-state reference — 8 October 2026
- User supplied historical Search mockup and **explicitly excluded the left-side custom keyboard**. Only right-hand guidance panel is relevant: heading `Search Supernova`, subheading `Just type what you're looking for.`, with concise capability hints `No exact formatting` (special characters optional), `Articles optional` (The/A/An), `Alternate titles` (known international variants), `Minor typos OK` (small spelling errors).
- Treat each guidance claim as a **capability requirement subject to verification**: do not present typo tolerance, article handling or alternate-title support as implemented until confirmed by code and tests. Maintain approved local-only search and match-role annotations for cast/crew/studios/networks; no online discovery implied.
