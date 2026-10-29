# Supernova0.146 QA candidate

This maintenance candidate integrates the compatible NOVA6.6.2 release-time source changes while retaining Supernova’s custom interface, saved library, credentials and preference keys. The full matrix accounts for438 prior upstream commits, including source-level adaptations, superseded changes and concrete exclusions.

The native player now uses rebuilt FFmpeg9.0.2, OpenSSL4.1 and libass0.17.5 with the complete font/shaping/codec chain on all four ABIs. Newly published FFmpeg, FreeType and zlib findings received explicit upstream backports. HLS aliases remain compatible and certificate/hostname verification remains on, including nested HLS requests.

Native subtitles use the existing TV/phone controls and saved sizing, with additional SAF font-folder/default-font controls, letterbox placement and full-subtitle policy for local-language audio. PCM speed offers Sonic/atempo while the original enable/AudioTrack switches remain authoritative. The retired experimental Sonic compressed-audio decoder is a different feature; its baseline behavior and optional-port path are documented separately.

The UPnP transport replaces Jetty9.2.30 with an explicitly identified source-built9.4 transport fork, parser/authority/Digest fixes and API23 NIO support. Four unused optional MethodHandle-based features are excluded. The139-artifact SBOM keeps version findings visible with source/feature dispositions, and64 offline legal entries retain full notices.

Software evidence includes all-ABI native source builds and loading,64 decode cases,36 TLS/HLS/raw-video cases,16 subtitle-format/backend cases, font/PSSH stress tests, native sanitizers and28 actual Android23 transport/trust/FTPS cases. The final APK conformance run determines the exact tested source/payload; signing and delivery cannot be claimed until that gate and the existing signing-environment branch policy permit them.

Physical Shield/HDMI/ARC/SPDIF/private-listening paths, real NAS/UPnP providers, GL/3D subtitle visuals and installed signed upgrade/rollback remain pending. The original incident ZIP and some visual reference attachments are unavailable. No main merge or public release is authorized.

Java FTPS now checks CA trust and the original hostname on both control/data channels. Untrusted/self-signed peers fail closed. Internal player/metadata services are private; playback-settings view references are released at close, and credential fragments retain their dismissal lifecycle.
