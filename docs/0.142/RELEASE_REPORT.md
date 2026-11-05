# Supernova 0.142 release preparation

Status: **signed QA delivered and download-verified; physical/final acceptance pending**.

Branch: `codex/supernova-0.142-implementation-2026-10-23`. Review: [draft PR #20](https://github.com/heretoecode/Supernova/pull/20). Verified delivered base: `4f0937f1fe8826f50d2c0e844b0837e8a2360a46`. Main is unmerged; the original release and authority branches are preserved.

Validated build/test source and frozen QA source: `b347707defdbf2ab3ae83d920ba9fa7af3936220`. Application inputs last changed at `08a896b1bee6a058053617fa7aa36c09773385d2`; later source changes are screenshot capture/backup-test synchronization and evidence only. The prepared signing workflow checks out the validated SHA and verifies its ancestry, following 0.141's established freeze pattern. Its workflow controls and documentation are separate from the frozen application checkout.

## Completed approved work

- Six permanent Network & Files destinations and a flexible workspace, four Overview cards, monochrome icons and the existing Supernova visual system. Original storage/network/cloud/library/scan/browser handlers remain accessible; no fictional capacity, provider or connection success is introduced.
- Connection recovery aliases, non-SMB email usernames/hostname-case credential lookup, stale listing/retry handling, discovery lifecycle, explicit inherited-exclusion restoration, scan defaults and disabled Put.io probes are covered by regression checks. The embedded Put.io browser retains read-only API behavior, account checks, pagination/ancestry and lifecycle ownership. Older already-truncated usernames still require re-entry; no credential/schema migration is claimed.
- Edit Library uses a 97% dark backdrop and 94% panels across all five existing steps, with layout, Live Preview and controls retained. Background interference is measured at 1 versus 25 in the original editor-source fixture; physical TV readability remains pending.
- Playback HUD: [source-grounded proposal only](PLAYBACK_HUD_PROPOSAL.md), with Subtitles, Audio, Display and Playback tabs. No player/HUD application changes. Final visual design still requires owner approval and does not block this A/B candidate.

The complete [202-entry parity map](PARITY_LEDGER.md) was committed before application edits (92 controls,28 state families,15 conditional findings,67 physical cases).

## Verification

| Check | Outcome |
| --- | --- |
| Clean exact-source CI | [40058921765](https://github.com/heretoecode/Supernova/actions/runs/40058921765): PASS at the frozen SHA |
| Android suite / compilation | 1,094 tests /232 classes; zero failures, errors or skips; release compilation and unsigned assembly pass |
| Repository tooling | 102 tests pass in CI and again locally after adding the source-freeze assertions |
| Release lint | Zero errors /1,788 warnings |
| Native/runtime | 64 decode comparisons across four ABIs;24 rebuilt FFmpeg libraries;88 packaged native hashes;138 reviewed runtime artifact hashes match |
| Full artifact readback | 109,610,082-byte ZIP; SHA-256 `2a15fb5557ce71e4e939a5fb448a476f96b7908b0dc4b093d04d3344b772d02d`; downloaded hash and ZIP CRC verified |
| Independent APK checks | Package/version/code, branding/providers/offline assets, all 88 native hashes,24 FFmpeg hashes and unsigned-signature rejection pass; pre-key evidence gate passes without key access |
| Navigation / functional software checks | Rail activation/entry, remembered folders, scroll/focus, dialogs, error/retry, shared hosts, Put.io lifecycle and a real loopback WebDAV email-username/listing test pass; these are not live Shield/server acceptance |
| Visual evidence | Eight actual 1920×1080 CI shell captures paired with all three preserved references; five editor before/after pairs; saved-pixel checks retain static brand/rail text. [Gallery](visual-evidence/index.html); physical fidelity pending |

Evidence: [conformance receipt](CONFORMANCE_40058921765.json), [complete-suite summary](test-runs/ci-40058921765-pass.json), [independent readback](UNSIGNED_ARTIFACT_READBACK.json), [native runtime report](NATIVE_RUNTIME_40058921765.json).

The unsigned review APK is 87,709,225 bytes, SHA-256 `bdfc4d4996f40efbc6dd95984530e4f2a7cfaf8aa5b1052c077b92d83b91d98e`. It is not a signed deliverable or install/upgrade certification. The signed delivery is recorded below; this prior unsigned artifact remains historical validation evidence.

## Signing and delivery gates

Signed QA **delivered and fully download-verified**: [Supernova_0.142.apk](https://drive.google.com/file/d/1CxWm1pxf5K005ZzIO4RWKzQyPF0YQcu4/view?usp=drivesdk), 87,907,280 bytes, package `app.supernova.player`, version **0.142 / code 142**. Exact source: `b347707defdbf2ab3ae83d920ba9fa7af3936220`. APK SHA-256: `af03d26719b00601ca08da5ad562c1f196ec536c6397ac5b4603c2337d7c8514`. Existing certificate SHA-256: `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`. [Signing workflow](https://github.com/heretoecode/Supernova/actions/runs/40080144690) passed the repeated tests/native/binary gates, existing-certificate signing, tested-payload parity and temporary-key cleanup. Independent signature, manifest/resources/assets, native hashes, ZIP CRC and 16 KiB alignment pass. The complete Drive copy matches the signed artifact. [Delivery receipt](SIGNED_QA_DELIVERY.json); [Shield quickcheck](QA_QUICKCHECK.md).

This is a QA candidate; final acceptance, physical Shield, real provider/storage interoperability and signed upgrade remain pending. Playback HUD redesign stays deferred pending final visual approval. No main merge.

Owner added only the 0.142 branch to `supernova-foundation-signing`; all eight previous rules remain. The runtime signing step successfully exercised existing protected credentials; no key values were read or exported. Earlier Drive APKs and release history are preserved; the existing development HTML is updated in place.

[SHIELD_QA.md](SHIELD_QA.md) retains all 67 original cases, five editor checks and one upgrade extension as PENDING. Software screenshots do not establish physical fidelity.

## Retained failed-run evidence

The prior local 1,094-case run had four old-navigation/private-field test failures; corrected behavior checks pass. CI 40023022572 then had one backup-test race: worker completion was visible before the UI publication, allowing a premature confirmation click. Waiting for the published phase retains all restore assertions; the 12-case class and the fresh full 1,094-case run pass. Repeated native screenshots also required test-harness descendant invalidation to retain cached static text. Neither correction changes backup/player application behavior. Failed receipts and focused runs remain in test-runs/; none is relabeled as a pass.
