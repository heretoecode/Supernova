# Release lint review

Successful complete run40876969463 reports zero errors and1492 warnings, versus1795 warnings in the preserved0.145 report. Final post-fix40886034550 passes with zero errors/1489 warnings,306 fewer than the0.145 count. The three additional reductions are two exported internal services and the credential-fragment dismiss warning. No warning severity or advisory was suppressed to obtain this result.

The review found and fixed meaningful issues: six static playback-settings views retained their Activity after close; Java FTPS used permissive certificate defaults; two internal player/metadata services were exported without a caller contract; and a credential DialogFragment overwrote its framework dismissal listener even though its existing onDismiss already forwards the callback. The final APK gate verifies the merged service flags and existing dialog tests assert released references. FTPS has five real TLS regression cases on both host and Android23.

Other reviewed security/lifecycle warnings:

- Three TrustAll warnings originate in BouncyCastle EST/Commons Net library helper classes. The app/shared source has no EST enrollment client. FTPS now explicitly selects the default CA trust manager and strict hostname verifier; no permissive helper is selected. The library classes/version findings remain visible.
- Diagnostics and PreviewVariants store application contexts, not Activity contexts. The loader inner class is owned by its lifecycle-bound parent CursorLoader. Static-field warnings are retained even where close/application-context logic makes their general pattern overbroad; no physical heap/leak clearance is claimed.
- The exported Android TV search provider retains a fixed read-only title/icon/id projection, bound SQL query arguments and unsupported mutation operations for system search. Internal media providers retain their Supernova authority/isolation rules.
- Cleartext permits explicitly configured dynamic local HTTP/WebDAV/UPnP hosts. Replacing this with a fixed domain allowlist would remove current local-server support. HTTPS verification remains strict.
- Trailer JavaScript serves an11-character-validated YouTube embed; file/content access and mixed content are disabled, navigation is restricted to YouTube hosts and the WebView is destroyed on dismissal. This is a functional browser feature, not a blanket claim that WebView has no risk.
- CutPasteId reports are repeated lookup of the same intended card/text view on separate retained styling/controller paths. No mismatched target was found.
- PIP auto-enter is optional behavior, not enabled as an unsolicited playback policy change. UsableSpace remains a conservative free-byte preflight; storage acquisition exceptions are still handled.

The largest remaining groups are unused resources, RTL/layout styling, private AndroidX resources, text localization and density sizing. Deleting shared alternate-layout resources or converting all geometry/localization in this maintenance would require separate visual/RTL acceptance.212 compatible semantic/focus attributes and327 safe translations were already integrated. The full per-issue final counts are recorded with final lint evidence; the physical/RTL/reference QA limits remain explicit.
