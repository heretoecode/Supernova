# Dependency/security review — unresolved findings

OSV was queried for all **135 unique Maven coordinates** in the existing 138-artifact runtime SBOM. The exact query scope and nine returned advisory records are retained in DEPENDENCY_ADVISORIES.json. Findings concern Jetty HTTP/server 9.2.30. This does not establish exploitation or reachability, and does not clear native, test or build dependencies.

Jetty is used by MediaLib's actual jUPnP servlet server/client. This is not merely an unused test dependency. Upstream MediaLib commit `44bf51b1de` explicitly retains Jetty 9.2.30 for Android API 23 compatibility: newer versions require LongAdder/API 24. Its cleanup is not a security version upgrade. No claim is made that adopting NOVA 6.6.2 resolves these advisories. A safe fix requires an API-23-compatible HTTP transport/security backport, or an explicitly approved minimum-SDK change. Neither dropping UPnP nor raising the minimum SDK is authorized by this maintenance task's feature/compatibility preservation constraints. **Remediation and real UPnP regression remain open; no security clearance.**

Upstream `e4696b972` mostly documents/suppresses lint findings and exports the suggestion provider; importing it wholesale would broaden Supernova's IPC access. Its later correction `ffd0eb50c` is adapted here by keeping the provider private. Existing cleartext allowance is necessary for configured local HTTP/WebDAV/UPnP sources; no TLS verification or certificate trust is weakened by this candidate.

Native buffer arithmetic port `96d5f4fb55` is included and passes sanitizer boundary tests locally. Native FFmpeg 9, OpenSSL 4.1 and libass migration are not treated as drop-in compatible replacements for the reviewed FFmpeg 8 runtime. Upstream inventory includes their exact observed revisions. ABI, native regression, licenses/SBOM, subtitle styles/settings and all four Android architectures need coordinated review before replacement.

No physical device, installed application, LAN provider or AV chain is available. No startup/playback/memory profiling numbers are invented. The work remains a draft maintenance candidate.
