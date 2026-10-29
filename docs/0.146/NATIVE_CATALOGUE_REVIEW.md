# Native capability catalogue

The per-ABI JSON compares codecs by name and decode/encode capability, ignoring numeric AVCodecID renumbering across FFmpeg major versions. Input/output protocols, demuxers, muxers and filters compare by type and name. All four ABI catalogues have the same five apparent removals and six additions.

- v308/v408/v410 decoder registry entries were consolidated into rawvideo. Generated MOV samples for all three formats decode successfully on every ABI; the compatible playback path remains.
- The old hls protocol entry is replaced by current FFmpeg HLS demuxing; AVOS normalizes the legacy hls+ alias, preserving the original URI/bookmark. Actual nested-HLS, trusted/wrong-host TLS and alias cases pass on all four ABIs.
- The removed experimental Sonic compressed-audio decoder was tested against the actual0.145 DSOs with a valid sample: all four reject it unless experimental strictness is explicitly enabled, which AVOS does not do. This is separate from the retained Sonic PCM speed backend. Source/negative results and a concrete optional-port plan are in SONIC_CODEC_RETIREMENT_REVIEW.json. An unsupported research-codec backport was not added solely to make registry text match.

The six new entries are webp_anim decoder/demuxer, hxvs and jpegxs_pipe demuxers, premultiply_dynamic filter and shared input protocol. This is an inventory, not exhaustive malformed-input or playback acceptance for each entry. All64 representative synthetic decode cases pass; AAC padding and ARM64 floating-point/Opus rounding are quantified separately in NATIVE_AUDIO_DIFFERENCE_REVIEW.json.
