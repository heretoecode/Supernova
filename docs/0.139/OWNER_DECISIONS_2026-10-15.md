# 0.139 remaining decisions — 15 October recheck

These are proposed exceptions or missing design decisions, **not approved changes**. The existing implementation, failed acceptance test and future-release boundary remain intact. Starting HEAD: `c20e8e0f249cb9199514770d9b62284bfde94e06`; test-only follow-up: `cbc0af07`. PR17 remains draft against the existing138 branch.

## Sources checked

Decision branch refreshed to `ddf4f28a04846897588f74be1a64526a30aed95f`. Read the [authoritative139 handover](https://github.com/heretoecode/Supernova/blob/ddf4f28a04846897588f74be1a64526a30aed95f/docs/project-decisions/2026-10-13-supernova-0139-codex-implementation-handover.md), consolidated scope, backup owner decisions and [connected-frame decision](https://github.com/heretoecode/Supernova/blob/ddf4f28a04846897588f74be1a64526a30aed95f/docs/project-decisions/2026-10-13-details-connected-tabs-panel-design-decision.md), plus original Details forensic layout and edge reports at `89166c38dfbfcc205701e6aee33b254ad98b7f6d`, existing43-row ledger, screenshots, CI receipts and continuation checkpoint. Later future-release documents supply no new139 approval. No future0.140/FD-033 content layout is adopted.

## B43 — fixed sidebar versus small viewport

The handover states: “Existing permanent left Settings navigation panel and its width/menu are immutable.” It also requires all actual categories simultaneously visible without scrolling, readable labels and comfortable targets, and explicitly requests measurements/responsive proposals if impossible at a supported size.

Fresh connected native test at `cbc0af07`, using the actual Settings sidebar/controller:

| Logical viewport | Sidebar | Category panel | Result |
|---|---:|---:|---|
|960×540|259px|681×380px|PASS all26 full labels, physical/ancestor bounds, existing44dp target role|
|640×360|395px|672×223px virtual panel|FAIL21 categories horizontally offscreen/clipped,6 undersized targets, locations label clipped|

The small profile inherits `SharedThreePanel.viewport`'s900dp horizontal workspace. Even independently of horizontal overflow, six rows at the existing37px scaled target minimum require222px **before** dividers, gaps and top/bottom padding, exceeding the223px category allocation. Reducing only width cannot resolve height. Blanket shrinking, hiding options, scrolling or silently changing the rail would violate explicit requirements.

**Smallest proposed decision:** retain the approved sidebar and current composition; explicitly approve960×540 logical as the minimum supported viewport for the complete no-scroll Backup workspace. This is a support-matrix exception, not a640 fix or permission to mark its test PASS. The app's other640 routes are unaffected. Physical Shield configuration must still be measured;1920×1080 pixels alone does not establish logical dp.

**Alternative:** require640×360 support and authorize a bounded responsive sidebar/right composition at narrow sizes, including redistribution of actions/information. Sidebar remains permanently visible with the same menu. A measurable prototype must prove all26 categories, target sizes, complete text and final-row padding before adoption; no guarantee of fit or invented width is offered.

No exception or responsive product change has been made.

## D03 — selected tab versus actual focus

The connected-frame decision explicitly says: “The exact selected-versus-focused outline states and transitions for those interactive tabs require a later explicit design/behaviour decision.” The139 handover includes the frame but does not fill that gap. This is an explicit owner design dependency, not general coding authorization.

**Proposed approval:** retain one continuous selected cap/frame at all times. Use full soft-white cap emphasis while its heading owns focus; soften only that cap when focus enters interactive media. The selected heading/cap remains identifiable; the focused poster/action uses the existing single external focus outline. No detached underline or border moving to a poster. Four-percent usable-width sides and usable-height bottom, stable frame across tabs and existing card/action/tab order stay unchanged. Exact integer rounding follows the measured usable view after existing safe-area padding. This does not authorize a new information layout.

## D04 — full information versus finite passive canvas

The original requirements simultaneously demand full unbounded synopsis/useful facts/up to8cast+8crew, readable type, one fixed viewport, no scrolling, no content focus and no unapproved omission/truncation. A finite canvas cannot display an unbounded amount of readable text. Original source audits document32dp initial lower teaser and long-value truncation; changing spacing cannot establish universal fit.

**Smallest proposed relaxation preserving all information:** permit vertical reading overflow only in Details, keeping facts/people non-focusable and focus on the selected tab heading. Down/Up read overflow; Up at the beginning returns to the primary card action. Left/Right continue to switch tabs, OK remains inert, and Back remains existing navigation. This changes the no-scroll and Down/Up clauses and requires explicit approval. It is not the future FD-033 redesign.

**Alternative:** preserve the passive no-scroll contract and approve a bounded synopsis/fact overflow policy plus a full-information route outside that canvas. Exact fields, limits and route must then be approved before implementation; no silent truncation or omitted fields.

## Signing — precise administrative dependency and prepared engineering route

15October live API recheck confirms environment `supernova-foundation-signing` uses custom branch policies; the139 implementation branch is absent. Existing138 workflow/helper reject139 ref/version. Runtime spec160 has no local signing binding; this does **not** prove Actions secrets absent. No key access or signing dispatch was attempted. The later exact-branch policy-write attempt below was denied.

The139 variant prepared in this branch uses the same protected environment, certificate, PKCS12 preparation/cleanup, secret-free full native/application verification, exact-source APK hash and signed payload-parity checks. It runs only on explicit manual dispatch for this branch with `approved_qa_distribution=true`; push registration skips the signing job. It refuses failed/skipped/incomplete tests, historical versions/refs, changed payloads and changed certificate. Historical workflows remain unchanged.

After software/specification gates pass, the environment administrator must authorize exactly this branch (or explicitly supply another approved route):

```json
{"name":"codex/supernova-0.139-implementation-2026-10-13","type":"branch"}
```

Endpoint: `POST /repos/heretoecode/Supernova/environments/supernova-foundation-signing/deployment-branch-policies`. No wildcard, key replacement, main merge or overwrite of existing allowed branches is required. Under the owner’s current instruction to resolve signing restrictions, this exact request was attempted on15October. GitHub returned **403 Resource not accessible by integration**. Before/after policy readback is identical; no policy changed. SIGNING_POLICY_ATTEMPT_2026-10-15.json preserves the request, denial and unchanged policies. A repository/environment administrator must perform it or supply another approved route. Preparing the variant does not itself establish access or authorize a failing candidate.

QA signing must not be made circular: a signed QA APK is needed for physical signed-upgrade testing. Once software and specification gates pass and authorized signing is available, a QA build can honestly record Shield/upgrade checks as NOT RUN for owner testing. Final physical acceptance and release/merge remain separate. Current software failure still blocks QA signing.

## Shield and delivery

15October `adb devices -l` is empty. The current enforced network snapshot has `vpn_configured:false`, no TCP destination grants, no supplied Shield endpoint, and no `/dev/kvm`. No emulator represents physical Shield decode/passthrough/HDR/remote/upgrade acceptance. See SHIELD_ACCEPTANCE.md for all43 checks. A device connection/owner-run result is the external prerequisite, not a routine code choice.

There is no signed139 APK or139 Drive upload. Existing folder1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM remains the sole target once a valid signedQA APK exists; signature/version/code/hash, signed payload parity and uploaded readback must be verified. Neither unsigned artifact is delivered under the final filename. HTML release-note source must continue to describe unfinished status until actual delivery.

Prepared signing source8e917bd3 passed81 Python tests. Workflow registration run38724382422 actually skipped its signing job with zero steps. This is verified registration/gating behavior, not a signing pass. Unsigned CI38724382347 and local full996-test regression are running; collect their actual results before ending.
