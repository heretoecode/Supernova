# Screenshot evidence

`Screenshots/Runtime` contains actual Android API 28 emulator captures at 1920 × 1080. The emulator has no personal media library or network credentials, so empty-library and empty-folder states are expected. These captures establish layout and accessible route behaviour, not real Shield performance or populated metadata quality.

`Screenshots/Fixtures` contains deterministic Robolectric renders with synthetic titles, metadata, backdrop and artwork markers. They check composition without querying a live service. Any poster border/text marked POSTER TOP / FULL FRAME is part of the test artwork, not an application focus border. The HUD fixture renders the layout rather than running native playback.

The Customise Home fixture forcibly expands its window to the test canvas and is not evidence of the real dialog footprint; it is deliberately excluded from the visual acceptance set. Source sizing and the retained row-operation tests are recorded separately. Physical compact-dialog acceptance still requires Shield QA.

The first build's screenshots were reviewed and led to corrections for the system keyboard over Search, browser status-bar overlap, Settings focus/help synchronisation and the Featured Resume width. Only final-build captures belong in the delivery acceptance folders. The full diagnostic ZIP retains the unfiltered build/test evidence.

No real playback/A/V, streaming title-page or physical Nvidia Shield acceptance is inferred from these images.
