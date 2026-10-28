# 0.145 verification checklist

`implemented` means source exists. `automated-tested` requires the actual 0.145 run; inherited 0.144 results alone do not establish it. No row is visually Shield-verified.

| Requirement | Source disposition | 0.145 regression gate | Physical / external evidence |
|---|---|---|---|
| WebDAV confirmed absence, removal vs deletion, safe reconciliation | 0.144 implementation retained; no reconciliation patch removed | WebdavDeleteResponseTest, WebdavTraversalCompletenessTest, WebdavReconciliationSelectionTest, Network142WebdavTest | Real 404/auth/offline/partial scan/reconnect pending |
| Custom-page focus and artwork generations | PreviewPages/PreviewBackdrop retained | CustomLibraryPageTest, Home/library/artwork suites | Rapid Documentaries switching, empty/artless pages pending |
| Scan action/progress/coalescing | PreviewLibraryScan/UniversalFileBrowser retained | Network142Test and library scan suites | Real long and interrupted scan pending |
| A4 playback controls and HUD | No UI source replaced | PreviewPlaybackMenusTest, Playback141RenderTest, Supernova138PlaybackTest | Exact A4 references unavailable; Shield pending |
| Eight Settings categories and 165 audited definitions | Mapping/assets/handlers retained | SettingsWorkspaceTest, SettingsShieldNavigationTest, Python mapping checks | 720p/1080p/4K, large text, real remote pending |
| Locale flags and purposeful copy | PNG resolver and copy retained | Locale/asset/Settings tests | Shield regional/neutral icons pending |
| Preparation artwork and seek HUD | Source retained | Loading/seek/render regressions | Real source preparation and subtitle occlusion pending |
| Age rating timing/output gate | Source retained | PlaybackAgeOverlayTest and output tests | Real first established output and 10-second dwell pending |
| Details ratings, metadata and artwork stability | Source retained; no written-review reintroduction | DetailsImprovementsTest and DetailsReferenceRenderTest | Missing hero references; Shield pending |
| October 27 diagnostics | Historical source findings distinguished from current evidence | No claim of original report re-analysis | Original ZIP unavailable |
| Network retry and latest failed seek | Upstream port implemented | PlaybackNetworkRetryTest (2 cases) | Real network fault injection pending |
| Subtitle pause/replacement/clear | Upstream Java worker port implemented | SubtitlePauseTimingTest (3 cases) | SRT/VTT/PGS pause/seek/resume acceptance pending |
| Cache fallback and scoped storage | Sibling patches implemented | MediaUtilsSubsDirTest (4), StorageWriteAccessTest (3) | External/unmounted/denied storage pending |
| Native buffer overflow safety | Upstream AVOS patch implemented | Sanitized C boundary test passed locally; four-ABI wrapper build required | Real playback memory-pressure campaign pending |
| Database and stored settings | No schema/version/keys changed | Existing migration, restore, preferences suites required | Signed upgrade/rollback pending; no device attached |
| Dependency/security | 135 Maven coordinates queried; 9 Jetty advisories returned | Exact findings retained; remediation/reachability open | No security clearance |
| Native/runtime/source/legal reproducibility | Existing pins and strict checks preserved | Full conformance run must pass before signing | No prebuilt substitution permitted |
| Signing/delivery | New branch gated, existing certificate pin retained | Exact-source/payload/native/lint checks unchanged or strengthened | Owner must allow exact branch; no 0.145 artifact delivered |

Initial local verification: 128 Python checks passed. Native upstream buffer regression passed with `-Wall -Wextra -Werror -fsanitize=undefined,address`. These do not substitute for Android compilation, lint, all existing 1,135 Android tests plus the 12 newly added cases, or real hardware.
