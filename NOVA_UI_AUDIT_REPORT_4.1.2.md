# Preview 4.1.2 UI audit

Authority: latest written 4.1.2 overrides, approved images, valid recovered implementation, 4.1.1 baseline, then older material. Reference images were inspected; not treated as inspiration.

Build `35506060249`, source `53cf382800ca0fbc3261456aaebdf4b99611241d`. Signed release and minimum smoke checks passed. Final runtime and fixture captures were reviewed against the supplied reference and written overrides. Implementation is not equivalent to physical acceptance; unverified populated behaviours are not marked DONE.

| Surface | Source implementation | Visual/runtime acceptance |
|---|---|---|
| Search | Compact six-column keyboard, simultaneous field/results, larger parent-show result from local index | Empty/query runtime layout reviewed; no system keyboard overlay. Populated show/episode selection requires QA |
| File browser | Primary source/path/list/context composition; classic root detached; compact rows | Actual local-folder route and three-panel empty state reviewed; status-bar overlap corrected. Populated network/USB requires QA |
| Settings | Standard 16:9 three panels, nine categories, focused help/options, first-setting focus, no slide-in | Runtime reviewed: first Playback setting focused with matching help; categories accessible; no old full-width layout |
| Exact utility background | Supplied JPEG retained byte-for-byte | Asset hash PASS; runtime use reviewed; duplicate body/background seam removed in Search/Settings |
| Global focus | Underline/bloom, icon tint/scale, no poster focus box; only artwork dims | Source inspected; physical remote visibility/performance pending |
| Top navigation | Media left; Network & Files/Search/cog/clock right | Runtime grouping/position reviewed; translucent rather than live backdrop blur |
| Playback HUD | One centre Play/Pause; XML and runtime hide other transport buttons; audio/subtitles/More retained | Final layout fixture reviewed. Real seeking/focus, labels and logo scale require QA |
| Details actions | Compact grouped headers, semantic icons and provider names/artwork | Movie/episode Details fixtures reviewed; populated Actions/provider display requires QA |
| Information | Centred dimmed overlay, no X, Back restoration, compact badges | Source checked; populated overlay visual acceptance requires QA; no fabricated ratings/reviews |
| Library summary | Indexed total/local/network count/size hierarchy above controls | Runtime empty hierarchy and populated synthetic list/grid reviewed; mixed-source counts disclosed |
| Featured and startup | Measured compact Play/Resume action; indicators focusable; waits for visible artwork callbacks | Full Resume text verified in final fixture; process startup/restarts passed. Populated Shield artwork timing requires QA |
| Customise Home | Content-related maximum height and narrower footprint; row handlers retained | Retained row-operation tests passed. Test window is forcibly resized, so no real dialog-footprint acceptance claimed |
| Provider selection | Genuine artwork, selected section then alphabetical alternatives, stable tick updates | Persistence regression tests passed; live catalogue/country/artwork visual QA required |

## Deliberate, documented implementation limits

- GPU alpha supplies the allowed lightweight artwork-depth treatment; no expensive per-card live blur or caption blur.
- Navigation is translucent; no claim of live frosted backdrop blur on the Shield/API 28 path.
- Settings context lists actual values/options and explanatory text. The subtitle sample is illustrative text, not a live renderer of all subtitle settings; hardware capability detection was not invented.
- Browser source rail identifies the current source and returns to source selection; unconfigured servers and fabricated source sizes/counts are never displayed.
- Provider selection groups remain stable during a selection session to preserve the passed no-flashing behaviour; reopen to regroup current selections.
- Information exposes available metadata/technical badges, without unsupported review services or portraits.

For every unverified physical behaviour: NOT TESTED — USER QA REQUIRED.
