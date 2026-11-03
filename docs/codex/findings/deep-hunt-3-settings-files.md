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
