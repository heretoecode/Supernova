# Rebuilding the0.146 source package

The application archive is the exact committed source; the sibling archives contain the reviewed patched source trees. The application manifest is transformed deterministically for the permanent Supernova identity during CI. After extracting the sibling archives under a common `nova/` directory and the application at `nova/Video`, run this identity preparation **before Gradle**:

```sh
cd nova
python3 Video/.github/build/prepare-foundation.py .
```

This preparation is idempotent for the already-prepared sibling authorities. Do not reapply the complete dependency-patch chain to the supplied patched sibling archives. For a completely fresh checkout at the original immutable sibling revisions, use `prepare-supernova146-dependencies.py` instead; it includes identity preparation at the correct point in the patch sequence.

The native source archives, full security/playback patches, four-ABI compiler/configuration receipts and exact source locks are included. `build-supernova146-native-stack.py` rebuilds each ABI from the pinned original sources. `package-supernova146-native.py` verifies and stages the reviewed binary inputs and rebuilds JNI; when modifying source, generate new native archive/file receipts rather than claiming the original hashes for changed bytes. Rebuild Jetty with its source-locked recipe, then run the universal noamazon release workflow with `-PfoundationVerify -PsupernovaQa -Puniversal`.

The NDK is acquired from the exact URL/hash in the native lock rather than included as a multi-gigabyte binary download. Java21 and the pinned ECJ/JRE8 inputs rebuild the Android Jetty transport. NIO desugaring2.1.5 is required at API23.

The source package includes LGPL FFmpeg/FriBidi/AudioCompress/native-player sources, the JCIFS fork, CDDL jUPnP/Servlet source, the reviewed OpenJDK/desugaring source and all15 native component sources. Google's NIO Maven source-classifier URL returns404; the pinned full OpenJDK/desugaring source archive is supplied instead. Its provenance is recorded, without asserting an unavailable source-classifier download succeeded.

Use a developer key for a separate test install of modified builds. An in-place upgrade of the installed Supernova application requires its existing certificate; private installation signing material is never part of corresponding source. The application does not restrict reverse engineering for debugging modifications to LGPL libraries.
