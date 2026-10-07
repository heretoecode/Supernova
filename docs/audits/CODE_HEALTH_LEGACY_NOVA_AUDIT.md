# Code Health / Old NOVA Audit

Status: **COMPLETED — evidence-based cleanup policy**
Preserved: 7 October 2026

## Purpose

Assess how much inherited NOVA / AVOS-era structure remains beneath Supernova and decide how it should be handled without destabilising a working media player.

## Finding

Supernova is an evolving product layer over a substantial inherited Nova Video Player / AVOS architecture. The existence of old names, old-looking classes, compatibility code or upstream abstractions is **not evidence that the code is dead**.

The safe classification is:

1. **Active foundation** — inherited code still directly responsible for playback, media/library, networking, Android integration or other current behaviour.
2. **Adapter/compatibility layer** — old-looking code that bridges current Supernova behaviour to upstream/platform/native components.
3. **Reachable legacy** — older behaviour or UI that is still callable/referenced and therefore cannot be deleted merely because the preferred Preview UI does not normally expose it.
4. **Proven dead code** — unreachable/unreferenced code demonstrated by source/reference analysis and validation, suitable for bounded cleanup.

## Core conclusion

Do **not** perform a broad rewrite, language migration or mass deletion as part of a corrective release. Code-health work must be incremental and evidence-based.

A visual redesign is not justification for deleting inherited implementation. Playback, decoder/output, database, scanning, networking, preferences, migration/compatibility and Android lifecycle paths have high regression potential.

## Cleanup policy

A legacy element may be removed only when:
- references/call sites have been traced;
- runtime/feature reachability is understood;
- persisted preferences/database compatibility has been considered;
- replacement behaviour is already established where needed;
- relevant tests/build checks exist or are added;
- removal is isolated enough to diagnose/revert if it causes regression.

Prefer:
- removing genuinely unreachable duplicate UI;
- consolidating duplicated adapters/helpers after proving equivalence;
- renaming/moving code only when it materially improves maintainability and does not obscure upstream comparison;
- documenting intentionally retained legacy/compatibility paths.

Avoid:
- mass package reorganisation during fixes;
- deleting code solely because a class/resource contains NOVA/AVOS/legacy naming;
- rewriting stable playback/native integration for aesthetic code cleanliness;
- bundling a Kotlin/Compose migration into this release;
- dependency churn unrelated to an evidenced problem.

## Current-release implication

The next version is a UI/conformance/stability pass. Code-health findings are guardrails, not permission for a cleanup project. Fix the approved scope in place and preserve working inherited foundations.

Package/application-ID transition, new signing identity and broader rebranding remain separate later work and are not code-health cleanup tasks for this pass.

## Follow-up

Future dedicated code-health work can build an inventory with evidence for each candidate:
- path/component;
- classification;
- references/reachability;
- persisted-state impact;
- upstream relationship;
- test coverage;
- remove/retain/refactor recommendation.

Only **proven dead** candidates should enter a deletion batch.

## Authority

This report preserves the completed audit conclusion. It complements, rather than overrides, current product/design authority and the explicit next-version exclusions in `docs/qa/next-version-authority.md`.
