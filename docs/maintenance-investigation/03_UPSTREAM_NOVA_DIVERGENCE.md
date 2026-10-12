# 03 — Upstream NOVA divergence and comparison method

## Exact ancestry

**VERIFIED:** Supernova is a registered Video fork. Full history gives Video common ancestor `f53a1fc055d1d550a2edad4992bf82f9d12a7872`; first custom commit `636c75c5731c91c1a4a5b972fc943abe5a055e1d` parents it directly. The build manifest's upstream Video pin is `0651a3e0b60ada996a9774ab527d7c1ef34b7503`; actual Video source is the Supernova checkout overlay, not that pin. Thus the historical NOVA 6.4.64 derivation is valid release-family context, not byte identity with a tag.

**VERIFIED:** AVP v6.4.64 resolves to `9c5842b74ab180c99441ed23fdd5d508448388a7`; v6.5.5 resolves to `eacf19d5ff8de2e250f77a639dfe9c12429ed076`. AVP is orchestration, not the application repository. v6.4.64 references moving Video/MediaLib/FileCore `v6.4` and AVOS `android_sync_back`; v6.5.5 references moving `v6.4-lint` component branches and FFmpeg `v8_0`. A release tag does not freeze those component commits.

## Frozen three-way comparison

| Component | Common base | Supernova source pin | Current upstream snapshot | Unique histories base→SN / base→up |
|---|---|---|---|---|
| Video | `f53a1fc055d1d550a2edad4992bf82f9d12a7872` | `b3504c2c4b1725f3070416d14b63e53ae1db3d74` | `e394eda85058d076c1b85adf7b64a788068d2c8a` | 464 / 90 |
| MediaLib | `53ba67259afa100e0898d7b6500413fb027d4b6f` | custom `5758074049bc153ac4967b4ecd21886469270076` | `34e319942e8f9df965bb7feae74e939e232f67c4` | 4 / 23 |
| FileCore | `c4b760c55102c72d45f68b0d16999fc1f4f013a7` | same + 3 workflow patches | `93e560b086f60b0e5080267d18613749d4d6cd7b` | 0 / 46 before patches |
| AVOS | `2cf21c486c7bf244c49b78d2220197a6d75396dd` | same + 2 workflow patches | `08ac39b6fa21396044963bace7cc3d60fedff695` | 0 / 60 before patches |

[Machine evidence](evidence/component-comparisons.json) includes full immutable references. The exhaustive metadata inventory has 219 upstream rows. Video has 754 Supernova-only changed paths, 456 upstream-only and 44 changed on both sides. MediaLib has 3 Supernova-only, 36 upstream-only and 5 both. Raw pins show 56 upstream-only FileCore paths and 133 AVOS paths; **effective overlays add Supernova overlap** and must be inspected using [effective comparison](evidence/effective-versus-upstream.tsv). Git file overlap is not textual conflict proof.

For each component, compare `base→Supernova` and `base→upstream`, then the effective Supernova content including its ordered build overlays against upstream. Exact-context reconstruction was executed in memory; no patches were applied to app checkouts. The plain unified-diff last two overlays are included, as well as Git-format patches. [Overlay evidence](evidence/effective-component-overlays.tsv) records final hashes; [patch identity checks](evidence/patch-identity-checks.tsv) found no full-patch identity matches. Bundled/adapted patches can still contain the same fix, so a failed patch-ID match is not evidence of absence.

## Upstream release and development state

**VERIFIED at collection on 2026-10-12:** latest public AVP release is **v6.5.5**, published 2026-10-09T19:19:45Z, not a prerelease. Releases between 6.4.64 and it: 6.4.66 (prerelease), 6.4.71, 6.4.72, 6.5.1 (prerelease), 6.5.2, 6.5.3, 6.5.5. [Release metadata](evidence/upstream-releases.json) supplies provenance. Current Video default is v6.4-lint at `e394eda8...`; it includes post-6.5.5 marker changes such as grouped-row stability, translations, dependency changes and updater-dialog focus. The Video 6.5.5 marker is `3e26a8b3`, not a release binary provenance guarantee. Other development branches include dev/exoplayer and v7_exoplayer; branch existence does not make ExoPlayer an active Supernova engine.

**UNKNOWN / INCOMPLETE release reconstruction:** exact sibling-module commits built into the distributed v6.5.5 APK were not recovered from an immutable release build manifest or APK/source bundle. The frozen current heads are documented alternatives for source comparison. No assertion that all audited current-head changes shipped in v6.5.5 is made. Before integrating a coherent release set, obtain/verify the actual build pins and native artifacts.

## Major independent changes and compatibility surfaces

**VERIFIED:** Supernova's MediaLib four commits add user-collection tables/provider routes, preserve metadata without artwork, serialize/coalesce scans, and retry requested scans despite stale reachability. Workflow overlays additionally alter provider scan gates, local import traversal, subtitle/audio metadata enum fields, privacy and source policy. These are not upstream equivalents merely because their class names match.

The highest-risk overlaps are:

1. **Database v62 collision:** Supernova custom collections vs upstream genre IDs. Neither schema can be inferred from `user_version=62` alone. Existing installed v62 does not enter a `<62` migration. Preserve both stores and use a separately approved successor migration, with schema fingerprints/column checks and backup plan.
2. **Native API metadata:** Supernova adds Dolby Vision profile/HDR10+ and audio/subtitle metadata; upstream changes IMediaPlayer/LibAvos/AVOS enums, AudioTrack routing and descriptor ownership. Replacing one module can misalign JNI and metadata indexing.
3. **Network relay ownership:** Supernova already carries HTTP range/unknown-length/cancel logic; upstream adds read-purpose policy, bounded backend refills and readiness. Import the missing delta rather than replacing StreamOverHttp and losing Supernova changes.
4. **Player/SurfaceController:** upstream lifecycle work is cross-module, while the same files own Supernova startup overlay, HUD, resume/checkpoint and track acknowledgements. Preserve visual behavior and identity history.
5. **Scanning:** custom ProviderDiscoveryGate/scan completion and upstream category-refresh deferral both coordinate scan/scrape publication. Lock order and final completion events must be reconciled.

Anchors: [MediaLib/VideoOpenHelper.java:50](https://github.com/heretoecode/aos-MediaLib/blob/5758074049bc153ac4967b4ecd21886469270076/src/com/archos/mediaprovider/video/VideoOpenHelper.java#L50), [MediaLib/ScraperTables.java:1590](https://github.com/heretoecode/aos-MediaLib/blob/5758074049bc153ac4967b4ecd21886469270076/src/com/archos/mediaprovider/video/ScraperTables.java#L1590), [medialib-supernova135.patch:231](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/.github/build/medialib-supernova135.patch#L231), [avos-supernova135.patch:22](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/.github/build/avos-supernova135.patch#L22).

## FFmpeg correction and exclusions

**VERIFIED configuration and attributed QA evidence:** the signed build uses the Foundation rebuild lock for **FFmpeg n8.0.1**, source `894da5ca7d742e4429ffb2af534fcda0103ef593`, plus recorded patches. The generic pinned builder script's n9.0.1 target is not used as proof of the shipped FFmpeg. 0.138 BUILD_QA and signed receipt connect 24 rebuilt FFmpeg binaries to the APK; those are previous executed checks, not repeated here. The earlier mismatch concern is superseded for this exact QA build by explicit rebuild provenance. See [ffmpeg-foundation.lock.json:4](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/.github/build/ffmpeg-foundation.lock.json#L4), [supernova-0138-qa.yml:85](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/.github/workflows/supernova-0138-qa.yml#L85).

**PROPOSED, preserving prior owner decisions:** exclude upstream UI/resources/navigation and updater. Adapt only separately reviewed nonvisual fixes in UI-bearing files. Treat swscale/libyuv removal, FFmpeg generation, buffer policies and native route API as a coordinated technical migration, with exact binary/source provenance. Do not independently bump FFmpeg to a newer patch while establishing that combination.
