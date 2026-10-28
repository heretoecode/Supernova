# 0.146 implementation status

This branch is an **in-progress integration candidate**, not a validated release. The verified0.145 application source and delivered APK remain the baseline.

The shared MediaLib, FileCoreLibrary and AVOS trees contain three-way integrations of the pinned NOVA6.6.2 release-time snapshots, preserving the0.145 patches. Video now includes the native subtitle/JNI lifecycle, routing and playback changes. Existing Supernova layouts/resources are retained, with an added transparent subtitle canvas and new resource names only. The existing TV/phone subtitle controls retain their keys; the native renderer receives an explicit conversion of the saved0..100/SP size into its720-line reference canvas. Collections/match-isolation schema63 is retained; genre IDs use an additive transactional schema63 extension.

All128 repository tooling tests pass. Native host suites passed13 ASan/UBSan and7 TSan cases; the corrected GitHub run40774320914 also passed all180 native regression cases. OpenSSL4.1 static cross-builds succeeded for all four ABIs at API21. The complete libass chain has built for ARM64; the other ABIs are being built. FFmpeg9 and final native/application validation are still pending.

Jetty9.4.58 passes the11 inherited UPnP tests but still has four OSV advisories, so a vanilla version bump is insufficient. A pinned-source fork adds strict chunk-extension parsing, authority/Host consistency, completed authority validation, lossless Latin1 Digest inputs and API23 atomic counters. Nine targeted tests pass. Negative controls are being verified against unmodified9.4.58; Android23 runtime validation is pending. The original Maven coordinates remain visible to advisory scanners; the fork is explicitly identified by source/patch hashes. No advisory is suppressed or declared cleared by a version relabel.

The GitHub signing environment currently has no rule permitting this new branch. No protection was changed and no signing attempted. Independent implementation and unsigned validation continue. A signed APK, checksum, Drive delivery and release acceptance are **not claimed**.
