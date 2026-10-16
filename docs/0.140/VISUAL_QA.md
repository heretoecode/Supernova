# 0.140 visual verification register

Status: implementation and regression validation in progress. Native Robolectric renders are software evidence, not NVIDIA Shield screenshots or final visual acceptance.

## Reference availability

The owner reports recovery of the original approved Goonies JPEG and instructs its use alongside the written v3 corrections. The response received in this session contains text only: no accessible binary path, attachment ID or Drive link is present. A bounded workspace/attachment-folder search found no JPEG. Do not claim to have inspected or archived it. Await the actual file/link. The separately annotated v3 PNG and FD-007 reference images remain unrecovered; the JPEG must not substitute for them.

## FD-033 implemented measurements

- Fixed six-region panel, upper/lower height weights 56/44. Upper column weights 37.8/32.8/29.4; lower 34.4/35.3/30.3. Internal region padding 6dp horizontally and 4dp vertically; panel padding 10dp/8dp.
- Full viewport left/right/bottom margins 4%. Continuous compound tab/panel perimeter, 1dp stroke, 4dp radius; default white, existing accent override. No line below the active tab; inactive tabs unboxed.
- Heading 15sp bold; aligned labels/values 10sp. Description 11–14sp autosize, centred, 3dp added line spacing; long text ends with ellipsis to preserve the fixed non-scrolling panel. Path middle-ellipsized on one line with full text in content description.
- Eight cast and eight crew maximum, each 4×2; circular portraits up to 34dp (reduced to fit compact cells), names/roles 9sp. Missing portraits use the existing placeholder, never invented faces.
- Reception has exactly the four specified rows and three subtle separators. Existing bundled IMDb artwork is reused; text currently replaces the other unavailable approved/provider-licensed artwork. Existing source-labelled indexed/TMDb scores and votes remain visible above the four slots. Supplied supplementary numeric scores use 16sp bold primary text with smaller units/support. Missing data uses stable “No rating available”/“Unavailable” values. OMDb production binding is denied.

## Details/Extras corrections

Genre fallback restored from indexed metadata; redundant resolution removed from human metadata; canonical Supernova background; single compound border; hero synopsis balanced with vertical spacers. Extras use the shared Home card renderer, horizontal rails, 172dp cards with 97dp 16:9 thumbnails and 125dp rows. Heading-aware focus reveal and return-to-tab reset are covered by fixtures. Provider buttons use at least 44dp targets, explicit D-pad routing and the existing action callback. A callback fixture does not establish a provider app launch on a device.

## Validation limits

Native renderer fixtures cover physical 1280×720, 1920×1080 and 3840×2160 viewports and 150% text. These checks can establish bounds, focusability, row count, margins and render output. They cannot establish source-image pixel conformance, physical overscan, TV decoder playback or Shield remote behaviour. No emulator or physical device is connected (`adb devices` empty; no KVM device). Instrumentation and real-device screenshots remain unavailable.

Final per-viewport results and artifact/source references must be added after the complete passing test run. Any mismatch with the recovered JPEG must be corrected after it becomes accessible; no pixel-perfect approval is claimed here.
