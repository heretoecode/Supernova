# Supernova 0.145 maintenance candidate — work in progress

Baseline branch tip: `43cef0529518d406e11b3ab69e61abded07a12ad`; frozen delivered 0.144 app source: `4e6f1f8f5691c34cd429c17421f92b6290ce3539`. PR #24 and signing run 40735286063 verified through the GitHub API. GitHub artifact download returned HTTP 403; complete Drive APK readback subsequently succeeded. All 87,940,129 bytes match SHA-256 `a841b6bcb49296b8968a40eef1b305178bfadb5f26aff759e5ee6aef5552ea61`; ZIP CRC and independent apksig verification pass with the existing certificate. Prior signed-delivery receipts are preserved.

Latest official stable release at investigation: NOVA v6.6.2, AVP `5c5d4ec81b47ba59be8f91c9d877b49b0ab1c01e`, published October 27. AVP gitlinks still point at older 6.4.29 source; v6_6.xml selects mutable branches. The inventory records branch tips at release publication, not a falsely exact frozen upstream release manifest. Video's 6.6.2 version commit is `a4dcf44b3`.

Compatible ports in this candidate:

- `d0be0f1161fb9521ce9f6da44522f4b7e2e6fc9c`: actually reopen failed network playback and preserve the requested seek target. Retains Supernova session/journey bookkeeping; upstream production regression tests included.
- `12cdaea1e5889bef6823a94774c42b1e7074605b`: subtitle pause/replay duration, monotonic clock, cue clearing and worker teardown; port applies without interface changes. Three new behavioral timing tests.
- `d273346c98bdf809812f5f2e35ec288825ab2b7c`: HDMI receiver registration guard adapted with synchronized registration/unregistration. Supernova's background unregistration has callers and is deliberately retained.
- `ffd0eb50cfb1791b837b84b7bd142323468ee172`: upstream suggestion-provider disclosure fix adapted by explicitly retaining Supernova's private provider (`exported=false`), rather than exporting it for NOVA system search.

Additional compatible ports: MediaLib `cf75fdcd85822d1f58d90ec35d72f9a59e4a6672` uses the internal app cache when external subtitle cache creation fails (raw-path logging removed); FileCore `868496a9ab` rejects unsupported MediaStore `_data` mutations without legacy/all-files access. Four cache and three storage production regressions are added.

Native safety port: AVOS `96d5f4fb55` guards stream-buffer allocation arithmetic before narrowing to int, including ring overlap, double-copy capacity and parser prebuffer scaling. It applies on the patched Supernova native baseline. Upstream C boundary regression passes locally with AddressSanitizer/UBSan and is required in both new workflows. The wrappers will be rebuilt from patched source; FFmpeg/runtime pins remain unchanged.

No UI replacement, storage schema migration, dependency/runtime hash substitution or native ABI replacement is included. The release is **not complete**: per-commit compatibility/security review, native subtitle/FFmpeg/OpenSSL integration assessment, performance profiling and full automated validation remain open. UPSTREAM_DELTA.json is an investigation inventory, not integration clearance. All unported changes remain deferred pending review; none is declared safe or irrelevant by omission.

Signing blocker: `supernova-foundation-signing` deployment policies do not allow `codex/supernova-0.145-maintenance-2026-10-28`. Only the owner can authorize that branch. The new workflow retains all native rebuild, runtime, full-suite, lint, exact-source, signature and payload gates. No protection is changed or bypassed.

Physical Shield, real provider/AV chain, signed upgrade/rollback and exact approved-reference visual acceptance remain pending. Original A4 reference photos and October 27 diagnostic ZIP remain unavailable. No signed 0.145 APK, Drive upload, public release or main merge is claimed.

## Current validation findings

Full Android run 40747266276 on source `67da778e28f8e32af7db268e9ad7f6a8d7c3d5b4` executed 1,147 cases: 1,144 passed and three new storage fixture cases failed at the Android 30 framework permission probe. The denied-access fixture is corrected at `e9da70a97`; focused rerun 40751619925 is pending. Do not treat the full suite as passed. Native conformance run 40747434294 passed FFmpeg rebuild/runtime checks and is still running on the earlier fixture source. See VALIDATION.json.
