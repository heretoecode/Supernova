# Supernova 0.140 signing environment — resolved

The owner added the exact branch rule on 16 October 2026. API readback confirms all six previous selected-branch entries are unchanged and the seventh entry is type `branch`, name `codex/supernova-0.140-implementation-2026-10-16` (rule 63311363). [Readback receipt](SIGNING_ENVIRONMENT_READBACK.json). No credential was supplied to the conversation and no signing identity or secret was changed.

The earlier [workflow 38949916248](https://github.com/heretoecode/Supernova/actions/runs/38949916248) was rejected before runner allocation because the branch was excluded; the integration’s narrow attempt to add it returned HTTP 403. That configuration blocker is now resolved. The next authorised manual-only signing workflow must still pass its exact-source native/test/lint/binary gates before the single protected signing step. This document alone does not certify passed validation or APK delivery.

Frozen implementation source: `866f21f11f27dea4846ddd3cac57d98b281eecbb`. Established certificate SHA-256: `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`. Version 0.140 / code 140 increases from the independently verified 0.139 baseline. No main merge is authorised.
