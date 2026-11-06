# APK size audit — 0.157 work, 6 November 2026

Measured baseline: real signed 0.155 arm64 QA artifact 13367287524 from run 41922310251, source
483ad0926c12d2b493e8d51a410967badea02b4d. Downloaded ZIP is 34,421,497 bytes; APK is 37,861,014 bytes,
SHA-256 f787266401ad77d885bd895dc3cd22452874b2a7affe176c5b7880956676719d, matching its public QA receipt.
Its certificate receipt is 79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e.
No 0.157 APK/R8/resource shrink has been built here; none of these numbers is a 0.157 result or promised saving.

| Category | Compressed bytes | Uncompressed payload bytes | Entries |
| --- | ---: | ---: | ---: |
| META-INF | 193,376 | 453,253 | 19 |
| assets | 606,829 | 3,671,513 | 87 |
| dex | 8,645,169 | 20,864,804 | 4 |
| native | 15,701,200 | 34,373,656 | 21 |
| other | 4,027,944 | 4,147,609 | 5 |
| javax | 4,668 | 9,215 | 7 |
| res | 8,411,433 | 9,289,631 | 2255 |

Entry totals exclude ZIP headers/alignment/signatures, and uncompressed payload is not total installed ART/storage.
Largest entries: libavcodec 5,589,917 compressed / 10,949,544 raw; classes.dex 4,430,933 / 10,287,760;
resources.arsc 4,006,196 stored; classes3.dex 3,846,663 / 9,673,016; libavformat 3,329,099 / 7,312,664;
libavos 2,483,578 / 5,550,640. Native and dex dominate installed payload; resource changes can dominate downloads.

Three large stored PNGs (obfuscated res/VP.png 2,006,535, X4.png 1,380,786, 8s.png 1,278,052 bytes) account for
4,665,373 bytes. Obfuscated names do not prove unused assets. Map them through the exact source resource table,
compare lossless alternatives/quality on the Shield and measure a rebuilt APK before claiming any reduction.
Resource edits are outside this task. Compression changes alone do not reduce decoded bitmap heap use.

Ranked reduction candidates: (1) duplicate/oversized artwork after resource mapping, with the above bytes as an
upper review budget, not a saving; (2) unused native codecs only after owner-supported-format decisions and native
link/fixture review; (3) dex/library removal only with R8 usage and reflection/JNI/manifest closure; (4) classic route
removal only after item-23 approval and device checks. No safe byte saving is established for any candidate yet.
Test fixtures and audit JSON are not main-source assets and must not be copied into the APK. Preserve a new exact
source native/R8/resource composition and signed/unsigned parity receipt at integration; compare like ABIs/build types.
