# Supernova

Supernova is a video player for Android TV, made for the NVIDIA Shield. It is built on
[NOVA Video Player](https://github.com/nova-video-player), which itself grew out of the open-source Archos Video
Player Community Edition.

It plays films and TV series from the Shield's own storage and from network shares and cloud sources, and shows them
as a library with posters, details, ratings, where-to-watch information, intro and credits skipping, and a
redesigned player.

## Current version

**0.155** (NOVA 6.6.4 engine), released 4 November 2026.

**Coming next: 0.156** — Chapters and Episodes in the player, Settings redesigned, Search rebuilt, Network & Files
pages, seek previews, sleep timer and more. The full list is in
[`docs/0.156/RELEASE_LIST.md`](docs/0.156/RELEASE_LIST.md) and the GitHub milestone **0.156**.

## Installing

Supernova is a personal build for one Shield and is not published to a store. Each signed APK is produced by the
project's signing workflow on GitHub Actions and installed on the Shield by side-loading (for example with
"Send files to TV" or `adb install`). It installs over the previous version and keeps your library and settings.

## Where things live

- [`CLAUDE.md`](CLAUDE.md) — how the project works, for people and AI assistants. **Start here.**
- [`PROJECT_STATUS.md`](PROJECT_STATUS.md) — what is released, in progress and waiting for a decision.
- [`docs/`](docs/README.md) — every version's records, the designs and mock-ups, and the history.
- **Issues** — one per planned item, grouped by milestone; parked ideas are labelled `parked`.

## Credits and licence

Supernova is a fork of NOVA Video Player by the NOVA team, based on Archos Video Player. It uses data from TMDB,
IntroDB, ChaptersDB, OpenSubtitles, Trakt and others; every provider and open-source component is credited on the
app's About page. The NOVA-derived code keeps its original licence: see [`LICENSE.txt`](LICENSE.txt).
