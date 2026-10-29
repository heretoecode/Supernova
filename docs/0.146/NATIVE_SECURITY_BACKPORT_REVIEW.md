# Native security review — candidate under validation

The exact-commit OSV query on29October found11 findings across FFmpeg9.0.2, zlib1.3.2, FreeType2.14.3 and libxml2. The raw query/details remain in NATIVE_ADVISORY_QUERY.json and NATIVE_ADVISORY_DETAILS.json. No version is relabelled or advisory suppressed. Prior native CI artifacts predate these backports and are not final packaging inputs.

| Finding | Source action and scope |
|---|---|
| CVE-2026-107675 | FFmpeg's libssh SFTP protocol is not enabled: CONFIG_LIBSSH_PROTOCOL=0, no libssh dependency. Supernova SFTP uses its FileCore transports and retained SftpHostTrust. No raw-FFmpeg SFTP handler is added. The finding remains a source-version alert for an unbuilt optional backend. |
| CVE-2026-107676 | Backport FFmpeg2c2f6e96e31795ae95a8f8323a493ffbc493111f: serialize HDR10+ colour saturation independently of tone mapping, matching allocated size and parser. |
| CVE-2026-107677 | Backport ed27bfcbbbc0872c0195eaf4dd2c0c93b9f1778e: empty replacement needles duplicate the input rather than looping. DASH demuxer is also disabled in the selected build; common av_strireplace is fixed regardless. |
| CVE-2026-107678 | Backport969c7eb091989a664cb7bf66a1f02051cc46cb52 iterative init-info freeing, d1434518fbd03789e3e5dc0657a7a9f1123ce074 append-in-place serialization, and71403a21b02a92b925e26e609e9bd3e48df451cf MOV PSSH collection. This removes both recursive stack growth and quadratic re-parsing. Public append API is additive; unrelated master version/API-history changes are omitted. |
| CVE-2026-107696 | Backport f5d4fd186c68913240c4f6e9cda98443e1518b34: bound RTSP redirects to8. Its required cross-origin credential clearing, a441a2eb383960d76632eb5dc42639ec52d46bd8, is already present in the pinned9.0.2 source and is retained. |
| CVE-2026-50811 | Backport FreeType5a280ecde6f324de0d226261036e736e0cb49a71: zero excess variation coordinates rather than reading beyond axes. |
| CVE-2026-95512 | Backport f3ca71c9900fe860849b3163a6e2c1e765b291d9: bound cumulative CID subroutine overlaps before allocation/decryption. |
| CVE-2026-76844 / CVE-2026-85091 | Backport upstream zlibdf84af25dc1942490e1d1c899a07619152a46148: on failed/stalled direct writes, clear avail_in and reset next_in to the owned buffer. This is upstream's85091 fix and removes the stale caller pointer described by76844. |
| OSV-2021-777 | The report's chain requires xmlXIncludeCopyRange/xmlXIncludeCopyXPointer. Fontconfig uses the SAX push parser in src/fcxml.c, not XInclude or XPointer. Disable those unneeded features in libxml2 rather than invent an undocumented global tree-ownership patch. Verify their absence in the linked Android player. |
| OSV-2024-698 | The reported chain requires htmlSaveFileFormat/serialization. Native XML is used only by fontconfig's SAX configuration reader. Disable HTML, output/writer and c14n features in the native libxml2 build. Encoding needed for SAX remains enabled. Verify actual symbols/configuration and font discovery. The incomplete open-ended OSV range is retained as a version-level finding. |

The aggregate security patches are independently locked in native-supernova146.lock.json. Official patches applied with zero fuzz to the selected sources. FFmpeg's already-present RTSP credential fix was identified before final patch construction; no reversal is included. Four-ABI rebuild, targeted negative controls and runtime revalidation are pending for these new bytes.
