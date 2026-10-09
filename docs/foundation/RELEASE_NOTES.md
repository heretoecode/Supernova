# Supernova Foundation — candidate 0.133

Status: implemented candidate, unsigned verification in progress; **not a signed release**. Physical Shield acceptance follows verified APK delivery. Version numbers are literal APK counters, so `0.10` follows `0.9`.

- Permanent Android application identity **app.supernova.player**, app label Supernova, isolated providers/task identity, and dedicated existing Foundation PKCS#12 signing workflow.
- Approved Space Black Blend, double-ring launcher icon (density/adaptive variants), Android TV banner, static splash and utility background. Existing Home hero imagery is preserved.
- Animated double-ring highlights in Preparing Playback and the existing Home scan information pill; concentric rings stay still and highlights move in opposite directions while the existing operation is active.
- About includes App Information, Release Notes, offline Open-source Licences, Credits & Acknowledgements with official-site QR codes, and read-only Technical Information. No in-app update checker or external-link launch.
- Retrospective `0.1`–`0.132` build history reconstructed from downloaded APK archives, actual manifests, hashes and source checkpoints. Development/QA builds are distinguished from feature/public releases. Historical APK binaries are untouched; gaps are disclosed.
- Runtime version reporting uses the literal counter without re-running obsolete NOVA version migrations against the new install identity.

This candidate is based on the existing interface/playback/library/network implementation and verified upstream source version Nova Video Player 6.4.64. It does not include the frozen maintenance release, custom Library Page or broader Settings/Network & Files redesign. Existing UI defects and optional integration limitations remain subject to the later fixes-only release and device QA.

Signing requires a corrected certificate pin, verified native corresponding-source obligations, complete automated conformance and the separate user readiness authorisation. See [implementation report](IMPLEMENTATION_REPORT.md), [readiness gates](READINESS.json) and [QA checklist](QA_CHECKLIST.md).
