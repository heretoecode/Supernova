# Software visual review and limits

The corrected conformance artifact for source `c92c68d617cc6fe1e11a1f5cdd032a919811ffaa` was independently downloaded, its archive SHA-256 matched, and its XML totals were checked: 1,147 tests, no failures/errors/skips. It contains 150 software component captures. Representative Details size/text variants, playback HUD/track popup and regional flag fixtures were inspected. These fixtures include legacy component states and are not a physical capture of every current route.

The retained 0.144 native software renders in `docs/0.144/software-renders/` were also inspected for the 720p/1080p/4K Settings grid, large text and Audio/Video playback panels. Their source components/resources remain identical in this maintenance change. Large-text content relies on scrolling; software component captures do not establish complete on-device navigation or exact mock-up parity.

No UI source/resource redesign is included in the Jetty continuation. The final protected signed-QA suite passed all 1,158 cases, including existing automated render/focus tests. No Nvidia Shield screenshot, real AV-chain output or original A4 comparison is claimed. Actual device focus, text scaling, subtitle/HUD clearance, signed upgrade and reference acceptance remain pending.
