# Lint inventory and triage — 0.157

Latest published inventory branch reports/lint was fetched read-only at d1defb52c40834647c5e6b1f7e88c8751a1246e2.
Its actual lint source is 0efd8e4480ef490dbfc796950686c15cedf5d2a3, generated 4 November 2026 08:54:59 UTC.
It contains 1,401 warnings. This is not lint output for the current continuation. AGP lint was not run locally:
the inherited MORE rule reserves Gradle builds for source integration. javac/style are not Android lint.
The earlier About parser blocker has since been fixed by the source branch; whole-source javac now passes.

| Issue | Published count |
| --- | ---: |
| UnusedResources | 423 |
| RtlHardcoded | 353 |
| PrivateResource | 116 |
| SetTextI18n | 90 |
| HardcodedText | 63 |
| SpUsage | 37 |
| PxUsage | 25 |
| ViewConstructor | 24 |
| RtlSymmetry | 22 |
| StaticFieldLeak | 19 |
| UseCompatTextViewDrawableXml | 15 |
| Typos | 14 |
| UselessParent | 14 |
| NotifyDataSetChanged | 13 |
| Overdraw | 13 |
| FragmentTagUsage | 11 |
| AlwaysShowAction | 11 |
| UnusedAttribute | 10 |
| Autofill | 10 |
| RelativeOverlap | 9 |
| ApplySharedPref | 8 |
| InflateParams | 8 |
| PluralsCandidate | 8 |
| ContentDescription | 8 |
| ObsoleteLayoutParam | 7 |
| MergeRootFrame | 6 |
| NestedWeights | 5 |
| RedundantNamespace | 5 |
| DiscouragedApi | 4 |
| InefficientWeight | 4 |
| IconDensities | 4 |
| CutPasteId | 3 |
| UnusedTranslation | 3 |
| TrustAllX509TrustManager | 3 |
| InconsistentLayout | 2 |
| SelectedPhotoAccess | 2 |
| VectorRaster | 2 |
| DrawAllocation | 2 |
| UseCompoundDrawables | 2 |
| TypographyEllipsis | 2 |
| IconDuplicates | 2 |
| ScrollViewSize | 1 |
| AppLinkWarning | 1 |
| PictureInPictureIssue | 1 |
| RedundantLabel | 1 |
| UseSwitchCompatOrMaterialCode | 1 |
| UseSwitchCompatOrMaterialXml | 1 |
| UseCompatLoadingForDrawables | 1 |
| SetJavaScriptEnabled | 1 |
| ExportedContentProvider | 1 |
| InsecureBaseConfiguration | 1 |
| ObsoleteSdkInt | 1 |
| VectorPath | 1 |
| UsableSpace | 1 |
| UnusedNamespace | 1 |
| IconDuplicatesConfig | 1 |
| IconLocation | 1 |
| ClickableViewAccessibility | 1 |
| LabelFor | 1 |
| RtlEnabled | 1 |

Triage: UnusedResources needs exact resource/manifest/reflection closure and R8 before deletion; no screen/resource
edits are authorised. RTL/text/dimensions/ViewConstructor warnings belong to the screen owner. ApplySharedPref
includes durable subtitle-discovery/viewing-history checkpoints: changing commit to apply can lose atomic recovery
semantics, so no blind substitution. StaticFieldLeak includes app-context singletons and protected Activity/View
fields; inspect actual ownership before changing a declaration. New DP-10 ownership and service callback teardown
have explicit regressions, rather than suppressing warnings. TrustAll helpers occur in third-party code and do not
prove use by FTPS's strict trust/hostname call sites; security-audit.md records the actual boundaries.

Source correction: removed the obsolete SDK < 23 guard/import from PutioTokenStore because the app minimum is 23.
Its existing encrypted-envelope/tamper/invalid-input/missing-key tests now retain SDK 28 and also run at SDKs 23
and 30; no assertion was weakened. Injected keys exercise the real AES-GCM envelope, not Android hardware KeyStore.
This removes the reported source pattern; only a new exact-source lint run can confirm its warning is gone.
Other lint rows are not suppressed or declared fixed merely because a file compiles. Required integration: run all three
module lint tasks/full conformance against the exact prepared source, publish XML plus source/patch receipts, compare
by issue/file/message rather than line shifts, and keep new API-23 errors at zero. No current AGP result is fabricated.
