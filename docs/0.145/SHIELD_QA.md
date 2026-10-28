# 0.145 physical and external QA — pending

No device is attached. Every row below remains **PENDING**. Install only the signed QA candidate identified in SIGNED_QA_DELIVERY.json; software conformance does not establish these results. Retain an existing backup before testing upgrades. Do not clear application data to make an upgrade appear successful.

| Area | Required observation | Status |
|---|---|---|
| Signed installation / upgrade | Existing 0.144 installs upgrade to app.supernova.player 0.145/145 without signature conflict; saved sources, libraries, settings, history and provider grants survive. | PENDING |
| Restore / rollback | Confirm backup/export/restore on disposable test data and document the exact package/data rollback route. Android version-code downgrade restrictions apply; matching certificates alone do not establish rollback. | PENDING |
| Preserved interface | Settings eight categories and 165 definitions, custom pages, Details ratings, HUD, age overlay, regional flags, artwork and focus match approved references. | PENDING |
| Resolution / accessibility | 720p, 1080p, 4K and large text; remote navigation, scrolling, focus return and subtitle/HUD clearance. | PENDING |
| Network recovery | Fault/reconnect while playing and while seeking; latest requested seek and journey bookkeeping survive, without a reopen loop. | PENDING |
| Subtitle timing | SRT/VTT and image subtitles across pause, replacement, clear, seek, resume and teardown; no stale cues or timing regressions. | PENDING |
| Audio / video | Real Shield/receiver/TV chain, passthrough and HDMI reconnect, AV sync and hardware decoding of representative media. | PENDING |
| UPnP security compatibility | Discovery, browsing, playback, subscriptions, renewal, async NOTIFY and IPv4/IPv6 devices work with one-exchange HTTP connections; errors omit private paths. Include older devices and streaming bodies. | PENDING |
| WebDAV / storage | Real confirmed404, auth/offline/partial listings and reconnect preserve unconfirmed records; removal versus permanent deletion and sidecar behavior remain correct. USB/external/unmounted/denied storage uses safe cache/access behavior. | PENDING |
| Scan / pages | Long/interrupted/coalesced scans; rapid custom-page switching, empty/artless pages, stale artwork cancellation and correct independent focus. | PENDING |
| Native memory pressure | Representative large/streamed media, bounded-buffer failures and repeated playback lifecycles on actual device. | PENDING |
| Performance / leaks | Record reproducible cold/warm startup, playback start, artwork/network/scan timings and retained-memory trends over repeated journeys. No host fixture timing is a hardware benchmark. | PENDING |
| Original evidence | Recover original A4/hero photographs and October27 diagnostic ZIP; compare/reanalyse before claiming those requirements accepted. | PENDING |

The software validation and known deferred library/native work are recorded separately in RELEASE_REPORT.md and JETTY_SECURITY_DISPOSITION.md. This checklist records required future observations, not completed execution or automatic acceptance.
