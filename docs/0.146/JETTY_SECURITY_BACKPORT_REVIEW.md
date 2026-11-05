# Jetty0.146 source remediation candidate

The candidate replaces all seven9.2.30 modules with full-module rebuilds from Jetty9.4.x commit `89d2ac7c7792e51cd4b5b633f249fc4c32e5c67b`, based on public9.4.58, plus the explicit locked patch. No duplicate-class overlay, TLS bypass, API-floor increase or advisory suppression is used. The official upstream coordinate/version remains9.4.58 for scanner visibility. `META-INF/SUPERNOVA-BACKPORT/jetty-<module>.json` records the actual source and patch. The exclusive Gradle repository prevents accidental resolution of vanilla9.4.58 jars.

Fresh OSV queries of all seven9.4.58 coordinates no longer report eight of the nine0.145 findings. The ninth, CVE2024-6763, and three newer findings remain on the unmodified version and are addressed in the candidate source below. OSV still reports those version findings: source backport status must accompany the SBOM rather than pretending the upstream coordinate is fixed.

| Finding | Concrete candidate change and evidence |
|---|---|
| CVE2024-6763 / GHSA-qh8g-58pp-2wxh | Retain upstream authority character/IPv6 validation, complete terminal percent-encoding checks and user-info/password-vs-port parsing following upstream PR12012. Direct HttpURI negative control accepts `http://host%`; patched test rejects malformed host/port/IPv6/encoding while preserving valid IPv6, encoded paths and user-info. |
| CVE2026-2332 / GHSA-355h-qmc2-wpwf | Backport the fixed modern chunk-extension state machine to Java8 and the bounded9.4 int chunk representation. Validate token names/values, quoted text/escapes and overflow across every buffer split. Unmodified9.4.58 accepts the unterminated quote; patched tests reject it before message completion. |
| CVE2026-6790 / GHSA-7p3p-8qv8-m2vh | Reject conflicting absolute request-target authority and Host before servlet dispatch, comparing case-insensitive hosts and normalized default ports. Unmodified real LocalConnector returns200 for mismatched authorities; patched transport returns400 and zero servlet calls. Matching/default-port forms remain accepted. |
| CVE2026-10050 / GHSA-2fvj-hgj9-j2gr | Both server and client Digest reject non-Latin1 inputs before hashing, preventing lossy replacement with literal question marks. Valid Latin1 credentials remain supported. No unsupported UTF8 Digest feature is claimed. Separate negative controls reproduce server password collision and client header emission; patched tests reject both. |

`Jetty146BackportTest` passes11 cases, including thousands of chunk-input segmentation checks and concurrent counter updates. All five vulnerability negative controls fail with the expected AssertionError against unmodified9.4.58, with only the API23 helper classes supplied to allow the same test class to load; none fails because of a missing class. The inherited11 actual TCP/servlet/jUPnP tests also pass against the rebuilt modules.

JDK striped counters are replaced with AtomicLong add/max helpers because Android23 lacks LongAdder/LongAccumulator. No public Jetty signature is changed. The concurrency test verifies40000 updates, maximum and reset behavior. This trades striped-counter throughput for compatibility; actual high-volume server throughput and real-provider performance are not measured.

The actual Android23 framework harness40846176706 passes all23 transport/trust cases using the current jUPnP fork, full source-built modules and NIO desugaring. Independent artifact/XML verification confirms zero failures/errors/skips. Final packaged-APK conformance is a separate gate. Real IPv6/provider discovery/streaming, a physical Shield and independent external security review remain unverified. The0.145 boundary protections remain applied.

The final authority pass also rejects encoded DNS-host delimiters through string and component constructors/setters, and preserves RFC6874 IPv6 LAN scope IDs. Scope syntax is validated without DNS/interface lookup; the resolver decodes the zone delimiter once. New tests cover component-builder bypasses and numeric IPv6 scope resolution. The source build now uses fuzz-free GNU patch application with mandatory source assertions and pinned Temurin8u504 boot classes; this fixes the earlier Git-context patch-skipping defect. The expanded exact-source standalone suite passes22 cases. The final independent builds have identical hashes for all seven source-built module archives; JETTY_REPRODUCIBLE_BUILD.json supersedes the older20-case receipt.


Actual Android23 dexing identified five signature-polymorphic MethodHandle calls in four optional classes: ModuleLocation (Java9 module discovery), CustomRequestLog (dynamic format-chain logging), ForwardedRequestCustomizer (reverse-proxy headers) and DecoratingListener (dynamic decorator reflection). These features are not used by Supernova’s UPnP servlet/client or the jUPnP fork. The source build still compiles every module; the explicit lock omits these four classes and their nested classes from the Android transport artifacts. TypeUtil already catches failure to load ModuleLocation and falls back to code-source/class-loader locations. No UPnP method, async servlet, IPv6, SOAP or TLS behavior is removed. This is a scoped Android transport fork, not a general Jetty9.4 drop-in supporting arbitrary optional server features. The omission list is included in each backport marker and build receipt. The actual Android23 harness now dexes and passes23 tests with those explicit omissions; no minSdk increase or unsupported-API suppression is used.

The first actual Android23 execution ran23 tests and exposed missing java.nio.file.Path/LinkOption in the default desugared library flavor. ContextHandler’s resource-alias checker requires NIO even for the SOAP-only servlet; simply not serving files does not remove that class-initialization dependency. Use Google’s2.1.5 `desugar_jdk_libs_nio` flavor consistently in Video, MediaLib, FileCoreLibrary and the separate API23 harness. This supplies supported NIO backports at API23; it does not raise minSdk or weaken alias checks. Both the library archive and configuration artifact have been independently hash/POM checked in the final139-artifact SBOM and offline legal catalogue.

## Each of the nine inherited findings

The full runtime OSV response is retained in MAVEN_ADVISORY_QUERY.json. The eight rows below marked absent refer to that exact-version query after replacing9.2.30 with9.4.58; they are not claims of an independent exploit reproduction for every historical CVE. The inherited boundary regression suite remains applied.

| Legacy advisory | CVE | Final disposition |
|---|---|---|
| GHSA-26vr-8j45-3r4w | CVE-2021-28165 | No longer returned for9.4.58; configured UPnP connector remains HTTP and other HTTPS providers retain their own verified TLS. |
| GHSA-84q7-p226-4x5w | CVE-2017-7656 | No longer returned for9.4.58; actual TCP framing/length/pipelining tests pass with the new parser and retained boundary guard. |
| GHSA-9rgv-h7x4-qw8g | CVE-2018-12536 | No longer returned for9.4.58; opaque-error/secret-path and preserved SOAP-fault tests pass. |
| GHSA-cj7v-27pg-wf7q | CVE-2022-2047 | No longer returned for9.4.58; malformed outbound authority tests and validated IPv6/encoded-path cases pass. |
| GHSA-hmr7-m48g-48f6 | CVE-2023-40167 | No longer returned for9.4.58; signed/duplicate/overflow lengths and transfer-framing conflicts rejected before servlet dispatch. |
| GHSA-m6cp-vxjx-65j6 | CVE-2021-34428 | No longer returned for9.4.58; NO_SESSIONS remains configured; no clustered session feature introduced. |
| GHSA-p26g-97m4-6q7c | CVE-2023-26049 | No longer returned for9.4.58; no cookie-login/rendering feature introduced. |
| GHSA-qh8g-58pp-2wxh | CVE-2024-6763 | Still returned by version query; complete locked authority-validation backport and original9.4.58 negative control documented above. |
| GHSA-qw69-rqj8-6qw8 | CVE-2023-26048 | No longer returned for9.4.58; no multipart-enabled upload servlet introduced. |

No legacy item is left as an uninvestigated blanket deferral. The remaining transport limits are the explicit optional-class scope, scanner-visible version findings with source backports, real-provider/physical-device interoperability and independent external security review.

The final expanded Android23 run40884866451 passes all28 cases: the same22 Jetty cases and NativeTrustRoots case, plus five actual FTPS/TLS cases. The independently verified receipt pins the exact Jetty lock, MediaLib/FileCore patches and20 selected runtime archives. It supersedes the narrower23-case receipt for the final APK gate.
