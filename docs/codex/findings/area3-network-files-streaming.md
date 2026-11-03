# Bug hunt area 3 — partial semantic review

Read-only findings against the source after Parts C and D. No code was changed for this review.

Coverage limitation: every listed Java file was read in full by the inventory/risk-pattern pass and joined with its file-filtered lint findings. This is **not a completed every-line semantic review** of all files. Direct code review was concentrated on the evidence sites below and their callers; the remaining semantic review is outstanding. A file with no finding is not certified defect-free.

The fetched reports/lint snapshot contains pre-edit line numbers; lint entries below retain those original coordinates. Findings use the current code coordinates. SuspiciousIndentation was addressed in Part A; no new lint run or tests were performed.

Inventory: 82 files, 18353 lines, 40 lint entries.

| ID | File:line | Kind | Severity (crash / user-visible / minor) | Confidence (high / medium / low) | What goes wrong, for whom, when | Evidence (lines and why) | Suggested fix (described, not applied) |
|---|---|---|---|---|---|---|---|
| N1 | `src/main/java/com/archos/mediacenter/video/leanback/network/ftp/FtpServerCredentialsDialog.java:172` | logic | user-visible | medium | Malformed nonempty FTP port input may show an invalid-port toast and still connect using the default port. | The NumberFormatException catch does not return or mark validUri false; port remains -1. Later code treats -1 as the default-port sentinel. Confirm UriUtils.isValidPort(-1) in the external dependency before fixing; the shared NetworkServerCredentialsDialog already rejects malformed input early. | Reject nonempty malformed input immediately; reserve the default-port sentinel for deliberately empty input. |
| N2 | `src/main/java/com/archos/mediacenter/video/leanback/network/ftp/FtpServerCredentialsDialog.java:210` | security/data | user-visible | medium | When debug logging is enabled, server usernames and private host/folder locations are written to ordinary application logs; invalid host/path warnings can disclose values at warning level. | The dialog logs scheme, username, address and path directly, and logs invalid host/path values without redaction. This is not a claim that the password field is logged. Ordinary SLF4J calls do not pass through the typed Diagnostics redaction shown elsewhere. | Log bounded outcome/protocol categories only, or route through a shared location/identity redactor before writing logs. |

## Files processed and filtered lint findings

This is the full scan inventory, not an assertion of complete manual line-by-line review. Each lint entry is shown only with the file to which it belongs; “none” means no entry in the supplied snapshot.

- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/BrowserByExtStorage.java` — 165 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/BrowserByFolder.java` — 941 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/BrowserByLocalFolder.java` — 93 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/BrowserByUsb.java` — 43 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/BrowserByVideoFolder.java` — 104 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/ListingAdapter.java` — 175 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/AdapterByNetwork.java` — 36 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/BrowserByNetwork.java` — 458 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/CreateShareDialog.java` — 112 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/FtpBrowser/BrowserBySFTP.java` — 256 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/FtpBrowser/FTPServerCredentialsDialog.java` — 80 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/FtpBrowser/FtpRootFragment.java` — 180 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/FtpBrowser/FtpShortcutAdapter.java` — 200 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/NewRootFragment.java` — 314 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/RootFragmentAdapter.java` — 342 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/ShortcutAdapter.java` — 200 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/ShortcutRootFragment.java` — 234 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/SmbBrowser/BrowserBySmb.java` — 147 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/SmbBrowser/SMBServerCredentialsDialog.java` — 64 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/SmbBrowser/SmbRootFragment.java` — 259 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/SmbBrowser/SmbWorkgroupShortcutAndServerAdapter.java` — 131 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/UpnpBrowser/BrowserByUpnp.java` — 66 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/UpnpBrowser/UpnpRootFragment.java` — 161 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/UpnpBrowser/UpnpShortcutAndServerAdapter.java` — 73 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/UpnpSmbCommonRootFragment.java` — 60 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/WorkgroupShortcutAndServerAdapter.java` — 186 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/BrowserSelection.java` — 72 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/ExtStorageListingActivity.java` — 55 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/ListingActivity.java` — 266 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/ListingFragment.java` — 1105 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/LocalListingActivity.java` — 59 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/LocalListingFragment.java` — 175 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/MultiBackHintManager.java` — 79 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/NetworkFilesLayout.java` — 80 lines; lint: ViewConstructor@12: Custom view `NetworkFilesLayout` is missing constructor used by tools: `(Context)` or `(Context,AttributeSet)` or `(Context,AttributeSet,int)`
- `src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/PreviewBrowserSurface.java` — 44 lines; lint: ViewConstructor@19: Custom view `PreviewBrowserSurface` is missing constructor used by tools: `(Context)` or `(Context,AttributeSet)` or `(Context,AttributeSet,int)`
- `src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/PreviewFilePresenter.java` — 54 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/UniversalFileBrowser.java` — 2830 lines; lint: ApplySharedPref@2333: Consider using `apply()` instead; `commit` writes its data to persistent storage immediately, whereas `apply` will handle it in the background; NotifyDataSetChanged@1777: It will always be more efficient to use more specific change events if you can. Rely on `notifyDataSetChanged` as a last resort.; NotifyDataSetChanged@2006: It will always be more efficient to use more specific change events if you can. Rely on `notifyDataSetChanged` as a last resort.; NotifyDataSetChanged@2112: It will always be more efficient to use more specific change events if you can. Rely on `notifyDataSetChanged` as a last resort.; NotifyDataSetChanged@2214: It will always be more efficient to use more specific change events if you can. Rely on `notifyDataSetChanged` as a last resort.; NotifyDataSetChanged@2234: It will always be more efficient to use more specific change events if you can. Rely on `notifyDataSetChanged` as a last resort.; NotifyDataSetChanged@2241: It will always be more efficient to use more specific change events if you can. Rely on `notifyDataSetChanged` as a last resort.; SetTextI18n@369: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@373: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@376: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@378: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@382: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@832: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@833: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@1158: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@1158: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@1168: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@1168: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@1191: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@1310: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@1491: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@1499: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@1499: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@1503: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@1532: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@1661: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@1667: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@1669: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@1731: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@1774: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@1806: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@1806: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@1917: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@2060: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@2157: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@2541: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@2541: String literal in `setText` can not be translated. Use Android resources instead.
- `src/main/java/com/archos/mediacenter/video/leanback/network/NetworkListingFragment.java` — 352 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/NetworkRootActivity.java` — 66 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/NetworkRootFragment.java` — 807 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/NetworkServerCredentialsDialog.java` — 365 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/NetworkShortcutDetailsActivity.java` — 61 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/NetworkShortcutDetailsFragment.java` — 390 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/PreviewSourceManagement.java` — 28 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/ftp/FtpListingActivity.java` — 27 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/ftp/FtpListingFragment.java` — 189 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/ftp/FtpServerCredentialsDialog.java` — 269 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/rescan/RescanActivity.java` — 35 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/rescan/RescanFragment.java` — 232 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/rescan/RescanWhenOpeningApplication.java` — 84 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/rescan/ScheduledRescanPeriod.java` — 177 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/rescan/ScheduledRescanShares.java` — 76 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/rescan/ScheduledRescanTime.java` — 128 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/smb/SmbListingActivity.java` — 29 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/smb/SmbListingFragment.java` — 130 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/smb/SmbServerCredentialsDialog.java` — 193 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/smbj/SmbjListingActivity.java` — 30 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/sshj/SshjListingActivity.java` — 30 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/upnp/UpnpListingActivity.java` — 27 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/upnp/UpnpListingFragment.java` — 92 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/network/webdav/WebdavListingActivity.java` — 30 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/PreviewProviderIcons.java` — 127 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/PreviewRemoteDetailsActivity.java` — 112 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/StreamingActionPresenter.java` — 109 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/StreamingActions.java` — 382 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/StreamingPreferences.java` — 196 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/StreamingRepository.java` — 693 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioAccountController.java` — 699 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioAssociationStore.java` — 384 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioAuthFlow.java` — 187 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioBrowserActivity.java` — 21 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioBrowserController.java` — 358 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioLibraryBridge.java` — 82 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioOAuthClient.java` — 147 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioReadClient.java` — 344 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioReassignment.java` — 242 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioReconciliation.java` — 131 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioRootLocation.java` — 47 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioSnapshotReader.java` — 39 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioSync.java` — 181 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioSyncScheduler.java` — 54 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioTokenStore.java` — 72 lines; lint: ObsoleteSdkInt@29: Unnecessary; `SDK_INT` is never < 23
