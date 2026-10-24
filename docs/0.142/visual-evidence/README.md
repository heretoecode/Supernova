# 0.142 visual evidence and limits

These are native Android view renders with controlled fixture data, not physical NVIDIA Shield captures. Target resolution is 1920×1080. Reference PNGs remain byte-for-byte preserved under `docs/design/assets/network-files/` and were inspected before implementation.

## Editor before/after

`editor-before/`: actual `CustomLibraryActivity.java` from `4f0937f1` rendered in the same test harness, with a deliberately busy checkerboard behind the editor. All five steps are captured. The new contrast test intentionally fails against that original source. This is a software reproduction, not the owner's original photograph.

`editor-after/`: corrected renderer, same steps and fixture. Layout, Live Preview and controls remain present. The test also alternates black/white artwork and records the maximum sampled RGB-channel difference inside blank panel areas in each `*-contrast.json`. Baseline25; corrected1 on all five steps at the recorded checkpoint. This measures background interference, not physical TV luminance or accessibility acceptance.

## Network & Files comparison

The final `network/` capture set is produced by Network142RenderTest with the actual TopNavigation + PreviewPages + browser shell. Final captures are downloaded from passing CI `40058921765` at `b347707d`; application inputs remain unchanged from `08a896b1`. The capture harness (introduced in `51e1ac59`) adds recursive invalidation before bitmap drawing and asserts visible static brand/rail glyphs in all eight frames. This prevents intermittent omissions of cached static text in repeated Robolectric draws; no application redraw/layout change was made. The separate capture regression passes (test-runs/capture-validation.json). The fixture declares internal/USB paths and empty remote accounts; it does not assert a physical mount or successful external connection.

| Reference | Implemented correspondence | Deliberate source-grounded differences / pending evidence |
| --- | --- | --- |
| R1 Overview | Fixed six-section rail, workspace, four stacked information cards, no Quick Actions, monochrome outline icons, existing Space Black background/font/focus | Existing global navigation replaces the mock-up header as required. Canonical rail x32/width352/workspace x404 follows blueprint values. Real dataset controls card height; workspace scrolls. No fake capacities, connected status, quota or library totals. Physical appearance and populated-data comparisons pending |
| R2 WebDAV storyboard | Network Shares, exact six-protocol picker, original address/port/path/user/password/save/show controls and keyboard, actual listing/error/retry path | Connect wording retained; no invented Test Connection success, display-name registry, save-verification or progress phases. Save Location/library membership remain separate actions. Loopback PROPFIND validates email username/backend listing, not Shield/TLS/server compatibility. Full physical state atlas pending |
| R3 Put.io storyboard | Cloud Services with only Put.io, existing device authorization, account actions, associations and embedded read-only API browser | No Drive/Dropbox/OneDrive, manual token entry, hardcoded code/link, fabricated account/quota or new file operation. Existing OAuth QR/modal behavior is retained. Real account authorization/association/screenshots pending |

Geometry and visual review caught and corrected excessive surface brightness, overlarge normal-page row spacing, verbose dashboard scan text, duplicate Health root navigation and the rail's missing shared travelling focus component. No pixel-perfect or physical visual PASS is claimed. The mock-ups are concept art; explicit blueprint geometry and the delivered app's global font/navigation win where they differ.

## Outstanding visual gates

Every live loading/error/provider state, populated network/account/Health lists, long-list and enlarged-text physical captures, TV overscan, USB reconnect and actual remote navigation remain on SHIELD_QA.md. The screenshot atlas must be completed and accepted before release requirements can be marked satisfied.
