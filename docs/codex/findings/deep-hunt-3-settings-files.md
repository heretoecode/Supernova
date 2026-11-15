# Deep bug hunt 3 — not reached in this run

Source snapshot: `72b0756cb`. Read-only: no fixes, tests or builds were performed. The known U1, N1, N2, L1, P1, U2 and L2 findings are excluded.

Coverage: **0 / 19 files fully read**. A yes means every source line was read and the constructors/methods, callbacks, guards and teardown were considered. An excerpt, reference scan or prior-run read is not counted. This is partial progress; no unreviewed file is certified clean.

Priority was kept: work stopped within player/, so no later group received a deep-review yes. Cross-file excerpts used to verify a trigger are supporting evidence only. The four coverage inventories include Part A exclusions as no; those exclusions are listed separately below. Group 4 includes a conservative transitive utils reference/import closure so utility dependencies are not silently omitted.

| ID | File:line | Kind | Severity (crash / user-visible / minor) | Confidence (high / medium / low) | What goes wrong, for whom, when | Evidence (quote the lines, explain the path) | Suggested fix (described, not applied) |
|---|---|---|---|---|---|---|---|

No new finding is asserted for this group: it has not yet been deep-reviewed. This does not mean no defects exist.

## Part A exclusions

None in this group. Shared player classes were retained for review; no whole player class was labelled phone-only.

## Coverage

| File | Lines | Fully read (yes/no) |
|---|---:|---|
| `src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/UniversalFileBrowser.java` | 2830 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/BackupOperationModel.java` | 351 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/BackupSettingsController.java` | 739 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/BackupWorkspace.java` | 607 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/InlineOpenSubtitles.java` | 131 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/InlinePreferenceChoices.java` | 188 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewBuildInfo.java` | 73 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewPreferenceDialogs.java` | 163 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java` | 1415 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsChoiceDialog.java` | 152 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsRegistry.java` | 144 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsSwitchThumb.java` | 18 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsWorkspace.java` | 867 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsActivity.java` | 153 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsFragment.java` | 236 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsLicencesActivity.java` | 38 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsLicencesFragment.java` | 163 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsMoreLeanbackActivity.java` | 37 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsMoreLeanbackFragment.java` | 156 | no |

## 0.157 inventory refresh — 24aa1d4eb

The carried MORE item 5 explicitly says "skip hands-off and removed files". All 21 current group-3 files fall
under the protected leanback/settings/ or leanback/filebrowsing/ directories. The latest user instruction also
forbids screen edits. No file is relabelled fully read, no protected file is changed, and no claim of clean code is
made. This completes the required inventory/exclusion pass for this group; a screen-owner review remains separate.

| Current file | Lines | SHA-256 | Disposition |
| --- | ---: | --- | --- |
| `src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/UniversalFileBrowser.java` | 3296 | `1afa931e9ff9692e9f14a8afdf671ff6a220d1c2843c6974706a1581bfcf622d` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/BackupOperationModel.java` | 351 | `52db809ee22dfc5b0f9afffe5529e0f0f651682715b3c07abb6ee9ced1645513` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/BackupSettingsController.java` | 858 | `4fdf608bcef7f504d311b53685cae29ea7a36e471b4bc7bb50c67854f35d374a` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/BackupWorkspace.java` | 607 | `62953025d7c5d0094edf89ff93055ecc7ac2e708af1c9387148e1e36757b72ff` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/InlineOpenSubtitles.java` | 131 | `5b03bf9815e0567d426451e445035e9c2c94395b4c86b16039cc5cec317df7f1` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/InlinePreferenceChoices.java` | 188 | `e92ae4920b0e9c0a4283b7ed988a6c4121cada7f846dc3ea877d7e20a2bad5bb` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewBuildInfo.java` | 73 | `a32f3db6529b212f89c6c6d6818bf8feb59b612ef24596ad925f6110d55d347b` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewPreferenceDialogs.java` | 163 | `efc1e6985db5aa8f2375c644edb690ae1224d8b6d47cb011a373172523a35f43` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java` | 1434 | `cc4c3c6e5431b40193a2d10bd01d7ecaef5f91855181127dac9df19a754f66ff` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsChoiceDialog.java` | 152 | `d724ed8ac3ce8613b912e5a93269c03de74464877994559d1b6af1be5964c01f` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsLayout.java` | 200 | `349526fd18f9329f41abc062bf08f97b4398006f8ccc378fb7cbb4b2b927cc8d` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsRegistry.java` | 147 | `45b6df4468e49770dea1086adf57a67b8cee59697cad9a33e6522110aa892756` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsSwitchThumb.java` | 18 | `8bbe2156eece703f55b6da76da4ca138a1f344865bc39626484b6e3338b692bb` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsWorkspace.java` | 859 | `f3117dd4d514891fa85c4a8c731e8a5ae9831d3974770cacda6f073874b10554` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/SubtitleStyleBlock.java` | 183 | `8d6b3c09033a1e18559000d70c654ce6d7b4e2b410b200ba965e3a926dea8ea1` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsActivity.java` | 153 | `3a73363a31ddf94747bc798bbf09157c96c0dccd7d54c2b274de2732669f79d3` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsFragment.java` | 236 | `58b2c35f20ef8200e5af22a9666e809952aefabd5d523f80a7fa20da2320f8e5` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsLicencesActivity.java` | 38 | `caed4f04e4526af63670aa2106106ad3f44e13ee5787d2e0fcf4cc0d98a8057b` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsLicencesFragment.java` | 163 | `5cab7cc8861c305e578ae155229b4bed5787579bc0bf8177f7d47f74456e7672` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsMoreLeanbackActivity.java` | 37 | `01f2af4030513f48f339e7e6e14750adab8cc00bff3bc8329a64cdd55b0b5132` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsMoreLeanbackFragment.java` | 156 | `c7754b6de29292f2fbba1532f740e41b45a47df3b7a1f071c1aba46b2e59d5f0` | skip-hands-off |
