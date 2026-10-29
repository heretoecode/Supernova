# Verified source assets and shared tokens

- Canonical Space Black Blend: `assets/branding/Supernova_Space_Black_Blend_4K_UHD.png` and `res/drawable-nodpi/foundation_space_black.png`; SHA-256 `09c25a2f1a4419f0ed0c73a1b0f664d3936ee93b12d69a9e18a960fd786c96d4`. `PreviewUtilityBackground` uses aspect-preserving viewport cover, decoded at inSampleSize 2. Unchanged.
- Global navigation: `TopNavigation`, including system sans-serif-light brand/body, actual TextClock, tab order, focus underline, safe insets and existing progressive-shade allowlist. No replacement header or new font asset.
- Settings and network landing cards: `SharedThreePanel.control(context,9)`, existing PreviewAccent focus; network adapts to 3 columns, Settings stays 4. Both use 12-unit gutters and 16/14 inner padding; title 20, description 15, icon 30 at the 960x540 logical reference. Network uses the same .5–1.25 viewport scale as Settings. Large-text fallback scrolls rather than dropping cards.
- Rows: shared accent focus and subtle separators. Switches reuse `PreviewSettings.styleSwitch`, including `SettingsSwitchThumb`, checked accent track and unchecked #526174 track, with non-interactive control chrome inside a focusable action row. Preference keys and values are untouched.
- Icons: existing `PreviewIcon`; monochrome local/network/plus/settings.nodes/refresh/settings. No cloud providers or sample accounts from mock-ups are added.
- Network content: no mounted category rail. Dedicated browsing retains its file/context split, three toolbar controls, six sorts and actual breadcrumbs. Forms use a field/action column and contextual-help column within one scroll workspace.

The three historical network storyboard Git blobs exactly match their asset README: R1 6c6930b5f87b7c1c80e66f460a8f898942162ea5; R2 585994c522581e18715eecdd4b21593c422fb28c; R3 224536a2389572d9ae885fd6e5a2fd1d60f2be56. Their left rail, sample credentials/providers, capacities and test/save semantics are superseded or illustrative; they are not screenshot geometry targets for the newly approved six-card layout.

Software screenshot comparisons and physical calibration remain separate acceptance gates. No pixel-perfect or Shield visual pass is asserted from these source tokens.
