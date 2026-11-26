# Supernova

Supernova is a video player for Android TV, made for the NVIDIA Shield. It is built on
[NOVA Video Player](https://github.com/nova-video-player), which itself grew out of the open-source Archos Video
Player Community Edition.

It plays films and TV shows from the Shield's own storage and from network shares and cloud sources, and shows them
as a library with posters, details, ratings, where-to-watch information, intro and credits skipping, and a
redesigned player.

## Current version

**0.158.10** (code 1590), signed 26 November 2026. What each APK brought:
[`docs/0.158/RELEASE_NOTES.md`](docs/0.158/RELEASE_NOTES.md) (one section per version).

**0.158** is the redesign version, built in two parts:
- **Part A (0.158):** the new top bar (every place a word, no wordmark), Movies, TV Shows and your own library page,
  Home, Search, and connections grouped by server.
- **Part B (0.158.1 onwards, in progress):** a speckled background and one shape for every button and chip,
  Playback Settings redone, the Settings redesign (seven tabs), the player's title block, Details fixes and the
  episodes feature, titles in one style, then Files.
- **Part C (0.158.8–0.158.9):** fixes from the 26 Nov diagnostics report, the Settings Backup bug, the next-episode
  label, Home's one-button hero, search history settings, NOVA fixes and the diagnostics upgrade.

Progress and what is still to come: [`PROJECT_STATUS.md`](PROJECT_STATUS.md) and
[`docs/ROADMAP.md`](docs/ROADMAP.md).

## Installing

Supernova is a personal build for one Shield and is not published to a store. Each signed APK is produced by the
project's signing workflow on GitHub Actions and installed on the Shield by side-loading (for example with
"Send files to TV" or `adb install`). It installs over the previous version and keeps your library and settings.

## Where things live

- [`CLAUDE.md`](CLAUDE.md): how the project works, for people and AI assistants. **Start here.**
- [`PROJECT_STATUS.md`](PROJECT_STATUS.md): what is released, in progress and waiting for a decision.
- [`docs/ROADMAP.md`](docs/ROADMAP.md): what comes in which version, and the ideas parked for later.
- [`docs/design/RULES.md`](docs/design/RULES.md): every current design decision; page sheets in
  [`docs/design/pages/`](docs/design/pages/).
- [`docs/`](docs/README.md): every version's records, the designs and mock-ups, and the history.

## Credits and licence

Supernova is a fork of NOVA Video Player by the NOVA team, based on Archos Video Player. It uses data from TMDB,
IntroDB, TheIntroDB, ChaptersDB, OpenSubtitles, Trakt, JustWatch and others; every provider and open-source component
is credited on the app's About page. The NOVA-derived code keeps its original licence: see
[`LICENSE.txt`](LICENSE.txt).
