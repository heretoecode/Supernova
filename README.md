# Supernova

Supernova is a custom Android TV / Nvidia Shield video-player build derived from NOVA Video Player.

This repository is the **canonical source of truth for the Supernova application**. It contains the main player/UI source, Supernova-specific features and fixes, tests, CI workflows, and the pinned build manifest/patches used to assemble supporting components.

## Repository structure

The application is built with supporting NOVA-derived modules, including `MediaLib`, `FileCoreLibrary`, and native components. Supernova's CI pins those dependencies in `.github/build/nova-ci.xml` so builds are reproducible.

The customised MediaLib fork is maintained separately at:
https://github.com/heretoecode/aos-MediaLib

The upstream NOVA `aos-AVP` repository is used only as a bootstrap manifest source by the current CI process. The personal `heretoecode/aos-AVP` fork is not required by the current Supernova build.

## Current development line

Preview 4.1.5 is the current validated development baseline. Historical NOVA/Supernova handovers and QA records are retained in the repository for traceability.

## Upstream

Supernova is derived from NOVA Video Player:
https://github.com/nova-video-player

NOVA itself derives from the open-source Archos Video Player Community Edition.
