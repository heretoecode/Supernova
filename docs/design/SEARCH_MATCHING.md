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
