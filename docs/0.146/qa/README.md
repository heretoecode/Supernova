# Supplemental native QA recipes

These are the actual standalone probes/runners used in the0.146 review. Their workspace paths describe the retained review environment; adapt paths when reproducing elsewhere. They do not affect the application build or claim physical-device acceptance.

The root is `/workspace/security146/nova`, containing freshly prepared pinned sibling source and the independently verified native artifacts from40843685431. The NDK is the exact30.0.14904198-beta1 toolchain in the native lock. `/workspace/native146-runtime` holds baseline0.145 DSOs, the generated corpus and probe outputs. The owned `supernova146-runtime` Docker container contains the four official Android23 bionic roots; the framework runtime-image pins/acquisition method are in `.github/build/foundation-native-runtimes.json` and the existing native verification tool. x86_64 runs natively; the other ABIs use QEMU user emulation.

`create_subtitle_fixtures.py` creates original synthetic SRT/VTT/ASS and a20x10-pixel PGS sample, including an explicit clear presentation. `run_native_subtitle_formats.py` compiles `native_subtitle_probe.c` against the actual four-ABI FFmpeg libraries and AVOS headers, dynamically loads the same native libraries as the app, then executes decoder-to-SRT/SSA/GFX backend render, cue expiry/PGS clear and seek-flush checks.

`run_native_font_stack.py` uses `tools/native146/font_stack.c`, a pinned RobotoFlex fixture (URL/hash in the security regression builder), and the same tiny fontconfig XML fixture under `/test`. It verifies fontconfig SAX discovery, FreeType excess-coordinate zeroing, libass glyph rasterization and absence of unneeded vulnerable XML feature symbols. Dynamic loading mirrors the application load path; this is not an eager-linked standalone AVOS executable.

`run_native_pssh_stress.py` decodes a synthetic MP4 carrying10000 PSSH records. `run_native_library_load.py` loads each native wrapper with RTLD_NOW/GLOBAL on every ABI. The JSON receipts in the parent directory record exact scope, native input run, outputs and limitations.

The reproducible host security builder `.github/build/test-supernova146-native-security.py` independently downloads hash-pinned original FFmpeg/FreeType/zlib sources, applies only reviewed security patches to its candidate copy, and runs six ASan/UBSan cases plus six original-source negative controls. CI40848631560 passes all12 expected outcomes. The source copyright for the zlib socket fixture is attributed in the fixture/reference receipts; it is test-only and never packaged in the application.

The native lifecycle suites model STREAM workers and a JavaVM, and these standalone probes run without real EGL surfaces. Actual ART/JNI player startup, GPU composition, HDMI chains and Shield remote interaction remain separate QA.

`run_native_default_fonts.py` additionally runs twelve SRT/VTT/ASS cases with no font-directory/name override, no FONTCONFIG_FILE/PATH and only a pinned Roboto Flex fixture in the Android system fonts directory. The built-in fontconfig fallback discovers it; the missing-default-config diagnostics are retained in the receipt. This models system-font availability without claiming a real device’s complete OEM font set.

The default-font follow-up executes asUID10000 with HOME/XDG unset, app-owned private test files and root-owned/readable system fonts. It passes despite explicit no-writable-cache diagnostics. `verify_source_package.py` verifies all28 source entries (26 archives plus two support documents, including every sibling module), native generated configs/evidence and exact rebuild instructions.

`verify_unsigned_apk.py` uses the existing qa145-verify-env interpreter (apkutils2), the public apksig.jar and VerifyApk class for independent unsigned rejection/manifest/dex/ZIP/ELF checks. `run_final_apk_native.py` streams verified APK DSOs directly into the owned Android23 roots, compiles the provided probes and runs36 cases asUID10000. It records the exact APK SHA256; no earlier source-build wrapper hash is substituted for the final-payload evidence.
