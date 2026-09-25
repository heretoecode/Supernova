# Supernova / Nova — Codex Operating Instructions

These instructions apply to all Codex work in this repository unless the current task explicitly overrides them.

## 1. Core principle

Preserve the current working implementation. Make the smallest safe change needed to satisfy the current authorised task. Do not redesign, broaden scope, or introduce unrelated features.

The current task specification, approved handover and explicitly approved/normative mock-ups are authoritative. Do not independently merge requirements from older handovers.

## 2. Before changing code

Before editing:
1. Confirm the repository, current branch and HEAD.
2. Inspect git status and identify staged, unstaged and untracked changes.
3. Separate generated/build artefacts from genuine source changes.
4. Understand the existing implementation relevant to the task.
5. Preserve completed work unless the current specification explicitly changes it.

Never restart from an older Nova baseline merely because a failure occurs.

## 3. Branch and source safety

- Do not merge into `main` unless Mark explicitly instructs it.
- Do not force-push, rewrite history, discard existing work, or perform destructive resets merely to solve a failure.
- Do not silently revert approved behaviour or UI.
- Do not remove functionality simply to make compilation or tests pass.
- Do not introduce new features or unrelated refactors during corrective/stability work.
- Keep changes tightly scoped and review the final diff for accidental changes.

## 4. Approved design authority

Approved or NORMATIVE Nova mock-ups are specifications, not general inspiration.

When implementing an approved design:
- follow its layout, hierarchy, spacing, focus behaviour, controls and visual intent as closely as the platform permits;
- retain existing Nova design language where the current specification does not request a change;
- do not invent functionality to fill perceived gaps;
- do not change an approved design to make implementation easier.

If the written specification and an approved mock-up genuinely conflict and the intended result cannot be determined safely, stop and request clarification.

## 5. Automatic failure recovery

An ordinary compile, test or APK-build failure is not, by itself, a reason to stop.

For a recoverable failure:
1. Capture and inspect the actual error.
2. Identify the most likely root cause.
3. Make the smallest safe corrective change.
4. Re-run the failed validation.
5. Continue the requested validation/build sequence when it passes.

Perform up to THREE autonomous corrective attempts for the same unresolved failure.

Do not repeat the same attempted fix without new evidence.

After three unsuccessful corrective attempts, stop and report:
- the exact failing command/stage;
- the relevant error;
- the suspected root cause;
- each correction attempted;
- what changed between attempts;
- the safest next action;
- whether Mark's input is actually required.

A successful correction resets the retry count for a later, distinct failure.

## 6. Validation sequence

Unless the current task specifies a stricter sequence, use:

1. Inspect Git/repository state.
2. Implement the authorised change.
3. Run the appropriate compile validation.
4. Run relevant automated tests.
5. Build the requested APK/build artefact.
6. Perform available smoke/static checks.
7. If signing is required, verify the finished artefact's signing identity/certificate.
8. Review git status and the complete source diff.
9. Self-review the implementation against the current specification and approved mock-ups.
10. Report results accurately, including anything that could not be tested.

Never claim a test, build, signing verification or physical-device check passed unless it actually ran and passed.

## 7. Signing safety

Signing identity is security-sensitive.

- Never generate, substitute or silently switch to a different signing key.
- Use only the signing key/certificate explicitly authorised for the current release.
- Never expose secrets or private key material in logs, commits or reports.
- If an expected signing secret is unavailable, stop rather than substituting another identity.
- When an expected certificate fingerprint is supplied, cryptographically verify the finished APK against it before declaring the signed build valid.

## 8. Self-review before completion

Before declaring a task complete:
- compare the implementation with every in-scope requirement;
- check that explicitly out-of-scope/deferred items were not introduced;
- inspect the diff for accidental regressions or unrelated edits;
- verify that working behaviour outside the requested scope was preserved as far as available tests allow;
- check that required artefacts were actually produced.

If self-review finds a defect, correct it and repeat the relevant validation rather than declaring completion.

## 9. When to stop and ask Mark

Escalate when progress genuinely requires human input, including:
- an ambiguous design/product decision with materially different outcomes;
- conflicting authoritative requirements that cannot safely be reconciled;
- missing credentials, permissions or signing material;
- an action that would require violating these safety rules;
- physical Nvidia Shield behaviour that cannot be established through repository/build validation;
- three unsuccessful evidence-based corrective attempts at the same failure.

Do not escalate routine compiler errors, test failures or straightforward build failures before attempting safe recovery.

## 10. Physical Nvidia Shield QA

Repository/build validation does not replace physical-device QA.

Clearly distinguish:
- compile/build success;
- automated test success;
- emulator/static/smoke checks, if performed;
- physical Nvidia Shield verification.

Never mark Shield-only behaviour as verified until Mark has tested it on the device or equivalent physical evidence is available.

## 11. Reporting

At completion provide a concise report covering:
- what changed;
- files/components affected;
- compile/test/build results;
- APK/signing verification when applicable;
- self-review result;
- anything still requiring physical Shield QA;
- any remaining known issue.

Use British English in project documentation and reports.

## 12. Priority order

When instructions conflict, use this practical project order:
1. Safety and preservation of repository/signing material.
2. The current explicit task from Mark.
3. The current authoritative handover/specification.
4. Approved/NORMATIVE mock-ups for visual requirements.
5. These repository operating instructions.
6. Existing implementation for unspecified behaviour.

Do not use older handovers to override a newer authoritative handover.
