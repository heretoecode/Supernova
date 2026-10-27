# Details improvements — implementation and validation

Authorised continuation of [PR #21 / 1f2a3394](https://github.com/heretoecode/Supernova/pull/21). Implementation branch: `codex/details-improvements-2026-10-27`, based on development tip `48e9cd6fbff47c120c013d5017f30cb3f3219030`. Initial working tree was clean. Main, the 0.142 development branch, PR #21, approved Settings designs and existing uncommitted work were preserved. No version bump, signing change, workflow change, APK assembly or release operation is included.

## Changes

- Language's specific globe invocation is removed from the Details fact row. Label/value, accessibility description and column alignment remain; other language icons are unchanged.
- Cast and Crew share an explicit 8-reference-dp row gap, retain four columns/eight-person cap, and remain read-only. The existing six-region composition reserves enough lower-row height for readable portraits/captions at enlarged text; Reception divider padding adapts at large text rather than clipping ratings.
- The hero summary has one writer. Matching partial metadata retains known nonempty facts and does not replace them with temporary missing/zero values. Remote Details seeds supplemental state from its already supplied title metadata. Known synopsis and classification survive optional failures. Fresh title/season/provider generations invalidate old callbacks and clear old-title artwork/content.
- Core metadata and credits publish before optional classifications, alternatives, extras, all-season metadata and recommendation/provider checks. Each publication snapshots collections. Details has its own two-worker lane, separate from provider availability work. Existing disk cache, request coalescing and stale-on-failure behaviour remain. Same-key enqueue is suppressed; failed loads can retry on subsequent binding.
- Information fields update in place; unchanged data avoids rebuilding the blueprint, tabs, people and adjacent sections. Async changes preserve the selected section/focus/scroll. Aggregate Reception changes still rebuild that bounded panel composition with semantic focus restoration.
- Artwork uses the actual measured target (`fit`/center crop), keeps the current same-title drawable during replacement, suppresses duplicate URI requests, disables fade and rejects stale callback bookkeeping. Failures retain the current image and have a 30-second per-URI retry cooldown. Cleanup cancels only a loader actually used; it does not initialise Picasso on teardown.
- Reception exposes **Read reviews** beside its heading, retaining space for the existing ratings. First Down on Details expands the panel; another Down focuses reviews; Up returns to the tab. Reader Back restores its opener. Movie and TV-series reviews are supported; episode/season facts are not relabelled as series reviews.

## Reviews: existing source, no additional credentials or fees

TMDB's existing application API access is reused through `StreamingRepository.reviews`; no account, API key configuration, new provider or OMDb activation was added. Official endpoint references: [movie reviews](https://developer.themoviedb.org/reference/movie-reviews), [TV-series reviews](https://developer.themoviedb.org/reference/tv-series-reviews), [API access and attribution](https://developer.themoviedb.org/docs/faq). Read-only live checks on 27 October returned HTTP 200 for movie 550 (12 reviews) and TV 1399 (15), with matching identities/page 1, using the already bundled access. No response bodies or credentials are retained in evidence. This verifies service access, not physical Shield presentation. Existing legal attribution in `assets/foundation/credits.json` is retained, and the reader identifies TMDB.

Reader states distinguish loading, successful empty, unavailable/error and saved content with unsuccessful refresh. Failed pagination retains previously displayed reviews. Language follows the existing metadata locale; no silent language fallback. Each request/page is identity/pagination validated before caching. At most five pages, 20 review entries/page and 12,000 characters/body are displayed; page/content limits are disclosed. Author/date/optional genuine score and validated TMDB source URL are shown. Bodies render as inert text; arbitrary response URLs are not followed. Generation/dismiss guards suppress late callbacks. Reviews load on demand in a separate lane and cannot delay core Details metadata or art.

## Automated validation and limits

**PASS: 68 Android tests in 16 classes, zero failures/errors/skips; 102 tooling tests; lint zero errors / 1,787 warnings.** See [TEST_RESULTS.json](TEST_RESULTS.json) for per-class counts, source hashes and limitations. [Before/after software renders](VISUAL_QA.md) cover 720p, 1080p, 4K and 150% text; four additional exact-baseline render tests pass. Tests run against an isolated prepared build copy with the repository's pinned MediaLib and FileCore commits and preparation patches; Android SDK 37, build-tools 36, JDK 21 and Gradle 9.5 were installed in the workspace. Runtime proxy/trust configuration is local test setup, not a repository change.

Run the focused Android tests using the existing secret-free unit-test variant (not an assemble task):

```sh
./gradlew testNoamazonReleaseUnitTest \
  --tests 'com.archos.mediacenter.video.leanback.details.*' \
  --tests 'com.archos.mediacenter.video.leanback.Supernova139SharedCardMetadataTest' \
  --tests 'com.archos.mediacenter.video.leanback.Details139AcceptanceTest' \
  --tests 'com.archos.mediacenter.video.streaming.StreamingRepositoryTest' \
  --tests 'com.archos.mediacenter.video.streaming.StreamingProviderFocusTest' \
  lintNoamazonRelease -PfoundationVerify -PsupernovaQa -Puniversal \
  --continue --no-daemon --no-build-cache --no-configuration-cache --max-workers=4 \
  -x :FileCoreLibrary:ndkBuild -x :MediaLib:ndkBuild
python -m unittest discover -s tools -p 'test_*.py'
```

The native renderer uses controlled illustrative content, fixture ratings and placeholder portraits, not real provider images or a device capture. No claim is made of reproducing `IMG_5035.mp4` or achieving an unmeasured Shield latency target. No native decoder rebuild, physical device test, signed upgrade or provider playback certification was performed. No APK is generated by these commands.

Regression tests cover language drawable removal, panel identity/update semantics, shared row gap, metadata collection snapshots and merge/empty/failure handling, core-before-optional delivery, stale generations/new-title clearing, target-art retention, review parsing/limits/provenance, identity/page rejection before cache, empty/error/page navigation, and reader/tab focus restoration. Existing Details/Extras/episode/season/recommendation/provider and native render tests remain in the selected suites. The episode-facts fixture now passes its actual current generation, preserving the stronger stale-result guard.

Initial runs exposed a too-small portrait at 150% text, the no-art Picasso teardown, review reachability, and a rating line-height constraint after spacing changes. These were corrected without relaxing visual assertions. Early attempts also needed local proxy/JDK/build-copy setup corrections; these are not passing application results. Physical before/after video and timing acceptance remain open.

## Remaining physical QA

[Shield checklist](SHIELD_QA.md) covers all requested changes and adjacent functionality. Run it on an authorised future test installation, especially cached/cold/slow/offline artwork and metadata, rapid title switching, 1080p/4K/enlarged text, review scrolling/pagination and Back restoration. Automated correctness does not establish real Shield timing or visual acceptance. Settings implementation and release/signing authorisation remain separate.
