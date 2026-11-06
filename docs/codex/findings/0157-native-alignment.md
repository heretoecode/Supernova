# Native observation alignment — 0.157

| ID | File / trigger | Kind | Severity | Confidence | Evidence / fix |
| --- | --- | --- | --- | --- | --- |
| DN157-01 | seek_picture.h and frame_observation.h on i686 | Atomic storage | User-visible performance / portability | High | NDK Clang warned that new 64-bit atomics had four-byte alignment; it could emit slow helper operations. Added explicit eight-byte alignment for ticket/counter/phase storage and the counter pointer type. New C assertions check record/phase alignment; the previous i686 structures fail those assertions. Four target-ABI assertion compiles and all 32 changed-production-unit compiles pass without atomic-alignment warnings. |

The additional avos-supernova157-atomic-alignment.patch applies after the seek/frame patch. Earlier patch bytes are
unchanged. The host UBSan/pthread test still proves generation/epoch/exactly-once ownership and 1.6 million counter
updates. These are object/header and host-unit checks, not a linked archive, Android atomic-runtime or decoder test.
The pre-existing audio-session signed-pointer compiler warning is outside these new fields; it was not hidden by
calling this a warning-free native build. A reviewed full archive and device acceptance are still required.
